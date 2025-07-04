package melonystudios.mellowui.config.option;

import melonystudios.mellowui.screen.widget.TooltippedButton;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class OpenMenuOption extends AbstractOption {
    private final String translation;
    @Nullable
    private final ITextComponent tooltipText;
    private final Screen optionsScreen;
    private final boolean onlyInWorld;
    public boolean boldText = true;

    public OpenMenuOption(String translation, Screen optionsScreen) {
        this(translation, false, null, optionsScreen);
    }

    public OpenMenuOption(String translation, boolean onlyInWorld, @Nullable ITextComponent tooltipText, Screen optionsScreen) {
        super(translation);
        this.translation = translation;
        this.onlyInWorld = onlyInWorld;
        this.tooltipText = tooltipText;
        this.optionsScreen = optionsScreen;
    }

    public OpenMenuOption boldText(boolean bold) {
        this.boldText = bold;
        return this;
    }

    @Override
    @Nonnull
    public Widget createButton(GameSettings options, int x, int y, int width) {
        if (this.tooltipText != null) this.setTooltip(Minecraft.getInstance().font.split(this.tooltipText, MellowUtils.TOOLTIP_MAX_WIDTH));
        TooltippedButton menuButton = new TooltippedButton(x, y, width, 20, new TranslationTextComponent(this.translation).withStyle(style -> style.withBold(this.boldText)), this.tooltipText,
                button -> Minecraft.getInstance().setScreen(this.optionsScreen));
        menuButton.active = !this.onlyInWorld;
        return menuButton;
    }
}
