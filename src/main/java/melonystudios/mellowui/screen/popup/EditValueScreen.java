package melonystudios.mellowui.screen.popup;

import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.config.option.BooleanOption;
import melonystudios.mellowui.screen.RenderComponents;
import melonystudios.mellowui.util.text.TextComponents;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.*;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

public class EditValueScreen extends Screen {
    private Component title;
    private final Screen lastScreen;
    private final Component configName;
    public final ForgeConfigSpec.ConfigValue<?> entry;
    public final Object savedValue;
    public final boolean displayColor;

    private Consumer<String> saveConfigValue = this::saveConfigValue;
    private Consumer<ForgeConfigSpec.ConfigValue<?>> revertEdit = this::revertEdit;
    @Nullable
    private EditBox entryWidget;

    public EditValueScreen(Screen lastScreen, Component configName, ForgeConfigSpec.ConfigValue<?> entry, Object savedValue, boolean displayColor) {
        super(NarratorChatListener.NO_TITLE);
        this.title = new TranslatableComponent("menu.mellowui.edit_value.title", configName).withStyle(TextComponents.titleStyle().withBold(true));
        this.lastScreen = lastScreen;
        this.configName = configName;
        this.entry = entry;
        this.savedValue = savedValue;
        this.displayColor = displayColor;
    }

    /// Defines a custom value saver for the config being edited/added.
    /// @param saver A *consumer* for the saver.
    public void configSaver(Consumer<String> saver) {
        this.saveConfigValue = saver;
    }

    /// Defines a reverter for when the "*Revert Edit*" button in clicked.
    /// @param reverter A *consumer* for the reverter.
    public void reverter(Consumer<ForgeConfigSpec.ConfigValue<?>> reverter) {
        this.revertEdit = reverter;
    }

    /// Changes the title of the screen. Defaults to "**Edit Value of \<config name>:**".
    /// @param title A {@linkplain Component text component} for the title.
    public void title(Component title) {
        this.title = title;
    }

    @Override
    @NotNull
    public Component getTitle() {
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
        if (this.entry instanceof ForgeConfigSpec.BooleanValue value && this.configName instanceof TranslatableComponent component) {
            BooleanOption option = new BooleanOption(component.getKey(),
                    options -> value.get(),
                    (options, newValue) -> value.set(!value.get()));
            this.addRenderableWidget(option.createButton(this.minecraft.options, this.width / 2 - 125, this.height / 2 - 10, 250));
        } else if (this.entry instanceof ForgeConfigSpec.IntValue value) {
            this.entryWidget = new EditBox(this.font, this.width / 2 - 125, this.height / 2 - 10, 250, 20, this.configName);
            if (this.displayColor) this.entryWidget.setValue("#" + Integer.toHexString(value.get()));
            else this.entryWidget.setValue(Integer.toString(value.get()));
        } else if (this.entry instanceof ForgeConfigSpec.LongValue value) {
            this.entryWidget = new EditBox(this.font, this.width / 2 - 125, this.height / 2 - 10, 250, 20, this.configName);
            if (this.displayColor) this.entryWidget.setValue("#" + Long.toHexString(value.get()));
            else this.entryWidget.setValue(Long.toString(value.get()));
        } else if (this.entry instanceof ForgeConfigSpec.DoubleValue value) {
            this.entryWidget = new EditBox(this.font, this.width / 2 - 125, this.height / 2 - 10, 250, 20, this.configName);
            this.entryWidget.setValue(Double.toString(value.get()));
        } else {
            Object value = this.entry.get();
            if (value instanceof String) {
                this.entryWidget = new EditBox(this.font, this.width / 2 - 125, this.height / 2 - 10, 250, 20, this.configName);
                this.entryWidget.setValue((String) value);
            }
        }

        // entry text field
        if (this.entryWidget != null) {
            this.entryWidget.setFocus(false);
            this.entryWidget.setCanLoseFocus(true);
            this.entryWidget.setMaxLength(256);
            this.addRenderableWidget(this.entryWidget);
        }

        // revert edit
        this.addRenderableWidget(new Button(this.width / 2 + 133, this.height / 2 - 10, 20, 20, new TranslatableComponent("button.mellowui.revert_edit"),
                button -> this.revertEdit.accept(this.entry),
                (button, stack, mouseX, mouseY) -> RenderComponents.INSTANCE.renderTooltip(this, button,
                        new TranslatableComponent("button.mellowui.revert_edit.tooltip"), mouseX, mouseY)));

        // back
        this.addRenderableWidget(new Button(this.width / 2 - 76, this.height / 2 + 18, 72, 20, CommonComponents.GUI_BACK,
                button -> this.onClose()));

        // submit
        this.addRenderableWidget(new Button(this.width / 2 + 4, this.height / 2 + 18, 72, 20, new TranslatableComponent("button.mellowui.submit"), button -> {
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
    /// @param config The config entry used for reverting.
    public void revertEdit(ForgeConfigSpec.ConfigValue<?> config) {
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
            if (this.entry instanceof ForgeConfigSpec.IntValue intValue) {
                intValue.set(Integer.decode(value));
            } else if (this.entry instanceof ForgeConfigSpec.LongValue longValue) {
                longValue.set(Long.decode(value));
            } else if (this.entry instanceof ForgeConfigSpec.DoubleValue doubleValue) {
                doubleValue.set(Double.parseDouble(value));
            } else {
                Object configValue = this.entry.get();
                if (configValue instanceof String) {
                    ((ForgeConfigSpec.ConfigValue<String>) this.entry).set(value);
                }
            }
        } catch (NumberFormatException ignored) {}
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        boolean showHexMessage = this.entry instanceof ForgeConfigSpec.IntValue || this.entry instanceof ForgeConfigSpec.LongValue;

        if (this.displayColor && this.entry instanceof ForgeConfigSpec.IntValue && this.entryWidget != null) {
            int color = (int) this.entry.get();
            try {
                color = Integer.decode(this.entryWidget.getValue());
            } catch (NumberFormatException ignored) {}

            MutableComponent component = new TranslatableComponent("menu.mellowui.edit_value.title",
                    this.configName.copy().withStyle(TextComponents.withColor(color))).withStyle(TextComponents.titleStyle().withBold(true));
            drawCenteredString(stack, this.font, component, this.width / 2, this.height / 2 - 40, 0xFFFFFF);
        } else {
            drawCenteredString(stack, this.font, this.getTitle(), this.width / 2, this.height / 2 - (showHexMessage ? 40 : 30), 0xFFFFFF);
        }

        if (showHexMessage) drawCenteredString(stack, this.font, new TranslatableComponent("menu.mellowui.edit_value.accepts_hex").withStyle(TextComponents.descriptionStyle()),
                this.width / 2, this.height / 2 - 27, 0xFFFFFF);
        super.render(stack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        } else if (hasControlDown() && keyCode == GLFW.GLFW_KEY_Z) { // ctrl + z to reset config value
            this.revertEdit.accept(this.entry);
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1));
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) { // enter to save config
            if (this.entryWidget instanceof EditBox) {
                String value = this.entryWidget.getValue();
                if (!value.isEmpty() && !value.equals(this.entry.get())) this.saveConfigValue.accept(value);
            }
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1));
            this.onClose();
            return true;
        }
        return false;
    }
}
