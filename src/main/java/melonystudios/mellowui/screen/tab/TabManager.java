package melonystudios.mellowui.screen.tab;

import net.minecraft.client.gui.components.AbstractWidget;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;

public class TabManager {
    private final Consumer<AbstractWidget> addWidget;
    private final Consumer<AbstractWidget> removeWidget;
    @Nullable
    private Tab currentTab;

    public TabManager(Consumer<AbstractWidget> addWidget, Consumer<AbstractWidget> removeWidget) {
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
