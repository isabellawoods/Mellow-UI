package melonystudios.mellowui.config.value;

import melonystudios.mellowui.screen.RenderComponents;
import melonystudios.mellowui.widget.TooltippedTextField;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;

// missing: lists
public interface ValueType<T> {
    int TEXT_FIELD_MAX_LENGTH = 1024;

    ValueType<String> STRING = new ValueType<String>() {
        @Override
        public void saveValue(ValueSaver<String> saver, String value) {
            saver.saveValue(value);
        }

        @Override
        public Widget addWidget(ValueEntry<String> entry, Screen screen, int x, int y, int width, int height) {
            if (entry.tooltip() != null) {
                TooltippedTextField textField = new TooltippedTextField(Minecraft.getInstance().font, x, y, width, height, entry.translation(), entry.tooltip());
                textField.setValue(entry.value());
                textField.setFocus(false);
                textField.setCanLoseFocus(true);
                textField.setMaxLength(TEXT_FIELD_MAX_LENGTH);
                textField.setResponder(entry::set);
                return textField;
            } else {
                TextFieldWidget textField = new TextFieldWidget(Minecraft.getInstance().font, x, y, width, height, entry.translation());
                textField.setValue(entry.value());
                textField.setFocus(false);
                textField.setCanLoseFocus(true);
                textField.setMaxLength(TEXT_FIELD_MAX_LENGTH);
                textField.setResponder(entry::set);
                return textField;
            }
        }
    };

    ValueType<Boolean> BOOLEAN = new ValueType<Boolean>() {
        @Override
        public void saveValue(ValueSaver<Boolean> saver, String value) {
            saver.saveValue(Boolean.valueOf(value));
        }

        @Override
        public Widget addWidget(ValueEntry<Boolean> entry, Screen screen, int x, int y, int width, int height) {
            return new Button(x, y, width, height, entry.translation(),
                    button -> entry.set(!entry.value()),
                    (button, stack, mouseX, mouseY) -> {
                        if (entry.tooltip() != null) RenderComponents.INSTANCE.renderTooltip(screen, button, entry.tooltip(), mouseX, mouseY);
            });
        }
    };

    ValueType<Integer> INTEGER = new ValueType<Integer>() {
        @Override
        public void saveValue(ValueSaver<Integer> saver, String value) {
            try {
                saver.saveValue(Integer.decode(value));
            } catch (NumberFormatException ignored) {}
        }

        @Override
        public Widget addWidget(ValueEntry<Integer> entry, Screen screen, int x, int y, int width, int height) {
            if (entry.tooltip() != null) {
                TooltippedTextField textField = new TooltippedTextField(Minecraft.getInstance().font, x, y, width, height, entry.translation(), entry.tooltip());
                textField.setValue(Integer.toString(entry.value()));
                textField.setFocus(false);
                textField.setCanLoseFocus(true);
                textField.setMaxLength(TEXT_FIELD_MAX_LENGTH);
                textField.setResponder(value -> {
                    try {
                        entry.set(Integer.decode(value));
                    } catch (NumberFormatException ignored) {}
                });
                return textField;
            } else {
                TextFieldWidget textField = new TextFieldWidget(Minecraft.getInstance().font, x, y, width, height, entry.translation());
                textField.setValue(Integer.toString(entry.value()));
                textField.setFocus(false);
                textField.setCanLoseFocus(true);
                textField.setMaxLength(TEXT_FIELD_MAX_LENGTH);
                textField.setResponder(value -> {
                    try {
                        entry.set(Integer.decode(value));
                    } catch (NumberFormatException ignored) {}
                });
                return textField;
            }
        }
    };

    ValueType<Long> LONG = new ValueType<Long>() {
        @Override
        public void saveValue(ValueSaver<Long> saver, String value) {
            try {
                saver.saveValue(Long.decode(value));
            } catch (NumberFormatException ignored) {}
        }

        @Override
        public Widget addWidget(ValueEntry<Long> entry, Screen screen, int x, int y, int width, int height) {
            if (entry.tooltip() != null) {
                TooltippedTextField textField = new TooltippedTextField(Minecraft.getInstance().font, x, y, width, height, entry.translation(), entry.tooltip());
                textField.setValue(Long.toString(entry.value()));
                textField.setFocus(false);
                textField.setCanLoseFocus(true);
                textField.setMaxLength(TEXT_FIELD_MAX_LENGTH);
                textField.setResponder(value -> {
                    try {
                        entry.set(Long.decode(value));
                    } catch (NumberFormatException ignored) {}
                });
                return textField;
            } else {
                TextFieldWidget textField = new TextFieldWidget(Minecraft.getInstance().font, x, y, width, height, entry.translation());
                textField.setValue(Long.toString(entry.value()));
                textField.setFocus(false);
                textField.setCanLoseFocus(true);
                textField.setMaxLength(TEXT_FIELD_MAX_LENGTH);
                textField.setResponder(value -> {
                    try {
                        entry.set(Long.decode(value));
                    } catch (NumberFormatException ignored) {}
                });
                return textField;
            }
        }
    };

    ValueType<Double> DOUBLE = new ValueType<Double>() {
        @Override
        public void saveValue(ValueSaver<Double> saver, String value) {
            try {
                saver.saveValue(Double.valueOf(value));
            } catch (NumberFormatException ignored) {}
        }

        @Override
        public Widget addWidget(ValueEntry<Double> entry, Screen screen, int x, int y, int width, int height) {
            if (entry.tooltip() != null) {
                TooltippedTextField textField = new TooltippedTextField(Minecraft.getInstance().font, x, y, width, height, entry.translation(), entry.tooltip());
                textField.setValue(Double.toString(entry.value()));
                textField.setFocus(false);
                textField.setCanLoseFocus(true);
                textField.setMaxLength(TEXT_FIELD_MAX_LENGTH);
                return textField;
            } else {
                TextFieldWidget textField = new TextFieldWidget(Minecraft.getInstance().font, x, y, width, height, entry.translation());
                textField.setValue(Double.toString(entry.value()));
                textField.setFocus(false);
                textField.setCanLoseFocus(true);
                textField.setMaxLength(TEXT_FIELD_MAX_LENGTH);
                return textField;
            }
        }
    };

    void saveValue(ValueSaver<T> saver, String value);

    Widget addWidget(ValueEntry<T> entry, Screen screen, int x, int y, int width, int height);
}
