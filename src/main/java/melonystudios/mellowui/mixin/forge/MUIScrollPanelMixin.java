package melonystudios.mellowui.mixin.forge;

import com.mojang.blaze3d.vertex.*;
import melonystudios.mellowui.backport.cursor.CursorTypes;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.util.GUITextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;
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
    @Shadow @Final private Minecraft client;
    @Shadow @Final protected int right;
    @Shadow @Final protected int left;
    @Shadow @Final protected int bottom;
    @Shadow @Final protected int top;
    @Shadow @Final protected int width;
    @Shadow @Final protected int height;
    @Shadow @Final private int barLeft;
    @Shadow @Final private int barWidth;
    @Shadow private boolean scrolling;
    @Shadow protected float scrollDistance;
    @Shadow protected abstract int getMaxScroll();

    /// Renders the graphical user interface (GUI) element.
    /// @param stack The {@link PoseStack} used for rendering.
    /// @param mouseX The x-coordinate of the mouse cursor.
    /// @param mouseY The y-coordinate of the mouse cursor.
    /// @param partialTicks The partial tick time.
    @Inject(method = "render", at = @At("TAIL"), remap = true)
    public void changeCursorShape(PoseStack stack, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        // Cursor
        if (this.getMaxScroll() > 0 && this.isWithinScrollerArea(mouseX, mouseY)) RenderComponents.INSTANCE.requestCursor(this.scrolling ? CursorTypes.RESIZE_NS : CursorTypes.POINTING_HAND);
    }

    /// Draws the background of the scroll panel. This runs **after** the scissors are enabled.
    /// @param stack The {@link PoseStack} used for rendering.
    /// @param partialTicks The partial tick time.
    @Inject(method = "drawBackground", at = @At("HEAD"), cancellable = true)
    public void renderUpdatedBackground(PoseStack stack, Tesselator tessellator, float partialTicks, CallbackInfo callback) {
        if (!MellowConfigs.CLIENT_CONFIGS.panelBackgroundStyle.get()) return;
        callback.cancel();

        // Panel background
        ResourceLocation backgroundTexture = this.client.level == null ? (defaultBackground() ? GuiComponent.BACKGROUND_LOCATION :
                GUITextures.MENU_PANEL_BACKGROUND) : GUITextures.INWORLD_MENU_PANEL_BACKGROUND;
        RenderComponents.INSTANCE.renderListBackground(backgroundTexture, this.left, this.top, this.width, this.height, 0, 0, defaultBackground() ?
                RenderComponents.OLD_PANEL_BACKGROUND_BRIGHTNESS : RenderComponents.DEFAULT_BACKGROUND_BRIGHTNESS, this.scrollDistance);
    }

    @Unique
    private boolean isWithinScrollerArea(int mouseX, int mouseY) {
        return mouseX >= this.barLeft && mouseY >= this.top && mouseX < this.barLeft + this.barWidth && mouseY < this.bottom;
    }
}
