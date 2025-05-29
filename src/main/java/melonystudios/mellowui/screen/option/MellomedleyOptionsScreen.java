package melonystudios.mellowui.screen.option;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.screen.widget.TooltippedTextFieldWidget;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SettingsScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.OptionsRowList;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import java.util.List;

import static melonystudios.mellowui.config.MellowConfigEntries.MELLO_SPLASH_TEXT_COLOR;

public class MellomedleyOptionsScreen extends SettingsScreen {
    public static final List<AbstractOption> SETTINGS = Lists.newArrayList(MELLO_SPLASH_TEXT_COLOR);
    private OptionsRowList list;
    @Nullable
    private TooltippedTextFieldWidget splashTextColor;

    public MellomedleyOptionsScreen(Screen screen, GameSettings options) {
        super(screen, options, new TranslationTextComponent("menu.mellowui.mellomedley_options.title"));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.splashTextColor != null) this.splashTextColor.tick();
    }

    @Override
    protected void init() {
        this.list = new OptionsRowList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
        this.list.addSmall(SETTINGS.toArray(new AbstractOption[0]));
        this.children.add(this.list);

        Widget widget = this.list.findOption(MELLO_SPLASH_TEXT_COLOR);
        if (widget != null) this.splashTextColor = (TooltippedTextFieldWidget) widget;

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
