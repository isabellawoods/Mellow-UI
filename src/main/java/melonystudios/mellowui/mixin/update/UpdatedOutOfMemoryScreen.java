package melonystudios.mellowui.mixin.update;

import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.screen.RenderComponents;
import melonystudios.mellowui.util.GUITextures;
import melonystudios.mellowui.util.text.TextComponents;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.OutOfMemoryScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(OutOfMemoryScreen.class)
public abstract class UpdatedOutOfMemoryScreen extends Screen {
    @Unique
    private final RenderComponents components = RenderComponents.INSTANCE;

    public UpdatedOutOfMemoryScreen(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    protected void init(CallbackInfo callback) {
        if (!MellowConfigs.CLIENT_CONFIGS.outOfMemoryStyle.get()) return;
        callback.cancel();

        this.addRenderableWidget(new Button(this.width / 2 - 155, this.height - 25, 150, 20, new TranslatableComponent("gui.toTitle"),
                button -> this.minecraft.setScreen(new TitleScreen())));
        this.addRenderableWidget(new Button(this.width / 2 + 5, this.height - 25, 150, 20, new TranslatableComponent("menu.quit"),
                button -> this.minecraft.stop()));
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        if (!MellowConfigs.CLIENT_CONFIGS.outOfMemoryStyle.get()) return;
        callback.cancel();
        // Background
        this.components.renderPanorama(partialTicks, this.width, this.height, 1);
        this.components.renderBlurredBackground(partialTicks, true);
        this.components.renderTiledBackground(GUITextures.OUT_OF_MEMORY_BACKGROUND, 255, 0, 0, this.width, this.height, 0);

        stack.pushPose();
        stack.scale(2, 2, 2);
        this.font.drawShadow(stack, new TranslatableComponent("menu.mellowui.out_of_memory.sad_face").withStyle(TextComponents.titleStyle().withBold(true)), 12, 10, 0xFFFFFF);
        this.font.drawShadow(stack, new TranslatableComponent("menu.mellowui.out_of_memory.title").withStyle(TextComponents.titleStyle().withBold(true)), 12, 25, 0xFFFFFF);
        stack.popPose();

        List<FormattedCharSequence> lines = this.font.split(new TranslatableComponent("menu.mellowui.out_of_memory.message"), this.width - 35);
        int yOffset = 80;
        for (FormattedCharSequence line : lines) {
            this.font.drawShadow(stack, line, 25, yOffset, 0xFFFFFF);
            yOffset += this.font.lineHeight + 1;
        }

        super.render(stack, mouseX, mouseY, partialTicks);
    }
}
