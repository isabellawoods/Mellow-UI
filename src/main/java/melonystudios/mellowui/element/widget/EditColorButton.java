package melonystudios.mellowui.element.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.Nullable;

public class EditColorButton extends EditButton {
    public static final int COLOR_DISPLAY_WIDTH = 5;

    public EditColorButton(int x, int y, int width, int height, Component configName, Component buttonText, ForgeConfigSpec.ConfigValue<?> config, OnPress onPress) {
        super(x, y, width, height, configName, buttonText, config, onPress);
    }

    public EditColorButton(int x, int y, int width, int height, Component configName, @Nullable Component tooltipComponent, Component buttonText, ForgeConfigSpec.ConfigValue<?> config, OnPress onPress) {
        super(x, y, width, height, configName, tooltipComponent, buttonText, config, onPress);
    }

    @Override
    public void renderButton(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        super.renderButton(stack, mouseX, mouseY, partialTicks);
        if (this.config instanceof ForgeConfigSpec.IntValue value) {
            try {
                int color = value.get();
                fill(stack, this.x - COLOR_DISPLAY_WIDTH, this.y, this.x - 1, this.y + this.height, color | Mth.ceil(this.alpha * 255F) << 24);
            } catch (NumberFormatException ignored) {}
        }
    }

    @Override
    protected int colorPadding() {
        return COLOR_DISPLAY_WIDTH;
    }
}
