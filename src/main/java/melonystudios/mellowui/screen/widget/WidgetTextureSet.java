package melonystudios.mellowui.screen.widget;

import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.config.type.ThreeStyles;
import melonystudios.mellowui.screen.MellomedleyTitleScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.util.ResourceLocation;

public class WidgetTextureSet {
    private final ResourceLocation defaultTexture;
    private final ResourceLocation highlightedTexture;
    private final ResourceLocation disabledTexture;

    public WidgetTextureSet(ResourceLocation defaultTexture, ResourceLocation highlightedTexture, ResourceLocation disabledTexture) {
        this.defaultTexture = defaultTexture;
        this.highlightedTexture = highlightedTexture;
        this.disabledTexture = disabledTexture;
    }

    public WidgetTextureSet(ResourceLocation defaultTexture, ResourceLocation highlightedTexture) {
        this(defaultTexture, highlightedTexture, defaultTexture);
    }

    public ResourceLocation getWidgetTexture(boolean highlighted, boolean active) {
        if (!active) {
            return this.disabledTexture();
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

    public ResourceLocation disabledTexture() {
        return this.disabledTexture;
    }

    public static void switchTitleScreenStyle(Minecraft minecraft) {
        ThreeStyles menuStyle = MellowConfigs.CLIENT_CONFIGS.mainMenuStyle.get();
        MellowConfigs.CLIENT_CONFIGS.mainMenuStyle.set(ThreeStyles.byId(menuStyle.getId() + 1));
        switch (menuStyle) {
            case OPTION_3: minecraft.setScreen(new MellomedleyTitleScreen());
            case OPTION_1: case OPTION_2: default: minecraft.setScreen(new MainMenuScreen());
        }
    }
}
