package melonystudios.mellowui.mixin.update;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static melonystudios.mellowui.config.VanillaConfigEntries.*;
import static net.minecraft.client.Option.*;

@Mixin(value = AccessibilityOptionsScreen.class, priority = 900)
public abstract class UpdatedAccessibilityScreen extends OptionsSubScreen {
    @Mutable @Shadow @Final private static Option[] OPTIONS;
    @Shadow protected abstract void createFooter();

    @Unique
    private static final List<Option> UPDATED_OPTIONS = Lists.newArrayList(NARRATOR, SHOW_SUBTITLES, HIGH_CONTRAST, AUTO_JUMP, MENU_BACKGROUND_BLURRINESS, TEXT_BACKGROUND_OPACITY, TEXT_BACKGROUND, CHAT_OPACITY, CHAT_LINE_SPACING, CHAT_DELAY, VIEW_BOBBING, TOGGLE_CROUCH, TOGGLE_SPRINT,
            SCREEN_EFFECTS_SCALE, FOV_EFFECTS_SCALE, HIDE_LIGHTNING_FLASH, DARK_MOJANG_STUDIOS_BACKGROUND_COLOR, PANORAMA_SCROLL_SPEED, HIDE_SPLASH_TEXTS);
    @Unique
    private OptionsList list;

    public UpdatedAccessibilityScreen(Screen lastScreen, Options options, Component title) {
        super(lastScreen, options, title);
    }

    @Override
    protected void init() {
        if (MellowConfigs.CLIENT_CONFIGS.updateAccessibilityMenu.get()) {
            this.list = new OptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
            for (Option option : OPTIONS) {
                if (!UPDATED_OPTIONS.contains(option)) UPDATED_OPTIONS.add(option);
            }
            this.list.addSmall(UPDATED_OPTIONS.toArray(new Option[0]));
            this.addWidget(this.list);
            this.createFooter();
            AbstractWidget narratorButton = this.list.findOption(NARRATOR);
            if (narratorButton != null) narratorButton.active = NarratorChatListener.INSTANCE.isActive();

            AbstractWidget highContrastButton = this.list.findOption(HIGH_CONTRAST);
            if (highContrastButton != null && MellowUtils.highContrastUnavailable()) {
                highContrastButton.active = false;
            }
        } else {
            super.init();
        }
    }

    @Inject(method = "createFooter", at = @At("HEAD"), cancellable = true)
    public void createFooter(CallbackInfo callback) {
        if (MellowConfigs.CLIENT_CONFIGS.updateAccessibilityMenu.get()) {
            callback.cancel();

            // Accessibility Guide
            this.addRenderableWidget(new Button(this.width / 2 - 155, this.height - 25, 150, 20, new TranslatableComponent("options.accessibility.link"),
                    button -> MellowUtils.openLink(this, "https://aka.ms/MinecraftJavaAccessibility", false)));

            // Done
            this.addRenderableWidget(new Button(this.width / 2 + 5, this.height - 25, 150, 20, CommonComponents.GUI_DONE,
                    button -> this.minecraft.setScreen(this.lastScreen)));
        }
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        if (MellowConfigs.CLIENT_CONFIGS.updateAccessibilityMenu.get()) {
            this.renderBackground(stack);
            this.list.render(stack, mouseX, mouseY, partialTicks);
            drawCenteredString(stack, this.font, new TranslatableComponent("menu.minecraft.accessibility_settings.title"), this.width / 2, MellowUtils.DEFAULT_TITLE_HEIGHT, 0xFFFFFF);
            for (Widget widget : this.renderables) widget.render(stack, mouseX, mouseY, partialTicks);
            List<FormattedCharSequence> processors = tooltipAt(this.list, mouseX, mouseY);
            if (!processors.isEmpty()) this.renderTooltip(stack, processors, mouseX, mouseY);
        } else {
            super.render(stack, mouseX, mouseY, partialTicks);
        }
    }
}
