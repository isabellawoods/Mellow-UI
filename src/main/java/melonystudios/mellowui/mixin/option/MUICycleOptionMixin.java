package melonystudios.mellowui.mixin.option;

import melonystudios.mellowui.screen.RenderComponents;
import net.minecraft.client.CycleOption;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Function;

@Mixin(CycleOption.class)
public abstract class MUICycleOptionMixin {
    @Shadow
    public static CycleOption<Boolean> createOnOff(String translation, Function<Options, Boolean> getter, CycleOption.OptionSetter<Boolean> setter) {
        return null;
    }

    @Inject(method = "createOnOff(Ljava/lang/String;Lnet/minecraft/network/chat/Component;Ljava/util/function/Function;Lnet/minecraft/client/CycleOption$OptionSetter;)Lnet/minecraft/client/CycleOption;", at = @At("HEAD"), cancellable = true)
    private static void createOnOff(String translation, Component tooltipComponent, Function<Options, Boolean> getter, CycleOption.OptionSetter<Boolean> setter, CallbackInfoReturnable<CycleOption<Boolean>> callback) {
        callback.cancel();
        callback.setReturnValue(createOnOff(translation, getter, setter).setTooltip(minecraft -> {
            List<FormattedCharSequence> lines = minecraft.font.split(tooltipComponent, RenderComponents.TOOLTIP_MAX_WIDTH);
            return value -> lines;
        }));
    }
}
