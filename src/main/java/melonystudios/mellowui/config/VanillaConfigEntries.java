package melonystudios.mellowui.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.settings.BooleanOption;
import net.minecraft.client.settings.SliderPercentageOption;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import static melonystudios.mellowui.config.MellowConfigs.CLIENT_CONFIGS;

public class VanillaConfigEntries {
    // Backported options
    public static final BooleanOption MONOCHROME_LOADING_SCREEN = new BooleanOption("config.minecraft.monochrome_loading_screen", new TranslationTextComponent("config.minecraft.monochrome_loading_screen.desc"),
            options -> CLIENT_CONFIGS.monochromeLoadingScreen.get(), (options, newValue) -> CLIENT_CONFIGS.monochromeLoadingScreen.set(newValue));
    public static final SliderPercentageOption PANORAMA_SCROLL_SPEED = new SliderPercentageOption("config.minecraft.panorama_scroll_speed", 0, 1, 0.01F,
            options -> CLIENT_CONFIGS.panoramaScrollSpeed.get(),
            (options, newValue) -> CLIENT_CONFIGS.panoramaScrollSpeed.set(newValue),
            (options, slider) -> new TranslationTextComponent("options.percent_value", new TranslationTextComponent("config.minecraft.panorama_scroll_speed"), (int) (slider.get(options) * 100)));
    public static final BooleanOption HIDE_SPLASH_TEXTS = new BooleanOption("config.minecraft.hide_splash_texts", new TranslationTextComponent("config.minecraft.hide_splash_texts.desc"),
            options -> CLIENT_CONFIGS.hideSplashTexts.get(), (options, newValue) -> CLIENT_CONFIGS.hideSplashTexts.set(newValue));
    public static final BooleanOption SHOW_MUSIC_TOAST = new BooleanOption("config.minecraft.show_music_toast", new TranslationTextComponent("config.minecraft.show_music_toast.desc"),
            options -> CLIENT_CONFIGS.showMusicToast.get(), (options, newValue) -> CLIENT_CONFIGS.showMusicToast.set(newValue));
    public static final BooleanOption REALMS_NEWS_AND_INVITES = new BooleanOption("config.minecraft.realms_notifications", new TranslationTextComponent("config.minecraft.realms_notifications.desc"),
            options -> options.realmsNotifications, (options, newValue) -> options.realmsNotifications = newValue);

    // Updated options
    public static final SliderPercentageOption FOV_EFFECTS = new SliderPercentageOption("options.fovEffectScale", 0, 1, 0,
            options -> Math.pow(options.fovEffectScale, 2),
            (options, newValue) -> options.fovEffectScale = MathHelper.sqrt(newValue),
            (options, slider) -> {
        slider.setTooltip(Minecraft.getInstance().font.split(new TranslationTextComponent("options.fovEffectScale.tooltip"), 200));
        double percentage = slider.toPct(slider.get(options));
        ITextComponent translation = new TranslationTextComponent("options.fovEffectScale");
        return percentage == 0 ? DialogTexts.optionStatus(translation, false) : new TranslationTextComponent("options.percent_value", translation, (int) (percentage * 100));
    });
}
