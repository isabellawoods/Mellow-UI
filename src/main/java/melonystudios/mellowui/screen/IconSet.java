package melonystudios.mellowui.screen;

import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.config.MellowConfigEntries;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.screen.updated.MellomedleyMainMenuScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.settings.IteratableOption;
import net.minecraft.util.ResourceLocation;

public class IconSet {
    public static final IconSet SWITCH_STYLE = new IconSet(MellowUI.gui("icon/main_menu/switch_style"), MellowUI.gui("icon/main_menu/switch_style_highlighted"), MellowUI.gui("icon/main_menu/switch_style_disabled"));
    private final ResourceLocation defaultTexture;
    private final ResourceLocation highlightedTexture;
    private final ResourceLocation inactiveTexture;

    public IconSet(ResourceLocation defaultTexture, ResourceLocation highlightedTexture, ResourceLocation inactiveTexture) {
        this.defaultTexture = defaultTexture;
        this.highlightedTexture = highlightedTexture;
        this.inactiveTexture = inactiveTexture;
    }

    public ResourceLocation getIconTexture(boolean highlighted, boolean active) {
        if (!active) {
            return this.inactiveTexture();
        } else if (highlighted) {
            return this.highlightedTexture();
        } else {
            return this.defaultTexture();
        }
    }

    public ResourceLocation defaultTexture() {
        return this.defaultTexture;
    }

    public ResourceLocation highlightedTexture() {
        return this.highlightedTexture;
    }

    public ResourceLocation inactiveTexture() {
        return this.inactiveTexture;
    }

    public static void switchMainMenuStyle() {
        Minecraft minecraft = Minecraft.getInstance();
        IteratableOption option = MellowConfigEntries.MAIN_MENU_STYLE;
        option.toggle(minecraft.options, 1);
        switch (MellowConfigs.CLIENT_CONFIGS.mainMenuStyle.get()) {
            case MELLOMEDLEY: minecraft.setScreen(new MellomedleyMainMenuScreen());
            case VANILLA: case MELLOW_UI: default: minecraft.setScreen(new MainMenuScreen());
        }
    }
}
