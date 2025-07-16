package melonystudios.mellowui.screen.backport;

import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.TranslatableComponent;

public class FeedbackScreen extends Screen {
    private final Screen lastScreen;

    public FeedbackScreen(Screen lastScreen) {
        super(new TranslatableComponent("menu.mellowui.feedback.title"));
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
        this.addRenderableWidget(new Button(this.width / 2 - 102, 68, 98, 20, new TranslatableComponent("menu.sendFeedback"), button ->
                MellowUtils.openLink(this, feedbackURL, false)));

        // Report Bugs
        this.addRenderableWidget(new Button(this.width / 2 + 4, 68, 98, 20, new TranslatableComponent("menu.reportBugs"), button ->
                MellowUtils.openLink(this, "https://aka.ms/snapshotbugs?ref=game", false)));

        // Back button
        this.addRenderableWidget(new Button(this.width / 2 - 100, this.height - 25, 200, 20, CommonComponents.GUI_BACK, button ->
                this.minecraft.setScreen(this.lastScreen)));
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        drawCenteredString(stack, this.font, this.title, this.width / 2, MellowUtils.DEFAULT_TITLE_HEIGHT, 0xFFFFFF);
        super.render(stack, mouseX, mouseY, partialTicks);
    }
}
