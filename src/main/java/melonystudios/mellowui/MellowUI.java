package melonystudios.mellowui;

import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.config.WidgetConfigs;
import melonystudios.mellowui.screen.MellowUIOptionsScreen;
import melonystudios.mellowui.sound.MUISounds;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.ConfigGuiHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(MellowUI.MOD_ID)
public class MellowUI {
    public static final Logger LOGGER = LogManager.getLogger(MellowUI.MOD_ID);
    public static final String MOD_NAME = "Mellow UI";
    public static final String MOD_ID = "mellowui";

    public MellowUI() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext context = ModLoadingContext.get();
        eventBus.addListener(this::commonSetup);
        eventBus.addListener(this::clientSetup);

        MUISounds.SOUNDS.register(eventBus);
        MinecraftForge.EVENT_BUS.register(this);

        context.registerConfig(ModConfig.Type.CLIENT, WidgetConfigs.WIDGET_SPEC, "melonystudios/mellowui-widgets.toml");
        context.registerConfig(ModConfig.Type.CLIENT, MellowConfigs.CLIENT_SPEC, "melonystudios/mellowui-client.toml");
        context.registerExtensionPoint(ConfigGuiHandler.ConfigGuiFactory.class, () -> new ConfigGuiHandler.ConfigGuiFactory((minecraft, lastScreen) -> new MellowUIOptionsScreen(lastScreen, minecraft.options)));
        context.registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (remoteVersion, network) -> true));
    }

    /// Creates a new resource location under ***Mellow UI***'s namespace.
    /// @param name The path of this resource location.
    public static ResourceLocation mellowUI(String name) {
        return new ResourceLocation(MellowUI.MOD_ID, name);
    }

    /// Creates a new resource location under the ***Generated*** namespace.
    /// @param name The path of this resource location.
    public static ResourceLocation generated(String name) {
        return new ResourceLocation("generated", name);
    }

    /// Creates a new resource location under ***Mellow UI***'s namespace.
    /// @param name The path of this resource location.
    /// @return A new resource location, being prefixed with `textures/gui/` and its extension being `.png`.
    public static ResourceLocation gui(String name) {
        return mellowUI("textures/gui/" + name + ".png");
    }

    /// Transforms the provided resource location into a texture path.
    /// @param location The resource location.
    /// @return A new location, with the `textures/` prefix and `.png` suffix added.
    public static ResourceLocation toTexturePath(ResourceLocation location) {
        return new ResourceLocation(location.getNamespace(), (location.getPath().startsWith("textures/") ? "" : "textures/") + location.getPath() + (location.getPath().endsWith(".png") ? "" : ".png"));
    }

    private void commonSetup(final FMLCommonSetupEvent event) {}

    private void clientSetup(final FMLClientSetupEvent event) {}
}
