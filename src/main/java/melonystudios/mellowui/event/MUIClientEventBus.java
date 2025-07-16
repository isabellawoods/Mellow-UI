package melonystudios.mellowui.event;

import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.util.GUITextures;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.resource.PathResourcePack;

import java.io.IOException;
import java.nio.file.Path;

@Mod.EventBusSubscriber(modid = MellowUI.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MUIClientEventBus {
    // Copied from teamtwilight/twilightforest.
    @SubscribeEvent
    public static void addHighContrastPack(AddPackFindersEvent event) {
        try {
            if (event.getPackType() == PackType.CLIENT_RESOURCES) {
                Path folderPath = ModList.get().getModFileById(MellowUI.MOD_ID).getFile().findResource("assets/mellowui/resourcepacks/high_contrast");
                PackResources pack = new PathResourcePack(GUITextures.MUI_HIGH_CONTRAST.toString(), folderPath);
                var metadataSection = pack.getMetadataSection(PackMetadataSection.SERIALIZER);
                if (metadataSection != null) {
                    event.addRepositorySource((packConsumer, constructor) -> packConsumer.accept(constructor.create(
                            GUITextures.MUI_HIGH_CONTRAST.toString(), new TranslatableComponent("resource_pack.mellowui.high_contrast"), false, () -> pack,
                            metadataSection, Pack.Position.TOP, PackSource.BUILT_IN, false)));
                }
            }
        } catch (IOException exception) {
            throw new RuntimeException("[MUI] Failed to add High Contrast resource pack", exception);
        }
    }
}
