package melonystudios.mellowui.screen.tab;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.screen.RenderComponents;
import melonystudios.mellowui.screen.backport.CreateNewWorldScreen;
import melonystudios.mellowui.screen.backport.WorldCreationUIState;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.BiomeGeneratorTypeScreens;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.OptionButton;
import net.minecraft.client.settings.BooleanOption;
import net.minecraft.client.settings.IteratableOption;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;

import static net.minecraft.client.gui.AbstractGui.drawString;

public class WorldTab extends TabContents {
    private TextFieldWidget seedEdit;

    public WorldTab() {
        super("world");
    }

    @Override
    public void tick() {
        if (this.seedEdit != null) this.seedEdit.tick();
    }

    @Override
    public void init(CreateNewWorldScreen screen) {
        int widgetY = 56;
        Minecraft minecraft = Minecraft.getInstance();

        // World Type
        IteratableOption worldTypeOption = new IteratableOption("selectWorld.mapType",
                (options, index) -> {}, /*screen.uiState().setWorldType(this.cycleWorldType(screen.uiState()))*/
                (options, button) -> new TranslationTextComponent("selectWorld.mapType").append(" ").append(screen.uiState().getWorldType().describePreset()));
        Widget worldTypeButton = worldTypeOption.createButton(minecraft.options, screen.width / 2 - 155, widgetY, 150);
        ((OptionButton) worldTypeButton).getOption().setTooltip(screen.uiState().getWorldType().isAmplified() ? minecraft.font.split(new TranslationTextComponent("generator.amplified.info"),
                RenderComponents.TOOLTIP_MAX_WIDTH) : Lists.newArrayList());
        screen.uiState().addListener(state -> {
            ((OptionButton) worldTypeButton).getOption().setTooltip(state.getWorldType().isAmplified() ? minecraft.font.split(new TranslationTextComponent("generator.amplified.info"),
                    RenderComponents.TOOLTIP_MAX_WIDTH) : Lists.newArrayList());
            worldTypeButton.active = screen.uiState().getWorldType().preset() != null;
        });
        this.addWidget(worldTypeButton);

        // Customize world type (will have to rewrite presets for this to work)
        Button customizeTypeButton = new Button(screen.width / 2 + 5, widgetY, 150, 20, new TranslationTextComponent("selectWorld.customizeType"),
                button -> this.openPresetEditor(screen.uiState()));
        screen.uiState().addListener(state -> customizeTypeButton.active = !state.isDebug() && state.getPresetEditor() != null);
        this.addWidget(customizeTypeButton);
        widgetY += 42;

        // Seed for the world generator
        this.seedEdit = new TextFieldWidget(minecraft.font, screen.width / 2 - 155, widgetY, 308, 20, new TranslationTextComponent("selectWorld.seedInfo")) {
            @Nonnull
            protected IFormattableTextComponent createNarrationMessage() {
                return super.createNarrationMessage().append(". ").append(new TranslationTextComponent("selectWorld.seedInfo"));
            }
        };
        this.seedEdit.setValue(screen.uiState().getSeed());
        this.seedEdit.setResponder(screen.uiState()::setSeed);
        this.addWidget(this.seedEdit);
        widgetY += 33;

        // Generate structures
        BooleanOption generateStructuresOption = new BooleanOption("options.on", new TranslationTextComponent("selectWorld.mapFeatures.info"),
                options -> screen.uiState().generatesStructures(),
                (options, newValue) -> screen.uiState().setGenerateStructures(newValue)) {
            @Nonnull
            public ITextComponent getMessage(GameSettings options) {
                return this.get(options) ? DialogTexts.OPTION_ON : DialogTexts.OPTION_OFF;
            }
        };
        Widget generateStructuresButton = generateStructuresOption.createButton(minecraft.options, screen.width / 2 + 111, widgetY, 44);
        generateStructuresButton.active = !screen.uiState().isDebug();
        screen.uiState().addListener(state -> {
            generateStructuresButton.active = !state.isDebug();
        });
        this.addWidget(generateStructuresButton);
        widgetY += 24;

        BooleanOption bonusChestOption = new BooleanOption("options.off",
                options -> screen.uiState().hasBonusChest(),
                (options, newValue) -> screen.uiState().setBonusChest(newValue)) {
            @Nonnull
            public ITextComponent getMessage(GameSettings options) {
                return this.get(options) ? DialogTexts.OPTION_ON : DialogTexts.OPTION_OFF;
            }
        };
        Widget bonusChestButton = bonusChestOption.createButton(minecraft.options, screen.width / 2 + 111, widgetY, 44);
        bonusChestButton.active = !screen.uiState().isHardcore() && !screen.uiState().isDebug();
        screen.uiState().addListener(state -> {
            bonusChestButton.active = !state.isHardcore() && !state.isDebug();
        });
        this.addWidget(bonusChestButton);

        super.init(screen);
    }

    private void openPresetEditor(WorldCreationUIState uiState) {
        BiomeGeneratorTypeScreens.IFactory presetScreen = uiState.getPresetEditor();
        if (presetScreen != null) {
            // this isn't going to work unless I rewrite the whole class, great... ~isa 14-7-25
            //Minecraft.getInstance().setScreen(presetScreen.createEditScreen((CreateWorldScreen) this.screen, uiState.getGeneratorSettings()));
        }
    }

    /*private WorldCreationUIState.WorldTypeEntry cycleWorldType(WorldCreationUIState uiState) {
        int presetCount = Screen.hasAltDown() ? uiState.getAlternatePresetList().size() : uiState.getNormalPresetList().size();
        ++PRESET_INDEX;
        if (PRESET_INDEX > presetCount) PRESET_INDEX = 0;
        return Screen.hasAltDown() ? uiState.getAlternatePresetList().get(PRESET_INDEX) : uiState.getNormalPresetList().get(PRESET_INDEX);
    }*/

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        super.render(stack, mouseX, mouseY, partialTicks);
        if (this.screen != null) {
            FontRenderer font = Minecraft.getInstance().font;
            if (this.seedEdit != null && this.seedEdit.getValue().isEmpty() && !this.seedEdit.isFocused()) {
                drawString(stack, font, new TranslationTextComponent("selectWorld.seedInfo").withStyle(TextFormatting.DARK_GRAY), this.seedEdit.x + 4, this.seedEdit.y + (this.seedEdit.getHeight() - 8) / 2, 0xFFFFFF);
            }
            drawString(stack, font, new TranslationTextComponent("selectWorld.enterSeed"), this.screen.width / 2 - 155, 84, 0xFFFFFF);
            drawString(stack, font, new TranslationTextComponent("selectWorld.mapFeatures"), this.screen.width / 2 - 155, 135, 0xFFFFFF);
            drawString(stack, font, new TranslationTextComponent("selectWorld.bonusItems"), this.screen.width / 2 - 155, 159, 0xFFFFFF);
        }
    }
}
