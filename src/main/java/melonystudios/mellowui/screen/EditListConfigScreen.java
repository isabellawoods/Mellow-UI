package melonystudios.mellowui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.element.widget.WidgetComponents;
import melonystudios.mellowui.screen.list.ConfigEntriesList;
import melonystudios.mellowui.util.Alignment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class EditListConfigScreen extends Screen {
    private final RenderComponents components = RenderComponents.INSTANCE;
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
        WidgetComponents.components(this, this::addButton).done(Alignment.CENTER);
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        this.list.render(stack, mouseX, mouseY, partialTicks);
        this.components.drawTitle(this.title, this.width);
        super.render(stack, mouseX, mouseY, partialTicks);
    }
}
