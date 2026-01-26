package melonystudios.mellowui.screen.backport;

import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.element.widget.WidgetComponents;
import melonystudios.mellowui.element.widget.WidgetLocations;
import melonystudios.mellowui.util.Alignment;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.WinScreen;
import net.minecraft.network.chat.TranslatableComponent;

public class CreditsAndAttributionsScreen extends Screen {
    private final RenderComponents components = RenderComponents.INSTANCE;
    private final Screen lastScreen;

    public CreditsAndAttributionsScreen(Screen lastScreen) {
        super(new TranslatableComponent("menu.mellowui.credits_and_attribution.title").withStyle(TextComponents.titleStyle()));
        this.lastScreen = lastScreen;
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) this.minecraft.setScreen(this.lastScreen);
    }

    @Override
    protected void init() {
        int yOffset = 63;

        // Credits
        this.addRenderableWidget(new Button(this.width / 2 - 105, yOffset, 210, 20, new TranslatableComponent("button.mellowui.credits"),
                button -> this.minecraft.setScreen(new WinScreen(false, () -> this.minecraft.setScreen(this)))));
        yOffset += 28;
        // Attribution
        this.addRenderableWidget(new Button(this.width / 2 - 105, yOffset, 210, 20, new TranslatableComponent("button.mellowui.attribution"),
                button -> WidgetLocations.openLink(this, "https://aka.ms/MinecraftJavaAttribution", false)));
        yOffset += 28;
        // Licenses
        this.addRenderableWidget(new Button(this.width / 2 - 105, yOffset, 210, 20, new TranslatableComponent("button.mellowui.licenses"),
                button -> WidgetLocations.openLink(this, "https://aka.ms/MinecraftJavaLicenses", false)));

        // Done button
        WidgetComponents.components(this, this::addRenderableWidget).done(Alignment.CENTER);
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        this.components.drawTitle(this.title, this.width);
        super.render(stack, mouseX, mouseY, partialTicks);
    }
}
