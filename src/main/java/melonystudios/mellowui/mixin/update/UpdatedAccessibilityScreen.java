package melonystudios.mellowui.mixin.update;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.config.option.OpenMenuOption;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.screen.backport.MUIControlsScreen;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AccessibilityScreen;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SettingsScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.OptionsRowList;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static melonystudios.mellowui.config.VanillaConfigEntries.*;
import static net.minecraft.client.AbstractOption.*;

@Mixin(value = AccessibilityScreen.class, priority = 900)
public abstract class UpdatedAccessibilityScreen extends SettingsScreen {
    @Unique
    private final OpenMenuOption controls = new OpenMenuOption("options.controls", new MUIControlsScreen(this, Minecraft.getInstance().options)).boldText(false);
    @Mutable
    @Shadow
    @Final
    private static AbstractOption[] OPTIONS;
    @Shadow
    protected abstract void createFooter();

    @Unique
    private static final List<AbstractOption> UPDATED_OPTIONS = Lists.newArrayList(CLOSED_CAPTIONS, HIGH_CONTRAST, MENU_BACKGROUND_BLURRINESS, TEXT_BACKGROUND_OPACITY, TEXT_BACKGROUND, CHAT_OPACITY, CHAT_LINE_SPACING, CHAT_DELAY, VIEW_BOBBING,
            SCREEN_EFFECTS_SCALE, FOV_EFFECTS, MONOCHROME_LOADING_SCREEN, PANORAMA_SCROLL_SPEED, HIDE_SPLASH_TEXTS);
    @Unique
    private OptionsRowList list;

    public UpdatedAccessibilityScreen(Screen lastScreen, GameSettings options, ITextComponent title) {
        super(lastScreen, options, title);
    }

    @Override
    protected void init() {
        if (MellowConfigs.CLIENT_CONFIGS.accessibilitySettingsStyle.get()) {
            this.list = new OptionsRowList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
            for (AbstractOption option : OPTIONS) {
                if (!UPDATED_OPTIONS.contains(option) && option != FOV_EFFECTS_SCALE && option != NARRATOR && option != AUTO_JUMP && option != SHOW_SUBTITLES &&
                        option != TOGGLE_CROUCH && option != TOGGLE_SPRINT) {
                    UPDATED_OPTIONS.add(option);
                }
            }
            this.list.addSmall(NARRATOR, this.controls);
            this.list.addSmall(UPDATED_OPTIONS.toArray(new AbstractOption[0]));
            this.children.add(this.list);
            this.createFooter();
            Widget narratorButton = this.list.findOption(NARRATOR);
            if (narratorButton != null) narratorButton.active = NarratorChatListener.INSTANCE.isActive();

            Widget highContrastButton = this.list.findOption(HIGH_CONTRAST);
            if (highContrastButton != null && MellowUtils.highContrastUnavailable()) {
                highContrastButton.active = false;
            }
        } else {
            super.init();
        }
    }

    @Inject(method = "createFooter", at = @At("HEAD"), cancellable = true)
    public void createFooter(CallbackInfo callback) {
        if (MellowConfigs.CLIENT_CONFIGS.accessibilitySettingsStyle.get()) {
            callback.cancel();

            // Accessibility Guide
            this.addButton(new Button(this.width / 2 - 155, this.height - 25, 150, 20, new TranslationTextComponent("options.accessibility.link"),
                    button -> MellowUtils.openLink(this, "https://aka.ms/MinecraftJavaAccessibility", false)));

            // Done
            this.addButton(new Button(this.width / 2 + 5, this.height - 25, 150, 20, DialogTexts.GUI_DONE,
                    button -> this.minecraft.setScreen(this.lastScreen)));
        }
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        if (MellowConfigs.CLIENT_CONFIGS.accessibilitySettingsStyle.get()) {
            this.renderBackground(stack);
            this.list.render(stack, mouseX, mouseY, partialTicks);
            drawCenteredString(stack, this.font, new TranslationTextComponent("menu.minecraft.accessibility_settings.title").withStyle(TextComponents.titleStyle()), this.width / 2, MellowUtils.DEFAULT_TITLE_HEIGHT, 0xFFFFFF);
            for (Widget button : this.buttons) button.render(stack, mouseX, mouseY, partialTicks);
            List<IReorderingProcessor> tooltip = tooltipAt(this.list, mouseX, mouseY);
            if (tooltip != null) this.renderTooltip(stack, tooltip, mouseX, mouseY);
        } else {
            super.render(stack, mouseX, mouseY, partialTicks);
        }
    }
}
