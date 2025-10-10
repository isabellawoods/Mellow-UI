package melonystudios.mellowui.screen.popup;

import melonystudios.mellowui.screen.RenderComponents;
import melonystudios.mellowui.widget.TooltippedTextField;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;

// missing: double, string, lists
public interface ValueType<T> {
    ValueType<String> STRING = new ValueType<String>() {
        @Override
        public void saveValue(ValueSaver<String> saver, String value) {
            saver.saveValue(value);
        }

        @Override
        public Widget addWidget(ValueEntry<String> entry, Screen screen, int x, int y, int width, int height) {
            return new Button(x, y, width, height, entry.translation(),
                    button -> {},
                    (button, stack, mouseX, mouseY) -> {
                        if (entry.tooltip() != null) RenderComponents.INSTANCE.renderTooltip(screen, button, entry.tooltip(), mouseX, mouseY);
                    });
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
                textField.setResponder(value -> entry.set(Integer.decode(value)));
                return textField;
            } else {
                TextFieldWidget textField = new TextFieldWidget(Minecraft.getInstance().font, x, y, width, height, entry.translation());
                textField.setValue(Integer.toString(entry.value()));
                textField.setFocus(false);
                textField.setCanLoseFocus(true);
                textField.setResponder(value -> entry.set(Integer.decode(value)));
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
                return textField;
            } else {
                TextFieldWidget textField = new TextFieldWidget(Minecraft.getInstance().font, x, y, width, height, entry.translation());
                textField.setValue(Long.toString(entry.value()));
                textField.setFocus(false);
                textField.setCanLoseFocus(true);
                return textField;
            }
        }
    };

    void saveValue(ValueSaver<T> saver, String value);

    Widget addWidget(ValueEntry<T> entry, Screen screen, int x, int y, int width, int height);
}
