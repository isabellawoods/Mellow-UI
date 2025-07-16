package melonystudios.mellowui.screen.tab;

import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.config.option.BooleanOption;
import melonystudios.mellowui.config.option.IterableOption;
import melonystudios.mellowui.screen.backport.CreateNewWorldScreen;
import melonystudios.mellowui.screen.backport.WorldCreationUIState;
import melonystudios.mellowui.screen.widget.MUIOptionButton;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.worldselection.WorldPreset;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;

import javax.annotation.Nonnull;

import static net.minecraft.client.gui.GuiComponent.drawString;

public class WorldTab extends TabContents {
    private EditBox seedEdit;

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
        IterableOption worldTypeOption = new IterableOption("selectWorld.mapType",
                (options, index) -> {}, /*screen.uiState().setWorldType(this.cycleWorldType(screen.uiState()))*/
                (options, button) -> new TranslatableComponent("selectWorld.mapType").append(" ").append(screen.uiState().getWorldType().describePreset()));
        AbstractWidget worldTypeButton = worldTypeOption.createButton(minecraft.options, screen.width / 2 - 155, widgetY, 150);
        ((MUIOptionButton) worldTypeButton).getOption().setTooltip(screen.uiState().getWorldType().isAmplified() ? new TranslatableComponent("generator.amplified.info") : null);
        screen.uiState().addListener(state -> {
            ((MUIOptionButton) worldTypeButton).getOption().setTooltip(state.getWorldType().isAmplified() ? new TranslatableComponent("generator.amplified.info") : null);
            worldTypeButton.active = screen.uiState().getWorldType().preset() != null;
        });
        this.addWidget(worldTypeButton);

        // Customize world type (will have to rewrite presets for this to work)
        Button customizeTypeButton = new Button(screen.width / 2 + 5, widgetY, 150, 20, new TranslatableComponent("selectWorld.customizeType"),
                button -> this.openPresetEditor(screen.uiState()));
        screen.uiState().addListener(state -> customizeTypeButton.active = !state.isDebug() && state.getPresetEditor() != null);
        this.addWidget(customizeTypeButton);
        widgetY += 42;

        // Seed for the world generator
        this.seedEdit = new EditBox(minecraft.font, screen.width / 2 - 155, widgetY, 308, 20, new TranslatableComponent("selectWorld.seedInfo")) {
            @Nonnull
            protected MutableComponent createNarrationMessage() {
                return super.createNarrationMessage().append(". ").append(new TranslatableComponent("selectWorld.seedInfo"));
            }
        };
        this.seedEdit.setValue(screen.uiState().getSeed());
        this.seedEdit.setResponder(screen.uiState()::setSeed);
        this.addWidget(this.seedEdit);
        widgetY += 33;

        // Generate structures
        BooleanOption generateStructuresOption = new BooleanOption("options.on", new TranslatableComponent("selectWorld.mapFeatures.info"),
                options -> screen.uiState().generatesStructures(),
                (options, newValue) -> screen.uiState().setGenerateStructures(newValue)) {
            @Nonnull
            public Component getMessage(Options options) {
                return this.get(options) ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF;
            }
        };
        AbstractWidget generateStructuresButton = generateStructuresOption.createButton(minecraft.options, screen.width / 2 + 111, widgetY, 44);
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
            public Component getMessage(Options options) {
                return this.get(options) ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF;
            }
        };
        AbstractWidget bonusChestButton = bonusChestOption.createButton(minecraft.options, screen.width / 2 + 111, widgetY, 44);
        bonusChestButton.active = !screen.uiState().isHardcore() && !screen.uiState().isDebug();
        screen.uiState().addListener(state -> {
            bonusChestButton.active = !state.isHardcore() && !state.isDebug();
        });
        this.addWidget(bonusChestButton);

        super.init(screen);
    }

    private void openPresetEditor(WorldCreationUIState uiState) {
        WorldPreset.PresetEditor presetScreen = uiState.getPresetEditor();
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
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        super.render(stack, mouseX, mouseY, partialTicks);
        if (this.screen != null) {
            Font font = Minecraft.getInstance().font;
            if (this.seedEdit != null && this.seedEdit.getValue().isEmpty() && !this.seedEdit.isFocused()) {
                drawString(stack, font, new TranslatableComponent("selectWorld.seedInfo").withStyle(ChatFormatting.DARK_GRAY), this.seedEdit.x + 4, this.seedEdit.y + (this.seedEdit.getHeight() - 8) / 2, 0xFFFFFF);
            }
            drawString(stack, font, new TranslatableComponent("selectWorld.enterSeed"), this.screen.width / 2 - 155, 84, 0xFFFFFF);
            drawString(stack, font, new TranslatableComponent("selectWorld.mapFeatures"), this.screen.width / 2 - 155, 135, 0xFFFFFF);
            drawString(stack, font, new TranslatableComponent("selectWorld.bonusItems"), this.screen.width / 2 - 155, 159, 0xFFFFFF);
        }
    }
}
