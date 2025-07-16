package melonystudios.mellowui.screen.update;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.config.option.ModelPartBooleanOption;
import melonystudios.mellowui.config.option.OpenMenuOption;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraftforge.fml.ModList;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.UUID;

public class MUISkinCustomizationScreen extends OptionsSubScreen {
    private OptionsList list;

    public MUISkinCustomizationScreen(Screen lastScreen, Options options) {
        super(lastScreen, options, new TranslatableComponent("options.skinCustomisation.title"));
    }

    @Override
    protected void init() {
        this.list = new OptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
        List<Option> settings = Lists.newArrayList();
        // Compatibility
        if (ModList.get().isLoaded("abnormals_core")) { // add slabfish hat settings button
            try {
                Class<?> screen = Class.forName("com.minecraftabnormals.abnormals_core.client.screen.SlabfishHatScreen");
                OpenMenuOption slabfishHatSettings = new OpenMenuOption("abnormals_core.screen.slabfish_settings",
                        new TranslatableComponent("abnormals_core.screen.slabfish_settings.tooltip",
                                new TextComponent("patreon.com/teamabnormals").withStyle(style -> style.withColor(0xEF323D).withBold(true))),
                        (Screen) screen.getConstructor(Screen.class).newInstance(this));
                this.list.addBig(slabfishHatSettings);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ignored) {
                MellowUI.LOGGER.error(new TranslatableComponent("error.mellowui.compatibility.blueprint_slabfish_hat").getString());
            }
        }

        if (ModList.get().isLoaded("wildfire_gender") && this.minecraft.level != null) { // add breast settings button
            try {
                Class<?> screen = Class.forName("com.wildfire.gui.screen.WardrobeBrowserScreen");
                OpenMenuOption wardrobe = new OpenMenuOption("button.mellowui.wildfire_gender_breast_settings",
                        (Screen) screen.getConstructor(Screen.class, UUID.class).newInstance(this, this.minecraft.getUser().getGameProfile().getId()));
                if (ModList.get().isLoaded("femalegender")) settings.add(wardrobe);
                else this.list.addBig(wardrobe);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ignored) {
                MellowUI.LOGGER.error(new TranslatableComponent("error.mellowui.compatibility.femalegender_breast_settings").getString());
            }
        }

        for (PlayerModelPart part : PlayerModelPart.values()) {
            settings.add(new ModelPartBooleanOption(part, options -> options.isModelPartEnabled(part), (options, newValue) -> options.toggleModelPart(part, newValue)));
        }
        settings.add(Option.MAIN_HAND);

        this.list.addSmall(settings.toArray(new Option[0]));
        this.addWidget(this.list);

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
