package melonystudios.mellowui.screen.forge;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

import static melonystudios.mellowui.config.ForgeConfigEntries.*;

public class ForgeServerOptionsScreen extends OptionsSubScreen {
    public static final List<Option> SETTINGS = Lists.newArrayList(BASE_ZOMBIE_SUMMON_CHANCE, BABY_ZOMBIE_CHANCE, REMOVE_ERRORING_ENTITIES, REMOVE_ERRORING_BLOCK_ENTITIES, FULL_BOUNDING_BOX_LADDERS, FIX_ADVANCEMENT_LOADING, TREAT_EMPTY_TAGS_AS_AIR, SKIP_EMPTY_SHAPELESS_CHECK);
    private OptionsList list;

    public ForgeServerOptionsScreen(Screen screen, Options options) {
        super(screen, options, new TranslatableComponent("menu.forge.server_options.title"));
    }

    @Override
    public void tick() {
        AbstractWidget permissionHandler = this.list.findOption(PERMISSION_HANDLER);
        if (permissionHandler instanceof EditBox) ((EditBox) permissionHandler).tick();
    }

    @Override
    protected void init() {
        this.list = new OptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
        this.list.addBig(PERMISSION_HANDLER);
        this.list.addSmall(SETTINGS.toArray(new Option[0]));
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
