package melonystudios.mellowui.mixin.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.screen.updated.MellomedleyMainMenuScreen;
import melonystudios.mellowui.util.GUITextures;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FocusableGui;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(Screen.class)
public abstract class MUIScreenMixin extends FocusableGui {
    @Shadow
    @Nullable
    protected Minecraft minecraft;
    @Shadow
    public int height;
    @Shadow
    public int width;
    @Shadow
    public abstract void renderDirtBackground(int vOffset);

    // #C0101010 to #D0101010 (Alpha: 192 to 208)
    @Inject(method = "renderBackground(Lcom/mojang/blaze3d/matrix/MatrixStack;I)V", at = @At("HEAD"), cancellable = true)
    public void renderBackground(MatrixStack stack, int vOffset, CallbackInfo callback) {
        if (MellowConfigs.CLIENT_CONFIGS.updateScreenBackground.get()) {
            callback.cancel();
            if (this.minecraft != null && this.minecraft.level != null) {
                RenderSystem.enableBlend();
                this.minecraft.getTextureManager().bind(GUITextures.INWORLD_BACKGROUND);
                blit(stack, 0, 0, 0, 0, this.minecraft.getWindow().getGuiScaledWidth(), this.minecraft.getWindow().getGuiScaledHeight(), this.minecraft.getWindow().getScreenWidth(), this.minecraft.getWindow().getScreenHeight());
                RenderSystem.disableBlend();
                MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.BackgroundDrawnEvent(this.minecraft.screen, stack));
            } else {
                this.renderDirtBackground(vOffset);
            }
        }
    }

    @Inject(method = "renderDirtBackground", at = @At("HEAD"), cancellable = true)
    public void renderDirtBackground(int vOffset, CallbackInfo callback) {
        if (MellowConfigs.CLIENT_CONFIGS.updateScreenBackground.get()) {
            callback.cancel();
            if (Minecraft.getInstance().level == null)
                MellowUtils.PANORAMA.render(this.minecraft.getDeltaFrameTime(), 1);
            if (!(Minecraft.getInstance().screen instanceof MainMenuScreen) && !(Minecraft.getInstance().screen instanceof MellomedleyMainMenuScreen) && this.minecraft != null) {
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder buffer = tessellator.getBuilder();
                this.minecraft.getTextureManager().bind(this.minecraft.level != null ? GUITextures.INWORLD_MENU_BACKGROUND : GUITextures.MENU_BACKGROUND);
                RenderSystem.enableBlend();
                GL11.glColor4f(1, 1, 1, 1);
                buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                buffer.vertex(0, this.height, 0).uv(0, (float) this.height / 32 + (float) vOffset).color(255, 255, 255, 255).endVertex();
                buffer.vertex(this.width, this.height, 0).uv((float) this.width / 32, (float) this.height / 32 + (float) vOffset).color(255, 255, 255, 255).endVertex();
                buffer.vertex(this.width, 0, 0).uv((float) this.width / 32, (float) vOffset).color(255, 255, 255, 255).endVertex();
                buffer.vertex(0, 0, 0).uv(0, (float) vOffset).color(255, 255, 255, 255).endVertex();
                tessellator.end();
                RenderSystem.disableBlend();
                MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.BackgroundDrawnEvent(Minecraft.getInstance().screen, new MatrixStack()));
            }
        }
    }
}
