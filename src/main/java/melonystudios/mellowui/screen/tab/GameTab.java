package melonystudios.mellowui.screen.tab;

import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.config.option.BooleanOption;
import melonystudios.mellowui.config.option.IterableOption;
import melonystudios.mellowui.screen.backport.CreateNewWorldScreen;
import melonystudios.mellowui.screen.backport.WorldCreationUIState;
import melonystudios.mellowui.screen.widget.HardcoreSetButton;
import melonystudios.mellowui.screen.widget.MUIOptionButton;
import melonystudios.mellowui.screen.widget.TooltippedTextField;
import melonystudios.mellowui.sound.MUISounds;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.Difficulty;

public class GameTab extends TabContents {
    private TooltippedTextField nameEdit;

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
        this.nameEdit = new TooltippedTextField(minecraft.font, screen.width / 2 - 104, widgetY, 208, 20, new TranslatableComponent("selectWorld.enterName"), TextComponent.EMPTY);
        this.nameEdit.setValue(screen.uiState().getName());
        this.nameEdit.setResponder(screen.uiState()::setName);
        this.nameEdit.setTooltip(new TranslatableComponent("menu.mellowui.create_new_world.target_folder", new TextComponent(screen.uiState().getTargetFolder()).withStyle(ChatFormatting.ITALIC)));
        screen.uiState().addListener(state -> {
            this.nameEdit.setTooltip(new TranslatableComponent("menu.mellowui.create_new_world.target_folder", new TextComponent(state.getTargetFolder()).withStyle(ChatFormatting.ITALIC)));
            screen.createWorldButton.active = !state.getName().isEmpty();
        });
        this.addWidget(this.nameEdit);
        screen.setInitialFocus(this.nameEdit);
        widgetY += 28;

        // Game Mode
        IterableOption gameModeOption = new IterableOption("selectWorld.gameMode",
                (options, index) -> screen.uiState().setGameMode(this.cycleGameMode(screen.uiState())),
                (options, button) -> new TranslatableComponent("options.generic_value", new TranslatableComponent("selectWorld.gameMode"),
                        screen.uiState().getGameMode().displayName()));
        AbstractWidget gameModeButton = gameModeOption.createButton(minecraft.options, screen.width / 2 - 105, widgetY, 210);
        ((MUIOptionButton) gameModeButton).getOption().setTooltip(screen.uiState().getGameMode().getInfo());
        screen.uiState().addListener(state -> ((MUIOptionButton) gameModeButton).getOption().setTooltip(state.getGameMode().getInfo()));
        this.addWidget(gameModeButton);
        widgetY += 28;

        // Difficulty
        IterableOption difficultyOption = new IterableOption("options.difficulty",
                (options, index) -> screen.uiState().setDifficulty(this.cycleDifficulty(screen.uiState())),
                (options, button) -> new TranslatableComponent("options.generic_value", new TranslatableComponent("options.difficulty"),
                        screen.uiState().getDifficulty().getDisplayName()));
        AbstractWidget difficultyButton = difficultyOption.createButton(minecraft.options, screen.width / 2 - 105, widgetY, 210);
        ((MUIOptionButton) difficultyButton).getOption().setTooltip(this.getDifficultyDescription(screen.uiState().getDifficulty().getKey()));
        screen.uiState().addListener(state -> {
            difficultyButton.active = !state.isHardcore();
            ((MUIOptionButton) difficultyButton).getOption().setTooltip(this.getDifficultyDescription(state.getDifficulty().getKey()));
        });
        this.addWidget(difficultyButton);

        // Hardcore
        HardcoreSetButton hardcoreButton = new HardcoreSetButton(screen.width / 2 + 114, widgetY, 20, 20,
                button -> screen.uiState().setHardcore(!screen.uiState().isHardcore()), (button, stack, mouseX, mouseY) ->
                this.components.renderTooltip(screen, button, new TranslatableComponent("config.minecraft.difficulty.hardcore.desc"), mouseX, mouseY),
                new TranslatableComponent("options.difficulty.hardcore")).setSelected(screen.uiState().isHardcore());
        screen.uiState().addListener(state -> {
            SoundManager manager = Minecraft.getInstance().getSoundManager();
            if ((state.isHardcore() && !hardcoreButton.selected()) || (!state.isHardcore() && hardcoreButton.selected())) {
                manager.play(SimpleSoundInstance.forUI(MUISounds.HARDCORE_TOGGLE.get(), 1));
            }
            if (state.isHardcore() && !hardcoreButton.selected()) manager.play(SimpleSoundInstance.forUI(MUISounds.HARDCORE_TURN_ON.get(), 1));
            if (!state.isHardcore() && hardcoreButton.selected()) manager.play(SimpleSoundInstance.forUI(MUISounds.HARDCORE_TURN_OFF.get(), 1));

            hardcoreButton.setSelected(state.isHardcore());
        });
        this.addWidget(hardcoreButton);
        widgetY += 28;

        // Allow Commands (Allow Cheats, in 1.16)
        BooleanOption allowCommandsOption = new BooleanOption("selectWorld.allowCommands", new TranslatableComponent("selectWorld.allowCommands.info"),
                options -> screen.uiState().allowsCommands(),
                (options, newValue) -> screen.uiState().setAllowCommands(newValue));
        AbstractWidget allowCommandsButton = allowCommandsOption.createButton(minecraft.options, screen.width / 2 - 105, widgetY, 210);
        screen.uiState().addListener(state -> allowCommandsButton.active = !state.isDebug() && !state.isHardcore());
        this.addWidget(allowCommandsButton);

        // MellowUI.LOGGER.debug("all widgets: {}", this.widgets);
        super.init(screen);
    }

    private WorldCreationUIState.SelectedGameMode cycleGameMode(WorldCreationUIState uiState) {
        return switch (uiState.getGameMode()) {
            case SURVIVAL -> WorldCreationUIState.SelectedGameMode.CREATIVE;
            case CREATIVE -> WorldCreationUIState.SelectedGameMode.ADVENTURE;
            case ADVENTURE -> Screen.hasAltDown() ? WorldCreationUIState.SelectedGameMode.SPECTATOR : WorldCreationUIState.SelectedGameMode.SURVIVAL;
            default -> WorldCreationUIState.SelectedGameMode.SURVIVAL;
        };
    }

    private Difficulty cycleDifficulty(WorldCreationUIState uiState) {
        return switch (uiState.getDifficulty()) {
            case PEACEFUL -> Difficulty.EASY;
            case NORMAL -> Difficulty.HARD;
            case HARD -> Difficulty.PEACEFUL;
            default -> Difficulty.NORMAL;
        };
    }

    private Component getDifficultyDescription(String difficultyKey) {
        return new TranslatableComponent("config.minecraft.difficulty." + difficultyKey + ".desc");
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        if (this.nameEdit != null) GuiComponent.drawString(stack, Minecraft.getInstance().font, new TranslatableComponent("selectWorld.enterName"), this.nameEdit.x, 56, 0xFFFFFF);
        super.render(stack, mouseX, mouseY, partialTicks);
    }
}
