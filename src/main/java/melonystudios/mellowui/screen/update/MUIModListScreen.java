package melonystudios.mellowui.screen.update;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.config.MellowConfigEntries;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.config.WidgetConfigs;
import melonystudios.mellowui.config.option.IterableOption;
import melonystudios.mellowui.screen.RenderComponents;
import melonystudios.mellowui.screen.widget.ImageSetButton;
import melonystudios.mellowui.screen.list.MUIModList;
import melonystudios.mellowui.screen.widget.ModButton;
import melonystudios.mellowui.util.GUITextures;
import melonystudios.mellowui.util.MellowUtils;
import melonystudios.mellowui.config.type.ModListSorting;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.client.ConfigGuiHandler;
import net.minecraftforge.client.gui.GuiUtils;
import net.minecraftforge.common.util.MavenVersionStringHelper;
import net.minecraftforge.common.util.Size2i;
import net.minecraftforge.fml.*;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.StringUtils;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.resource.PathResourcePack;
import net.minecraftforge.resource.ResourcePackLoader;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.abbreviateMiddle;

public class MUIModListScreen extends Screen {
    private final RenderComponents components = RenderComponents.INSTANCE;
    private final Screen lastScreen;
    private EditBox searchField;
    private MUIModList.Mod selectedMod = null;
    private List<IModInfo> mods;
    private final List<IModInfo> unsortedMods;
    private String lastFilterText = "";
    private boolean sorted = false;
    private ModListSorting sorting = MellowConfigs.CLIENT_CONFIGS.modListSorting.get();
    private Size2i logoDimension = new Size2i(0, 0);
    private ResourceLocation logoPath;
    private MUIModList list;
    private Button configButton;
    private Button issueTrackerButton;
    private Button websiteButton;
    private Button updateModButton;
    private Button changelogsButton;

    public MUIModListScreen(Screen lastScreen) {
        super(new TranslatableComponent("menu.mellowui.mods.title", ModList.get().getMods().size()));
        this.lastScreen = lastScreen;
        this.mods = Collections.unmodifiableList(ModList.get().getMods());
        this.unsortedMods = this.mods;
    }

    @Override
    public void tick() {
        this.searchField.tick();
        if (!this.searchField.getValue().equals(this.lastFilterText)) {
            this.reloadMods();
            this.sorted = false;
        }

        if (!this.sorted) {
            this.reloadMods();
            this.mods.sort(this.sorting);
            this.list.refreshModList();
            if (this.selectedMod != null) {
                this.selectedMod = this.list.children().stream().filter(mod -> mod.getModInformation() == this.selectedMod.getModInformation()).findFirst().orElse(null);
                this.updateCache();
            }
            this.sorted = true;
        }
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) this.minecraft.setScreen(this.lastScreen);
    }

    @Override
    protected void init() {
        // Sort button
        IterableOption config = MellowConfigEntries.MOD_LIST_SORTING;
        this.addRenderableWidget(new Button(10, 14, 100, 20, config.getMessage(this.minecraft.options), button -> {
            config.toggle(this.minecraft.options, 1);
            button.setMessage(config.getMessage(this.minecraft.options));
            this.resortMods(MellowConfigs.CLIENT_CONFIGS.modListSorting.get());
        }));
        // Configure button
        this.addRenderableWidget(this.configButton = new ImageSetButton(114, 14, 20, 20, GUITextures.CONFIGURE_SET,
                button -> this.getModConfigScreen(), (button, stack, mouseX, mouseY) ->
                this.components.renderTooltip(this, button, new TranslatableComponent("button.mellowui.configure"), mouseX, mouseY), new TranslatableComponent("button.mellowui.configure")));
        this.configButton.active = false;

        // Search field
        this.searchField = new EditBox(this.minecraft.font, 11, 39, 98, 18, new TranslatableComponent("fml.menu.mods.search"));
        this.searchField.setFocus(false);
        this.searchField.setCanLoseFocus(true);
        this.addWidget(this.searchField);

        // Open mods folder button
        this.addRenderableWidget(new ImageSetButton(114, 38, 20, 20, GUITextures.OPEN_FOLDER_SET,
                button -> Util.getPlatform().openFile(FMLPaths.MODSDIR.get().toFile()), (button, stack, mouseX, mouseY) ->
                this.components.renderTooltip(this, button, new TranslatableComponent("button.mellowui.open_mods_folder"), mouseX, mouseY), new TranslatableComponent("button.mellowui.open_mods_folder")));

        boolean maxGUIScale = this.minecraft.getWindow().getScreenWidth() <= 1366 || this.minecraft.getWindow().getGuiScale() == 4;
        int width = maxGUIScale ? 100 : 150;
        int buttonOffset = width + 4;
        Component updateAvailable = new TranslatableComponent("button.mellowui.update_available").withStyle(style -> style.withColor(MellowUtils.highContrastEnabled() ?
                WidgetConfigs.WIDGET_CONFIGS.highContrastUpdateAvailableColor.get() : WidgetConfigs.WIDGET_CONFIGS.defaultUpdateAvailableColor.get()).withUnderlined(true));

        // Mod link buttons
        this.addRenderableWidget(this.websiteButton = new Button(140, 120, width, 20,
                new TranslatableComponent("button.mellowui.website"), button -> this.getModWebsite()));
        this.websiteButton.active = false;
        this.addRenderableWidget(this.issueTrackerButton = new Button(140 + buttonOffset, 120, width, 20,
                new TranslatableComponent("button.mellowui.report_issues"), button -> this.getModIssueTracker()));
        this.issueTrackerButton.active = false;
        this.addRenderableWidget(this.updateModButton = new ModButton(140 + buttonOffset * 2, 120, width, 20, updateAvailable, button -> this.getModUpdateSite()).renderOnCorner(maxGUIScale));
        this.updateModButton.active = false;
        this.addRenderableWidget(this.changelogsButton = new ImageSetButton(this.width - 24, this.height - 24, 20, 20, GUITextures.CHANGELOGS_SET,
                button -> this.getModChangelogs(), (button, stack, mouseX, mouseY) ->
                this.components.renderTooltip(this, button, new TranslatableComponent("button.mellowui.changelogs"), mouseX, mouseY), new TranslatableComponent("button.mellowui.changelogs")));

        // Mods list
        this.list = new MUIModList(this, 124, this.height, 56, this.height - 25, this.font.lineHeight * 2 + 8);
        this.list.setLeftPos(10);
        this.list.setRenderTopAndBottom(false);
        this.list.setRenderBackground(false);
        this.addWidget(this.list);

        if (this.selectedMod != null) {
            this.list.setSelected(this.selectedMod);
            this.list.setFocused(this.selectedMod);
            this.list.centerScrollOn(this.selectedMod);
        }

        // Done button
        this.addRenderableWidget(new Button(10, this.height - 25, 124, 20, CommonComponents.GUI_DONE,
                button -> this.minecraft.setScreen(this.lastScreen)));

        this.updateCache();
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        this.renderModInformation(stack);

        this.list.render(stack, mouseX, mouseY, partialTicks);
        this.searchField.render(stack, mouseX, mouseY, partialTicks);
        drawCenteredString(stack, this.font, this.title, this.width / 2, 6, 0xFFFFFF);
        super.render(stack, mouseX, mouseY, partialTicks);
    }

    public void renderModInformation(PoseStack stack) {
        if (this.minecraft == null) return;
        boolean maxGUIScale = this.minecraft.getWindow().getScreenWidth() <= 1366 || this.minecraft.getWindow().getGuiScale() == 4;

        if (this.selectedMod == null) {
            MutableComponent noModSelected = new TranslatableComponent("menu.mellowui.mods.no_mod_selected");
            this.font.drawShadow(stack, noModSelected.withStyle(ChatFormatting.GRAY), this.width / 2 - (this.font.width(noModSelected) / 2), this.height / 2 - 10, 0xFFFFFF);
        }

        // Logo
        if (this.logoPath != null && this.selectedMod != null) {
            int logoWidth = this.logoDimension.width;
            int logoHeight = this.logoDimension.height;
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, this.logoPath);
            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(1, 1, 1, 1);
            GuiUtils.drawInscribedRect(stack, 142, 30, this.width, 50, logoWidth, logoHeight, false, false);
            RenderSystem.disableBlend();
        }

        // Text
        if (this.selectedMod != null) {
            int yOffset = this.logoPath != null ? 100 : 25;
            // Name
            IModInfo info = this.selectedMod.getModInformation();
            stack.pushPose();
            stack.scale(2, 2, 2);
            MutableComponent modName = new TextComponent(info.getDisplayName()).withStyle(ChatFormatting.BOLD);
            this.font.drawShadow(stack, modName, 70, yOffset / 2F, 0xFFFFFF);
            stack.popPose();

            // Authors | Mod ID | Version
            MutableComponent authors = info.getConfig().getConfigElement("authors").isPresent() ? new TextComponent(info.getConfig().getConfigElement("authors").get().toString()).withStyle(ChatFormatting.WHITE) :
                    new TranslatableComponent("menu.mellowui.mods.authors.not_available");
            MutableComponent modID = new TextComponent(info.getModId()).withStyle(ChatFormatting.WHITE);
            MutableComponent version = new TextComponent(MavenVersionStringHelper.artifactVersionToString(info.getVersion())).withStyle(ChatFormatting.WHITE);

            this.font.drawShadow(stack, new TranslatableComponent("menu.mellowui.mods.mod_id_and_version", modID, version).withStyle(ChatFormatting.GRAY),
                    145 + this.font.width(modName) * 2, yOffset, 0xFFFFFF);
            this.font.drawShadow(stack, new TranslatableComponent("menu.mellowui.mods.authors", authors).withStyle(ChatFormatting.GRAY), 145 + this.font.width(modName) * 2,
                    yOffset + 10, 0xFFFFFF);

            // Description, child mods, license and credits
            List<FormattedCharSequence> descLines = Lists.newArrayList();
            int lineWidth = this.width - 150;
            if (maxGUIScale) descLines.addAll(this.font.split(new TextComponent(abbreviateMiddle(info.getDescription(), new TranslatableComponent("menu.mellowui.ellipsis").getString(), 200)), lineWidth));
            else descLines.addAll(this.font.split(new TextComponent(info.getDescription()), lineWidth));
            descLines.addAll(this.font.split(new TextComponent(" "), lineWidth));
            if (info.getOwningFile() == null || info.getOwningFile().getMods().size() == 1) {
                descLines.addAll(this.font.split(new TranslatableComponent("menu.mellowui.mods.no_child_mods_found").withStyle(ChatFormatting.GRAY), lineWidth));
            } else {
                descLines.addAll(this.font.split(new TranslatableComponent("menu.mellowui.mods.child_mods", new TextComponent(info.getOwningFile().getMods().stream().map(IModInfo::getDisplayName)
                        .collect(Collectors.joining(new TranslatableComponent("menu.mellowui.mods.delimiter").getString()))).withStyle(Style.EMPTY.withBold(false))).withStyle(ChatFormatting.BOLD), lineWidth));
            }
            info.getConfig().getConfigElement("credits").ifPresent(credits -> descLines.addAll(this.font.split(new TranslatableComponent("menu.mellowui.mods.credits",
                    new TextComponent((String) credits).withStyle(Style.EMPTY.withBold(false))).withStyle(ChatFormatting.BOLD), lineWidth)));
            descLines.addAll(this.font.split(new TranslatableComponent("menu.mellowui.mods.license",
                    new TextComponent(info.getOwningFile().getLicense()).withStyle(Style.EMPTY.withBold(false))).withStyle(ChatFormatting.BOLD), lineWidth));
            descLines.addAll(this.font.split(new TranslatableComponent("menu.mellowui.mods.state", ModList.get().getModContainerById(info.getModId()).map(ModContainer::getCurrentState)
                    .map(stage -> new TranslatableComponent("loading_stage.forge." + stage.toString().toLowerCase(Locale.ROOT)).withStyle(Style.EMPTY.withBold(false))).orElse(new TranslatableComponent(
                            "loading_stage.forge.none"))).withStyle(ChatFormatting.BOLD), lineWidth));

            int descYOffset = yOffset + 50;
            for (FormattedCharSequence processor : descLines) {
                this.font.drawShadow(stack, processor, 142, descYOffset, 0xFFFFFF);
                descYOffset += this.font.lineHeight + 2;
            }
        }
    }

    public void setSelected(MUIModList.Mod modEntry) {
        this.selectedMod = modEntry;
        this.updateCache();
    }

    public <T extends ObjectSelectionList.Entry<T>> void loadModList(Consumer<T> modAdder, Function<IModInfo, T> newEntry) {
        this.mods.forEach(mod -> modAdder.accept(newEntry.apply(mod)));
    }

    private void reloadMods() {
        this.mods = this.unsortedMods.stream().filter(mod -> StringUtils.toLowerCase(mod.getDisplayName()).contains(StringUtils.toLowerCase(this.searchField.getValue()))).collect(Collectors.toList());
        this.lastFilterText = this.searchField.getValue();
    }

    private void resortMods(ModListSorting newSort) {
        this.sorting = newSort;
        this.sorted = false;
    }

    private void getModConfigScreen() {
        if (this.selectedMod == null) return;

        IModInfo info = this.selectedMod.getModInformation();
        try {
            ConfigGuiHandler.getGuiFactoryFor(info).map(func -> func.apply(this.minecraft, this)).ifPresent(newScreen -> this.minecraft.setScreen(newScreen));
        } catch (final Exception exception) {
            MellowUI.LOGGER.error(new TranslatableComponent("error.mellowui.broken_config_screen", info.getModId()).getString(), exception);
        }
    }

    private void getModWebsite() {
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

    private void getModIssueTracker() {
        if (this.selectedMod == null) return;

        IModInfo info = this.selectedMod.getModInformation();
        if (info.getModId().equals("minecraft")) {
            MellowUtils.openLink(this, "https://aka.ms/snapshotbugs?ref=game", false);
        } else if (info.getModId().equals("forge")) {
            MellowUtils.openLink(this, "https://github.com/MinecraftForge/MinecraftForge/issues", true);
        } else {
            if (info instanceof ModFileInfo && ((ModFileInfo) info).getIssueURL() != null) {
                MellowUtils.openLink(this, ((ModFileInfo) info).getIssueURL().toString(), true);
            }
        }
    }

    private void getModChangelogs() {
        if (this.selectedMod == null) return;

        IModInfo info = this.selectedMod.getModInformation();
        if (info.getModId().equals("minecraft")) {
            MellowUtils.openLink(this, "https://feedback.minecraft.net/hc/en-us/sections/360002267532-Snapshot-Information-and-Changelogs", false);
        } else if (info.getModId().equals("forge")) {
            MellowUtils.openLink(this, "https://maven.minecraftforge.net/net/minecraftforge/forge/1.16.5-36.2.39/forge-1.16.5-36.2.39-changelog.txt", true);
        } else {
            info.getConfig().getConfigElement("changelogsURL").ifPresent(changelogsURL -> MellowUtils.openLink(this, (String) changelogsURL, true));
        }
    }

    public void getModUpdateSite() {
        if (this.selectedMod == null) return;
        VersionChecker.CheckResult checkResult = VersionChecker.getResult(this.selectedMod.getModInformation());
        if (checkResult.url() != null) MellowUtils.openLink(this, checkResult.url(), true);
    }

    private void updateCache() {
        if (this.selectedMod == null) {
            this.configButton.active = false;
            this.websiteButton.visible = false;
            this.issueTrackerButton.visible = false;
            this.updateModButton.visible = false;
            this.changelogsButton.visible = false;
            return;
        } else {
            this.websiteButton.visible = true;
            this.issueTrackerButton.visible = true;
            this.changelogsButton.visible = true;
        }

        IModInfo selectedMod = this.selectedMod.getModInformation();
        this.configButton.active = ConfigGuiHandler.getGuiFactoryFor(selectedMod).isPresent();
        this.websiteButton.active = this.selectedMod.getModInformation().getConfig().getConfigElement("displayURL").isPresent() || this.defaultModIDs();
        this.issueTrackerButton.active = (selectedMod instanceof ModFileInfo && ((ModFileInfo) selectedMod).getIssueURL() != null) || this.defaultModIDs();
        this.changelogsButton.active = this.changelogsButton.visible = this.selectedMod.getModInformation().getConfig().getConfigElement("changelogsURL").isPresent() || this.defaultModIDs();
        VersionChecker.CheckResult checkResult = VersionChecker.getResult(selectedMod);
        this.updateModButton.active = this.updateModButton.visible = checkResult.url() != null;

        Pair<ResourceLocation, Size2i> logoData = selectedMod.getLogoFile().map(logoFile -> {
            TextureManager manager = this.minecraft.getTextureManager();
            final PathResourcePack pack = ResourcePackLoader.getPackFor(selectedMod.getModId()).orElse(ResourcePackLoader.getPackFor("forge")
                    .orElseThrow(() -> new RuntimeException(new TranslatableComponent("error.mellowui.cannot_find_forge").getString())));

            try {
                InputStream logoResource = pack.getRootResource(logoFile);
                NativeImage logo = NativeImage.read(logoResource);

                return Pair.of(manager.register("modlogo", new DynamicTexture(logo) {
                    @Override
                    public void upload() {
                        this.bind();
                        if (this.getPixels() != null) {
                            NativeImage pixels = this.getPixels();
                            // Use custom "blur" value which controls texture filtering (nearest-neighbor vs linear).
                            this.getPixels().upload(0, 0, 0, 0, 0, pixels.getWidth(), pixels.getHeight(), selectedMod.getLogoBlur(), false, false, false);
                        }
                    }
                }), new Size2i(logo.getWidth(), logo.getHeight()));
            } catch (IOException ignored) { }
            return Pair.<ResourceLocation, Size2i>of(null, new Size2i(0, 0));
        }).orElse(Pair.of(null, new Size2i(0, 0)));

        this.logoPath = logoData.getLeft();
        this.logoDimension = logoData.getRight();

        // reposition buttons based on whether there's a logo ~isa 30-3-25
        int yOffset = this.logoPath != null ? 100 : 25;
        this.websiteButton.y = yOffset + 20;
        this.issueTrackerButton.y = yOffset + 20;
        this.updateModButton.y = yOffset + 20;
    }

    private boolean defaultModIDs() {
        return this.selectedMod != null && (this.selectedMod.getModInformation().getModId().equals("minecraft") || this.selectedMod.getModInformation().getModId().equals("forge"));
    }
}
