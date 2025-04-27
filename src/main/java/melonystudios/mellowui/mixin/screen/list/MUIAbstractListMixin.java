package melonystudios.mellowui.mixin.screen.list;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.util.GUITextures;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FocusableGui;
import net.minecraft.client.gui.widget.list.AbstractList;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("deprecation")
@OnlyIn(Dist.CLIENT)
@Mixin(AbstractList.class)
public abstract class MUIAbstractListMixin extends FocusableGui {
    @Shadow @Final protected Minecraft minecraft;
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
    @Shadow protected abstract void renderBackground(MatrixStack stack);
    @Shadow protected abstract void renderHeader(MatrixStack stack, int x, int y, Tessellator tessellator);
    @Shadow protected abstract void renderList(MatrixStack stack, int x, int y, int mouseX, int mouseY, float partialTicks);
    @Shadow protected abstract void renderDecorations(MatrixStack stack, int mouseX, int mouseY);

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        if (MellowConfigs.CLIENT_CONFIGS.updateListBackground.get()) {
            callback.cancel();
            this.renderBackground(stack);
            int scrollbarPosition = this.getScrollbarPosition();
            int i = scrollbarPosition + 6;
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuilder();
            if (this.renderBackground) {
                this.minecraft.getTextureManager().bind(this.minecraft.level != null ? GUITextures.INWORLD_MENU_LIST_BACKGROUND : GUITextures.MENU_LIST_BACKGROUND);
                RenderSystem.enableBlend();
                buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                buffer.vertex(this.x0, this.y0, -100).uv(0, (float) this.y0 / 32).color(255, 255, 255, 255).endVertex();
                buffer.vertex((this.x0 + this.width), this.y0, -100).uv((float) this.width / 32, (float) this.y0 / 32).color(255, 255, 255, 255).endVertex();
                buffer.vertex((this.x0 + this.width), 0, -100).uv((float) this.width / 32, 0).color(255, 255, 255, 255).endVertex();
                buffer.vertex(this.x0, 0, -100).uv(0, 0).color(255, 255, 255, 255).endVertex();
                buffer.vertex(this.x0, this.height, -100).uv(0, (float) this.height / 32).color(255, 255, 255, 255).endVertex();
                buffer.vertex((this.x0 + this.width), this.height, -100).uv((float) this.width / 32, (float) this.height / 32).color(255, 255, 255, 255).endVertex();
                buffer.vertex((this.x0 + this.width), this.y1, -100).uv((float) this.width / 32, (float) this.y1 / 32).color(255, 255, 255, 255).endVertex();
                buffer.vertex(this.x0, this.y1, -100).uv(0, (float) this.y1 / 32).color(255, 255, 255, 255).endVertex();
                tessellator.end();
                GL11.glColor4f(1, 1, 1, 1);
                buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                buffer.vertex(this.x0, this.y1, 0).uv((float) this.x0 / 32, (float) (this.y1 + (int) this.getScrollAmount()) / 32).color(255, 255, 255, 255).endVertex();
                buffer.vertex(this.x1, this.y1, 0).uv((float) this.x1 / 32, (float) (this.y1 + (int) this.getScrollAmount()) / 32).color(255, 255, 255, 255).endVertex();
                buffer.vertex(this.x1, this.y0, 0).uv((float) this.x1 / 32, (float) (this.y0 + (int) this.getScrollAmount()) / 32).color(255, 255, 255, 255).endVertex();
                buffer.vertex(this.x0, this.y0, 0).uv((float) this.x0 / 32, (float) (this.y0 + (int) this.getScrollAmount()) / 32).color(255, 255, 255, 255).endVertex();
                tessellator.end();
                RenderSystem.disableBlend();
            }

            int leftRow = this.getRowLeft();
            int y = this.y0 + 4 - (int) this.getScrollAmount();
            MellowUtils.scissor(() -> {
                if (this.renderHeader) this.renderHeader(stack, leftRow, y, tessellator);
                this.renderList(stack, leftRow, y, mouseX, mouseY, partialTicks);
            }, this.x0, this.x1, this.y0, this.y1, this.height);

            if (this.renderTopAndBottom) {
                this.minecraft.getTextureManager().bind(GUITextures.TAB_HEADER_BACKGROUND);
                RenderSystem.enableDepthTest();
                RenderSystem.depthFunc(519);
                RenderSystem.enableBlend();
                buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                buffer.vertex(this.x0, this.y0, -100).uv(0, (float) this.y0 / 32).color(255, 255, 255, 255).endVertex();
                buffer.vertex((this.x0 + this.width), this.y0, -100).uv((float) this.width / 32, (float) this.y0 / 32).color(255, 255, 255, 255).endVertex();
                buffer.vertex((this.x0 + this.width), 0, -100).uv((float) this.width / 32, 0).color(255, 255, 255, 255).endVertex();
                buffer.vertex(this.x0, 0, -100).uv(0, 0).color(255, 255, 255, 255).endVertex();
                buffer.vertex(this.x0, this.height, -100).uv(0, (float) this.height / 32).color(255, 255, 255, 255).endVertex();
                buffer.vertex((this.x0 + this.width), this.height, -100).uv((float) this.width / 32, (float) this.height / 32).color(255, 255, 255, 255).endVertex();
                buffer.vertex((this.x0 + this.width), this.y1, -100).uv((float) this.width / 32, (float) this.y1 / 32).color(255, 255, 255, 255).endVertex();
                buffer.vertex(this.x0, this.y1, -100).uv(0, (float) this.y1 / 32).color(255, 255, 255, 255).endVertex();
                tessellator.end();
                this.minecraft.getTextureManager().bind(this.minecraft.level != null ? GUITextures.INWORLD_HEADER_SEPARATOR : GUITextures.HEADER_SEPARATOR);
                blit(stack, this.x0, this.y0, 0, 0, this.x0 + this.width, 2, 32, 2);
                this.minecraft.getTextureManager().bind(this.minecraft.level != null ? GUITextures.INWORLD_FOOTER_SEPARATOR : GUITextures.FOOTER_SEPARATOR);
                blit(stack, this.x0, this.y1, 0, 0, this.x0 + this.width, 2, 32, 2);
                RenderSystem.depthFunc(515);
                RenderSystem.disableBlend();
                RenderSystem.disableDepthTest();
            }

            int maxScroll = this.getMaxScroll();
            if (maxScroll > 0) {
                RenderSystem.disableTexture();
                int scrollY0 = this.y0 + 2;
                int i1 = (int) ((float) ((this.y1 - scrollY0) * (this.y1 - scrollY0)) / (float) this.getMaxPosition());
                i1 = MathHelper.clamp(i1, 32, this.y1 - scrollY0 - 8);
                int i2 = (int) this.getScrollAmount() * (this.y1 - scrollY0 - i1) / maxScroll + scrollY0;
                if (i2 < scrollY0) i2 = scrollY0;

                buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                buffer.vertex(scrollbarPosition, this.y1, 0).uv(0, 1).color(0, 0, 0, 255).endVertex();
                buffer.vertex(i, this.y1, 0).uv(1, 1).color(0, 0, 0, 255).endVertex();
                buffer.vertex(i, scrollY0, 0).uv(1, 0).color(0, 0, 0, 255).endVertex();
                buffer.vertex(scrollbarPosition, scrollY0, 0).uv(0, 0).color(0, 0, 0, 255).endVertex();
                buffer.vertex(scrollbarPosition, (i2 + i1), 0).uv(0, 1).color(128, 128, 128, 255).endVertex();
                buffer.vertex(i, (i2 + i1), 0).uv(1, 1).color(128, 128, 128, 255).endVertex();
                buffer.vertex(i, i2, 0).uv(1, 0).color(128, 128, 128, 255).endVertex();
                buffer.vertex(scrollbarPosition, i2, 0).uv(0, 0).color(128, 128, 128, 255).endVertex();
                buffer.vertex(scrollbarPosition, (i2 + i1 - 1), 0).uv(0, 1).color(192, 192, 192, 255).endVertex();
                buffer.vertex((i - 1), (i2 + i1 - 1), 0).uv(1, 1).color(192, 192, 192, 255).endVertex();
                buffer.vertex((i - 1), i2, 0).uv(1, 0).color(192, 192, 192, 255).endVertex();
                buffer.vertex(scrollbarPosition, i2, 0).uv(0, 0).color(192, 192, 192, 255).endVertex();
                tessellator.end();
            }

            this.renderDecorations(stack, mouseX, mouseY);
            RenderSystem.enableTexture();
            RenderSystem.shadeModel(7424);
            RenderSystem.enableAlphaTest();
        }
    }
}
