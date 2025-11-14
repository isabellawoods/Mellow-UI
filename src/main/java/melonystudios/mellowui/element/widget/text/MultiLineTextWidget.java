package melonystudios.mellowui.element.widget.text;

import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.element.text.MultiLineLabel;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.function.Function;

public class MultiLineTextWidget extends AbstractStringWidget {
    private OptionalInt maxWidth = OptionalInt.empty();
    private OptionalInt maxRows = OptionalInt.empty();
    private final SingleKeyCache<MultiLineTextWidget.CacheKey, MultiLineLabel> cache;
    private boolean centered = false;

    public MultiLineTextWidget(ITextComponent text, FontRenderer font) {
        this(0, 0, text, font);
    }

    public MultiLineTextWidget(int x, int y, ITextComponent text, FontRenderer font) {
        super(x, y, 0, 0, text, font);
        this.cache = singleKeyCache(cache -> cache.maxRows.isPresent() ?
                MultiLineLabel.create(font, cache.maxWidth, cache.maxRows.getAsInt(), cache.message) :
                MultiLineLabel.create(font, cache.message, cache.maxWidth)
        );
        this.active = false;
    }

    public static <K, V> SingleKeyCache<K, V> singleKeyCache(Function<K, V> computeValue) {
        return new SingleKeyCache<>(computeValue);
    }

    public MultiLineTextWidget setColor(int color) {
        super.setColor(color);
        return this;
    }

    public MultiLineTextWidget setMaxWidth(int maxWidth) {
        this.maxWidth = OptionalInt.of(maxWidth);
        return this;
    }

    public MultiLineTextWidget setMaxRows(int maxRows) {
        this.maxRows = OptionalInt.of(maxRows);
        return this;
    }

    public MultiLineTextWidget setCentered(boolean centered) {
        this.centered = centered;
        return this;
    }

    @Override
    public int getWidth() {
        return this.cache.getValue(this.getFreshCacheKey()).getWidth();
    }

    @Override
    public int getHeight() {
        return this.cache.getValue(this.getFreshCacheKey()).getLineCount() * 9;
    }

    @Override
    public void renderButton(MatrixStack stack, int mouseX, int mouseY, float partialTick) {
        MultiLineLabel label = this.cache.getValue(this.getFreshCacheKey());
        if (this.centered) {
            label.renderCentered(stack, this.x + this.getWidth() / 2, this.y, 9, this.getColor());
        } else {
            label.renderLeftAligned(stack, this.x, this.y, 9, this.getColor());
        }
    }

    @Override
    protected boolean clicked(double mouseX, double mouseY) {
        return this.active && this.visible && mouseX >= (double) this.x && mouseY >= (double) this.y && mouseX < (double) (this.x + this.getWidth()) && mouseY < (double) (this.y + this.getHeight());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean clicked = this.clicked(mouseX, mouseY);
        this.setFocused(clicked);
        if (this.active && this.visible && this.isValidClickButton(button) && clicked) {
            this.onClick(mouseX, mouseY);
            return true;
        }
        return clicked;
    }

    private MultiLineTextWidget.CacheKey getFreshCacheKey() {
        return new MultiLineTextWidget.CacheKey(this.getMessage(), this.maxWidth.orElse(Integer.MAX_VALUE), this.maxRows);
    }

    @OnlyIn(Dist.CLIENT)
    static class CacheKey {
        private final ITextComponent message;
        private final int maxWidth;
        private final OptionalInt maxRows;

        CacheKey(ITextComponent message, int maxWidth, OptionalInt maxRows) {
            this.message = message;
            this.maxWidth = maxWidth;
            this.maxRows = maxRows;
        }
    }

    public static class SingleKeyCache<K, V> {
        private final Function<K, V> computeValue;
        @Nullable
        private K cacheKey = null;
        @Nullable
        private V cachedValue;

        public SingleKeyCache(Function<K, V> computeValue) {
            this.computeValue = computeValue;
        }

        public V getValue(K cacheKey) {
            if (this.cachedValue == null || !Objects.equals(this.cacheKey, cacheKey)) {
                this.cachedValue = this.computeValue.apply(cacheKey);
                this.cacheKey = cacheKey;
            }
            return this.cachedValue;
        }
    }
}
