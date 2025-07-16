package melonystudios.mellowui.screen.tab;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.screen.RenderComponents;
import melonystudios.mellowui.screen.backport.CreateNewWorldScreen;
import melonystudios.mellowui.screen.widget.TabButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.TooltipAccessor;
import net.minecraft.client.gui.components.Widget;
import org.jetbrains.annotations.Nullable;

import java.util.List;

// TODO: ~isa 14-7-25
//  - make adding and removing widgets like in 1.21;
//  - make more compatible for usage in other classes (like customized world options & mellow ui options);
public abstract class TabContents implements Widget {
    public final RenderComponents components = RenderComponents.INSTANCE;
    public final List<AbstractWidget> widgets = Lists.newArrayList();
    @Nullable
    protected CreateNewWorldScreen screen;
    public final String identifier;

    protected TabContents(String identifier) {
        this.identifier = identifier;
    }

    public AbstractWidget addWidget(AbstractWidget widget) {
        this.widgets.add(widget);
        return widget;
    }

    public void tick() {}

    public void init(CreateNewWorldScreen screen) {
        this.widgets.forEach(screen::addRenderableWidget);
    }

    public void openTab(String identifier, CreateNewWorldScreen screen, TabButton tab) {
        if (!identifier.equals(this.identifier)) return;
        this.screen = screen;
        screen.selectedTab = this;
        this.widgets.clear();
        this.init(screen);
        screen.uiState().onChanged();
        tab.setSelected(true);
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        for (AbstractWidget widget : this.widgets) {
            widget.render(stack, mouseX, mouseY, partialTicks);
            if (this.screen != null && widget instanceof TooltipAccessor && !((TooltipAccessor) widget).getTooltip().isEmpty()) {
                if (widget.isMouseOver(mouseX, mouseY)) this.components.renderTooltip(this.screen, widget, ((TooltipAccessor) widget).getTooltip(), mouseX, mouseY);
            }
        }
    }
}
