package melonystudios.mellowui.config;

import com.google.common.collect.Lists;
import melonystudios.mellowui.config.type.*;
import melonystudios.mellowui.resource.panorama.Panorama;
import net.minecraft.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class MellowConfigs {
    public static final List<String> CLASSIFIED_AS_CONTAINERS = Lists.newArrayList(
            "melonystudios.femalegender.gui.screen.BreastCustomizationScreen", "melonystudios.femalegender.gui.screen.CharacterSettingsScreen", "melonystudios.femalegender.gui.screen.PlayerListScreen", "melonystudios.femalegender.gui.screen.WardrobeScreen",
            "com.wildfire.gui.screen.WildfireSettingsScreen", "com.wildfire.gui.screen.WardrobeBrowserScreen", "com.wildfire.gui.screen.WildfireCapeScreen", "com.wildfire.gui.screen.WildfireKeyScreen", "com.wildfire.gui.screen.SteinPlayerListScreen",
            "mezz.jei.gui.recipes.RecipesGui", "me.shedaniel.rei.impl.client.gui.screen.DefaultDisplayViewingScreen");
    private static final Pair<MellowConfigs, ForgeConfigSpec> CLIENT_CONFIG_PAIR = new ForgeConfigSpec.Builder().configure(MellowConfigs::new);
    public static final MellowConfigs CLIENT_CONFIGS = CLIENT_CONFIG_PAIR.getLeft();
    public static final ForgeConfigSpec CLIENT_SPEC = CLIENT_CONFIG_PAIR.getRight();

    // Backported Vanilla Configs
    public final ForgeConfigSpec.BooleanValue monochromeLoadingScreen;
    public final ForgeConfigSpec.DoubleValue panoramaScrollSpeed;
    public final ForgeConfigSpec.BooleanValue hideSplashTexts;
    public final ForgeConfigSpec.BooleanValue showMusicToast;
    public final ForgeConfigSpec.BooleanValue highContrastPack;
    public final ForgeConfigSpec.IntValue menuBackgroundBlurriness;
    public final ForgeConfigSpec.EnumValue<TwoStyles> directionalAudio;
    public final ForgeConfigSpec.ConfigValue<String> soundDevice;
    public final ForgeConfigSpec.BooleanValue onboardAccessibility;
    public final ForgeConfigSpec.DoubleValue uiVolume;

    // Mellow UI Configs
    public final ForgeConfigSpec.ConfigValue<List<String>> classifiedAsContainers;
    public final ForgeConfigSpec.ConfigValue<List<String>> oversizedInGUI;
    public final ForgeConfigSpec.IntValue panoramaCameraPitch;
    public final ForgeConfigSpec.BooleanValue panoramaBobbing;
    public final ForgeConfigSpec.BooleanValue legacyButtonColors;
    public final ForgeConfigSpec.BooleanValue scrollingText;
    public final ForgeConfigSpec.EnumValue<FourStyles> mainMenuModButton;
    public final ForgeConfigSpec.EnumValue<FourStyles> pauseMenuModButton;
    public final ForgeConfigSpec.BooleanValue cullOversizedItems;
    public final ForgeConfigSpec.BooleanValue backgroundShaders;
    public final ForgeConfigSpec.BooleanValue logGLErrors;
    public final ForgeConfigSpec.BooleanValue blurryContainers;
    public final ForgeConfigSpec.BooleanValue defaultBackground;
    public final ForgeConfigSpec.BooleanValue gradientBackground;
    public final ForgeConfigSpec.BooleanValue disableBranding;

    // Mellomedley Configs
    public final ForgeConfigSpec.EnumValue<TwoStyles> mellomedleyMainMenuModButton;
    public final ForgeConfigSpec.ConfigValue<String> mellomedleyVersion;

    // Screen Toggles
    public final ForgeConfigSpec.EnumValue<ThreeStyles> titleStyle;
    public final ForgeConfigSpec.EnumValue<ThreeStyles> modListStyle;
    public final ForgeConfigSpec.BooleanValue pauseStyle;
    public final ForgeConfigSpec.BooleanValue createNewWorldStyle;
    public final ForgeConfigSpec.BooleanValue optionsStyle;
    public final ForgeConfigSpec.BooleanValue skinCustomizationStyle;
    public final ForgeConfigSpec.BooleanValue musicAndSoundsStyle;
    public final ForgeConfigSpec.EnumValue<ThreeStyles> videoSettingsStyle;
    public final ForgeConfigSpec.BooleanValue controlsStyle;
    public final ForgeConfigSpec.BooleanValue mouseSettingsStyle;
    public final ForgeConfigSpec.BooleanValue chatSettingsStyle;
    public final ForgeConfigSpec.BooleanValue packListStyle;
    public final ForgeConfigSpec.BooleanValue accessibilitySettingsStyle;
    public final ForgeConfigSpec.BooleanValue outOfMemoryStyle;
    public final ForgeConfigSpec.BooleanValue statisticsStyle;
    public final ForgeConfigSpec.BooleanValue screenBackgroundStyle;
    public final ForgeConfigSpec.BooleanValue listBackgroundStyle;
    public final ForgeConfigSpec.BooleanValue replaceRealmsNotifications;
    public final ForgeConfigSpec.BooleanValue splashTextPosition;
    public final ForgeConfigSpec.EnumValue<FourStyles> logoStyle;

    // Forge Configs
    public final ForgeConfigSpec.EnumValue<ModListSorting> modListSorting;

    // Dynamic resource config
    public final ForgeConfigSpec.ConfigValue<String> selectedPanorama;

    public MellowConfigs(ForgeConfigSpec.Builder builder) {
        builder.push("vanillaOptions");
        this.monochromeLoadingScreen = builder.comment("Changes the Mojang Studios loading screen background color to, by default, black.").translation("config.minecraft.monochrome_loading_screen").define("monochromeLoadingScreen", false);
        this.panoramaScrollSpeed = builder.comment("Changes the scrolling speed of the panoramic background.").translation("config.minecraft.panorama_scroll_speed").defineInRange("panoramaScrollSpeed", 1F, 0, 1);
        this.menuBackgroundBlurriness = builder.comment("Changes the blurriness of menu backgrounds.", "Setting this to 20 will result in the same blurring effect as in the pre-1.13 panorama.").translation("config.minecraft.menu_background_blurriness").defineInRange("menuBackgroundBlurriness", 5, 0, 20);
        this.hideSplashTexts = builder.comment("Hides the yellow splash text in the main menu.").translation("config.minecraft.hide_splash_texts").define("hideSplashTexts", false);
        this.showMusicToast = builder.comment("Displays a toast whenever a song starts playing.", "The same toast is constantly displayed in the in-game pause menu while a song is playing.").translation("config.minecraft.show_music_toast").define("showMusicToast", false);
        this.highContrastPack = builder.comment("Enhances the contrast of UI elements.").translation("config.minecraft.high_contrast").define("highContrastPack", false);
        this.directionalAudio = builder.comment("Enables the use of HRTF-based directional audio to improve simulation of 3D sound.", "Option 1 = Classic stereo | Option 2 = HRTF-based directional audio").translation("config.minecraft.directional_audio").defineEnum("directionalAudio", TwoStyles.OPTION_1);
        this.soundDevice = builder.comment("Which device Minecraft should output audio from.").translation("config.minecraft.sound_device").define("soundDevice", "");
        this.onboardAccessibility = builder.comment("Whether to show the accessibility onboarding menu upon loading the game for the first time.").translation("config.minecraft.onboard_accessiblity").define("onboardAccessibility", true);
        this.uiVolume = builder.comment("The volume of the in-game UI elements.").translation("config.minecraft.sound_category.ui").defineInRange("uiVolume", 1F, 0, 1);
        builder.pop();

        builder.push("forgeOptions");
        this.modListSorting = builder.comment("How the mod list should be sorted.").translation("config.forge.mod_list_sorting").defineEnum("modListSorting", ModListSorting.A_TO_Z);
        builder.pop();

        builder.push("mellowUIOptions");
        this.classifiedAsContainers = builder.comment("List of classes for screens that don't blur the background and instead render a transparent gradient.").translation("config.mellowui.classified_as_containers").define("classifiedAsContainers", CLASSIFIED_AS_CONTAINERS);
        this.oversizedInGUI = builder.comment("List of items that are allowed to render past their slot boundary.").translation("config.mellowui.oversized_in_gui").define("oversizedInGUI", Lists.newArrayList());
        this.cullOversizedItems = builder.comment("Whether items that render past their slot should be culled to fit.").translation("config.mellowui.cull_oversized_items").define("cullOversizedItems", true);
        this.panoramaCameraPitch = builder.comment("The pitch of the panoramic camera. Defaults to 10º.").translation("config.mellowui.panorama_camera_pitch").defineInRange("panoramaCameraPitch", 10, -90, 90);
        this.panoramaBobbing = builder.comment("Whether the panorama should bob up and down instead of being at a consistent pitch.").translation("config.mellowui.panorama_bobbing").define("panoramaBobbing", false);
        this.legacyButtonColors = builder.comment("Makes the text in widgets slightly darker, and when hovered the text will have a slight yellow tint.").translation("config.mellowui.legacy_button_colors").define("legacyButtonColors", false);
        this.scrollingText = builder.comment("Makes the text in widgets scroll if it's too long instead of rendering on top of other widgets.").translation("config.mellowui.scrolling_text").define("scrollingText", true);
        this.splashTextPosition = builder.comment("Where the splash texts should be positioned in the main menu.").define("splashTextPosition", true);
        this.mainMenuModButton = builder.comment("Where the 'Mods' button should be located in the main menu.", "Option 1 = Adjacent to Realms | Option 2 = Icon | Option 3 = Replace Realms | Option 4 = Below Realms.").translation("config.mellowui.main_menu_mod_button").defineEnum("mainMenuModButton", FourStyles.OPTION_1);
        this.pauseMenuModButton = builder.comment("Where the 'Mods' button should be located in the pause menu.", "Option 1 = Adjacent | Option 2 = Icon | Option 3 = Replace Feedback | Option 4 = Below Options.").translation("config.mellowui.pause_menu_mod_button").defineEnum("pauseMenuModButton", FourStyles.OPTION_3);
        this.replaceRealmsNotifications = builder.comment("Replaces the 'Realms Notifications' button in the options screen with the online options button from newer versions.").define("replaceRealmsNotifications", true);
        this.backgroundShaders = builder.comment("Whether shaders, like super secret settings and the blur, should render on the panorama.", "This may fix rendering issues with menus added by other mods.").translation("config.mellowui.background_shaders").define("backgroundShaders", true);
        this.logGLErrors = builder.comment("Whether to log OpenGL error messages, in order to not spam the logs.", "Useful if playing with Fabulous! graphics.").translation("config.mellowui.log_gl_errors").define("logGLErrors", false);
        this.blurryContainers = builder.comment("If the background blur and shaders should be applied on containers like chests, furnaces and your inventory.").translation("config.mellowui.blurry_containers").define("blurryContainers", false);
        this.defaultBackground = builder.comment("Whether to use the default dirt background texture instead of the current, transparent background.").translation("config.mellowui.default_background").define("defaultBackground", false);
        this.gradientBackground = builder.comment("Makes in-game screens use a gray gradient background instead of a regular transparent background.").translation("config.mellowui.gradient_background").define("gradientBackground", false);
        this.disableBranding = builder.comment("Disables Forge's branding lines. Branding lines are the Forge and MCP versions on the bottom-left corner.").translation("config.mellowui.disable_branding").define("disableBranding", true);
        builder.pop();

        builder.push("styleOptions");
        this.screenBackgroundStyle = builder.comment("Whether Mellow UI should update the background of all screens for a transparent menu.").translation("config.mellowui.screen_background_style").define("screenBackground", true);
        this.listBackgroundStyle = builder.comment("Whether Mellow UI should update the background of all lists (like video settings or languages) for a transparent menu.").translation("config.mellowui.list_background_style").define("listBackground", true);
        this.logoStyle = builder.comment("Style to use for the Minecraft logo. Includes the pre-1.16 logo, the current logo (1.16), the new logo (1.20+), and the Mellomedley logo.", "Option 1 = Pre-1.16 | Option 2 = 1.19 | Option 3 = 1.20+ | Option 4 = Mellomedley.").translation("config.mellowui.logo_style").defineEnum("logo", FourStyles.OPTION_3);
        this.titleStyle = builder.comment("Which style to use for the main menu / title screen.", "Defaults to 'Vanilla' as the main menu is frequently updated by modpacks using FancyMenu.", "Option 1 = Vanilla | Option 2 = Mellow UI | Option 3 = Mellomedley").translation("config.mellowui.title_style").defineEnum("mainMenu", ThreeStyles.OPTION_1);
        this.pauseStyle = builder.comment("Which style to use for the pause menu.").translation("config.mellowui.pause_style").define("pauseMenu", true);
        this.createNewWorldStyle = builder.comment("Which style to use for the create new world menu.").translation("config.mellowui.create_new_world_style").define("createNewWorldMenu", false);
        this.optionsStyle = builder.comment("Which style to use for the options' menu.").translation("config.mellowui.options_style").define("optionsMenu", true);
        this.skinCustomizationStyle = builder.comment("Which style to use for the skin customization menu.").translation("config.mellowui.skin_customization_style").define("skinCustomizationMenu", true);
        this.musicAndSoundsStyle = builder.comment("Which style to use for the music & sounds menu.").translation("config.mellowui.music_and_sounds_style").define("musicAndSoundsMenu", true);
        this.videoSettingsStyle = builder.comment("Which style to use for the video settings menu.", "Option 1 = Vanilla | Option 2 = Mellow UI | Option 3 = Rubidium").translation("config.mellowui.video_settings_style").defineEnum("videoSettingsMenu", ThreeStyles.OPTION_3);
        this.controlsStyle = builder.comment("Which style to use for the menu accessed from the controls button.").translation("config.mellowui.controls_style").define("controlsMenu", true);
        this.mouseSettingsStyle = builder.comment("Which style to use for the mouse settings menu.").translation("config.mellowui.mouse_settings_style").define("mouseSettingsMenu", true);
        this.chatSettingsStyle = builder.comment("Which style to use for the chat settings menu.").translation("config.mellowui.chat_settings_style").define("chatSettingsMenu", true);
        this.packListStyle = builder.comment("Which style to use for the resource and data packs list.", "True = Mellow UI | False = Vanilla.").translation("config.mellowui.pack_list_style").define("packListMenu", false);
        this.accessibilitySettingsStyle = builder.comment("Which style to use for the accessibility settings menu.").translation("config.mellowui.accessibility_settings_style").define("accessibilitySettingsMenu", true);
        this.outOfMemoryStyle = builder.comment("Which style to use for the out of memory menu.").translation("config.mellowui.out_of_memory_style").define("outOfMemoryMenu", true);
        this.statisticsStyle = builder.comment("Which style to use for the statistics menu.").translation("config.mellowui.statistics_style").define("statisticsMenu", true);
        this.modListStyle = builder.comment("Which style to use for the mod list.", "Option 1 = Forge | Option 2 = Mellow UI | Option 3 = Catalogue (if available).").translation("config.mellowui.mod_list_style").defineEnum("modListMenu", ThreeStyles.OPTION_3);
        builder.pop();

        builder.push("mellomedleyOptions");
        this.mellomedleyMainMenuModButton = builder.comment("Where the 'Mods' button should be located in Mellomedley's main menu.", "Option 1 = Icon | Option 2 = Below 'Options'").translation("config.mellomedley.main_menu_mod_button").defineEnum("mainMenuModButton", TwoStyles.OPTION_1);
        this.mellomedleyVersion = builder.comment("The current version of the Mellomedley modpack, displayed on its variant of the main menu.").translation("config.mellomedley.mellomedley_version").define("mellomedleyVersion", "0.6");
        builder.pop();

        builder.push("dynamicResources");
        this.selectedPanorama = builder.comment("The currently selected panorama. This overrides any panoramas added via resource packs.").define("selectedPanorama", Panorama.DEFAULT_LOCATION.toString());
        builder.pop();
    }

    /// Whether the specified item can render outside its slot boundary.
    /// @param item The item to check.
    public static boolean oversizedInGUI(Item item) {
        return !MellowConfigs.CLIENT_CONFIGS.cullOversizedItems.get() || MellowConfigs.CLIENT_CONFIGS.oversizedInGUI.get().contains(item.getRegistryName().toString());
    }
}
