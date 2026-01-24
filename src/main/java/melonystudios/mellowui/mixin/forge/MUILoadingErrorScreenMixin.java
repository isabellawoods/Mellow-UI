package melonystudios.mellowui.mixin.forge;

import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.resource.panorama.Panoramas;
import melonystudios.mellowui.resource.theme.Themes;
import melonystudios.mellowui.screen.forge.LoadingErrorsScreen;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.ErrorScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.client.gui.LoadingErrorScreen;
import net.minecraftforge.fml.ModLoadingException;
import net.minecraftforge.fml.ModLoadingWarning;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Path;
import java.util.List;

@Mixin(value = LoadingErrorScreen.class, remap = false)
public class MUILoadingErrorScreenMixin extends ErrorScreen {
    @Shadow
    @Final
    private List<ModLoadingException> modLoadErrors;
    @Shadow
    @Final
    private List<ModLoadingWarning> modLoadWarnings;
    @Shadow
    @Final
    private Path dumpedLocation;

    public MUILoadingErrorScreenMixin(Component title, Component message) {
        super(title, message);
    }

    @Inject(method = "init", at = @At("HEAD"), remap = true, cancellable = true)
    protected void init(CallbackInfo callback) {
        // select the background panorama and shaders
        Themes.selectTheme(Themes.theme(), MellowConfigs.CLIENT_CONFIGS.selectedTheme.get());
        Panoramas.selectPanorama(Panoramas.panorama(), MellowConfigs.CLIENT_CONFIGS.selectedPanorama.get());
        if (!MellowConfigs.CLIENT_CONFIGS.loadingErrorsStyle.get()) return;
        callback.cancel();
        this.minecraft.setScreen(new LoadingErrorsScreen(this.modLoadErrors, this.modLoadWarnings, this.dumpedLocation));
        MellowUtils.LOADING_ERRORS |= !this.modLoadErrors.isEmpty();
    }

    @Mixin(value = LoadingErrorScreen.LoadingEntryList.LoadingMessageEntry.class, remap = false)
    public static abstract class MUILoadingMessageEntryMixin extends ObjectSelectionList.Entry<MUILoadingMessageEntryMixin> {
        @Shadow
        @Final
        private Component message;
        @Shadow
        @Final
        private boolean center;

        @Inject(method = "render", at = @At("HEAD"), cancellable = true, remap = true)
        public void render(PoseStack stack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean mouseOver, float partialTicks, CallbackInfo callback) {
            callback.cancel();
            Font font = Minecraft.getInstance().font;
            List<FormattedCharSequence> lines = font.split(this.message, width);
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
