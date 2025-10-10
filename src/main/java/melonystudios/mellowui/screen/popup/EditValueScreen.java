package melonystudios.mellowui.screen.popup;

import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.screen.RenderComponents;
import melonystudios.mellowui.util.MellowUtils;
import melonystudios.mellowui.util.text.TextComponents;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.*;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public class EditValueScreen<T> extends Screen {
    private ITextComponent title;
    private final Screen lastScreen;
    private final ITextComponent configName;
    private final ValueType<T> type;
    private final boolean shouldColorTitle;

    private Consumer<String> saveConfigValue = this::saveConfigValue;
    private Consumer<ValueType<T>> resetToDefault = this::resetToDefault;
    @Nullable
    private TextFieldWidget textField;
    private ValueEntry<T> value;

    public EditValueScreen(Screen lastScreen, ITextComponent configName, ValueType<T> type, boolean shouldColorTitle) {
        super(StringTextComponent.EMPTY);
        this.title = new TranslationTextComponent("menu.mellowui.edit_value.title", configName).withStyle(TextFormatting.BOLD);
        this.lastScreen = lastScreen;
        this.configName = configName;
        this.type = type;
        this.shouldColorTitle = shouldColorTitle;
    }

    public void configSaver(Consumer<String> configSaver) {
        this.saveConfigValue = configSaver;
    }

    public void resetter(Consumer<ValueType<T>> resetter) {
        this.resetToDefault = resetter;
    }

    public void title(ITextComponent title) {
        this.title = title;
    }

    @Override
    @Nonnull
    public ITextComponent getTitle() {
        return this.title;
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) this.minecraft.setScreen(this.lastScreen);
    }

    @Override
    public void tick() {
        if (this.textField != null) this.textField.tick();
    }

    @Override
    protected void init() {
        // main config button
        this.addButton(this.type.addWidget(this.value, this, this.width / 2 - 125, this.height / 2 - 10, 250, 20));

        /*if (this.type instanceof ForgeConfigSpec.BooleanValue) {
            ForgeConfigSpec.BooleanValue value = (ForgeConfigSpec.BooleanValue) this.type;
            this.addButton(new Button(this.width / 2 - 125, this.height / 2 - 10, 250, 20, DialogTexts.optionStatus(value.get()),
                    button -> value.set(!value.get())));
        } else if (this.type instanceof ForgeConfigSpec.IntValue) {
            ForgeConfigSpec.IntValue value = (ForgeConfigSpec.IntValue) this.type;
            this.textField = new TextFieldWidget(this.font, this.width / 2 - 125, this.height / 2 - 10, 250, 20, this.configName);
            this.textField.setValue(Integer.toString(value.get()));
        } else if (this.type instanceof ForgeConfigSpec.LongValue) {
            ForgeConfigSpec.LongValue value = (ForgeConfigSpec.LongValue) this.type;
            this.textField = new TextFieldWidget(this.font, this.width / 2 - 125, this.height / 2 - 10, 250, 20, this.configName);
            this.textField.setValue(Long.toString(value.get()));
        } else if (this.type instanceof ForgeConfigSpec.DoubleValue) {
            ForgeConfigSpec.DoubleValue value = (ForgeConfigSpec.DoubleValue) this.type;
            this.textField = new TextFieldWidget(this.font, this.width / 2 - 125, this.height / 2 - 10, 250, 20, this.configName);
            this.textField.setValue(Double.toString(value.get()));
        } else {
            Object value = this.type.get();
            if (value instanceof String) {
                this.textField = new TextFieldWidget(this.font, this.width / 2 - 125, this.height / 2 - 10, 250, 20, this.configName);
                this.textField.setValue((String) value);
            }
        }*/

        if (this.textField != null) {
            this.textField.setFocus(false);
            this.textField.setCanLoseFocus(true);
            this.addWidget(this.textField);
        }

        // reset to default
        this.addButton(new Button(this.width / 2 + 133, this.height / 2 - 10, 20, 20, new TranslationTextComponent("button.mellowui.reset_to_default"),
                button -> this.resetToDefault.accept(this.type), // todo: somehow make this work ~isa 10-10-25
                (button, stack, mouseX, mouseY) -> RenderComponents.INSTANCE.renderTooltip(this, button,
                        new TranslationTextComponent("button.mellowui.reset_to_default.tooltip"), mouseX, mouseY)));

        // back
        this.addButton(new Button(this.width / 2 - 76, this.height / 2 + 18, 72, 20, DialogTexts.GUI_BACK,
                button -> this.onClose()));

        // submit
        this.addButton(new Button(this.width / 2 + 4, this.height / 2 + 18, 72, 20, new TranslationTextComponent("button.mellowui.submit"), button -> {
            if (this.textField != null) this.saveConfigValue.accept(this.textField.getValue());
            this.onClose();
        }));
    }

    public void saveConfigValue(String value) {
        this.type.saveValue(new ValueSaver.ForgeConfigSaver<>(null), value);
    }

    public void resetToDefault(ValueType<T> type) {}

    /*@SuppressWarnings("unchecked")
    private void saveTextFieldValue(String value) {
        if (this.textField == null) return;

        try {
            if (this.type instanceof ForgeConfigSpec.IntValue) {
                ((ForgeConfigSpec.IntValue) this.type).set(Integer.decode(value));
            } else if (this.type instanceof ForgeConfigSpec.LongValue) {
                ((ForgeConfigSpec.LongValue) this.type).set(Long.decode(value));
            } else if (this.type instanceof ForgeConfigSpec.DoubleValue) {
                ((ForgeConfigSpec.DoubleValue) this.type).set(Double.parseDouble(value));
            } else {
                Object configValue = this.type.get();
                if (configValue instanceof String) {
                    ((ForgeConfigSpec.ConfigValue<String>) this.type).set(value);
                }
            }
        } catch (NumberFormatException ignored) {}
    }*/

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        if (this.textField != null) this.textField.render(stack, mouseX, mouseY, partialTicks);

        if (this.shouldColorTitle && this.type instanceof ForgeConfigSpec.IntValue) {
            try {
                int color = ((ForgeConfigSpec.IntValue) this.type).get();
                IFormattableTextComponent component = new TranslationTextComponent("menu.mellowui.edit_value.title",
                        this.configName.copy().withStyle(MellowUtils.withColor(color))).withStyle(TextFormatting.BOLD);
                drawCenteredString(stack, this.font, component, this.width / 2, this.height / 2 - 40, 0xFFFFFF);
            } catch (NumberFormatException ignored) {}
            drawCenteredString(stack, this.font, new TranslationTextComponent("menu.mellowui.edit_value.accepts_hex").withStyle(TextComponents.descriptionStyle()),
                    this.width / 2, this.height / 2 - 27, 0xFFFFFF);
        } else {
            drawCenteredString(stack, this.font, this.getTitle(), this.width / 2, this.height / 2 - 30, 0xFFFFFF);
        }
        super.render(stack, mouseX, mouseY, partialTicks);
    }
}
