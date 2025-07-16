package melonystudios.mellowui.util;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ObjectHolder;

public class CompatUtils {
    // Blocks
    @ObjectHolder("blue_skies:everbright_portal")
    public static final Block EVERBRIGHT_PORTAL = null;
    @ObjectHolder("blue_skies:everdawn_portal")
    public static final Block EVERDAWN_PORTAL = null;

    // Dimensions
    public static final ResourceKey<Level> THE_ALJAN = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation("backmath", "the_aljan"));
    public static final ResourceKey<Level> EVERBRIGHT = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation("blue_skies", "everbright"));
    public static final ResourceKey<Level> EVERDAWN = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation("blue_skies", "everdawn"));
    public static final ResourceKey<Level> TWILIGHT_FOREST = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation("twilightforest", "twilightforest"));
}
