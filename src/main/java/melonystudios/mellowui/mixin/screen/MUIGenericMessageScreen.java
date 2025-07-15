package melonystudios.mellowui.mixin.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.screen.RenderComponents;
import net.minecraft.client.gui.screen.DirtMessageScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(DirtMessageScreen.class)
public abstract class MUIGenericMessageScreen extends Screen {
    @Unique
    private final RenderComponents components = RenderComponents.INSTANCE;
    @Unique
    @Nullable
    private TextFieldWidget textBackground;

    public MUIGenericMessageScreen(ITextComponent title) {
        super(title);
    }

    @Override
    protected void init() {
        this.textBackground = new TextFieldWidget(this.font, this.width / 2, this.height / 2, this.font.width(this.title) + 20, 30, this.title);
        this.textBackground.setMaxLength(128);
        this.textBackground.setEditable(false);
        this.textBackground.x = this.width / 2 - this.textBackground.getWidth() / 2;
        this.textBackground.y = ((this.height / 2) - 9 / 2) - 7;
        this.children.add(this.textBackground);
    }

    @Override
    public void tick() {
        this.textBackground.tick();
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        callback.cancel();
        if (this.title instanceof TranslationTextComponent && ((TranslationTextComponent) this.title).getKey().equals("menu.savingLevel") && MellowConfigs.CLIENT_CONFIGS.updateScreenBackground.get()) {
            this.components.renderPanorama(partialTicks, this.width, this.height, 1);
            this.components.renderBlurredBackground(partialTicks);
            this.renderDirtBackground(0);
        } else {
            this.renderBackground(stack);
        }
        this.textBackground.render(stack, mouseX, mouseY, partialTicks);
        drawCenteredString(stack, this.font, this.title, this.width / 2, this.height / 2, 0xFFFFFF);
        super.render(stack, mouseX, mouseY, partialTicks);
    }
}
