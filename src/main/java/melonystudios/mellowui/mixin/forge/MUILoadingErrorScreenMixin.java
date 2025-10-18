package melonystudios.mellowui.mixin.forge;

import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.resource.panorama.Panoramas;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.ErrorScreen;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.client.gui.screen.LoadingErrorScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = LoadingErrorScreen.class, remap = false)
public class MUILoadingErrorScreenMixin extends ErrorScreen {
    public MUILoadingErrorScreenMixin(ITextComponent title, ITextComponent message) {
        super(title, message);
    }

    @Inject(method = "init", at = @At("HEAD"), remap = true)
    protected void init(CallbackInfo callback) {
        // select the background panorama and shaders
        Panoramas.selectPanorama(Panoramas.panorama(), MellowConfigs.CLIENT_CONFIGS.selectedPanorama.get());
    }

    @Mixin(value = LoadingErrorScreen.LoadingEntryList.LoadingMessageEntry.class, remap = false)
    public static abstract class MUILoadingMessageEntryMixin extends ExtendedList.AbstractListEntry<MUILoadingMessageEntryMixin> {
        @Shadow
        @Final
        private ITextComponent message;
        @Shadow
        @Final
        private boolean center;

        @Inject(method = "render", at = @At("HEAD"), cancellable = true, remap = true)
        public void render(MatrixStack stack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean mouseOver, float partialTicks, CallbackInfo callback) {
            callback.cancel();
            FontRenderer font = Minecraft.getInstance().font;
            List<IReorderingProcessor> lines = font.split(this.message, width);
            int yOffset = top + 2;
            for (int i = 0; i < Math.min(lines.size(), 2); i++) {
                if (this.center) font.drawShadow(stack, lines.get(i), left + (width) - font.width(lines.get(i)) / 2F, yOffset, 0xFFFFFF);
                else font.drawShadow(stack, lines.get(i), left + 5, yOffset, 0xFFFFFF);
                yOffset += font.lineHeight + 1;
            }
        }

        @Unique
        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int item) {
            if (item == 0 && this.list != null) {
                this.list.setSelected(this);
                this.list.setFocused(this);
                return true;
            }
            return super.mouseClicked(mouseX, mouseY, item);
        }
    }
}
