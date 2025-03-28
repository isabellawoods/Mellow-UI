package melonystudios.mellowui.mixin.screen.list;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.gui.widget.list.ResourcePackList;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.text.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ResourcePackList.class)
public abstract class MUIResourcePackListMixin extends ExtendedList<ResourcePackList.ResourcePackEntry> {
    @Shadow
    @Final
    private ITextComponent title;

    public MUIResourcePackListMixin(Minecraft minecraft, int width, int height, int y0, int y1, int itemHeight) {
        super(minecraft, width, height, y0, y1, itemHeight);
    }

    @Inject(method = "renderHeader", at = @At("HEAD"), cancellable = true)
    protected void renderHeader(MatrixStack stack, int x, int y, Tessellator tessellator, CallbackInfo callback) {
        callback.cancel();
        ITextComponent title = ((IFormattableTextComponent) this.title).withStyle(TextFormatting.UNDERLINE, TextFormatting.BOLD);
        this.minecraft.font.drawShadow(stack, title, (float) (x + this.width / 2 - this.minecraft.font.width(title) / 2), (float) Math.min(this.y0 + 3, y), 0xFFFFFF);
    }
}
