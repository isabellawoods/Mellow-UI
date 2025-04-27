package melonystudios.mellowui.util;

import com.mojang.datafixers.util.Pair;
import melonystudios.mellowui.screen.updated.MUILoadingTerrainScreen;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;

public class DimensionTransitionScreenManager {
    private static final Map<Pair<RegistryKey<World>, RegistryKey<World>>, LoadingTerrainScreenFactory> CONDITIONAL_DIMENSION_EFFECTS = new HashMap<>();
    private static final Map<RegistryKey<World>, LoadingTerrainScreenFactory> TO_DIMENSION_TRANSITIONS = new HashMap<>();
    private static final Map<RegistryKey<World>, LoadingTerrainScreenFactory> FROM_DIMENSION_TRANSITIONS = new HashMap<>();

    public static LoadingTerrainScreenFactory getScreenFromWorld(@Nullable World target, @Nullable World source) {
        if (source == null) { // Source level is null on login: transition screen should not appear in this case
            return getScreen(null, null);
        } else if (target == null) { // The target level shouldn't ever be null, but anyone could call Minecraft.setLevel() and pass null in
            return getScreen(null, source.dimension());
        }
        return getScreen(target.dimension(), source.dimension());
    }

    public static LoadingTerrainScreenFactory getScreen(@Nullable RegistryKey<World> toDimension, @Nullable RegistryKey<World> fromDimension) {
        LoadingTerrainScreenFactory conditionalScreen = CONDITIONAL_DIMENSION_EFFECTS.get(Pair.of(toDimension, fromDimension));
        if (conditionalScreen != null) return conditionalScreen;

        LoadingTerrainScreenFactory toDim = TO_DIMENSION_TRANSITIONS.get(toDimension);
        if (toDim != null) return toDim;

        LoadingTerrainScreenFactory fromDim = FROM_DIMENSION_TRANSITIONS.get(fromDimension);
        if (fromDim != null) return fromDim;
        return MUILoadingTerrainScreen::new;
    }

    public interface LoadingTerrainScreenFactory {
        MUILoadingTerrainScreen create(BooleanSupplier worldReceived, MUILoadingTerrainScreen.Reason reason);
    }

    static {
        TO_DIMENSION_TRANSITIONS.put(World.NETHER, (worldReceived, reason) -> new MUILoadingTerrainScreen(worldReceived, MUILoadingTerrainScreen.Reason.NETHER_PORTAL));
        TO_DIMENSION_TRANSITIONS.put(World.END, (worldReceived, reason) -> new MUILoadingTerrainScreen(worldReceived, MUILoadingTerrainScreen.Reason.END_PORTAL));
        TO_DIMENSION_TRANSITIONS.put(CompatUtils.THE_ALJAN, (worldReceived, reason) -> new MUILoadingTerrainScreen(worldReceived, MUILoadingTerrainScreen.Reason.ALJAN_PORTAL_STAND));
        TO_DIMENSION_TRANSITIONS.put(CompatUtils.EVERBRIGHT, (worldReceived, reason) -> new MUILoadingTerrainScreen(worldReceived, MUILoadingTerrainScreen.Reason.EVERBRIGHT_PORTAL));
        TO_DIMENSION_TRANSITIONS.put(CompatUtils.EVERDAWN, (worldReceived, reason) -> new MUILoadingTerrainScreen(worldReceived, MUILoadingTerrainScreen.Reason.EVERDAWN_PORTAL));
        FROM_DIMENSION_TRANSITIONS.put(World.NETHER, (worldReceived, reason) -> new MUILoadingTerrainScreen(worldReceived, MUILoadingTerrainScreen.Reason.NETHER_PORTAL));
        FROM_DIMENSION_TRANSITIONS.put(World.END, (worldReceived, reason) -> new MUILoadingTerrainScreen(worldReceived, MUILoadingTerrainScreen.Reason.END_PORTAL));
        FROM_DIMENSION_TRANSITIONS.put(CompatUtils.THE_ALJAN, (worldReceived, reason) -> new MUILoadingTerrainScreen(worldReceived, MUILoadingTerrainScreen.Reason.ALJAN_PORTAL_STAND));
        FROM_DIMENSION_TRANSITIONS.put(CompatUtils.EVERBRIGHT, (worldReceived, reason) -> new MUILoadingTerrainScreen(worldReceived, MUILoadingTerrainScreen.Reason.EVERBRIGHT_PORTAL));
        FROM_DIMENSION_TRANSITIONS.put(CompatUtils.EVERDAWN, (worldReceived, reason) -> new MUILoadingTerrainScreen(worldReceived, MUILoadingTerrainScreen.Reason.EVERDAWN_PORTAL));
    }
}
