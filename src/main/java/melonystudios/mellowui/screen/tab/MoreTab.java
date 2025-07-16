package melonystudios.mellowui.screen.tab;

import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.screen.backport.CreateNewWorldScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.worldselection.EditGameRulesScreen;
import net.minecraft.network.chat.TranslatableComponent;

public class MoreTab extends TabContents {
    public MoreTab() {
        super("more");
    }

    @Override
    public void init(CreateNewWorldScreen screen) {
        int widgetY = 56;
        Minecraft minecraft = Minecraft.getInstance();

        // Game Rules
        this.addWidget(new Button(screen.width / 2 - 105, widgetY, 210, 20, new TranslatableComponent("selectWorld.gameRules"),
                button -> minecraft.setScreen(new EditGameRulesScreen(screen.uiState().getGameRules().copy(), updatedRules -> {
                    minecraft.setScreen(screen);
                    updatedRules.ifPresent(screen.uiState()::setGameRules);
                }))));
        widgetY += 28;

        // Import Settings (Experiments, in newer versions)
        this.addWidget(new Button(screen.width / 2 - 105, widgetY, 210, 20, new TranslatableComponent("selectWorld.import_worldgen_settings"), button -> {})).active = false;
        widgetY += 28;

        // Data Packs
        this.addWidget(new Button(screen.width / 2 - 105, widgetY, 210, 20, new TranslatableComponent("selectWorld.dataPacks"),
                button -> screen.openDataPacksSelectionScreen()));

        super.init(screen);
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        super.render(stack, mouseX, mouseY, partialTicks);
    }
}
