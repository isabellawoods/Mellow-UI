package melonystudios.mellowui.screen.update;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.config.option.ModelPartBooleanOption;
import melonystudios.mellowui.config.option.OpenMenuOption;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.element.widget.WidgetComponents;
import melonystudios.mellowui.util.Alignment;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SettingsScreen;
import net.minecraft.client.gui.widget.list.OptionsRowList;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.ModList;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class SkinCustomizationScreen extends SettingsScreen {
    private final RenderComponents components = RenderComponents.INSTANCE;
    private OptionsRowList list;

    public SkinCustomizationScreen(Screen lastScreen, GameSettings options) {
        super(lastScreen, options, new TranslationTextComponent("options.skinCustomisation.title").withStyle(TextComponents.titleStyle()));
    }

    @Override
    protected void init() {
        WidgetComponents components = WidgetComponents.components(this, this::addButton);
        this.list = components.optionsList(33, this.height - 33);
        List<AbstractOption> settings = Lists.newArrayList();
        // Compatibility
        if (ModList.get().isLoaded("abnormals_core")) { // add slabfish hat settings button
            try {
                Class<?> screen = Class.forName("com.minecraftabnormals.abnormals_core.client.screen.SlabfishHatScreen");
                OpenMenuOption slabfishHatSettings = new OpenMenuOption("abnormals_core.screen.slabfish_settings",
                        new TranslationTextComponent("abnormals_core.screen.slabfish_settings.tooltip",
                                new StringTextComponent("patreon.com/teamabnormals").withStyle(style -> style.withColor(Color.fromRgb(0xFF424D)).withBold(true))),
                        (Screen) screen.getConstructor(Screen.class).newInstance(this));
                this.list.addBig(slabfishHatSettings);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ignored) {
                MellowUI.LOGGER.error(new TranslationTextComponent("error.mellowui.compatibility.blueprint_slabfish_hat").getString());
            }
        }

        for (PlayerModelPart part : PlayerModelPart.values()) {
            settings.add(new ModelPartBooleanOption(part, options -> options.getModelParts().contains(part), (options, newValue) -> options.toggleModelPart(part)));
        }
        settings.add(AbstractOption.MAIN_HAND);

        this.list.addSmall(settings.toArray(new AbstractOption[0]));
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
