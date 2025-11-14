package melonystudios.mellowui.config.option;

import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.widget.EditColorButton;
import melonystudios.mellowui.screen.popup.EditValueScreen;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EditColorConfigOption extends EditConfigOption {
    public EditColorConfigOption(String translation, @Nullable ITextComponent tooltipComponent, ForgeConfigSpec.ConfigValue<?> config) {
        super(translation, tooltipComponent, config);
    }

    public EditColorConfigOption(String translation, ForgeConfigSpec.ConfigValue<?> config) {
        super(translation, config);
    }

    @Override
    @Nonnull
    public Widget createButton(GameSettings options, int x, int y, int width) {
        Minecraft minecraft = Minecraft.getInstance();
        if (this.tooltipComponent != null) this.setTooltip(minecraft.font.split(this.tooltipComponent, RenderComponents.TOOLTIP_MAX_WIDTH));

        EditValueScreen screen = new EditValueScreen(minecraft.screen, this.getCaption(), this.config, this.config.get(), true);
        return new EditColorButton(x, y, width, 20, this.getCaption(), this.tooltipComponent, new TranslationTextComponent("button.mellowui.edit"), this.config,
                button -> minecraft.setScreen(screen));
    }
}
