package melonystudios.mellowui.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.config.VanillaConfigEntries;
import melonystudios.mellowui.screen.widget.TabButton;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SettingsScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.OptionsRowList;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;

import static melonystudios.mellowui.config.MellowConfigEntries.*;
import static melonystudios.mellowui.config.VanillaConfigEntries.*;

public class MellowUIOptionsScreen extends SettingsScreen {
    private final RenderComponents components = RenderComponents.INSTANCE;

    // Mellow UI
    public static final List<AbstractOption> MELLOW_UI = Lists.newArrayList(PANORAMA_BOBBING, MONOCHROME_LOADING_SCREEN_COLOR, LEGACY_BUTTON_COLORS, SCROLLING_TEXT, DEFAULT_BACKGROUND, BACKGROUND_SHADERS, BLURRY_CONTAINERS, LOG_GL_ERRORS);
    public static final List<AbstractOption> MAIN_MENU = Lists.newArrayList(SPLASH_TEXT_COLOR, SPLASH_TEXT_POSITION, DISABLE_BRANDING, MAIN_MENU_MOD_BUTTON, LOGO_STYLE);
    public static final List<AbstractOption> INGAME_MENUS = Lists.newArrayList(PAUSE_MENU_MOD_BUTTON, GRADIENT_BACKGROUND);
    public static final List<AbstractOption> MENU_UPDATES = Lists.newArrayList(UPDATED_SCREEN_BACKGROUND, UPDATED_LIST_BACKGROUND, MAIN_MENU_STYLE, UPDATED_CREATE_NEW_WORLD_MENU, UPDATED_PAUSE_MENU, UPDATED_OUT_OF_MEMORY_MENU);
    public static final List<AbstractOption> OPTION_UPDATES = Lists.newArrayList(UPDATED_OPTIONS_MENU, REPLACE_REALMS_NOTIFICATIONS, UPDATED_SKIN_CUSTOMIZATION_MENU, UPDATED_MUSIC_AND_SOUNDS_MENU, UPDATED_VIDEO_SETTINGS_MENU, UPDATED_CONTROLS_MENU, UPDATED_MOUSE_SETTINGS_MENU, UPDATED_CHAT_SETTINGS_MENU, UPDATED_PACK_MENU, UPDATED_ACCESSIBILITY_MENU);
    private OptionsRowList mellowUIList;

    // Mellomedley
    public static final List<AbstractOption> MELLOMEDLEY = Lists.newArrayList(MELLO_SPLASH_TEXT_COLOR, MELLOMEDLEY_MAIN_MENU_MOD_BUTTON);
    private OptionsRowList mellomedleyList;

    // Vanilla
    public static final List<AbstractOption> ACCESSIBILITY = Lists.newArrayList(MONOCHROME_LOADING_SCREEN, PANORAMA_SCROLL_SPEED, HIDE_SPLASH_TEXTS, HIGH_CONTRAST, MENU_BACKGROUND_BLURRINESS);
    public static final List<AbstractOption> MUSIC_AND_SOUNDS = Lists.newArrayList(DIRECTIONAL_AUDIO, SHOW_MUSIC_TOAST);
    private OptionsRowList vanillaList;

    // Forge
    public static final List<AbstractOption> FORGE = Lists.newArrayList(MOD_LIST_SORTING, MOD_LIST_STYLE);
    private OptionsRowList forgeList;

    // Tabs and lists
    private final List<TabButton> tabs = Lists.newArrayList();
    private OptionsRowList currentList = null;

    public MellowUIOptionsScreen(Screen lastScreen, GameSettings options) {
        super(lastScreen, options, new TranslationTextComponent("menu.mellowui.options.title"));
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        super.resize(minecraft, width, height);
        this.tabs.get(0).setSelected(true);
    }

    @Override
    public void tick() {
        super.tick();
        MONOCHROME_LOADING_SCREEN_COLOR.tick();
        SPLASH_TEXT_COLOR.tick();
        MELLO_SPLASH_TEXT_COLOR.tick();
    }

    @Override
    protected void init() {
        // Lists
        this.mellowUIList = new OptionsRowList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
        this.mellowUIList.addBig(PANORAMA_CAMERA_PITCH);
        this.mellowUIList.addSmall(MELLOW_UI.toArray(new AbstractOption[0]));
        this.mellowUIList.addBig(MAIN_MENU_SEPARATOR);
        this.mellowUIList.addSmall(MAIN_MENU.toArray(new AbstractOption[0]));
        this.mellowUIList.addBig(INGAME_MENUS_SEPARATOR);
        this.mellowUIList.addSmall(INGAME_MENUS.toArray(new AbstractOption[0]));
        this.mellowUIList.addBig(MENU_UPDATES_SEPARATOR);
        this.mellowUIList.addSmall(MENU_UPDATES.toArray(new AbstractOption[0]));
        this.mellowUIList.addBig(OPTION_MENU_UPDATES_SEPARATOR);
        this.mellowUIList.addSmall(OPTION_UPDATES.toArray(new AbstractOption[0]));
        this.mellowUIList.setRenderTopAndBottom(false);
        this.mellowUIList.setRenderBackground(false);

        this.mellomedleyList = new OptionsRowList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
        this.mellomedleyList.addSmall(MELLOMEDLEY.toArray(new AbstractOption[0]));
        this.mellomedleyList.setRenderTopAndBottom(false);
        this.mellomedleyList.setRenderBackground(false);

        this.vanillaList = new OptionsRowList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
        this.vanillaList.addBig(ACCESSIBILITY_SEPARATOR);
        this.vanillaList.addSmall(ACCESSIBILITY.toArray(new AbstractOption[0]));
        this.vanillaList.addBig(MUSIC_AND_SOUNDS_SEPARATOR);
        this.vanillaList.addBig(SOUND_DEVICE);
        this.vanillaList.addSmall(MUSIC_AND_SOUNDS.toArray(new AbstractOption[0]));
        this.vanillaList.setRenderTopAndBottom(false);
        this.vanillaList.setRenderBackground(false);

        this.forgeList = new OptionsRowList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
        this.forgeList.addSmall(FORGE.toArray(new AbstractOption[0]));
        this.forgeList.setRenderTopAndBottom(false);
        this.forgeList.setRenderBackground(false);

        int tabWidth = this.components.fourTabWidth(this.width);
        this.currentList = this.mellowUIList;
        this.children.add(this.currentList);

        // Button toggles
        Widget panoramaCameraPitch = this.mellowUIList.findOption(PANORAMA_CAMERA_PITCH);
        if (panoramaCameraPitch != null) panoramaCameraPitch.active = !MellowConfigs.CLIENT_CONFIGS.panoramaBobbing.get();

        Widget updatedCreateNewWorldMenu = this.mellowUIList.findOption(UPDATED_CREATE_NEW_WORLD_MENU);
        if (updatedCreateNewWorldMenu != null) updatedCreateNewWorldMenu.active = false;

        Widget highContrast = this.vanillaList.findOption(VanillaConfigEntries.HIGH_CONTRAST);
        if (highContrast != null && MellowUtils.highContrastUnavailable()) highContrast.active = false;

        // Tabs
        this.tabs.add(this.addButton(new TabButton(this.width / 2 - tabWidth * 2, 10, tabWidth, 24, new TranslationTextComponent("tab.mellowui.mellow_ui"), button -> {
            this.tabs.forEach(tab -> tab.setSelected(false));
            this.selectList(this.mellowUIList);
        })));
        this.tabs.add(this.addButton(new TabButton(this.width / 2 - tabWidth, 10, tabWidth, 24, new TranslationTextComponent("tab.mellowui.mellomedley"), button -> {
            this.tabs.forEach(tab -> tab.setSelected(false));
            this.selectList(this.mellomedleyList);
        })));
        this.tabs.add(this.addButton(new TabButton(this.width / 2, 10, tabWidth, 24, new TranslationTextComponent("tab.mellowui.vanilla"), button -> {
            this.tabs.forEach(tab -> tab.setSelected(false));
            this.selectList(this.vanillaList);
        })));
        this.tabs.add(this.addButton(new TabButton(this.width / 2 + tabWidth, 10, tabWidth, 24, new TranslationTextComponent("tab.mellowui.forge"), button -> {
            this.tabs.forEach(tab -> tab.setSelected(false));
            this.selectList(this.forgeList);
        })));

        // Done button
        this.addButton(new Button(this.width / 2 - 100, this.height - 25, 200, 20, DialogTexts.GUI_DONE,
                button -> this.minecraft.setScreen(this.lastScreen)));

        this.tabs.get(0).setSelected(true);
    }

    private void selectList(OptionsRowList list) {
        this.children.remove(this.currentList);
        this.currentList = list;
        this.children.add(this.currentList);
        this.currentList.setScrollAmount(0);
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);

        if (this.currentList != null) {
            if (!MellowConfigs.CLIENT_CONFIGS.updateListBackground.get()) {
                this.components.enableScissor(this.currentList.getLeft(), this.currentList.getTop() + 2, this.currentList.getRight(), this.currentList.getBottom());
                this.currentList.render(stack, mouseX, mouseY, partialTicks);
                this.components.disableScissor();
            } else {
                this.components.renderTabHeaderBackground(0, 0, this.width, 34);
                this.currentList.render(stack, mouseX, mouseY, partialTicks);
            }
            this.components.renderListSeparators(this.currentList, this.width, 4, this.components.fourTabWidth(this.width));
        }

        drawCenteredString(stack, this.font, this.title, this.width / 2, MellowUtils.TABBED_TITLE_HEIGHT, 0xFFFFFF);
        super.render(stack, mouseX, mouseY, partialTicks);
        List<IReorderingProcessor> processors = tooltipAt(this.currentList, mouseX, mouseY);
        if (processors != null) this.renderTooltip(stack, processors, mouseX, mouseY);
    }

    @Override
    public void renderDirtBackground(int vOffset) {
        if (MellowConfigs.CLIENT_CONFIGS.updateScreenBackground.get()) this.components.renderMenuBackground(0, 34, this.width,  this.height, vOffset);
        else super.renderDirtBackground(vOffset);
    }
}
