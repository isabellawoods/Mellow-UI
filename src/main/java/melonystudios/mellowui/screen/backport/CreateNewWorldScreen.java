package melonystudios.mellowui.screen.backport;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.datafixers.util.Pair;
import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.tab.Tab;
import melonystudios.mellowui.element.tab.TabManager;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.element.text.TooltipDisplayData;
import melonystudios.mellowui.element.widget.HardcoreSetButton;
import melonystudios.mellowui.element.widget.TabButton;
import melonystudios.mellowui.element.widget.TooltippedTextField;
import melonystudios.mellowui.sound.MUISounds;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.toasts.SystemToast;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.OptionButton;
import net.minecraft.client.settings.BooleanOption;
import net.minecraft.client.settings.IteratableOption;
import net.minecraft.command.Commands;
import net.minecraft.resources.*;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.codec.DatapackCodec;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.*;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import net.minecraft.world.storage.FolderName;
import net.minecraft.world.storage.SaveFormat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fml.packs.ResourcePackLoader;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
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
    private ResourcePackList tempDataPackRepository;
    protected DatapackCodec dataPacks;

    // Widgets
    private final TabManager manager = new TabManager(this::addButton, this::removeWidget);
    private final List<TabButton> tabs = Lists.newArrayList();
    private final WorldCreationUIState uiState;
    public Button createWorldButton;

    public static void openFresh(Minecraft minecraft, @Nullable Screen lastScreen) {
        queueLoadScreen(minecraft, new TranslationTextComponent("createWorld.preparing"));
        ResourcePackList repository = new ResourcePackList(new ServerPackFinder());
        ResourcePackLoader.loadResourcePacks(repository, ServerLifecycleHooks::buildPackFinder);
        WorldSettings settings = new WorldSettings(WorldCreationUIState.DEFAULT_WORLD_NAME.getString(), GameType.SURVIVAL, false, Difficulty.NORMAL, false, new GameRules(), DatapackCodec.DEFAULT);
        DynamicRegistries.Impl registries = DynamicRegistries.builtin();
        DimensionGeneratorSettings generatorSettings = ForgeHooksClient.getDefaultWorldType().map(type ->
                type.create(registries, new Random().nextLong(), true, false)).orElseGet(() ->
                DimensionGeneratorSettings.makeDefault(registries.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY), registries.registryOrThrow(Registry.BIOME_REGISTRY),
                        registries.registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY)));

        minecraft.setScreen(new CreateNewWorldScreen(minecraft, lastScreen, settings, generatorSettings, DatapackCodec.DEFAULT, registries, ForgeHooksClient.getDefaultWorldType(), OptionalLong.empty()));
    }

    public static CreateNewWorldScreen createFromExisting(Minecraft minecraft, @Nullable Screen lastScreen, WorldSettings settings, DimensionGeneratorSettings generatorSettings, DatapackCodec dataPacks, DynamicRegistries.Impl registries, @Nullable Path tempDataPackDirectory) {
        CreateNewWorldScreen worldCreationScreen = new CreateNewWorldScreen(minecraft, lastScreen, settings, generatorSettings, dataPacks, registries, BiomeGeneratorTypeScreens.of(generatorSettings), OptionalLong.of(generatorSettings.seed()));
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

    private CreateNewWorldScreen(Minecraft minecraft, @Nullable Screen lastScreen, WorldSettings settings, DimensionGeneratorSettings generatorSettings, DatapackCodec dataPacks, DynamicRegistries.Impl registries, Optional<BiomeGeneratorTypeScreens> preset, OptionalLong seed) {
        super(new TranslationTextComponent("selectWorld.create").withStyle(TextComponents.titleStyle()));
        this.lastScreen = lastScreen;
        this.dataPacks = dataPacks;
        this.uiState = new WorldCreationUIState(minecraft.getLevelSource().getBaseDir(), settings, generatorSettings, registries, preset, seed);
    }

    public WorldCreationUIState uiState() {
        return this.uiState;
    }

    public void removeWidget(IGuiEventListener listener) {
        if (listener instanceof Widget) this.buttons.remove(listener);
        this.children.remove(listener);
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        super.resize(minecraft, width, height);
        this.tabs.get(0).setSelected(true);
    }

    @Override
    public void tick() {
        if (this.manager.getCurrentTab() != null) this.manager.getCurrentTab().tickingWidgets.forEach(IScreen::tick);
    }

    @Override
    protected void init() {
        // Create New World
        this.addButton(this.createWorldButton = new Button(this.width / 2 - 155, this.height - 25, 150, 20, new TranslationTextComponent("selectWorld.create"),
                button -> this.onCreate()));
        this.createWorldButton.active = !this.uiState.getName().isEmpty();

        // Cancel button
        this.addButton(new Button(this.width / 2 + 5, this.height - 25, 150, 20, DialogTexts.GUI_CANCEL,
                button -> this.popScreen()));

        // WIP warning button
        this.addButton(new Button(this.width / 2 + 165, this.height - 25, 20, 20, new TranslationTextComponent("button.mellowui.work_in_progress").withStyle(
                style -> style.withColor(TextFormatting.YELLOW).withBold(true)), button -> {
            MellowConfigs.CLIENT_CONFIGS.createNewWorldStyle.set(false);
            this.minecraft.setScreen(CreateWorldScreen.create(this.lastScreen));
        }, (button, stack, mouseX, mouseY) -> this.components.renderTooltip(this, button, new TranslationTextComponent("button.mellowui.work_in_progress.tooltip").withStyle(TextFormatting.YELLOW),
                        mouseX, mouseY)));

        this.addTabs();
    }

    private void addTabs() {
        int tabWidth = this.components.threeTabWidth(this.width);
        GameTab game = new GameTab();
        WorldTab world = new WorldTab();
        MoreTab more = new MoreTab();

        // Game tab
        TabButton gameTab = this.addButton(new TabButton(this.width / 2 - tabWidth / 2 - tabWidth, 0, tabWidth, 24, "game", new TranslationTextComponent("tab.mellowui.game"), button -> {
            this.tabs.forEach(tab -> tab.setSelected(false));
            this.manager.openTab(game);
        }));

        // World tab
        TabButton worldTab = this.addButton(new TabButton(this.width / 2 - tabWidth / 2, 0, tabWidth, 24, "world", new TranslationTextComponent("tab.mellowui.world"), button -> {
            this.tabs.forEach(tab -> tab.setSelected(false));
            this.manager.openTab(world);
        }));

        // More tab
        TabButton moreTab = this.addButton(new TabButton(this.width / 2 + tabWidth / 2, 0, tabWidth, 24, "more", new TranslationTextComponent("tab.mellowui.more"), button -> {
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

    private static void queueLoadScreen(Minecraft minecraft, ITextComponent title) {
        minecraft.forceSetScreen(new DirtMessageScreen(title));
    }

    private void onCreate() {
        queueLoadScreen(this.minecraft, new TranslationTextComponent("createWorld.preparing"));
        if (this.copyTempDataPackDirectoryToNewWorld()) {
            this.cleanupTempResources();
            DimensionGeneratorSettings generatorSettings = this.uiState.getGeneratorSettings();
            WorldSettings settings = this.createWorldSettings(this.uiState.isDebug());

            this.minecraft.createLevel(this.uiState.getTargetFolder(), settings, this.uiState.getRegistryHolder(), generatorSettings);
        }
    }

    private WorldSettings createWorldSettings(boolean debug) {
        String worldName = this.uiState.getName().trim();
        if (debug) {
            GameRules gameRules = new GameRules();
            gameRules.getRule(GameRules.RULE_DAYLIGHT).set(false, null);
            return new WorldSettings(worldName, GameType.SPECTATOR, this.uiState.isHardcore(), Difficulty.PEACEFUL, this.uiState.allowsCommands(), gameRules, this.uiState.getSettings().getDataPackConfig());
        } else {
            return new WorldSettings(worldName, this.uiState.getGameMode().gameType(), this.uiState.isHardcore(), this.uiState.getDifficulty(), this.uiState.allowsCommands(), this.uiState.getGameRules(), this.uiState.getSettings().getDataPackConfig());
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
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
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
    private Pair<File, ResourcePackList> getDataPackSelectionSettings() {
        Path packsFolder = this.getTempDataPackDirectory();
        if (packsFolder != null) {
            File file1 = packsFolder.toFile();
            if (this.tempDataPackRepository == null) {
                this.tempDataPackRepository = new ResourcePackList(new ServerPackFinder(), new FolderPackFinder(file1, IPackNameDecorator.DEFAULT));
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
        Pair<File, ResourcePackList> pair = this.getDataPackSelectionSettings();
        if (pair != null) {
            this.minecraft.setScreen(new PackScreen(this, pair.getSecond(), this::tryApplyNewDataPacks, pair.getFirst(), new TranslationTextComponent("dataPack.title")));
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
            } catch (IOException ioexception) {
                MellowUI.LOGGER.warn(MARKER, "Failed to create temporary data packs directory", ioexception);
                SystemToast.onPackCopyFailure(this.minecraft, this.uiState.getTargetFolder());
                this.popScreen();
            }
        }

        return this.tempDataPackDirectory;
    }

    private boolean copyTempDataPackDirectoryToNewWorld() {
        if (this.tempDataPackDirectory != null) {
            try (
                    SaveFormat.LevelSave save = this.minecraft.getLevelSource().createAccess(this.uiState.getTargetFolder());
                    Stream<Path> stream = Files.walk(this.tempDataPackDirectory)
            ) {
                Path datapacksFolder = save.getLevelPath(FolderName.DATAPACK_DIR);
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

    private void tryApplyNewDataPacks(ResourcePackList packList) {
        List<String> selectedPacks = ImmutableList.copyOf(packList.getSelectedIds());
        List<String> newPacks = packList.getAvailableIds().stream().filter(packID -> !selectedPacks.contains(packID)).collect(ImmutableList.toImmutableList());
        DatapackCodec codec = new DatapackCodec(selectedPacks, newPacks);
        if (selectedPacks.equals(this.dataPacks.getEnabled())) {
            this.dataPacks = codec;
        } else {
            this.minecraft.tell(() -> this.minecraft.setScreen(new DirtMessageScreen(new TranslationTextComponent("dataPack.validation.working"))));
            DataPackRegistries.loadResources(packList.openAllSelected(), Commands.EnvironmentType.INTEGRATED, 2, Util.backgroundExecutor(), this.minecraft).handle((registries, exception) -> {
                if (exception != null) {
                    MellowUI.LOGGER.warn(MARKER, "Failed to validate datapack", exception);
                    this.minecraft.tell(() -> this.minecraft.setScreen(new ConfirmScreen(onTrue -> {
                        if (onTrue) {
                            this.openDataPacksSelectionScreen();
                        } else {
                            this.dataPacks = DatapackCodec.DEFAULT;
                            this.minecraft.setScreen(this);
                        }
                    }, new TranslationTextComponent("dataPack.validation.failed").withStyle(TextFormatting.BOLD), StringTextComponent.EMPTY, new TranslationTextComponent("dataPack.validation.back"), new TranslationTextComponent("dataPack.validation.reset"))));
                } else {
                    this.minecraft.tell(() -> {
                        this.dataPacks = codec;
                        this.uiState.tryUpdateDataConfiguration(registries);
                        registries.close();
                        this.minecraft.setScreen(this);
                    });
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
            this.nameEdit = new TooltippedTextField(this.minecraft.font, CreateNewWorldScreen.this.width / 2 - 105, widgetY, 210, 20, new TranslationTextComponent("selectWorld.enterName"), StringTextComponent.EMPTY);
            this.nameEdit.setValue(CreateNewWorldScreen.this.uiState().getName());
            this.nameEdit.setResponder(CreateNewWorldScreen.this.uiState()::setName);
            this.nameEdit.setTooltip(new TranslationTextComponent("menu.mellowui.create_new_world.target_folder", new StringTextComponent(CreateNewWorldScreen.this.uiState().getTargetFolder()).withStyle(TextFormatting.ITALIC)));
            CreateNewWorldScreen.this.uiState().addListener(state -> {
                this.nameEdit.setTooltip(new TranslationTextComponent("menu.mellowui.create_new_world.target_folder", new StringTextComponent(state.getTargetFolder()).withStyle(TextFormatting.ITALIC)));
                CreateNewWorldScreen.this.createWorldButton.active = !state.getName().isEmpty();
            });
            this.addWidget(this.nameEdit);
            CreateNewWorldScreen.this.setInitialFocus(this.nameEdit);
            widgetY += 28;

            // Game Mode
            IteratableOption gameModeOption = new IteratableOption("selectWorld.gameMode",
                    (options, index) -> CreateNewWorldScreen.this.uiState().setGameMode(this.cycleGameMode(CreateNewWorldScreen.this.uiState())),
                    (options, button) -> new TranslationTextComponent("options.generic_value", new TranslationTextComponent("selectWorld.gameMode"),
                            CreateNewWorldScreen.this.uiState().getGameMode().displayName()));
            Widget gameModeButton = gameModeOption.createButton(this.minecraft.options, CreateNewWorldScreen.this.width / 2 - 105, widgetY, 210);
            ((OptionButton) gameModeButton).getOption().setTooltip(this.minecraft.font.split(CreateNewWorldScreen.this.uiState().getGameMode().getInfo(), RenderComponents.TOOLTIP_MAX_WIDTH));
            CreateNewWorldScreen.this.uiState().addListener(state -> {
                gameModeButton.active = !state.isHardcore();
                ((OptionButton) gameModeButton).getOption().setTooltip(this.minecraft.font.split(state.getGameMode().getInfo(), RenderComponents.TOOLTIP_MAX_WIDTH));
                gameModeButton.setMessage(new TranslationTextComponent("options.generic_value", new TranslationTextComponent("selectWorld.gameMode"), CreateNewWorldScreen.this.uiState().getGameMode().displayName()));
            });
            this.addWidget(gameModeButton);
            widgetY += 28;

            // Difficulty
            IteratableOption difficultyOption = new IteratableOption("options.difficulty",
                    (options, index) -> CreateNewWorldScreen.this.uiState().setDifficulty(this.cycleDifficulty(CreateNewWorldScreen.this.uiState())),
                    (options, button) -> new TranslationTextComponent("options.generic_value", new TranslationTextComponent("options.difficulty"),
                            CreateNewWorldScreen.this.uiState().getDifficulty().getDisplayName()));
            Widget difficultyButton = difficultyOption.createButton(this.minecraft.options, CreateNewWorldScreen.this.width / 2 - 105, widgetY, 210);
            ((OptionButton) difficultyButton).getOption().setTooltip(this.minecraft.font.split(this.getDifficultyDescription(CreateNewWorldScreen.this.uiState().getDifficulty().getKey()), RenderComponents.TOOLTIP_MAX_WIDTH));
            CreateNewWorldScreen.this.uiState().addListener(state -> {
                difficultyButton.active = !state.isHardcore();
                ((OptionButton) difficultyButton).getOption().setTooltip(this.minecraft.font.split(this.getDifficultyDescription(state.getDifficulty().getKey()), RenderComponents.TOOLTIP_MAX_WIDTH));
            });
            this.addWidget(difficultyButton);

            // Hardcore
            HardcoreSetButton hardcoreButton = new HardcoreSetButton(CreateNewWorldScreen.this.width / 2 + 114, widgetY, 20, 20,
                    button -> CreateNewWorldScreen.this.uiState().setHardcore(!CreateNewWorldScreen.this.uiState().isHardcore()), (button, stack, mouseX, mouseY) ->
                    this.components.renderTooltip(CreateNewWorldScreen.this, button, new TranslationTextComponent("config.minecraft.difficulty.hardcore.tooltip",
                            new TranslationTextComponent("config.minecraft.difficulty.hardcore.title").withStyle(TextComponents.withColor(0xFF0000).withBold(true))), mouseX, mouseY),
                    new TranslationTextComponent("options.difficulty.hardcore")).setSelected(CreateNewWorldScreen.this.uiState().isHardcore());
            CreateNewWorldScreen.this.uiState().addListener(state -> {
                SoundHandler manager = this.minecraft.getSoundManager();
                if ((state.isHardcore() && !hardcoreButton.selected()) || (!state.isHardcore() && hardcoreButton.selected())) {
                    manager.play(SimpleSound.forUI(MUISounds.HARDCORE_TOGGLE.get(), 1));
                }
                if (state.isHardcore() && !hardcoreButton.selected()) manager.play(SimpleSound.forUI(MUISounds.HARDCORE_TURN_ON.get(), 1));
                if (!state.isHardcore() && hardcoreButton.selected()) manager.play(SimpleSound.forUI(MUISounds.HARDCORE_TURN_OFF.get(), 1));

                hardcoreButton.setSelected(state.isHardcore());
            });
            this.addWidget(hardcoreButton);
            widgetY += 28;

            // Allow Commands (Allow Cheats, in 1.16)
            BooleanOption allowCommandsOption = new BooleanOption("selectWorld.allowCommands", new TranslationTextComponent("selectWorld.allowCommands.info"),
                    options -> CreateNewWorldScreen.this.uiState().allowsCommands(),
                    (options, newValue) -> CreateNewWorldScreen.this.uiState().setAllowCommands(newValue));
            Widget allowCommandsButton = allowCommandsOption.createButton(this.minecraft.options, CreateNewWorldScreen.this.width / 2 - 105, widgetY, 210);
            CreateNewWorldScreen.this.uiState().addListener(state -> allowCommandsButton.active = !state.isDebug() && !state.isHardcore());
            this.addWidget(allowCommandsButton);

            // MellowUI.LOGGER.debug(MARKER, "all widgets: {}", this.widgets);
            super.init();
        }

        private WorldCreationUIState.SelectedGameMode cycleGameMode(WorldCreationUIState uiState) {
            switch (uiState.getGameMode()) {
                case SURVIVAL: return WorldCreationUIState.SelectedGameMode.CREATIVE;
                case CREATIVE: return WorldCreationUIState.SelectedGameMode.ADVENTURE;
                case ADVENTURE: return hasAltDown() ? WorldCreationUIState.SelectedGameMode.SPECTATOR : WorldCreationUIState.SelectedGameMode.SURVIVAL;
                default: return WorldCreationUIState.SelectedGameMode.SURVIVAL;
            }
        }

        private Difficulty cycleDifficulty(WorldCreationUIState uiState) {
            switch (uiState.getDifficulty()) {
                case PEACEFUL: return Difficulty.EASY;
                case NORMAL: return Difficulty.HARD;
                case HARD: return Difficulty.PEACEFUL;
                default: return Difficulty.NORMAL;
            }
        }

        private ITextComponent getDifficultyDescription(String difficultyKey) {
            return new TranslationTextComponent("config.minecraft.difficulty." + difficultyKey + ".tooltip");
        }

        @Override
        public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
            super.render(stack, mouseX, mouseY, partialTicks);
            if (this.nameEdit != null) this.components.drawString(new TranslationTextComponent("selectWorld.enterName"), true, this.nameEdit.x, 56, 0xFFFFFF);
        }
    }

    class WorldTab extends Tab {
        private TextFieldWidget seedEdit;

        @Override
        public void init() {
            int widgetY = 56;

            // World Type
            IteratableOption worldTypeOption = new IteratableOption("selectWorld.mapType",
                    (options, index) -> {}, /*CreateNewWorldScreen.this.uiState().setWorldType(this.cycleWorldType(CreateNewWorldScreen.this.uiState()))*/
                    (options, button) -> new TranslationTextComponent("selectWorld.mapType").append(" ").append(CreateNewWorldScreen.this.uiState().getWorldType().describePreset()));
            Widget worldTypeButton = worldTypeOption.createButton(this.minecraft.options, CreateNewWorldScreen.this.width / 2 - 155, widgetY, 150);
            ((OptionButton) worldTypeButton).getOption().setTooltip(CreateNewWorldScreen.this.uiState().getWorldType().isAmplified() ? this.minecraft.font.split(new TranslationTextComponent("generator.amplified.info"),
                    RenderComponents.TOOLTIP_MAX_WIDTH) : Lists.newArrayList());
            CreateNewWorldScreen.this.uiState().addListener(state -> {
                ((OptionButton) worldTypeButton).getOption().setTooltip(state.getWorldType().isAmplified() ? this.minecraft.font.split(new TranslationTextComponent("generator.amplified.info"),
                        RenderComponents.TOOLTIP_MAX_WIDTH) : Lists.newArrayList());
                worldTypeButton.active = CreateNewWorldScreen.this.uiState().getWorldType().preset() != null;
            });
            this.addWidget(worldTypeButton);

            // Customize world type (will have to rewrite presets for this to work)
            Button customizeTypeButton = new Button(CreateNewWorldScreen.this.width / 2 + 5, widgetY, 150, 20, new TranslationTextComponent("selectWorld.customizeType"),
                    button -> this.openPresetEditor(CreateNewWorldScreen.this.uiState()));
            CreateNewWorldScreen.this.uiState().addListener(state -> customizeTypeButton.active = !state.isDebug() && state.getPresetEditor() != null);
            this.addWidget(customizeTypeButton);
            widgetY += 42;

            // Seed for the world generator
            this.seedEdit = new TextFieldWidget(this.minecraft.font, CreateNewWorldScreen.this.width / 2 - 159, widgetY, 310, 20, new TranslationTextComponent("selectWorld.seedInfo")) {
                @Nonnull
                protected IFormattableTextComponent createNarrationMessage() {
                    return super.createNarrationMessage().append(". ").append(new TranslationTextComponent("selectWorld.seedInfo"));
                }
            };
            this.seedEdit.setValue(CreateNewWorldScreen.this.uiState().getSeed());
            this.seedEdit.setResponder(CreateNewWorldScreen.this.uiState()::setSeed);
            this.addWidget(this.seedEdit);
            widgetY += 33;

            // Generate structures
            BooleanOption generateStructuresOption = new BooleanOption("options.on", new TranslationTextComponent("selectWorld.mapFeatures.info"),
                    options -> CreateNewWorldScreen.this.uiState().generatesStructures(),
                    (options, newValue) -> CreateNewWorldScreen.this.uiState().setGenerateStructures(newValue)) {
                @Nonnull
                public ITextComponent getMessage(GameSettings options) {
                    return this.get(options) ? DialogTexts.OPTION_ON : DialogTexts.OPTION_OFF;
                }
            };
            Widget generateStructuresButton = generateStructuresOption.createButton(this.minecraft.options, CreateNewWorldScreen.this.width / 2 + 111, widgetY, 44);
            generateStructuresButton.active = !CreateNewWorldScreen.this.uiState().isDebug();
            CreateNewWorldScreen.this.uiState().addListener(state -> generateStructuresButton.active = !state.isDebug());
            this.addWidget(generateStructuresButton);
            widgetY += 24;

            BooleanOption bonusChestOption = new BooleanOption("options.off",
                    options -> CreateNewWorldScreen.this.uiState().hasBonusChest(),
                    (options, newValue) -> CreateNewWorldScreen.this.uiState().setBonusChest(newValue)) {
                @Nonnull
                public ITextComponent getMessage(GameSettings options) {
                    return this.get(options) ? DialogTexts.OPTION_ON : DialogTexts.OPTION_OFF;
                }
            };
            Widget bonusChestButton = bonusChestOption.createButton(this.minecraft.options, CreateNewWorldScreen.this.width / 2 + 111, widgetY, 44);
            bonusChestButton.active = !CreateNewWorldScreen.this.uiState().isHardcore() && !CreateNewWorldScreen.this.uiState().isDebug();
            CreateNewWorldScreen.this.uiState().addListener(state -> bonusChestButton.active = !state.isHardcore() && !state.isDebug());
            this.addWidget(bonusChestButton);

            super.init();
        }

        private void openPresetEditor(WorldCreationUIState uiState) {
            BiomeGeneratorTypeScreens.IFactory presetScreen = uiState.getPresetEditor();
            if (presetScreen != null) {
                // this isn't going to work unless I rewrite the whole class, great... ~isa 14-7-25
                //Minecraft.getInstance().setScreen(presetScreen.createEditScreen((CreateWorldScreen) this.screen, uiState.getGeneratorSettings()));
            }
        }

        /*private WorldCreationUIState.WorldTypeEntry cycleWorldType(WorldCreationUIState uiState) {
            int presetCount = Screen.hasAltDown() ? uiState.getAlternatePresetList().size() : uiState.getNormalPresetList().size();
            ++PRESET_INDEX;
            if (PRESET_INDEX > presetCount) PRESET_INDEX = 0;
            return Screen.hasAltDown() ? uiState.getAlternatePresetList().get(PRESET_INDEX) : uiState.getNormalPresetList().get(PRESET_INDEX);
        }*/

        @Override
        public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
            super.render(stack, mouseX, mouseY, partialTicks);
            this.components.renderTextBoxSuggestion(this.seedEdit, new TranslationTextComponent("selectWorld.seedInfo").withStyle(TextFormatting.DARK_GRAY));
            this.components.drawString(new TranslationTextComponent("selectWorld.enterSeed"), true, CreateNewWorldScreen.this.width / 2 - 155, 84, 0xFFFFFF);
            this.components.drawString(new TranslationTextComponent("selectWorld.mapFeatures"), true, CreateNewWorldScreen.this.width / 2 - 155, 135, 0xFFFFFF);
            this.components.drawString(new TranslationTextComponent("selectWorld.bonusItems"), true, CreateNewWorldScreen.this.width / 2 - 155, 159, 0xFFFFFF);
        }
    }

    class MoreTab extends Tab {
        @Override
        public void init() {
            int widgetY = 56;

            // Game Rules
            this.addWidget(new Button(CreateNewWorldScreen.this.width / 2 - 105, widgetY, 210, 20, new TranslationTextComponent("selectWorld.gameRules"),
                    button -> this.minecraft.setScreen(new EditGamerulesScreen(CreateNewWorldScreen.this.uiState().getGameRules().copy(), updatedRules -> {
                        this.minecraft.setScreen(CreateNewWorldScreen.this);
                        updatedRules.ifPresent(CreateNewWorldScreen.this.uiState()::setGameRules);
                    }))));
            widgetY += 28;

            // Import Settings (Experiments, in newer versions)
            this.addWidget(new Button(CreateNewWorldScreen.this.width / 2 - 105, widgetY, 210, 20, new TranslationTextComponent("selectWorld.import_worldgen_settings"), button -> {})).active = false;
            widgetY += 28;

            // Data Packs
            this.addWidget(new Button(CreateNewWorldScreen.this.width / 2 - 105, widgetY, 210, 20, new TranslationTextComponent("selectWorld.dataPacks"),
                    button -> CreateNewWorldScreen.this.openDataPacksSelectionScreen()));

            super.init();
        }
    }
}
