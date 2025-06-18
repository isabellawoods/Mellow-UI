package melonystudios.mellowui.mixin.update;

import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.config.type.ThreeStyles;
import melonystudios.mellowui.screen.MellomedleyTitleScreen;
import melonystudios.mellowui.util.GUITextures;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.MemoryErrorScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(MemoryErrorScreen.class)
public abstract class UpdatedOutOfMemoryScreen extends Screen {
    public UpdatedOutOfMemoryScreen(ITextComponent title) {
        super(title);
    }

    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    protected void init(CallbackInfo callback) {
        if (MellowConfigs.CLIENT_CONFIGS.updateOutOfMemoryMenu.get()) {
            callback.cancel();

            this.addButton(new Button(this.width / 2 - 155, this.height - 25, 150, 20, new TranslationTextComponent("gui.toTitle"),
                    button -> this.minecraft.setScreen(MellowConfigs.CLIENT_CONFIGS.mainMenuStyle.get() == ThreeStyles.OPTION_3 ? new MellomedleyTitleScreen() : new MainMenuScreen())));
            this.addButton(new Button(this.width / 2 + 5, this.height - 25, 150, 20, new TranslationTextComponent("menu.quit"),
                    button -> this.minecraft.stop()));
        }
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        if (MellowConfigs.CLIENT_CONFIGS.updateOutOfMemoryMenu.get()) {
            callback.cancel();
            // Background
            MellowUtils.renderTiledBackground(stack, GUITextures.OUT_OF_MEMORY_BACKGROUND, this.width, this.height, 0);

            stack.pushPose();
            stack.scale(2, 2, 2);
            this.font.drawShadow(stack, new TranslationTextComponent("menu.mellowui.out_of_memory.sad_face").withStyle(TextFormatting.BOLD), 12, 10, 0xFFFFFF);
            this.font.drawShadow(stack, new TranslationTextComponent("menu.mellowui.out_of_memory.title").withStyle(TextFormatting.BOLD), 12, 25, 0xFFFFFF);
            stack.popPose();

            List<IReorderingProcessor> message = this.font.split(new TranslationTextComponent("menu.mellowui.out_of_memory.message"), this.width - 35);
            int yOffset = 80;
            for (IReorderingProcessor processor : message) {
                this.font.drawShadow(stack, processor, 25, yOffset, 0xFFFFFF);
                yOffset += this.font.lineHeight + 1;
            }

            super.render(stack, mouseX, mouseY, partialTicks);
        }
    }
}
