package melonystudios.mellowui.mixin.update;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(value = AdvancementsScreen.class, priority = 900)
public abstract class UpdatedAdvancementsScreen extends Screen {
    @Shadow
    @Nullable
    private AdvancementTab selectedTab;

    public UpdatedAdvancementsScreen(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    protected void init(CallbackInfo callback) {
        // Done button
        this.addRenderableWidget(new Button(this.width / 2 - 100, this.height - 25, 200, 20, CommonComponents.GUI_DONE,
                button -> this.minecraft.setScreen(new PauseScreen(true))));
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void renderScreenName(PoseStack stack, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        drawCenteredString(stack, this.font, new TranslatableComponent("gui.advancements"), this.width / 2, 16, 0xFFFFFF);
        super.render(stack, mouseX, mouseY, partialTicks);
    }

    @Inject(method = "renderWindow", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font;draw(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/network/chat/Component;FFI)I"), cancellable = true)
    public void renderTabName(PoseStack stack, int offsetX, int offsetY, CallbackInfo callback) {
        if (this.selectedTab == null) return;
        callback.cancel();
        this.font.draw(stack, this.selectedTab.getTitle(), (float) (offsetX + 8), (float) (offsetY + 6), 0x404040);
    }
}
