package melonystudios.mellowui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.screen.list.ConfigEntriesList;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class EditListConfigScreen<T> extends Screen {
    private final Screen lastScreen;
    private final Component configName;
    private final ForgeConfigSpec.ConfigValue<List<T>> config;
    private ConfigEntriesList list;

    public EditListConfigScreen(Screen lastScreen, Component configName, ForgeConfigSpec.ConfigValue<List<T>> config) {
        super(TextComponents.buildScreenSubtitle(MellowUI.MOD_ID, MellowUI.MOD_NAME, configName));
        this.lastScreen = lastScreen;
        this.configName = configName;
        this.config = config;
    }

    public ForgeConfigSpec.ConfigValue<List<T>> getConfig() {
        return this.config;
    }

    public Component getConfigName() {
        return this.configName;
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) this.minecraft.setScreen(this.lastScreen);
    }

    @Override
    protected void init() {
        // List
        this.list = new ConfigEntriesList(this.minecraft, this);
        this.addWidget(this.list);

        // Done button
        this.addRenderableWidget(new Button(this.width / 2 - 100, this.height - 25, 200, 20, CommonComponents.GUI_DONE,
                button -> this.minecraft.setScreen(this.lastScreen)));
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        this.list.render(stack, mouseX, mouseY, partialTicks);
        drawCenteredString(stack, this.font, this.title, this.width / 2, MellowUtils.DEFAULT_TITLE_HEIGHT, 0xFFFFFF);
        super.render(stack, mouseX, mouseY, partialTicks);
    }
}
