package melonystudios.mellowui.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.SubtitleOverlay;
import net.minecraft.client.sounds.SoundEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
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
@Mixin(SubtitleOverlay.class)
public abstract class MUISubtitleOverlayMixin extends GuiComponent implements SoundEventListener {
    @Unique
    private static final MutableComponent COMING_FROM_LEFT = new TranslatableComponent("subtitles.coming_from_left");
    @Unique
    private static final MutableComponent COMING_FROM_RIGHT = new TranslatableComponent("subtitles.coming_from_right");
    @Unique
    private static final MutableComponent SPACE = new TranslatableComponent("subtitles.space");
    @Shadow
    @Final
    private List<SubtitleOverlay.Subtitle> subtitles;
    @Shadow
    @Final
    private Minecraft minecraft;
    @Shadow
    private boolean isListening;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(PoseStack stack, CallbackInfo callback) {
        callback.cancel();
        if (!this.isListening && this.minecraft.options.showSubtitles) {
            this.minecraft.getSoundManager().addListener(this);
            this.isListening = true;
        } else if (this.isListening && !this.minecraft.options.showSubtitles) {
            this.minecraft.getSoundManager().removeListener(this);
            this.isListening = false;
        }

        if (this.isListening && !this.subtitles.isEmpty()) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            Vec3 vec3 = new Vec3(this.minecraft.player.getX(), this.minecraft.player.getEyeY(), this.minecraft.player.getZ());
            Vec3 vec31 = (new Vec3(0, 0, -1)).xRot(-this.minecraft.player.getXRot() * ((float) Math.PI / 180F)).yRot(-this.minecraft.player.getYRot() * ((float) Math.PI / 180F));
            Vec3 vec32 = (new Vec3(0, 1, 0)).xRot(-this.minecraft.player.getXRot() * ((float) Math.PI / 180F)).yRot(-this.minecraft.player.getYRot() * ((float) Math.PI / 180F));
            Vec3 vec33 = vec31.cross(vec32);
            int i = 0;
            int j = 0;
            Iterator<SubtitleOverlay.Subtitle> subtitleIterator = this.subtitles.iterator();

            while (subtitleIterator.hasNext()) {
                SubtitleOverlay.Subtitle subtitle = subtitleIterator.next();
                if (subtitle.getTime() + 3000L <= Util.getMillis()) {
                    subtitleIterator.remove();
                } else {
                    j = Math.max(j, this.minecraft.font.width(subtitle.getText()));
                }
            }

            j = j + this.minecraft.font.width(COMING_FROM_LEFT) + this.minecraft.font.width(SPACE) + this.minecraft.font.width(COMING_FROM_RIGHT) + this.minecraft.font.width(SPACE);

            for (SubtitleOverlay.Subtitle subtitle : this.subtitles) {
                Component subtitleText = subtitle.getText();
                Vec3 vec3D4 = subtitle.getLocation().subtract(vec3).normalize();
                double d1 = -vec33.dot(vec3D4);
                double d2 = -vec31.dot(vec3D4);
                boolean flag = d2 > 0.5D;
                int l = j / 2;
                int i1 = 9;
                int j1 = i1 / 2;
                int subtitleWidth = this.minecraft.font.width(subtitleText);
                int l1 = Mth.floor(Mth.clampedLerp(255, 75, (float) (Util.getMillis() - subtitle.getTime()) / 3000));
                int i2 = l1 << 16 | l1 << 8 | l1;
                stack.pushPose();
                stack.translate((float) this.minecraft.getWindow().getGuiScaledWidth() - (float) l * 1 - 2, (float) (this.minecraft.getWindow().getGuiScaledHeight() - 30) - (float)(i * (i1 + 1)) * 1, 0);
                stack.scale(1, 1, 1);
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
                stack.popPose();
                ++i;
            }

            RenderSystem.disableBlend();
        }
    }
}
