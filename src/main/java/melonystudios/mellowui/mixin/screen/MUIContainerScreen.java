package melonystudios.mellowui.mixin.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import melonystudios.mellowui.util.GUITextures;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.Set;

@SuppressWarnings("deprecation")
@Mixin(ContainerScreen.class)
public abstract class MUIContainerScreen<T extends Container> extends Screen implements IHasContainer<T> {
    @Shadow protected int leftPos;
    @Shadow protected int topPos;
    @Shadow protected abstract void renderBg(MatrixStack stack, float partialTicks, int mouseX, int mouseY);
    @Shadow @Nullable protected Slot hoveredSlot;
    @Shadow @Final protected T menu;
    @Shadow protected abstract void renderSlot(MatrixStack stack, Slot slot);
    @Shadow protected abstract boolean isHovering(Slot slot, double p_195362_2_, double p_195362_4_);
    @Shadow protected abstract void renderLabels(MatrixStack stack, int mouseX, int mouseY);
    @Shadow private ItemStack draggingItem;
    @Shadow private boolean isSplittingStack;
    @Shadow protected boolean isQuickCrafting;
    @Shadow @Final protected Set<Slot> quickCraftSlots;
    @Shadow private int quickCraftingRemainder;
    @Shadow protected abstract void renderFloatingItem(ItemStack p_146982_1_, int p_146982_2_, int p_146982_3_, String p_146982_4_);
    @Shadow private ItemStack snapbackItem;
    @Shadow private long snapbackTime;
    @Shadow @Nullable private Slot snapbackEnd;
    @Shadow private int snapbackStartX;
    @Shadow private int snapbackStartY;

    public MUIContainerScreen(ITextComponent title) {
        super(title);
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        callback.cancel();
        int leftPos1 = this.leftPos;
        int topPos1 = this.topPos;
        this.renderBg(stack, partialTicks, mouseX, mouseY);
        // MinecraftForge.EVENT_BUS.post(new GuiContainerEvent.DrawBackground(this, stack, mouseX, mouseY));
        RenderSystem.disableRescaleNormal();
        RenderSystem.disableDepthTest();
        super.render(stack, mouseX, mouseY, partialTicks);
        RenderSystem.pushMatrix();
        RenderSystem.translatef((float) leftPos1, (float) topPos1, 0);
        RenderSystem.color4f(1, 1, 1, 1);
        RenderSystem.enableRescaleNormal();
        this.hoveredSlot = null;
        RenderSystem.glMultiTexCoord2f(33986, 240, 240);
        RenderSystem.color4f(1, 1, 1, 1);

        for (int i = 0; i < this.menu.slots.size(); ++i) {
            Slot slot = this.menu.slots.get(i);

            if (this.isHovering(slot, mouseX, mouseY) && slot.isActive() && this.minecraft != null) {
                this.hoveredSlot = slot;
                RenderSystem.disableDepthTest();
                RenderSystem.enableBlend();
                RenderSystem.enableAlphaTest();
                RenderSystem.enableTexture();
                RenderSystem.color4f(1, 1, 1, 1);
                int x = slot.x;
                int y = slot.y;
                this.minecraft.textureManager.bind(GUITextures.SLOT_HIGHLIGHT_BACK);
                blit(stack, x - 4, y - 4, 0, 0, 24, 24, 24, 24);
                RenderSystem.enableDepthTest();
            }

            if (slot.isActive()) this.renderSlot(stack, slot);

            if (this.isHovering(slot, mouseX, mouseY) && slot.isActive() && this.minecraft != null) {
                this.hoveredSlot = slot;
                RenderSystem.disableDepthTest();
                RenderSystem.enableAlphaTest();
                RenderSystem.enableBlend();
                RenderSystem.enableTexture();
                RenderSystem.color4f(1, 1, 1, 1);
                int x = slot.x;
                int y = slot.y;
                this.minecraft.textureManager.bind(GUITextures.SLOT_HIGHLIGHT_FRONT);
                blit(stack, x - 4, y - 4, 0, 0, 24, 24, 24, 24);
                RenderSystem.enableDepthTest();
            }
        }

        this.renderLabels(stack, mouseX, mouseY);
        // MinecraftForge.EVENT_BUS.post(new GuiContainerEvent.DrawForeground(this, stack, mouseX, mouseY));
        PlayerInventory inventory = this.minecraft.player.inventory;
        ItemStack draggingStack = this.draggingItem.isEmpty() ? inventory.getCarried() : this.draggingItem;
        if (!draggingStack.isEmpty()) {
            int k2 = this.draggingItem.isEmpty() ? 8 : 16;
            String count = null;
            if (!this.draggingItem.isEmpty() && this.isSplittingStack) {
                draggingStack = draggingStack.copy();
                draggingStack.setCount(MathHelper.ceil((float) draggingStack.getCount() / 2));
            } else if (this.isQuickCrafting && this.quickCraftSlots.size() > 1) {
                draggingStack = draggingStack.copy();
                draggingStack.setCount(this.quickCraftingRemainder);
                if (draggingStack.isEmpty()) count = TextFormatting.YELLOW + "0";
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

        RenderSystem.popMatrix();
        RenderSystem.enableDepthTest();
    }
}
