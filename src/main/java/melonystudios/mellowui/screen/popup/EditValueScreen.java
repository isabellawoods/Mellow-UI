package melonystudios.mellowui.screen.popup;

import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.screen.RenderComponents;
import melonystudios.mellowui.util.MellowUtils;
import melonystudios.mellowui.util.text.TextComponents;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.settings.BooleanOption;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.*;
import net.minecraftforge.common.ForgeConfigSpec;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public class EditValueScreen extends Screen {
    private ITextComponent title;
    private final Screen lastScreen;
    private final ITextComponent configName;
    public final ForgeConfigSpec.ConfigValue<?> entry;
    public final Object savedValue;
    public final boolean displayColor;

    private Consumer<String> saveConfigValue = this::saveConfigValue;
    private Consumer<ForgeConfigSpec.ConfigValue<?>> revertEdit = this::revertEdit;
    @Nullable
    public TextFieldWidget entryWidget;

    public EditValueScreen(Screen lastScreen, ITextComponent configName, ForgeConfigSpec.ConfigValue<?> entry, Object savedValue, boolean displayColor) {
        super(StringTextComponent.EMPTY);
        this.title = new TranslationTextComponent("menu.mellowui.edit_value.title", configName).withStyle(TextFormatting.BOLD);
        this.lastScreen = lastScreen;
        this.configName = configName;
        this.entry = entry;
        this.savedValue = savedValue;
        this.displayColor = displayColor;
    }

    /// Defines a custom value saver for the config being edited/added.
    /// @param configSaver A *consumer* for the saver.
    public void configSaver(Consumer<String> configSaver) {
        this.saveConfigValue = configSaver;
    }

    /// Defines a reverter for when the "*Revert Edit*" button is clicked.
    /// @param reverter A *consumer* for the reverter.
    public void reverter(Consumer<ForgeConfigSpec.ConfigValue<?>> reverter) {
        this.revertEdit = reverter;
    }

    /// Changes the title of the screen. Defaults to "**Edit Value of \<config name>:**".
    /// @param title A {@linkplain ITextComponent text component} for the title.
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
        if (this.entryWidget != null) this.entryWidget.tick();
    }

    @Override
    protected void init() {
        // main config button
        if (this.entry instanceof ForgeConfigSpec.BooleanValue && this.configName instanceof TranslationTextComponent) {
            ForgeConfigSpec.BooleanValue value = (ForgeConfigSpec.BooleanValue) this.entry;
            BooleanOption option = new BooleanOption(((TranslationTextComponent) this.configName).getKey(),
                    options -> value.get(),
                    (options, newValue) -> value.set(!value.get()));
            option.createButton(this.minecraft.options, this.width / 2 - 125, this.height / 2 - 10, 250);
        } else if (this.entry instanceof ForgeConfigSpec.IntValue) {
            this.entryWidget = new TextFieldWidget(this.font, this.width / 2 - 125, this.height / 2 - 10, 250, 20, this.configName);
            this.entryWidget.setValue("#" + Integer.toHexString(((ForgeConfigSpec.IntValue) this.entry).get()));
        } else if (this.entry instanceof ForgeConfigSpec.LongValue) {
            this.entryWidget = new TextFieldWidget(this.font, this.width / 2 - 125, this.height / 2 - 10, 250, 20, this.configName);
            this.entryWidget.setValue("#" + Long.toHexString(((ForgeConfigSpec.LongValue) this.entry).get()));
        } else if (this.entry instanceof ForgeConfigSpec.DoubleValue) {
            this.entryWidget = new TextFieldWidget(this.font, this.width / 2 - 125, this.height / 2 - 10, 250, 20, this.configName);
            this.entryWidget.setValue(Double.toString(((ForgeConfigSpec.DoubleValue) this.entry).get()));
        } else {
            Object value = this.entry.get();
            if (value instanceof String) {
                this.entryWidget = new TextFieldWidget(this.font, this.width / 2 - 125, this.height / 2 - 10, 250, 20, this.configName);
                this.entryWidget.setValue((String) value);
            }
        }

        // entry text field
        if (this.entryWidget != null) {
            this.entryWidget.setFocus(false);
            this.entryWidget.setCanLoseFocus(true);
            this.entryWidget.setMaxLength(256);
            this.addButton(this.entryWidget);
        }

        // revert edit
        this.addButton(new Button(this.width / 2 + 133, this.height / 2 - 10, 20, 20, new TranslationTextComponent("button.mellowui.revert_edit"),
                button -> this.revertEdit.accept(this.entry),
                (button, stack, mouseX, mouseY) -> RenderComponents.INSTANCE.renderTooltip(this, button,
                        new TranslationTextComponent("button.mellowui.revert_edit.tooltip"), mouseX, mouseY)));

        // back
        this.addButton(new Button(this.width / 2 - 76, this.height / 2 + 18, 72, 20, DialogTexts.GUI_BACK,
                button -> this.onClose()));

        // submit
        this.addButton(new Button(this.width / 2 + 4, this.height / 2 + 18, 72, 20, new TranslationTextComponent("button.mellowui.submit"), button -> {
            if (this.entryWidget != null) this.saveConfigValue.accept(this.entryWidget.getValue());
            this.onClose();
        }));
    }

    /// Saves the value of the text field to the config.
    /// @param value The value of the text field.
    public void saveConfigValue(String value) {
        this.saveTextFieldValue(value);
        this.entry.save();
    }

    /// Reverts the text field back to its original value (from before editing, at least).
    ///
    /// For integer and long configs, the values are shown as hexadecimal (`#rrggbb`).
    /// @param entry The config entry used for reverting.
    public void revertEdit(ForgeConfigSpec.ConfigValue<?> entry) {
        if (this.entryWidget == null) return;

        if (this.savedValue instanceof Integer) {
            if (this.displayColor) this.entryWidget.setValue("#" + Integer.toHexString((Integer) this.savedValue));
            else this.entryWidget.setValue(Integer.toString((Integer) this.savedValue));
        } else if (this.savedValue instanceof Long) {
            if (this.displayColor) this.entryWidget.setValue("#" + Long.toHexString((Long) this.savedValue));
            else this.entryWidget.setValue(Long.toString((Long) this.savedValue));
        } else if (this.savedValue instanceof Double) {
            this.entryWidget.setValue(Double.toString((Double) this.savedValue));
        } else {
            this.entryWidget.setValue(this.savedValue.toString());
        }
    }

    @SuppressWarnings("unchecked")
    private void saveTextFieldValue(String value) {
        if (this.entryWidget == null) return;

        try {
            if (this.entry instanceof ForgeConfigSpec.IntValue) {
                ((ForgeConfigSpec.IntValue) this.entry).set(Integer.decode(value));
            } else if (this.entry instanceof ForgeConfigSpec.LongValue) {
                ((ForgeConfigSpec.LongValue) this.entry).set(Long.decode(value));
            } else if (this.entry instanceof ForgeConfigSpec.DoubleValue) {
                ((ForgeConfigSpec.DoubleValue) this.entry).set(Double.parseDouble(value));
            } else {
                Object configValue = this.entry.get();
                if (configValue instanceof String) {
                    ((ForgeConfigSpec.ConfigValue<String>) this.entry).set(value);
                }
            }
        } catch (NumberFormatException ignored) {}
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);

        if (this.displayColor && this.entry instanceof ForgeConfigSpec.IntValue && this.entryWidget != null) {
            int color = (Integer) this.entry.get();
            try {
                color = Integer.decode(this.entryWidget.getValue());
            } catch (NumberFormatException ignored) {}

            IFormattableTextComponent component = new TranslationTextComponent("menu.mellowui.edit_value.title",
                    this.configName.copy().withStyle(MellowUtils.withColor(color))).withStyle(TextFormatting.BOLD);
            drawCenteredString(stack, this.font, component, this.width / 2, this.height / 2 - 40, 0xFFFFFF);
        } else {
            drawCenteredString(stack, this.font, this.getTitle(), this.width / 2, this.height / 2 - 40, 0xFFFFFF);
        }

        drawCenteredString(stack, this.font, new TranslationTextComponent("menu.mellowui.edit_value.accepts_hex").withStyle(TextComponents.descriptionStyle()),
                this.width / 2, this.height / 2 - 27, 0xFFFFFF);
        super.render(stack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        } else if (hasControlDown() && keyCode == GLFW.GLFW_KEY_Z) { // ctrl + z to reset config value
            this.revertEdit(this.entry);
            this.minecraft.getSoundManager().play(SimpleSound.forUI(SoundEvents.UI_BUTTON_CLICK, 1));
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) { // enter to save config
            if (this.entryWidget != null) {
                String value = this.entryWidget.getValue();
                if (!value.isEmpty() && !value.equals(this.entry.get())) this.saveConfigValue.accept(value);
            }
            this.minecraft.getSoundManager().play(SimpleSound.forUI(SoundEvents.UI_BUTTON_CLICK, 1));
            this.onClose();
            return true;
        }
        return false;
    }
}
