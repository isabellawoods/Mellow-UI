package melonystudios.mellowui.config.option;

import melonystudios.mellowui.screen.widget.TooltippedTextField;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.TooltipAccessor;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public class TextFieldOption extends MUIOption implements TooltipAccessor {
    private final String getter;
    private final Consumer<String> setter;
    private final BiConsumer<String, Consumer<String>> responder;
    @Nullable
    private final Component tooltipComponent;
    public EditBox textField;
    public int maxValue = 128;

    public TextFieldOption(String translation, String getter, Consumer<String> setter, BiConsumer<String, Consumer<String>> responder) {
        this(translation, null, getter, setter, responder);
    }

    public TextFieldOption(String translation, @Nullable Component tooltipComponent, String getter, Consumer<String> setter, BiConsumer<String, Consumer<String>> responder) {
        super(translation);
        this.getter = getter;
        this.setter = setter;
        this.responder = responder;
        this.tooltipComponent = tooltipComponent;
    }

    public void tick() {
        if (this.textField != null) this.textField.tick();
    }

    @Override
    @Nonnull
    public AbstractWidget createButton(Options options, int x, int y, int width) {
        if (this.tooltipComponent != null) this.setTooltip(this.tooltipComponent);

        this.textField = new TooltippedTextField(Minecraft.getInstance().font, x + 1, y, width - 2, 18, this.getCaption(), this);
        this.textField.setMaxLength(this.maxValue);
        this.textField.setFocus(false);
        this.textField.setCanLoseFocus(true);
        this.textField.setValue(this.getter);
        this.textField.setResponder(text -> this.responder.accept(text, this.setter));
        return this.textField;
    }
}
