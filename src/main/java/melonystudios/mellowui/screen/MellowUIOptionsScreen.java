package melonystudios.mellowui.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.config.ForgeConfigEntries;
import melonystudios.mellowui.config.MellomedleyConfigEntries;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.config.VanillaConfigEntries;
import melonystudios.mellowui.config.option.OpenMenuOption;
import melonystudios.mellowui.screen.widget.TabButton;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

import static melonystudios.mellowui.config.MellowConfigEntries.*;
import static melonystudios.mellowui.config.VanillaConfigEntries.*;

public class MellowUIOptionsScreen extends OptionsSubScreen {
    public final OpenMenuOption customization = new OpenMenuOption("button.mellowui.customize", new MellowCustomizationScreen(this, Minecraft.getInstance().options));
    private final RenderComponents components = RenderComponents.INSTANCE;

    // Mellow UI
    public static final List<Option> MELLOW_UI = Lists.newArrayList(PANORAMA_BOBBING, MONOCHROME_LOADING_SCREEN_COLOR, LEGACY_BUTTON_COLORS, SCROLLING_TEXT, REPLACE_REALMS_NOTIFICATIONS, DEFAULT_BACKGROUND, CULL_OVERSIZED_ITEMS, BACKGROUND_SHADERS, BLURRY_CONTAINERS, LOG_GL_ERRORS);
    public static final List<Option> MAIN_MENU = Lists.newArrayList(SPLASH_TEXT_COLOR, SPLASH_TEXT_POSITION, DISABLE_BRANDING, MAIN_MENU_MOD_BUTTON);
    public static final List<Option> INGAME_MENUS = Lists.newArrayList(PAUSE_MENU_MOD_BUTTON, GRADIENT_BACKGROUND);
    private OptionsList mellowUIList;

    // Mellomedley
    public static final List<Option> MELLOMEDLEY = Lists.newArrayList(MellomedleyConfigEntries.SPLASH_TEXT_COLOR, MellomedleyConfigEntries.MAIN_MENU_MOD_BUTTON, MellomedleyConfigEntries.MELLOMEDLEY_VERSION);
    private OptionsList mellomedleyList;

    // Vanilla
    public static final List<Option> ACCESSIBILITY = Lists.newArrayList(HIGH_CONTRAST, MENU_BACKGROUND_BLURRINESS, PANORAMA_SCROLL_SPEED, HIDE_SPLASH_TEXTS);
    public static final List<Option> MUSIC_AND_SOUNDS = Lists.newArrayList(UI_VOLUME, DIRECTIONAL_AUDIO, SHOW_MUSIC_TOAST);
    private OptionsList vanillaList;

    // Forge
    public static final List<Option> FORGE = Lists.newArrayList(MOD_LIST_STYLE, ForgeConfigEntries.MOD_LIST_SORTING);
    private OptionsList forgeList;

    // Tabs and lists
    private final List<TabButton> tabs = Lists.newArrayList();
    private OptionsList activeList = null;

    public MellowUIOptionsScreen(Screen lastScreen, Options options) {
        super(lastScreen, options, new TranslatableComponent("menu.mellowui.options.title"));
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
        MellomedleyConfigEntries.SPLASH_TEXT_COLOR.tick();
    }

    @Override
    protected void init() {
        // Lists
        this.mellowUIList = new OptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
        this.mellowUIList.addBig(PANORAMA_CAMERA_PITCH);
        this.mellowUIList.addBig(this.customization);
        this.mellowUIList.addSmall(MELLOW_UI.toArray(new Option[0]));
        this.mellowUIList.addBig(MAIN_MENU_SEPARATOR);
        this.mellowUIList.addSmall(MAIN_MENU.toArray(new Option[0]));
        this.mellowUIList.addBig(INGAME_MENUS_SEPARATOR);
        this.mellowUIList.addSmall(INGAME_MENUS.toArray(new Option[0]));
        this.mellowUIList.setRenderTopAndBottom(false);
        this.mellowUIList.setRenderBackground(false);

        this.mellomedleyList = new OptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
        this.mellomedleyList.addSmall(MELLOMEDLEY.toArray(new Option[0]));
        this.mellomedleyList.setRenderTopAndBottom(false);
        this.mellomedleyList.setRenderBackground(false);

        this.vanillaList = new OptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
        this.vanillaList.addSmall(ONBOARD_ACCESSIBILITY, null);
        this.vanillaList.addBig(ACCESSIBILITY_SEPARATOR);
        this.vanillaList.addSmall(ACCESSIBILITY.toArray(new Option[0]));
        this.vanillaList.addBig(MUSIC_AND_SOUNDS_SEPARATOR);
        this.vanillaList.addSmall(MUSIC_AND_SOUNDS.toArray(new Option[0]));
        this.vanillaList.setRenderTopAndBottom(false);
        this.vanillaList.setRenderBackground(false);

        this.forgeList = new OptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
        this.forgeList.addSmall(FORGE.toArray(new Option[0]));
        this.forgeList.setRenderTopAndBottom(false);
        this.forgeList.setRenderBackground(false);

        int tabWidth = this.components.fourTabWidth(this.width);
        this.activeList = this.mellowUIList;
        this.addWidget(this.activeList);
        this.tabs.clear();

        // Button toggles
        AbstractWidget panoramaCameraPitch = this.mellowUIList.findOption(PANORAMA_CAMERA_PITCH);
        if (panoramaCameraPitch != null) panoramaCameraPitch.active = !MellowConfigs.CLIENT_CONFIGS.panoramaBobbing.get();

        AbstractWidget highContrast = this.vanillaList.findOption(VanillaConfigEntries.HIGH_CONTRAST);
        if (highContrast != null && MellowUtils.highContrastUnavailable()) highContrast.active = false;

        // Tabs
        this.tabs.add(this.addRenderableWidget(new TabButton(this.width / 2 - tabWidth * 2, 10, tabWidth, 24, new TranslatableComponent("tab.mellowui.mellow_ui"), button -> {
            this.tabs.forEach(tab -> tab.setSelected(false));
            this.selectList(this.mellowUIList);
        })));
        this.tabs.add(this.addRenderableWidget(new TabButton(this.width / 2 - tabWidth, 10, tabWidth, 24, new TranslatableComponent("tab.mellowui.mellomedley"), button -> {
            this.tabs.forEach(tab -> tab.setSelected(false));
            this.selectList(this.mellomedleyList);
        })));
        this.tabs.add(this.addRenderableWidget(new TabButton(this.width / 2, 10, tabWidth, 24, new TranslatableComponent("tab.mellowui.vanilla"), button -> {
            this.tabs.forEach(tab -> tab.setSelected(false));
            this.selectList(this.vanillaList);
        })));
        this.tabs.add(this.addRenderableWidget(new TabButton(this.width / 2 + tabWidth, 10, tabWidth, 24, new TranslatableComponent("tab.mellowui.forge"), button -> {
            this.tabs.forEach(tab -> tab.setSelected(false));
            this.selectList(this.forgeList);
        })));

        // Done button
        this.addRenderableWidget(new Button(this.width / 2 - 100, this.height - 25, 200, 20, CommonComponents.GUI_DONE,
                button -> this.minecraft.setScreen(this.lastScreen)));

        this.tabs.get(0).setSelected(true);
    }

    private void selectList(OptionsList list) {
        this.removeWidget(this.mellowUIList);
        this.removeWidget(this.mellomedleyList);
        this.removeWidget(this.vanillaList);
        this.removeWidget(this.forgeList);
        if (list != null) {
            this.addWidget(list);
            this.activeList = list;
        }
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
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

        drawCenteredString(stack, this.font, this.title, this.width / 2, MellowUtils.TABBED_TITLE_HEIGHT, 0xFFFFFF);
        super.render(stack, mouseX, mouseY, partialTicks);
        List<FormattedCharSequence> processors = tooltipAt(this.activeList, mouseX, mouseY);
        if (!processors.isEmpty()) this.renderTooltip(stack, processors, mouseX, mouseY);
    }

    @Override
    public void renderDirtBackground(int vOffset) {
        if (MellowConfigs.CLIENT_CONFIGS.screenBackgroundStyle.get()) this.components.renderMenuBackground(0, 34, this.width, this.height, vOffset);
        else super.renderDirtBackground(vOffset);
    }
}
