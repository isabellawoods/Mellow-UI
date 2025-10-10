package melonystudios.mellowui.config;

import melonystudios.mellowui.config.option.EditConfigOption;
import melonystudios.mellowui.config.type.TwoStyles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.IteratableOption;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import static melonystudios.mellowui.config.MellowConfigs.CLIENT_CONFIGS;
import static melonystudios.mellowui.screen.RenderComponents.TOOLTIP_MAX_WIDTH;

public class MellomedleyConfigEntries {
    // Tooltips
    public static final IFormattableTextComponent MELLOMEDLEY_MAIN_MENU_ICON_TOOLTIP = new TranslationTextComponent("config.mellomedley.main_menu_mod_button.tooltip", new TranslationTextComponent("config.mellomedley.main_menu_mod_button.option_1.tooltip"));
    public static final IFormattableTextComponent BELOW_OPTIONS_TOOLTIP = new TranslationTextComponent("config.mellomedley.main_menu_mod_button.tooltip", new TranslationTextComponent("config.mellomedley.main_menu_mod_button.option_2.tooltip"));

    // Options
    public static final IteratableOption MAIN_MENU_MOD_BUTTON = new IteratableOption("config.mellomedley.main_menu_mod_button",
            (options, identifier) -> CLIENT_CONFIGS.mellomedleyMainMenuModButton.set(TwoStyles.byId(CLIENT_CONFIGS.mellomedleyMainMenuModButton.get().getId() + identifier)),
            (options, option) -> {
                switch (CLIENT_CONFIGS.mellomedleyMainMenuModButton.get()) {
                    case OPTION_1:
                        option.setTooltip(Minecraft.getInstance().font.split(MELLOMEDLEY_MAIN_MENU_ICON_TOOLTIP, TOOLTIP_MAX_WIDTH));
                        break;
                    case OPTION_2:
                        option.setTooltip(Minecraft.getInstance().font.split(BELOW_OPTIONS_TOOLTIP, TOOLTIP_MAX_WIDTH));
                }
                return new TranslationTextComponent("config.mellomedley.main_menu_mod_button", new TranslationTextComponent("config.mellomedley.main_menu_mod_button." + CLIENT_CONFIGS.mellomedleyMainMenuModButton.get().toString()));
            });
    public static final EditConfigOption MELLOMEDLEY_VERSION = new EditConfigOption("config.mellomedley.mellomedley_version",
            new TranslationTextComponent("config.mellomedley.mellomedley_version.tooltip"), CLIENT_CONFIGS.mellomedleyVersion);
}
