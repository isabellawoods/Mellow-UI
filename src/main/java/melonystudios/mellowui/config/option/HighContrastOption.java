package melonystudios.mellowui.config.option;

import com.google.common.collect.Lists;
import melonystudios.mellowui.screen.RenderComponents;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class HighContrastOption extends BooleanOption {
    public HighContrastOption(String translation, @Nullable Component tooltipComponent, Predicate<Options> getter, BiConsumer<Options, Boolean> setter) {
        super(translation, tooltipComponent, getter, setter);
    }

    @Override
    @Nonnull
    public List<FormattedCharSequence> getTooltip() {
        if (this.tooltipComponent != null) {
            if (MellowUtils.highContrastUnavailable()) {
                return Minecraft.getInstance().font.split(new TranslatableComponent("config.minecraft.high_contrast.not_available"), RenderComponents.TOOLTIP_MAX_WIDTH);
            } else {
                return Minecraft.getInstance().font.split(this.tooltipComponent, RenderComponents.TOOLTIP_MAX_WIDTH);
            }
        }
        return Lists.newArrayList();
    }
}
