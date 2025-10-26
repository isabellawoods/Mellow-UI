package melonystudios.mellowui.util;

import com.google.common.collect.Lists;
import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.config.option.EditColorConfigOption;
import melonystudios.mellowui.config.option.EditConfigOption;
import net.minecraft.client.Option;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.InterModComms;

import java.util.List;

/// *Mellow UI*'s {@linkplain InterModComms **Inter-Mod Communications**} message handler. These can be one of the following:
/// - {@link #addToColorList(String) `mellowui:add_to_color_list/<translation>`}: Adds any *Forge* config to *Mellow UI*'s color options screen.
public class MUICommsProcessor {
    /// Adds any *Forge* config to *Mellow UI*'s {@linkplain melonystudios.mellowui.screen.ColorOptionsScreen color options screen}.
    /// @apiNote Use the {@link #addToColorList(String)} method.
    public static final String ADD_TO_COLOR_LIST = MellowUI.mellowUI("add_to_color_list").toString();
    public static final List<Option> ENTRIES = Lists.newArrayList();

    /// Processes a message send by another mod.
    /// @param message The message being sent.
    public static void processMessage(InterModComms.IMCMessage message) {
        String method = message.method();
        if (method.contains(ADD_TO_COLOR_LIST) && message.messageSupplier().get() instanceof ForgeConfigSpec.ConfigValue<?> config) {
            String translationKey = method.substring(method.indexOf('/') + 1);
            if (config instanceof ForgeConfigSpec.IntValue) {
                EditColorConfigOption option = new EditColorConfigOption(translationKey, new TranslatableComponent(translationKey + ".tooltip"), config);
                ENTRIES.add(option);
            } else {
                EditConfigOption option = new EditConfigOption(translationKey, new TranslatableComponent(translationKey + ".tooltip"), config);
                ENTRIES.add(option);
            }
        }
    }

    /// Adds any *Forge* config to *Mellow UI*'s {@linkplain melonystudios.mellowui.screen.ColorOptionsScreen color options screen}.
    /// @param translation The base translation key for this config, adding `.tooltip` for the description.
    public static String addToColorList(String translation) {
        return ADD_TO_COLOR_LIST + "/" + translation;
    }
}
