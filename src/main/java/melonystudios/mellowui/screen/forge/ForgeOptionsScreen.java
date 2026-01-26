package melonystudios.mellowui.screen.forge;

import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.config.option.OpenMenuOption;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.element.widget.WidgetComponents;
import melonystudios.mellowui.util.Alignment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class ForgeOptionsScreen extends OptionsSubScreen {
    private final OpenMenuOption clientSettings = new OpenMenuOption("menu.forge.client_options", new ForgeClientOptionsScreen(this, Minecraft.getInstance().options));
    private final OpenMenuOption serverSettings = new OpenMenuOption("menu.forge.server_options", new ForgeServerOptionsScreen(this, Minecraft.getInstance().options));
    private final RenderComponents components = RenderComponents.INSTANCE;
    private OptionsList list;

    public ForgeOptionsScreen(Screen lastScreen, Options options) {
        super(lastScreen, options, TextComponents.buildScreenTitle("forge", "Forge"));
    }

    @Override
    protected void init() {
        WidgetComponents components = WidgetComponents.components(this, this::addRenderableWidget);
        this.list = components.optionsList(33, this.height - 33);
        this.list.addBig(this.clientSettings);
        this.list.addBig(this.serverSettings);
        this.addWidget(this.list);

        AbstractWidget serverSettings = this.list.findOption(this.serverSettings);
        if (serverSettings != null) serverSettings.active = this.minecraft.level != null;

        // Done button
        components.done(Alignment.CENTER);
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        this.list.render(stack, mouseX, mouseY, partialTicks);
        this.components.drawTitle(this.title, this.width);
        super.render(stack, mouseX, mouseY, partialTicks);
        List<FormattedCharSequence> tooltip = tooltipAt(this.list, mouseX, mouseY);
        if (!tooltip.isEmpty()) this.renderTooltip(stack, tooltip, mouseX, mouseY);
    }
}
