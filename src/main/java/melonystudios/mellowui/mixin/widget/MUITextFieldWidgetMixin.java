package melonystudios.mellowui.mixin.widget;

import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;

//@Mixin(TextFieldWidget.class)
public abstract class MUITextFieldWidgetMixin extends Widget {
    /*@Shadow protected abstract int getMaxLength();
    @Shadow public abstract int getInnerWidth();
    @Shadow protected abstract boolean isBordered();
    @Shadow public abstract boolean isVisible();
    @Shadow protected abstract void renderHighlight(int startX, int startY, int endX, int endY);
    @Shadow private BiFunction<String, Integer, IReorderingProcessor> formatter;
    @Shadow @Final private FontRenderer font;
    @Shadow private String value;
    @Shadow private String suggestion;
    @Shadow private int textColor;
    @Shadow private int textColorUneditable;
    @Shadow private int cursorPos;
    @Shadow private int displayPos;
    @Shadow private int highlightPos;
    @Shadow private int frame;
    @Shadow private boolean isEditable;
    @Shadow private boolean bordered;*/

    public MUITextFieldWidgetMixin(int x, int y, int width, int height, ITextComponent message) {
        super(x, y, width, height, message);
    }

    /*@Inject(method = "renderButton", at = @At("HEAD"), cancellable = true)
    public void renderButton(MatrixStack stack, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        callback.cancel();
        if (this.isVisible()) {
            if (this.isBordered()) {
                RenderSystem.enableBlend();
                Minecraft.getInstance().getTextureManager().bind(this.isFocused() ? GUITextures.TEXT_FIELD_HIGHLIGHTED : GUITextures.TEXT_FIELD);
                AbstractGui.blit(stack, this.x, this.y, 0, 0, this.width, this.height, this.width, this.height);
                RenderSystem.disableBlend();
            }

            int i = this.isEditable ? this.textColor : this.textColorUneditable;
            int i1 = this.cursorPos - this.displayPos;
            int i2 = this.highlightPos - this.displayPos;
            String shortenedText = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), this.getInnerWidth());
            boolean flag = i1 >= 0 && i1 <= shortenedText.length();
            boolean flag1 = this.isFocused() && this.frame / 6 % 2 == 0 && flag;
            int i3 = this.bordered ? this.x + 4 : this.x;
            int i4 = this.bordered ? this.y + (this.height - 8) / 2 : this.y;
            int i5 = i3;
            if (i2 > shortenedText.length()) {
                i2 = shortenedText.length();
            }

            if (!shortenedText.isEmpty()) {
                String string = flag ? shortenedText.substring(0, i1) : shortenedText;
                i5 = this.font.drawShadow(stack, this.formatter.apply(string, this.displayPos), (float)i3, (float)i4, i);
            }

            boolean flag2 = this.cursorPos < this.value.length() || this.value.length() >= this.getMaxLength();
            int i6 = i5;
            if (!flag) {
                i6 = i1 > 0 ? i3 + this.width : i3;
            } else if (flag2) {
                i6 = i5 - 1;
                --i5;
            }

            if (!shortenedText.isEmpty() && flag && i1 < shortenedText.length()) {
                this.font.drawShadow(stack, this.formatter.apply(shortenedText.substring(i1), this.cursorPos), (float) i5, (float) i4, i);
            }

            if (!flag2 && this.suggestion != null) {
                this.font.drawShadow(stack, this.suggestion, (float) (i6 - 1), (float) i4, 0xFF808080);
            }

            if (flag1) {
                if (flag2) {
                    AbstractGui.fill(stack, i6, i4 - 1, i6 + 1, i4 + 1 + 9, 0xFFD0D0D0);
                } else {
                    this.font.drawShadow(stack, new TranslationTextComponent("text_field.cursor"), (float) i6, (float) i4, i);
                }
            }

            if (i2 != i1) {
                int l1 = i3 + this.font.width(shortenedText.substring(0, i2));
                this.renderHighlight(i6, i4 - 1, l1 - 1, i4 + 1 + 9);
            }
        }
    }*/
}
