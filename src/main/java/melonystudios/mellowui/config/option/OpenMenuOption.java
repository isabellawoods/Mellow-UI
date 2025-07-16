package melonystudios.mellowui.config.option;

import melonystudios.mellowui.screen.widget.TooltippedButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.TooltipAccessor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class OpenMenuOption extends MUIOption implements TooltipAccessor {
    private final String translation;
    private final Screen optionsScreen;
    public boolean boldText = true;

    public OpenMenuOption(String translation, Screen optionsScreen) {
        this(translation, null, optionsScreen);
    }

    public OpenMenuOption(String translation, @Nullable Component tooltipComponent, Screen optionsScreen) {
        super(translation, tooltipComponent);
        this.translation = translation;
        this.optionsScreen = optionsScreen;
    }

    public OpenMenuOption boldText(boolean bold) {
        this.boldText = bold;
        return this;
    }

    @Override
    @Nonnull
    public AbstractWidget createButton(Options options, int x, int y, int width) {
        if (this.tooltipComponent != null) this.setTooltip(this.tooltipComponent);
        return new TooltippedButton(x, y, width, 20, new TranslatableComponent(this.translation).withStyle(style -> style.withBold(this.boldText)), this.tooltipComponent,
                button -> Minecraft.getInstance().setScreen(this.optionsScreen));
    }
}
