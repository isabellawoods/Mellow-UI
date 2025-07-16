package melonystudios.mellowui.mixin.screen.list;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.screen.RenderComponents;
import melonystudios.mellowui.sound.MUISounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
@Mixin(AbstractSelectionList.class)
public abstract class MUIAbstractSelectionListMixin<E extends AbstractSelectionList.Entry<E>> extends AbstractContainerEventHandler {
    @Shadow @Final protected Minecraft minecraft;
    @Shadow @Nullable private E hovered;
    @Shadow private boolean renderBackground;
    @Shadow private boolean renderTopAndBottom;
    @Shadow private boolean renderHeader;
    @Shadow protected int width;
    @Shadow protected int height;
    @Shadow protected int x0;
    @Shadow protected int x1;
    @Shadow protected int y0;
    @Shadow protected int y1;
    @Shadow protected abstract int getScrollbarPosition();
    @Shadow public abstract double getScrollAmount();
    @Shadow protected abstract int getMaxPosition();
    @Shadow public abstract int getMaxScroll();
    @Shadow public abstract int getRowLeft();
    @Shadow protected abstract void renderBackground(PoseStack stack);
    @Shadow protected abstract void renderHeader(PoseStack stack, int x, int y, Tesselator tessellator);
    @Shadow protected abstract void renderList(PoseStack stack, int x, int y, int mouseX, int mouseY, float partialTicks);
    @Shadow protected abstract void renderDecorations(PoseStack stack, int mouseX, int mouseY);
    @Shadow @Nullable protected abstract E getEntryAtPosition(double mouseX, double mouseY);

    @Inject(method = "setSelected", at = @At("HEAD"))
    public void setSelected(E entry, CallbackInfo callback) {
        if (entry != null) this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(MUISounds.LIST_ENTRY_SELECTED.get(), 1, 1));
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        if (MellowConfigs.CLIENT_CONFIGS.updateListBackground.get()) {
            callback.cancel();
            RenderComponents components = RenderComponents.INSTANCE;
            Tesselator tessellator = Tesselator.getInstance();
            BufferBuilder buffer = tessellator.getBuilder();
            int leftRow = this.getRowLeft();
            int y = this.y0 + 4 - (int) this.getScrollAmount();
            this.hovered = this.isMouseOver(mouseX, mouseY) && components.containsPointInScissor(mouseX, mouseY) ? this.getEntryAtPosition(mouseX, mouseY) : null;

            // List's unique background (I guess)
            this.renderBackground(stack);

            // List contents (background, header and entries)
            components.enableScissor(this.x0, this.y0 + 2, this.x1, this.y1);
            if (this.renderBackground) components.renderListBackground(this.x0, this.y0 + 2, this.width, this.height, this.x1, this.y1, this.getScrollAmount());
            if (this.renderHeader) this.renderHeader(stack, leftRow, y, tessellator);
            this.renderList(stack, leftRow, y, mouseX, mouseY, partialTicks);
            components.disableScissor();

            // List separators (header and footer)
            if (this.renderTopAndBottom) components.renderListSeparators(this.width, this.x0, this.y1, this.y0, 0, 0);

            // Scroller
            int maxScroll = this.getMaxScroll();
            if (maxScroll > 0) {
                RenderSystem.enableBlend();
                RenderSystem.disableTexture();
                RenderSystem.setShader(GameRenderer::getPositionColorShader);
                int scrollX0 = this.getScrollbarPosition();
                int scrollX1 = scrollX0 + 6;
                int scrollY0 = this.y0 + 2;
                int i1 = (int) ((float) ((this.y1 - scrollY0) * (this.y1 - scrollY0)) / (float) this.getMaxPosition());
                i1 = Mth.clamp(i1, 32, this.y1 - scrollY0 - 8);
                int i2 = (int) this.getScrollAmount() * (this.y1 - scrollY0 - i1) / maxScroll + scrollY0;
                if (i2 < scrollY0) i2 = scrollY0;

                buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
                buffer.vertex(scrollX0, this.y1, 0).uv(0, 1).color(0, 0, 0, 255).endVertex();
                buffer.vertex(scrollX1, this.y1, 0).uv(1, 1).color(0, 0, 0, 255).endVertex();
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

            this.renderDecorations(stack, mouseX, mouseY);
            RenderSystem.enableTexture();
        }
    }
}
