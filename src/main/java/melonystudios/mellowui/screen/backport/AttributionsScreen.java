package melonystudios.mellowui.screen.backport;

import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.WinScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.TranslatableComponent;

public class AttributionsScreen extends Screen {
    private final Screen lastScreen;

    public AttributionsScreen(Screen lastScreen) {
        super(new TranslatableComponent("menu.mellowui.credits_and_attribution.title"));
        this.lastScreen = lastScreen;
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) this.minecraft.setScreen(this.lastScreen);
    }

    @Override
    protected void init() {
        // Credits
        this.addRenderableWidget(new Button(this.width / 2 - 105, 58, 210, 20, new TranslatableComponent("button.mellowui.credits"),
                button -> this.minecraft.setScreen(new WinScreen(false, () -> this.minecraft.setScreen(this)))));
        // Attribution
        this.addRenderableWidget(new Button(this.width / 2 - 105, 86, 210, 20, new TranslatableComponent("button.mellowui.attribution"),
                button -> MellowUtils.openLink(this, "https://aka.ms/MinecraftJavaAttribution", false)));
        // Licenses
        this.addRenderableWidget(new Button(this.width / 2 - 105, 114, 210, 20, new TranslatableComponent("button.mellowui.licenses"),
                button -> MellowUtils.openLink(this, "https://aka.ms/MinecraftJavaLicenses", false)));

        // Done button
        this.addRenderableWidget(new Button(this.width / 2 - 100, this.height - 25, 200, 20, CommonComponents.GUI_DONE,
                button -> this.minecraft.setScreen(this.lastScreen)));
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        drawCenteredString(stack, this.minecraft.font, this.title, this.width / 2, MellowUtils.DEFAULT_TITLE_HEIGHT, 0xFFFFFF);
        super.render(stack, mouseX, mouseY, partialTicks);
    }
}
