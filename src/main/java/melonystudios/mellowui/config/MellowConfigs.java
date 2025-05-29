package melonystudios.mellowui.config;

import melonystudios.mellowui.config.type.*;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class MellowConfigs {
    private static final Pair<MellowConfigs, ForgeConfigSpec> CLIENT_CONFIG_PAIR = new ForgeConfigSpec.Builder().configure(MellowConfigs::new);
    public static final MellowConfigs CLIENT_CONFIGS = CLIENT_CONFIG_PAIR.getLeft();
    public static final ForgeConfigSpec CLIENT_SPEC = CLIENT_CONFIG_PAIR.getRight();

    // Backported Vanilla Configs
    public final ForgeConfigSpec.BooleanValue monochromeLoadingScreen;
    public final ForgeConfigSpec.DoubleValue panoramaScrollSpeed;
    public final ForgeConfigSpec.BooleanValue hideSplashTexts;
    public final ForgeConfigSpec.BooleanValue showMusicToast;

    // Mellow UI Configs
    public final ForgeConfigSpec.IntValue monochromeLoadingScreenColor;
    public final ForgeConfigSpec.IntValue splashTextColor;
    public final ForgeConfigSpec.IntValue panoramaCameraPitch;
    public final ForgeConfigSpec.BooleanValue legacyButtonColors;
    public final ForgeConfigSpec.BooleanValue scrollingText;
    public final ForgeConfigSpec.EnumValue<ThreeStyles> mainMenuModButton;
    public final ForgeConfigSpec.EnumValue<TwoStyles> pauseMenuModButton;
    public final ForgeConfigSpec.BooleanValue disableBranding;

    // Mellomedley Configs
    public final ForgeConfigSpec.IntValue mellomedleySplashTextColor;

    // Screen Toggles
    public final ForgeConfigSpec.EnumValue<ThreeStyles> mainMenuStyle;
    public final ForgeConfigSpec.EnumValue<ThreeStyles> modListStyle;
    public final ForgeConfigSpec.BooleanValue updatePauseMenu;
    public final ForgeConfigSpec.BooleanValue updateOptionsMenu;
    public final ForgeConfigSpec.BooleanValue updateSkinCustomizationMenu;
    public final ForgeConfigSpec.BooleanValue updateMusicAndSoundsMenu;
    public final ForgeConfigSpec.BooleanValue updateControlsMenu;
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
        builder.pop();

        builder.push("forgeOptions");
        this.modListSorting = builder.comment("How the mod list should be sorted.").defineEnum("modListSorting", ModListSorting.A_TO_Z);
        builder.pop();

        builder.push("mellowUI");
        this.monochromeLoadingScreenColor = builder.comment("The color to use for the loading screen when the \"Monochrome Logo\" config is true.").defineInRange("monochromeLoadingScreenColor", 0, 0, 0xFFFFFF);
        this.splashTextColor = builder.comment("The color to use for the splash text in the default main menu.").defineInRange("splashTextColor", 0xFFFF00, 0, 0xFFFFFF);
        this.panoramaCameraPitch = builder.comment("The pitch used by the camera in the panorama. Defaults to 10.").defineInRange("panoramaCameraPitch", 10, -90, 90);
        this.legacyButtonColors = builder.comment("When enabled, buttons will have slightly darker text, and hovering on them will make it have a slight yellow tint.").define("legacyButtonColors", false);
        this.scrollingText = builder.comment("Whether the text in buttons should scroll if it's too long instead of rendering on top of other widgets.").define("scrollingText", true);
        this.mainMenuModButton = builder.comment("Where the 'Mods' button should be located in the main menu.", "Option 1 = Adjacent | Option 2 = Icon | Option 3 = Replace Realms.").defineEnum("mainMenuModButton", ThreeStyles.OPTION_1);
        this.pauseMenuModButton = builder.comment("Where the 'Mods' button should be located in the pause menu.", "Option 1 = Replace | Option 2 = Icon.").defineEnum("pauseMenuModButton", TwoStyles.OPTION_1);
        this.disableBranding = builder.comment("Whether to disable branding lines (the Forge and MCP versions) in the main menu.").define("disableBranding", true);

        this.mainMenuStyle = builder.comment("Which style to use for the main menu.", "Defaults to 'Vanilla' as the main menu is frequently updated by modpacks using FancyMenu.", "Option 1 = Vanilla | Option 2 = Mellow UI | Option 3 = Mellomedley").defineEnum("styles.mainMenu", ThreeStyles.OPTION_1);
        this.updatePauseMenu = builder.comment("Whether Mellow UI should update the pause menu.").define("updates.pauseMenu", true);
        this.updateOptionsMenu = builder.comment("Whether Mellow UI should update the options menu.").define("updates.optionsMenu", true);
        this.updateSkinCustomizationMenu = builder.comment("Whether Mellow UI should update the skin customization menu.").define("updates.skinCustomizationMenu", true);
        this.updateMusicAndSoundsMenu = builder.comment("Whether Mellow UI should update the music & sounds menu.").define("updates.musicAndSoundsMenu", true);
        this.updateControlsMenu = builder.comment("Whether Mellow UI should update the controls menu, making the old screen the 'key binds' menu.").define("updates.controlsMenu", true);
        this.updateAccessibilityMenu = builder.comment("Whether Mellow UI should update the accessibility settings menu.").define("updates.accessibilityMenu", true);
        this.updateOutOfMemoryMenu = builder.comment("Whether Mellow UI should update the out of memory menu.").define("updates.outOfMemoryMenu", true);
        this.updateScreenBackground = builder.comment("Whether Mellow UI should update the background of all screens for a transparent menu.").define("updates.screenBackground", true);
        this.updateListBackground = builder.comment("Whether Mellow UI should update the background of all lists (like video settings or languages) for a transparent menu.").define("updates.listBackground", true);
        this.replaceRealmsNotifications = builder.comment("Whether Mellow UI should replace the 'Realms Notifications' button on the options menu with the 'Online Settings' menu.").define("updates.realmsNotifications", true);
        this.splashTextPosition = builder.comment("Where the splash texts should be located in the main menu.").define("splashTextPosition", true);
        this.logoStyle = builder.comment("Style to use for the Minecraft logo. Includes the current logo (1.16), new logo (1.20+), and the Mellomedley logo.", "Option 1 = -1.19 | Option 2 = +1.20 | Option 3 = Mellomedley").defineEnum("styles.logo", ThreeStyles.OPTION_2);
        this.modListStyle = builder.comment("Which style to use for the mod list.", "Option 1 = Forge | Option 2 = Mellow UI | Option 3 = Catalogue (if available).").defineEnum("styles.modList", ThreeStyles.OPTION_3);
        this.updatePackMenu = builder.comment("Which style to use for the resource and data packs list.", "True = Mellow UI | False = Vanilla.").define("styles.packList", false);
        builder.pop();

        builder.push("mellomedley");
        this.mellomedleySplashTextColor = builder.comment("The color to use for the splash text in the Mellomedley main menu.").defineInRange("splashTextColor", 0xBDCF73, 0, 0xFFFFFF);
        builder.pop();
    }
}
