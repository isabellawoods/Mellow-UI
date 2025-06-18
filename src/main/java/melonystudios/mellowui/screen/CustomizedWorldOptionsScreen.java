package melonystudios.mellowui.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import melonystudios.mellowui.screen.widget.TabButton;
import melonystudios.mellowui.util.GUITextures;
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
    private OptionsRowList list;
    private final List<TabButton> tabs = Lists.newArrayList();
    private final Screen lastScreen;

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
        this.list = new OptionsRowList(this.minecraft, this.width, this.height, 24, this.height - 32, 25);
        this.list.setRenderTopAndBottom(false);
        this.list.setRenderBackground(false);
        this.children.add(this.list);
        int buttonWidth = this.buttonWidth();

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
        this.renderBars(stack, this.list);
        this.list.render(stack, mouseX, mouseY, partialTicks);
        super.render(stack, mouseX, mouseY, partialTicks);
    }

    private void renderBars(MatrixStack stack, OptionsRowList list) {
        RenderSystem.enableBlend();
        this.minecraft.getTextureManager().bind(this.minecraft.level != null ? GUITextures.INWORLD_HEADER_SEPARATOR : GUITextures.HEADER_SEPARATOR);
        blit(stack, list.getLeft(), list.getTop() - 2, 0, 0, this.width / 2 - this.buttonWidth() * 2, 2, 32, 2);
        blit(stack, this.width / 2 + this.buttonWidth() * 2, list.getTop() - 2, 0, 0, this.width, 2, 32, 2);
        this.minecraft.getTextureManager().bind(this.minecraft.level != null ? GUITextures.INWORLD_FOOTER_SEPARATOR : GUITextures.FOOTER_SEPARATOR);
        blit(stack, list.getLeft(), list.getBottom(), 0, 0, list.getLeft() + this.width, 2, 32, 2);
        RenderSystem.disableBlend();
    }

    private int buttonWidth() {
        return this.width / 2 - 130 * 2 <= 0 ? 90 : 130;
    }
}
