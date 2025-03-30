package melonystudios.mellowui.mixin.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISoundEventListener;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.overlay.SubtitleOverlayGui;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.List;

@OnlyIn(Dist.CLIENT)
@Mixin(SubtitleOverlayGui.class)
public abstract class MUISubtitleOverlayMixin extends AbstractGui implements ISoundEventListener {
    @Unique
    private static final IFormattableTextComponent COMING_FROM_LEFT = new TranslationTextComponent("subtitles.coming_from_left");
    @Unique
    private static final IFormattableTextComponent COMING_FROM_RIGHT = new TranslationTextComponent("subtitles.coming_from_right");
    @Unique
    private static final IFormattableTextComponent SPACE = new TranslationTextComponent("subtitles.space");
    @Shadow
    @Final
    private List<SubtitleOverlayGui.Subtitle> subtitles;
    @Shadow
    @Final
    private Minecraft minecraft;
    @Shadow
    private boolean isListening;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(MatrixStack stack, CallbackInfo callback) {
        callback.cancel();
        if (!this.isListening && this.minecraft.options.showSubtitles) {
            this.minecraft.getSoundManager().addListener(this);
            this.isListening = true;
        } else if (this.isListening && !this.minecraft.options.showSubtitles) {
            this.minecraft.getSoundManager().removeListener(this);
            this.isListening = false;
        }

        if (this.isListening && !this.subtitles.isEmpty()) {
            RenderSystem.pushMatrix();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            Vector3d vec3D = new Vector3d(this.minecraft.player.getX(), this.minecraft.player.getEyeY(), this.minecraft.player.getZ());
            Vector3d vec3D1 = (new Vector3d(0, 0, -1)).xRot(-this.minecraft.player.xRot * ((float) Math.PI / 180F)).yRot(-this.minecraft.player.yRot * ((float) Math.PI / 180F));
            Vector3d vec3D2 = (new Vector3d(0, 1, 0)).xRot(-this.minecraft.player.xRot * ((float) Math.PI / 180F)).yRot(-this.minecraft.player.yRot * ((float) Math.PI / 180F));
            Vector3d vec3D3 = vec3D1.cross(vec3D2);
            int i = 0;
            int j = 0;
            Iterator<SubtitleOverlayGui.Subtitle> subtitleIterator = this.subtitles.iterator();

            while (subtitleIterator.hasNext()) {
                SubtitleOverlayGui.Subtitle subtitle = subtitleIterator.next();
                if (subtitle.getTime() + 3000L <= Util.getMillis()) {
                    subtitleIterator.remove();
                } else {
                    j = Math.max(j, this.minecraft.font.width(subtitle.getText()));
                }
            }

            j = j + this.minecraft.font.width(COMING_FROM_LEFT) + this.minecraft.font.width(SPACE) + this.minecraft.font.width(COMING_FROM_RIGHT) + this.minecraft.font.width(SPACE);

            for (SubtitleOverlayGui.Subtitle subtitle : this.subtitles) {
                ITextComponent subtitleText = subtitle.getText();
                Vector3d vec3D4 = subtitle.getLocation().subtract(vec3D).normalize();
                double d1 = -vec3D3.dot(vec3D4);
                double d2 = -vec3D1.dot(vec3D4);
                boolean flag = d2 > 0.5D;
                int l = j / 2;
                int i1 = 9;
                int j1 = i1 / 2;
                int subtitleWidth = this.minecraft.font.width(subtitleText);
                int l1 = MathHelper.floor(MathHelper.clampedLerp(255, 75, (float) (Util.getMillis() - subtitle.getTime()) / 3000));
                int i2 = l1 << 16 | l1 << 8 | l1;
                RenderSystem.pushMatrix();
                RenderSystem.translatef((float) this.minecraft.getWindow().getGuiScaledWidth() - (float) l * 1 - 2, (float) (this.minecraft.getWindow().getGuiScaledHeight() - 30) - (float)(i * (i1 + 1)) * 1, 0);
                RenderSystem.scalef(1, 1, 1);
                fill(stack, -l - 1, -j1 - 1, l + 1, j1 + 1, this.minecraft.options.getBackgroundColor(0.8F));
                RenderSystem.enableBlend();
                if (!flag) {
                    if (d1 > 0) {
                        this.minecraft.font.drawShadow(stack, COMING_FROM_RIGHT, (float) (l - this.minecraft.font.width(COMING_FROM_RIGHT)), (float) (-j1), i2 + 0xFF000000);
                    } else if (d1 < 0) {
                        this.minecraft.font.drawShadow(stack, COMING_FROM_LEFT, (float) (-l), (float) (-j1), i2 + 0xFF000000);
                    }
                }

                this.minecraft.font.drawShadow(stack, subtitleText, (float) (-subtitleWidth / 2), (float) (-j1), i2 + 0xFF000000);
                RenderSystem.popMatrix();
                ++i;
            }

            RenderSystem.disableBlend();
            RenderSystem.popMatrix();
        }
    }
}
