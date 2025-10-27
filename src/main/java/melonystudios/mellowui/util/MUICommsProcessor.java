package melonystudios.mellowui.util;

import com.google.common.collect.Lists;
import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.config.option.EditColorConfigOption;
import melonystudios.mellowui.config.option.EditConfigOption;
import net.minecraft.client.AbstractOption;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;

import java.util.List;

/// *Mellow UI*'s {@linkplain InterModComms **Inter-Mod Communications**} message handler. These can be one of the following:
/// - {@link #addToColorList(String) `mellowui:add_to_color_list/<translation>`}: Adds any *Forge* config to *Mellow UI*'s color options screen.
@Mod.EventBusSubscriber(modid = MellowUI.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MUICommsProcessor {
    /// Adds any *Forge* config to *Mellow UI*'s {@linkplain melonystudios.mellowui.screen.ColorOptionsScreen color options screen}.
    /// @apiNote Use the {@link #addToColorList(String)} method.
    public static final String ADD_TO_COLOR_LIST = MellowUI.mellowUI("add_to_color_list").toString();
    public static final List<AbstractOption> ENTRIES = Lists.newArrayList();

    @SubscribeEvent
    public static void receiveIMCMessages(InterModProcessEvent event) {
        InterModComms.getMessages(MellowUI.MOD_ID).forEach(MUICommsProcessor::processMessage);
    }

    /// Processes a message send by another mod.
    /// @param message The message being sent.
    public static void processMessage(InterModComms.IMCMessage message) {
        String method = message.getMethod();
        if (method.contains(ADD_TO_COLOR_LIST) && message.getMessageSupplier().get() instanceof ForgeConfigSpec.ConfigValue) {
            ForgeConfigSpec.ConfigValue<?> config = (ForgeConfigSpec.ConfigValue<?>) message.getMessageSupplier().get();
            String translationKey = method.substring(method.indexOf('/') + 1);
            if (config instanceof ForgeConfigSpec.IntValue) {
                EditColorConfigOption option = new EditColorConfigOption(translationKey, new TranslationTextComponent(translationKey + ".tooltip"), config);
                ENTRIES.add(option);
            } else {
                EditConfigOption option = new EditConfigOption(translationKey, new TranslationTextComponent(translationKey + ".tooltip"), config);
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
