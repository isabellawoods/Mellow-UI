package melonystudios.mellowui.mixin.screen.list;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import melonystudios.mellowui.util.text.TextComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.packs.TransferableSelectionList;
import net.minecraft.network.chat.Component;
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
        Component title = this.title.copy().withStyle(TextComponents.titleStyle().withBold(true).withUnderlined(true));
        this.minecraft.font.drawShadow(stack, title, (float) (x + this.width / 2 - this.minecraft.font.width(title) / 2), (float) Math.min(this.y0 + 3, y), 0xFFFFFF);
    }
}
