package melonystudios.mellowui.mixin.client;

import melonystudios.mellowui.element.text.TextComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.storage.LevelSummary;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@OnlyIn(Dist.CLIENT)
@Mixin(LevelSummary.class)
public class MUILevelSummaryMixin {
    @ModifyArg(method = "createInfo", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/TextComponent;append(Lnet/minecraft/network/chat/Component;)Lnet/minecraft/network/chat/MutableComponent;", ordinal = 0))
    private Component recolorHardcoreText(Component component) {
        return new TranslatableComponent("gameMode.hardcore").withStyle(TextComponents.withColor(0xFF0000));
    }
}
