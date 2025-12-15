package melonystudios.mellowui.config.option;

import melonystudios.mellowui.element.widget.TooltippedButton;
import melonystudios.mellowui.screen.EditListConfigScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EditListConfigOption<T> extends EditConfigOption {
    private final ForgeConfigSpec.ConfigValue<List<T>> config;

    public EditListConfigOption(String translation, @Nullable Component tooltipComponent, ForgeConfigSpec.ConfigValue<List<T>> config) {
        super(translation, tooltipComponent, config);
        this.config = config;
    }

    public EditListConfigOption(String translation, ForgeConfigSpec.ConfigValue<List<T>> config) {
        super(translation, config);
        this.config = config;
    }

    @Override
    @NotNull
    public AbstractWidget createButton(Options options, int x, int y, int width) {
        Minecraft minecraft = Minecraft.getInstance();
        if (this.tooltipComponent != null) this.setTooltip(this.tooltipComponent);

        return new TooltippedButton(x, y, width, 20, this.getCaption(), this.tooltipComponent,
                button -> minecraft.setScreen(new EditListConfigScreen<>(minecraft.screen, this.getCaption(), this.config)));
    }
}
