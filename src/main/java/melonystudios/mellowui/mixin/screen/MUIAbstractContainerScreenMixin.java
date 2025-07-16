package melonystudios.mellowui.mixin.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.util.GUITextures;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.ContainerScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.Set;

@Mixin(AbstractContainerScreen.class)
public abstract class MUIAbstractContainerScreenMixin<T extends AbstractContainerMenu> extends Screen implements MenuAccess<T> {
    @Shadow protected int leftPos;
    @Shadow protected int topPos;
    @Shadow protected abstract void renderBg(PoseStack stack, float partialTicks, int mouseX, int mouseY);
    @Shadow @Nullable protected Slot hoveredSlot;
    @Shadow @Final protected T menu;
    @Shadow protected abstract void renderSlot(PoseStack stack, Slot slot);
    @Shadow protected abstract boolean isHovering(Slot slot, double mouseX, double mouseY);
    @Shadow protected abstract void renderLabels(PoseStack stack, int mouseX, int mouseY);
    @Shadow private ItemStack draggingItem;
    @Shadow private boolean isSplittingStack;
    @Shadow protected boolean isQuickCrafting;
    @Shadow @Final protected Set<Slot> quickCraftSlots;
    @Shadow private int quickCraftingRemainder;
    @Shadow protected abstract void renderFloatingItem(ItemStack stack, int x, int y, String altText);
    @Shadow private ItemStack snapbackItem;
    @Shadow private long snapbackTime;
    @Shadow @Nullable private Slot snapbackEnd;
    @Shadow private int snapbackStartX;
    @Shadow private int snapbackStartY;

    public MUIAbstractContainerScreenMixin(Component title) {
        super(title);
    }

    @SuppressWarnings("unchecked")
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        callback.cancel();
        int leftPos1 = this.leftPos;
        int topPos1 = this.topPos;
        this.renderBg(stack, partialTicks, mouseX, mouseY);
        MinecraftForge.EVENT_BUS.post(new ContainerScreenEvent.DrawBackground((AbstractContainerScreen<T>) this.minecraft.screen, stack, mouseX, mouseY));
        RenderSystem.disableDepthTest();
        super.render(stack, mouseX, mouseY, partialTicks);
        PoseStack modelStack = RenderSystem.getModelViewStack();
        modelStack.pushPose();
        modelStack.translate((float) leftPos1, (float) topPos1, 0);
        RenderSystem.applyModelViewMatrix();
        RenderSystem.setShaderColor(1, 1, 1, 1);
        this.hoveredSlot = null;
        RenderSystem.setShaderColor(1, 1, 1, 1);

        for (int i = 0; i < this.menu.slots.size(); ++i) {
            Slot slot = this.menu.slots.get(i);

            if (this.isHovering(slot, mouseX, mouseY) && slot.isActive() && this.minecraft != null && slot.isActive()) {
                this.hoveredSlot = slot;
                RenderSystem.disableDepthTest();
                RenderSystem.enableBlend();
                RenderSystem.enableTexture();
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderColor(1, 1, 1, 1);
                int x = slot.x;
                int y = slot.y;
                RenderSystem.setShaderTexture(0, GUITextures.SLOT_HIGHLIGHT_BACK);
                RenderSystem.colorMask(true, true, true, false);
                blit(stack, x - 4, y - 4, 0, 0, 24, 24, 24, 24);
                RenderSystem.colorMask(true, true, true, true);
                RenderSystem.enableDepthTest();
            }

            if (slot.isActive()) {
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                this.renderSlot(stack, slot);
            }

            if (this.isHovering(slot, mouseX, mouseY) && slot.isActive() && this.minecraft != null && slot.isActive()) {
                RenderSystem.disableDepthTest();
                RenderSystem.enableBlend();
                RenderSystem.enableTexture();
                RenderSystem.setShaderColor(1, 1, 1, 1);
                int x = slot.x;
                int y = slot.y;
                RenderSystem.setShaderTexture(0, GUITextures.SLOT_HIGHLIGHT_FRONT);
                RenderSystem.colorMask(true, true, true, false);
                blit(stack, x - 4, y - 4, 0, 0, 24, 24, 24, 24);
                RenderSystem.colorMask(true, true, true, true);
                RenderSystem.enableDepthTest();
            }
        }

        this.renderLabels(stack, mouseX, mouseY);
        MinecraftForge.EVENT_BUS.post(new ContainerScreenEvent.DrawForeground((AbstractContainerScreen<?>) this.minecraft.screen, stack, mouseX, mouseY));
        ItemStack draggingStack = this.draggingItem.isEmpty() ? this.menu.getCarried() : this.draggingItem;
        if (!draggingStack.isEmpty()) {
            int k2 = this.draggingItem.isEmpty() ? 8 : 16;
            String count = null;
            if (!this.draggingItem.isEmpty() && this.isSplittingStack) {
                draggingStack = draggingStack.copy();
                draggingStack.setCount(Mth.ceil((float) draggingStack.getCount() / 2));
            } else if (this.isQuickCrafting && this.quickCraftSlots.size() > 1) {
                draggingStack = draggingStack.copy();
                draggingStack.setCount(this.quickCraftingRemainder);
                if (draggingStack.isEmpty()) count = ChatFormatting.YELLOW + "0";
            }

            this.renderFloatingItem(draggingStack, mouseX - leftPos1 - 8, mouseY - topPos1 - k2, count);
        }

        if (!this.snapbackItem.isEmpty()) {
            float f = (float) (Util.getMillis() - this.snapbackTime) / 100;
            if (f >= 1) {
                f = 1;
                this.snapbackItem = ItemStack.EMPTY;
            }

            int l2 = this.snapbackEnd.x - this.snapbackStartX;
            int i3 = this.snapbackEnd.y - this.snapbackStartY;
            int l1 = this.snapbackStartX + (int) ((float) l2 * f);
            int i2 = this.snapbackStartY + (int) ((float) i3 * f);
            this.renderFloatingItem(this.snapbackItem, l1, i2, null);
        }

        modelStack.popPose();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.enableDepthTest();
    }
}
