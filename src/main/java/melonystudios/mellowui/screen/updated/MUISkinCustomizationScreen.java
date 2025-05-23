package melonystudios.mellowui.screen.updated;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.config.option.ModelPartBooleanOption;
import melonystudios.mellowui.config.option.OpenMenuOption;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SettingsScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.OptionsRowList;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.ModList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class MUISkinCustomizationScreen extends SettingsScreen {
    public static final Logger LOGGER = LogManager.getLogger();
    private OptionsRowList list;

    public MUISkinCustomizationScreen(Screen lastScreen, GameSettings options) {
        super(lastScreen, options, new TranslationTextComponent("options.skinCustomisation.title"));
    }

    @Override
    protected void init() {
        List<AbstractOption> settings = Lists.newArrayList();
        // Compatibility
        if (ModList.get().isLoaded("abnormals_core")) { // add slabfish hat settings button
            try {
                Class<?> screen = Class.forName("com.minecraftabnormals.abnormals_core.client.screen.SlabfishHatScreen");
                OpenMenuOption slabfishHatSettings = new OpenMenuOption("abnormals_core.screen.slabfish_settings", false,
                        new TranslationTextComponent("abnormals_core.screen.slabfish_settings.tooltip",
                                new StringTextComponent("patreon.com/teamabnormals").withStyle(Style.EMPTY.withColor(Color.fromRgb(0xFF424D)))),
                        (Screen) screen.getConstructor(Screen.class).newInstance(this));
                settings.add(slabfishHatSettings);
                LOGGER.info("[Mellow UI] Added 'Slabfish Hat Settings' button from original skin customization screen");
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ignored) {}
        }

        for (PlayerModelPart part : PlayerModelPart.values()) {
            settings.add(new ModelPartBooleanOption(part, options -> options.getModelParts().contains(part), (options, newValue) -> options.toggleModelPart(part)));
        }
        settings.add(AbstractOption.MAIN_HAND);

        this.list = new OptionsRowList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
        this.list.addSmall(settings.toArray(new AbstractOption[0]));
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
