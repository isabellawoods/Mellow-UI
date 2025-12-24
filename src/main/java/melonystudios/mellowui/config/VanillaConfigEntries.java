package melonystudios.mellowui.config;

import melonystudios.mellowui.config.option.*;
import melonystudios.mellowui.config.type.ThreeStyles;
import melonystudios.mellowui.config.type.TwoStyles;
import melonystudios.mellowui.methods.InterfaceMethods;
import melonystudios.mellowui.screen.update.MUIOptionsScreen;
import melonystudios.mellowui.sound.MUISoundCategory;
import melonystudios.mellowui.sound.SoundPreviewHandler;
import melonystudios.mellowui.util.GUITextures;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.settings.AmbientOcclusionStatus;
import net.minecraft.client.settings.BooleanOption;
import net.minecraft.client.settings.IteratableOption;
import net.minecraft.client.settings.SliderPercentageOption;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import static melonystudios.mellowui.config.MellowConfigs.CLIENT_CONFIGS;
import static melonystudios.mellowui.element.RenderComponents.TOOLTIP_MAX_WIDTH;

public class VanillaConfigEntries {
    // Tooltips
    private static final ITextComponent CLASSIC_STEREO_TOOLTIP = new TranslationTextComponent("config.minecraft.directional_audio.off_tooltip");
    private static final ITextComponent HRTF_BASED_AUDIO_TOOLTIP = new TranslationTextComponent("config.minecraft.directional_audio.on_tooltip");
    private static final ITextComponent NEVER_TOOLTIP = new TranslationTextComponent("config.minecraft.music_toast.option_1.tooltip");
    private static final ITextComponent PAUSE_MENU_TOOLTIP = new TranslationTextComponent("config.minecraft.music_toast.option_2.tooltip");
    private static final ITextComponent PAUSE_MENU_AND_TOAST_TOOLTIP = new TranslationTextComponent("config.minecraft.music_toast.option_3.tooltip");

    // Separators
    public static final SeparatorOption ACCESSIBILITY_SEPARATOR = new SeparatorOption(new TranslationTextComponent("menu.minecraft.accessibility_settings.title"));
    public static final SeparatorOption MUSIC_AND_SOUNDS_SEPARATOR = new SeparatorOption(new TranslationTextComponent("options.sounds.title"));
    public static final SeparatorOption MOUSE_SETTINGS_SEPARATOR = new SeparatorOption(new TranslationTextComponent("options.mouse_settings.title"));

    // Backported options
    public static final BooleanOption MONOCHROME_LOADING_SCREEN = new BooleanOption("config.minecraft.monochrome_loading_screen", new TranslationTextComponent("config.minecraft.monochrome_loading_screen.tooltip"),
            options -> CLIENT_CONFIGS.monochromeLoadingScreen.get(), (options, newValue) -> CLIENT_CONFIGS.monochromeLoadingScreen.set(newValue));
    public static final SliderPercentageOption PANORAMA_SCROLL_SPEED = new SliderPercentageOption("config.minecraft.panorama_scroll_speed", 0, 1, 0.01F,
            options -> CLIENT_CONFIGS.panoramaScrollSpeed.get(),
            (options, newValue) -> CLIENT_CONFIGS.panoramaScrollSpeed.set(newValue),
            (options, slider) -> new TranslationTextComponent("options.percent_value", new TranslationTextComponent("config.minecraft.panorama_scroll_speed"), (int) (slider.get(options) * 100)));
    public static final BooleanOption HIDE_SPLASH_TEXTS = new BooleanOption("config.minecraft.hide_splash_texts", new TranslationTextComponent("config.minecraft.hide_splash_texts.tooltip"),
            options -> CLIENT_CONFIGS.hideSplashTexts.get(), (options, newValue) -> CLIENT_CONFIGS.hideSplashTexts.set(newValue));
    public static final SliderPercentageOption MENU_BACKGROUND_BLURRINESS = new SliderPercentageOption("config.minecraft.menu_background_blurriness", 0, 20, 1,
            options -> CLIENT_CONFIGS.menuBackgroundBlurriness.get().doubleValue(),
            (options, newValue) -> CLIENT_CONFIGS.menuBackgroundBlurriness.set((int) Math.round(newValue)),
            (options, slider) -> {
                slider.setTooltip(Minecraft.getInstance().font.split(new TranslationTextComponent("config.minecraft.menu_background_blurriness.tooltip"), TOOLTIP_MAX_WIDTH));
                int value = (int) Math.round(slider.get(options));
                return new TranslationTextComponent("options.generic_value", new TranslationTextComponent("config.minecraft.menu_background_blurriness"), value != 0 ? value : new TranslationTextComponent("options.off"));
            });
    public static final IteratableOption MUSIC_TOAST = new MusicToastOption("config.minecraft.music_toast", new TranslationTextComponent("config.minecraft.music_toast.option_1.tooltip"),
            (options, identifier) -> CLIENT_CONFIGS.musicToast.set(ThreeStyles.byId(CLIENT_CONFIGS.musicToast.get().getId() + identifier)),
            (options, option) -> {
                switch (CLIENT_CONFIGS.musicToast.get()) {
                    case OPTION_1:
                        option.setTooltip(Minecraft.getInstance().font.split(NEVER_TOOLTIP, TOOLTIP_MAX_WIDTH));
                        break;
                    case OPTION_2:
                        option.setTooltip(Minecraft.getInstance().font.split(PAUSE_MENU_TOOLTIP, TOOLTIP_MAX_WIDTH));
                        break;
                    case OPTION_3:
                        option.setTooltip(Minecraft.getInstance().font.split(PAUSE_MENU_AND_TOAST_TOOLTIP, TOOLTIP_MAX_WIDTH));
                }
                return new TranslationTextComponent("config.minecraft.music_toast", new TranslationTextComponent("config.minecraft.music_toast." + CLIENT_CONFIGS.musicToast.get().toString()));
            });
    public static final HighContrastOption HIGH_CONTRAST = new HighContrastOption("config.minecraft.high_contrast", new TranslationTextComponent("config.minecraft.high_contrast.tooltip"),
            options -> CLIENT_CONFIGS.highContrastPack.get(), (options, newValue) -> {
        ResourcePackList repository = Minecraft.getInstance().getResourcePackRepository();
        boolean highContrastEnabled = repository.getSelectedIds().contains("mellowui:high_contrast");
        if (!highContrastEnabled && newValue) {
            if (((InterfaceMethods.PackRepositoryMethods) repository).addPack(GUITextures.MUI_HIGH_CONTRAST.toString())) {
                MUIOptionsScreen.updateResourcePacksList(repository);
            }
        } else if (highContrastEnabled && !newValue && ((InterfaceMethods.PackRepositoryMethods) repository).removePack(GUITextures.MUI_HIGH_CONTRAST.toString())) {
            MUIOptionsScreen.updateResourcePacksList(repository);
        }
        CLIENT_CONFIGS.highContrastPack.set(newValue);
    });
    public static final TooltippedIterableOption DIRECTIONAL_AUDIO = new TooltippedIterableOption("config.minecraft.directional_audio", new TranslationTextComponent("config.minecraft.directional_audio.off_tooltip"),
            (options, identifier) -> CLIENT_CONFIGS.directionalAudio.set(TwoStyles.byId(CLIENT_CONFIGS.directionalAudio.get().getId() + identifier)),
            (options, option) -> {
                switch (CLIENT_CONFIGS.directionalAudio.get()) {
                    case OPTION_1:
                        option.setTooltip(Minecraft.getInstance().font.split(CLASSIC_STEREO_TOOLTIP, TOOLTIP_MAX_WIDTH));
                        break;
                    case OPTION_2:
                        option.setTooltip(Minecraft.getInstance().font.split(HRTF_BASED_AUDIO_TOOLTIP, TOOLTIP_MAX_WIDTH));
                }
                return DialogTexts.optionStatus(new TranslationTextComponent("config.minecraft.directional_audio"), CLIENT_CONFIGS.directionalAudio.get() == TwoStyles.OPTION_2);
            });
    public static final SoundDeviceOption SOUND_DEVICE = new SoundDeviceOption("config.minecraft.sound_device");
    public static final SliderPercentageOption UI_VOLUME = new SliderPercentageOption("config.minecraft.sound_category.ui", 0, 1, 0.01F,
            options -> CLIENT_CONFIGS.uiVolume.get(),
            (options, newValue) -> {
                if (Minecraft.getInstance().level == null) SoundPreviewHandler.preview(Minecraft.getInstance().getSoundManager(), MUISoundCategory.UI, newValue.floatValue());
                CLIENT_CONFIGS.uiVolume.set(newValue);
            },
            (options, slider) -> {
                ITextComponent value = (int) (slider.get(options) * 100) == 0 ? DialogTexts.OPTION_OFF : new TranslationTextComponent("config.minecraft.sound_category.percent", Integer.toString((int) (slider.get(options) * 100)));
                return new TranslationTextComponent("options.generic_value", new TranslationTextComponent("config.minecraft.sound_category.ui"), value);
            });
    public static final BooleanOption REALMS_NEWS_AND_INVITES = new BooleanOption("config.minecraft.realms_notifications", new TranslationTextComponent("config.minecraft.realms_notifications.tooltip"),
            options -> options.realmsNotifications, (options, newValue) -> options.realmsNotifications = newValue);
    public static final BooleanOption ALLOW_CURSOR_CHANGES = new BooleanOption("config.minecraft.allow_cursor_changes", new TranslationTextComponent("config.minecraft.allow_cursor_changes.tooltip"),
            options -> CLIENT_CONFIGS.allowCursorChanges.get(), (options, newValue) -> CLIENT_CONFIGS.allowCursorChanges.set(newValue));
    public static final BooleanOption ONBOARD_ACCESSIBILITY = new BooleanOption("config.minecraft.onboard_accessibility", new TranslationTextComponent("config.minecraft.onboard_accessibility.tooltip"),
            options -> CLIENT_CONFIGS.onboardAccessibility.get(), (options, newValue) -> CLIENT_CONFIGS.onboardAccessibility.set(newValue));

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
    public static final IteratableOption SMOOTH_LIGHTING = new IteratableOption("options.ao",
            (options, newValue) -> {
                options.ambientOcclusion = options.ambientOcclusion.getId() == 0 ? AmbientOcclusionStatus.MAX : AmbientOcclusionStatus.OFF;
                Minecraft.getInstance().levelRenderer.allChanged();
            },
            (options, button) -> DialogTexts.optionStatus(new TranslationTextComponent("options.ao"), options.ambientOcclusion.getId() != 0));
    public static final SliderPercentageOption BRIGHTNESS = new SliderPercentageOption("options.gamma", 0, 1, 0,
            options -> options.gamma,
            (options, newValue) -> options.gamma = newValue,
            (options, slider) -> {
        double percentage = slider.toPct(slider.get(options));
        int rounded = (int) (percentage * 100);

        if (rounded == 0) {
            return new TranslationTextComponent("options.generic_value", new TranslationTextComponent("options.gamma"), new TranslationTextComponent("options.gamma.min"));
        } else if (rounded == 50) {
            return new TranslationTextComponent("options.generic_value", new TranslationTextComponent("options.gamma"), new TranslationTextComponent("config.minecraft.brightness.default"));
        } else if (rounded == 100) {
            return new TranslationTextComponent("options.generic_value", new TranslationTextComponent("options.gamma"), new TranslationTextComponent("options.gamma.max"));
        } else {
            return new TranslationTextComponent("options.generic_value", new TranslationTextComponent("options.gamma"), (int) (percentage * 100));
        }
    });
    public static final BooleanOption CLOSED_CAPTIONS = new BooleanOption("config.minecraft.closed_captions", new TranslationTextComponent("config.minecraft.closed_captions.tooltip"),
            options -> options.showSubtitles, (options, newValue) -> options.showSubtitles = newValue);
    public static final BooleanOption FORCE_UNICODE_FONT = new BooleanOption("options.forceUnicodeFont",
            AbstractOption.FORCE_UNICODE_FONT::get,
            (options, newValue) -> {
                Minecraft minecraft = Minecraft.getInstance();
                AbstractOption.FORCE_UNICODE_FONT.set(options, Boolean.toString(newValue));
                minecraft.resizeDisplay();
            });
}
