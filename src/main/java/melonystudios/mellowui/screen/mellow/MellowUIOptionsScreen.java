package melonystudios.mellowui.screen.mellow;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SettingsScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.OptionsRowList;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;

import static melonystudios.mellowui.config.MellowConfigEntries.*;

public class MellowUIOptionsScreen extends SettingsScreen {
    public static final List<AbstractOption> SETTINGS = Lists.newArrayList(MAIN_MENU_MOD_BUTTON, YELLOW_BUTTON_HIGHLIGHT);
    private OptionsRowList list;

    public MellowUIOptionsScreen(Screen screen, GameSettings options) {
        super(screen, options, new TranslationTextComponent("menu.mellowui.mellow_ui_options.title"));
    }

    @Override
    public void tick() {
        super.tick();
        Widget loadingScreenColor = this.list.findOption(MONOCHROME_LOADING_SCREEN_COLOR);
        if (this.list != null && loadingScreenColor instanceof TextFieldWidget) {
            ((TextFieldWidget) loadingScreenColor).tick();
        }
    }

    @Override
    protected void init() {
        this.list = new OptionsRowList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
        this.list.addBig(PANORAMA_CAMERA_PITCH);
        this.list.addBig(MONOCHROME_LOADING_SCREEN_COLOR);
        this.list.addSmall(SETTINGS.toArray(new AbstractOption[0]));
        this.children.add(this.list);

        // Done button
        this.addButton(new Button(this.width / 2 - 100, this.height - 27, 200, 20, DialogTexts.GUI_DONE,
                button -> this.minecraft.setScreen(this.lastScreen)));
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        this.list.render(stack, mouseX, mouseY, partialTicks);
        drawCenteredString(stack, this.font, this.title, this.width / 2, 10, 0xFFFFFF);
        super.render(stack, mouseX, mouseY, partialTicks);
        List<IReorderingProcessor> processors = tooltipAt(this.list, mouseX, mouseY);
        if (processors != null) this.renderTooltip(stack, processors, mouseX, mouseY);
    }
}
