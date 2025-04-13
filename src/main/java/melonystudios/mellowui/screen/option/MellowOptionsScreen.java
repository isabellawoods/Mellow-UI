package melonystudios.mellowui.screen.option;

import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.config.option.OpenMenuOption;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SettingsScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.OptionsRowList;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;

public class MellowOptionsScreen extends SettingsScreen {
    public final OpenMenuOption forgeSettings = new OpenMenuOption("menu.mellowui.forge_options", new MellowForgeOptionsScreen(this, Minecraft.getInstance().options));
    public final OpenMenuOption mellowUISettings = new OpenMenuOption("menu.mellowui.mellow_ui_options", new MellowUIOptionsScreen(this, Minecraft.getInstance().options));
    public final OpenMenuOption mellomedleySettings = new OpenMenuOption("menu.mellowui.mellomedley_options", new MellomedleyOptionsScreen(this, Minecraft.getInstance().options));
    private OptionsRowList list;

    public MellowOptionsScreen(Screen lastScreen, GameSettings options) {
        super(lastScreen, options, new TranslationTextComponent("menu.mellowui.options.title"));
    }

    @Override
    protected void init() {
        this.list = new OptionsRowList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
        this.list.addBig(this.forgeSettings);
        this.list.addBig(this.mellowUISettings);
        this.list.addBig(this.mellomedleySettings);
        this.children.add(this.list);

        // Done button
        this.addButton(new Button(this.width / 2 - 100, this.height - 27, 200, 20, DialogTexts.GUI_DONE,
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
