package melonystudios.mellowui.screen.backport;

import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.WinGameScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.TranslationTextComponent;

public class AttributionsScreen extends Screen {
    private final Screen lastScreen;

    public AttributionsScreen(Screen lastScreen) {
        super(new TranslationTextComponent("menu.mellowui.credits_and_attribution.title"));
        this.lastScreen = lastScreen;
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) this.minecraft.setScreen(this.lastScreen);
    }

    @Override
    protected void init() {
        // Credits
        this.addButton(new Button(this.width / 2 - 105, 50, 210, 20, new TranslationTextComponent("button.mellowui.credits"),
                button -> this.minecraft.setScreen(new WinGameScreen(false, () -> this.minecraft.setScreen(this)))));
        // Attribution
        this.addButton(new Button(this.width / 2 - 105, 78, 210, 20, new TranslationTextComponent("button.mellowui.attribution"),
                button -> MellowUtils.openLink(this, "https://aka.ms/MinecraftJavaAttribution", false)));
        // Licenses
        this.addButton(new Button(this.width / 2 - 105, 106, 210, 20, new TranslationTextComponent("button.mellowui.licenses"),
                button -> MellowUtils.openLink(this, "https://aka.ms/MinecraftJavaLicenses", false)));

        // Done button
        this.addButton(new Button(this.width / 2 - 100, this.height - 25, 200, 20, DialogTexts.GUI_DONE,
                button -> this.minecraft.setScreen(this.lastScreen)));
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        drawCenteredString(stack, this.minecraft.font, this.title, this.width / 2, MellowUtils.DEFAULT_TITLE_HEIGHT, 0xFFFFFF);
        super.render(stack, mouseX, mouseY, partialTicks);
    }
}
