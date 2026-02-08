package melonystudios.mellowui.mixin.update;

import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.element.widget.WidgetComponents;
import melonystudios.mellowui.util.Alignment;
import net.minecraft.client.gui.advancements.AdvancementTabGui;
import net.minecraft.client.gui.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screen.IngameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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
    private AdvancementTabGui selectedTab;
    @Shadow
    private boolean isScrolling;

    public UpdatedAdvancementsScreen(ITextComponent title) {
        super(title);
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) this.minecraft.setScreen(new IngameMenuScreen(true));
    }

    @Inject(method = "init", at = @At("TAIL"))
    protected void init(CallbackInfo callback) {
        // Done button
        WidgetComponents.components(this, this::addButton).done(Alignment.CENTER);
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void renderScreenName(MatrixStack stack, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        this.components.drawTitle(new TranslationTextComponent("gui.advancements").withStyle(TextComponents.titleStyle()), this.width, 16);
        super.render(stack, mouseX, mouseY, partialTicks);
    }

    @Inject(method = "renderWindow", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;draw(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/util/text/ITextComponent;FFI)I"), cancellable = true)
    public void renderTabName(MatrixStack stack, int offsetX, int offsetY, CallbackInfo callback) {
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
