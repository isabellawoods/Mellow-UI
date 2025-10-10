package melonystudios.mellowui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nullable;

public class EditColorButton extends EditButton {
    public EditColorButton(int x, int y, int width, int height, ITextComponent translation, ForgeConfigSpec.ConfigValue<?> config, ITextComponent component, IPressable whenPressed) {
        super(x, y, width, height, translation, config, component, whenPressed);
    }

    public EditColorButton(int x, int y, int width, int height, ITextComponent translation, @Nullable ITextComponent tooltipComponent, ForgeConfigSpec.ConfigValue<?> config, ITextComponent component, IPressable whenPressed) {
        super(x, y, width, height, translation, tooltipComponent, config, component, whenPressed);
    }

    @Override
    public void renderButton(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        super.renderButton(stack, mouseX, mouseY, partialTicks);
        if (this.config instanceof ForgeConfigSpec.IntValue) {
            try {
                int color = ((ForgeConfigSpec.IntValue) this.config).get();
                fill(stack, this.x - 5, this.y, this.x - 1, this.y + this.height, color | 0xFF000000);
            } catch (NumberFormatException ignored) {}
        }
    }

    @Override
    protected int colorPadding() {
        return 5;
    }
}
