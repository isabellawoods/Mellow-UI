package melonystudios.mellowui.screen.backport;

import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.methods.InterfaceMethods;
import net.minecraft.client.gui.screen.BiomeGeneratorTypeScreens;
import net.minecraft.resources.DataPackRegistries;
import net.minecraft.util.FileUtil;
import net.minecraft.util.Util;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.WorldGenSettingsExport;
import net.minecraft.util.registry.WorldSettingsImport;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;

import static net.minecraft.client.gui.screen.BiomeGeneratorTypeScreens.*;

@OnlyIn(Dist.CLIENT)
public class WorldCreationUIState {
    public static final ITextComponent DEFAULT_WORLD_NAME = new TranslationTextComponent("selectWorld.newWorld");
    private final List<Consumer<WorldCreationUIState>> listeners = new ArrayList<>();
    private Optional<BiomeGeneratorTypeScreens> preset;
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
    private WorldSettings worldSettings;
    private DimensionGeneratorSettings settings;
    private WorldTypeEntry worldType;
    private DynamicRegistries.Impl registryHolder;
    private final List<WorldTypeEntry> normalPresetList = new ArrayList<>();
    private final List<WorldTypeEntry> alternatePresetList = new ArrayList<>();
    private GameRules gameRules = new GameRules();

    public WorldCreationUIState(Path savesFolder, WorldSettings worldSettings, DimensionGeneratorSettings settings, DynamicRegistries.Impl registryHolder, Optional<BiomeGeneratorTypeScreens> preset, OptionalLong seed) {
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
            this.worldSettings = new WorldSettings(this.worldSettings.levelName(), this.worldSettings.gameType(), hardcore, this.worldSettings.difficulty(), this.worldSettings.allowCommands(), this.worldSettings.gameRules(), this.getSettings().getDataPackConfig());
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

    public void setSettings(WorldSettings settings) {
        this.worldSettings = settings;
        this.updatePresetLists();
        this.onChanged();
    }

    public WorldSettings getSettings() {
        return this.worldSettings;
    }

    public DimensionGeneratorSettings getGeneratorSettings() {
        return this.settings;
    }

    public DynamicRegistries.Impl getRegistryHolder() {
        return this.registryHolder;
    }

    protected void tryUpdateDataConfiguration(DataPackRegistries registries) {
        DynamicRegistries.Impl registryHolder = DynamicRegistries.builtin();
        WorldGenSettingsExport<JsonElement> settingsExport = WorldGenSettingsExport.create(JsonOps.INSTANCE, this.registryHolder);
        WorldSettingsImport<JsonElement> settingsImport = WorldSettingsImport.create(JsonOps.INSTANCE, registries.getResourceManager(), registryHolder);
        DataResult<DimensionGeneratorSettings> result = DimensionGeneratorSettings.CODEC.encodeStart(settingsExport, this.settings).flatMap(
                element -> DimensionGeneratorSettings.CODEC.parse(settingsImport, element));

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
    public BiomeGeneratorTypeScreens.IFactory getPresetEditor() {
        if (!this.preset.isPresent()) return null;
        IFactory editor = ((InterfaceMethods.WorldPresetsMethods) this.preset.get()).getEditors().get(this.preset);
        return ForgeHooksClient.getBiomeGeneratorTypeScreenFactory(this.preset, editor);
    }

    public List<WorldTypeEntry> getNormalPresetList() {
        return this.normalPresetList;
    }

    public List<WorldTypeEntry> getAlternatePresetList() {
        return this.alternatePresetList;
    }

    private void updatePresetLists() {
        Optional<BiomeGeneratorTypeScreens> optionalPreset = this.preset;
        if (optionalPreset != null && optionalPreset.isPresent()) {
            List<BiomeGeneratorTypeScreens> presets = ((InterfaceMethods.WorldPresetsMethods) optionalPreset.get()).getPresets();
            this.normalPresetList.clear();
            presets.stream().filter(preset -> !((TranslationTextComponent) preset.description()).getKey().equals("generator.debug_all_block_states")).forEach(
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

        public TranslationTextComponent displayName() {
            return new TranslationTextComponent("selectWorld.gameMode." + this.name);
        }

        public IFormattableTextComponent getInfo() {
            IFormattableTextComponent component = new TranslationTextComponent(this.displayName().getKey() + ".line1");
            component.append(" ");
            component.append(new TranslationTextComponent(this.displayName().getKey() + ".line2"));
            return component;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class WorldTypeEntry {
        public static final ITextComponent CUSTOM_WORLD_DESCRIPTION = new TranslationTextComponent("generator.custom");
        @Nullable
        private final BiomeGeneratorTypeScreens preset;

        public WorldTypeEntry(@Nullable BiomeGeneratorTypeScreens preset) {
            this.preset = preset;
        }

        @Nullable
        public BiomeGeneratorTypeScreens preset() {
            return this.preset;
        }

        public ITextComponent describePreset() {
            return Optional.ofNullable(this.preset).map(BiomeGeneratorTypeScreens::description).orElse(CUSTOM_WORLD_DESCRIPTION);
        }

        public boolean isAmplified() {
            return this.preset == AMPLIFIED;
        }
    }
}
