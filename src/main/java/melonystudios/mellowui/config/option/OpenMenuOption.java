package melonystudios.mellowui.config.option;

import melonystudios.mellowui.screen.RenderComponents;
import melonystudios.mellowui.screen.widget.TooltippedButton;
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
    private final ITextComponent tooltipComponent;
    private final Screen optionsScreen;
    public boolean boldText = true;

    public OpenMenuOption(String translation, Screen optionsScreen) {
        this(translation, null, optionsScreen);
    }

    public OpenMenuOption(String translation, @Nullable ITextComponent tooltipComponent, Screen optionsScreen) {
        super(translation);
        this.translation = translation;
        this.tooltipComponent = tooltipComponent;
        this.optionsScreen = optionsScreen;
    }

    public OpenMenuOption boldText(boolean bold) {
        this.boldText = bold;
        return this;
    }

    @Override
    @Nonnull
    public Widget createButton(GameSettings options, int x, int y, int width) {
        if (this.tooltipComponent != null) this.setTooltip(Minecraft.getInstance().font.split(this.tooltipComponent, RenderComponents.TOOLTIP_MAX_WIDTH));
        return new TooltippedButton(x, y, width, 20, new TranslationTextComponent(this.translation).withStyle(style -> style.withBold(this.boldText)), this.tooltipComponent,
                button -> Minecraft.getInstance().setScreen(this.optionsScreen));
    }
}
