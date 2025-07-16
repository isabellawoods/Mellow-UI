package melonystudios.mellowui.screen.backport;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.screen.RenderComponents;
import melonystudios.mellowui.screen.tab.GameTab;
import melonystudios.mellowui.screen.tab.MoreTab;
import melonystudios.mellowui.screen.tab.TabContents;
import melonystudios.mellowui.screen.tab.WorldTab;
import melonystudios.mellowui.screen.widget.TabButton;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.GenericDirtMessageScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldPreset;
import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.WorldStem;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.FolderRepositorySource;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.resource.ResourcePackLoader;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class CreateNewWorldScreen extends Screen {
    private final RenderComponents components = RenderComponents.INSTANCE;
    private final Map<TabButton, TabContents> tabs = Maps.newHashMap();
    @Nullable
    private Path tempDataPackDirectory;
    @Nullable
    private PackRepository tempDataPackRepository;
    private boolean recreated;
    protected DataPackConfig dataPacks;
    public TabContents selectedTab;
    @Nullable
    private final Screen lastScreen;
    private final WorldCreationUIState uiState;
    public Button createWorldButton;
    private String identifier = "game";

    public static void openFresh(Minecraft minecraft, @Nullable Screen lastScreen) {
        queueLoadScreen(minecraft, new TranslatableComponent("createWorld.preparing"));
        PackRepository repository = new PackRepository(PackType.SERVER_DATA, new ServerPacksSource());
        ResourcePackLoader.loadResourcePacks(repository, ServerLifecycleHooks::buildPackFinder);
        LevelSettings settings = new LevelSettings(WorldCreationUIState.DEFAULT_WORLD_NAME.getString(), GameType.SURVIVAL, false, Difficulty.NORMAL, false, new GameRules(), DataPackConfig.DEFAULT);
        RegistryAccess.Frozen registries = RegistryAccess.BUILTIN.get();
        WorldGenSettings generatorSettings = ForgeHooksClient.getDefaultWorldPreset().map(type ->
                type.create(registries, new Random().nextLong(), true, false)).orElseGet(() ->
                WorldGenSettings.makeDefault(registries));

        minecraft.setScreen(new CreateNewWorldScreen(minecraft, lastScreen, settings, generatorSettings, DataPackConfig.DEFAULT, registries, ForgeHooksClient.getDefaultWorldPreset(), OptionalLong.empty()));
    }

    public static CreateNewWorldScreen createFromExisting(Minecraft minecraft, @Nullable Screen lastScreen, LevelSettings settings, WorldGenSettings  generatorSettings, DataPackConfig dataPacks, RegistryAccess.Frozen registries, @Nullable Path tempDataPackDirectory) {
        CreateNewWorldScreen worldCreationScreen = new CreateNewWorldScreen(minecraft, lastScreen, settings, generatorSettings, dataPacks, registries, WorldPreset.of(generatorSettings), OptionalLong.of(generatorSettings.seed()));
        worldCreationScreen.recreated = true;
        worldCreationScreen.uiState.setName(settings.levelName());
        worldCreationScreen.uiState.setAllowCommands(settings.allowCommands());
        worldCreationScreen.uiState.setDifficulty(settings.difficulty());
        worldCreationScreen.uiState.getGameRules().assignFrom(settings.gameRules(), null);
        worldCreationScreen.uiState.setHardcore(settings.hardcore());

        switch (settings.gameType()) {
            case SURVIVAL:
                worldCreationScreen.uiState.setGameMode(WorldCreationUIState.SelectedGameMode.SURVIVAL);
                break;
            case CREATIVE:
                worldCreationScreen.uiState.setGameMode(WorldCreationUIState.SelectedGameMode.CREATIVE);
                break;
            case ADVENTURE:
                worldCreationScreen.uiState.setGameMode(WorldCreationUIState.SelectedGameMode.ADVENTURE);
                break;
            case SPECTATOR:
                worldCreationScreen.uiState.setGameMode(WorldCreationUIState.SelectedGameMode.SPECTATOR);
        }

        worldCreationScreen.tempDataPackDirectory = tempDataPackDirectory;
        return worldCreationScreen;
    }

    private CreateNewWorldScreen(Minecraft minecraft, @Nullable Screen lastScreen, LevelSettings settings, WorldGenSettings generatorSettings, DataPackConfig dataPacks, RegistryAccess.Frozen registries, Optional<WorldPreset> preset, OptionalLong seed) {
        super(new TranslatableComponent("selectWorld.create"));
        this.lastScreen = lastScreen;
        this.dataPacks = dataPacks;
        this.uiState = new WorldCreationUIState(minecraft.getLevelSource().getBaseDir(), settings, generatorSettings, registries, preset, seed);
    }

    public WorldCreationUIState uiState() {
        return this.uiState;
    }

    @Override
    @Nonnull
    public <T extends GuiEventListener & Widget & NarratableEntry> T addRenderableWidget(T widget) {
        return super.addRenderableWidget(widget);
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        super.resize(minecraft, width, height);
        this.tabs.keySet().stream().findFirst().ifPresent(tab -> tab.setSelected(true));
    }

    @Override
    public void tick() {
        this.tabs.forEach((tab, contents) -> contents.tick());
    }

    @Override
    protected void init() {
        // Create New World
        this.addRenderableWidget(this.createWorldButton = new Button(this.width / 2 - 155, this.height - 25, 150, 20, new TranslatableComponent("selectWorld.create"),
                button -> this.onCreate()));
        this.createWorldButton.active = !this.uiState.getName().isEmpty();

        // Cancel button
        this.addRenderableWidget(new Button(this.width / 2 + 5, this.height - 25, 150, 20, CommonComponents.GUI_CANCEL,
                button -> this.popScreen()));

        // WIP warning button
        this.addRenderableWidget(new Button(this.width / 2 + 165, this.height - 25, 20, 20, new TranslatableComponent("button.mellowui.work_in_progress").withStyle(
                style -> style.withColor(ChatFormatting.YELLOW).withBold(true)), button -> {
            MellowConfigs.CLIENT_CONFIGS.updateCreateNewWorldMenu.set(false);
            this.minecraft.setScreen(CreateWorldScreen.createFresh(this.lastScreen));
        }, (button, stack, mouseX, mouseY) -> this.components.renderTooltip(this, button, new TranslatableComponent("button.mellowui.work_in_progress.desc").withStyle(ChatFormatting.YELLOW),
                        mouseX, mouseY)));

        this.addTabs();
    }

    private void addTabs() {
        int tabWidth = this.components.threeTabWidth(this.width);

        // Game tab
        GameTab game = new GameTab();
        if (this.selectedTab == null) this.selectedTab = game;
        TabButton gameTab;
        this.tabs.put(gameTab = this.addRenderableWidget(new TabButton(this.width / 2 - tabWidth / 2 - tabWidth, 0, tabWidth, 24, new TranslatableComponent("tab.mellowui.game"), button -> {
            this.tabs.forEach((tab, contents) -> tab.setSelected(false));
            this.setSelectedTab(game.identifier);
        })), game);
        game.openTab(this.identifier, this, gameTab);

        // World tab
        WorldTab world = new WorldTab();
        TabButton worldTab;
        this.tabs.put(worldTab = this.addRenderableWidget(new TabButton(this.width / 2 - tabWidth / 2, 0, tabWidth, 24, new TranslatableComponent("tab.mellowui.world"), button -> {
            this.tabs.forEach((tab, contents) -> tab.setSelected(false));
            this.setSelectedTab(world.identifier);
        })), world);
        world.openTab(this.identifier, this, worldTab);

        // More tab
        MoreTab more = new MoreTab();
        TabButton moreTab;
        this.tabs.put(moreTab = this.addRenderableWidget(new TabButton(this.width / 2 + tabWidth / 2, 0, tabWidth, 24, new TranslatableComponent("tab.mellowui.more"), button -> {
            this.tabs.forEach((tab, contents) -> tab.setSelected(false));
            this.setSelectedTab(more.identifier);
        })), more);
        more.openTab(this.identifier, this, moreTab);

        // MellowUI.LOGGER.debug("all children: {}", this.children);
    }

    private void setSelectedTab(String identifier) {
        if (!this.identifier.equals(identifier)) {
            if (this.selectedTab != null) this.selectedTab.widgets.clear();
            this.tabs.clear();
            this.clearWidgets();
            this.identifier = identifier;
            this.init();
        }
    }

    private static void queueLoadScreen(Minecraft minecraft, Component title) {
        minecraft.forceSetScreen(new GenericDirtMessageScreen(title));
    }

    private void onCreate() {
        queueLoadScreen(this.minecraft, new TranslatableComponent("createWorld.preparing"));
        if (this.copyTempDataPackDirectoryToNewWorld()) {
            this.cleanupTempResources();
            WorldGenSettings generatorSettings = this.uiState.getGeneratorSettings();
            LevelSettings settings = this.createWorldSettings(this.uiState.isDebug());

            this.minecraft.createLevel(this.uiState.getTargetFolder(), settings, this.uiState.registryHolder(), generatorSettings);
        }
    }

    private LevelSettings createWorldSettings(boolean debug) {
        String worldName = this.uiState.getName().trim();
        if (debug) {
            GameRules gameRules = new GameRules();
            gameRules.getRule(GameRules.RULE_DAYLIGHT).set(false, null);
            return new LevelSettings(worldName, GameType.SPECTATOR, this.uiState.isHardcore(), Difficulty.PEACEFUL, this.uiState.allowsCommands(), gameRules, this.uiState.getSettings().getDataPackConfig());
        } else {
            return new LevelSettings(worldName, this.uiState.getGameMode().gameType(), this.uiState.isHardcore(), this.uiState.getDifficulty(), this.uiState.allowsCommands(), this.uiState.getGameRules(), this.uiState.getSettings().getDataPackConfig());
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        } else if (keyCode != GLFW.GLFW_KEY_ENTER && keyCode != GLFW.GLFW_KEY_KP_ENTER) {
            return false;
        } else {
            this.onCreate();
            return true;
        }
    }

    @Override
    public void onClose() {
        this.popScreen();
    }

    private void popScreen() {
        this.minecraft.setScreen(this.lastScreen);
        this.removeTempDataPackDirectory();
    }

    private void cleanupTempResources() {
        if (this.tempDataPackRepository != null) {
            this.tempDataPackRepository.close();
        }

        this.removeTempDataPackDirectory();
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        this.components.renderTabHeaderBackground(0, 0, this.width, 24);
        this.components.renderListSeparators(this.width, 0, this.height - 32, 22, 3, this.components.threeTabWidth(this.width));
        super.render(stack, mouseX, mouseY, partialTicks);
        if (this.selectedTab != null) this.selectedTab.render(stack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void renderDirtBackground(int vOffset) {
        if (MellowConfigs.CLIENT_CONFIGS.updateScreenBackground.get()) this.components.renderMenuBackground(0, 24, this.width,  this.height, vOffset);
        else super.renderDirtBackground(vOffset);
    }

    @Nullable
    private Pair<File, PackRepository> getDataPackSelectionSettings() {
        Path packsFolder = this.getTempDataPackDirectory();
        if (packsFolder != null) {
            File file1 = packsFolder.toFile();
            if (this.tempDataPackRepository == null) {
                this.tempDataPackRepository = new PackRepository(PackType.SERVER_DATA, new ServerPacksSource(), new FolderRepositorySource(file1, PackSource.DEFAULT));
                ResourcePackLoader.loadResourcePacks(this.tempDataPackRepository, ServerLifecycleHooks::buildPackFinder);
                this.tempDataPackRepository.reload();
            }

            this.tempDataPackRepository.setSelected(this.dataPacks.getEnabled());
            return Pair.of(file1, this.tempDataPackRepository);
        } else {
            return null;
        }
    }

    public void openDataPacksSelectionScreen() {
        Pair<File, PackRepository> pair = this.getDataPackSelectionSettings();
        if (pair != null) {
            this.minecraft.setScreen(new PackSelectionScreen(this, pair.getSecond(), this::tryApplyNewDataPacks, pair.getFirst(), new TranslatableComponent("dataPack.title")));
        }
    }

    private static void copyBetweenDirectories(Path fromPath, Path toPath, Path path) {
        try {
            Util.copyBetweenDirs(fromPath, toPath, path);
        } catch (IOException exception) {
            MellowUI.LOGGER.warn("Failed to copy datapack file from {} to {}", path, toPath);
            throw new DatapackException(exception);
        }
    }

    @Nullable
    protected Path getTempDataPackDirectory() {
        if (this.tempDataPackDirectory == null) {
            try {
                this.tempDataPackDirectory = Files.createTempDirectory("mcworld-");
            } catch (IOException exception) {
                MellowUI.LOGGER.warn("Failed to create temporary data packs directory", exception);
                SystemToast.onPackCopyFailure(this.minecraft, this.uiState.getTargetFolder());
                this.popScreen();
            }
        }

        return this.tempDataPackDirectory;
    }

    private boolean copyTempDataPackDirectoryToNewWorld() {
        if (this.tempDataPackDirectory != null) {
            try (
                    LevelStorageSource.LevelStorageAccess save = this.minecraft.getLevelSource().createAccess(this.uiState.getTargetFolder());
                    Stream<Path> stream = Files.walk(this.tempDataPackDirectory);
            ) {
                Path datapacksFolder = save.getLevelPath(LevelResource.DATAPACK_DIR);
                Files.createDirectories(datapacksFolder);
                stream.filter(path -> !path.equals(this.tempDataPackDirectory)).forEach(path -> copyBetweenDirectories(this.tempDataPackDirectory, datapacksFolder, path));
            } catch (DatapackException | IOException exception) {
                MellowUI.LOGGER.warn("Failed to copy datapacks to world {}", this.uiState.getTargetFolder(), exception);
                SystemToast.onPackCopyFailure(this.minecraft, this.uiState.getTargetFolder());
                this.popScreen();
                return false;
            }
        }

        return true;
    }

    private void removeTempDataPackDirectory() {
        if (this.tempDataPackDirectory != null) {
            try (Stream<Path> stream = Files.walk(this.tempDataPackDirectory)) {
                stream.sorted(Comparator.reverseOrder()).forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException exception) {
                        MellowUI.LOGGER.warn("Failed to remove temporary file {}", path, exception);
                    }
                });
            } catch (IOException exception) {
                MellowUI.LOGGER.warn("Failed to list temporary directory {}", this.tempDataPackDirectory);
            }

            this.tempDataPackDirectory = null;
        }
    }

    private void tryApplyNewDataPacks(PackRepository repository) {
        List<String> selectedPacks = ImmutableList.copyOf(repository.getSelectedIds());
        List<String> appliedPacks = repository.getAvailableIds().stream().filter(id -> !selectedPacks.contains(id)).collect(ImmutableList.toImmutableList());
        DataPackConfig config = new DataPackConfig(selectedPacks, appliedPacks);
        if (selectedPacks.equals(this.dataPacks.getEnabled())) {
            this.dataPacks = config;
        } else {
            this.minecraft.tell(() -> this.minecraft.setScreen(new GenericDirtMessageScreen(new TranslatableComponent("dataPack.validation.working"))));
            WorldStem.load(new WorldStem.InitConfig(repository, Commands.CommandSelection.INTEGRATED, 2, false), () -> config, (manager, config1) -> {
                RegistryAccess access = this.uiState().registryHolder();
                RegistryAccess.Writable writableAccess = RegistryAccess.builtinCopy();
                DynamicOps<JsonElement> exportSettings = RegistryOps.create(JsonOps.INSTANCE, access);
                DynamicOps<JsonElement> importSettings = RegistryOps.createAndLoad(JsonOps.INSTANCE, writableAccess, manager);
                DataResult<WorldGenSettings> result = WorldGenSettings.CODEC.encodeStart(exportSettings, this.uiState.makeSettings(this.uiState.isHardcore())).flatMap(
                        element -> WorldGenSettings.CODEC.parse(importSettings, element));
                WorldGenSettings generatorSettings = result.getOrThrow(false, Util.prefix("Error parsing world generation settings after loading data packs: ", MellowUI.LOGGER::error));
                LevelSettings settings = this.createWorldSettings(generatorSettings.isDebug());
                return Pair.of(new PrimaryLevelData(settings, generatorSettings, result.lifecycle()), writableAccess.freeze());
            }, Util.backgroundExecutor(), this.minecraft).thenAcceptAsync((stem) -> {
                this.dataPacks = config;
                this.uiState.tryUpdateDataConfiguration(stem);
                stem.close();
            }, this.minecraft).handle((p_205431_, exception) -> {
                if (exception != null) {
                    MellowUI.LOGGER.warn("Failed to validate datapack", exception);
                    this.minecraft.tell(() -> this.minecraft.setScreen(new ConfirmScreen(onTrue -> {
                        if (onTrue) {
                            this.openDataPacksSelectionScreen();
                        } else {
                            this.dataPacks = DataPackConfig.DEFAULT;
                            this.minecraft.setScreen(this);
                        }
                    }, new TranslatableComponent("dataPack.validation.failed"), TextComponent.EMPTY, new TranslatableComponent("dataPack.validation.back"), new TranslatableComponent("dataPack.validation.reset"))));
                } else {
                    this.minecraft.tell(() -> this.minecraft.setScreen(this));
                }

                return null;
            });
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class DatapackException extends RuntimeException {
        public DatapackException(Throwable throwable) {
            super(throwable);
        }
    }
}
