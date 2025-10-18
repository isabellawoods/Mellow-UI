package melonystudios.mellowui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.config.WidgetConfigs;
import melonystudios.mellowui.screen.RenderComponents;
import melonystudios.mellowui.util.Alignment;
import melonystudios.mellowui.util.text.ScrollingText;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IBidiTooltip;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class EditButton extends Button implements ScrollingText, IBidiTooltip {
    protected final ForgeConfigSpec.ConfigValue<?> config;
    private final ITextComponent configName;
    @Nullable
    private final ITextComponent tooltipComponent;
    private final int realX;

    public EditButton(int x, int y, int width, int height, ITextComponent configName, ITextComponent buttonText, ForgeConfigSpec.ConfigValue<?> config, IPressable whenPressed) {
        super(x + width / 2 + 5, y, width / 2 - 5, height, buttonText, whenPressed);
        this.config = config;
        this.configName = configName;
        this.tooltipComponent = null;
        this.realX = x;
    }

    public EditButton(int x, int y, int width, int height, ITextComponent configName, @Nullable ITextComponent tooltipComponent, ITextComponent buttonText, ForgeConfigSpec.ConfigValue<?> config, IPressable whenPressed) {
        super(x + width / 2 + 5, y, width / 2 - 5, height, buttonText, whenPressed);
        this.config = config;
        this.configName = configName;
        this.tooltipComponent = tooltipComponent;
        this.realX = x;
    }

    @Override
    @Nonnull
    public Optional<List<IReorderingProcessor>> getTooltip() {
        if (this.tooltipComponent != null) {
            return Optional.of(Minecraft.getInstance().font.split(this.tooltipComponent, RenderComponents.TOOLTIP_MAX_WIDTH));
        }
        return Optional.empty();
    }

    @Override
    public void renderButton(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        this.renderWidgetText(
                () -> this.renderString(stack, minecraft.font, this.getFGColor() | MathHelper.ceil(this.alpha * 255F) << 24),
                () -> drawString(stack, minecraft.font, this.configName, this.realX + this.padding(), this.y + (this.height - 8) / 2, this.getFGColor() | MathHelper.ceil(this.alpha * 255F) << 24)
        );
        super.renderButton(stack, mouseX, mouseY, partialTicks);
    }

    public void renderString(MatrixStack stack, FontRenderer font, int color) {
        int padding = this.padding();
        int minX = this.realX + padding;
        int minY = this.y;
        int maxX = this.x - padding - this.colorPadding();
        int maxY = this.y + this.height;
        this.renderAlignedScrollingText(stack, font, this.configName, Alignment.LEFT, minX, minY, maxX, maxY, color);
    }

    protected int colorPadding() {
        return 0;
    }

    private int padding() {
        return MathHelper.clamp(WidgetConfigs.WIDGET_CONFIGS.editButtonTextPadding.get(), 0, this.width / 2 - 1);
    }
}
