package melonystudios.mellowui.screen.backport;

import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.text.TranslationTextComponent;

public class FeedbackScreen extends Screen {
    private final Screen lastScreen;

    public FeedbackScreen(Screen lastScreen) {
        super(new TranslationTextComponent("menu.mellowui.feedback.title"));
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
                MellowUtils.openLink(this, feedbackURL, false)));

        // Report Bugs
        this.addButton(new Button(this.width / 2 + 4, 68, 98, 20, new TranslationTextComponent("menu.reportBugs"), button ->
                MellowUtils.openLink(this, "https://aka.ms/snapshotbugs?ref=game", false)));

        // Back button
        this.addButton(new Button(this.width / 2 - 100, this.height - 25, 200, 20, DialogTexts.GUI_BACK, button ->
                this.minecraft.setScreen(this.lastScreen)));
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        drawCenteredString(stack, this.font, this.title, this.width / 2, MellowUtils.DEFAULT_TITLE_HEIGHT, 0xFFFFFF);
        super.render(stack, mouseX, mouseY, partialTicks);
    }
}
