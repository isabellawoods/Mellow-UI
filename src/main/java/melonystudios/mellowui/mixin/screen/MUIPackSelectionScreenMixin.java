package melonystudios.mellowui.mixin.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.element.text.TextComponents;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import net.minecraft.client.gui.screens.packs.TransferableSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PackSelectionScreen.class)
public abstract class MUIPackSelectionScreenMixin extends Screen {
    @Shadow
    private TransferableSelectionList availablePackList;
    @Shadow
    private TransferableSelectionList selectedPackList;

    public MUIPackSelectionScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        callback.cancel();
        this.renderBackground(stack);
        this.availablePackList.render(stack, mouseX, mouseY, partialTicks);
        this.selectedPackList.render(stack, mouseX, mouseY, partialTicks);
        drawCenteredString(stack, this.font, this.title.copy().withStyle(TextComponents.titleStyle()), this.width / 2, 8, 0xFFFFFF);
        drawCenteredString(stack, this.font, new TranslatableComponent("pack.dropInfo").withStyle(TextComponents.descriptionStyle()), this.width / 2, 20, 0xFFFFFF);
        super.render(stack, mouseX, mouseY, partialTicks);
    }
}
