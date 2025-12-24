package melonystudios.mellowui.screen.forge;

import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.config.option.OpenMenuOption;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.element.widget.WidgetComponents;
import melonystudios.mellowui.util.Alignment;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SettingsScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.list.OptionsRowList;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;

public class ForgeOptionsScreen extends SettingsScreen {
    private final OpenMenuOption clientSettings = new OpenMenuOption("menu.forge.client_options", new ForgeClientOptionsScreen(this, Minecraft.getInstance().options));
    private final OpenMenuOption serverSettings = new OpenMenuOption("menu.forge.server_options", new TranslationTextComponent("config.forge.server_settings.only_in_world").withStyle(TextFormatting.RED),
            new ForgeServerOptionsScreen(this, Minecraft.getInstance().options));
    private final RenderComponents components = RenderComponents.INSTANCE;
    private OptionsRowList list;

    public ForgeOptionsScreen(Screen lastScreen, GameSettings options) {
        super(lastScreen, options, TextComponents.buildScreenTitle("forge", "Forge"));
    }

    @Override
    protected void init() {
        WidgetComponents components = WidgetComponents.components(this, this::addButton);
        this.list = components.optionsList(33, this.height - 33);
        this.list.addBig(this.clientSettings);
        this.list.addBig(this.serverSettings);
        this.children.add(this.list);

        Widget serverSettings = this.list.findOption(this.serverSettings);
        if (serverSettings != null) serverSettings.active = Minecraft.getInstance().level != null;

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
