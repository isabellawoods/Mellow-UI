package melonystudios.mellowui.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.config.MellowConfigEntries;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.element.widget.WidgetComponents;
import melonystudios.mellowui.util.Alignment;
import melonystudios.mellowui.util.MUICommsProcessor;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SettingsScreen;
import net.minecraft.client.gui.widget.list.OptionsRowList;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;

import static melonystudios.mellowui.config.WidgetConfigEntries.*;

public class ColorOptionsScreen extends SettingsScreen {
    public static final List<AbstractOption> WIDGETS = Lists.newArrayList(WIDGETS_SEPARATOR, DEFAULT_WIDGET_TEXT_COLOR, HIGHLIGHTED_WIDGET_TEXT_COLOR, DISABLED_WIDGET_TEXT_COLOR, DEFAULT_LEGACY_WIDGET_TEXT_COLOR, HIGHLIGHTED_LEGACY_WIDGET_TEXT_COLOR, DISABLED_LEGACY_WIDGET_TEXT_COLOR, LOCKED_WIDGET_TEXT_COLOR, HIGHLIGHTED_ICON_BUTTON_COLOR);
    public static final List<AbstractOption> TEXT_FIELDS = Lists.newArrayList(TEXT_FIELDS_SEPARATOR, TEXT_FIELD_CENTER_COLOR, TEXT_FIELD_DEFAULT_BORDER_COLOR, TEXT_FIELD_HIGHLIGHTED_BORDER_COLOR, TEXT_FIELD_SUGGESTION_COLOR, TEXT_FIELD_HIGHLIGHTED_SUGGESTION_COLOR, TEXT_FIELD_HIGHLIGHT_COLOR);
    public static final List<AbstractOption> SPLASHES = Lists.newArrayList(SPLASHES_SEPARATOR, SPLASH_TEXT_COLOR, HIGH_CONTRAST_SPLASH_TEXT_COLOR, MELLO_SPLASH_TEXT_COLOR);
    public static final List<AbstractOption> UPDATE_AVAILABILITY = Lists.newArrayList(UPDATE_AVAILABILITY_SEPARATOR, DEFAULT_UPDATE_AVAILABLE_COLOR, HIGH_CONTRAST_UPDATE_AVAILABLE_COLOR);
    public static final List<AbstractOption> BACKGROUNDS = Lists.newArrayList(BACKGROUNDS_SEPARATOR, MONOCHROME_LOADING_SCREEN_COLOR, WARNING_32BIT_COLOR);
    public static final List<AbstractOption> TOASTS = Lists.newArrayList(TOASTS_SEPARATOR, SYSTEM_TOAST_TITLE_COLOR, SYSTEM_TOAST_DESCRIPTION_COLOR, MUSIC_TOAST_TEXT_COLOR);
    public static final List<AbstractOption> MISCELLANEOUS = Lists.newArrayList(MellowConfigEntries.MISCELLANEOUS_SEPARATOR, TITLE_TEXT_COLOR, DESCRIPTION_TEXT_COLOR);
    private final RenderComponents components = RenderComponents.INSTANCE;
    private OptionsRowList list;

    public ColorOptionsScreen(Screen lastScreen, GameSettings options) {
        super(lastScreen, options, TextComponents.buildScreenSubtitle(MellowUI.MOD_ID, MellowUI.MOD_NAME, new TranslationTextComponent("menu.mellowui.color_options.title")));
    }

    @Override
    protected void init() {
        WidgetComponents components = WidgetComponents.components(this, this::addButton);

        // List
        this.list = components.optionsList(33, this.height - 33);
        for (AbstractOption option : WIDGETS) this.list.addBig(option);
        for (AbstractOption option : TEXT_FIELDS) this.list.addBig(option);
        for (AbstractOption option : SPLASHES) this.list.addBig(option);
        for (AbstractOption option : UPDATE_AVAILABILITY) this.list.addBig(option);
        for (AbstractOption option : BACKGROUNDS) this.list.addBig(option);
        for (AbstractOption option : TOASTS) this.list.addBig(option);
        for (AbstractOption option : MISCELLANEOUS) this.list.addBig(option);
        if (!MUICommsProcessor.ENTRIES.isEmpty()) {
            this.list.addBig(MODDED_COLORS_SEPARATOR);
            for (AbstractOption option : MUICommsProcessor.ENTRIES) this.list.addBig(option);
        }
        this.children.add(this.list);

        // Done button
        components.done(Alignment.CENTER);
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        this.list.render(stack, mouseX, mouseY, partialTicks);
        this.components.drawTitle(this.title, this.width);
        super.render(stack, mouseX, mouseY, partialTicks);
        List<IReorderingProcessor> tooltip = tooltipAt(this.list, mouseX, mouseY);
        if (tooltip != null) this.renderTooltip(stack, tooltip, mouseX, mouseY);
    }
}
