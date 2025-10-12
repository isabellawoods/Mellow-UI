package melonystudios.mellowui.config;

import melonystudios.mellowui.config.option.EditConfigOption;
import melonystudios.mellowui.config.option.IterableOption;
import melonystudios.mellowui.config.type.TwoStyles;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;

import static melonystudios.mellowui.config.MellowConfigs.CLIENT_CONFIGS;

public class MellomedleyConfigEntries {
    // Tooltips
    public static final MutableComponent MELLOMEDLEY_MAIN_MENU_ICON_TOOLTIP = new TranslatableComponent("config.mellomedley.main_menu_mod_button.tooltip", new TranslatableComponent("config.mellomedley.main_menu_mod_button.option_1.tooltip"));
    public static final MutableComponent BELOW_OPTIONS_TOOLTIP = new TranslatableComponent("config.mellomedley.main_menu_mod_button.tooltip", new TranslatableComponent("config.mellomedley.main_menu_mod_button.option_2.tooltip"));

    // Options
    public static final IterableOption MAIN_MENU_MOD_BUTTON = new IterableOption("config.mellomedley.main_menu_mod_button",
            (options, identifier) -> CLIENT_CONFIGS.mellomedleyMainMenuModButton.set(TwoStyles.byId(CLIENT_CONFIGS.mellomedleyMainMenuModButton.get().getId() + identifier)),
            (options, option) -> {
                switch (CLIENT_CONFIGS.mellomedleyMainMenuModButton.get()) {
                    case OPTION_1:
                        option.setTooltip(MELLOMEDLEY_MAIN_MENU_ICON_TOOLTIP);
                        break;
                    case OPTION_2:
                        option.setTooltip(BELOW_OPTIONS_TOOLTIP);
                }
                return new TranslatableComponent("config.mellomedley.main_menu_mod_button", new TranslatableComponent("config.mellomedley.main_menu_mod_button." + CLIENT_CONFIGS.mellomedleyMainMenuModButton.get().toString()));
            });
    public static final EditConfigOption MELLOMEDLEY_VERSION = new EditConfigOption("config.mellomedley.mellomedley_version",
            new TranslatableComponent("config.mellomedley.mellomedley_version.tooltip"), CLIENT_CONFIGS.mellomedleyVersion);
}
