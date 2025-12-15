package melonystudios.mellowui.mixin.client;

import melonystudios.mellowui.element.text.TextComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.storage.LevelSummary;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@OnlyIn(Dist.CLIENT)
@Mixin(LevelSummary.class)
public abstract class MUILevelSummaryMixin {
    @Shadow public abstract boolean isLocked();
    @Shadow public abstract boolean isCompatible();
    @Shadow public abstract boolean isHardcore();
    @Shadow public abstract boolean hasCheats();
    @Shadow(remap = false) public abstract boolean isExperimental();
    @Shadow public abstract boolean markVersionInList();
    @Shadow public abstract boolean askToOpenWorld();
    @Shadow public abstract boolean requiresManualConversion();
    @Shadow public abstract GameType getGameMode();
    @Shadow public abstract MutableComponent getWorldVersionName();

    @Inject(method = "getInfo", at = @At("HEAD"), cancellable = true)
    private void addHardcoreAndExperimental(CallbackInfoReturnable<Component> callback) {
        if (this.isLocked()) {
            callback.setReturnValue((new TranslatableComponent("selectWorld.locked")).withStyle(ChatFormatting.RED));
        } else if (this.requiresManualConversion()) {
            callback.setReturnValue((new TranslatableComponent("selectWorld.conversion")).withStyle(ChatFormatting.RED));
        } else if (!this.isCompatible()) {
            callback.setReturnValue((new TranslatableComponent("selectWorld.incompatible_series")).withStyle(ChatFormatting.RED));
        } else {
            MutableComponent component = this.isHardcore() ? new TextComponent("").append(new TranslatableComponent("gameMode.hardcore").withStyle(TextComponents.withColor(0xFF0000))) : new TranslatableComponent("gameMode." + this.getGameMode().getName());

            if (this.hasCheats()) {
                component.append(", ").append(new TranslatableComponent("selectWorld.cheats"));
            }

            if (this.isExperimental()) {
                component.append(", ").append(new TranslatableComponent("menu.mellowui.select_world.experimental").withStyle(ChatFormatting.YELLOW));
            }

            MutableComponent versionName = this.getWorldVersionName();
            MutableComponent version = (new TextComponent(", ")).append(new TranslatableComponent("selectWorld.version")).append(" ");
            if (this.markVersionInList()) {
                version.append(versionName.withStyle(this.askToOpenWorld() ? ChatFormatting.RED : ChatFormatting.ITALIC));
            } else {
                version.append(versionName);
            }

            component.append(version);
            callback.setReturnValue(component);
        }
    }
}
