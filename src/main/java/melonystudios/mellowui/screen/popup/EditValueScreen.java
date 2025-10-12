package melonystudios.mellowui.screen.popup;

import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.screen.RenderComponents;
import melonystudios.mellowui.util.MellowUtils;
import melonystudios.mellowui.util.text.TextComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.*;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

public class EditValueScreen extends Screen {
    private Component title;
    private final Screen lastScreen;
    private final Component configName;
    private final ForgeConfigSpec.ConfigValue<?> config;
    private final boolean shouldColorTitle;

    private Consumer<String> saveConfigValue = this::saveConfigValue;
    private Consumer<ForgeConfigSpec.ConfigValue<?>> resetToDefault = this::resetToDefault;
    @Nullable
    private EditBox entryWidget;

    public EditValueScreen(Screen lastScreen, Component configName, ForgeConfigSpec.ConfigValue<?> config, boolean shouldColorTitle) {
        super(TextComponent.EMPTY);
        this.title = new TranslatableComponent("menu.mellowui.edit_value.title", configName).withStyle(ChatFormatting.BOLD);
        this.lastScreen = lastScreen;
        this.configName = configName;
        this.config = config;
        this.shouldColorTitle = shouldColorTitle;
    }

    public void configSaver(Consumer<String> saver) {
        this.saveConfigValue = saver;
    }

    public void resetter(Consumer<ForgeConfigSpec.ConfigValue<?>> resetter) {
        this.resetToDefault = resetter;
    }

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
        if (this.config instanceof ForgeConfigSpec.BooleanValue value) {
            this.addRenderableOnly(new Button(this.width / 2 - 125, this.height / 2 - 10, 250, 20, CommonComponents.optionStatus(value.get()),
                    button -> value.set(!value.get())));
        } else if (this.config instanceof ForgeConfigSpec.IntValue value) {
            this.entryWidget = new EditBox(this.font, this.width / 2 - 125, this.height / 2 - 10, 250, 20, this.configName);
            this.entryWidget.setValue(Integer.toString(value.get()));
        } else if (this.config instanceof ForgeConfigSpec.LongValue value) {
            this.entryWidget = new EditBox(this.font, this.width / 2 - 125, this.height / 2 - 10, 250, 20, this.configName);
            this.entryWidget.setValue(Long.toString(value.get()));
        } else if (this.config instanceof ForgeConfigSpec.DoubleValue value) {
            this.entryWidget = new EditBox(this.font, this.width / 2 - 125, this.height / 2 - 10, 250, 20, this.configName);
            this.entryWidget.setValue(Double.toString(value.get()));
        } else {
            Object value = this.config.get();
            if (value instanceof String) {
                this.entryWidget = new EditBox(this.font, this.width / 2 - 125, this.height / 2 - 10, 250, 20, this.configName);
                this.entryWidget.setValue((String) value);
            }
        }

        if (this.entryWidget != null) {
            this.entryWidget.setFocus(false);
            this.entryWidget.setCanLoseFocus(true);
            this.addRenderableWidget(this.entryWidget);
        }

        // reset to default
        this.addRenderableWidget(new Button(this.width / 2 + 133, this.height / 2 - 10, 20, 20, new TranslatableComponent("button.mellowui.reset_to_default"),
                button -> this.resetToDefault.accept(this.config), // todo: somehow make this work ~isa 10-10-25
                (button, stack, mouseX, mouseY) -> RenderComponents.INSTANCE.renderTooltip(this, button,
                        new TranslatableComponent("button.mellowui.reset_to_default.tooltip"), mouseX, mouseY)));

        // back
        this.addRenderableWidget(new Button(this.width / 2 - 76, this.height / 2 + 18, 72, 20, CommonComponents.GUI_BACK,
                button -> this.onClose()));

        // submit
        this.addRenderableWidget(new Button(this.width / 2 + 4, this.height / 2 + 18, 72, 20, new TranslatableComponent("button.mellowui.submit"), button -> {
            if (this.entryWidget != null) this.saveConfigValue.accept(this.entryWidget.getValue());
            this.onClose();
        }));
    }

    public void saveConfigValue(String value) {
        this.saveTextFieldValue(value);
        this.config.save();
    }

    public void resetToDefault(ForgeConfigSpec.ConfigValue<?> config) {}

    @SuppressWarnings("unchecked")
    private void saveTextFieldValue(String value) {
        if (this.entryWidget == null) return;

        try {
            if (this.config instanceof ForgeConfigSpec.IntValue intValue) {
                intValue.set(Integer.decode(value));
            } else if (this.config instanceof ForgeConfigSpec.LongValue longValue) {
                longValue.set(Long.decode(value));
            } else if (this.config instanceof ForgeConfigSpec.DoubleValue doubleValue) {
                doubleValue.set(Double.parseDouble(value));
            } else {
                Object configValue = this.config.get();
                if (configValue instanceof String) {
                    ((ForgeConfigSpec.ConfigValue<String>) this.config).set(value);
                }
            }
        } catch (NumberFormatException ignored) {}
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);

        if (this.shouldColorTitle && this.config instanceof ForgeConfigSpec.IntValue) {
            try {
                int color = (int) this.config.get();
                MutableComponent component = new TranslatableComponent("menu.mellowui.edit_value.title",
                        this.configName.copy().withStyle(MellowUtils.withColor(color))).withStyle(ChatFormatting.BOLD);
                drawCenteredString(stack, this.font, component, this.width / 2, this.height / 2 - 40, 0xFFFFFF);
            } catch (NumberFormatException ignored) {}
            drawCenteredString(stack, this.font, new TranslatableComponent("menu.mellowui.edit_value.accepts_hex").withStyle(TextComponents.descriptionStyle()),
                    this.width / 2, this.height / 2 - 27, 0xFFFFFF);
        } else {
            drawCenteredString(stack, this.font, this.getTitle(), this.width / 2, this.height / 2 - 30, 0xFFFFFF);
        }
        super.render(stack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        } else if (keyCode != GLFW.GLFW_KEY_ENTER && keyCode != GLFW.GLFW_KEY_KP_ENTER) {
            return false;
        } else {
            if (this.entryWidget instanceof EditBox) {
                String value = this.entryWidget.getValue();
                if (!value.isEmpty() && !value.equals(this.config.get())) this.saveConfigValue.accept(value);
            }
            this.onClose();
            return true;
        }
    }
}
