package melonystudios.mellowui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.screen.list.ConfigEntriesList;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class EditListConfigScreen extends Screen {
    private final Screen lastScreen;
    private final ITextComponent configName;
    private final ForgeConfigSpec.ConfigValue<List<String>> config;
    private ConfigEntriesList list;

    public EditListConfigScreen(Screen lastScreen, ITextComponent configName, ForgeConfigSpec.ConfigValue<List<String>> config) {
        super(TextComponents.buildScreenSubtitle(MellowUI.MOD_ID, MellowUI.MOD_NAME, configName));
        this.lastScreen = lastScreen;
        this.configName = configName;
        this.config = config;
    }

    public ITextComponent getConfigName() {
        return this.configName;
    }

    public ForgeConfigSpec.ConfigValue<List<String>> getConfig() {
        return this.config;
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) this.minecraft.setScreen(this.lastScreen);
    }

    @Override
    protected void init() {
        // List
        this.list = new ConfigEntriesList(this.minecraft, this);
        this.children.add(this.list);

        // Done button
        this.addButton(new Button(this.width / 2 - 100, this.height - 25, 200, 20, DialogTexts.GUI_DONE,
                button -> this.onClose()));
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        this.list.render(stack, mouseX, mouseY, partialTicks);
        drawCenteredString(stack, this.font, this.title, this.width / 2, MellowUtils.DEFAULT_TITLE_HEIGHT, 0xFFFFFF);
        super.render(stack, mouseX, mouseY, partialTicks);
    }
}
