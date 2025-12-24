package melonystudios.mellowui.screen.backport;

import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.element.widget.WidgetComponents;
import melonystudios.mellowui.util.Alignment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.WinGameScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.TranslationTextComponent;

public class CreditsAndAttributionsScreen extends Screen {
    private final RenderComponents components = RenderComponents.INSTANCE;
    private final Screen lastScreen;

    public CreditsAndAttributionsScreen(Screen lastScreen) {
        super(new TranslationTextComponent("menu.mellowui.credits_and_attribution.title").withStyle(TextComponents.titleStyle()));
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
        this.addButton(new Button(this.width / 2 - 105, yOffset, 210, 20, new TranslationTextComponent("button.mellowui.credits"),
                button -> this.minecraft.setScreen(new WinGameScreen(false, () -> this.minecraft.setScreen(this)))));
        yOffset += 28;
        // Attribution
        this.addButton(new Button(this.width / 2 - 105, yOffset, 210, 20, new TranslationTextComponent("button.mellowui.attribution"),
                button -> WidgetComponents.openLink(this, "https://aka.ms/MinecraftJavaAttribution", false)));
        yOffset += 28;
        // Licenses
        this.addButton(new Button(this.width / 2 - 105, yOffset, 210, 20, new TranslationTextComponent("button.mellowui.licenses"),
                button -> WidgetComponents.openLink(this, "https://aka.ms/MinecraftJavaLicenses", false)));

        // Done button
        WidgetComponents.components(this, this::addButton).done(Alignment.CENTER);
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        this.components.drawTitle(this.title, this.width);
        super.render(stack, mouseX, mouseY, partialTicks);
    }
}
