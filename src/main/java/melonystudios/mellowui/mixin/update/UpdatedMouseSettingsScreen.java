package melonystudios.mellowui.mixin.update;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.config.VanillaConfigEntries;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.element.widget.WidgetComponents;
import melonystudios.mellowui.util.Alignment;
import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.MouseSettingsScreen;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Mixin(value = MouseSettingsScreen.class, priority = 900)
public class UpdatedMouseSettingsScreen extends OptionsSubScreen {
    @Unique
    private final RenderComponents components = RenderComponents.INSTANCE;
    @Shadow
    @Final
    private static Option[] OPTIONS;
    @Shadow
    private OptionsList list;

    public UpdatedMouseSettingsScreen(Screen lastScreen, Options options, Component title) {
        super(lastScreen, options, title);
    }

    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    protected void init(CallbackInfo callback) {
        if (!MellowConfigs.CLIENT_CONFIGS.mouseSettingsStyle.get()) return;
        callback.cancel();
        WidgetComponents components = WidgetComponents.components(this, this::addRenderableWidget);
        this.list = components.optionsList(33, this.height - 33);
        if (InputConstants.isRawMouseInputSupported()) {
            this.list.addSmall(Stream.concat(Arrays.stream(OPTIONS), Stream.of(VanillaConfigEntries.ALLOW_CURSOR_CHANGES, Option.RAW_MOUSE_INPUT)).toArray(Option[]::new));
        } else {
            this.list.addSmall(Stream.concat(Arrays.stream(OPTIONS), Stream.of(VanillaConfigEntries.ALLOW_CURSOR_CHANGES)).toArray(Option[]::new));
        }
        this.addWidget(this.list);

        // Done button
        components.done(Alignment.CENTER);
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        if (!MellowConfigs.CLIENT_CONFIGS.mouseSettingsStyle.get()) return;
        callback.cancel();
        this.renderBackground(stack);
        this.list.render(stack, mouseX, mouseY, partialTicks);
        this.components.drawTitle(this.title.copy().withStyle(TextComponents.titleStyle()), this.width);
        super.render(stack, mouseX, mouseY, partialTicks);
        List<FormattedCharSequence> tooltip = tooltipAt(this.list, mouseX, mouseY);
        if (!tooltip.isEmpty()) this.renderTooltip(stack, tooltip, mouseX, mouseY);
    }
}
