package melonystudios.mellowui.screen.backport;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.text2speech.Narrator;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.renderer.LogoRenderer;
import melonystudios.mellowui.screen.RenderComponents;
import melonystudios.mellowui.screen.widget.ImageSetButton;
import melonystudios.mellowui.util.GUITextures;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.gui.AccessibilityScreen;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.LanguageScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import java.util.List;

public class AccessibilityOnboardingScreen extends Screen {
    private static final ITextComponent ONBOARDING_NARRATOR_MESSAGE = new TranslationTextComponent("menu.minecraft.accessibility_onboarding.narrator");
    private final RenderComponents components = RenderComponents.INSTANCE;
    private final Runnable onClose;
    private final boolean narratorAvailable;
    private boolean hasNarrated;
    private float timer;
    @Nullable
    private TextFieldWidget textWidget;

    public AccessibilityOnboardingScreen(Runnable onClose) {
        super(new TranslationTextComponent("menu.minecraft.accessibility_settings.title"));
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
    public void tick() {
        super.tick();
        if (this.textWidget != null) this.textWidget.tick();
    }

    @Override
    protected void init() {
        this.textWidget = new TextFieldWidget(this.minecraft.font, this.width / 2 - 186, 100, 372, 35, new TranslationTextComponent("menu.minecraft.accessibility_onboarding.text_box"));
        this.textWidget.setEditable(false);
        this.textWidget.setCanLoseFocus(true);
        this.addWidget(this.textWidget);

        // Narrator
        Button narrator = (Button) AbstractOption.NARRATOR.createButton(this.minecraft.options, this.width / 2 - 75, 147, 150);
        this.addButton(narrator);

        // Accessibility Settings
        this.addButton(new ImageSetButton(this.width / 2 - 75, 175, 150, 20, GUITextures.ACCESSIBILITY_SET,
                button -> this.closeAndSetScreen(new AccessibilityScreen(this, this.minecraft.options)), new TranslationTextComponent("options.accessibility.title"))
                .renderText(true).alignment(ImageSetButton.Alignment.RIGHT));

        // Language
        this.addButton(new ImageSetButton(this.width / 2 - 75, 203, 150, 20, GUITextures.LANGUAGE_SET,
                button -> this.closeAndSetScreen(new LanguageScreen(this, this.minecraft.options, this.minecraft.getLanguageManager())),
                new TranslationTextComponent("options.language")).renderText(true).alignment(ImageSetButton.Alignment.RIGHT));

        // Continue
        this.addButton(new Button(this.width / 2 - 75, this.height - 25, 150, 20, new TranslationTextComponent("button.mellowui.continue"),
                button  -> this.onClose()));

        this.setInitialFocus(narrator);
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.handleInitialNarrationDelay();
        this.components.renderPanorama(0, this.width, this.height, 1);
        this.components.renderBlurredBackground(partialTicks);
        this.renderDirtBackground(0);
        this.components.renderTiledBackground(GUITextures.ACCESSIBILITY_ONBOARDING_BACKGROUND, 255, 0, 0, this.width, this.height, 0);
        this.renderLogo(stack);

        if (this.textWidget != null) this.textWidget.render(stack, mouseX, mouseY, partialTicks);

        // todo: replace this with a multiline text widget when I add that ~isa 10-6-25
        List<IReorderingProcessor> processors = this.font.split(new TranslationTextComponent("menu.minecraft.accessibility_onboarding.text_box"), 368);
        int line = 0;
        for (IReorderingProcessor processor : processors) {
            int textWidth = this.font.width(processor);
            this.font.drawShadow(stack, processor, this.width / 2 - (textWidth / 2), 102 + line, 0xFFFFFF);
            line += this.font.lineHeight + 2;
        }

        super.render(stack, mouseX, mouseY, partialTicks);
    }

    private void renderLogo(MatrixStack stack) {
        switch (MellowConfigs.CLIENT_CONFIGS.mainMenuStyle.get()) {
            case OPTION_1: {
                LogoRenderer.renderOldLogo(stack, this, this.width, 1, true);
                break;
            }
            case OPTION_3: {
                LogoRenderer.renderMellomedleyLogo(stack, this.width / 2 - 129, 10, 258, 100, 1, true);
                break;
            }
            case OPTION_2: {
                switch (MellowConfigs.CLIENT_CONFIGS.logoStyle.get()) {
                    case OPTION_1: // Pre 1.19
                        LogoRenderer.renderOldLogo(stack, this, this.width, 1, true);
                        break;
                    case OPTION_2: // 1.20 and above
                        LogoRenderer.renderUpdatedLogo(stack, this.width, 1, true);
                        break;
                    case OPTION_3: // Mellomedley's logo
                        LogoRenderer.renderMellomedleyLogo(stack, this.width / 2 - 129, 10, 258, 100, 1, true);
                        break;
                }
            }
        }
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
