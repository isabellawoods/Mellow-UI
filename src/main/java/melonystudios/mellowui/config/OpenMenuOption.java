package melonystudios.mellowui.config;

import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;

public class OpenMenuOption extends AbstractOption {
    private final String translation;
    private final Screen optionsScreen;

    public OpenMenuOption(String translation, Screen optionsScreen) {
        super(translation);
        this.translation = translation;
        this.optionsScreen = optionsScreen;
    }

    @Override
    @Nonnull
    public Widget createButton(GameSettings options, int x, int y, int width) {
        return new Button(x, y, width, 20, new TranslationTextComponent(this.translation).withStyle(TextFormatting.BOLD),
                button -> Minecraft.getInstance().setScreen(this.optionsScreen));
    }
}
