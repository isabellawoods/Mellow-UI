package melonystudios.mellowui.mixin.update;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.advancements.AdvancementTabGui;
import net.minecraft.client.gui.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screen.IngameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(value = AdvancementsScreen.class, priority = 900)
public abstract class UpdatedAdvancementsScreen extends Screen {
    @Shadow
    private AdvancementTabGui selectedTab;

    public UpdatedAdvancementsScreen(ITextComponent title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    protected void init(CallbackInfo callback) {
        // Done button
        this.addButton(new Button(this.width / 2 - 100, this.height - 25, 200, 20, DialogTexts.GUI_DONE,
                button -> this.minecraft.setScreen(new IngameMenuScreen(true))));
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void renderScreenName(MatrixStack stack, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        drawCenteredString(stack, this.font, new TranslationTextComponent("gui.advancements"), this.width / 2, 16, 0xFFFFFF);
        super.render(stack, mouseX, mouseY, partialTicks);
    }

    @Inject(method = "renderWindow", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;draw(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/util/text/ITextComponent;FFI)I"), cancellable = true)
    public void renderTabName(MatrixStack stack, int x, int y, CallbackInfo callback) {
        callback.cancel();
        this.font.draw(stack, this.selectedTab.getTitle(), (float) (x + 8), (float) (y + 6), 0x404040);
    }
}
