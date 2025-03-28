package melonystudios.mellowui.config;

import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public class ColorTextFieldOption extends AbstractOption {
    private final String getter;
    private final Consumer<String> setter;
    @Nullable
    private final ITextComponent tooltipComponent;
    public TextFieldWidget textField;

    public ColorTextFieldOption(String translation, String getter, Consumer<String> setter) {
        this(translation, null, getter, setter);
    }

    public ColorTextFieldOption(String translation, @Nullable ITextComponent tooltipComponent, String getter, Consumer<String> setter) {
        super(translation);
        this.getter = getter;
        this.setter = setter;
        this.tooltipComponent = tooltipComponent;
    }

    public void tickTextField() {
        if (this.textField != null) this.textField.tick();
    }

    @Override
    @Nonnull
    public Widget createButton(GameSettings options, int x, int y, int width) {
        if (this.tooltipComponent != null) this.setTooltip(Minecraft.getInstance().font.split(this.tooltipComponent, 200));

        this.textField = new TextFieldWidget(Minecraft.getInstance().font, x, y, width, 20, this.getCaption());
        this.textField.setMaxLength(128);
        this.textField.setFocus(false);
        this.textField.setCanLoseFocus(true);
        this.textField.setValue(this.getter);
        this.textField.setResponder(text -> {
            try {
                int newValue = Integer.parseInt(text);
                this.setter.accept(Integer.toString(MathHelper.clamp(newValue, 0, 16777215)));
            } catch (NumberFormatException ignored) {}
        });
        return this.textField;
    }
}
