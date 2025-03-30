package melonystudios.mellowui.config;

import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class OpenMenuOption extends AbstractOption {
    private final String translation;
    @Nullable
    private final ITextComponent tooltipText;
    private final Screen optionsScreen;

    public OpenMenuOption(String translation, Screen optionsScreen) {
        this(translation, null, optionsScreen);
    }

    public OpenMenuOption(String translation, @Nullable ITextComponent tooltipText, Screen optionsScreen) {
        super(translation);
        this.translation = translation;
        this.tooltipText = tooltipText;
        this.optionsScreen = optionsScreen;
    }

    @Override
    @Nonnull
    public Widget createButton(GameSettings options, int x, int y, int width) {
        if (this.tooltipText != null) this.setTooltip(Minecraft.getInstance().font.split(this.tooltipText, 200));
        return new Button(x, y, width, 20, new TranslationTextComponent(this.translation).withStyle(TextFormatting.BOLD),
                button -> Minecraft.getInstance().setScreen(this.optionsScreen));
    }
}
