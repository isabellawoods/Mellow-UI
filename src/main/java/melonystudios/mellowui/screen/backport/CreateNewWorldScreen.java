package melonystudios.mellowui.screen.backport;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.config.option.BooleanOption;
import melonystudios.mellowui.config.option.IterableOption;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.tab.Tab;
import melonystudios.mellowui.element.tab.TabManager;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.element.text.TooltipDisplayData;
import melonystudios.mellowui.element.widget.*;
import melonystudios.mellowui.sound.MUISounds;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.GenericDirtMessageScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.EditGameRulesScreen;
import net.minecraft.client.gui.screens.worldselection.WorldPreset;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.*;
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
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
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
    protected static final Marker MARKER = MarkerManager.getMarker("CreateNewWorldScreen");
    private final RenderComponents components = RenderComponents.INSTANCE;
    @Nullable
    private final Screen lastScreen;
    @Nullable
    private Path tempDataPackDirectory;
    @Nullable
    private PackRepository tempDataPackRepository;
    protected DataPackConfig dataPacks;

    // Widgets
    private final TabManager manager = new TabManager(this::addRenderableWidget, this::removeWidget);
    private final List<TabButton> tabs = Lists.newArrayList();
    private final WorldCreationUIState uiState;
    public Button createWorldButton;

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

    public static CreateNewWorldScreen createFromExisting(Minecraft minecraft, @Nullable Screen lastScreen, LevelSettings settings, WorldGenSettings generatorSettings, DataPackConfig dataPacks, RegistryAccess.Frozen registries, @Nullable Path tempDataPackDirectory) {
        CreateNewWorldScreen worldCreationScreen = new CreateNewWorldScreen(minecraft, lastScreen, settings, generatorSettings, dataPacks, registries, WorldPreset.of(generatorSettings), OptionalLong.of(generatorSettings.seed()));
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
        super(new TranslatableComponent("selectWorld.create").withStyle(TextComponents.titleStyle()));
        this.lastScreen = lastScreen;
        this.dataPacks = dataPacks;
        this.uiState = new WorldCreationUIState(minecraft.getLevelSource().getBaseDir(), settings, generatorSettings, registries, preset, seed);
    }

    public WorldCreationUIState uiState() {
        return this.uiState;
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        super.resize(minecraft, width, height);
        this.tabs.get(0).setSelected(true);
    }

    @Override
    public void tick() {
        if (this.manager.getCurrentTab() != null) this.manager.getCurrentTab().tickingWidgets.forEach(TickingWidget::tickWidget);
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
            MellowConfigs.CLIENT_CONFIGS.createNewWorldStyle.set(false);
            this.minecraft.setScreen(CreateWorldScreen.createFresh(this.lastScreen));
        }, (button, stack, mouseX, mouseY) -> this.components.renderTooltip(this, button, new TranslatableComponent("button.mellowui.work_in_progress.tooltip").withStyle(ChatFormatting.YELLOW),
                        mouseX, mouseY)));

        this.addTabs();
    }

    private void addTabs() {
        int tabWidth = this.components.threeTabWidth(this.width);
        GameTab game = new GameTab();
        WorldTab world = new WorldTab();
        MoreTab more = new MoreTab();

        // Game tab
        TabButton gameTab = this.addRenderableWidget(new TabButton(this.width / 2 - tabWidth / 2 - tabWidth, 0, tabWidth, 24, "game", new TranslatableComponent("tab.mellowui.game"), button -> {
            this.tabs.forEach(tab -> tab.setSelected(false));
            this.manager.openTab(game);
        }));

        // World tab
        TabButton worldTab = this.addRenderableWidget(new TabButton(this.width / 2 - tabWidth / 2, 0, tabWidth, 24, "world", new TranslatableComponent("tab.mellowui.world"), button -> {
            this.tabs.forEach(tab -> tab.setSelected(false));
            this.manager.openTab(world);
        }));

        // More tab
        TabButton moreTab  = this.addRenderableWidget(new TabButton(this.width / 2 + tabWidth / 2, 0, tabWidth, 24, "more", new TranslatableComponent("tab.mellowui.more"), button -> {
            this.tabs.forEach(tab -> tab.setSelected(false));
            this.manager.openTab(more);
        }));

        if (!this.tabs.isEmpty()) {
            this.tabs.stream().filter(TabButton::selected).findFirst().ifPresent(tab -> {
                switch (tab.tabName()) {
                    case "world": {
                        this.manager.openTab(world);
                        worldTab.setSelected(true);
                        break;
                    }
                    case "more": {
                        this.manager.openTab(more);
                        moreTab.setSelected(true);
                        break;
                    }
                    case "game": default: {
                        this.manager.openTab(game);
                        gameTab.setSelected(true);
                        break;
                    }
                }
                this.tabs.clear();
                this.tabs.add(gameTab);
                this.tabs.add(worldTab);
                this.tabs.add(moreTab);
            });
        } else {
            this.tabs.add(gameTab);
            this.tabs.add(worldTab);
            this.tabs.add(moreTab);
            this.manager.openTab(game);
            this.tabs.get(0).setSelected(true);
        }
        // MellowUI.LOGGER.debug(MARKER, "all children: {}", this.children);
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
        if (this.manager.getCurrentTab() != null) {
            this.manager.getCurrentTab().render(stack, mouseX, mouseY, partialTicks);
            TooltipDisplayData tooltipData = this.manager.getCurrentTab().tooltipData();
            if (tooltipData != null) this.manager.getCurrentTab().renderTooltip(stack, this);
        }
    }

    @Override
    public void renderDirtBackground(int vOffset) {
        if (MellowConfigs.CLIENT_CONFIGS.screenBackgroundStyle.get()) this.components.renderMenuBackground(0, 24, this.width, this.height, vOffset);
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
            MellowUI.LOGGER.warn(MARKER, "Failed to copy datapack file from {} to {}", path, toPath);
            throw new DatapackException(exception);
        }
    }

    @Nullable
    protected Path getTempDataPackDirectory() {
        if (this.tempDataPackDirectory == null) {
            try {
                this.tempDataPackDirectory = Files.createTempDirectory("mcworld-");
            } catch (IOException exception) {
                MellowUI.LOGGER.warn(MARKER, "Failed to create temporary data packs directory", exception);
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
                    Stream<Path> stream = Files.walk(this.tempDataPackDirectory)
            ) {
                Path datapacksFolder = save.getLevelPath(LevelResource.DATAPACK_DIR);
                Files.createDirectories(datapacksFolder);
                stream.filter(path -> !path.equals(this.tempDataPackDirectory)).forEach(path -> copyBetweenDirectories(this.tempDataPackDirectory, datapacksFolder, path));
            } catch (DatapackException | IOException exception) {
                MellowUI.LOGGER.warn(MARKER, "Failed to copy datapacks to world {}", this.uiState.getTargetFolder(), exception);
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
                        MellowUI.LOGGER.warn(MARKER, "Failed to remove temporary file {}", path, exception);
                    }
                });
            } catch (IOException exception) {
                MellowUI.LOGGER.warn(MARKER, "Failed to list temporary directory {}", this.tempDataPackDirectory);
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
            }, this.minecraft).handle((void1, exception) -> {
                if (exception != null) {
                    MellowUI.LOGGER.warn(MARKER, "Failed to validate datapack", exception);
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

    class GameTab extends Tab {
        private TooltippedTextField nameEdit;

        @Override
        public void init() {
            int widgetY = 68;

            // World name
            this.nameEdit = new TooltippedTextField(this.minecraft.font, CreateNewWorldScreen.this.width / 2 - 105, widgetY, 210, 20, new TranslatableComponent("selectWorld.enterName"), TextComponent.EMPTY);
            this.nameEdit.setValue(CreateNewWorldScreen.this.uiState().getName());
            this.nameEdit.setResponder(CreateNewWorldScreen.this.uiState()::setName);
            this.nameEdit.setTooltip(new TranslatableComponent("menu.mellowui.create_new_world.target_folder", new TextComponent(CreateNewWorldScreen.this.uiState().getTargetFolder()).withStyle(ChatFormatting.ITALIC)));
            CreateNewWorldScreen.this.uiState().addListener(state -> {
                this.nameEdit.setTooltip(new TranslatableComponent("menu.mellowui.create_new_world.target_folder", new TextComponent(state.getTargetFolder()).withStyle(ChatFormatting.ITALIC)));
                CreateNewWorldScreen.this.createWorldButton.active = !state.getName().isEmpty();
            });
            this.addWidget(this.nameEdit);
            CreateNewWorldScreen.this.setInitialFocus(this.nameEdit);
            widgetY += 28;

            // Game Mode
            IterableOption gameModeOption = new IterableOption("selectWorld.gameMode",
                    (options, index) -> CreateNewWorldScreen.this.uiState().setGameMode(this.cycleGameMode(CreateNewWorldScreen.this.uiState())),
                    (options, button) -> new TranslatableComponent("options.generic_value", new TranslatableComponent("selectWorld.gameMode"),
                            CreateNewWorldScreen.this.uiState().getGameMode().displayName()));
            AbstractWidget gameModeButton = gameModeOption.createButton(this.minecraft.options, CreateNewWorldScreen.this.width / 2 - 105, widgetY, 210);
            ((MUIOptionButton) gameModeButton).getOption().setTooltip(CreateNewWorldScreen.this.uiState().getGameMode().getInfo());
            CreateNewWorldScreen.this.uiState().addListener(state -> ((MUIOptionButton) gameModeButton).getOption().setTooltip(state.getGameMode().getInfo()));
            this.addWidget(gameModeButton);
            widgetY += 28;

            // Difficulty
            IterableOption difficultyOption = new IterableOption("options.difficulty",
                    (options, index) -> CreateNewWorldScreen.this.uiState().setDifficulty(this.cycleDifficulty(CreateNewWorldScreen.this.uiState())),
                    (options, button) -> new TranslatableComponent("options.generic_value", new TranslatableComponent("options.difficulty"),
                            CreateNewWorldScreen.this.uiState().getDifficulty().getDisplayName()));
            AbstractWidget difficultyButton = difficultyOption.createButton(this.minecraft.options, CreateNewWorldScreen.this.width / 2 - 105, widgetY, 210);
            ((MUIOptionButton) difficultyButton).getOption().setTooltip(this.getDifficultyDescription(CreateNewWorldScreen.this.uiState().getDifficulty().getKey()));
            CreateNewWorldScreen.this.uiState().addListener(state -> {
                difficultyButton.active = !state.isHardcore();
                ((MUIOptionButton) difficultyButton).getOption().setTooltip(this.getDifficultyDescription(state.getDifficulty().getKey()));
            });
            this.addWidget(difficultyButton);

            // Hardcore
            HardcoreSetButton hardcoreButton = new HardcoreSetButton(CreateNewWorldScreen.this.width / 2 + 114, widgetY, 20, 20,
                    button -> CreateNewWorldScreen.this.uiState().setHardcore(!CreateNewWorldScreen.this.uiState().isHardcore()), (button, stack, mouseX, mouseY) ->
                    this.components.renderTooltip(CreateNewWorldScreen.this, button, new TranslatableComponent("config.minecraft.difficulty.hardcore.tooltip",
                            new TranslatableComponent("config.minecraft.difficulty.hardcore.title").withStyle(TextComponents.withColor(0xFF0000).withBold(true))), mouseX, mouseY),
                    new TranslatableComponent("options.difficulty.hardcore")).setSelected(CreateNewWorldScreen.this.uiState().isHardcore());
            CreateNewWorldScreen.this.uiState().addListener(state -> {
                SoundManager manager = this.minecraft.getSoundManager();
                if ((state.isHardcore() && !hardcoreButton.selected()) || (!state.isHardcore() && hardcoreButton.selected())) {
                    manager.play(SimpleSoundInstance.forUI(MUISounds.HARDCORE_TOGGLE.get(), 1));
                }
                if (state.isHardcore() && !hardcoreButton.selected()) manager.play(SimpleSoundInstance.forUI(MUISounds.HARDCORE_TURN_ON.get(), 1));
                if (!state.isHardcore() && hardcoreButton.selected()) manager.play(SimpleSoundInstance.forUI(MUISounds.HARDCORE_TURN_OFF.get(), 1));

                hardcoreButton.setSelected(state.isHardcore());
            });
            this.addWidget(hardcoreButton);
            widgetY += 28;

            // Allow Commands (Allow Cheats, in 1.16)
            BooleanOption allowCommandsOption = new BooleanOption("selectWorld.allowCommands", new TranslatableComponent("selectWorld.allowCommands.info"),
                    options -> CreateNewWorldScreen.this.uiState().allowsCommands(),
                    (options, newValue) -> CreateNewWorldScreen.this.uiState().setAllowCommands(newValue));
            AbstractWidget allowCommandsButton = allowCommandsOption.createButton(this.minecraft.options, CreateNewWorldScreen.this.width / 2 - 105, widgetY, 210);
            CreateNewWorldScreen.this.uiState().addListener(state -> allowCommandsButton.active = !state.isDebug() && !state.isHardcore());
            this.addWidget(allowCommandsButton);

            // MellowUI.LOGGER.debug(MARKER, "all widgets: {}", this.widgets);
            super.init();
        }

        private WorldCreationUIState.SelectedGameMode cycleGameMode(WorldCreationUIState uiState) {
            return switch (uiState.getGameMode()) {
                case SURVIVAL -> WorldCreationUIState.SelectedGameMode.CREATIVE;
                case CREATIVE -> WorldCreationUIState.SelectedGameMode.ADVENTURE;
                case ADVENTURE -> hasAltDown() ? WorldCreationUIState.SelectedGameMode.SPECTATOR : WorldCreationUIState.SelectedGameMode.SURVIVAL;
                default -> WorldCreationUIState.SelectedGameMode.SURVIVAL;
            };
        }

        private Difficulty cycleDifficulty(WorldCreationUIState uiState) {
            return switch (uiState.getDifficulty()) {
                case PEACEFUL -> Difficulty.EASY;
                case NORMAL -> Difficulty.HARD;
                case HARD -> Difficulty.PEACEFUL;
                default -> Difficulty.NORMAL;
            };
        }

        private Component getDifficultyDescription(String difficultyKey) {
            return new TranslatableComponent("config.minecraft.difficulty." + difficultyKey + ".tooltip");
        }

        @Override
        public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
            super.render(stack, mouseX, mouseY, partialTicks);
            if (this.nameEdit != null) this.components.drawString(new TranslatableComponent("selectWorld.enterName"), true, this.nameEdit.x, 56, 0xFFFFFF);
        }
    }

    class WorldTab extends Tab {
        private EditBox seedEdit;

        @Override
        public void init() {
            int widgetY = 56;
            Minecraft minecraft = Minecraft.getInstance();

            // World Type
            IterableOption worldTypeOption = new IterableOption("selectWorld.mapType",
                    (options, index) -> {}, /*screen.uiState().setWorldType(this.cycleWorldType(screen.uiState()))*/
                    (options, button) -> new TranslatableComponent("selectWorld.mapType").append(" ").append(CreateNewWorldScreen.this.uiState().getWorldType().describePreset()));
            AbstractWidget worldTypeButton = worldTypeOption.createButton(minecraft.options, CreateNewWorldScreen.this.width / 2 - 155, widgetY, 150);
            ((MUIOptionButton) worldTypeButton).getOption().setTooltip(CreateNewWorldScreen.this.uiState().getWorldType().isAmplified() ? new TranslatableComponent("generator.amplified.info") : null);
            CreateNewWorldScreen.this.uiState().addListener(state -> {
                ((MUIOptionButton) worldTypeButton).getOption().setTooltip(state.getWorldType().isAmplified() ? new TranslatableComponent("generator.amplified.info") : null);
                worldTypeButton.active = CreateNewWorldScreen.this.uiState().getWorldType().preset() != null;
            });
            this.addWidget(worldTypeButton);

            // Customize world type (will have to rewrite presets for this to work)
            Button customizeTypeButton = new Button(CreateNewWorldScreen.this.width / 2 + 5, widgetY, 150, 20, new TranslatableComponent("selectWorld.customizeType"),
                    button -> this.openPresetEditor(CreateNewWorldScreen.this.uiState()));
            CreateNewWorldScreen.this.uiState().addListener(state -> customizeTypeButton.active = !state.isDebug() && state.getPresetEditor() != null);
            this.addWidget(customizeTypeButton);
            widgetY += 42;

            // Seed for the world generator
            this.seedEdit = new EditBox(minecraft.font, CreateNewWorldScreen.this.width / 2 - 159, widgetY, 310, 20, new TranslatableComponent("selectWorld.seedInfo")) {
                @Nonnull
                protected MutableComponent createNarrationMessage() {
                    return super.createNarrationMessage().append(". ").append(new TranslatableComponent("selectWorld.seedInfo"));
                }
            };
            this.seedEdit.setValue(CreateNewWorldScreen.this.uiState().getSeed());
            this.seedEdit.setResponder(CreateNewWorldScreen.this.uiState()::setSeed);
            this.addWidget(this.seedEdit);
            widgetY += 33;

            // Generate structures
            BooleanOption generateStructuresOption = new BooleanOption("options.on", new TranslatableComponent("selectWorld.mapFeatures.info"),
                    options -> CreateNewWorldScreen.this.uiState().generatesStructures(),
                    (options, newValue) -> CreateNewWorldScreen.this.uiState().setGenerateStructures(newValue)) {
                @Nonnull
                public Component getMessage(Options options) {
                    return this.get(options) ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF;
                }
            };
            AbstractWidget generateStructuresButton = generateStructuresOption.createButton(minecraft.options, CreateNewWorldScreen.this.width / 2 + 111, widgetY, 44);
            generateStructuresButton.active = !CreateNewWorldScreen.this.uiState().isDebug();
            CreateNewWorldScreen.this.uiState().addListener(state -> generateStructuresButton.active = !state.isDebug());
            this.addWidget(generateStructuresButton);
            widgetY += 24;

            BooleanOption bonusChestOption = new BooleanOption("options.off",
                    options -> CreateNewWorldScreen.this.uiState().hasBonusChest(),
                    (options, newValue) -> CreateNewWorldScreen.this.uiState().setBonusChest(newValue)) {
                @Nonnull
                public Component getMessage(Options options) {
                    return this.get(options) ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF;
                }
            };
            AbstractWidget bonusChestButton = bonusChestOption.createButton(minecraft.options, CreateNewWorldScreen.this.width / 2 + 111, widgetY, 44);
            bonusChestButton.active = !CreateNewWorldScreen.this.uiState().isHardcore() && !CreateNewWorldScreen.this.uiState().isDebug();
            CreateNewWorldScreen.this.uiState().addListener(state -> bonusChestButton.active = !state.isHardcore() && !state.isDebug());
            this.addWidget(bonusChestButton);

            super.init();
        }

        private void openPresetEditor(WorldCreationUIState uiState) {
            WorldPreset.PresetEditor presetScreen = uiState.getPresetEditor();
            if (presetScreen != null) {
                // this isn't going to work unless I rewrite the whole class, great... ~isa 14-7-25
                //this.minecraft.setScreen(presetScreen.createEditScreen(CreateNewWorldScreen.this, uiState.getGeneratorSettings()));
            }
        }

        /*private WorldCreationUIState.WorldTypeEntry cycleWorldType(WorldCreationUIState uiState) {
            int presetCount = Screen.hasAltDown() ? uiState.getAlternatePresetList().size() : uiState.getNormalPresetList().size();
            ++PRESET_INDEX;
            if (PRESET_INDEX > presetCount) PRESET_INDEX = 0;
            return Screen.hasAltDown() ? uiState.getAlternatePresetList().get(PRESET_INDEX) : uiState.getNormalPresetList().get(PRESET_INDEX);
        }*/

        @Override
        public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
            super.render(stack, mouseX, mouseY, partialTicks);
            this.components.renderTextBoxSuggestion(this.seedEdit, new TranslatableComponent("selectWorld.seedInfo").withStyle(ChatFormatting.DARK_GRAY));
            this.components.drawString(new TranslatableComponent("selectWorld.enterSeed"), true, CreateNewWorldScreen.this.width / 2 - 155, 84, 0xFFFFFF);
            this.components.drawString(new TranslatableComponent("selectWorld.mapFeatures"), true, CreateNewWorldScreen.this.width / 2 - 155, 135, 0xFFFFFF);
            this.components.drawString(new TranslatableComponent("selectWorld.bonusItems"), true, CreateNewWorldScreen.this.width / 2 - 155, 159, 0xFFFFFF);
        }
    }

    class MoreTab extends Tab {
        @Override
        public void init() {
            int widgetY = 56;

            // Game Rules
            this.addWidget(new Button(CreateNewWorldScreen.this.width / 2 - 105, widgetY, 210, 20, new TranslatableComponent("selectWorld.gameRules"),
                    button -> this.minecraft.setScreen(new EditGameRulesScreen(CreateNewWorldScreen.this.uiState().getGameRules().copy(), updatedRules -> {
                        this.minecraft.setScreen(CreateNewWorldScreen.this);
                        updatedRules.ifPresent(CreateNewWorldScreen.this.uiState()::setGameRules);
                    }))));
            widgetY += 28;

            // Import Settings (Experiments, in newer versions)
            this.addWidget(new Button(CreateNewWorldScreen.this.width / 2 - 105, widgetY, 210, 20, new TranslatableComponent("selectWorld.import_worldgen_settings"), button -> {})).active = false;
            widgetY += 28;

            // Data Packs
            this.addWidget(new Button(CreateNewWorldScreen.this.width / 2 - 105, widgetY, 210, 20, new TranslatableComponent("selectWorld.dataPacks"),
                    button -> CreateNewWorldScreen.this.openDataPacksSelectionScreen()));

            super.init();
        }
    }
}
