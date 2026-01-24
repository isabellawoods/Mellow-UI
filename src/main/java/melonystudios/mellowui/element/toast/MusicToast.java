package melonystudios.mellowui.element.toast;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.backport.ColorLerper;
import melonystudios.mellowui.config.WidgetConfigs;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.methods.InterfaceMethods;
import melonystudios.mellowui.util.GUITextures;
import melonystudios.mellowui.util.debug.MUIDebuggingFlags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.GuiUtils;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class MusicToast implements Toast {
    private ResourceLocation musicLocation;
    private boolean fromPauseMenu;
    private long timeSinceLastChanged;
    private boolean hasChanged;
    private static int musicNoteColorTick;
    private static long lastMusicNoteColorChange;
    private static int musicNoteColor = -1;

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
        Minecraft minecraft = Minecraft.getInstance();
        int width = this.width();
        int height = this.height();
        this.tickMusicNoteColor();

        // Background
        GuiUtils.drawContinuousTexturedBox(stack, GUITextures.NOW_PLAYING_TOAST, 0, 0, 0, 0, width, height, 160, 32, 4, 0);

        // Music Notes
        float red = FastColor.ARGB32.red(musicNoteColor) / 255F;
        float green = FastColor.ARGB32.green(musicNoteColor) / 255F;
        float blue = FastColor.ARGB32.blue(musicNoteColor) / 255F;

        RenderSystem.setShaderColor(red, green, blue, 1);
        RenderSystem.setShaderTexture(0, GUITextures.GUI_SPRITES_ATLAS);
        GuiComponent.blit(stack, 8, 8, 0, 16, 16, GUITextures.getSprite(GUITextures.MUSIC_NOTES));
        RenderSystem.setShaderColor(1, 1, 1, 1);

        // Text
        GuiComponent.drawString(stack, minecraft.font, this.getMusicName(), 32, (height - 8) / 2, 0xFFFFFF);

        // if toast debugging is enabled, render it.
        boolean isPlaying = ((InterfaceMethods.MusicManagerMethods) minecraft.getMusicManager()).mui$getNowPlaying() != null;
        if (MUIDebuggingFlags.DEBUG_CONSTANT_MUSIC_TOAST && isPlaying) return Visibility.SHOW;

        // If it's from, and in, the pause screen (and the song is playing), render it
        if (this.fromPauseMenu && isPlaying) return minecraft.screen instanceof PauseScreen ? Visibility.SHOW : Visibility.HIDE;
        return timeSinceLastChanged - this.timeSinceLastChanged < 5000L ? Visibility.SHOW : Visibility.HIDE;
    }

    private void tickMusicNoteColor() {
        long millis = System.currentTimeMillis();
        if (millis > lastMusicNoteColorChange + 25L) {
            musicNoteColorTick++;
            lastMusicNoteColorChange = millis;
            musicNoteColor = ColorLerper.getLerpedColor(ColorLerper.Type.MUSIC_NOTE, musicNoteColorTick);
        }
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
                .replace(".ogg", ""))
                .withStyle(TextComponents.withColor(WidgetConfigs.WIDGET_CONFIGS.musicToastTextColor.get()));
    }

    @Override
    public int width() {
        return 37 + Minecraft.getInstance().font.width(this.getMusicName());
    }
}
