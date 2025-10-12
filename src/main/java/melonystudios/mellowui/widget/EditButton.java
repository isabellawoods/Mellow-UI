package melonystudios.mellowui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.config.WidgetConfigs;
import melonystudios.mellowui.screen.RenderComponents;
import melonystudios.mellowui.util.Alignment;
import melonystudios.mellowui.util.text.ScrollingText;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.TooltipAccessor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EditButton extends Button implements ScrollingText, TooltipAccessor {
    protected final ForgeConfigSpec.ConfigValue<?> config;
    private final Component configName;
    @Nullable
    private final Component tooltipComponent;
    protected final int realX;

    public EditButton(int x, int y, int width, int height, Component configName, Component buttonText, ForgeConfigSpec.ConfigValue<?> config, OnPress onPress) {
        super(x + width / 2 + 5, y, width / 2 - 5, height, buttonText, onPress);
        this.configName = configName;
        this.tooltipComponent = null;
        this.config = config;
        this.realX = x;
    }

    public EditButton(int x, int y, int width, int height, Component configName, @Nullable Component tooltipComponent, Component buttonText, ForgeConfigSpec.ConfigValue<?> config, OnPress onPress) {
        super(x + width / 2 + 5, y, width / 2 - 5, height, buttonText, onPress);
        this.configName = configName;
        this.tooltipComponent = tooltipComponent;
        this.config = config;
        this.realX = x;
    }

    @Override
    @NotNull
    public List<FormattedCharSequence> getTooltip() {
        if (this.tooltipComponent != null) {
            return Minecraft.getInstance().font.split(this.tooltipComponent, RenderComponents.TOOLTIP_MAX_WIDTH);
        }
        return List.of();
    }

    @Override
    public void renderButton(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        this.renderWidgetText(
                () -> this.renderString(stack, minecraft.font, this.getFGColor() | Mth.ceil(this.alpha * 255F) << 24),
                () -> drawString(stack, minecraft.font, this.configName, this.realX + this.padding(), this.y + (this.height - 8) / 2, this.getFGColor() | Mth.ceil(this.alpha * 255F) << 24)
        );
        super.renderButton(stack, mouseX, mouseY, partialTicks);
    }

    private void renderString(PoseStack stack, Font font, int color) {
        int padding = this.padding();
        int minX = this.realX + padding;
        int minY = this.y;
        int maxX = this.x - padding - this.colorPadding();
        int maxY = this.y + this.height;
        this.renderAlignedScrollingText(stack, font, this.configName, Alignment.LEFT, minX, minY, maxX, maxY, color);
    }

    private int padding() {
        return Mth.clamp(WidgetConfigs.WIDGET_CONFIGS.editButtonTextPadding.get(), 0, this.width / 2 - 1);
    }

    protected int colorPadding() {
        return 0;
    }
}
