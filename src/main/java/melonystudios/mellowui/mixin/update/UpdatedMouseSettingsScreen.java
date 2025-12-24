package melonystudios.mellowui.mixin.update;

import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.config.VanillaConfigEntries;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.element.widget.WidgetComponents;
import melonystudios.mellowui.util.Alignment;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.screen.MouseSettingsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SettingsScreen;
import net.minecraft.client.gui.widget.list.OptionsRowList;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
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
public class UpdatedMouseSettingsScreen extends SettingsScreen {
    @Unique
    private final RenderComponents components = RenderComponents.INSTANCE;
    @Shadow
    @Final
    private static AbstractOption[] OPTIONS;
    @Shadow
    private OptionsRowList list;

    public UpdatedMouseSettingsScreen(Screen lastScreen, GameSettings options, ITextComponent title) {
        super(lastScreen, options, title);
    }

    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    protected void init(CallbackInfo callback) {
        if (!MellowConfigs.CLIENT_CONFIGS.mouseSettingsStyle.get()) return;
        callback.cancel();
        WidgetComponents components = WidgetComponents.components(this, this::addButton);
        this.list = components.optionsList(33, this.height - 33);
        if (InputMappings.isRawMouseInputSupported()) {
            this.list.addSmall(Stream.concat(Arrays.stream(OPTIONS), Stream.of(VanillaConfigEntries.ALLOW_CURSOR_CHANGES, AbstractOption.RAW_MOUSE_INPUT)).toArray(AbstractOption[]::new));
        } else {
            this.list.addSmall(Stream.concat(Arrays.stream(OPTIONS), Stream.of(VanillaConfigEntries.ALLOW_CURSOR_CHANGES)).toArray(AbstractOption[]::new));
        }
        this.children.add(this.list);

        // Done button
        components.done(Alignment.CENTER);
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        if (!MellowConfigs.CLIENT_CONFIGS.mouseSettingsStyle.get()) return;
        callback.cancel();
        this.renderBackground(stack);
        this.list.render(stack, mouseX, mouseY, partialTicks);
        this.components.drawTitle(this.title.copy().withStyle(TextComponents.titleStyle()), this.width);
        super.render(stack, mouseX, mouseY, partialTicks);
        List<IReorderingProcessor> tooltip = tooltipAt(this.list, mouseX, mouseY);
        if (tooltip != null) this.renderTooltip(stack, tooltip, mouseX, mouseY);
    }
}
