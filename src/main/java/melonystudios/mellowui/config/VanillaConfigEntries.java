package melonystudios.mellowui.config;

import melonystudios.mellowui.config.option.BooleanOption;
import melonystudios.mellowui.config.option.HighContrastOption;
import melonystudios.mellowui.config.option.MusicToastOption;
import melonystudios.mellowui.config.option.IterableOption;
import melonystudios.mellowui.config.type.TwoStyles;
import melonystudios.mellowui.methods.InterfaceMethods;
import melonystudios.mellowui.screen.update.MUIOptionsScreen;
import melonystudios.mellowui.util.GUITextures;
import net.minecraft.client.AmbientOcclusionStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ProgressOption;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.packs.repository.PackRepository;

import static melonystudios.mellowui.config.MellowConfigs.CLIENT_CONFIGS;
import static melonystudios.mellowui.screen.RenderComponents.TOOLTIP_MAX_WIDTH;

public class VanillaConfigEntries {
    // Tooltips
    private static final MutableComponent CLASSIC_STEREO_TOOLTIP = new TranslatableComponent("config.minecraft.directional_audio.off_desc");
    private static final MutableComponent HRTF_BASED_AUDIO_TOOLTIP = new TranslatableComponent("config.minecraft.directional_audio.on_desc");

    // Backported options
    public static final ProgressOption PANORAMA_SCROLL_SPEED = new ProgressOption("config.minecraft.panorama_scroll_speed", 0, 1, 0.01F,
            options -> CLIENT_CONFIGS.panoramaScrollSpeed.get(),
            (options, newValue) -> CLIENT_CONFIGS.panoramaScrollSpeed.set(newValue),
            (options, slider) -> new TranslatableComponent("options.percent_value", new TranslatableComponent("config.minecraft.panorama_scroll_speed"), (int) (slider.get(options) * 100)));
    public static final BooleanOption HIDE_SPLASH_TEXTS = new BooleanOption("config.minecraft.hide_splash_texts", new TranslatableComponent("config.minecraft.hide_splash_texts.desc"),
            options -> CLIENT_CONFIGS.hideSplashTexts.get(), (options, newValue) -> CLIENT_CONFIGS.hideSplashTexts.set(newValue));
    public static final ProgressOption MENU_BACKGROUND_BLURRINESS = new ProgressOption("config.minecraft.menu_background_blurriness", 0, 10, 1,
            options -> CLIENT_CONFIGS.menuBackgroundBlurriness.get().doubleValue(),
            (options, newValue) -> CLIENT_CONFIGS.menuBackgroundBlurriness.set((int) Math.round(newValue)),
            (options, slider) -> new TranslatableComponent("options.generic_value", new TranslatableComponent("config.minecraft.menu_background_blurriness"), (int) Math.round(slider.get(options))),
            minecraft -> minecraft.font.split(new TranslatableComponent("config.minecraft.menu_background_blurriness.desc"), TOOLTIP_MAX_WIDTH));
    public static final BooleanOption SHOW_MUSIC_TOAST = new MusicToastOption("config.minecraft.show_music_toast", new TranslatableComponent("config.minecraft.show_music_toast.desc"),
            options -> CLIENT_CONFIGS.showMusicToast.get(), (options, newValue) -> CLIENT_CONFIGS.showMusicToast.set(newValue));
    public static final HighContrastOption HIGH_CONTRAST = new HighContrastOption("config.minecraft.high_contrast", new TranslatableComponent("config.minecraft.high_contrast.desc"),
            options -> CLIENT_CONFIGS.highContrastPack.get(), (options, newValue) -> {
        PackRepository packRepository = Minecraft.getInstance().getResourcePackRepository();
        boolean highContrastEnabled = packRepository.getSelectedIds().contains("mellowui:high_contrast");
        if (!highContrastEnabled && newValue) {
            if (((InterfaceMethods.PackRepositoryMethods) packRepository).addPack(GUITextures.MUI_HIGH_CONTRAST.toString())) {
                MUIOptionsScreen.updateResourcePacksList(packRepository);
            }
        } else if (highContrastEnabled && !newValue && ((InterfaceMethods.PackRepositoryMethods) packRepository).removePack(GUITextures.MUI_HIGH_CONTRAST.toString())) {
            MUIOptionsScreen.updateResourcePacksList(packRepository);
        }
        CLIENT_CONFIGS.highContrastPack.set(newValue);
    });
    public static final IterableOption DIRECTIONAL_AUDIO = new IterableOption("config.minecraft.directional_audio", new TranslatableComponent("config.minecraft.directional_audio.off_desc"),
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
    public static final BooleanOption REALMS_NEWS_AND_INVITES = new BooleanOption("config.minecraft.realms_notifications", new TranslatableComponent("config.minecraft.realms_notifications.desc"),
            options -> options.realmsNotifications, (options, newValue) -> options.realmsNotifications = newValue);

    // Updated options
    public static final IterableOption SMOOTH_LIGHTING = new IterableOption("options.ao",
            (options, newValue) -> {
                options.ambientOcclusion = options.ambientOcclusion.getId() == 0 ? AmbientOcclusionStatus.MAX : AmbientOcclusionStatus.OFF;
                Minecraft.getInstance().levelRenderer.allChanged();
            },
            (options, button) -> CommonComponents.optionStatus(new TranslatableComponent("options.ao"), options.ambientOcclusion.getId() != 0));
}
