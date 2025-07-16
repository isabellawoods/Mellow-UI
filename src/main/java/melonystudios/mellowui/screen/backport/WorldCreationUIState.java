package melonystudios.mellowui.screen.backport;

import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.methods.InterfaceMethods;
import net.minecraft.FileUtil;
import net.minecraft.Util;
import net.minecraft.client.gui.screens.worldselection.WorldPreset;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.WorldStem;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public class WorldCreationUIState {
    public static final Component DEFAULT_WORLD_NAME = new TranslatableComponent("selectWorld.newWorld");
    private final List<Consumer<WorldCreationUIState>> listeners = new ArrayList<>();
    private Optional<WorldPreset> preset;
    private String name = DEFAULT_WORLD_NAME.getString();
    private SelectedGameMode gameMode = SelectedGameMode.SURVIVAL;
    private Difficulty difficulty = Difficulty.NORMAL;
    @Nullable
    private Boolean allowCommands;
    private String seed;
    private boolean generateStructures;
    private boolean bonusChest;
    private boolean hardcore;
    private final Path savesFolder;
    private String targetFolder;
    private LevelSettings worldSettings;
    private WorldGenSettings settings;
    private WorldTypeEntry worldType;
    private RegistryAccess.Frozen registryHolder;
    private final List<WorldTypeEntry> normalPresetList = new ArrayList<>();
    private final List<WorldTypeEntry> alternatePresetList = new ArrayList<>();
    private GameRules gameRules = new GameRules();

    public WorldCreationUIState(Path savesFolder, LevelSettings worldSettings, WorldGenSettings settings, RegistryAccess.Frozen registryHolder, Optional<WorldPreset> preset, OptionalLong seed) {
        this.savesFolder = savesFolder;
        this.worldSettings = worldSettings;
        this.settings = settings;
        this.registryHolder = registryHolder;
        this.worldType = new WorldTypeEntry(preset.orElse(null));
        this.updatePresetLists();
        this.seed = seed.isPresent() ? Long.toString(seed.getAsLong()) : "";
        this.generateStructures = settings.generateFeatures();
        this.bonusChest = settings.generateBonusChest();
        this.preset = preset;
        this.targetFolder = this.findResultFolder(this.name);
    }

    public void addListener(Consumer<WorldCreationUIState> listener) {
        this.listeners.add(listener);
    }

    public void onChanged() {
        boolean hasBonusChest = this.hasBonusChest();
        if (hasBonusChest != this.settings.generateBonusChest()) {
            this.settings = this.settings.withBonusChestToggled();
        }

        boolean generateStructures = this.generatesStructures();
        if (generateStructures != this.settings.generateFeatures()) {
            this.settings = this.settings.withFeaturesToggled();
        }

        boolean hardcore = this.isHardcore();
        if (hardcore != this.worldSettings.hardcore()) {
            this.worldSettings = new LevelSettings(this.worldSettings.levelName(), this.worldSettings.gameType(), hardcore, this.worldSettings.difficulty(), this.worldSettings.allowCommands(), this.worldSettings.gameRules(), this.getSettings().getDataPackConfig());
        }

        for (Consumer<WorldCreationUIState> consumer : this.listeners) {
            consumer.accept(this);
        }
    }

    public void setName(String name) {
        this.name = name;
        this.targetFolder = this.findResultFolder(name);
        this.onChanged();
    }

    private String findResultFolder(String name) {
        String trimmedName = name.trim();

        try {
            return FileUtil.findAvailableName(this.savesFolder, !trimmedName.isEmpty() ? trimmedName : DEFAULT_WORLD_NAME.getString(), "");
        } catch (Exception exception) {
            try {
                return FileUtil.findAvailableName(this.savesFolder, "World", "");
            } catch (IOException ioException) {
                throw new RuntimeException("Could not create save folder", ioException);
            }
        }
    }

    public String getName() {
        return this.name;
    }

    public String getTargetFolder() {
        return this.targetFolder;
    }

    public void setGameMode(SelectedGameMode gameMode) {
        this.gameMode = gameMode;
        this.onChanged();
    }

    public SelectedGameMode getGameMode() {
        return this.isDebug() ? SelectedGameMode.SPECTATOR : this.gameMode;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
        this.onChanged();
    }

    public Difficulty getDifficulty() {
        return this.isHardcore() ? Difficulty.HARD : this.difficulty;
    }

    public boolean isHardcore() {
        return this.hardcore;
    }

    public void setHardcore(boolean hardcore) {
        this.hardcore = hardcore;
        this.onChanged();
    }

    public void setAllowCommands(boolean allowCommands) {
        this.allowCommands = allowCommands;
        this.onChanged();
    }

    public boolean allowsCommands() {
        if (this.isDebug()) {
            this.allowCommands = true;
            return true;
        } else if (this.isHardcore()) {
            this.allowCommands = false;
            return false;
        } else {
            return this.allowCommands == null ? this.getGameMode() == SelectedGameMode.CREATIVE : this.allowCommands;
        }
    }

    public void setSeed(String seed) {
        this.seed = seed;
        this.settings = this.settings.withSeed(this.isHardcore(), this.parseSeed(this.getSeed()));
        this.onChanged();
    }

    private OptionalLong parseSeed(String seed) {
        OptionalLong actualSeed;
        if (StringUtils.isEmpty(seed)) {
            actualSeed = OptionalLong.empty();
        } else {
            OptionalLong parsedSeed = parseLong(seed);
            if (parsedSeed.isPresent() && parsedSeed.getAsLong() != 0L) {
                actualSeed = parsedSeed;
            } else {
                actualSeed = OptionalLong.of(seed.hashCode());
            }
        }

        return actualSeed;
    }

    private static OptionalLong parseLong(String seed) {
        try {
            return OptionalLong.of(Long.parseLong(seed));
        } catch (NumberFormatException exception) {
            return OptionalLong.empty();
        }
    }

    public String getSeed() {
        return this.seed;
    }

    public void setGenerateStructures(boolean generateStructures) {
        this.generateStructures = generateStructures;
        this.onChanged();
    }

    public boolean generatesStructures() {
        return !this.isDebug() && this.generateStructures;
    }

    public void setBonusChest(boolean bonusChest) {
        this.bonusChest = bonusChest;
        this.onChanged();
    }

    public boolean hasBonusChest() {
        return !this.isDebug() && !this.isHardcore() && this.bonusChest;
    }

    public void setSettings(LevelSettings settings) {
        this.worldSettings = settings;
        this.updatePresetLists();
        this.onChanged();
    }

    public LevelSettings getSettings() {
        return this.worldSettings;
    }

    public WorldGenSettings getGeneratorSettings() {
        return this.settings;
    }

    public RegistryAccess.Frozen registryHolder() {
        return this.registryHolder;
    }

    public WorldGenSettings makeSettings(boolean hardcore) {
        OptionalLong seed1 = this.parseSeed(this.seed);
        return this.settings.withSeed(hardcore, seed1);
    }

    protected void tryUpdateDataConfiguration(WorldStem stem) {
        RegistryAccess.Frozen registryHolder = RegistryAccess.BUILTIN.get();
        DynamicOps<JsonElement> settingsExport = RegistryOps.create(JsonOps.INSTANCE, this.registryHolder);
        DynamicOps<JsonElement> settingsImport = RegistryOps.createAndLoad(JsonOps.INSTANCE, RegistryAccess.builtinCopy(), stem.resourceManager());
        DataResult<WorldGenSettings> result = WorldGenSettings.CODEC.encodeStart(settingsExport, this.settings).flatMap(
                element -> WorldGenSettings.CODEC.parse(settingsImport, element));

        result.resultOrPartial(Util.prefix("Error parsing world generation settings after loading data packs: ", MellowUI.LOGGER::error)).ifPresent(settings -> {
            this.settings = settings;
            this.registryHolder = registryHolder;
        });
    }

    public boolean isDebug() {
        return this.settings.isDebug() || this.gameMode == SelectedGameMode.SPECTATOR;
    }

    public void setWorldType(WorldTypeEntry preset) {
        this.worldType = preset;
        this.preset = Optional.ofNullable(preset.preset);
        if (preset.preset != null) {
            this.settings = preset.preset.create(this.registryHolder, this.settings.seed(), this.settings.generateFeatures(), this.settings.generateBonusChest());
        }
    }

    public WorldTypeEntry getWorldType() {
        return this.worldType;
    }

    @Nullable
    public WorldPreset.PresetEditor getPresetEditor() {
        if (!this.preset.isPresent()) return null;
        WorldPreset.PresetEditor editor = ((InterfaceMethods.WorldPresetsMethods) this.preset.get()).getEditors().get(this.preset);
        return ForgeHooksClient.getPresetEditor(this.preset, editor);
    }

    public List<WorldTypeEntry> getNormalPresetList() {
        return this.normalPresetList;
    }

    public List<WorldTypeEntry> getAlternatePresetList() {
        return this.alternatePresetList;
    }

    private void updatePresetLists() {
        Optional<WorldPreset> optionalPreset = this.preset;
        if (optionalPreset != null && optionalPreset.isPresent()) {
            List<WorldPreset> presets = ((InterfaceMethods.WorldPresetsMethods) optionalPreset.get()).getPresets();
            this.normalPresetList.clear();
            presets.stream().filter(preset -> !((TranslatableComponent) preset.description()).getKey().equals("generator.debug_all_block_states")).forEach(
                    preset -> this.normalPresetList.add(new WorldTypeEntry(preset)));
            this.alternatePresetList.clear();
            presets.forEach(preset -> this.alternatePresetList.add(new WorldTypeEntry(preset)));
        }
    }

    public void setGameRules(GameRules gameRules) {
        this.gameRules = gameRules;
        this.onChanged();
    }

    public GameRules getGameRules() {
        return this.gameRules;
    }

    @OnlyIn(Dist.CLIENT)
    public enum SelectedGameMode {
        SURVIVAL("survival", GameType.SURVIVAL),
        CREATIVE("creative", GameType.CREATIVE),
        ADVENTURE("adventure", GameType.ADVENTURE),
        SPECTATOR("spectator", GameType.SPECTATOR);

        private final String name;
        private final GameType gameType;

        SelectedGameMode(String name, GameType type) {
            this.name = name;
            this.gameType = type;
        }

        public GameType gameType() {
            return this.gameType;
        }

        public TranslatableComponent displayName() {
            return new TranslatableComponent("selectWorld.gameMode." + this.name);
        }

        public MutableComponent getInfo() {
            MutableComponent component = new TranslatableComponent(this.displayName().getKey() + ".line1");
            component.append(" ");
            component.append(new TranslatableComponent(this.displayName().getKey() + ".line2"));
            return component;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public record WorldTypeEntry(@Nullable WorldPreset preset) {
        public static final Component CUSTOM_WORLD_DESCRIPTION = new TranslatableComponent("generator.custom");

        public Component describePreset() {
            return Optional.ofNullable(this.preset).map(WorldPreset::description).orElse(CUSTOM_WORLD_DESCRIPTION);
        }

        public boolean isAmplified() {
                return this.preset == WorldPreset.AMPLIFIED;
            }
    }
}
