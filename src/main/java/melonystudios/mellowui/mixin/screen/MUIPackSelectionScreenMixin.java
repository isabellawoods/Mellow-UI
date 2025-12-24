package melonystudios.mellowui.mixin.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.text.TextComponents;
import net.minecraft.client.gui.screen.PackScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.list.ResourcePackList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PackScreen.class)
public abstract class MUIPackSelectionScreenMixin extends Screen {
    @Unique
    private final RenderComponents components = RenderComponents.INSTANCE;
    @Shadow
    private ResourcePackList availablePackList;
    @Shadow
    private ResourcePackList selectedPackList;

    public MUIPackSelectionScreenMixin(ITextComponent title) {
        super(title);
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        callback.cancel();
        this.renderBackground(stack);
        this.availablePackList.render(stack, mouseX, mouseY, partialTicks);
        this.selectedPackList.render(stack, mouseX, mouseY, partialTicks);
        this.components.drawTitle(this.title.copy().withStyle(TextComponents.titleStyle()), this.width, 8);
        this.components.drawCenteredString(new TranslationTextComponent("pack.dropInfo").withStyle(TextComponents.descriptionStyle()), true, this.width / 2, 20, 0xFFFFFF);
        super.render(stack, mouseX, mouseY, partialTicks);
    }
}
