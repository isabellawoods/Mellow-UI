package melonystudios.mellowui.mixin.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.config.WidgetConfigs;
import melonystudios.mellowui.screen.RenderComponents;
import melonystudios.mellowui.util.text.TextComponents;
import melonystudios.mellowui.widget.TickingWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BiFunction;

@OnlyIn(Dist.CLIENT)
@Mixin(EditBox.class)
public abstract class MUIEditBoxMixin extends AbstractWidget implements TickingWidget {
    @Shadow public abstract boolean isVisible();
    @Shadow public abstract int getInnerWidth();
    @Shadow protected abstract int getMaxLength();
    @Shadow protected abstract boolean isBordered();
    @Shadow protected abstract boolean isEditable();
    @Shadow protected abstract void renderHighlight(int startX, int startY, int endX, int endY);
    @Shadow private BiFunction<String, Integer, FormattedCharSequence> formatter;
    @Shadow private String value;
    @Shadow private String suggestion;
    @Shadow private boolean bordered;
    @Shadow private int textColor;
    @Shadow private int textColorUneditable;
    @Shadow private int cursorPos;
    @Shadow private int displayPos;
    @Shadow private int highlightPos;
    @Shadow private int frame;

    public MUIEditBoxMixin(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    @Inject(method = "renderButton", at = @At("HEAD"), cancellable = true)
    public void renderButton(PoseStack stack, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        callback.cancel();
        if (!this.isVisible()) return;
        Font font = Minecraft.getInstance().font;
        WidgetConfigs configs = WidgetConfigs.WIDGET_CONFIGS;
        int alpha = Mth.ceil(this.alpha * 255F);

        if (this.isBordered()) {
            int borderColor = this.isHoveredOrFocused() ? configs.textFieldHighlightedBorderColor.get() | alpha << 24 : configs.textFieldDefaultBorderColor.get() | alpha << 24;
            fill(stack, this.x, this.y, this.x + this.width, this.y + this.height, borderColor);
            fill(stack, this.x + 1, this.y + 1, this.x + this.width - 1, this.y + this.height - 1, configs.textFieldCenterColor.get() | alpha << 24);
        }

        int textColor = this.isEditable() ? this.textColor : this.textColorUneditable;
        int j = this.cursorPos - this.displayPos;
        int length = this.highlightPos - this.displayPos;
        String displayedText = font.plainSubstrByWidth(this.value.substring(this.displayPos), this.getInnerWidth());
        boolean longerThanWidget = j >= 0 && j <= displayedText.length();
        boolean shouldBlink = this.isFocused() && this.isEditable() && this.frame / 6 % 2 == 0 && longerThanWidget;
        int textX = this.bordered ? this.x + 4 : this.x;
        int textY = this.bordered ? this.y + (this.height - 8) / 2 : this.y;
        int newX = textX;

        if (length > displayedText.length()) length = displayedText.length();

        if (!displayedText.isEmpty()) {
            String s1 = longerThanWidget ? displayedText.substring(0, j) : displayedText;
            newX = font.drawShadow(stack, this.formatter.apply(s1, this.displayPos), (float) textX, (float) textY, textColor);
        }

        boolean tooLong = this.cursorPos < this.value.length() || this.value.length() >= this.getMaxLength();
        int cursorX = newX;
        if (!longerThanWidget) {
            cursorX = j > 0 ? textX + this.width : textX;
        } else if (tooLong) {
            cursorX = newX - 1;
            --newX;
        }

        if (!displayedText.isEmpty() && longerThanWidget && j < displayedText.length()) {
            font.drawShadow(stack, this.formatter.apply(displayedText.substring(j), this.cursorPos), (float) newX, (float) textY, textColor);
        }

        if (!tooLong && this.suggestion != null) {
            font.drawShadow(stack, this.suggestion, (float) (cursorX - 1), (float) textY, configs.textFieldSuggestionColor.get() | alpha << 24);
        }

        if (shouldBlink) {
            int selectedColor = configs.textFieldHighlightedBorderColor.get();
            if (tooLong) {
                fill(stack, cursorX, textY - 1, cursorX + 1, textY + 9, selectedColor | alpha << 24);
                fill(stack, cursorX + 1, textY, cursorX + 2, textY + 10, TextComponents.darkenColor(selectedColor, 1, 0.25F));
            } else {
                font.drawShadow(stack, new TranslatableComponent("text_field.cursor"), (float) cursorX, (float) textY, selectedColor);
            }
        }

        if (length != j) {
            int endX = textX + font.width(displayedText.substring(0, length));
            this.renderHighlight(cursorX, textY - 1, endX - 1, textY + 9);
        }

        if (this.isFocused()) {
            this.renderToolTip(stack, this.x + this.width, this.y);
        } else if (this.isHovered) {
            this.renderToolTip(stack, mouseX, mouseY);
        }
    }

    @Inject(method = "renderHighlight", at = @At("HEAD"), cancellable = true)
    public void renderHighlight(int startX, int startY, int endX, int endY, CallbackInfo callback) {
        callback.cancel();
        if (startX < endX) {
            int i = startX;
            startX = endX;
            endX = i;
        }

        if (startY < endY) {
            int j = startY;
            startY = endY;
            endY = j;
        }

        if (endX > this.x + this.width) endX = this.x + this.width - 1;
        if (startX > this.x + this.width) startX = this.x + this.width - 1;

        fill(RenderComponents.INSTANCE.poseStack(), startX, startY, endX, endY, WidgetConfigs.WIDGET_CONFIGS.textFieldHighlightColor.get() | 153 << 24);
    }
}
