package melonystudios.mellowui.config.option;

import melonystudios.mellowui.screen.popup.EditValueScreen;
import melonystudios.mellowui.widget.EditButton;
import melonystudios.mellowui.widget.EditColorButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EditColorConfigOption extends EditConfigOption {
    public EditColorConfigOption(String translation, @Nullable Component tooltipComponent, ForgeConfigSpec.ConfigValue<?> config) {
        super(translation, tooltipComponent, config);
    }

    public EditColorConfigOption(String translation, ForgeConfigSpec.ConfigValue<?> config) {
        super(translation, config);
    }

    @Override
    @NotNull
    public AbstractWidget createButton(Options options, int x, int y, int width) {
        Minecraft minecraft = Minecraft.getInstance();
        if (this.tooltipComponent != null) this.setTooltip(this.tooltipComponent);

        EditValueScreen screen = new EditValueScreen(minecraft.screen, this.getCaption(), this.config, this.config.get(), true);
        return new EditColorButton(x, y, width, 20, this.getCaption(), this.tooltipComponent, new TranslatableComponent("button.mellowui.edit"), this.config,
                button -> minecraft.setScreen(screen));
    }
}
