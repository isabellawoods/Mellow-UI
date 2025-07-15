package melonystudios.mellowui.screen.tab;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.screen.RenderComponents;
import melonystudios.mellowui.screen.backport.CreateNewWorldScreen;
import melonystudios.mellowui.screen.widget.TabButton;
import net.minecraft.client.gui.IBidiTooltip;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.gui.screen.IScreen;
import net.minecraft.client.gui.widget.Widget;

import javax.annotation.Nullable;
import java.util.List;

// TODO: ~isa 14-7-25
//  - make adding and removing widgets like in 1.21;
//  - make more compatible for usage in other classes (like customized world options & mellow ui options);
public abstract class TabContents implements IScreen, IRenderable {
    public final RenderComponents components = RenderComponents.INSTANCE;
    public final List<Widget> widgets = Lists.newArrayList();
    @Nullable
    protected CreateNewWorldScreen screen;
    public final String identifier;

    protected TabContents(String identifier) {
        this.identifier = identifier;
    }

    public Widget addWidget(Widget widget) {
        this.widgets.add(widget);
        return widget;
    }

    public void init(CreateNewWorldScreen screen) {
        this.widgets.forEach(screen::addButton);
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
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        for (Widget widget : this.widgets) {
            widget.render(stack, mouseX, mouseY, partialTicks);
            if (this.screen != null && widget instanceof IBidiTooltip && ((IBidiTooltip) widget).getTooltip().isPresent()) {
                if (widget.isMouseOver(mouseX, mouseY)) this.components.renderTooltip(this.screen, widget, ((IBidiTooltip) widget).getTooltip().get(), mouseX, mouseY);
            }
        }
    }
}
