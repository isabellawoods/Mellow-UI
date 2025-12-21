package melonystudios.mellowui.element.toast;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import melonystudios.mellowui.backport.ColorLerper;
import melonystudios.mellowui.config.WidgetConfigs;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.methods.InterfaceMethods;
import melonystudios.mellowui.util.DebuggingFlags;
import melonystudios.mellowui.util.GUITextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.IngameMenuScreen;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.gui.toasts.ToastGui;
import net.minecraft.util.ColorHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.GuiUtils;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class MusicToast implements IToast {
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
    public Visibility render(MatrixStack stack, ToastGui toast, long timeSinceLastChanged) {
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
        float red = ColorHelper.PackedColor.red(musicNoteColor) / 255F;
        float green = ColorHelper.PackedColor.green(musicNoteColor) / 255F;
        float blue = ColorHelper.PackedColor.blue(musicNoteColor) / 255F;

        RenderSystem.color4f(red, green, blue, 1);
        minecraft.getTextureManager().bind(GUITextures.GUI_SPRITES_ATLAS);
        AbstractGui.blit(stack, 8, 8, 0, 16, 16, GUITextures.getSprite(GUITextures.MUSIC_NOTES));
        RenderSystem.color4f(1, 1, 1, 1);

        // Text
        AbstractGui.drawString(stack, minecraft.font, this.getMusicName(), 32, (height - 8) / 2, 0xFFFFFF);

        // if toast debugging is enabled, render it
        boolean isPlaying =  ((InterfaceMethods.MusicManagerMethods) minecraft.getMusicManager()).mui$getNowPlaying() != null;
        if (DebuggingFlags.DEBUG_CONSTANT_MUSIC_TOAST && isPlaying) return Visibility.SHOW;

        // if it's from, and in, the pause screen (and the song is playing), render it
        if (this.fromPauseMenu && isPlaying) return minecraft.screen instanceof IngameMenuScreen ? Visibility.SHOW : Visibility.HIDE;
        return timeSinceLastChanged - this.timeSinceLastChanged < 5000L ? IToast.Visibility.SHOW : IToast.Visibility.HIDE;
    }

    private void tickMusicNoteColor() {
        long millis = System.currentTimeMillis();
        if (millis > lastMusicNoteColorChange + 25L) {
            musicNoteColorTick++;
            lastMusicNoteColorChange = millis;
            musicNoteColor = ColorLerper.getLerpedColor(ColorLerper.Type.MUSIC_NOTE, musicNoteColorTick);
        }
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
                .replace(".ogg", ""))
                .withStyle(TextComponents.withColor(WidgetConfigs.WIDGET_CONFIGS.musicToastTextColor.get()));
    }

    @Override
    public int width() {
        return 37 + Minecraft.getInstance().font.width(this.getMusicName());
    }
}
