package melonystudios.mellowui.screen.forge;

import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.config.option.OpenMenuOption;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SettingsScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.OptionsRowList;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;

public class ForgeOptionsScreen extends SettingsScreen {
    public final ITextComponent onlyChangeInWorld = new TranslationTextComponent("config.forge.server_settings.only_in_world").withStyle(TextFormatting.RED);
    public final OpenMenuOption clientSettings = new OpenMenuOption("menu.forge.client_options", new ForgeClientOptionsScreen(this, Minecraft.getInstance().options));
    private OptionsRowList list;

    public ForgeOptionsScreen(Screen lastScreen, GameSettings options) {
        super(lastScreen, options, new TranslationTextComponent("menu.forge.options.title"));
    }

    @Override
    protected void init() {
        this.list = new OptionsRowList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
        this.list.addBig(this.clientSettings);
        OpenMenuOption serverSettings = new OpenMenuOption("menu.forge.server_options", this.minecraft.level == null, this.onlyChangeInWorld, new ForgeServerOptionsScreen(this, Minecraft.getInstance().options));
        this.list.addBig(serverSettings);
        this.children.add(this.list);

        Widget settingsWidget = this.list.findOption(serverSettings);
        if (settingsWidget != null) settingsWidget.active = false;

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
