package melonystudios.mellowui.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.config.MellowConfigEntries;
import melonystudios.mellowui.util.MellowUtils;
import melonystudios.mellowui.util.text.TextComponents;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SettingsScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.OptionsRowList;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;

import static melonystudios.mellowui.config.WidgetConfigEntries.*;

public class ColorOptionsScreen extends SettingsScreen {
    public static final List<AbstractOption> WIDGETS = Lists.newArrayList(WIDGETS_SEPARATOR, DEFAULT_WIDGET_TEXT_COLOR, HIGHLIGHTED_WIDGET_TEXT_COLOR, DISABLED_WIDGET_TEXT_COLOR, DEFAULT_LEGACY_WIDGET_TEXT_COLOR, HIGHLIGHTED_LEGACY_WIDGET_TEXT_COLOR, DISABLED_LEGACY_WIDGET_TEXT_COLOR, HIGHLIGHTED_ICON_BUTTON_COLOR);
    public static final List<AbstractOption> SPLASHES = Lists.newArrayList(SPLASHES_SEPARATOR, SPLASH_TEXT_COLOR, HIGH_CONTRAST_SPLASH_TEXT_COLOR, MELLO_SPLASH_TEXT_COLOR);
    public static final List<AbstractOption> UPDATE_AVAILABILITY = Lists.newArrayList(UPDATE_AVAILABILITY_SEPARATOR, DEFAULT_UPDATE_AVAILABLE_COLOR, HIGH_CONTRAST_UPDATE_AVAILABLE_COLOR);
    public static final List<AbstractOption> BACKGROUNDS = Lists.newArrayList(BACKGROUNDS_SEPARATOR, MONOCHROME_LOADING_SCREEN_COLOR);
    public static final List<AbstractOption> MISCELLANEOUS = Lists.newArrayList(MellowConfigEntries.MISCELLANEOUS_SEPARATOR, DESCRIPTION_TEXT_COLOR);
    private OptionsRowList list;

    public ColorOptionsScreen(Screen lastScreen, GameSettings options) {
        super(lastScreen, options, TextComponents.buildScreenSubtitle(MellowUI.MOD_ID, MellowUI.MOD_NAME, new TranslationTextComponent("menu.mellowui.color_options.title")));
    }

    @Override
    protected void init() {
        // List
        this.list = new OptionsRowList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
        for (AbstractOption option : WIDGETS) this.list.addBig(option);
        for (AbstractOption option : SPLASHES) this.list.addBig(option);
        for (AbstractOption option : UPDATE_AVAILABILITY) this.list.addBig(option);
        for (AbstractOption option : BACKGROUNDS) this.list.addBig(option);
        for (AbstractOption option : MISCELLANEOUS) this.list.addBig(option);
        this.children.add(this.list);

        // Done button
        this.addButton(new Button(this.width / 2 - 100, this.height - 25, 200, 20, DialogTexts.GUI_DONE,
                button -> this.minecraft.setScreen(this.lastScreen)));
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        this.list.render(stack, mouseX, mouseY, partialTicks);
        drawCenteredString(stack, this.font, this.title, this.width / 2, MellowUtils.DEFAULT_TITLE_HEIGHT, 0xFFFFFF);
        super.render(stack, mouseX, mouseY, partialTicks);
        List<IReorderingProcessor> processors = tooltipAt(this.list, mouseX, mouseY);
        if (processors != null) this.renderTooltip(stack, processors, mouseX, mouseY);
    }
}
