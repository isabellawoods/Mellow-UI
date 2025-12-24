package melonystudios.mellowui.screen.backport;

import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.element.widget.WidgetComponents;
import melonystudios.mellowui.util.Alignment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.text.TranslationTextComponent;

public class FeedbackScreen extends Screen {
    private final RenderComponents components = RenderComponents.INSTANCE;
    private final Screen lastScreen;

    public FeedbackScreen(Screen lastScreen) {
        super(new TranslationTextComponent("menu.mellowui.feedback.title").withStyle(TextComponents.titleStyle()));
        this.lastScreen = lastScreen;
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }

    @Override
    protected void init() {
        String feedbackURL = SharedConstants.getCurrentVersion().isStable() ? "https://aka.ms/javafeedback?ref=game" : "https://aka.ms/snapshotfeedback?ref=game";

        // Give Feedback
        this.addButton(new Button(this.width / 2 - 102, 68, 98, 20, new TranslationTextComponent("menu.sendFeedback"), button ->
                WidgetComponents.openLink(this, feedbackURL, false)));

        // Report Bugs
        this.addButton(new Button(this.width / 2 + 4, 68, 98, 20, new TranslationTextComponent("menu.reportBugs"), button ->
                WidgetComponents.openLink(this, "https://aka.ms/snapshotbugs?ref=game", false)));

        // Back button
        WidgetComponents.components(this, this::addButton).done(Alignment.CENTER);
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        this.components.drawTitle(this.title, this.width);
        super.render(stack, mouseX, mouseY, partialTicks);
    }
}
