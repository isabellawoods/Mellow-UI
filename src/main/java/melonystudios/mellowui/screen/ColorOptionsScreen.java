package melonystudios.mellowui.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.config.MellowConfigEntries;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.util.MUICommsProcessor;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

import static melonystudios.mellowui.config.WidgetConfigEntries.*;

public class ColorOptionsScreen extends OptionsSubScreen {
    public static final List<Option> WIDGETS = Lists.newArrayList(WIDGETS_SEPARATOR, DEFAULT_WIDGET_TEXT_COLOR, HIGHLIGHTED_WIDGET_TEXT_COLOR, DISABLED_WIDGET_TEXT_COLOR, DEFAULT_LEGACY_WIDGET_TEXT_COLOR, HIGHLIGHTED_LEGACY_WIDGET_TEXT_COLOR, DISABLED_LEGACY_WIDGET_TEXT_COLOR, LOCKED_WIDGET_TEXT_COLOR, HIGHLIGHTED_ICON_BUTTON_COLOR);
    public static final List<Option> TEXT_FIELDS = Lists.newArrayList(TEXT_FIELDS_SEPARATOR, TEXT_FIELD_CENTER_COLOR, TEXT_FIELD_DEFAULT_BORDER_COLOR, TEXT_FIELD_HIGHLIGHTED_BORDER_COLOR, TEXT_FIELD_SUGGESTION_COLOR, TEXT_FIELD_HIGHLIGHTED_SUGGESTION_COLOR, TEXT_FIELD_HIGHLIGHT_COLOR);
    public static final List<Option> SPLASHES = Lists.newArrayList(SPLASHES_SEPARATOR, SPLASH_TEXT_COLOR, HIGH_CONTRAST_SPLASH_TEXT_COLOR, MELLO_SPLASH_TEXT_COLOR);
    public static final List<Option> UPDATE_AVAILABILITY = Lists.newArrayList(UPDATE_AVAILABILITY_SEPARATOR, DEFAULT_UPDATE_AVAILABLE_COLOR, HIGH_CONTRAST_UPDATE_AVAILABLE_COLOR);
    public static final List<Option> BACKGROUNDS = Lists.newArrayList(BACKGROUNDS_SEPARATOR, MONOCHROME_LOADING_SCREEN_COLOR, WARNING_32BIT_COLOR);
    public static final List<Option> TOASTS = Lists.newArrayList(TOASTS_SEPARATOR, SYSTEM_TOAST_TITLE_COLOR, SYSTEM_TOAST_DESCRIPTION_COLOR, MUSIC_TOAST_TEXT_COLOR);
    public static final List<Option> MISCELLANEOUS = Lists.newArrayList(MellowConfigEntries.MISCELLANEOUS_SEPARATOR, TITLE_TEXT_COLOR, DESCRIPTION_TEXT_COLOR);
    private OptionsList list;

    public ColorOptionsScreen(Screen lastScreen, Options options) {
        super(lastScreen, options, TextComponents.buildScreenSubtitle(MellowUI.MOD_ID, MellowUI.MOD_NAME, new TranslatableComponent("menu.mellowui.color_options.title")));
    }

    @Override
    protected void init() {
        // List
        this.list = new OptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
        for (Option option : WIDGETS) this.list.addBig(option);
        for (Option option : TEXT_FIELDS) this.list.addBig(option);
        for (Option option : SPLASHES) this.list.addBig(option);
        for (Option option : UPDATE_AVAILABILITY) this.list.addBig(option);
        for (Option option : BACKGROUNDS) this.list.addBig(option);
        for (Option option : TOASTS) this.list.addBig(option);
        for (Option option : MISCELLANEOUS) this.list.addBig(option);
        if (!MUICommsProcessor.ENTRIES.isEmpty()) {
            this.list.addBig(MODDED_COLORS_SEPARATOR);
            for (Option option : MUICommsProcessor.ENTRIES) this.list.addBig(option);
        }
        this.addWidget(this.list);

        // Done button
        this.addRenderableWidget(new Button(this.width / 2 - 100, this.height - 25, 200, 20, CommonComponents.GUI_DONE,
                button -> this.minecraft.setScreen(this.lastScreen)));
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        this.list.render(stack, mouseX, mouseY, partialTicks);
        drawCenteredString(stack, this.font, this.title, this.width / 2, MellowUtils.DEFAULT_TITLE_HEIGHT, 0xFFFFFF);
        super.render(stack, mouseX, mouseY, partialTicks);
        List<FormattedCharSequence> lines = tooltipAt(this.list, mouseX, mouseY);
        if (!lines.isEmpty()) this.renderTooltip(stack, lines, mouseX, mouseY);
    }
}
