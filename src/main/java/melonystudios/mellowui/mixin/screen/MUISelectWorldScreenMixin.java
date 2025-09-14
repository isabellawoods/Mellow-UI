package melonystudios.mellowui.mixin.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.screen.RenderComponents;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SelectWorldScreen.class)
public class MUISelectWorldScreenMixin extends Screen {
    @Shadow
    protected EditBox searchBox;

    public MUISelectWorldScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "render", at = @At("HEAD"))
    public void renderBackground(PoseStack stack, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        this.renderBackground(stack);
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/worldselection/SelectWorldScreen;drawCenteredString(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)V"))
    public void renderSearchSuggestion(PoseStack stack, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        RenderComponents.INSTANCE.renderTextBoxSuggestion(this.searchBox, MellowUtils.SEARCH_TEXT);
    }
}
