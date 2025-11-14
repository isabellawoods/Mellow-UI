package melonystudios.mellowui.element.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nullable;

public class EditColorButton extends EditButton {
    public EditColorButton(int x, int y, int width, int height, ITextComponent configName, ITextComponent buttonText, ForgeConfigSpec.ConfigValue<?> config, IPressable whenPressed) {
        super(x, y, width, height, configName, buttonText, config, whenPressed);
    }

    public EditColorButton(int x, int y, int width, int height, ITextComponent configName, @Nullable ITextComponent tooltipComponent, ITextComponent buttonText, ForgeConfigSpec.ConfigValue<?> config, IPressable whenPressed) {
        super(x, y, width, height, configName, tooltipComponent, buttonText, config, whenPressed);
    }

    @Override
    public void renderButton(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        super.renderButton(stack, mouseX, mouseY, partialTicks);
        if (this.config instanceof ForgeConfigSpec.IntValue) {
            try {
                int color = ((ForgeConfigSpec.IntValue) this.config).get();
                fill(stack, this.x - 5, this.y, this.x - 1, this.y + this.height, color | MathHelper.ceil(this.alpha * 255F) << 24);
            } catch (NumberFormatException ignored) {}
        }
    }

    @Override
    protected int colorPadding() {
        return 5;
    }
}
