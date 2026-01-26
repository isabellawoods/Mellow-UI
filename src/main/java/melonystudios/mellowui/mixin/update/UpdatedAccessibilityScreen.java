package melonystudios.mellowui.mixin.update;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.config.option.OpenMenuOption;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.element.widget.WidgetComponents;
import melonystudios.mellowui.element.widget.WidgetLocations;
import melonystudios.mellowui.screen.backport.MUIControlsScreen;
import melonystudios.mellowui.util.Alignment;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.Minecraft;
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
    @Unique
    private final OpenMenuOption controls = new OpenMenuOption("options.controls", new MUIControlsScreen(this, Minecraft.getInstance().options)).boldText(false);
    @Mutable
    @Shadow
    @Final
    private static Option[] OPTIONS;
    @Shadow
    protected abstract void createFooter();

    @Unique
    private static final List<Option> UPDATED_OPTIONS = Lists.newArrayList(CLOSED_CAPTIONS, HIGH_CONTRAST, MENU_BACKGROUND_BLURRINESS, TEXT_BACKGROUND_OPACITY, TEXT_BACKGROUND, CHAT_OPACITY, CHAT_LINE_SPACING, CHAT_DELAY, VIEW_BOBBING,
            SCREEN_EFFECTS_SCALE, FOV_EFFECTS_SCALE, HIDE_LIGHTNING_FLASH, DARK_MOJANG_STUDIOS_BACKGROUND_COLOR, PANORAMA_SCROLL_SPEED, HIDE_SPLASH_TEXTS);
    @Unique
    private OptionsList list;

    public UpdatedAccessibilityScreen(Screen lastScreen, Options options, Component title) {
        super(lastScreen, options, title);
    }

    @Override
    protected void init() {
        if (!MellowConfigs.CLIENT_CONFIGS.accessibilitySettingsStyle.get()) super.init();
        this.list = WidgetComponents.components(this, this::addRenderableWidget).optionsList(33, this.height - 33);
        for (Option option : OPTIONS) {
            if (!UPDATED_OPTIONS.contains(option) && option != NARRATOR && option != AUTO_JUMP && option != SHOW_SUBTITLES &&
                    option != TOGGLE_CROUCH && option != TOGGLE_SPRINT) {
                UPDATED_OPTIONS.add(option);
            }
        }
        this.list.addSmall(NARRATOR, this.controls);
        this.list.addSmall(UPDATED_OPTIONS.toArray(new Option[0]));
        this.addWidget(this.list);
        this.createFooter();

        AbstractWidget narratorButton = this.list.findOption(NARRATOR);
        if (narratorButton != null) narratorButton.active = NarratorChatListener.INSTANCE.isActive();

        AbstractWidget highContrastButton = this.list.findOption(HIGH_CONTRAST);
        if (highContrastButton != null && MellowUtils.highContrastUnavailable()) {
            highContrastButton.active = false;
        }
    }

    @Inject(method = "createFooter", at = @At("HEAD"), cancellable = true)
    public void createFooter(CallbackInfo callback) {
        if (!MellowConfigs.CLIENT_CONFIGS.accessibilitySettingsStyle.get()) return;
        callback.cancel();

        // Accessibility Guide
        this.addRenderableWidget(new Button(this.width / 2 - 155, this.height - 26, 150, 20, new TranslatableComponent("options.accessibility.link"),
                button -> WidgetLocations.openLink(this, "https://aka.ms/MinecraftJavaAccessibility", false)));

        // Done
        WidgetComponents.components(this, this::addRenderableWidget).done(Alignment.RIGHT);
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        if (!MellowConfigs.CLIENT_CONFIGS.accessibilitySettingsStyle.get()) super.render(stack, mouseX, mouseY, partialTicks);
        this.renderBackground(stack);
        this.list.render(stack, mouseX, mouseY, partialTicks);
        RenderComponents.INSTANCE.drawTitle(new TranslatableComponent("menu.minecraft.accessibility_settings.title").withStyle(TextComponents.titleStyle()), this.width);
        for (Widget widget : this.renderables) widget.render(stack, mouseX, mouseY, partialTicks);
        List<FormattedCharSequence> tooltip = tooltipAt(this.list, mouseX, mouseY);
        if (!tooltip.isEmpty()) this.renderTooltip(stack, tooltip, mouseX, mouseY);
    }
}
