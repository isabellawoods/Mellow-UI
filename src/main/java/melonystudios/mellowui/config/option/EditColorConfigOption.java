package melonystudios.mellowui.config.option;

import melonystudios.mellowui.screen.RenderComponents;
import melonystudios.mellowui.screen.popup.EditValueScreen;
import melonystudios.mellowui.screen.popup.ValueType;
import melonystudios.mellowui.widget.EditColorButton;
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

        return new EditColorButton(x, y, width, 20, this.getCaption(), this.tooltipComponent, this.config,
                new TranslationTextComponent("button.mellowui.edit"),
                button -> minecraft.setScreen(new EditValueScreen<>(minecraft.screen, this.getCaption(), ValueType.INTEGER, true)));
    }
}
