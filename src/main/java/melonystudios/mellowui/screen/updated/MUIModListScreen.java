package melonystudios.mellowui.screen.updated;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import melonystudios.mellowui.config.MellowConfigEntries;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.screen.DisableableImageButton;
import melonystudios.mellowui.screen.forge.MUIModUpdateScreen;
import melonystudios.mellowui.screen.list.MUIModList;
import melonystudios.mellowui.screen.option.ForgeOptionsScreen;
import melonystudios.mellowui.util.GUITextures;
import melonystudios.mellowui.util.ModListSorting;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.ConfirmOpenLinkScreen;
import net.minecraft.client.gui.screen.OptionsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.settings.IteratableOption;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.*;
import net.minecraftforge.client.gui.NotificationModUpdateScreen;
import net.minecraftforge.common.util.Size2i;
import net.minecraftforge.fml.*;
import net.minecraftforge.fml.client.ConfigGuiHandler;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.StringUtils;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.fml.packs.ModFileResourcePack;
import net.minecraftforge.fml.packs.ResourcePackLoader;
import net.minecraftforge.forgespi.language.IModInfo;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.abbreviateMiddle;

public class MUIModListScreen extends Screen {
    private final Screen lastScreen;
    private TextFieldWidget searchField;
    private NotificationModUpdateScreen modUpdateScreen;
    private MUIModList.Mod selectedMod = null;
    private List<ModInfo> mods;
    private final List<ModInfo> unsortedMods;
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
        super(new TranslationTextComponent("fml.menu.mods.title"));
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
                this.selectedMod = list.children().stream().filter(mod -> mod.getModInformation() == this.selectedMod.getModInformation()).findFirst().orElse(null);
                this.updateCache();
            }
            this.sorted = true;
        }
    }

    @Override
    protected void init() {
        // Sort button
        IteratableOption config = MellowConfigEntries.MOD_LIST_SORTING;
        this.addButton(new Button(10, 10, 150, 20, config.getMessage(this.minecraft.options), button -> {
            config.toggle(this.minecraft.options, 1);
            button.setMessage(config.getMessage(this.minecraft.options));
            this.resortMods(MellowConfigs.CLIENT_CONFIGS.modListSorting.get());
        }));
        // Search field
        this.searchField = new TextFieldWidget(this.minecraft.font, 11, 34, 148, 20, new TranslationTextComponent("fml.menu.mods.search"));
        this.searchField.setFocus(false);
        this.searchField.setCanLoseFocus(true);
        this.children.add(this.searchField);

        // Mod link buttons
        boolean maxGUIScale = this.minecraft.getWindow().getGuiScale() == 4;
        int width = maxGUIScale ? 100 : 150;
        int leftOffset = maxGUIScale ? 38 : 0;
        this.addButton(this.websiteButton = new Button(this.width / 2 - (width / 2) - width - 4 + leftOffset, 120, width, 20,
                new TranslationTextComponent("button.mellowui.mod_website"), button -> this.getModWebsite()));
        this.websiteButton.active = false;
        this.addButton(this.issueTrackerButton = new Button(this.width / 2 - (width / 2) + leftOffset, 120, width, 20,
                new TranslationTextComponent("button.mellowui.mod_issue_tracker"), button -> this.getModIssueTracker()));
        this.issueTrackerButton.active = false;
        this.addButton(this.updateModButton = new Button(this.width / 2 + (width / 2) + 4 + leftOffset, 120, width, 20,
                new TranslationTextComponent("button.mellowui.update_mod").withStyle(Style.EMPTY.withColor(Color.fromRgb(0x41F384)).withUnderlined(true)), button -> this.getModUpdateSite()));
        this.updateModButton.active = false;
        this.modUpdateScreen = MUIModUpdateScreen.create(this.minecraft.screen, this.updateModButton);
        this.addButton(this.changelogsButton = new DisableableImageButton(this.width / 2 + 233 + leftOffset, 120, 20, 20, 0, 0, 20,
                GUITextures.CHANGELOGS_BUTTON, 32, 64, button -> this.getModChangelogs(), (button, stack, mouseX, mouseY) ->
                this.renderTooltip(stack, this.minecraft.font.split(new TranslationTextComponent("button.mellowui.changelogs.desc"), 200), mouseX, mouseY), new TranslationTextComponent("button.mellowui.changelogs")));

        // Icons
        this.addButton(this.configButton = new DisableableImageButton(164, 10, 20, 20, 0, 0, 20,
                GUITextures.CONFIG_BUTTON, 32, 64, button -> this.getModConfigScreen(), (button, stack, mouseX, mouseY) ->
                this.renderTooltip(stack, this.minecraft.font.split(new TranslationTextComponent("button.mellowui.configure.desc"), 200), mouseX, mouseY), new TranslationTextComponent("button.mellowui.configure")));
        this.configButton.active = false;
        this.addButton(new DisableableImageButton(164, 34, 20, 20, 0, 0, 20,
                GUITextures.OPEN_MODS_FOLDER_BUTTON, 32, 64, button -> Util.getPlatform().openFile(FMLPaths.MODSDIR.get().toFile()), (button, stack, mouseX, mouseY) ->
                this.renderTooltip(stack, this.minecraft.font.split(new TranslationTextComponent("button.mellowui.open_mods_folder.desc"), 200), mouseX, mouseY), new TranslationTextComponent("button.mellowui.open_mods_folder")));

        // List
        this.list = new MUIModList(this, 174, this.height, 64, this.height - 10, this.font.lineHeight * 2 + 8);
        this.list.setLeftPos(10);
        this.children.add(this.list);

        // Done button
        this.addButton(new Button(this.width / 2 - 100, this.height - 27, 200, 20, DialogTexts.GUI_DONE,
                button -> this.minecraft.setScreen(this.lastScreen)));

        this.updateCache();
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);

        this.renderModInformation(stack);

        this.list.render(stack, mouseX, mouseY, partialTicks);
        this.searchField.render(stack, mouseX, mouseY, partialTicks);
        drawCenteredString(stack, this.font, this.title, this.width / 2, 6, 0xFFFFFF);
        super.render(stack, mouseX, mouseY, partialTicks);
        if (this.selectedMod != null && this.updateModButton.visible) this.modUpdateScreen.render(stack, mouseX, mouseY, partialTicks);
    }

    public void renderModInformation(MatrixStack stack) {
        if (this.minecraft == null) return;
        boolean maxGUIScale = this.minecraft.getWindow().getGuiScale() == 4;
        // Background
        RenderSystem.enableBlend();
        this.minecraft.getTextureManager().bind(this.minecraft.level != null ? GUITextures.INWORLD_MENU_LIST_BACKGROUND : GUITextures.MENU_LIST_BACKGROUND);
        blit(stack, 194, 20, 0, 0, this.width, this.height - 57, 32, 32); // / 2 + 79
        RenderSystem.disableBlend();

        if (this.selectedMod == null) {
            IFormattableTextComponent noModSelected = new TranslationTextComponent("menu.mellowui.no_mod_selected");
            this.font.drawShadow(stack, noModSelected.withStyle(TextFormatting.GRAY), this.width / 2 - (this.font.width(noModSelected) / 2), this.height / 2 - 10, 0xFFFFFF);
        }

        // Logo
        if (this.logoPath != null && this.selectedMod != null) {
            int logoWidth = this.logoDimension.width;
            int logoHeight = this.logoDimension.height;
            int leftOffset = maxGUIScale ? 35 : 0;
            this.minecraft.getTextureManager().bind(this.logoPath);
            RenderSystem.enableBlend();
            RenderSystem.color4f(1, 1, 1, 1);
            GuiUtils.drawInscribedRect(stack, leftOffset, 30, this.width, 50, logoWidth, logoHeight, true, false);
            RenderSystem.disableBlend();
        }

        // Text
        if (this.selectedMod != null) {
            int yOffset = this.logoPath != null ? 100 : 25;
            // Name
            ModInfo info = this.selectedMod.getModInformation();
            stack.pushPose();
            stack.scale(2, 2, 2);
            IFormattableTextComponent modName = new StringTextComponent(info.getDisplayName()).withStyle(TextFormatting.BOLD);
            this.font.drawShadow(stack, modName, 102, yOffset / 2F, 0xFFFFFF);
            stack.popPose();

            // Authors | Mod ID | Version
            IFormattableTextComponent authors = new StringTextComponent(info.getConfigElement("authors").isPresent() ? (String) info.getConfigElement("authors").get() : "null")
                    .withStyle(TextFormatting.WHITE);
            IFormattableTextComponent modID = new StringTextComponent(info.getModId()).withStyle(TextFormatting.WHITE);
            IFormattableTextComponent version = new StringTextComponent(MavenVersionStringHelper.artifactVersionToString(info.getVersion())).withStyle(TextFormatting.WHITE);

            this.font.drawShadow(stack, new TranslationTextComponent("menu.mellowui.mod.mod_id_and_version", modID, version).withStyle(TextFormatting.GRAY),
                    212 + this.font.width(modName) * 2, yOffset, 0xFFFFFF);
            this.font.drawShadow(stack, new TranslationTextComponent("menu.mellowui.mod.authors", authors).withStyle(TextFormatting.GRAY), 212 + this.font.width(modName) * 2,
                    yOffset + 10, 0xFFFFFF);

            // Description, child mods, license and credits
            List<IReorderingProcessor> descLines = Lists.newArrayList();
            int lineWidth = 490;
            if (maxGUIScale) descLines.addAll(this.font.split(new StringTextComponent(abbreviateMiddle(info.getDescription(), "...", 200)), lineWidth));
            else descLines.addAll(this.font.split(new StringTextComponent(info.getDescription()), lineWidth));
            descLines.addAll(this.font.split(new StringTextComponent(" "), lineWidth));
            if (info.getOwningFile() == null || info.getOwningFile().getMods().size() == 1) {
                descLines.addAll(this.font.split(new TranslationTextComponent("menu.mellowui.no_child_mods_found").withStyle(TextFormatting.GRAY), lineWidth));
            } else {
                descLines.addAll(this.font.split(new TranslationTextComponent("menu.mellowui.mod.child_mods", new StringTextComponent(info.getOwningFile().getMods().stream().map(IModInfo::getDisplayName)
                        .collect(Collectors.joining(", "))).withStyle(Style.EMPTY.withBold(false))).withStyle(TextFormatting.BOLD), lineWidth));
            }
            info.getConfigElement("credits").ifPresent(credits -> descLines.addAll(this.font.split(new TranslationTextComponent("menu.mellowui.mod.credits",
                    new StringTextComponent((String) credits).withStyle(Style.EMPTY.withBold(false))).withStyle(TextFormatting.BOLD), lineWidth)));
            descLines.addAll(this.font.split(new TranslationTextComponent("menu.mellowui.mod.license",
                    new StringTextComponent(info.getOwningFile().getLicense()).withStyle(Style.EMPTY.withBold(false))).withStyle(TextFormatting.BOLD), lineWidth));
            descLines.addAll(this.font.split(new TranslationTextComponent("menu.mellowui.mod.state", ModList.get().getModContainerById(info.getModId()).map(ModContainer::getCurrentState)
                    .map(stage -> new TranslationTextComponent("loading_stage.forge." + stage.toString().toLowerCase(Locale.ROOT)).withStyle(Style.EMPTY.withBold(false))).orElse(new TranslationTextComponent(
                            "loading_stage.forge.none"))).withStyle(TextFormatting.BOLD), lineWidth));

            int descYOffset = (int) ((yOffset + 50) * (maxGUIScale ? 1.375F : 1));
            for (IReorderingProcessor processor : descLines) {
                if (maxGUIScale) {
                    stack.pushPose();
                    stack.scale(0.75F, 0.75F, 0.75F);
                    this.font.drawShadow(stack, processor, 270, descYOffset, 0xFFFFFF);
                    descYOffset += this.font.lineHeight + 2;
                    stack.popPose();
                } else {
                    this.font.drawShadow(stack, processor, 204, descYOffset, 0xFFFFFF);
                    descYOffset += this.font.lineHeight + 2;
                }
            }
        }
    }

    public void setSelected(MUIModList.Mod modEntry) {
        this.selectedMod = modEntry == this.selectedMod ? null : modEntry;
        this.updateCache();
    }

    public <T extends ExtendedList.AbstractListEntry<T>> void loadModList(Consumer<T> modAdder, Function<ModInfo, T> newEntry) {
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

        ModInfo info = this.selectedMod.getModInformation();
        if (info.getModId().equals("minecraft")) {
            this.minecraft.setScreen(new OptionsScreen(this, this.minecraft.options));
        } else if (info.getModId().equals("forge")) {
            this.minecraft.setScreen(new ForgeOptionsScreen(this, this.minecraft.options));
        } else {
            try {
                ConfigGuiHandler.getGuiFactoryFor(info).map(func -> func.apply(this.minecraft, this)).ifPresent(newScreen -> this.minecraft.setScreen(newScreen));
            } catch (final Exception exception) {
                LogManager.getLogger().error("There was a critical issue trying to build the config GUI for {}", info.getModId(), exception);
            }
        }
    }

    private void getModWebsite() {
        if (this.selectedMod == null) return;

        ModInfo info = this.selectedMod.getModInformation();
        if (info.getModId().equals("minecraft")) {
            this.openLink("https://minecraft.net", false);
        } else if (info.getModId().equals("forge")) {
            this.openLink("https://files.minecraftforge.net", true);
        } else {
            info.getConfigElement("displayURL").ifPresent(displayURL -> this.openLink((String) displayURL, true));
        }
    }

    private void getModIssueTracker() {
        if (this.selectedMod == null) return;

        ModInfo info = this.selectedMod.getModInformation();
        if (info.getModId().equals("minecraft")) {
            this.openLink("https://aka.ms/snapshotbugs?ref=game", false);
        } else if (info.getModId().equals("forge")) {
            this.openLink("https://github.com/MinecraftForge/MinecraftForge/issues", true);
        } else {
            if (info.getOwningFile().getIssueURL() != null) this.openLink(info.getOwningFile().getIssueURL().toString(), true);
        }
    }

    private void getModChangelogs() {
        if (this.selectedMod == null) return;

        ModInfo info = this.selectedMod.getModInformation();
        if (info.getModId().equals("minecraft")) {
            this.openLink("https://feedback.minecraft.net/hc/en-us/sections/360002267532-Snapshot-Information-and-Changelogs", false);
        } else if (info.getModId().equals("forge")) {
            this.openLink("https://maven.minecraftforge.net/net/minecraftforge/forge/1.16.5-36.2.39/forge-1.16.5-36.2.39-changelog.txt", true);
        } else {
            info.getConfigElement("changelogsURL").ifPresent(changelogsURL -> this.openLink((String) changelogsURL, true));
        }
    }

    public void getModUpdateSite() {
        if (this.selectedMod == null) return;
        VersionChecker.CheckResult checkResult = VersionChecker.getResult(this.selectedMod.getModInformation());
        if (checkResult.url != null) this.openLink(checkResult.url, true);
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

        ModInfo selectedMod = this.selectedMod.getModInformation();
        this.configButton.active = ConfigGuiHandler.getGuiFactoryFor(selectedMod).isPresent() || this.defaultModIDs();
        this.websiteButton.active = this.selectedMod.getModInformation().getConfigElement("displayURL").isPresent() || this.defaultModIDs();
        this.issueTrackerButton.active = selectedMod.getOwningFile().getIssueURL() != null || this.defaultModIDs();
        this.changelogsButton.active = this.changelogsButton.visible = this.selectedMod.getModInformation().getConfigElement("changelogsURL").isPresent() || this.defaultModIDs();
        VersionChecker.CheckResult checkResult = VersionChecker.getResult(selectedMod);
        this.updateModButton.active = this.updateModButton.visible = checkResult.url != null;

        Pair<ResourceLocation, Size2i> logoData = selectedMod.getLogoFile().map(logoFile -> {
            TextureManager manager = this.minecraft.getTextureManager();
            final ModFileResourcePack modFilesPack = ResourcePackLoader.getResourcePackFor(selectedMod.getModId()).orElse(ResourcePackLoader.getResourcePackFor("forge")
                    .orElseThrow(() -> new RuntimeException(new TranslationTextComponent("error.mellowui.cannot_find_forge").getString())));

            try {
                InputStream logoResource = modFilesPack.getRootResource(logoFile);
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
        int width = this.minecraft.getWindow().getGuiScale() == 4 ? 100 : 150;
        int yOffset = this.logoPath != null ? 100 : 25;
        int leftOffset = this.minecraft.getWindow().getGuiScale() == 4 ? 38 : 0;
        this.websiteButton.y = yOffset + 20;
        this.issueTrackerButton.y = yOffset + 20;
        this.updateModButton.y = yOffset + 20;
        this.changelogsButton.y = yOffset + 20;

        // Changelogs
        if (!this.updateModButton.visible) this.changelogsButton.x = this.width / 2 + (width / 2) + 4 + leftOffset;
        else this.changelogsButton.x = this.width / 2 + (width / 2) + width + 8 + leftOffset;
    }

    private void openLink(String url, boolean showWarning) {
        if (this.minecraft != null) this.minecraft.setScreen(new ConfirmOpenLinkScreen(confirmed -> {
            if (confirmed) Util.getPlatform().openUri(url);
            this.minecraft.setScreen(this);
        }, url, !showWarning));
    }

    private boolean defaultModIDs() {
        return this.selectedMod != null && (this.selectedMod.getModInformation().getModId().equals("minecraft") || this.selectedMod.getModInformation().getModId().equals("forge"));
    }
}
