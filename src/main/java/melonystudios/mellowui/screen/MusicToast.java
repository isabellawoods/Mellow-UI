package melonystudios.mellowui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import melonystudios.mellowui.util.GUITextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
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
    private long timeSinceLastChanged;
    private boolean hasChanged;

    public MusicToast(ResourceLocation musicLocation) {
        this.musicLocation = musicLocation;
    }

    @Override
    @Nonnull
    public Visibility render(MatrixStack stack, ToastGui toast, long timeSinceLastChanged) {
        if (this.hasChanged) {
            this.timeSinceLastChanged = timeSinceLastChanged;
            this.hasChanged = false;
        }
        int width = this.width();
        int height = this.height();

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.getOverlay() != null) return Visibility.HIDE;
        minecraft.getTextureManager().bind(GUITextures.NOW_PLAYING_TOAST);
        RenderSystem.color4f(1, 1, 1, 1);

        // Background
        AbstractGui.blit(stack, 0, 0, 0, 0, width / 2, height, 160, 32);
        AbstractGui.blit(stack, width / 2, 0, width - width / 2, 0, width / 2, height, 160, 32);

        // Music Notes
        float noteColorSin = MathHelper.sin((float) (Util.getMillis() % 1000L) / 1000 * ((float) Math.PI)); // this is what you get for colors for now ~isa 16-5-25
        RenderSystem.color4f(noteColorSin * 0.8F, noteColorSin * 0.5F, noteColorSin * 0.4F, 1);
        int yOffset = (int) Util.getMillis() / 100 * 16;
        minecraft.getTextureManager().bind(GUITextures.MUSIC_NOTES); // swap for "bind animated texture" in GUITextures
        AbstractGui.blit(stack, 8, 8, 0, yOffset, 16, 16, 16, 128);
        RenderSystem.color4f(1, 1, 1, 1);

        // Text
        AbstractGui.drawString(stack, minecraft.font, this.getMusicName(), 32, (height - 8) / 2, 0xD3D3D3);

        return timeSinceLastChanged - this.timeSinceLastChanged < 5000L ? IToast.Visibility.SHOW : IToast.Visibility.HIDE;
    }

    public static void add(ResourceLocation musicLocation, ToastGui toast) {
        toast.addToast(new MusicToast(musicLocation));
    }

    public static void addOrUpdate(ResourceLocation musicLocation, ToastGui toast) {
        MusicToast musicToast = toast.getToast(MusicToast.class, NO_TOKEN);
        if (musicToast == null) add(musicLocation, toast);
        else musicToast.reset(musicLocation);
    }

    public void reset(ResourceLocation musicLocation) {
        this.musicLocation = musicLocation;
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

    @Override
    public int width() {
        return 38 + Minecraft.getInstance().font.width(this.getMusicName());
    }
}
