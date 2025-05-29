package melonystudios.mellowui.screen.updated;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.config.option.OpenMenuOption;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.ControlsScreen;
import net.minecraft.client.gui.screen.MouseSettingsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SettingsScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.OptionsRowList;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;

public class MUIControlsScreen extends SettingsScreen {
    public final OpenMenuOption mouseSettings = new OpenMenuOption("options.mouse_settings", new MouseSettingsScreen(this, Minecraft.getInstance().options)).boldText(false);
    public final OpenMenuOption keyBinds = new OpenMenuOption("button.mellowui.key_binds", new ControlsScreen(this, Minecraft.getInstance().options)).boldText(false);
    private static final List<AbstractOption> SETTINGS = Lists.newArrayList(AbstractOption.TOGGLE_CROUCH, AbstractOption.TOGGLE_SPRINT, AbstractOption.AUTO_JUMP);
    private OptionsRowList list;

    public MUIControlsScreen(Screen lastScreen, GameSettings options) {
        super(lastScreen, options, new TranslationTextComponent("menu.mellowui.controls.title"));
    }

    @Override
    protected void init() {
        this.list = new OptionsRowList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
        this.list.addSmall(this.mouseSettings, this.keyBinds);
        this.list.addSmall(SETTINGS.toArray(new AbstractOption[0]));
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
