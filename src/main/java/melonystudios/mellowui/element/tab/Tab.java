package melonystudios.mellowui.element.tab;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.text.TooltipDisplayData;
import melonystudios.mellowui.element.text.TooltipProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IBidiTooltip;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.gui.screen.IScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.OptionButton;

import java.util.List;

/// Represents the contents of a single tab.
public abstract class Tab implements IRenderable, TooltipProvider {
    protected final RenderComponents components = RenderComponents.INSTANCE;
    protected final Minecraft minecraft = Minecraft.getInstance();
    public final List<Widget> widgets = Lists.newArrayList();
    public final List<IScreen> tickingWidgets = Lists.newArrayList();
    public List<Widget> unmodifiableWidgets;
    /// Represents the data of a tooltip that's going to be rendered.
    private TooltipDisplayData tooltipData;

    public Tab() {
        this.init();
    }

    /// Renders this tab.
    /// @param stack The default {@link MatrixStack} used for rendering.
    /// @param mouseX The x position of the mouse cursor.
    /// @param mouseY The y position of the mouse cursor.
    /// @param partialTicks The partial tick time.
    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        for (Widget widget : this.widgets) {
            widget.render(stack, mouseX, mouseY, partialTicks);
            if (this.isMouseOver(mouseX, mouseY, widget) && widget instanceof OptionButton && ((OptionButton) widget).getTooltip().isPresent()) {
                this.setTooltipData(new TooltipDisplayData(((OptionButton) widget).getTooltip().get(), mouseX, mouseY));
            } else if (this.isMouseOver(mouseX, mouseY, widget) && widget instanceof IBidiTooltip && ((IBidiTooltip) widget).getTooltip().isPresent()) {
                this.setTooltipData(new TooltipDisplayData(((IBidiTooltip) widget).getTooltip().get(), mouseX, mouseY));
            }
        }
    }

    /// Checks if the given mouse coordinates are over the GUI element.
    /// @param mouseX The x position of the mouse cursor.
    /// @param mouseY The y position of the mouse cursor.
    /// @param widget The widget being hovered.
    private boolean isMouseOver(int mouseX, int mouseY, Widget widget) {
        return mouseX >= widget.x && mouseY >= widget.y && mouseX < (widget.x + widget.getWidth()) && mouseY < (widget.y + widget.getHeight());
    }

    public void init() {
        this.unmodifiableWidgets = ImmutableList.copyOf(this.widgets);
    }

    public Widget addWidget(Widget widget) {
        this.widgets.add(widget);
        if (widget instanceof IScreen) this.tickingWidgets.add((IScreen) widget);
        return widget;
    }

    /// Gets the {@linkplain TooltipDisplayData display data} for this provider.
    @Override
    public TooltipDisplayData tooltipData() {
        return this.tooltipData;
    }

    /// Sets the display data for the provider.
    /// @param data The display data.
    @Override
    public void setTooltipData(TooltipDisplayData data) {
        this.tooltipData = data;
    }
}
