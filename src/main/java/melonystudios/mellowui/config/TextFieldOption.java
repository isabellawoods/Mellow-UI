package melonystudios.mellowui.config;

import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public class TextFieldOption extends AbstractOption {
    private final String getter;
    private final Consumer<String> setter;
    private final BiConsumer<String, Consumer<String>> responder;
    @Nullable
    private final ITextComponent tooltipComponent;
    public TextFieldWidget textField;

    public TextFieldOption(String translation, String getter, Consumer<String> setter, BiConsumer<String, Consumer<String>> responder) {
        this(translation, null, getter, setter, responder);
    }

    public TextFieldOption(String translation, @Nullable ITextComponent tooltipComponent, String getter, Consumer<String> setter, BiConsumer<String, Consumer<String>> responder) {
        super(translation);
        this.getter = getter;
        this.setter = setter;
        this.responder = responder;
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
        this.textField.setResponder(text -> this.responder.accept(text, this.setter));
        return this.textField;
    }
}
