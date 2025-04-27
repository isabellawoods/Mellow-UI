package melonystudios.mellowui.mixin.updates;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screen.IngameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(value = AdvancementsScreen.class, priority = 900)
public abstract class UpdatedAdvancementsScreen extends Screen {
    public UpdatedAdvancementsScreen(ITextComponent title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    protected void init(CallbackInfo callback) {
        // Done button
        this.addButton(new Button(this.width / 2 - 100, this.height - 27, 200, 20, DialogTexts.GUI_DONE,
                button -> this.minecraft.setScreen(new IngameMenuScreen(true))));
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        drawCenteredString(stack, this.font, new TranslationTextComponent("gui.advancements"), this.width / 2, 16, 0xFFFFFF);
        super.render(stack, mouseX, mouseY, partialTicks);
    }
}
