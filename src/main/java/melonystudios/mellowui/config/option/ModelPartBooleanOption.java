package melonystudios.mellowui.config.option;

import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.settings.BooleanOption;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class ModelPartBooleanOption extends BooleanOption {
    private final PlayerModelPart part;

    public ModelPartBooleanOption(PlayerModelPart part, Predicate<GameSettings> getter, BiConsumer<GameSettings, Boolean> setter) {
        super(part.getId(), getter, setter);
        this.part = part;
    }

    @Override
    @Nonnull
    public ITextComponent getMessage(GameSettings options) {
        return DialogTexts.optionStatus(this.part.getName(), options.getModelParts().contains(this.part));
    }
}
