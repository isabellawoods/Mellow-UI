package melonystudios.mellowui.config;

import melonystudios.mellowui.config.option.*;
import melonystudios.mellowui.config.type.ThreeStyles;
import melonystudios.mellowui.config.type.TwoStyles;
import melonystudios.mellowui.methods.InterfaceMethods;
import melonystudios.mellowui.screen.update.MUIOptionsScreen;
import melonystudios.mellowui.sound.MUISoundSource;
import melonystudios.mellowui.sound.SoundPreviewHandler;
import melonystudios.mellowui.util.GUITextures;
import net.minecraft.client.AmbientOcclusionStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ProgressOption;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.packs.repository.PackRepository;

import static melonystudios.mellowui.config.MellowConfigs.CLIENT_CONFIGS;
import static melonystudios.mellowui.element.RenderComponents.TOOLTIP_MAX_WIDTH;

public class VanillaConfigEntries {
    // Tooltips
    private static final Component CLASSIC_STEREO_TOOLTIP = new TranslatableComponent("config.minecraft.directional_audio.off_tooltip");
    private static final Component HRTF_BASED_AUDIO_TOOLTIP = new TranslatableComponent("config.minecraft.directional_audio.on_tooltip");
    private static final Component NEVER_TOOLTIP = new TranslatableComponent("config.minecraft.music_toast.option_1.tooltip");
    private static final Component PAUSE_MENU_TOOLTIP = new TranslatableComponent("config.minecraft.music_toast.option_2.tooltip");
    private static final Component PAUSE_MENU_AND_TOAST_TOOLTIP = new TranslatableComponent("config.minecraft.music_toast.option_3.tooltip");

    // Separators
    public static final SeparatorOption ACCESSIBILITY_SEPARATOR = new SeparatorOption(new TranslatableComponent("menu.minecraft.accessibility_settings.title"));
    public static final SeparatorOption MUSIC_AND_SOUNDS_SEPARATOR = new SeparatorOption(new TranslatableComponent("options.sounds.title"));
    public static final SeparatorOption MOUSE_SETTINGS_SEPARATOR = new SeparatorOption(new TranslatableComponent("options.mouse_settings.title"));

    // Backported options
    public static final ProgressOption PANORAMA_SCROLL_SPEED = new ProgressOption("config.minecraft.panorama_scroll_speed", 0, 1, 0.01F,
            options -> CLIENT_CONFIGS.panoramaScrollSpeed.get(),
            (options, newValue) -> CLIENT_CONFIGS.panoramaScrollSpeed.set(newValue),
            (options, slider) -> new TranslatableComponent("options.percent_value", new TranslatableComponent("config.minecraft.panorama_scroll_speed"), (int) (slider.get(options) * 100)));
    public static final BooleanOption HIDE_SPLASH_TEXTS = new BooleanOption("config.minecraft.hide_splash_texts", new TranslatableComponent("config.minecraft.hide_splash_texts.tooltip"),
            options -> CLIENT_CONFIGS.hideSplashTexts.get(), (options, newValue) -> CLIENT_CONFIGS.hideSplashTexts.set(newValue));
    public static final ProgressOption MENU_BACKGROUND_BLURRINESS = new ProgressOption("config.minecraft.menu_background_blurriness", 0, 20, 1,
            options -> CLIENT_CONFIGS.menuBackgroundBlurriness.get().doubleValue(),
            (options, newValue) -> CLIENT_CONFIGS.menuBackgroundBlurriness.set((int) Math.round(newValue)),
            (options, slider) -> {
                int value = (int) Math.round(slider.get(options));
                return new TranslatableComponent("options.generic_value", new TranslatableComponent("config.minecraft.menu_background_blurriness"), value != 0 ? value : new TranslatableComponent("options.off"));
            },
            minecraft -> minecraft.font.split(new TranslatableComponent("config.minecraft.menu_background_blurriness.tooltip"), TOOLTIP_MAX_WIDTH));
    public static final IterableOption MUSIC_TOAST = new MusicToastOption("config.minecraft.music_toast", new TranslatableComponent("config.minecraft.music_toast.option_1.tooltip"),
            (options, identifier) -> CLIENT_CONFIGS.musicToast.set(ThreeStyles.byId(CLIENT_CONFIGS.musicToast.get().getId() + identifier)),
            (options, option) -> {
                switch (CLIENT_CONFIGS.musicToast.get()) {
                    case OPTION_1:
                        option.setTooltip(NEVER_TOOLTIP);
                        break;
                    case OPTION_2:
                        option.setTooltip(PAUSE_MENU_TOOLTIP);
                        break;
                    case OPTION_3:
                        option.setTooltip(PAUSE_MENU_AND_TOAST_TOOLTIP);
                }
                return new TranslatableComponent("config.minecraft.music_toast", new TranslatableComponent("config.minecraft.music_toast." + CLIENT_CONFIGS.musicToast.get().toString()));
            });
    public static final HighContrastOption HIGH_CONTRAST = new HighContrastOption("config.minecraft.high_contrast", new TranslatableComponent("config.minecraft.high_contrast.tooltip"),
            options -> CLIENT_CONFIGS.highContrastPack.get(), (options, newValue) -> {
        PackRepository repository = Minecraft.getInstance().getResourcePackRepository();
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
    public static final IterableOption DIRECTIONAL_AUDIO = new IterableOption("config.minecraft.directional_audio", new TranslatableComponent("config.minecraft.directional_audio.off_tooltip"),
            (options, identifier) -> CLIENT_CONFIGS.directionalAudio.set(TwoStyles.byId(CLIENT_CONFIGS.directionalAudio.get().getId() + identifier)),
            (options, option) -> {
                switch (CLIENT_CONFIGS.directionalAudio.get()) {
                    case OPTION_1:
                        option.setTooltip(CLASSIC_STEREO_TOOLTIP);
                        break;
                    case OPTION_2:
                        option.setTooltip(HRTF_BASED_AUDIO_TOOLTIP);
                }
                return CommonComponents.optionStatus(new TranslatableComponent("config.minecraft.directional_audio"), CLIENT_CONFIGS.directionalAudio.get() == TwoStyles.OPTION_2);
            });
    public static final ProgressOption UI_VOLUME = new ProgressOption("config.minecraft.ui_volume", 0, 1, 0.01F,
            options -> CLIENT_CONFIGS.uiVolume.get(),
            (options, newValue) -> {
                if (Minecraft.getInstance().level == null) SoundPreviewHandler.preview(Minecraft.getInstance().getSoundManager(), MUISoundSource.UI, newValue.floatValue());
                CLIENT_CONFIGS.uiVolume.set(newValue);
            },
            (options, slider) -> {
                Component value = (int) (slider.get(options) * 100) == 0 ? CommonComponents.OPTION_OFF : new TranslatableComponent("config.minecraft.sound_category.percent", Integer.toString((int) (slider.get(options) * 100)));
                return new TranslatableComponent("options.generic_value", new TranslatableComponent("config.minecraft.sound_category.ui"), value);
            });
    public static final BooleanOption REALMS_NEWS_AND_INVITES = new BooleanOption("config.minecraft.realms_notifications", new TranslatableComponent("config.minecraft.realms_notifications.tooltip"),
            options -> options.realmsNotifications, (options, newValue) -> options.realmsNotifications = newValue);
    public static final BooleanOption ALLOW_CURSOR_CHANGES = new BooleanOption("config.minecraft.allow_cursor_changes", new TranslatableComponent("config.minecraft.allow_cursor_changes.tooltip"),
            options -> CLIENT_CONFIGS.allowCursorChanges.get(), (options, newValue) -> CLIENT_CONFIGS.allowCursorChanges.set(newValue));
    public static final BooleanOption ONBOARD_ACCESSIBILITY = new BooleanOption("config.minecraft.onboard_accessibility", new TranslatableComponent("config.minecraft.onboard_accessibility.tooltip"),
            options -> CLIENT_CONFIGS.onboardAccessibility.get(), (options, newValue) -> CLIENT_CONFIGS.onboardAccessibility.set(newValue));

    // Updated options
    public static final IterableOption SMOOTH_LIGHTING = new IterableOption("options.ao",
            (options, newValue) -> {
                options.ambientOcclusion = options.ambientOcclusion.getId() == 0 ? AmbientOcclusionStatus.MAX : AmbientOcclusionStatus.OFF;
                Minecraft.getInstance().levelRenderer.allChanged();
            },
            (options, button) -> CommonComponents.optionStatus(new TranslatableComponent("options.ao"), options.ambientOcclusion.getId() != 0));
    public static final BooleanOption CLOSED_CAPTIONS = new BooleanOption("config.minecraft.closed_captions", new TranslatableComponent("config.minecraft.closed_captions.tooltip"),
            options -> options.showSubtitles, (options, newValue) -> options.showSubtitles = newValue);
}
