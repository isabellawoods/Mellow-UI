package melonystudios.mellowui.mixin.screen.list;

import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.text.TextComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.packs.TransferableSelectionList;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TransferableSelectionList.class)
public abstract class MUITransferableSelectionListMixin<E extends ObjectSelectionList.Entry<E>> extends ObjectSelectionList<E> {
    @Shadow
    @Final
    private Component title;

    public MUITransferableSelectionListMixin(Minecraft minecraft, int width, int height, int y0, int y1, int itemHeight) {
        super(minecraft, width, height, y0, y1, itemHeight);
    }

    @Redirect(method = "renderHeader", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font;draw(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/network/chat/Component;FFI)I"))
    protected int renderHeader(Font font, PoseStack stack, Component oldTitle, float x, float y, int color) {
        Component title = this.title.copy().withStyle(TextComponents.titleStyle().withBold(true).withUnderlined(true));
        RenderComponents.INSTANCE.drawTitle(title, this.width, (int) Math.min(this.y0 + 3, y));
        return color;
    }
}
