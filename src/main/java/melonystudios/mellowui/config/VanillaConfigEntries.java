package melonystudios.mellowui.config;

import melonystudios.mellowui.config.option.HighContrastOption;
import melonystudios.mellowui.config.option.SoundDeviceOption;
import melonystudios.mellowui.config.option.TooltippedIterableOption;
import melonystudios.mellowui.config.type.TwoStyles;
import melonystudios.mellowui.methods.InterfaceMethods;
import melonystudios.mellowui.screen.updated.MUIOptionsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.settings.BooleanOption;
import net.minecraft.client.settings.SliderPercentageOption;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import static melonystudios.mellowui.config.MellowConfigs.CLIENT_CONFIGS;
import static melonystudios.mellowui.util.MellowUtils.TOOLTIP_MAX_WIDTH;

public class VanillaConfigEntries {
    // Tooltips
    private static final ITextComponent CLASSIC_STEREO_TOOLTIP = new TranslationTextComponent("config.minecraft.directional_audio.off_desc");
    private static final ITextComponent HRTF_BASED_AUDIO_TOOLTIP = new TranslationTextComponent("config.minecraft.directional_audio.on_desc");

    // Backported options
    public static final BooleanOption MONOCHROME_LOADING_SCREEN = new BooleanOption("config.minecraft.monochrome_loading_screen", new TranslationTextComponent("config.minecraft.monochrome_loading_screen.desc"),
            options -> CLIENT_CONFIGS.monochromeLoadingScreen.get(), (options, newValue) -> CLIENT_CONFIGS.monochromeLoadingScreen.set(newValue));
    public static final SliderPercentageOption PANORAMA_SCROLL_SPEED = new SliderPercentageOption("config.minecraft.panorama_scroll_speed", 0, 1, 0.01F,
            options -> CLIENT_CONFIGS.panoramaScrollSpeed.get(),
            (options, newValue) -> CLIENT_CONFIGS.panoramaScrollSpeed.set(newValue),
            (options, slider) -> new TranslationTextComponent("options.percent_value", new TranslationTextComponent("config.minecraft.panorama_scroll_speed"), (int) (slider.get(options) * 100)));
    public static final BooleanOption HIDE_SPLASH_TEXTS = new BooleanOption("config.minecraft.hide_splash_texts", new TranslationTextComponent("config.minecraft.hide_splash_texts.desc"),
            options -> CLIENT_CONFIGS.hideSplashTexts.get(), (options, newValue) -> CLIENT_CONFIGS.hideSplashTexts.set(newValue));
    public static final SliderPercentageOption MENU_BACKGROUND_BLURRINESS = new SliderPercentageOption("config.minecraft.menu_background_blurriness", 0, 10, 1,
            options -> CLIENT_CONFIGS.menuBackgroundBlurriness.get().doubleValue(),
            (options, newValue) -> CLIENT_CONFIGS.menuBackgroundBlurriness.set((int) Math.round(newValue)),
            (options, slider) -> {
                slider.setTooltip(Minecraft.getInstance().font.split(new TranslationTextComponent("config.minecraft.menu_background_blurriness.desc"), TOOLTIP_MAX_WIDTH));
                return new TranslationTextComponent("options.generic_value", new TranslationTextComponent("config.minecraft.menu_background_blurriness"), (int) Math.round(slider.get(options)));
            });
    public static final BooleanOption SHOW_MUSIC_TOAST = new BooleanOption("config.minecraft.show_music_toast", new TranslationTextComponent("config.minecraft.show_music_toast.desc"),
            options -> CLIENT_CONFIGS.showMusicToast.get(), (options, newValue) -> CLIENT_CONFIGS.showMusicToast.set(newValue));
    public static final HighContrastOption HIGH_CONTRAST = new HighContrastOption("config.minecraft.high_contrast", new TranslationTextComponent("config.minecraft.high_contrast.desc"),
            options -> CLIENT_CONFIGS.highContrastPack.get(), (options, newValue) -> {
        ResourcePackList packRepository = Minecraft.getInstance().getResourcePackRepository();
        boolean highContrastEnabled = packRepository.getSelectedIds().contains("mellowui:high_contrast");
        if (!highContrastEnabled && newValue) {
            if (((InterfaceMethods.PackRepositoryMethods) packRepository).addPack("mellowui:high_contrast")) {
                MUIOptionsScreen.updateResourcePacksList(packRepository);
            }
        } else if (highContrastEnabled && !newValue && ((InterfaceMethods.PackRepositoryMethods) packRepository).removePack("mellowui:high_contrast")) {
            MUIOptionsScreen.updateResourcePacksList(packRepository);
        }
        CLIENT_CONFIGS.highContrastPack.set(newValue);
    });
    public static final TooltippedIterableOption DIRECTIONAL_AUDIO = new TooltippedIterableOption("config.minecraft.directional_audio", new TranslationTextComponent("config.minecraft.directional_audio.desc"),
            (options, identifier) -> CLIENT_CONFIGS.directionalAudio.set(TwoStyles.byId(CLIENT_CONFIGS.directionalAudio.get().getId() + identifier)),
            (options, option) -> {
                switch (CLIENT_CONFIGS.directionalAudio.get()) {
                    case OPTION_1:
                        option.setTooltip(Minecraft.getInstance().font.split(CLASSIC_STEREO_TOOLTIP, 200));
                        break;
                    case OPTION_2:
                        option.setTooltip(Minecraft.getInstance().font.split(HRTF_BASED_AUDIO_TOOLTIP, 200));
                }
                return DialogTexts.optionStatus(new TranslationTextComponent("config.minecraft.directional_audio"), CLIENT_CONFIGS.directionalAudio.get() == TwoStyles.OPTION_2);
            });
    public static final SoundDeviceOption SOUND_DEVICE = new SoundDeviceOption();
    public static final BooleanOption REALMS_NEWS_AND_INVITES = new BooleanOption("config.minecraft.realms_notifications", new TranslationTextComponent("config.minecraft.realms_notifications.desc"),
            options -> options.realmsNotifications, (options, newValue) -> options.realmsNotifications = newValue);

    // Updated options
    public static final SliderPercentageOption FOV_EFFECTS = new SliderPercentageOption("options.fovEffectScale", 0, 1, 0,
            options -> Math.pow(options.fovEffectScale, 2),
            (options, newValue) -> options.fovEffectScale = MathHelper.sqrt(newValue),
            (options, slider) -> {
        slider.setTooltip(Minecraft.getInstance().font.split(new TranslationTextComponent("options.fovEffectScale.tooltip"), TOOLTIP_MAX_WIDTH));
        double percentage = slider.toPct(slider.get(options));
        ITextComponent translation = new TranslationTextComponent("options.fovEffectScale");
        return percentage == 0 ? DialogTexts.optionStatus(translation, false) : new TranslationTextComponent("options.percent_value", translation, (int) (percentage * 100));
    });
}
