package melonystudios.mellowui.screen.tab;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.screen.RenderComponents;
import melonystudios.mellowui.util.text.TooltipDisplayData;
import melonystudios.mellowui.util.text.TooltipProvider;
import melonystudios.mellowui.widget.TickingWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.TooltipAccessor;
import net.minecraft.client.gui.components.Widget;

import java.util.ArrayList;
import java.util.List;

/// Represents the contents of a single tab.
public abstract class Tab implements Widget, TickingWidget, TooltipProvider {
    protected final RenderComponents components = RenderComponents.INSTANCE;
    protected final Minecraft minecraft = Minecraft.getInstance();
    public final List<AbstractWidget> widgets = new ArrayList<>();
    public final List<TickingWidget> tickingWidgets = new ArrayList<>();
    public List<AbstractWidget> unmodifiableWidgets;
    /// Represents the data of a tooltip that's going to be rendered.
    private TooltipDisplayData tooltipData;

    public Tab() {
        this.init();
    }

    /// Renders this tab.
    /// @param stack The default {@link PoseStack} used for rendering.
    /// @param mouseX The x position of the mouse cursor.
    /// @param mouseY The y position of the mouse cursor.
    /// @param partialTicks The partial tick time.
    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        for (AbstractWidget widget : this.widgets) {
            widget.render(stack, mouseX, mouseY, partialTicks);
            if (this.isMouseOver(mouseX, mouseY, widget) && widget instanceof TooltipAccessor accessor && !accessor.getTooltip().isEmpty()) {
                this.setTooltipData(new TooltipDisplayData(accessor.getTooltip(), mouseX, mouseY));
            }
        }
    }

    /// Checks if the given mouse coordinates are over the GUI element.
    /// @param mouseX The x position of the mouse cursor.
    /// @param mouseY The y position of the mouse cursor.
    /// @param widget The widget being hovered.
    private boolean isMouseOver(int mouseX, int mouseY, AbstractWidget widget) {
        return mouseX >= widget.x && mouseY >= widget.y && mouseX < (widget.x + widget.getWidth()) && mouseY < (widget.y + widget.getHeight());
    }

    public void init() {
        this.unmodifiableWidgets = ImmutableList.copyOf(this.widgets);
    }

    public AbstractWidget addWidget(AbstractWidget widget) {
        this.widgets.add(widget);
        if (widget instanceof TickingWidget ticking) this.tickingWidgets.add(ticking);
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
