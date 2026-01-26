package melonystudios.mellowui.screen.backport;

import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.element.widget.WidgetComponents;
import melonystudios.mellowui.element.widget.WidgetLocations;
import melonystudios.mellowui.util.Alignment;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TranslatableComponent;

public class FeedbackScreen extends Screen {
    private final RenderComponents components = RenderComponents.INSTANCE;
    private final Screen lastScreen;

    public FeedbackScreen(Screen lastScreen) {
        super(new TranslatableComponent("menu.mellowui.feedback.title").withStyle(TextComponents.titleStyle()));
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
                WidgetLocations.openLink(this, feedbackURL, false)));

        // Report Bugs
        this.addRenderableWidget(new Button(this.width / 2 + 4, 68, 98, 20, new TranslatableComponent("menu.reportBugs"), button ->
                WidgetLocations.openLink(this, "https://aka.ms/snapshotbugs?ref=game", false)));

        // Back button
        WidgetComponents.components(this, this::addRenderableWidget).done(Alignment.CENTER);
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        this.components.drawTitle(this.title, this.width);
        super.render(stack, mouseX, mouseY, partialTicks);
    }
}
