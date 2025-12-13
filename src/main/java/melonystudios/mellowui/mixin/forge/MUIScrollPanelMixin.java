package melonystudios.mellowui.mixin.forge;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import melonystudios.mellowui.backport.cursor.CursorTypes;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.util.GUITextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.gui.ScrollPanel;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static melonystudios.mellowui.util.MellowUtils.defaultBackground;

@Mixin(value = ScrollPanel.class, remap = false)
public abstract class MUIScrollPanelMixin {
    @Shadow protected abstract void drawBackground();
    @Shadow @Final private Minecraft client;
    @Shadow @Final protected int right;
    @Shadow @Final protected int left;
    @Shadow @Final protected int bottom;
    @Shadow @Final protected int top;
    @Shadow @Final protected int width;
    @Shadow @Final protected int height;
    @Shadow @Final protected int border;
    @Shadow @Final private int barLeft;
    @Shadow @Final private int barWidth;
    @Shadow private boolean scrolling;
    @Shadow protected float scrollDistance;
    @Shadow protected abstract int getMaxScroll();
    @Shadow protected abstract int getContentHeight();
    @Shadow protected abstract void drawPanel(MatrixStack stack, int entryRight, int relativeY, Tessellator tessellator, int mouseX, int mouseY);

    /// Renders the graphical user interface (GUI) element.
    /// @param stack The {@link MatrixStack} used for rendering.
    /// @param mouseX The x-coordinate of the mouse cursor.
    /// @param mouseY The y-coordinate of the mouse cursor.
    /// @param partialTicks The partial tick time.
    @Inject(method = "render", at = @At("HEAD"), cancellable = true, remap = true)
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        if (!MellowConfigs.CLIENT_CONFIGS.panelBackgroundStyle.get()) return;
        callback.cancel();
        RenderComponents components = RenderComponents.INSTANCE;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        int relativeY = this.top + this.border - (int) this.scrollDistance;

        // Panel's unique background (I guess)
        this.drawBackground();
        components.enableScissor(this.left, this.top, this.left + this.width, this.top + this.height);

        // Panel contents
        ResourceLocation backgroundTexture = this.client.level == null ? (defaultBackground() ? AbstractGui.BACKGROUND_LOCATION :
                GUITextures.MENU_PANEL_BACKGROUND) : GUITextures.INWORLD_MENU_PANEL_BACKGROUND;
        components.renderListBackground(backgroundTexture, this.left, this.top, this.width, this.height, 0, 0, defaultBackground() ?
                RenderComponents.OLD_PANEL_BACKGROUND_BRIGHTNESS : RenderComponents.DEFAULT_BACKGROUND_BRIGHTNESS, this.scrollDistance);
        this.drawPanel(stack, this.right, relativeY, tessellator, mouseX, mouseY);

        // Scroller
        // max scroll doesn't accurately represent the content height but that's Forge's problem ~isa 5-12-25
        int maxScroll = this.getMaxScroll();
        if (maxScroll > 0) {
            RenderSystem.enableBlend();
            RenderSystem.disableTexture();
            int scrollX0 = this.barLeft;
            int scrollX1 = scrollX0 + this.barWidth;
            int scrollY0 = this.top;
            int i1 = (int) ((float) ((this.bottom - scrollY0) * (this.bottom - scrollY0)) / (float) this.getContentHeight());
            i1 = MathHelper.clamp(i1, 32, this.bottom - scrollY0 - 8);
            int i2 = (int) this.scrollDistance * (this.bottom - scrollY0 - i1) / maxScroll + scrollY0;
            if (i2 < scrollY0) i2 = scrollY0;

            buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            buffer.vertex(scrollX0, this.bottom, 0).uv(0, 1).color(0, 0, 0, 255).endVertex();
            buffer.vertex(scrollX1, this.bottom, 0).uv(1, 1).color(0, 0, 0, 255).endVertex();
            buffer.vertex(scrollX1, scrollY0, 0).uv(1, 0).color(0, 0, 0, 255).endVertex();
            buffer.vertex(scrollX0, scrollY0, 0).uv(0, 0).color(0, 0, 0, 255).endVertex();
            buffer.vertex(scrollX0, (i2 + i1), 0).uv(0, 1).color(128, 128, 128, 255).endVertex();
            buffer.vertex(scrollX1, (i2 + i1), 0).uv(1, 1).color(128, 128, 128, 255).endVertex();
            buffer.vertex(scrollX1, i2, 0).uv(1, 0).color(128, 128, 128, 255).endVertex();
            buffer.vertex(scrollX0, i2, 0).uv(0, 0).color(128, 128, 128, 255).endVertex();
            buffer.vertex(scrollX0, (i2 + i1 - 1), 0).uv(0, 1).color(192, 192, 192, 255).endVertex();
            buffer.vertex((scrollX1 - 1), (i2 + i1 - 1), 0).uv(1, 1).color(192, 192, 192, 255).endVertex();
            buffer.vertex((scrollX1 - 1), i2, 0).uv(1, 0).color(192, 192, 192, 255).endVertex();
            buffer.vertex(scrollX0, i2, 0).uv(0, 0).color(192, 192, 192, 255).endVertex();
            tessellator.end();
            RenderSystem.disableBlend();
        }

        RenderSystem.enableTexture();
        RenderSystem.shadeModel(7424);
        RenderSystem.enableAlphaTest();
        components.disableScissor();

        // Cursor
        if (maxScroll > 0 && this.isWithinScrollerArea(mouseX, mouseY)) components.requestCursor(this.scrolling ? CursorTypes.RESIZE_NS : CursorTypes.POINTING_HAND);
    }

    @Unique
    private boolean isWithinScrollerArea(int mouseX, int mouseY) {
        return mouseX >= this.barLeft && mouseY >= this.top && mouseX < this.barLeft + this.barWidth && mouseY < this.bottom;
    }
}
