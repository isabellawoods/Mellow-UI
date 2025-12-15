package melonystudios.mellowui.element.panel;

/// Represents an empty **panel entry** that serves as a separator.
public class SeparatorPanelEntry extends PanelEntry {
    private final int contentHeight;

    /// Represents an empty **panel entry** that serves as a separator.
    /// @param panel The parent panel.
    /// @param contentHeight The height, in **Minecraft Pixels** (`mpx`), of the content being rendered.
    public SeparatorPanelEntry(Panel panel, int contentHeight) {
        super(panel);
        this.contentHeight = contentHeight;
    }

    @Override
    public int getContentHeight() {
        return this.contentHeight;
    }
}
