package melonystudios.mellowui.config;

import com.google.common.collect.Lists;
import melonystudios.mellowui.config.type.*;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class MellowConfigs {
    public static final List<String> CLASSIFIED_AS_CONTAINERS = Lists.newArrayList(
            "melonystudios.femalegender.gui.screen.BreastCustomizationScreen", "melonystudios.femalegender.gui.screen.CharacterSettingsScreen", "melonystudios.femalegender.gui.screen.PlayerListScreen", "melonystudios.femalegender.gui.screen.WardrobeScreen",
            "com.wildfire.gui.screen.WildfireSettingsScreen", "com.wildfire.gui.screen.WildfireBrowserScreen", "com.wildfire.gui.screen.WildfireCapeScreen", "com.wildfire.gui.screen.WildfireKeyScreen", "com.wildfire.gui.screen.SteinPlayerListScreen",
            "mezz.jei.gui.recipes.RecipesGui");
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

    // Mellow UI Configs
    public final ForgeConfigSpec.ConfigValue<List<String>> classifiedAsContainers;
    public final ForgeConfigSpec.IntValue panoramaCameraPitch;
    public final ForgeConfigSpec.BooleanValue panoramaBobbing;
    public final ForgeConfigSpec.BooleanValue legacyButtonColors;
    public final ForgeConfigSpec.BooleanValue scrollingText;
    public final ForgeConfigSpec.EnumValue<ThreeStyles> mainMenuModButton;
    public final ForgeConfigSpec.EnumValue<ThreeStyles> pauseMenuModButton;
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
    public final ForgeConfigSpec.EnumValue<ThreeStyles> mainMenuStyle;
    public final ForgeConfigSpec.EnumValue<ThreeStyles> modListStyle;
    public final ForgeConfigSpec.BooleanValue updatePauseMenu;
    public final ForgeConfigSpec.BooleanValue updateCreateNewWorldMenu;
    public final ForgeConfigSpec.BooleanValue updateOptionsMenu;
    public final ForgeConfigSpec.BooleanValue updateSkinCustomizationMenu;
    public final ForgeConfigSpec.BooleanValue updateMusicAndSoundsMenu;
    public final ForgeConfigSpec.EnumValue<ThreeStyles> updateVideoSettingsMenu;
    public final ForgeConfigSpec.BooleanValue updateControlsMenu;
    public final ForgeConfigSpec.BooleanValue updateMouseSettingsMenu;
    public final ForgeConfigSpec.BooleanValue updateChatSettingsMenu;
    public final ForgeConfigSpec.BooleanValue updatePackMenu;
    public final ForgeConfigSpec.BooleanValue updateAccessibilityMenu;
    public final ForgeConfigSpec.BooleanValue updateOutOfMemoryMenu;
    public final ForgeConfigSpec.BooleanValue updateScreenBackground;
    public final ForgeConfigSpec.BooleanValue updateListBackground;
    public final ForgeConfigSpec.BooleanValue replaceRealmsNotifications;
    public final ForgeConfigSpec.BooleanValue splashTextPosition;
    public final ForgeConfigSpec.EnumValue<ThreeStyles> logoStyle;

    // Forge Configs
    public final ForgeConfigSpec.EnumValue<ModListSorting> modListSorting;

    public MellowConfigs(ForgeConfigSpec.Builder builder) {
        builder.push("vanillaOptions");
        this.monochromeLoadingScreen = builder.comment("Changes the Mojang Studios loading screen color from red to the color defined in the monochrome loading screen color config.").define("monochromeLoadingScreen", false);
        this.panoramaScrollSpeed = builder.comment("Changes the scrolling speed of the panorama in the menus.").defineInRange("panoramaScrollSpeed", 1F, 0, 1);
        this.hideSplashTexts = builder.comment("Toggles the ability to show or hide splashes on the main menu.").define("hideSplashTexts", false);
        this.showMusicToast = builder.comment("Shows a toast with the name of the currently playing song.").define("showMusicToast", false);
        this.highContrastPack = builder.comment("Whether the 'High Contrast' resource pack is enabled.").define("highContrastPack", false);
        this.menuBackgroundBlurriness = builder.comment("How blurry the panorama background should be.", "Setting this to 20 will result in the same blurring effect as in the pre-1.13 panorama.").defineInRange("menuBackgroundBlurriness", 5, 0, 20);
        this.directionalAudio = builder.comment("Enables the use of HRTF-based directional audio to improve simulation of 3D sound.", "Option 1 = Classic stereo | Option 2 = HRTF-based audio").defineEnum("directionalAudio", TwoStyles.OPTION_1);
        this.soundDevice = builder.comment("Which device Minecraft should output audio from.").define("soundDevice", "");
        this.onboardAccessibility = builder.comment("Whether to show the accessibility onboarding menu upon loading the game for the first time.").define("onboardAccessibility", true);
        builder.pop();

        builder.push("forgeOptions");
        this.modListSorting = builder.comment("How the mod list should be sorted.").defineEnum("modListSorting", ModListSorting.A_TO_Z);
        builder.pop();

        builder.push("mellowUI");
        this.classifiedAsContainers = builder.comment("List of class paths for screens that don't blur the background and instead render a transparent gradient.").define("classifiedAsContainers", CLASSIFIED_AS_CONTAINERS);
        this.panoramaCameraPitch = builder.comment("The pitch used by the camera in the panorama. Defaults to 10.").defineInRange("panoramaCameraPitch", 10, -90, 90);
        this.panoramaBobbing = builder.comment("Whether the panorama should bob up and down instead of being at a consistent pitch.").define("panoramaBobbing", false);
        this.legacyButtonColors = builder.comment("When enabled, buttons will have slightly darker text, and hovering on them will make it have a slight yellow tint.").define("legacyButtonColors", false);
        this.scrollingText = builder.comment("Whether the text in buttons should scroll if it's too long instead of rendering on top of other widgets.").define("scrollingText", true);
        this.mainMenuModButton = builder.comment("Where the 'Mods' button should be located in the main menu.", "Option 1 = Adjacent | Option 2 = Icon | Option 3 = Replace Realms.").defineEnum("mainMenuModButton", ThreeStyles.OPTION_1);
        this.pauseMenuModButton = builder.comment("Where the 'Mods' button should be located in the pause menu.", "Option 1 = Adjacent | Option 2 = Icon | Option 3 = Replace Feedback.").defineEnum("pauseMenuModButton", ThreeStyles.OPTION_3);
        this.backgroundShaders = builder.comment("Whether shaders, like super secret settings and the blur, should render on the panorama.", "This may fix rendering issues with menus added by other mods.").define("backgroundShaders", true);
        this.logGLErrors = builder.comment("Whether to disable OpenGL error messages to not spam the logs.", "Useful if playing with Fabulous! graphics.").define("logGLErrors", false);
        this.blurryContainers = builder.comment("Whether to apply blur (or an extra layer of shaders) on the background while a container (chest, inventory, furnace) is open.").define("blurryContainers", false);
        this.defaultBackground = builder.comment("Whether mods that replace the 'options_background.png' texture should still work with Mellow UI.", "Setting this to false will make the transparent background be used regardless of that.").define("defaultBackground", false);
        this.gradientBackground = builder.comment("Whether in-game screens should use a gradient background instead of a regular transparent background.").define("gradientBackground", false);
        this.disableBranding = builder.comment("Whether to disable branding lines (the Forge and MCP versions) in the main menu.").define("disableBranding", true);

        this.mainMenuStyle = builder.comment("Which style to use for the main menu.", "Defaults to 'Vanilla' as the main menu is frequently updated by modpacks using FancyMenu.", "Option 1 = Vanilla | Option 2 = Mellow UI | Option 3 = Mellomedley").defineEnum("styles.mainMenu", ThreeStyles.OPTION_1);
        this.updatePauseMenu = builder.comment("Whether Mellow UI should update the pause menu.").define("updates.pauseMenu", true);
        this.updateCreateNewWorldMenu = builder.comment("Whether Mellow UI should update the create new world menu.").define("updates.createNewWorldMenu", false);
        this.updateOptionsMenu = builder.comment("Whether Mellow UI should update the options' menu.").define("updates.optionsMenu", true);
        this.updateSkinCustomizationMenu = builder.comment("Whether Mellow UI should update the skin customization menu.").define("updates.skinCustomizationMenu", true);
        this.updateMusicAndSoundsMenu = builder.comment("Whether Mellow UI should update the music & sounds menu.").define("updates.musicAndSoundsMenu", true);
        this.updateVideoSettingsMenu = builder.comment("Whether Mellow UI should update the video settings menu.", "Option 1 = Vanilla | Option 2 = Mellow UI | Option 3 = Rubidium (Sodium)").defineEnum("updates.videoSettingsMenu", ThreeStyles.OPTION_3);
        this.updateControlsMenu = builder.comment("Whether Mellow UI should update the controls menu, making the old screen the 'key binds' menu.").define("updates.controlsMenu", true);
        this.updateMouseSettingsMenu = builder.comment("Whether Mellow UI should update the mouse settings menu.").define("updates.mouseSettingsMenu", true);
        this.updateChatSettingsMenu = builder.comment("Whether Mellow UI should update the chat settings menu.").define("updates.chatSettingsMenu", true);
        this.updateAccessibilityMenu = builder.comment("Whether Mellow UI should update the accessibility settings menu.").define("updates.accessibilityMenu", true);
        this.updateOutOfMemoryMenu = builder.comment("Whether Mellow UI should update the out of memory menu.").define("updates.outOfMemoryMenu", true);
        this.updateScreenBackground = builder.comment("Whether Mellow UI should update the background of all screens for a transparent menu.").define("updates.screenBackground", true);
        this.updateListBackground = builder.comment("Whether Mellow UI should update the background of all lists (like video settings or languages) for a transparent menu.").define("updates.listBackground", true);
        this.replaceRealmsNotifications = builder.comment("Whether Mellow UI should replace the 'Realms Notifications' button on the options' menu with the 'Online Settings' menu.").define("updates.realmsNotifications", true);
        this.splashTextPosition = builder.comment("Where the splash texts should be located in the main menu.").define("splashTextPosition", true);
        this.logoStyle = builder.comment("Style to use for the Minecraft logo. Includes the current logo (1.16), new logo (1.20+), and the Mellomedley logo.", "Option 1 = -1.19 | Option 2 = +1.20 | Option 3 = Mellomedley").defineEnum("styles.logo", ThreeStyles.OPTION_2);
        this.modListStyle = builder.comment("Which style to use for the mod list.", "Option 1 = Forge | Option 2 = Mellow UI | Option 3 = Catalogue (if available).").defineEnum("styles.modList", ThreeStyles.OPTION_3);
        this.updatePackMenu = builder.comment("Which style to use for the resource and data packs list.", "True = Mellow UI | False = Vanilla.").define("styles.packList", false);
        builder.pop();

        builder.push("mellomedley");
        this.mellomedleyMainMenuModButton = builder.comment("Where the 'Mods' button should be located in Mellomedley's main menu. Rearranges buttons to fit.", "Option 1 = Below 'Options' | Option 2 = With Accessibility and Language").defineEnum("mainMenuModButton", TwoStyles.OPTION_1);
        this.mellomedleyVersion = builder.comment("The current version of the Mellomedley modpack.").define("mellomedleyVersion", "0.4");
        builder.pop();
    }
}
