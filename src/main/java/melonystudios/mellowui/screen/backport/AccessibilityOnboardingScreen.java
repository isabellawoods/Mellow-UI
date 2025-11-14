package melonystudios.mellowui.screen.backport;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.text2speech.Narrator;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.element.widget.ImageSetButton;
import melonystudios.mellowui.element.widget.text.FocusableTextWidget;
import melonystudios.mellowui.util.Alignment;
import melonystudios.mellowui.util.GUITextures;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.gui.AccessibilityScreen;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.LanguageScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;

public class AccessibilityOnboardingScreen extends Screen {
    private static final ITextComponent TITLE = new TranslationTextComponent("menu.minecraft.accessibility_onboarding.title").withStyle(TextComponents.titleStyle());
    private static final ITextComponent ONBOARDING_NARRATOR_MESSAGE = new TranslationTextComponent("menu.minecraft.accessibility_onboarding.narrator");
    private final RenderComponents components = RenderComponents.INSTANCE;
    private final Runnable onClose;
    private final boolean narratorAvailable;
    private boolean hasNarrated;
    private float timer;
    @Nullable
    private FocusableTextWidget textWidget;

    public AccessibilityOnboardingScreen(Runnable onClose) {
        super(TITLE);
        this.onClose = onClose;
        this.narratorAvailable = NarratorChatListener.INSTANCE.isActive();
    }

    @Override
    public void onClose() {
        this.close(true, this.onClose);
    }

    private void closeAndSetScreen(Screen lastScreen) {
        this.close(false, () -> this.minecraft.setScreen(lastScreen));
    }

    private void close(boolean markAsFinished, Runnable onClose) {
        if (markAsFinished) MellowConfigs.CLIENT_CONFIGS.onboardAccessibility.set(false);
        this.minecraft.options.save();
        Narrator.getNarrator().clear();
        onClose.run();
    }

    @Override
    protected void init() {
        this.textWidget = this.addButton(new FocusableTextWidget(this.width, this.title, this.font));
        this.textWidget.containWithin(this.width);
        this.textWidget.x = this.width / 2 - 186;
        this.textWidget.y = 100;

        // Narrator
        Button narrator = (Button) AbstractOption.NARRATOR.createButton(this.minecraft.options, this.width / 2 - 75, 147, 150);
        narrator.active = this.narratorAvailable;
        this.addButton(narrator);

        // Accessibility Settings
        this.addButton(new ImageSetButton(this.width / 2 - 75, 175, 150, 20, GUITextures.ACCESSIBILITY_SET,
                button -> this.closeAndSetScreen(new AccessibilityScreen(this, this.minecraft.options)), new TranslationTextComponent("options.accessibility.title"))
                .renderText(true).alignment(Alignment.RIGHT));

        // Language
        this.addButton(new ImageSetButton(this.width / 2 - 75, 203, 150, 20, GUITextures.LANGUAGE_SET,
                button -> this.closeAndSetScreen(new LanguageScreen(this, this.minecraft.options, this.minecraft.getLanguageManager())),
                new TranslationTextComponent("options.language")).renderText(true).alignment(Alignment.RIGHT));

        // Continue
        this.addButton(new Button(this.width / 2 - 75, this.height - 25, 150, 20, new TranslationTextComponent("button.mellowui.continue"),
                button  -> this.onClose()));

        if (this.narratorAvailable) this.setInitialFocus(narrator);
    }

    @Override
    public void renderBackground(MatrixStack stack) {
        this.components.renderPanorama(0, this.width, this.height, 1);
        this.components.renderBlurredBackground(this.minecraft.getDeltaFrameTime(), true);
        if (!MellowConfigs.CLIENT_CONFIGS.defaultBackground.get()) this.renderDirtBackground(0);
        else this.components.renderTiledBackground(GUITextures.OVERSPIN_PROTECTION_BACKGROUND, 255, 0, 0, this.width, this.height, 0);
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        super.render(stack, mouseX, mouseY, partialTicks);
        this.handleInitialNarrationDelay();
        this.components.renderLogo(this, this.width, this.height, 1, true);
    }

    private void handleInitialNarrationDelay() {
        if (!this.hasNarrated && this.narratorAvailable) {
            if (this.timer < 40) {
                ++this.timer;
            } else if (this.minecraft.isWindowActive()) {
                Narrator.getNarrator().say(ONBOARDING_NARRATOR_MESSAGE.getString(), true);
                this.hasNarrated = true;
            }
        }
    }
}
