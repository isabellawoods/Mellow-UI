package melonystudios.mellowui.config.option;

import net.minecraft.client.Options;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.PlayerModelPart;

import javax.annotation.Nonnull;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class ModelPartBooleanOption extends BooleanOption {
    private final PlayerModelPart part;

    public ModelPartBooleanOption(PlayerModelPart part, Predicate<Options> getter, BiConsumer<Options, Boolean> setter) {
        super(part.getId(), getter, setter);
        this.part = part;
    }

    @Override
    @Nonnull
    public Component getMessage(Options options) {
        return CommonComponents.optionStatus(this.part.getName(), options.isModelPartEnabled(this.part));
    }
}
