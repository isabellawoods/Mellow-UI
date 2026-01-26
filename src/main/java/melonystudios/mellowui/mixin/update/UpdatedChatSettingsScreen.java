package melonystudios.mellowui.mixin.update;

import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.element.widget.WidgetComponents;
import melonystudios.mellowui.util.Alignment;
import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.ChatOptionsScreen;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(value = ChatOptionsScreen.class, priority = 900)
public class UpdatedChatSettingsScreen extends OptionsSubScreen {
    @Unique
    private final RenderComponents components = RenderComponents.INSTANCE;
    @Shadow
    @Final
    private static Option[] CHAT_OPTIONS;
    @Unique
    private OptionsList list;

    public UpdatedChatSettingsScreen(Screen lastScreen, Options options, Component title) {
        super(lastScreen, options, title);
    }

    @Override
    protected void init() {
        if (!MellowConfigs.CLIENT_CONFIGS.mouseSettingsStyle.get()) super.init();
        WidgetComponents components = WidgetComponents.components(this, this::addRenderableWidget);
        this.list = components.optionsList(33, this.height - 33);
        this.list.addSmall(CHAT_OPTIONS);
        this.addWidget(this.list);

        // Done button
        components.done(Alignment.CENTER);
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        if (!MellowConfigs.CLIENT_CONFIGS.mouseSettingsStyle.get()) super.render(stack, mouseX, mouseY, partialTicks);
        this.renderBackground(stack);
        this.list.render(stack, mouseX, mouseY, partialTicks);
        this.components.drawTitle(new TranslatableComponent("menu.minecraft.chat_settings.title").withStyle(TextComponents.titleStyle()), this.width);
        for (GuiEventListener listener : this.children()) {
            if (listener instanceof Widget widget) widget.render(stack, mouseX, mouseY, partialTicks);
        }
        List<FormattedCharSequence> tooltip = tooltipAt(this.list, mouseX, mouseY);
        if (!tooltip.isEmpty()) this.renderTooltip(stack, tooltip, mouseX, mouseY);
    }
}
