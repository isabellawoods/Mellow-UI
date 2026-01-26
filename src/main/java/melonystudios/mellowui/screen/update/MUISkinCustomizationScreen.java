package melonystudios.mellowui.screen.update;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.config.option.ModelPartBooleanOption;
import melonystudios.mellowui.config.option.OpenMenuOption;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.element.widget.WidgetComponents;
import melonystudios.mellowui.util.Alignment;
import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraftforge.fml.ModList;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class MUISkinCustomizationScreen extends OptionsSubScreen {
    private final RenderComponents components = RenderComponents.INSTANCE;
    private OptionsList list;

    public MUISkinCustomizationScreen(Screen lastScreen, Options options) {
        super(lastScreen, options, new TranslatableComponent("options.skinCustomisation.title").withStyle(TextComponents.titleStyle()));
    }

    @Override
    protected void init() {
        WidgetComponents components = WidgetComponents.components(this, this::addRenderableWidget);
        this.list = components.optionsList(33, this.height - 33);
        List<Option> settings = Lists.newArrayList();
        // Compatibility
        if (ModList.get().isLoaded("blueprint")) { // add slabfish hat settings button
            try {
                Class<?> screen = Class.forName("com.teamabnormals.blueprint.client.screen.SlabfishHatScreen");
                OpenMenuOption slabfishHatSettings = new OpenMenuOption("blueprint.screen.slabfish_settings",
                        new TranslatableComponent("blueprint.screen.slabfish_settings.tooltip",
                                new TextComponent("patreon.com/teamabnormals").withStyle(style -> style.withColor(0xFF424D).withBold(true))),
                        (Screen) screen.getConstructor(Screen.class).newInstance(this));
                this.list.addBig(slabfishHatSettings);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ignored) {
                MellowUI.LOGGER.error(new TranslatableComponent("error.mellowui.compatibility.blueprint_slabfish_hat").getString());
            }
        }

        for (PlayerModelPart part : PlayerModelPart.values()) {
            settings.add(new ModelPartBooleanOption(part, options -> options.isModelPartEnabled(part), (options, newValue) -> options.toggleModelPart(part, newValue)));
        }
        settings.add(Option.MAIN_HAND);

        this.list.addSmall(settings.toArray(new Option[0]));
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
