package melonystudios.mellowui.screen.backport;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.config.option.OpenMenuOption;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.element.widget.WidgetComponents;
import melonystudios.mellowui.util.Alignment;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ControlsScreen;
import net.minecraft.client.gui.screen.MouseSettingsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SettingsScreen;
import net.minecraft.client.gui.widget.list.OptionsRowList;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;

public class MUIControlsScreen extends SettingsScreen {
    private static final List<AbstractOption> SETTINGS = Lists.newArrayList(AbstractOption.TOGGLE_CROUCH, AbstractOption.TOGGLE_SPRINT, AbstractOption.AUTO_JUMP);
    private final OpenMenuOption mouseSettings = new OpenMenuOption("options.mouse_settings", new MouseSettingsScreen(this, Minecraft.getInstance().options)).boldText(false);
    private final OpenMenuOption keyBinds = new OpenMenuOption("button.mellowui.key_binds", new ControlsScreen(this, Minecraft.getInstance().options)).boldText(false);
    private final RenderComponents components = RenderComponents.INSTANCE;
    private OptionsRowList list;

    public MUIControlsScreen(Screen lastScreen, GameSettings options) {
        super(lastScreen, options, new TranslationTextComponent("menu.mellowui.controls.title").withStyle(TextComponents.titleStyle()));
    }

    @Override
    protected void init() {
        WidgetComponents components = WidgetComponents.components(this, this::addButton);
        this.list = components.optionsList(33, this.height - 33);
        this.list.addSmall(this.mouseSettings, this.keyBinds);
        this.list.addSmall(SETTINGS.toArray(new AbstractOption[0]));
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
