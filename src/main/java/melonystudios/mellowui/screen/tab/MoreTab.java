package melonystudios.mellowui.screen.tab;

import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.screen.backport.CreateNewWorldScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.EditGamerulesScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.TranslationTextComponent;

public class MoreTab extends TabContents {
    public MoreTab() {
        super("more");
    }

    @Override
    public void tick() {

    }

    @Override
    public void init(CreateNewWorldScreen screen) {
        int widgetY = 56;
        Minecraft minecraft = Minecraft.getInstance();

        // Game Rules
        this.addWidget(new Button(screen.width / 2 - 105, widgetY, 210, 20, new TranslationTextComponent("selectWorld.gameRules"),
                button -> minecraft.setScreen(new EditGamerulesScreen(screen.uiState().getGameRules().copy(), updatedRules -> {
                    minecraft.setScreen(screen);
                    updatedRules.ifPresent(screen.uiState()::setGameRules);
                }))));
        widgetY += 28;

        // Import Settings (Experiments, in newer versions)
        this.addWidget(new Button(screen.width / 2 - 105, widgetY, 210, 20, new TranslationTextComponent("selectWorld.import_worldgen_settings"), button -> {})).active = false;
        widgetY += 28;

        // Data Packs
        this.addWidget(new Button(screen.width / 2 - 105, widgetY, 210, 20, new TranslationTextComponent("selectWorld.dataPacks"),
                button -> screen.openDataPacksSelectionScreen()));

        super.init(screen);
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        super.render(stack, mouseX, mouseY, partialTicks);
    }
}
