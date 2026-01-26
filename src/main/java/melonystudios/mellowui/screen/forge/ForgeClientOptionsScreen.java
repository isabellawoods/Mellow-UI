package melonystudios.mellowui.screen.forge;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.element.widget.WidgetComponents;
import melonystudios.mellowui.util.Alignment;
import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

import static melonystudios.mellowui.config.ForgeConfigEntries.*;

public class ForgeClientOptionsScreen extends OptionsSubScreen {
    public static final List<Option> SETTINGS = Lists.newArrayList(EXPERIMENTAL_LIGHT_PIPELINE, THREADED_CHUNK_RENDERING, SHOW_LOAD_WARNINGS, USE_COMBINED_DEPTH_STENCIL_ATTACHMENT, FORCE_SYSTEM_NANO_TIME, COMPRESS_LAN_IPV6_ADDRESSES);
    private final RenderComponents components = RenderComponents.INSTANCE;
    private OptionsList list;

    public ForgeClientOptionsScreen(Screen screen, Options options) {
        super(screen, options, TextComponents.buildScreenSubtitle("forge", "Forge", new TranslatableComponent("menu.forge.client_options.title")));
    }

    @Override
    protected void init() {
        WidgetComponents components = WidgetComponents.components(this, this::addRenderableWidget);
        this.list = components.optionsList(33, this.height - 33);
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
