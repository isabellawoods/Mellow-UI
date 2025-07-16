package melonystudios.mellowui.screen.backport;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.text2speech.Narrator;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.renderer.LogoRenderer;
import melonystudios.mellowui.screen.RenderComponents;
import melonystudios.mellowui.screen.widget.ImageSetButton;
import melonystudios.mellowui.util.GUITextures;
import net.minecraft.client.Option;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screens.LanguageSelectScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;

import javax.annotation.Nullable;
import java.util.List;

public class AccessibilityOnboardingScreen extends Screen {
    private static final Component ONBOARDING_NARRATOR_MESSAGE = new TranslatableComponent("menu.minecraft.accessibility_onboarding.narrator");
    private final RenderComponents components = RenderComponents.INSTANCE;
    private final Runnable onClose;
    private final boolean narratorAvailable;
    private boolean hasNarrated;
    private float timer;
    @Nullable
    private EditBox textWidget;

    public AccessibilityOnboardingScreen(Runnable onClose) {
        super(new TranslatableComponent("menu.minecraft.accessibility_settings.title"));
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
        this.textWidget = new EditBox(this.minecraft.font, this.width / 2 - 186, 100, 372, 35, new TranslatableComponent("menu.minecraft.accessibility_onboarding.text_box"));
        this.textWidget.setEditable(false);
        this.textWidget.setCanLoseFocus(true);
        this.addWidget(this.textWidget);

        // Narrator
        AbstractWidget narrator = Option.NARRATOR.createButton(this.minecraft.options, this.width / 2 - 75, 147, 150);
        this.addRenderableWidget(narrator);

        // Accessibility Settings
        this.addRenderableWidget(new ImageSetButton(this.width / 2 - 75, 175, 150, 20, GUITextures.ACCESSIBILITY_SET,
                button -> this.closeAndSetScreen(new AccessibilityOptionsScreen(this, this.minecraft.options)), new TranslatableComponent("options.accessibility.title"))
                .renderText(true).alignment(ImageSetButton.Alignment.RIGHT));

        // Language
        this.addRenderableWidget(new ImageSetButton(this.width / 2 - 75, 203, 150, 20, GUITextures.LANGUAGE_SET,
                button -> this.closeAndSetScreen(new LanguageSelectScreen(this, this.minecraft.options, this.minecraft.getLanguageManager())),
                new TranslatableComponent("options.language")).renderText(true).alignment(ImageSetButton.Alignment.RIGHT));

        // Continue
        this.addRenderableWidget(new Button(this.width / 2 - 75, this.height - 25, 150, 20, new TranslatableComponent("button.mellowui.continue"),
                button  -> this.onClose()));

        this.setInitialFocus(narrator);
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        this.handleInitialNarrationDelay();
        this.components.renderPanorama(0, this.width, this.height, 1);
        this.components.renderBlurredBackground(partialTicks);
        this.renderDirtBackground(0);
        this.components.renderTiledBackground(GUITextures.ACCESSIBILITY_ONBOARDING_BACKGROUND, 255, 0, 0, this.width, this.height, 0);
        this.renderLogo(stack);

        if (this.textWidget != null) this.textWidget.render(stack, mouseX, mouseY, partialTicks);

        // todo: replace this with a multiline text widget when I add that ~isa 10-6-25
        List<FormattedCharSequence> processors = this.font.split(new TranslatableComponent("menu.minecraft.accessibility_onboarding.text_box"), 368);
        int line = 0;
        for (FormattedCharSequence processor : processors) {
            int textWidth = this.font.width(processor);
            this.font.drawShadow(stack, processor, this.width / 2 - (textWidth / 2), 102 + line, 0xFFFFFF);
            line += this.font.lineHeight + 2;
        }

        super.render(stack, mouseX, mouseY, partialTicks);
    }

    private void renderLogo(PoseStack stack) {
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
