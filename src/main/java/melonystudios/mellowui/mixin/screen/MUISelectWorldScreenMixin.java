package melonystudios.mellowui.mixin.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.text.TextComponents;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.WorldSelectionScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldSelectionScreen.class)
public class MUISelectWorldScreenMixin extends Screen {
    @Shadow
    protected TextFieldWidget searchBox;

    public MUISelectWorldScreenMixin(ITextComponent title) {
        super(title);
    }

    @Inject(method = "render", at = @At("HEAD"))
    public void renderBackground(MatrixStack stack, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        this.renderBackground(stack);
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/WorldSelectionScreen;drawCenteredString(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/gui/FontRenderer;Lnet/minecraft/util/text/ITextComponent;III)V"))
    public void renderSearchSuggestion(MatrixStack stack, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        RenderComponents.INSTANCE.renderTextBoxSuggestion(this.searchBox, TextComponents.searchText());
    }
}
