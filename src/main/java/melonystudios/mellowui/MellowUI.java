package melonystudios.mellowui;

import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.config.WidgetConfigs;
import melonystudios.mellowui.resource.flair.FlairReloadListener;
import melonystudios.mellowui.resource.panorama.PanoramaReloadListener;
import melonystudios.mellowui.screen.MellowUIOptionsScreen;
import melonystudios.mellowui.sound.MUISounds;
import melonystudios.mellowui.util.GUITextures;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
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
    public static final String MOD_NAME = "Mellow UI";
    public static final String MOD_ID = "mellowui";

    public MellowUI() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext context = ModLoadingContext.get();
        eventBus.addListener(this::commonSetup);
        eventBus.addListener(this::clientSetup);
        eventBus.addListener(this::registerGUISpriteUploader);

        MUISounds.SOUNDS.register(eventBus);
        MinecraftForge.EVENT_BUS.register(this);

        context.registerConfig(ModConfig.Type.CLIENT, WidgetConfigs.WIDGET_SPEC, "melonystudios/mellowui-widgets.toml");
        context.registerConfig(ModConfig.Type.CLIENT, MellowConfigs.CLIENT_SPEC, "melonystudios/mellowui-client.toml");
        context.registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (minecraft, lastScreen) -> new MellowUIOptionsScreen(lastScreen, minecraft.options));
        context.registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (remoteVersion, network) -> true));

        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> MellowUtils::addHighContrastPack);
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.getResourceManager() instanceof IReloadableResourceManager) {
            IReloadableResourceManager manager = (IReloadableResourceManager) minecraft.getResourceManager();
            manager.registerReloadListener(new FlairReloadListener());
            manager.registerReloadListener(new PanoramaReloadListener());
        }
    }

    /// Gets a logger instance with the `mellowui/<name>` name.
    /// @param name The name of this logger instance.
    public static Logger logger(String name) {
        return LogManager.getLogger(MOD_ID + "/" + name);
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

    private void registerGUISpriteUploader(ColorHandlerEvent.Block event) {
        GUITextures.registerGUITextureManager();
    }
}
