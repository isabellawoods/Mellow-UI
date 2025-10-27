package melonystudios.mellowui.mixin.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.screen.RenderComponents;
import melonystudios.mellowui.widget.text.FocusableTextWidget;
import net.minecraft.client.gui.screen.DirtMessageScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DirtMessageScreen.class)
public abstract class MUIGenericMessageScreen extends Screen {
    public MUIGenericMessageScreen(ITextComponent title) {
        super(title);
    }

    @Override
    protected void init() {
        FocusableTextWidget textBackground = this.addButton(new FocusableTextWidget(this.width, this.title, this.font, 12));
        textBackground.containWithin(this.width);
        textBackground.x = this.width / 2 - textBackground.getWidth() / 2;
        textBackground.y = this.height / 2 - 9 / 2;
    }

    @Override
    public void renderBackground(MatrixStack stack) {
        RenderComponents components = RenderComponents.INSTANCE;
        float partialTicks = this.minecraft.getDeltaFrameTime();
        components.renderPanorama(partialTicks, this.width, this.height, 1);
        components.renderBlurredBackground(partialTicks, null);
        components.renderMenuBackground(0, 0, this.width, this.height, 0);
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void cancelForegroundRendering(MatrixStack stack, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        callback.cancel();
        this.renderBackground(stack);
        super.render(stack, mouseX, mouseX, partialTicks);
    }
}
