package melonystudios.mellowui.screen.update;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.config.MellowConfigEntries;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.config.WidgetConfigs;
import melonystudios.mellowui.config.option.IterableOption;
import melonystudios.mellowui.config.type.ModListSorting;
import melonystudios.mellowui.screen.Alignment;
import melonystudios.mellowui.screen.RenderComponents;
import melonystudios.mellowui.screen.list.MellowModList;
import melonystudios.mellowui.screen.panel.*;
import melonystudios.mellowui.screen.widget.ImageSetButton;
import melonystudios.mellowui.screen.widget.ModButton;
import melonystudios.mellowui.util.GUITextures;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.ConfigGuiHandler;
import net.minecraftforge.common.util.Size2i;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.StringUtils;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.versions.forge.ForgeVersion;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static melonystudios.mellowui.util.MellowUtils.withColor;

public class MellowModListScreen extends Screen {
    private final RenderComponents components = RenderComponents.INSTANCE;
    private final Screen lastScreen;
    private EditBox searchBox;
    private MellowModList modList;
    private Panel panel;

    // Mods
    private List<IModInfo> mods;
    private final List<IModInfo> unsortedMods;
    private MellowModList.Mod selectedMod = null;

    // Sorting
    private ModListSorting sortingMethod = MellowConfigs.CLIENT_CONFIGS.modListSorting.get();
    private static String LAST_SEARCH = "";
    private boolean sorted = false;

    // Mod data
    private Pair<ResourceLocation, Size2i> logoData;

    // Buttons
    private Button configureButton;
    private Button websiteButton;
    private Button issueTrackerButton;
    private Button changelogsButton;
    private Button updateAvailableButton;

    public MellowModListScreen(Screen lastScreen) {
        super(new TranslatableComponent("menu.mellowui.mods.title", ModList.get().size()));
        this.lastScreen = lastScreen;
        this.mods = Collections.unmodifiableList(ModList.get().getMods());
        this.unsortedMods = this.mods;
    }

    @Override
    public void tick() {
        this.searchBox.tick();
        if (!this.searchBox.getValue().equals(LAST_SEARCH)) {
            this.reloadMods();
            this.sorted = false;
        }

        if (!this.sorted) {
            this.reloadMods();
            this.mods.sort(this.sortingMethod);
            this.modList.refreshModList();
            if (this.selectedMod != null) {
                this.selectedMod = this.modList.children().stream().filter(mod -> mod.getModInformation() == this.selectedMod.getModInformation()).findFirst().orElse(null);
                this.updateCache();
            }
            if (this.selectedMod == null) this.panel.clearEntries();
            this.sorted = true;
        }
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) this.minecraft.setScreen(this.lastScreen);
    }

    @Override
    protected void init() {
        // Mod list
        this.modList = new MellowModList(this, this.width / 4, this.height, 32, this.height - 32, this.font.lineHeight * 2 + 8);
        this.modList.setRenderTopAndBottom(false);
        this.modList.setRenderBackground(false);
        this.addWidget(this.modList);

        // Mod information panel
        this.panel = new Panel(this.modList.getRight() + 2, 34, (this.modList.getRight() / 2) + (this.width / 4) * 3, this.height - 66, this, new TranslatableComponent("panel.mellowui.mod_information"));
        this.addWidget(this.panel);
        this.selectMod(this.selectedMod);
        this.panel.init();

        // Search box
        this.searchBox = new EditBox(this.font, this.width / 2 - 100, 17, 200, 12, MellowUtils.SEARCH_TEXT);
        this.searchBox.setFocus(false);
        this.searchBox.setCanLoseFocus(true);
        this.searchBox.setValue(LAST_SEARCH);
        this.searchBox.setResponder(value -> this.modList.setScrollAmount(!value.isEmpty() ? 0 : this.modList.getScrollAmount()));
        this.addWidget(this.searchBox);

        int xPos = (this.modList.getRight() / 2) + (this.width / 4) * 3;
        int yPos = 40;
        int buttonWidth = xPos / 4;

        // Configure
        this.addRenderableWidget(this.configureButton = new ImageSetButton(xPos - buttonWidth / 2, yPos, buttonWidth, 20, GUITextures.CONFIGURE_SET,
                button -> this.openConfigScreen(), new TranslatableComponent("button.mellowui.configure")).renderText(true).alignment(Alignment.RIGHT));
        yPos += 30;

        // Website
        this.addRenderableWidget(this.websiteButton = new Button(xPos - buttonWidth / 2, yPos, buttonWidth, 20, new TranslatableComponent("button.mellowui.website"),
                button -> this.openWebsite()));
        yPos += 30;

        // Issue tracker
        this.addRenderableWidget(this.issueTrackerButton = new Button(xPos - buttonWidth / 2, yPos, buttonWidth, 20, new TranslatableComponent("button.mellowui.report_issues"),
                button -> this.openIssueTracker()));
        yPos += 30;

        // Changelogs
        this.addRenderableWidget(this.changelogsButton = new ImageSetButton(xPos - buttonWidth / 2, yPos, buttonWidth, 20, GUITextures.CHANGELOGS_SET,
                button -> this.openChangelogs(), new TranslatableComponent("button.mellowui.changelogs")).renderText(true).alignment(Alignment.RIGHT));
        yPos += 30;

        // Update available
        Component updateAvailable = new TranslatableComponent("button.mellowui.update_available").withStyle(withColor(MellowUtils.highContrastEnabled() ?
                WidgetConfigs.WIDGET_CONFIGS.highContrastUpdateAvailableColor.get() : WidgetConfigs.WIDGET_CONFIGS.defaultUpdateAvailableColor.get()).withUnderlined(true));
        this.addRenderableWidget(this.updateAvailableButton = new ModButton(xPos - buttonWidth / 2, yPos, buttonWidth, 20, updateAvailable,
                button -> this.openUpdateCheckerHomepage()).renderOnCorner(buttonWidth < 110));

        // Sort
        IterableOption sortingConfig = MellowConfigEntries.MOD_LIST_SORTING;
        this.addRenderableWidget(new Button(this.width / 2 - 165, this.height - 25, 150, 20, sortingConfig.getMessage(this.minecraft.options), button -> {
            sortingConfig.toggle(this.minecraft.options, 1);
            button.setMessage(sortingConfig.getMessage(this.minecraft.options));
            this.resortMods(MellowConfigs.CLIENT_CONFIGS.modListSorting.get());
        }));

        // Open mods folder
        this.addRenderableWidget(new ImageSetButton(this.width / 2 - 10, this.height - 25, 20, 20, GUITextures.OPEN_FOLDER_SET,
                button -> Util.getPlatform().openFile(FMLPaths.MODSDIR.get().toFile()), (button, stack, mouseX, mouseY) ->
                this.components.renderTooltip(this, button, new TranslatableComponent("button.mellowui.open_mods_folder"), mouseX, mouseY), new TranslatableComponent("button.mellowui.open_mods_folder")));

        // Done button
        this.addRenderableWidget(new Button(this.width / 2 + 15, this.height - 25, 150, 20, CommonComponents.GUI_DONE,
                button -> this.minecraft.setScreen(this.lastScreen)));

        if (this.selectedMod != null) this.modList.centerScrollOn(this.selectedMod);
        this.updateCache();
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        this.modList.render(stack, mouseX, mouseY, partialTicks);
        this.components.renderListSeparators(this.width, 0, this.height - 32, 32, 0, 0);

        int rightSeparatorX = (this.width / 4) * 3;
        this.components.enableScissor(this.modList.getRight(), 34, rightSeparatorX + 2, this.height - 32);
        this.components.renderVerticalSeparator(this.modList.getRight(), 34, this.height - 32, false);
        this.components.renderVerticalSeparator(rightSeparatorX, 34, this.height - 32, true);
        this.panel.render(stack, mouseX, mouseY, partialTicks);
        this.components.disableScissor();

        this.searchBox.render(stack, mouseX, mouseY, partialTicks);
        this.components.renderTextBoxSuggestion(this.searchBox, this.searchBox.getMessage());
        drawCenteredString(stack, this.font, this.title, this.width / 2, 6, 0xFFFFFF);
        drawCenteredString(stack, this.font, new TranslatableComponent("menu.mellowui.mods.links"), (this.modList.getRight() / 2) + rightSeparatorX, MellowUtils.DEFAULT_TITLE_HEIGHT, 0xFFFFFF);
        if (this.selectedMod == null) {
            drawCenteredString(stack, this.font, new TranslatableComponent("menu.mellowui.mods.no_mod_selected").withStyle(ChatFormatting.GRAY), this.width / 2, this.height / 2 - 5, 0xFFFFFF);
        }
        super.render(stack, mouseX, mouseY, partialTicks);
    }

    public void selectMod(MellowModList.Mod mod) {
        this.panel.clearEntries();
        this.panel.setScrollAmount(0);
        if (mod == null) return;

        IModInfo info = mod.getModInformation();
        int accentColor = this.getAccentColor(info.getModId());
        ImagePanelEntry imageEntry = new ImagePanelEntry(this.panel, info);
        this.setModLogo(imageEntry.loadModLogo());
        if (this.logoData.getFirst() != null) this.panel.addEntry(imageEntry);

        // more "important" information like the mod's name, authors and description
        // version, authors and mod id
        this.panel.addEntry(new InformationPanelEntry(this.panel, info, accentColor));
        this.panel.addEntry(new SeparatorPanelEntry(this.panel, this.font.lineHeight));
        // name
        this.panel.addEntry(new TextPanelEntry(this.panel, Alignment.CENTER, new TranslatableComponent("menu.mellowui.mods.name.extended",
                info.getDisplayName()).withStyle(withColor(accentColor).withBold(true))));
        // description
        this.panel.addEntry(new TextPanelEntry(this.panel, new TextComponent(info.getDescription())));

        // less important info, like credits, licenses and more
        this.panel.addEntry(new SeparatorPanelEntry(this.panel, this.font.lineHeight));

        // child mods
        if (info.getOwningFile() == null || info.getOwningFile().getMods().size() == 1) {
            // "no child mods found"
            this.panel.addEntry(new TextPanelEntry(this.panel, new TranslatableComponent("menu.mellowui.mods.no_child_mods_found").withStyle(ChatFormatting.GRAY)));
        } else {
            // "child mods: x, y, z"
            this.panel.addEntry(new TextPanelEntry(this.panel, new TranslatableComponent("menu.mellowui.mods.child_mods",
                    new TextComponent(this.getChildMods(info)).withStyle(withColor(0xFFFFFF).withBold(false)))
                    .withStyle(withColor(accentColor).withBold(true))));
        }

        // credits
        info.getConfig().getConfigElement("credits").ifPresent(credits -> {
            this.panel.addEntry(new TextPanelEntry(this.panel, new TranslatableComponent("menu.mellowui.mods.credits",
                    new TextComponent(credits.toString()).withStyle(withColor(0xFFFFFF).withBold(false)))
                    .withStyle(withColor(accentColor).withBold(true))));
        });
        // license
        this.panel.addEntry(new TextPanelEntry(this.panel, new TranslatableComponent("menu.mellowui.mods.license",
                new TextComponent(info.getOwningFile().getLicense()).withStyle(withColor(0xFFFFFF).withBold(false)))
                .withStyle(withColor(accentColor).withBold(true))));
        // state (done)
        this.panel.addEntry(new TextPanelEntry(this.panel, new TranslatableComponent("menu.mellowui.mods.state",
                this.getLoadingStage(info).withStyle(withColor(0xFFFFFF).withBold(false)))
                .withStyle(withColor(accentColor).withBold(true))));
    }

    private MutableComponent getLoadingStage(IModInfo info) {
        return ModList.get().getModContainerById(info.getModId())
                .map(ModContainer::getCurrentState)
                .map(stage -> new TranslatableComponent("loading_stage.forge." + stage.toString().toLowerCase(Locale.ROOT)))
                .orElse(new TranslatableComponent("loading_stage.forge.none"));
    }

    private String getChildMods(IModInfo info) {
        return info.getOwningFile().getMods().stream()
                .map(IModInfo::getDisplayName)
                .collect(Collectors.joining(new TranslatableComponent("menu.mellowui.delimiter").getString()));
    }

    public int getAccentColor(String modID) {
        if (modID.equals("mellowui")) {
            return 0x955AE0;
        } else if (modID.equals("forge")) {
            return 0xDFA76A;
        } else {
            return MellowUtils.highContrastEnabled() ? WidgetConfigs.WIDGET_CONFIGS.highContrastFlairAccentColor.get() : WidgetConfigs.WIDGET_CONFIGS.defaultFlairAccentColor.get();
        }
    }

    public void setSelected(MellowModList.Mod mod) {
        this.selectedMod = mod;
        this.selectMod(mod);
        this.updateCache();
    }

    public Pair<ResourceLocation, Size2i> getLogoData() {
        return this.logoData;
    }

    public void setModLogo(Pair<ResourceLocation, Size2i> logoData) {
        this.logoData = logoData;
    }

    public boolean isLogoLoaded() {
        return this.logoData != null;
    }

    public <T extends ObjectSelectionList.Entry<T>> void loadMods(Consumer<T> modAdder, Function<IModInfo, T> newEntry) {
        this.mods.forEach(mod -> modAdder.accept(newEntry.apply(mod)));
    }

    private void reloadMods() {
        this.mods = this.unsortedMods.stream().filter(mod -> StringUtils.toLowerCase(mod.getDisplayName()).contains(StringUtils.toLowerCase(this.searchBox.getValue()))).collect(Collectors.toList());
        LAST_SEARCH = this.searchBox.getValue();
    }

    private void resortMods(ModListSorting method) {
        this.sortingMethod = method;
        this.sorted = false;
    }

    private boolean defaultModIDs() {
        return this.selectedMod != null && (this.selectedMod.getModInformation().getModId().equals("minecraft") || this.selectedMod.getModInformation().getModId().equals("forge"));
    }

    private void updateCache() {
        this.logoData = null;
        if (this.selectedMod == null) {
            this.configureButton.visible = false;
            this.websiteButton.visible = false;
            this.issueTrackerButton.visible = false;
            this.changelogsButton.visible = false;
            this.updateAvailableButton.visible = false;
            return;
        } else {
            this.configureButton.visible = true;
            this.websiteButton.visible = true;
            this.issueTrackerButton.visible = true;
            this.changelogsButton.visible = true;
        }

        this.selectMod(this.selectedMod);
        IModInfo info = this.selectedMod.getModInformation();

        this.configureButton.active = ConfigGuiHandler.getGuiFactoryFor(info).isPresent();
        this.websiteButton.active = info.getConfig().getConfigElement("displayURL").isPresent() || this.defaultModIDs();
        this.issueTrackerButton.active = info.getOwningFile().getConfig().getConfigElement("issueTrackerURL").isPresent() || this.defaultModIDs();
        this.changelogsButton.active = info.getConfig().getConfigElement("changelogsURL").isPresent() || this.defaultModIDs();
        this.updateAvailableButton.active = this.updateAvailableButton.visible = VersionChecker.getResult(info).status().shouldDraw();
    }

    private void openConfigScreen() {
        if (this.selectedMod == null) return;
        IModInfo info = this.selectedMod.getModInformation();

        try {
            ConfigGuiHandler.getGuiFactoryFor(info).map(func -> func.apply(this.minecraft, this)).ifPresent(newScreen -> this.minecraft.setScreen(newScreen));
        } catch (final Exception exception) {
            MellowUI.LOGGER.error(new TranslatableComponent("error.mellowui.broken_config_screen", info.getModId()).getString(), exception);
        }
    }

    // LINKS

    private void openWebsite() {
        if (this.selectedMod == null) return;
        IModInfo info = this.selectedMod.getModInformation();

        if (info.getModId().equals("minecraft")) {
            MellowUtils.openLink(this, "https://minecraft.net", false);
        } else if (info.getModId().equals("forge")) {
            MellowUtils.openLink(this, "https://files.minecraftforge.net", true);
        } else {
            info.getConfig().getConfigElement("displayURL").ifPresent(displayURL -> MellowUtils.openLink(this, (String) displayURL, true));
        }
    }

    private void openIssueTracker() {
        if (this.selectedMod == null) return;
        IModInfo info = this.selectedMod.getModInformation();

        if (info.getModId().equals("minecraft")) {
            MellowUtils.openLink(this, "https://aka.ms/snapshotbugs?ref=game", false);
        } else if (info.getModId().equals("forge")) {
            MellowUtils.openLink(this, "https://github.com/MinecraftForge/MinecraftForge/issues", true);
        } else {
            info.getOwningFile().getConfig().getConfigElement("issueTrackerURL").ifPresent(issueTrackerURL -> MellowUtils.openLink(this, (String) issueTrackerURL, true));
        }
    }

    private void openChangelogs() {
        if (this.selectedMod == null) return;
        IModInfo info = this.selectedMod.getModInformation();

        if (info.getModId().equals("minecraft")) {
            MellowUtils.openLink(this, "https://feedback.minecraft.net/hc/en-us/sections/360002267532-Snapshot-Information-and-Changelogs", false);
        } else if (info.getModId().equals("forge")) {
            String minecraftVersion = SharedConstants.getCurrentVersion().getName();
            String forgeVersion = ForgeVersion.getVersion();
            MellowUtils.openLink(this, String.format("https://maven.minecraftforge.net/net/minecraftforge/forge/%1$s-%2$s/forge-%1$s-%2$s-changelog.txt", minecraftVersion, forgeVersion), true);
        } else {
            info.getConfig().getConfigElement("changelogsURL").ifPresent(changelogsURL -> MellowUtils.openLink(this, (String) changelogsURL, true));
        }
    }

    public void openUpdateCheckerHomepage() {
        if (this.selectedMod == null) return;
        VersionChecker.CheckResult checkResult = VersionChecker.getResult(this.selectedMod.getModInformation());
        if (checkResult.url() != null) MellowUtils.openLink(this, checkResult.url(), true);
    }
}
