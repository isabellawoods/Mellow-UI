package melonystudios.mellowui.screen.tab;

import net.minecraft.client.gui.widget.Widget;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;

public class TabManager {
    private final Consumer<Widget> addWidget;
    private final Consumer<Widget> removeWidget;
    @Nullable
    private Tab currentTab;

    public TabManager(Consumer<Widget> addWidget, Consumer<Widget> removeWidget) {
        this.addWidget = addWidget;
        this.removeWidget = removeWidget;
    }

    public void openTab(Tab tab) {
        if (!Objects.equals(this.currentTab, tab)) {
            if (this.currentTab != null) {
                this.currentTab.widgets.forEach(this.removeWidget);
                this.currentTab.widgets.clear();
            }
            this.currentTab = tab;
            tab.unmodifiableWidgets.forEach(widget -> {
                tab.widgets.add(widget);
                this.addWidget.accept(widget);
            });
        }
    }

    @Nullable
    public Tab getCurrentTab() {
        return this.currentTab;
    }
}
