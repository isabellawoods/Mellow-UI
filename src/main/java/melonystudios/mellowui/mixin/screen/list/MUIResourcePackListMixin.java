package melonystudios.mellowui.mixin.screen.list;

import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.text.TextComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.gui.widget.list.ResourcePackList;
import net.minecraft.util.text.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ResourcePackList.class)
public abstract class MUIResourcePackListMixin extends ExtendedList<ResourcePackList.ResourcePackEntry> {
    @Shadow
    @Final
    private ITextComponent title;

    public MUIResourcePackListMixin(Minecraft minecraft, int width, int height, int y0, int y1, int itemHeight) {
        super(minecraft, width, height, y0, y1, itemHeight);
    }

    @Redirect(method = "renderHeader", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;draw(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/util/text/ITextComponent;FFI)I"))
    protected int renderHeader(FontRenderer font, MatrixStack stack, ITextComponent oldTitle, float x, float y, int color) {
        ITextComponent title = this.title.copy().withStyle(TextComponents.titleStyle().withBold(true).withUnderlined(true));
        RenderComponents.INSTANCE.drawTitle(title, this.width, (int) Math.min(this.y0 + 3, y));
        return font.width(title);
    }
}
