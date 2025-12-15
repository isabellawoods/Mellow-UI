package melonystudios.mellowui.config.option;

import melonystudios.mellowui.element.widget.EditButton;
import melonystudios.mellowui.screen.popup.EditValueScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.TooltipAccessor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class EditConfigOption extends MUIOption implements TooltipAccessor {
    @Nullable
    protected final Component tooltipComponent;
    protected final ForgeConfigSpec.ConfigValue<?> config;

    public EditConfigOption(String translation, @Nullable Component tooltipComponent, ForgeConfigSpec.ConfigValue<?> config) {
        super(translation);
        this.tooltipComponent = tooltipComponent;
        this.config = config;
    }

    public EditConfigOption(String translation, ForgeConfigSpec.ConfigValue<?> config) {
        this(translation, null, config);
    }

    @Override
    @NotNull
    public AbstractWidget createButton(Options options, int x, int y, int width) {
        Minecraft minecraft = Minecraft.getInstance();
        if (this.tooltipComponent != null) this.setTooltip(this.tooltipComponent);

        EditValueScreen screen = new EditValueScreen(minecraft.screen, this.getCaption(), this.config, this.config.get(), false);
        return new EditButton(x, y, width, 20, this.getCaption(), this.tooltipComponent, new TranslatableComponent("button.mellowui.edit"), this.config,
                button -> minecraft.setScreen(screen));
    }
}
