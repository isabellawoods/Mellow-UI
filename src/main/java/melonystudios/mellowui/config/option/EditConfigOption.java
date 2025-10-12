package melonystudios.mellowui.config.option;

import melonystudios.mellowui.config.value.ValueEntries;
import melonystudios.mellowui.config.value.ValueEntry;
import melonystudios.mellowui.screen.RenderComponents;
import melonystudios.mellowui.screen.popup.EditValueScreen;
import melonystudios.mellowui.widget.EditButton;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class EditConfigOption extends AbstractOption {
    @Nullable
    protected final ITextComponent tooltipComponent;
    protected final ForgeConfigSpec.ConfigValue<?> config;
    protected final String translation;

    public EditConfigOption(String translation, @Nullable ITextComponent tooltipComponent, ForgeConfigSpec.ConfigValue<?> config) {
        super(translation);
        this.translation = translation;
        this.tooltipComponent = tooltipComponent;
        this.config = config;
    }

    public EditConfigOption(String translation, ForgeConfigSpec.ConfigValue<?> config) {
        this(translation, null, config);
    }

    @Override
    @Nonnull
    public Widget createButton(GameSettings options, int x, int y, int width) {
        Minecraft minecraft = Minecraft.getInstance();
        if (this.tooltipComponent != null) this.setTooltip(minecraft.font.split(this.tooltipComponent, RenderComponents.TOOLTIP_MAX_WIDTH));

        ResourceLocation id = ResourceLocation.tryParse(this.translation.substring(this.translation.indexOf('.') + 1).replace(".", ":"));
        ValueEntry<?> entry = ValueEntries.fromForgeConfig(this.config, id);
        EditValueScreen<?> screen = new EditValueScreen<>(minecraft.screen, this.getCaption(), entry, false);
        screen.configSaver(value -> {
//            if (entry != null) entry.type().saveValue(new ValueSaver.ForgeConfigSaver<>(this.config), value);
        });

        return new EditButton(x, y, width, 20, this.getCaption(), this.tooltipComponent, this.config,
                new TranslationTextComponent("button.mellowui.edit"), button -> minecraft.setScreen(screen));
    }
}
