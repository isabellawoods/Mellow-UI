package melonystudios.mellowui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.methods.InterfaceMethods;
import melonystudios.mellowui.util.GUITextures;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class MusicToast implements Toast {
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
    public Visibility render(PoseStack stack, ToastComponent toast, long timeSinceLastChanged) {
        if (this.hasChanged) {
            this.timeSinceLastChanged = timeSinceLastChanged;
            this.hasChanged = false;
        }
        this.animationTicks += 1;
        int width = this.width();
        int height = this.height();

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.getOverlay() != null) return Visibility.HIDE;
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, GUITextures.NOW_PLAYING_TOAST);

        // Background
        GuiComponent.blit(stack, 0, 0, 0, 0, width / 2, 32, width, 32);
        GuiComponent.blit(stack, width / 2, 0, width - width / 2F, 0, width / 2, 32, width, 32);

        // Music Notes
        float hue = this.animationTicks < 0 ? -this.animationTicks : this.animationTicks;
        float[] noteColor = hsvToRgb(hue / 50F, 0.7F, 0.6F);
        RenderSystem.setShaderColor(noteColor[0], noteColor[1], noteColor[2], 1);
        int yOffset = (int) Util.getMillis() / 100 * 16;
        RenderSystem.setShaderTexture(0, GUITextures.MUSIC_NOTES); // swap for "bind animated texture" in GUITextures
        GuiComponent.blit(stack, 8, 8, 0, yOffset, 16, 16, 16, 128);
        RenderSystem.setShaderColor(1, 1, 1, 1);

        // Text
        GuiComponent.drawString(stack, minecraft.font, this.getMusicName(), 32, (height - 8) / 2, 0xD3D3D3);

        if (this.fromPauseMenu && ((InterfaceMethods.MusicManagerMethods) minecraft.getMusicManager()).mui$getNowPlaying() != null) {
            return minecraft.screen instanceof PauseScreen ? Visibility.SHOW : Visibility.HIDE;
        }
        return timeSinceLastChanged - this.timeSinceLastChanged < 5000L ? Toast.Visibility.SHOW : Toast.Visibility.HIDE;
    }

    public static void add(ResourceLocation musicLocation, boolean fromPauseMenu, ToastComponent toast) {
        toast.addToast(new MusicToast(musicLocation, fromPauseMenu));
    }

    public static void addOrUpdate(ResourceLocation musicLocation, boolean fromPauseMenu, ToastComponent toast) {
        MusicToast musicToast = toast.getToast(MusicToast.class, NO_TOKEN);
        if (musicToast == null) add(musicLocation, fromPauseMenu, toast);
        else musicToast.reset(musicLocation, fromPauseMenu);
    }

    public void reset(ResourceLocation musicLocation, boolean fromPauseMenu) {
        this.musicLocation = musicLocation;
        this.fromPauseMenu = this.fromPauseMenu && fromPauseMenu;
        this.hasChanged = true;
    }

    private Component getMusicName() {
        return new TranslatableComponent("music." + this.musicLocation.toString()
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
            default: throw new IllegalArgumentException(new TranslatableComponent("error.mellowui.hsv_conversion", hue, saturation, value, i).getString());
        }

        return new float[] {Mth.clamp(red1, 0, 1), Mth.clamp(green1, 0, 1), Mth.clamp(blue1, 0, 1)};
    }

    @Override
    public int width() {
        return 38 + Minecraft.getInstance().font.width(this.getMusicName());
    }
}
