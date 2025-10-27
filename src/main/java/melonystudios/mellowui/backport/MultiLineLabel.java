package melonystudios.mellowui.backport;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.LanguageMap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public interface MultiLineLabel {
    MultiLineLabel EMPTY = new MultiLineLabel() {
        @Override
        public void renderCentered(MatrixStack stack, int x, int y) {}

        @Override
        public void renderCentered(MatrixStack stack, int x, int y, int lineHeight, int color) {}

        @Override
        public void renderLeftAligned(MatrixStack stack, int x, int y, int lineHeight, int color) {}

        @Override
        public int renderLeftAlignedNoShadow(MatrixStack stack, int x, int y, int lineHeight, int color) {
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

    static MultiLineLabel create(FontRenderer font, ITextComponent... components) {
        return create(font, Integer.MAX_VALUE, Integer.MAX_VALUE, components);
    }

    static MultiLineLabel create(FontRenderer font, int maxWidth, ITextComponent... components) {
        return create(font, maxWidth, Integer.MAX_VALUE, components);
    }

    static MultiLineLabel create(FontRenderer font, ITextComponent component, int maxWidth) {
        return create(font, maxWidth, Integer.MAX_VALUE, component);
    }

    static MultiLineLabel create(FontRenderer font, int maxWidth, int maxRows, ITextComponent... components) {
        return components.length == 0 ? EMPTY : new MultiLineLabel() {
            @Nullable
            private List<MultiLineLabel.TextAndWidth> cachedTextAndWidth;
            @Nullable
            private LanguageMap splitWithLanguage;

            @Override
            public void renderCentered(MatrixStack stack, int x, int y) {
                this.renderCentered(stack, x, y, 9, -1);
            }

            @Override
            public void renderCentered(MatrixStack stack, int x, int y, int lineHeight, int color) {
                int yOffset = y;

                for (MultiLineLabel.TextAndWidth textAndWidth : this.getSplitMessage()) {
                    font.drawShadow(stack, textAndWidth.text, x - font.width(textAndWidth.text) / 2, yOffset, color);
                    yOffset += lineHeight;
                }
            }

            @Override
            public void renderLeftAligned(MatrixStack stack, int x, int y, int lineHeight, int color) {
                int yOffset = y;

                for (MultiLineLabel.TextAndWidth textAndWidth : this.getSplitMessage()) {
                    font.drawShadow(stack, textAndWidth.text, x, yOffset, color);
                    yOffset += lineHeight;
                }
            }

            @Override
            public int renderLeftAlignedNoShadow(MatrixStack stack, int x, int y, int lineHeight, int color) {
                int yOffset = y;

                for (MultiLineLabel.TextAndWidth multilinelabel$textandwidth : this.getSplitMessage()) {
                    font.draw(stack, multilinelabel$textandwidth.text, x, yOffset, color);
                    yOffset += lineHeight;
                }

                return yOffset;
            }

            private List<MultiLineLabel.TextAndWidth> getSplitMessage() {
                LanguageMap language = LanguageMap.getInstance();

                if (this.cachedTextAndWidth != null && language == this.splitWithLanguage) {
                    return this.cachedTextAndWidth;
                } else {
                    this.splitWithLanguage = language;
                    List<IReorderingProcessor> list = new ArrayList<>();

                    for (ITextComponent component : components) {
                        list.addAll(font.split(component, maxWidth));
                    }

                    this.cachedTextAndWidth = new ArrayList<>();

                    for (IReorderingProcessor line : list.subList(0, Math.min(list.size(), maxRows))) {
                        this.cachedTextAndWidth.add(new MultiLineLabel.TextAndWidth(line, font.width(line)));
                    }

                    return this.cachedTextAndWidth;
                }
            }

            @Override
            public int getLineCount() {
                return this.getSplitMessage().size();
            }

            @Override
            public int getWidth() {
                return Math.min(maxWidth, this.getSplitMessage().stream().mapToInt(MultiLineLabel.TextAndWidth::width).max().orElse(0));
            }
        };
    }

    void renderCentered(MatrixStack stack, int x, int y);

    void renderCentered(MatrixStack stack, int x, int y, int lineHeight, int color);

    void renderLeftAligned(MatrixStack stack, int x, int y, int lineHeight, int color);

    int renderLeftAlignedNoShadow(MatrixStack stack, int x, int y, int lineHeight, int color);

    int getLineCount();

    int getWidth();

    public static class TextAndWidth {
        private final IReorderingProcessor text;
        private final int width;

        public TextAndWidth(IReorderingProcessor text, int width) {
            this.text = text;
            this.width = width;
        }

        public int width() {
            return this.width;
        }
    }
}
