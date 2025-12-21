package melonystudios.mellowui.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.config.WidgetConfigEntries;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.element.widget.TabButton;
import melonystudios.mellowui.screen.list.PanoramaList;
import melonystudios.mellowui.screen.list.ThemeList;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SettingsScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.AbstractList;
import net.minecraft.client.gui.widget.list.OptionsRowList;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import java.util.List;

import static melonystudios.mellowui.config.MellowConfigEntries.*;

public class MellowCustomizationScreen extends SettingsScreen {
    public static final List<AbstractOption> BACKGROUNDS = Lists.newArrayList(SCREEN_BACKGROUND_STYLE, LIST_BACKGROUND_STYLE, PANEL_BACKGROUND_STYLE);
    public static final List<AbstractOption> SCREENS = Lists.newArrayList(TITLE_STYLE, LOGO_STYLE, CREATE_NEW_WORLD_STYLE, WORLD_LOADING_STYLE, PAUSE_STYLE, STATISTICS_STYLE, OUT_OF_MEMORY_STYLE);
    public static final List<AbstractOption> OPTIONS = Lists.newArrayList(SKIN_CUSTOMIZATION_STYLE, MUSIC_AND_SOUNDS_STYLE, VIDEO_SETTINGS_STYLE, CONTROLS_STYLE, MOUSE_SETTINGS_STYLE, CHAT_SETTINGS_STYLE, PACK_LIST_STYLE, ACCESSIBILITY_SETTINGS_STYLE);
    public static final List<AbstractOption> FORGE = Lists.newArrayList(UPDATE_AVAILABLE_ICON_STYLE, MOD_LIST_STYLE, LOADING_ERRORS_STYLE);
    private final RenderComponents components = RenderComponents.INSTANCE;
    private TextFieldWidget searchBox;
    public String search = "";

    // Tabs
    private final List<TabButton> tabs = Lists.newArrayList();
    private String selectedTab = "styles";
    private OptionsRowList styles;
    private ThemeList themes;
    private PanoramaList panoramas;
    @Nullable
    private AbstractList<?> activeList = null;

    public MellowCustomizationScreen(Screen lastScreen, GameSettings options) {
        super(lastScreen, options, TextComponents.buildScreenSubtitle(MellowUI.MOD_ID, MellowUI.MOD_NAME, new TranslationTextComponent("menu.mellowui.customization.title")));
    }

    @Override
    public void tick() {
        this.searchBox.tick();
    }

    @Override
    protected void init() {
        // Lists
        this.styles = new OptionsRowList(this.minecraft, this.width, this.height, 22, this.height - 32, 25);
        this.styles.addBig(WidgetConfigEntries.BACKGROUNDS_SEPARATOR);
        this.styles.addSmall(BACKGROUNDS.toArray(new AbstractOption[0]));
        this.styles.addBig(SCREENS_SEPARATOR);
        this.styles.addSmall(SCREENS.toArray(new AbstractOption[0]));
        this.styles.addBig(OPTIONS_SEPARATOR);
        this.styles.addBig(OPTIONS_STYLE);
        this.styles.addSmall(OPTIONS.toArray(new AbstractOption[0]));
        this.styles.addBig(FORGE_SEPARATOR);
        this.styles.addSmall(FORGE.toArray(new AbstractOption[0]));
        this.styles.setRenderBackground(false);
        this.styles.setRenderTopAndBottom(false);

        this.themes = new ThemeList(this.minecraft, this);
        this.themes.setRenderBackground(false);
        this.themes.setRenderTopAndBottom(false);
        this.themes.setSelected(this.themes.children().stream().filter(entry -> entry.assetID.toString().equals(MellowConfigs.CLIENT_CONFIGS.selectedTheme.get())).findFirst().orElse(null));

        this.panoramas = new PanoramaList(this.minecraft, this);
        this.panoramas.setRenderBackground(false);
        this.panoramas.setRenderTopAndBottom(false);
        this.panoramas.setSelected(this.panoramas.children().stream().filter(entry -> entry.assetID.toString().equals(MellowConfigs.CLIENT_CONFIGS.selectedPanorama.get())).findFirst().orElse(null));

        this.activeList = this.styles;
        this.children.add(this.activeList);

        // Button toggles
        Widget createNewWorldStyle = this.styles.findOption(CREATE_NEW_WORLD_STYLE);
        if (createNewWorldStyle != null) createNewWorldStyle.active = false;

        // Tabs
        int tabWidth = this.components.threeTabWidth(this.width);
        this.tabs.clear();

        // Styles
        this.tabs.add(this.addButton(new TabButton(this.width / 2 - tabWidth / 2 - tabWidth, 0, tabWidth, 24, "styles", new TranslationTextComponent("tab.mellowui.styles"), button -> {
            this.tabs.forEach(tab -> tab.setSelected(false));
            this.selectedTab = ((TabButton) button).tabName();
            this.selectList(this.styles);
        })));

        // Themes
        this.tabs.add(this.addButton(new TabButton(this.width / 2 - tabWidth / 2, 0, tabWidth, 24, "themes", new TranslationTextComponent("tab.mellowui.themes"), button -> {
            this.tabs.forEach(tab -> tab.setSelected(false));
            this.selectedTab = ((TabButton) button).tabName();
            this.themes.refreshList(this.search);
            this.selectList(this.themes);
        })));

        // Panoramas
        this.tabs.add(this.addButton(new TabButton(this.width / 2 + tabWidth / 2, 0, tabWidth, 24, "panoramas", new TranslationTextComponent("tab.mellowui.panoramas"), button -> {
            this.tabs.forEach(tab -> tab.setSelected(false));
            this.selectedTab = ((TabButton) button).tabName();
            this.panoramas.refreshList(this.search);
            this.selectList(this.panoramas);
        })));

        // Search box
        this.searchBox = new TextFieldWidget(this.font, this.width / 2 - 155, this.height - 25, 150, 20, TextComponents.searchText());
        this.searchBox.setFocus(false);
        this.searchBox.setCanLoseFocus(true);
        this.searchBox.setValue(this.search);
        this.searchBox.setResponder(value -> {
            this.search = value.trim();
            this.themes.refreshList(value);
            this.panoramas.refreshList(value);
        });
        this.addWidget(this.searchBox);

        // Done button
        this.addButton(new Button(this.width / 2 + 5, this.height - 25, 150, 20, DialogTexts.GUI_DONE,
                button -> this.minecraft.setScreen(this.lastScreen)));

        this.tabs.stream().filter(tab -> tab.tabName().equals(this.selectedTab)).findFirst().ifPresent(tab -> {
            tab.setSelected(true);
            this.selectList(this.byName(this.selectedTab));
        });
    }

    private AbstractList<?> byName(String selectedTab) {
        switch (selectedTab) {
            case "themes": return this.themes;
            case "panoramas": return this.panoramas;
            default: return this.styles;
        }
    }

    private void selectList(AbstractList<?> list) {
        this.children.remove(this.styles);
        this.children.remove(this.themes);
        this.children.remove(this.panoramas);
        if (list != null) {
            this.children.add(0, list);
            this.activeList = list;
        }
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        if (!MellowConfigs.CLIENT_CONFIGS.listBackgroundStyle.get() && this.activeList != null) {
            this.components.enableScissor(this.activeList.getLeft(), this.activeList.getTop(), this.activeList.getRight(), this.activeList.getBottom());
            this.activeList.render(stack, mouseX, mouseY, partialTicks);
            this.components.disableScissor();
        } else if (this.activeList != null) {
            this.components.renderTabHeaderBackground(0, 0, this.width, 24);
            this.activeList.render(stack, mouseX, mouseY, partialTicks);
        }
        this.components.renderListSeparators(this.width, 0, this.height - 32, 22, 3, this.components.threeTabWidth(this.width));
        this.searchBox.render(stack, mouseX, mouseY, partialTicks);
        this.components.renderTextBoxSuggestion(this.searchBox, this.searchBox.getMessage());
        super.render(stack, mouseX, mouseY, partialTicks);

        if (this.activeList instanceof OptionsRowList) {
            List<IReorderingProcessor> tooltip = tooltipAt((OptionsRowList) this.activeList, mouseX, mouseY);
            if (tooltip != null) this.renderTooltip(stack, tooltip, mouseX, mouseY);
        }
    }

    @Override
    public void renderDirtBackground(int vOffset) {
        if (MellowConfigs.CLIENT_CONFIGS.screenBackgroundStyle.get()) this.components.renderMenuBackground(0, 24, this.width, this.height, vOffset);
        else super.renderDirtBackground(vOffset);
    }
}
