package melonystudios.mellowui.screen.forge;

import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.config.option.OpenMenuOption;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class ForgeOptionsScreen extends OptionsSubScreen {
    public final OpenMenuOption clientSettings = new OpenMenuOption("menu.forge.client_options", new ForgeClientOptionsScreen(this, Minecraft.getInstance().options));
    public final OpenMenuOption serverSettings = new OpenMenuOption("menu.forge.server_options", new ForgeServerOptionsScreen(this, Minecraft.getInstance().options));
    private OptionsList list;

    public ForgeOptionsScreen(Screen lastScreen, Options options) {
        super(lastScreen, options, new TranslatableComponent("menu.forge.options.title"));
    }

    @Override
    protected void init() {
        this.list = new OptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
        this.list.addBig(this.clientSettings);
        this.list.addBig(this.serverSettings);
        this.addWidget(this.list);

        AbstractWidget serverSettings = this.list.findOption(this.serverSettings);
        if (serverSettings != null) serverSettings.active = this.minecraft.level != null;

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
        List<FormattedCharSequence> processors = tooltipAt(this.list, mouseX, mouseY);
        if (!processors.isEmpty()) this.renderTooltip(stack, processors, mouseX, mouseY);
    }
}
