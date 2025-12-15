package melonystudios.mellowui.element.text;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public interface MUIMultiLineLabel {
    MUIMultiLineLabel EMPTY = new MUIMultiLineLabel() {
        @Override
        public void renderCentered(PoseStack stack, int x, int y) {}

        @Override
        public void renderCentered(PoseStack stack, int x, int y, int lineHeight, int color) {}

        @Override
        public void renderLeftAligned(PoseStack stack, int x, int y, int lineHeight, int color) {}

        @Override
        public int renderLeftAlignedNoShadow(PoseStack stack, int x, int y, int lineHeight, int color) {
            return y;
        }

        @Override
        public int getLineCount() {
            return 0;
        }

        @Override
        public int getWidth() {
            return 0;
        }
    };

    static MUIMultiLineLabel create(Font font, Component... components) {
        return create(font, Integer.MAX_VALUE, Integer.MAX_VALUE, components);
    }

    static MUIMultiLineLabel create(Font font, int maxWidth, Component... components) {
        return create(font, maxWidth, Integer.MAX_VALUE, components);
    }

    static MUIMultiLineLabel create(Font font, Component component, int maxWidth) {
        return create(font, maxWidth, Integer.MAX_VALUE, component);
    }

    static MUIMultiLineLabel create(Font font, int maxWidth, int maxRows, Component... components) {
        return components.length == 0 ? EMPTY : new MUIMultiLineLabel() {
            @Nullable
            private List<MUIMultiLineLabel.TextAndWidth> cachedTextAndWidth;
            @Nullable
            private Language splitWithLanguage;

            @Override
            public void renderCentered(PoseStack stack, int x, int y) {
                this.renderCentered(stack, x, y, 9, -1);
            }

            @Override
            public void renderCentered(PoseStack stack, int x, int y, int lineHeight, int color) {
                int yOffset = y;

                for (MUIMultiLineLabel.TextAndWidth textAndWidth : this.getSplitMessage()) {
                    font.drawShadow(stack, textAndWidth.text, x - font.width(textAndWidth.text) / 2, yOffset, color);
                    yOffset += lineHeight;
                }
            }

            @Override
            public void renderLeftAligned(PoseStack stack, int x, int y, int lineHeight, int color) {
                int yOffset = y;

                for (MUIMultiLineLabel.TextAndWidth textAndWidth : this.getSplitMessage()) {
                    font.drawShadow(stack, textAndWidth.text, x, yOffset, color);
                    yOffset += lineHeight;
                }
            }

            @Override
            public int renderLeftAlignedNoShadow(PoseStack stack, int x, int y, int lineHeight, int color) {
                int yOffset = y;

                for (MUIMultiLineLabel.TextAndWidth multilinelabel$textandwidth : this.getSplitMessage()) {
                    font.draw(stack, multilinelabel$textandwidth.text, x, yOffset, color);
                    yOffset += lineHeight;
                }

                return yOffset;
            }

            private List<TextAndWidth> getSplitMessage() {
                Language language = Language.getInstance();

                if (this.cachedTextAndWidth == null || language != this.splitWithLanguage) {
                    this.splitWithLanguage = language;
                    List<FormattedCharSequence> list = new ArrayList<>();

                    for (Component component : components) {
                        list.addAll(font.split(component, maxWidth));
                    }

                    this.cachedTextAndWidth = new ArrayList<>();

                    for (FormattedCharSequence line : list.subList(0, Math.min(list.size(), maxRows))) {
                        this.cachedTextAndWidth.add(new TextAndWidth(line, font.width(line)));
                    }
                }
                return this.cachedTextAndWidth;
            }

            @Override
            public int getLineCount() {
                return this.getSplitMessage().size();
            }

            @Override
            public int getWidth() {
                return Math.min(maxWidth, this.getSplitMessage().stream().mapToInt(MUIMultiLineLabel.TextAndWidth::width).max().orElse(0));
            }
        };
    }

    void renderCentered(PoseStack stack, int x, int y);

    void renderCentered(PoseStack stack, int x, int y, int lineHeight, int color);

    void renderLeftAligned(PoseStack stack, int x, int y, int lineHeight, int color);

    int renderLeftAlignedNoShadow(PoseStack stack, int x, int y, int lineHeight, int color);

    int getLineCount();

    int getWidth();

    record TextAndWidth(FormattedCharSequence text, int width) {}
}
