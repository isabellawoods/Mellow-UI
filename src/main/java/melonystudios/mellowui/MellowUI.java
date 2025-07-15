package melonystudios.mellowui;

import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.config.WidgetConfigs;
import melonystudios.mellowui.screen.MellowUIOptionsScreen;
import melonystudios.mellowui.sound.MUISounds;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(MellowUI.MOD_ID)
public class MellowUI {
    public static final Logger LOGGER = LogManager.getLogger(MellowUI.MOD_ID);
    public static final String MOD_ID = "mellowui";

    public MellowUI() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.addListener(this::commonSetup);
        eventBus.addListener(this::clientSetup);

        MUISounds.SOUNDS.register(eventBus);

        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, WidgetConfigs.WIDGET_SPEC, "melonystudios/mellowui-widgets.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, MellowConfigs.CLIENT_SPEC, "melonystudios/mellowui-client.toml");
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (minecraft, lastScreen) -> new MellowUIOptionsScreen(lastScreen, minecraft.options));
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (remoteVersion, network) -> true));
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> (DistExecutor.SafeRunnable) MellowUtils::addHighContrastPack);
    }

    public static ResourceLocation mellowUI(String name) {
        return new ResourceLocation(MellowUI.MOD_ID, name);
    }

    public static ResourceLocation gui(String name) {
        return mellowUI("textures/gui/" + name + ".png");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {}

    private void clientSetup(final FMLClientSetupEvent event) {}
}
