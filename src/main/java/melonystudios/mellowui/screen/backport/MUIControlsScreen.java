package melonystudios.mellowui.screen.backport;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.config.option.OpenMenuOption;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.element.widget.WidgetComponents;
import melonystudios.mellowui.util.Alignment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.MouseSettingsScreen;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.controls.KeyBindsScreen;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class MUIControlsScreen extends OptionsSubScreen {
    private static final List<Option> SETTINGS = Lists.newArrayList(Option.TOGGLE_CROUCH, Option.TOGGLE_SPRINT, Option.AUTO_JUMP);
    public final OpenMenuOption mouseSettings = new OpenMenuOption("options.mouse_settings", new MouseSettingsScreen(this, Minecraft.getInstance().options)).boldText(false);
    public final OpenMenuOption keyBinds = new OpenMenuOption("controls.keybinds", new KeyBindsScreen(this, Minecraft.getInstance().options)).boldText(false);
    private final RenderComponents components = RenderComponents.INSTANCE;
    private OptionsList list;

    public MUIControlsScreen(Screen lastScreen, Options options) {
        super(lastScreen, options, new TranslatableComponent("controls.title").withStyle(TextComponents.titleStyle()));
    }

    @Override
    protected void init() {
        WidgetComponents components = WidgetComponents.components(this, this::addRenderableWidget);
        this.list = components.optionsList(33, this.height - 33);
        this.list.addSmall(this.mouseSettings, this.keyBinds);
        this.list.addSmall(SETTINGS.toArray(new Option[0]));
        this.addWidget(this.list);

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
