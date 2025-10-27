package melonystudios.mellowui.mixin.client;

import melonystudios.mellowui.util.text.TextComponents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.storage.WorldSummary;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@OnlyIn(Dist.CLIENT)
@Mixin(WorldSummary.class)
public class MUIWorldSummaryMixin {
    @ModifyArg(method = "createInfo", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/text/StringTextComponent;append(Lnet/minecraft/util/text/ITextComponent;)Lnet/minecraft/util/text/IFormattableTextComponent;", ordinal = 0))
    private ITextComponent recolorHardcoreText(ITextComponent component) {
        return new TranslationTextComponent("gameMode.hardcore").withStyle(TextComponents.withColor(0xFF0000));
    }
}
