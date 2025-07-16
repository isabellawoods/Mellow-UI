package melonystudios.mellowui.mixin.screen.list;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.packs.TransferableSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TransferableSelectionList.class)
public abstract class MUITransferableSelectionListMixin<E extends ObjectSelectionList.Entry<E>> extends ObjectSelectionList<E> {
    @Shadow
    @Final
    private Component title;

    public MUITransferableSelectionListMixin(Minecraft minecraft, int width, int height, int y0, int y1, int itemHeight) {
        super(minecraft, width, height, y0, y1, itemHeight);
    }

    @Inject(method = "renderHeader", at = @At("HEAD"), cancellable = true)
    protected void renderHeader(PoseStack stack, int x, int y, Tesselator tessellator, CallbackInfo callback) {
        callback.cancel();
        Component title = ((MutableComponent) this.title).withStyle(ChatFormatting.UNDERLINE, ChatFormatting.BOLD);
        this.minecraft.font.drawShadow(stack, title, (float) (x + this.width / 2 - this.minecraft.font.width(title) / 2), (float) Math.min(this.y0 + 3, y), 0xFFFFFF);
    }
}
