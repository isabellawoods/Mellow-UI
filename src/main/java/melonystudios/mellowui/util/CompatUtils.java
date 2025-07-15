package melonystudios.mellowui.util;

import net.minecraft.block.Block;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.registries.ObjectHolder;

public class CompatUtils {
    // Blocks
    @ObjectHolder("blue_skies:everbright_portal")
    public static final Block EVERBRIGHT_PORTAL = null;
    @ObjectHolder("blue_skies:everdawn_portal")
    public static final Block EVERDAWN_PORTAL = null;

    // Dimensions
    public static final RegistryKey<World> THE_ALJAN = RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation("backmath", "the_aljan"));
    public static final RegistryKey<World> EVERBRIGHT = RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation("blue_skies", "everbright"));
    public static final RegistryKey<World> EVERDAWN = RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation("blue_skies", "everdawn"));
    public static final RegistryKey<World> TWILIGHT_FOREST = RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation("twilightforest", "twilightforest"));
}
