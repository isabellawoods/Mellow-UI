package melonystudios.mellowui.mixin.update;

import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.element.widget.WidgetComponents;
import melonystudios.mellowui.util.Alignment;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(value = AdvancementsScreen.class, priority = 900)
public abstract class UpdatedAdvancementsScreen extends Screen {
    @Unique
    private final RenderComponents components = RenderComponents.INSTANCE;
    @Shadow
    @Nullable
    private AdvancementTab selectedTab;
    @Shadow
    private boolean isScrolling;

    public UpdatedAdvancementsScreen(Component title) {
        super(title);
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) this.minecraft.setScreen(new PauseScreen(true));
    }

    @Inject(method = "init", at = @At("TAIL"))
    protected void init(CallbackInfo callback) {
        // Done button
        WidgetComponents.components(this, this::addRenderableWidget).done(Alignment.CENTER);
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void renderScreenName(PoseStack stack, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        this.components.drawTitle(new TranslatableComponent("gui.advancements").withStyle(TextComponents.titleStyle()), this.width, 16);
        super.render(stack, mouseX, mouseY, partialTicks);
    }

    @Inject(method = "renderWindow", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font;draw(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/network/chat/Component;FFI)I"), cancellable = true)
    public void renderTabName(PoseStack stack, int offsetX, int offsetY, CallbackInfo callback) {
        if (this.selectedTab == null) return;
        callback.cancel();
        this.components.drawString(this.selectedTab.getTitle(), false, offsetX + 8, offsetY + 6, 0x404040);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.isScrolling = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }
}
