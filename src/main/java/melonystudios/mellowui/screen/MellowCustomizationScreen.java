package melonystudios.mellowui.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.screen.list.PanoramaList;
import melonystudios.mellowui.screen.list.ThemeList;
import melonystudios.mellowui.util.text.TextComponents;
import melonystudios.mellowui.widget.TabButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static melonystudios.mellowui.config.MellowConfigEntries.*;

public class MellowCustomizationScreen extends OptionsSubScreen {
    public static final List<Option> STYLES = Lists.newArrayList(SCREEN_BACKGROUND_STYLE, LIST_BACKGROUND_STYLE, LOGO_STYLE, TITLE_STYLE, CREATE_NEW_WORLD_STYLE, PAUSE_STYLE, OPTIONS_STYLE, ONLINE_OPTIONS_STYLE, SKIN_CUSTOMIZATION_STYLE, MUSIC_AND_SOUNDS_STYLE, VIDEO_SETTINGS_STYLE, CONTROLS_STYLE, MOUSE_SETTINGS_STYLE, CHAT_SETTINGS_STYLE, PACK_LIST_STYLE, ACCESSIBILITY_SETTINGS_STYLE, OUT_OF_MEMORY_STYLE, STATISTICS_STYLE, MOD_LIST_STYLE);
    private final RenderComponents components = RenderComponents.INSTANCE;

    // Tabs
    private final List<TabButton> tabs = Lists.newArrayList();
    private OptionsList styles;
    private ThemeList themes;
    private PanoramaList panoramas;
    @Nullable
    private AbstractSelectionList<?> activeList = null;

    public MellowCustomizationScreen(Screen lastScreen, Options options) {
        super(lastScreen, options, TextComponents.buildScreenSubtitle(MellowUI.MOD_ID, MellowUI.MOD_NAME, new TranslatableComponent("menu.mellowui.customization.title")));
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        super.resize(minecraft, width, height);
        this.tabs.get(0).setSelected(true);
    }

    @Override
    protected void init() {
        // Lists
        this.styles = new OptionsList(this.minecraft, this.width, this.height, 22, this.height - 32, 25);
        this.styles.addBig(STYLES_SEPARATOR);
        this.styles.addSmall(STYLES.toArray(new Option[0]));
        this.styles.setRenderBackground(false);
        this.styles.setRenderTopAndBottom(false);

        this.themes = new ThemeList(this.minecraft, this);
        this.themes.setRenderBackground(false);
        this.themes.setRenderTopAndBottom(false);

        this.panoramas = new PanoramaList(this.minecraft, this);
        this.panoramas.setRenderBackground(false);
        this.panoramas.setRenderTopAndBottom(false);
        this.panoramas.setSelected(this.panoramas.children().stream().filter(entry -> entry.location().equals(MellowConfigs.CLIENT_CONFIGS.selectedPanorama.get())).findFirst().orElse(null));

        int tabWidth = this.components.threeTabWidth(this.width);
        this.activeList = this.styles;
        this.addWidget(this.activeList);
        this.tabs.clear();

        // Button toggles
        AbstractWidget createNewWorldStyle = this.styles.findOption(CREATE_NEW_WORLD_STYLE);
        if (createNewWorldStyle != null) createNewWorldStyle.active = false;

        // Styles
        this.tabs.add(this.addRenderableWidget(new TabButton(this.width / 2 - tabWidth / 2 - tabWidth, 0, tabWidth, 24, new TranslatableComponent("tab.mellowui.styles"), button -> {
            this.tabs.forEach(tab -> tab.setSelected(false));
            this.selectList(this.styles);
        })));

        // Themes
        this.tabs.add(this.addRenderableWidget(new TabButton(this.width / 2 - tabWidth / 2, 0, tabWidth, 24, new TranslatableComponent("tab.mellowui.themes"), button -> {
            this.tabs.forEach(tab -> tab.setSelected(false));
            this.selectList(this.themes);
        })));

        // Panoramas
        this.tabs.add(this.addRenderableWidget(new TabButton(this.width / 2 + tabWidth / 2, 0, tabWidth, 24, new TranslatableComponent("tab.mellowui.panoramas"), button -> {
            this.tabs.forEach(tab -> tab.setSelected(false));
            this.selectList(this.panoramas);
        })));

        // Done button
        this.addRenderableWidget(new Button(this.width / 2 - 100, this.height - 25, 200, 20, CommonComponents.GUI_DONE,
                button -> this.minecraft.setScreen(this.lastScreen)));

        this.tabs.get(0).setSelected(true);
    }

    private void selectList(AbstractSelectionList<?> list) {
        this.removeWidget(this.styles);
        this.removeWidget(this.themes);
        this.removeWidget(this.panoramas);
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
                this.components.enableScissor(this.activeList.getLeft(), this.activeList.getTop(), this.activeList.getRight(), this.activeList.getBottom());
                this.activeList.render(stack, mouseX, mouseY, partialTicks);
                this.components.disableScissor();
            } else {
                this.components.renderTabHeaderBackground(0, 0, this.width, 24);
                this.activeList.render(stack, mouseX, mouseY, partialTicks);
            }
            this.components.renderListSeparators(this.width, 0, this.height - 32, 22, 3, this.components.threeTabWidth(this.width));
        }

        super.render(stack, mouseX, mouseY, partialTicks);
        if (this.activeList instanceof OptionsList list) {
            List<FormattedCharSequence> processors = tooltipAt(list, mouseX, mouseY);
            if (!processors.isEmpty()) this.renderTooltip(stack, processors, mouseX, mouseY);
        }
    }

    @Override
    public void renderDirtBackground(int vOffset) {
        if (MellowConfigs.CLIENT_CONFIGS.screenBackgroundStyle.get()) this.components.renderMenuBackground(0, 24, this.width, this.height, vOffset);
        else super.renderDirtBackground(vOffset);
    }
}
