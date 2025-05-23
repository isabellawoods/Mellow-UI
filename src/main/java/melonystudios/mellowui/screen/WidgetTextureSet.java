package melonystudios.mellowui.screen;

import melonystudios.mellowui.config.MellowConfigEntries;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.screen.updated.MellomedleyMainMenuScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.settings.IteratableOption;
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
