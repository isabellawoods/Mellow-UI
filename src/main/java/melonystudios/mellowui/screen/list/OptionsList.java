package melonystudios.mellowui.screen.list;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.OptionButton;
import net.minecraft.client.gui.widget.list.AbstractOptionList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class OptionsList extends AbstractOptionList<OptionsList.Row> {
    public OptionsList(Minecraft minecraft, int width, int height, int y0, int y1, int itemHeight) {
        super(minecraft, width, height, y0, y1, itemHeight);
        this.centerListVertically = false;
    }

    public OptionsList(Minecraft minecraft, int width, int height) {
        this(minecraft, width, height, 32, height - 32, 25);
    }

    public int addBig(AbstractOption option) {
        return this.addEntry(Row.big(this.minecraft.options, this.width, option));
    }

    public void addSmall(AbstractOption leftOption, @Nullable AbstractOption rightOption) {
        this.addEntry(Row.small(this.minecraft.options, this.width, leftOption, rightOption));
    }

    public void addSmall(AbstractOption[] options) {
        for (int i = 0; i < options.length; i += 2) {
            this.addSmall(options[i], i < options.length - 1 ? options[i + 1] : null);
        }
    }

    @Override
    public int getRowWidth() {
        return 310;
    }

    @Override
    protected int getScrollbarPosition() {
        return this.getRealRowRight() + 10;
    }

    private int getRealRowRight() {
        return this.getRealRowLeft() + this.getRowWidth();
    }

    private int getRealRowLeft() {
        return this.x0 + this.width / 2 - this.getRowWidth() / 2;
    }

    @Nullable
    public Widget findWidgetByOption(AbstractOption option) {
        for (Row row : this.children()) {
            for (Widget widget : row.children) {
                if (widget instanceof OptionButton && ((OptionButton) widget).getOption() == option) return widget;
            }
        }
        return null;
    }

    public Optional<Widget> getMouseOver(double mouseX, double mouseY) {
        for (Row row : this.children()) {
            for (Widget widget : row.children) {
                if (widget.isMouseOver(mouseX, mouseY)) return Optional.of(widget);
            }
        }
        return Optional.empty();
    }

    @OnlyIn(Dist.CLIENT)
    public static class Row extends AbstractOptionList.Entry<Row> {
        private final List<Widget> children;

        private Row(List<Widget> widgets) {
            this.children = widgets;
        }

        public static Row big(GameSettings options, int width, AbstractOption option) {
            return new Row(ImmutableList.of(option.createButton(options, width / 2 - 155, 0, 310)));
        }

        public static Row small(GameSettings options, int width, AbstractOption rightOption, @Nullable AbstractOption leftOption) {
            Widget rightWidget = rightOption.createButton(options, width / 2 - 155, 0, 150);
            return leftOption == null ? new Row(ImmutableList.of(rightWidget)) : new Row(ImmutableList.of(rightWidget, leftOption.createButton(options, width / 2 - 155 + 160, 0, 150)));
        }

        @Override
        public void render(MatrixStack stack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean mouseOver, float partialTicks) {
            this.children.forEach(widget -> {
                widget.y = top;
                widget.render(stack, mouseX, mouseY, partialTicks);
            });
        }

        @Override
        @Nonnull
        public List<? extends IGuiEventListener> children() {
            return this.children;
        }
    }
}
