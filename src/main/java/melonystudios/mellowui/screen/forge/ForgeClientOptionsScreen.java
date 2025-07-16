package melonystudios.mellowui.screen.forge;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

import static melonystudios.mellowui.config.ForgeConfigEntries.*;

public class ForgeClientOptionsScreen extends OptionsSubScreen {
    public static final List<Option> SETTINGS = Lists.newArrayList(EXPERIMENTAL_LIGHT_PIPELINE, THREADED_CHUNK_RENDERING, SHOW_LOAD_WARNINGS, USE_COMBINED_DEPTH_STENCIL_ATTACHMENT, FORCE_SYSTEM_NANO_TIME, COMPRESS_LAN_IPV6_ADDRESSES);
    private OptionsList list;

    public ForgeClientOptionsScreen(Screen screen, Options options) {
        super(screen, options, new TranslatableComponent("menu.forge.client_options.title"));
    }

    @Override
    protected void init() {
        this.list = new OptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
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
