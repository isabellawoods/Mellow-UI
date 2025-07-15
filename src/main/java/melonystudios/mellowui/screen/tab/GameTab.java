package melonystudios.mellowui.screen.tab;

import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.screen.RenderComponents;
import melonystudios.mellowui.screen.backport.CreateNewWorldScreen;
import melonystudios.mellowui.screen.backport.WorldCreationUIState;
import melonystudios.mellowui.screen.widget.HardcoreSetButton;
import melonystudios.mellowui.screen.widget.TooltippedTextFieldWidget;
import melonystudios.mellowui.sound.MUISounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.OptionButton;
import net.minecraft.client.settings.BooleanOption;
import net.minecraft.client.settings.IteratableOption;
import net.minecraft.util.text.*;
import net.minecraft.world.Difficulty;

public class GameTab extends TabContents {
    private TooltippedTextFieldWidget nameEdit;

    public GameTab() {
        super("game");
    }

    @Override
    public void tick() {
        if (this.nameEdit != null) this.nameEdit.tick();
    }

    @Override
    public void init(CreateNewWorldScreen screen) {
        int widgetY = 68;
        Minecraft minecraft = Minecraft.getInstance();

        // World name
        this.nameEdit = new TooltippedTextFieldWidget(minecraft.font, screen.width / 2 - 104, widgetY, 208, 20, new TranslationTextComponent("selectWorld.enterName"), StringTextComponent.EMPTY);
        this.nameEdit.setValue(screen.uiState().getName());
        this.nameEdit.setResponder(screen.uiState()::setName);
        this.nameEdit.setTooltip(new TranslationTextComponent("menu.mellowui.create_new_world.target_folder", new StringTextComponent(screen.uiState().getTargetFolder()).withStyle(TextFormatting.ITALIC)));
        screen.uiState().addListener(state -> {
            this.nameEdit.setTooltip(new TranslationTextComponent("menu.mellowui.create_new_world.target_folder", new StringTextComponent(state.getTargetFolder()).withStyle(TextFormatting.ITALIC)));
            screen.createWorldButton.active = !state.getName().isEmpty();
        });
        this.addWidget(this.nameEdit);
        screen.setInitialFocus(this.nameEdit);
        widgetY += 28;

        // Game Mode
        IteratableOption gameModeOption = new IteratableOption("selectWorld.gameMode",
                (options, index) -> screen.uiState().setGameMode(this.cycleGameMode(screen.uiState())),
                (options, button) -> new TranslationTextComponent("options.generic_value", new TranslationTextComponent("selectWorld.gameMode"),
                        screen.uiState().getGameMode().displayName()));
        Widget gameModeButton = gameModeOption.createButton(minecraft.options, screen.width / 2 - 105, widgetY, 210);
        ((OptionButton) gameModeButton).getOption().setTooltip(minecraft.font.split(screen.uiState().getGameMode().getInfo(), RenderComponents.TOOLTIP_MAX_WIDTH));
        screen.uiState().addListener(state -> ((OptionButton) gameModeButton).getOption().setTooltip(minecraft.font.split(state.getGameMode().getInfo(), RenderComponents.TOOLTIP_MAX_WIDTH)));
        this.addWidget(gameModeButton);
        widgetY += 28;

        // Difficulty
        IteratableOption difficultyOption = new IteratableOption("options.difficulty",
                (options, index) -> screen.uiState().setDifficulty(this.cycleDifficulty(screen.uiState())),
                (options, button) -> new TranslationTextComponent("options.generic_value", new TranslationTextComponent("options.difficulty"),
                        screen.uiState().getDifficulty().getDisplayName()));
        Widget difficultyButton = difficultyOption.createButton(minecraft.options, screen.width / 2 - 105, widgetY, 210);
        ((OptionButton) difficultyButton).getOption().setTooltip(minecraft.font.split(this.getDifficultyDescription(screen.uiState().getDifficulty().getKey()), RenderComponents.TOOLTIP_MAX_WIDTH));
        screen.uiState().addListener(state -> {
            difficultyButton.active = !state.isHardcore();
            ((OptionButton) difficultyButton).getOption().setTooltip(minecraft.font.split(this.getDifficultyDescription(state.getDifficulty().getKey()), RenderComponents.TOOLTIP_MAX_WIDTH));
        });
        this.addWidget(difficultyButton);

        // Hardcore
        HardcoreSetButton hardcoreButton = new HardcoreSetButton(screen.width / 2 + 114, widgetY, 20, 20,
                button -> screen.uiState().setHardcore(!screen.uiState().isHardcore()), (button, stack, mouseX, mouseY) ->
                this.components.renderTooltip(screen, button, new TranslationTextComponent("config.minecraft.difficulty.hardcore.desc"), mouseX, mouseY),
                new TranslationTextComponent("options.difficulty.hardcore")).setSelected(screen.uiState().isHardcore());
        screen.uiState().addListener(state -> {
            SoundHandler manager = Minecraft.getInstance().getSoundManager();
            if ((state.isHardcore() && !hardcoreButton.selected()) || (!state.isHardcore() && hardcoreButton.selected())) {
                manager.play(SimpleSound.forUI(MUISounds.HARDCORE_TOGGLE.get(), 1));
            }
            if (state.isHardcore() && !hardcoreButton.selected()) manager.play(SimpleSound.forUI(MUISounds.HARDCORE_TURN_ON.get(), 1));
            if (!state.isHardcore() && hardcoreButton.selected()) manager.play(SimpleSound.forUI(MUISounds.HARDCORE_TURN_OFF.get(), 1));

            hardcoreButton.setSelected(state.isHardcore());
        });
        this.addWidget(hardcoreButton);
        widgetY += 28;

        // Allow Commands (Allow Cheats, in 1.16)
        BooleanOption allowCommandsOption = new BooleanOption("selectWorld.allowCommands", new TranslationTextComponent("selectWorld.allowCommands.info"),
                options -> screen.uiState().allowsCommands(),
                (options, newValue) -> screen.uiState().setAllowCommands(newValue));
        Widget allowCommandsButton = allowCommandsOption.createButton(minecraft.options, screen.width / 2 - 105, widgetY, 210);
        screen.uiState().addListener(state -> allowCommandsButton.active = !state.isDebug() && !state.isHardcore());
        this.addWidget(allowCommandsButton);

        // MellowUI.LOGGER.debug("all widgets: {}", this.widgets);
        super.init(screen);
    }

    private WorldCreationUIState.SelectedGameMode cycleGameMode(WorldCreationUIState uiState) {
        switch (uiState.getGameMode()) {
            case SURVIVAL: return WorldCreationUIState.SelectedGameMode.CREATIVE;
            case CREATIVE: return WorldCreationUIState.SelectedGameMode.ADVENTURE;
            case ADVENTURE: return Screen.hasAltDown() ? WorldCreationUIState.SelectedGameMode.SPECTATOR : WorldCreationUIState.SelectedGameMode.SURVIVAL;
            default: return WorldCreationUIState.SelectedGameMode.SURVIVAL;
        }
    }

    private Difficulty cycleDifficulty(WorldCreationUIState uiState) {
        switch (uiState.getDifficulty()) {
            case PEACEFUL: return Difficulty.EASY;
            case NORMAL: return Difficulty.HARD;
            case HARD: return Difficulty.PEACEFUL;
            default: return Difficulty.NORMAL;
        }
    }

    private ITextComponent getDifficultyDescription(String difficultyKey) {
        return new TranslationTextComponent("config.minecraft.difficulty." + difficultyKey + ".desc");
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        if (this.nameEdit != null) AbstractGui.drawString(stack, Minecraft.getInstance().font, new TranslationTextComponent("selectWorld.enterName"), this.nameEdit.x, 56, 0xFFFFFF);
        super.render(stack, mouseX, mouseY, partialTicks);
    }
}
