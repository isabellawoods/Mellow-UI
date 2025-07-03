package melonystudios.mellowui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import melonystudios.mellowui.methods.InterfaceMethods;
import melonystudios.mellowui.util.GUITextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.IngameMenuScreen;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.gui.toasts.ToastGui;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class MusicToast implements IToast {
    private ResourceLocation musicLocation;
    private boolean fromPauseMenu;
    private long animationTicks = 1;
    private long timeSinceLastChanged;
    private boolean hasChanged;

    public MusicToast(ResourceLocation musicLocation, boolean fromPauseMenu) {
        this.musicLocation = musicLocation;
        this.fromPauseMenu = fromPauseMenu;
    }

    @Override
    @Nonnull
    public Visibility render(MatrixStack stack, ToastGui toast, long timeSinceLastChanged) {
        if (this.hasChanged) {
            this.timeSinceLastChanged = timeSinceLastChanged;
            this.hasChanged = false;
        }
        this.animationTicks += 1;
        int width = this.width();
        int height = this.height();

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.getOverlay() != null) return Visibility.HIDE;
        minecraft.getTextureManager().bind(GUITextures.NOW_PLAYING_TOAST);
        RenderSystem.color4f(1, 1, 1, 1);

        // Background
        AbstractGui.blit(stack, 0, 0, 0, 0, width / 2, 32, width, 32);
        AbstractGui.blit(stack, width / 2, 0, width - width / 2F, 0, width / 2, 32, width, 32);

        // Music Notes
        float hue = this.animationTicks < 0 ? -this.animationTicks : this.animationTicks;
        float[] noteColor = hsvToRgb(hue / 50F, 0.7F, 0.6F);
        RenderSystem.color4f(noteColor[0], noteColor[1], noteColor[2], 1);
        int yOffset = (int) Util.getMillis() / 100 * 16;
        minecraft.getTextureManager().bind(GUITextures.MUSIC_NOTES); // swap for "bind animated texture" in GUITextures
        AbstractGui.blit(stack, 8, 8, 0, yOffset, 16, 16, 16, 128);
        RenderSystem.color4f(1, 1, 1, 1);

        // Text
        AbstractGui.drawString(stack, minecraft.font, this.getMusicName(), 32, (height - 8) / 2, 0xD3D3D3);

        if (this.fromPauseMenu && ((InterfaceMethods.MusicManagerMethods) minecraft.getMusicManager()).mui$getNowPlaying() != null) {
            return minecraft.screen instanceof IngameMenuScreen ? Visibility.SHOW : Visibility.HIDE;
        }
        return timeSinceLastChanged - this.timeSinceLastChanged < 5000L ? IToast.Visibility.SHOW : IToast.Visibility.HIDE;
    }

    public static void add(ResourceLocation musicLocation, boolean fromPauseMenu, ToastGui toast) {
        toast.addToast(new MusicToast(musicLocation, fromPauseMenu));
    }

    public static void addOrUpdate(ResourceLocation musicLocation, boolean fromPauseMenu, ToastGui toast) {
        MusicToast musicToast = toast.getToast(MusicToast.class, NO_TOKEN);
        if (musicToast == null) add(musicLocation, fromPauseMenu, toast);
        else musicToast.reset(musicLocation, fromPauseMenu);
    }

    public void reset(ResourceLocation musicLocation, boolean fromPauseMenu) {
        this.musicLocation = musicLocation;
        this.fromPauseMenu = this.fromPauseMenu && fromPauseMenu;
        this.hasChanged = true;
    }

    private ITextComponent getMusicName() {
        return new TranslationTextComponent("music." + this.musicLocation.toString()
                .replace(":", ".")
                .replace("/", ".")
                .replace("sounds.", "")
                .replace("music.", "")
                .replace(".ogg", ""));
    }

    public static float[] hsvToRgb(float hue, float saturation, float value) {
        int i = (int) (hue * 6) % 6;
        float f = hue * 6 - (float) i;
        float f1 = value * (1 - saturation);
        float f2 = value * (1 - f * saturation);
        float f3 = value * (1 - (1 - f) * saturation);
        float red1;
        float green1;
        float blue1;
        switch (i) {
            case 0:
                red1 = value;
                green1 = f3;
                blue1 = f1;
                break;
            case 1:
                red1 = f2;
                green1 = value;
                blue1 = f1;
                break;
            case 2:
                red1 = f1;
                green1 = value;
                blue1 = f3;
                break;
            case 3:
                red1 = f1;
                green1 = f2;
                blue1 = value;
                break;
            case 4:
                red1 = f3;
                green1 = f1;
                blue1 = value;
                break;
            case 5:
                red1 = value;
                green1 = f1;
                blue1 = f2;
                break;
            default: throw new IllegalArgumentException(new TranslationTextComponent("error.mellowui.hsv_conversion", hue, saturation, value, i).getString());
        }

        return new float[] {MathHelper.clamp(red1, 0, 1), MathHelper.clamp(green1, 0, 1), MathHelper.clamp(blue1, 0, 1)};
    }

    @Override
    public int width() {
        return 38 + Minecraft.getInstance().font.width(this.getMusicName());
    }
}
