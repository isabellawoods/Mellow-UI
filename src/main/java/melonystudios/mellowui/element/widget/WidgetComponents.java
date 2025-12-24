package melonystudios.mellowui.element.widget;

import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.util.Alignment;
import melonystudios.mellowui.util.GUITextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.ConfirmOpenLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.OptionsRowList;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/// ***Widget Components*** is a utility class that provides instances of widgets that are
/// used more than once throughout the codebase.
public class WidgetComponents {
    private final Minecraft minecraft;
    private final Screen screen;
    private final Consumer<Widget> addButton;

    /// ***Widget Components*** is a utility class that provides instances of widgets that are
    /// used more than once throughout the codebase.
    /// @param minecraft The *Minecraft* instance class.
    /// @param screen The screen where these widgets are being added. Can be `null`.
    /// @param addButton A consumer to add buttons to the screen.
    private WidgetComponents(Minecraft minecraft, Screen screen, Consumer<Widget> addButton) {
        this.minecraft = minecraft;
        this.screen = screen;
        this.addButton = addButton;
    }

    /// Creates a new instance of the ***Widget Components***.
    /// @param screen The screen where these widgets are being added. Can be `null`.
    /// @param addButton A consumer to add buttons to the screen.
    public static WidgetComponents components(@Nullable Screen screen, Consumer<Widget> addButton) {
        return new WidgetComponents(Minecraft.getInstance(), screen, addButton);
    }

    /// @return The default instance of the ***Render Components***.
    public RenderComponents components() {
        return RenderComponents.INSTANCE;
    }

    /// Opens the confirmation screen when trying to open a link.
    /// @param lastScreen The parent screen, or the one that lead to this confirmation screen.
    /// @param url The URL to be opened (or not).
    /// @param showWarning Whether to show the "Never open links from people that you don't trust!" text.
    public static void openLink(Screen lastScreen, String url, boolean showWarning) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.setScreen(new ConfirmOpenLinkScreen(confirmed -> {
            if (confirmed) Util.getPlatform().openUri(url);
            minecraft.setScreen(lastScreen);
        }, url, !showWarning));
    }

    public Button done(Alignment alignment) {
        switch (alignment) {
            case LEFT: return this.done(this.screen.width / 2 - 155, 150);
            case RIGHT: return this.done(this.screen.width / 2 + 5, 150);
            case CENTER: default: return this.done(this.screen.width / 2 - 100, 200);
        }
    }

    public Button done(int x, int width) {
        Button doneButton = new Button(x, this.screen.height - 26, width, 20, DialogTexts.GUI_DONE,
                button -> this.screen.onClose());
        this.addButton.accept(doneButton);
        return doneButton;
    }

    /// Creates and adds a new *"Switch Style"* {@link IconButton}.
    /// @param onPressed What happens when this button is {@linkplain net.minecraft.client.gui.widget.button.Button.IPressable pressed}.
    /// @param x The x-position of the button.
    /// @param y The y-position of the button.
    public void switchStyle(Button.IPressable onPressed, int x, int y) {
        this.addButton.accept(new IconButton(x, y, 12, 12, GUITextures.SWITCH_STYLE_SET, new TranslationTextComponent("button.mellowui.switch_style"), onPressed));
    }

    /// Creates and adds a new *"Customize"* {@link IconButton}.
    /// @param onPressed What happens when this button is {@linkplain net.minecraft.client.gui.widget.button.Button.IPressable pressed}.
    /// @param x The x-position of the button.
    /// @param y The y-position of the button.
    public void customize(Button.IPressable onPressed, int x, int y) {
        this.addButton.accept(new IconButton(x, y, 12, 12, GUITextures.CUSTOMIZE_SET, new TranslationTextComponent("button.mellowui.customize.title"), onPressed));
    }

    /// Creates a new *"Enable Packs"* {@link IconButton}.
    /// @param onPressed What happens when this button is {@linkplain net.minecraft.client.gui.widget.button.Button.IPressable pressed}.
    /// @param x The x-position of the button.
    /// @param y The y-position of the button.
    public IconButton enablePacks(Button.IPressable onPressed, int x, int y) {
        return new IconButton(x, y, 12, 12, GUITextures.ENABLE_PACKS_SET, new TranslationTextComponent("button.mellowui.enable_packs.title"), onPressed);
    }

    /// Creates a new *"Disable Packs"* {@link IconButton}.
    /// @param onPressed What happens when this button is {@linkplain net.minecraft.client.gui.widget.button.Button.IPressable pressed}.
    /// @param x The x-position of the button.
    /// @param y The y-position of the button.
    public IconButton disablePacks(Button.IPressable onPressed, int x, int y) {
        return new IconButton(x, y, 12, 12, GUITextures.DISABLE_PACKS_SET, new TranslationTextComponent("button.mellowui.disable_packs.title"), onPressed);
    }

    public OptionsRowList optionsList(int minY, int maxY) {
        return new OptionsRowList(this.minecraft, this.screen.width, this.screen.height, minY, maxY, 25);
    }
}
