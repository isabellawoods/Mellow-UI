package melonystudios.mellowui.config.option;

import melonystudios.mellowui.screen.EditListConfigScreen;
import melonystudios.mellowui.screen.RenderComponents;
import melonystudios.mellowui.widget.TooltippedButton;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class EditListConfigOption<T> extends EditConfigOption {
    private final ForgeConfigSpec.ConfigValue<List<T>> config;

    public EditListConfigOption(String translation, @Nullable ITextComponent tooltipComponent, ForgeConfigSpec.ConfigValue<List<T>> config) {
        super(translation, tooltipComponent, config);
        this.config = config;
    }

    public EditListConfigOption(String translation, ForgeConfigSpec.ConfigValue<List<T>> config) {
        super(translation, config);
        this.config = config;
    }

    @Override
    @Nonnull
    public Widget createButton(GameSettings options, int x, int y, int width) {
        Minecraft minecraft = Minecraft.getInstance();
        if (this.tooltipComponent != null) this.setTooltip(minecraft.font.split(this.tooltipComponent, RenderComponents.TOOLTIP_MAX_WIDTH));

        return new TooltippedButton(x, y, width, 20, this.getCaption(), this.tooltipComponent,
                button -> minecraft.setScreen(new EditListConfigScreen<>(minecraft.screen, this.getCaption(), this.config)));
    }
}
