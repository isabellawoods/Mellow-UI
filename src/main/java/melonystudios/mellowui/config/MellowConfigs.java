package melonystudios.mellowui.config;

import melonystudios.mellowui.util.ModListSorting;
import melonystudios.mellowui.util.MainMenuModButton;
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

    // Mellow UI Configs
    public final ForgeConfigSpec.IntValue monochromeLoadingScreenColor;
    public final ForgeConfigSpec.IntValue splashTextColor;
    public final ForgeConfigSpec.IntValue panoramaCameraPitch;
    public final ForgeConfigSpec.BooleanValue yellowButtonHighlight;
    public final ForgeConfigSpec.EnumValue<MainMenuModButton> mainMenuModButton;

    // Forge Configs
    public final ForgeConfigSpec.EnumValue<ModListSorting> modListSorting;

    public MellowConfigs(ForgeConfigSpec.Builder builder) {
        builder.push("vanillaOptions");
        this.monochromeLoadingScreen = builder.comment("Changes the Mojang Studios loading screen color from red to the color defined in the monochrome loading screen color config.").define("monochromeLoadingScreen", false);
        this.panoramaScrollSpeed = builder.comment("Changes the scrolling speed of the panorama in the menus.").defineInRange("panoramaScrollSpeed", 1F, 0, 1);
        this.hideSplashTexts = builder.comment("Toggles the ability to show or hide splashes on the main menu.").define("hideSplashTexts", false);
        builder.pop();

        builder.push("forgeOptions");
        this.modListSorting = builder.comment("How the mod list should be sorted.").defineEnum("modListSorting", ModListSorting.A_TO_Z);
        builder.pop();

        builder.push("mellowUI");
        this.monochromeLoadingScreenColor = builder.comment("The color to use for the loading screen when the \"Monochrome Logo\" config is true.").defineInRange("monochromeLoadingScreenColor", 0, 0, 0xFFFFFF);
        this.splashTextColor = builder.comment("The color to use for the splash text in the main menu.").defineInRange("splashTextColor", 0xFFFF00, 0, 0xFFFFFF);
        this.panoramaCameraPitch = builder.comment("The pitch used by the camera in the panorama. Defaults to 10.").defineInRange("panoramaCameraPitch", 10, -90, 90);
        this.yellowButtonHighlight = builder.comment("When enabled, hovering on buttons will make the text have a slight yellow tint.").define("yellowButtonHighlight", false);
        this.mainMenuModButton = builder.comment("Where the \"Mods\" button should be located in the main menu.").defineEnum("mainMenuModButton", MainMenuModButton.ADJACENT);
        builder.pop();
    }
}
