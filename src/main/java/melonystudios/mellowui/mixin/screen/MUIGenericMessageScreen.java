package melonystudios.mellowui.mixin.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.screen.RenderComponents;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.GenericDirtMessageScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(GenericDirtMessageScreen.class)
public abstract class MUIGenericMessageScreen extends Screen {
    @Unique
    private final RenderComponents components = RenderComponents.INSTANCE;
    @Unique
    @Nullable
    private EditBox textBackground;

    public MUIGenericMessageScreen(Component title) {
        super(title);
    }

    @Override
    protected void init() {
        this.textBackground = new EditBox(this.font, this.width / 2, this.height / 2, this.font.width(this.title) + 20, 30, this.title);
        this.textBackground.setMaxLength(128);
        this.textBackground.setEditable(false);
        this.textBackground.x = this.width / 2 - this.textBackground.getWidth() / 2;
        this.textBackground.y = ((this.height / 2) - 9 / 2) - 7;
        this.addWidget(this.textBackground);
    }

    @Override
    public void tick() {
        this.textBackground.tick();
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        callback.cancel();
        if (this.title instanceof TranslatableComponent && ((TranslatableComponent) this.title).getKey().equals("menu.savingLevel") && MellowConfigs.CLIENT_CONFIGS.updateScreenBackground.get()) {
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
