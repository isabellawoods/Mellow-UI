package melonystudios.mellowui.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.screen.widget.TabButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.OptionsRowList;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;

/// Customized world settings:
/// - Tabs (same as in world creation screen in 1.19.4+):
///    - Basics (sea level, carvers, river size, biome size)
///       - {@link net.minecraft.world.gen.feature.structure.StructureFeatures Structures}
///    - {@link net.minecraft.world.gen.feature.ConfiguredFeature Features} (list of all possible configured features, sorted alphabetically & by {@link net.minecraft.world.gen.GenerationStage.Decoration generation stage})
///    - {@link net.minecraft.world.DimensionType Dimension Settings}:
///         - Logical height, ambient light, bed works, respawn anchor works, has raids, ultrawarm, natural, coordinate scale, piglin safe, has skylight, and has ceiling
///    - Expert Settings
/// - Reset button (old "Defaults")
/// - Randomize
/// - Presets
/// - Done
public class CustomizedWorldOptionsScreen extends Screen {
    private final RenderComponents components = RenderComponents.INSTANCE;
    private final List<TabButton> tabs = Lists.newArrayList();
    private final Screen lastScreen;
    private OptionsRowList list;

    public CustomizedWorldOptionsScreen(Screen lastScreen) {
        super(new TranslationTextComponent("menu.mellowui.customized_world_options.title"));
        this.lastScreen = lastScreen;
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) this.minecraft.setScreen(lastScreen);
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        super.resize(minecraft, width, height);
        this.tabs.get(0).setSelected(true);
    }

    @Override
    protected void init() {
        this.list = new OptionsRowList(this.minecraft, this.width, this.height, 22, this.height - 32, 25);
        this.list.setRenderTopAndBottom(false);
        this.list.setRenderBackground(false);
        this.children.add(this.list);
        int buttonWidth = this.components.fourTabWidth(this.width);

        // Tabs
        this.tabs.add(this.addButton(new TabButton(this.width / 2 - buttonWidth * 2, 0, buttonWidth, 24, new TranslationTextComponent("tab.mellowui.basics"), button -> this.tabs.forEach(tab -> tab.setSelected(false)))));
        this.tabs.add(this.addButton(new TabButton(this.width / 2 - buttonWidth, 0, buttonWidth, 24, new TranslationTextComponent("tab.mellowui.features"), button -> this.tabs.forEach(tab -> tab.setSelected(false)))));
        this.tabs.add(this.addButton(new TabButton(this.width / 2, 0, buttonWidth, 24, new TranslationTextComponent("tab.mellowui.dimension_settings"), button -> this.tabs.forEach(tab -> tab.setSelected(false)))));
        this.tabs.add(this.addButton(new TabButton(this.width / 2 + buttonWidth, 0, buttonWidth, 24, new TranslationTextComponent("tab.mellowui.expert_settings"), button -> this.tabs.forEach(tab -> tab.setSelected(false)))));

        // Done button
        this.addButton(new Button(this.width / 2 - 100, this.height - 25, 200, 20, DialogTexts.GUI_DONE,
                button -> this.minecraft.setScreen(this.lastScreen)));

        this.tabs.get(0).setSelected(true);
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        this.components.renderTabHeaderBackground(0, 0, this.width, 24);
        this.components.renderListSeparators(this.list, this.width, 4, this.components.fourTabWidth(this.width));
        this.list.render(stack, mouseX, mouseY, partialTicks);
        super.render(stack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void renderDirtBackground(int vOffset) {
        if (MellowConfigs.CLIENT_CONFIGS.updateScreenBackground.get()) this.components.renderMenuBackground(0, 24, this.width,  this.height, vOffset);
        else super.renderDirtBackground(vOffset);
    }
}
