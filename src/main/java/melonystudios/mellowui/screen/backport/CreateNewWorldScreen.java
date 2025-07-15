package melonystudios.mellowui.screen.backport;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.datafixers.util.Pair;
import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.screen.RenderComponents;
import melonystudios.mellowui.screen.tab.GameTab;
import melonystudios.mellowui.screen.tab.MoreTab;
import melonystudios.mellowui.screen.tab.TabContents;
import melonystudios.mellowui.screen.tab.WorldTab;
import melonystudios.mellowui.screen.widget.TabButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.toasts.SystemToast;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.command.Commands;
import net.minecraft.resources.*;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.codec.DatapackCodec;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
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
    private ResourcePackList tempDataPackRepository;
    private boolean recreated;
    protected DatapackCodec dataPacks;
    public TabContents selectedTab;
    @Nullable
    private final Screen lastScreen;
    private final WorldCreationUIState uiState;
    public Button createWorldButton;
    private String identifier = "game";

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

    public static CreateNewWorldScreen createFromExisting(Minecraft minecraft, @Nullable Screen lastScreen, WorldSettings settings, DimensionGeneratorSettings  generatorSettings, DatapackCodec dataPacks, DynamicRegistries.Impl registries, @Nullable Path tempDataPackDirectory) {
        CreateNewWorldScreen worldCreationScreen = new CreateNewWorldScreen(minecraft, lastScreen, settings, generatorSettings, dataPacks, registries, BiomeGeneratorTypeScreens.of(generatorSettings), OptionalLong.of(generatorSettings.seed()));
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

    private CreateNewWorldScreen(Minecraft minecraft, @Nullable Screen lastScreen, WorldSettings settings, DimensionGeneratorSettings generatorSettings, DatapackCodec dataPacks, DynamicRegistries.Impl registries, Optional<BiomeGeneratorTypeScreens> preset, OptionalLong seed) {
        super(new TranslationTextComponent("selectWorld.create"));
        this.lastScreen = lastScreen;
        this.dataPacks = dataPacks;
        this.uiState = new WorldCreationUIState(minecraft.getLevelSource().getBaseDir(), settings, generatorSettings, registries, preset, seed);
    }

    public WorldCreationUIState uiState() {
        return this.uiState;
    }

    @Override
    @Nonnull
    public <T extends Widget> T addButton(T widget) {
        return super.addButton(widget);
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
        this.addButton(this.createWorldButton = new Button(this.width / 2 - 155, this.height - 25, 150, 20, new TranslationTextComponent("selectWorld.create"),
                button -> this.onCreate()));
        this.createWorldButton.active = !this.uiState.getName().isEmpty();

        // Cancel button
        this.addButton(new Button(this.width / 2 + 5, this.height - 25, 150, 20, DialogTexts.GUI_CANCEL,
                button -> this.popScreen()));

        // WIP warning button
        this.addButton(new Button(this.width / 2 + 165, this.height - 25, 20, 20, new TranslationTextComponent("button.mellowui.work_in_progress").withStyle(
                style -> style.withColor(TextFormatting.YELLOW).withBold(true)), button -> {
            MellowConfigs.CLIENT_CONFIGS.updateCreateNewWorldMenu.set(false);
            this.minecraft.setScreen(CreateWorldScreen.create(this.lastScreen));
        }, (button, stack, mouseX, mouseY) -> this.components.renderTooltip(this, button, new TranslationTextComponent("button.mellowui.work_in_progress.desc").withStyle(TextFormatting.YELLOW),
                        mouseX, mouseY)));

        this.addTabs();
    }

    private void addTabs() {
        int tabWidth = this.components.threeTabWidth(this.width);

        // Game tab
        GameTab game = new GameTab();
        if (this.selectedTab == null) this.selectedTab = game;
        TabButton gameTab;
        this.tabs.put(gameTab = this.addButton(new TabButton(this.width / 2 - tabWidth / 2 - tabWidth, 0, tabWidth, 24, new TranslationTextComponent("tab.mellowui.game"), button -> {
            this.tabs.forEach((tab, contents) -> tab.setSelected(false));
            this.setSelectedTab(game.identifier);
        })), game);
        game.openTab(this.identifier, this, gameTab);

        // World tab
        WorldTab world = new WorldTab();
        TabButton worldTab;
        this.tabs.put(worldTab = this.addButton(new TabButton(this.width / 2 - tabWidth / 2, 0, tabWidth, 24, new TranslationTextComponent("tab.mellowui.world"), button -> {
            this.tabs.forEach((tab, contents) -> tab.setSelected(false));
            this.setSelectedTab(world.identifier);
        })), world);
        world.openTab(this.identifier, this, worldTab);

        // More tab
        MoreTab more = new MoreTab();
        TabButton moreTab;
        this.tabs.put(moreTab = this.addButton(new TabButton(this.width / 2 + tabWidth / 2, 0, tabWidth, 24, new TranslationTextComponent("tab.mellowui.more"), button -> {
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
            this.buttons.clear();
            this.children.clear();
            this.identifier = identifier;
            this.init();
        }
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
        if (this.selectedTab != null) this.selectedTab.render(stack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void renderDirtBackground(int vOffset) {
        if (MellowConfigs.CLIENT_CONFIGS.updateScreenBackground.get()) this.components.renderMenuBackground(0, 24, this.width,  this.height, vOffset);
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
            MellowUI.LOGGER.warn("Failed to copy datapack file from {} to {}", path, toPath);
            throw new DatapackException(exception);
        }
    }

    @Nullable
    protected Path getTempDataPackDirectory() {
        if (this.tempDataPackDirectory == null) {
            try {
                this.tempDataPackDirectory = Files.createTempDirectory("mcworld-");
            } catch (IOException ioexception) {
                MellowUI.LOGGER.warn("Failed to create temporary data packs directory", ioexception);
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
                    Stream<Path> stream = Files.walk(this.tempDataPackDirectory);
            ) {
                Path datapacksFolder = save.getLevelPath(FolderName.DATAPACK_DIR);
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
                    MellowUI.LOGGER.warn("Failed to validate datapack", exception);
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
}
