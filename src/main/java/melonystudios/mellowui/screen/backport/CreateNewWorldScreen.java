package melonystudios.mellowui.screen.backport;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import melonystudios.mellowui.screen.widget.TabButton;
import melonystudios.mellowui.util.GUITextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;

public class CreateNewWorldScreen extends Screen {
    private final List<TabButton> tabs = Lists.newArrayList();
    private final Screen lastScreen;

    public CreateNewWorldScreen(Screen lastScreen) {
        super(new TranslationTextComponent("selectWorld.create"));
        this.lastScreen = lastScreen;
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        super.resize(minecraft, width, height);
        this.tabs.get(0).setSelected(true);
    }

    @Override
    protected void init() {
        int buttonWidth = this.buttonWidth();

        this.tabs.add(this.addButton(new TabButton(this.width / 2 - buttonWidth / 2 - buttonWidth, 0, buttonWidth, 24, new TranslationTextComponent("tab.mellowui.game"), button -> {
            this.tabs.forEach(tab -> tab.setSelected(false));
        })));
        this.tabs.add(this.addButton(new TabButton(this.width / 2 - buttonWidth / 2, 0, buttonWidth, 24, new TranslationTextComponent("tab.mellowui.world"), button -> {
            this.tabs.forEach(tab -> tab.setSelected(false));
        })));
        this.tabs.add(this.addButton(new TabButton(this.width / 2 + buttonWidth / 2, 0, buttonWidth, 24, new TranslationTextComponent("tab.mellowui.more"), button -> {
            this.tabs.forEach(tab -> tab.setSelected(false));
        })));

        // Create New World button
        this.addButton(new Button(this.width / 2 - 155, this.height - 25, 150, 20, new TranslationTextComponent("selectWorld.create"),
                button -> this.minecraft.setScreen(this.lastScreen)));

        // Cancel button
        this.addButton(new Button(this.width / 2 + 5, this.height - 25, 150, 20, DialogTexts.GUI_CANCEL,
                button -> this.minecraft.setScreen(this.lastScreen)));

        this.tabs.get(0).setSelected(true);
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        this.renderBars(stack);
        super.render(stack, mouseX, mouseY, partialTicks);
    }

    private void renderBars(MatrixStack stack) {
        RenderSystem.enableBlend();
        this.minecraft.getTextureManager().bind(this.minecraft.level != null ? GUITextures.INWORLD_HEADER_SEPARATOR : GUITextures.HEADER_SEPARATOR);
        blit(stack, 0, 22, 0, 0, this.width / 2 - this.buttonWidth() / 2 - this.buttonWidth(), 2, 32, 2);
        blit(stack, this.width / 2 + this.buttonWidth() / 2 + this.buttonWidth(), 22, 0, 0, this.width, 2, 32, 2);
        this.minecraft.getTextureManager().bind(this.minecraft.level != null ? GUITextures.INWORLD_FOOTER_SEPARATOR : GUITextures.FOOTER_SEPARATOR);
        blit(stack, 0, this.height - 32, 0, 0, this.width, 2, 32, 2);
        RenderSystem.disableBlend();
    }

    private int buttonWidth() {
        return this.width / 2 - 130 * 2 <= 0 ? 90 : 130;
    }
}
