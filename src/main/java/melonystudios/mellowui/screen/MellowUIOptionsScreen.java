package melonystudios.mellowui.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.config.*;
import melonystudios.mellowui.config.option.OpenMenuOption;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.element.widget.TabButton;
import melonystudios.mellowui.element.widget.WidgetComponents;
import melonystudios.mellowui.util.Alignment;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SettingsScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.list.OptionsRowList;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;

import static melonystudios.mellowui.config.MellowConfigEntries.*;
import static melonystudios.mellowui.config.VanillaConfigEntries.*;
import static melonystudios.mellowui.config.WidgetConfigEntries.*;

public class MellowUIOptionsScreen extends SettingsScreen {
    public final OpenMenuOption customization = new OpenMenuOption("button.mellowui.customize", new TranslationTextComponent("button.mellowui.customize.tooltip"),
            new MellowCustomizationScreen(this, Minecraft.getInstance().options));
    public final OpenMenuOption colorOptions = new OpenMenuOption("button.mellowui.color_options", new TranslationTextComponent("button.mellowui.color_options.tooltip"),
            new ColorOptionsScreen(this, Minecraft.getInstance().options));
    private final RenderComponents components = RenderComponents.INSTANCE;

    // Mellow UI
    public static final List<AbstractOption> BACKGROUNDS = Lists.newArrayList(PANORAMA_BOBBING, DEFAULT_BACKGROUND, GRADIENT_BACKGROUND, BACKGROUND_SHADERS, BLURRY_CONTAINERS, FADING_BLUR);
    public static final List<AbstractOption> MENU_UPDATES = Lists.newArrayList(SPLASH_TEXT_POSITION, REPLACE_REALMS_NOTIFICATIONS, MAIN_MENU_MOD_BUTTON, PAUSE_MENU_MOD_BUTTON);
    public static final List<AbstractOption> MISCELLANEOUS = Lists.newArrayList(CULL_OVERSIZED_ITEMS, LOG_GL_ERRORS);
    public static final List<AbstractOption> WIDGETS_SMALL = Lists.newArrayList(LEGACY_BUTTON_COLORS, SCROLLING_TEXT);
    public static final List<AbstractOption> WIDGETS_BIG = Lists.newArrayList(BUTTON_TEXT_PADDING, EDIT_BUTTON_TEXT_PADDING, TAB_TEXT_PADDING, STRING_WIDGET_TEXT_PADDING, MOD_ENTRY_TEXT_PADDING);
    private OptionsRowList mellowUIList;

    // Mellomedley
    public static final List<AbstractOption> MELLOMEDLEY = Lists.newArrayList(MellomedleyConfigEntries.MAIN_MENU_MOD_BUTTON, MellomedleyConfigEntries.MELLOMEDLEY_VERSION);
    private OptionsRowList mellomedleyList;

    // Vanilla
    public static final List<AbstractOption> ACCESSIBILITY = Lists.newArrayList(HIGH_CONTRAST, MENU_BACKGROUND_BLURRINESS, MONOCHROME_LOADING_SCREEN, PANORAMA_SCROLL_SPEED, HIDE_SPLASH_TEXTS);
    public static final List<AbstractOption> MUSIC_AND_SOUNDS = Lists.newArrayList(UI_VOLUME, DIRECTIONAL_AUDIO, MUSIC_TOAST);
    public static final List<AbstractOption> MOUSE_SETTINGS = Lists.newArrayList(ALLOW_CURSOR_CHANGES);
    private OptionsRowList vanillaList;

    // Forge
    public static final List<AbstractOption> FORGE = Lists.newArrayList(BRANDING_LINES, ForgeConfigEntries.MOD_LIST_SORTING);
    private OptionsRowList forgeList;

    // Tabs and lists
    private final List<TabButton> tabs = Lists.newArrayList();
    private String selectedTab = "mellow_ui";
    private OptionsRowList activeList = null;

    public MellowUIOptionsScreen(Screen lastScreen, GameSettings options) {
        super(lastScreen, options, TextComponents.buildScreenTitle(MellowUI.MOD_ID, MellowUI.MOD_NAME));
    }

    @Override
    protected void init() {
        WidgetComponents components = WidgetComponents.components(this, this::addButton);

        // Lists
        this.mellowUIList = components.optionsList(34, this.height - 33);
        this.mellowUIList.addSmall(this.customization, this.colorOptions);
        this.mellowUIList.addBig(BACKGROUNDS_SEPARATOR);
        this.mellowUIList.addBig(PANORAMA_CAMERA_PITCH);
        this.mellowUIList.addSmall(BACKGROUNDS.toArray(new AbstractOption[0]));
        this.mellowUIList.addBig(MENU_UPDATES_SEPARATOR);
        this.mellowUIList.addSmall(MENU_UPDATES.toArray(new AbstractOption[0]));
        this.mellowUIList.addBig(MISCELLANEOUS_SEPARATOR);
        this.mellowUIList.addSmall(MISCELLANEOUS.toArray(new AbstractOption[0]));
        this.mellowUIList.addBig(WIDGETS_SEPARATOR);
        this.mellowUIList.addSmall(WIDGETS_SMALL.toArray(new AbstractOption[0]));
        for (AbstractOption option : WIDGETS_BIG) this.mellowUIList.addBig(option);
        this.mellowUIList.setRenderTopAndBottom(false);
        this.mellowUIList.setRenderBackground(false);

        this.mellomedleyList = components.optionsList(34, this.height - 33);
        for (AbstractOption option : MELLOMEDLEY) this.mellomedleyList.addBig(option);
        this.mellomedleyList.setRenderTopAndBottom(false);
        this.mellomedleyList.setRenderBackground(false);

        this.vanillaList = components.optionsList(34, this.height - 33);
        this.vanillaList.addSmall(ONBOARD_ACCESSIBILITY, null);
        this.vanillaList.addBig(ACCESSIBILITY_SEPARATOR);
        this.vanillaList.addSmall(ACCESSIBILITY.toArray(new AbstractOption[0]));
        this.vanillaList.addBig(MUSIC_AND_SOUNDS_SEPARATOR);
        this.vanillaList.addBig(SOUND_DEVICE);
        this.vanillaList.addSmall(MUSIC_AND_SOUNDS.toArray(new AbstractOption[0]));
        this.vanillaList.addBig(MOUSE_SETTINGS_SEPARATOR);
        this.vanillaList.addSmall(MOUSE_SETTINGS.toArray(new AbstractOption[0]));
        this.vanillaList.setRenderTopAndBottom(false);
        this.vanillaList.setRenderBackground(false);

        this.forgeList = components.optionsList(34, this.height - 33);
        this.forgeList.addSmall(FORGE.toArray(new AbstractOption[0]));
        this.forgeList.setRenderTopAndBottom(false);
        this.forgeList.setRenderBackground(false);

        int tabWidth = this.components.fourTabWidth(this.width);
        this.activeList = this.mellowUIList;
        this.children.add(this.activeList);
        this.tabs.clear();

        // Button toggles
        Widget panoramaCameraPitch = this.mellowUIList.findOption(PANORAMA_CAMERA_PITCH);
        if (panoramaCameraPitch != null) panoramaCameraPitch.active = !MellowConfigs.CLIENT_CONFIGS.panoramaBobbing.get();

        Widget cullOversizedItems = this.mellowUIList.findOption(CULL_OVERSIZED_ITEMS);
        if (cullOversizedItems != null) cullOversizedItems.active = false;

        Widget highContrast = this.vanillaList.findOption(HIGH_CONTRAST);
        if (highContrast != null && MellowUtils.highContrastUnavailable()) highContrast.active = false;

        // Tabs
        this.tabs.add(this.addButton(new TabButton(this.width / 2 - tabWidth * 2, 10, tabWidth, 24, "mellow_ui", new TranslationTextComponent("tab.mellowui.mellow_ui"), button -> {
            this.tabs.forEach(tab -> tab.setSelected(false));
            this.selectedTab = ((TabButton) button).tabName();
            this.selectList(this.mellowUIList);
        })));
        this.tabs.add(this.addButton(new TabButton(this.width / 2 - tabWidth, 10, tabWidth, 24, "mellomedley", new TranslationTextComponent("tab.mellowui.mellomedley"), button -> {
            this.tabs.forEach(tab -> tab.setSelected(false));
            this.selectedTab = ((TabButton) button).tabName();
            this.selectList(this.mellomedleyList);
        })));
        this.tabs.add(this.addButton(new TabButton(this.width / 2, 10, tabWidth, 24, "vanilla", new TranslationTextComponent("tab.mellowui.vanilla"), button -> {
            this.tabs.forEach(tab -> tab.setSelected(false));
            this.selectedTab = ((TabButton) button).tabName();
            this.selectList(this.vanillaList);
        })));
        this.tabs.add(this.addButton(new TabButton(this.width / 2 + tabWidth, 10, tabWidth, 24, "forge", new TranslationTextComponent("tab.mellowui.forge"), button -> {
            this.tabs.forEach(tab -> tab.setSelected(false));
            this.selectedTab = ((TabButton) button).tabName();
            this.selectList(this.forgeList);
        })));

        // Done button
        components.done(Alignment.CENTER);

        this.tabs.stream().filter(tab -> tab.tabName().equals(this.selectedTab)).findFirst().ifPresent(tab -> {
            tab.setSelected(true);
            this.selectList(this.byName(this.selectedTab));
        });
    }

    private OptionsRowList byName(String selectedTab) {
        switch (selectedTab) {
            case "mellomedley": return this.mellomedleyList;
            case "forge": return this.forgeList;
            case "vanilla": return this.vanillaList;
            default: return this.mellowUIList;
        }
    }

    private void selectList(OptionsRowList list) {
        this.children.remove(this.mellowUIList);
        this.children.remove(this.mellomedleyList);
        this.children.remove(this.vanillaList);
        this.children.remove(this.forgeList);
        if (list != null) {
            this.children.add(0, list);
            this.activeList = list;
        }
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);

        if (this.activeList != null) {
            if (!MellowConfigs.CLIENT_CONFIGS.listBackgroundStyle.get()) {
                this.components.enableScissor(this.activeList.getLeft(), this.activeList.getTop() + 2, this.activeList.getRight(), this.activeList.getBottom());
                this.activeList.render(stack, mouseX, mouseY, partialTicks);
                this.components.disableScissor();
            } else {
                this.components.renderTabHeaderBackground(0, 0, this.width, 34);
                this.activeList.render(stack, mouseX, mouseY, partialTicks);
            }
            this.components.renderListSeparators(this.activeList, this.width, 4, this.components.fourTabWidth(this.width));
        }

        this.components.drawTitle(this.title, this.width, RenderComponents.TABBED_TITLE_HEIGHT);
        super.render(stack, mouseX, mouseY, partialTicks);
        List<IReorderingProcessor> tooltip = tooltipAt(this.activeList, mouseX, mouseY);
        if (tooltip != null) this.renderTooltip(stack, tooltip, mouseX, mouseY);
    }

    @Override
    public void renderDirtBackground(int vOffset) {
        if (MellowConfigs.CLIENT_CONFIGS.screenBackgroundStyle.get()) this.components.renderMenuBackground(0, 34, this.width, this.height, vOffset);
        else super.renderDirtBackground(vOffset);
    }
}
