package melonystudios.mellowui.screen.update;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.config.VanillaConfigEntries;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.element.widget.ImageSetButton;
import melonystudios.mellowui.element.widget.WidgetComponents;
import melonystudios.mellowui.element.widget.WidgetLocations;
import melonystudios.mellowui.screen.SuperSecretSettingsScreen;
import melonystudios.mellowui.screen.backport.CreditsAndAttributionsScreen;
import melonystudios.mellowui.util.Alignment;
import melonystudios.mellowui.util.GUITextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.LockIconButton;
import net.minecraft.client.gui.screens.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ServerboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ServerboundLockDifficultyPacket;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.Difficulty;
import net.minecraftforge.fml.ModList;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.UUID;

public class MUIOptionsScreen extends OptionsSubScreen {
    private final RenderComponents components = RenderComponents.INSTANCE;
    private Button difficultyButton;
    private LockIconButton lockButton;
    private Difficulty currentDifficulty;

    public MUIOptionsScreen(Screen lastScreen, Options options) {
        super(lastScreen, options, new TranslatableComponent("options.title").withStyle(TextComponents.titleStyle()));
    }

    @Override
    protected void init() {
        int buttonHeight = 29;
        int leftOffset = this.width / 2 - 154;
        int rightOffset = this.width / 2 + 4;

        // FOV
        this.addRenderableWidget(Option.FOV.createButton(this.options, leftOffset, buttonHeight, 150));

        if (this.minecraft.level != null) {
            this.currentDifficulty = this.minecraft.level.getDifficulty();
            // Difficulty
            this.difficultyButton = this.addRenderableWidget(new Button(rightOffset, buttonHeight, 150, 20, this.getDifficultyText(this.currentDifficulty), button -> {
                this.currentDifficulty = Difficulty.byId(this.currentDifficulty.getId() + 1);
                this.minecraft.getConnection().send(new ServerboundChangeDifficultyPacket(this.currentDifficulty));
                this.difficultyButton.setMessage(this.getDifficultyText(this.currentDifficulty));
            }));
            if (this.minecraft.hasSingleplayerServer() && !this.minecraft.level.getLevelData().isHardcore()) {
                this.difficultyButton.setWidth(this.difficultyButton.getWidth() - 20);
                // Difficulty Lock
                this.lockButton = this.addRenderableWidget(new LockIconButton(this.difficultyButton.x + this.difficultyButton.getWidth(), this.difficultyButton.y, button ->
                        this.minecraft.setScreen(new ConfirmScreen(this::lockCallback, new TranslatableComponent("difficulty.lock.title"),
                                new TranslatableComponent("difficulty.lock.question",
                                        new TranslatableComponent("options.difficulty." + this.minecraft.level.getLevelData().getDifficulty().getKey()))))));
                this.lockButton.setLocked(this.minecraft.level.getLevelData().isDifficultyLocked());
                this.lockButton.active = !this.lockButton.isLocked();
                this.difficultyButton.active = !this.lockButton.isLocked();
            } else {
                this.difficultyButton.active = false;
            }
        } else {
            if (MellowConfigs.CLIENT_CONFIGS.replaceRealmsNotifications.get()) {
                // Online...
                this.addRenderableWidget(new Button(rightOffset, buttonHeight, 150, 20, new TranslatableComponent("options.online"), button ->
                        this.minecraft.setScreen(WidgetLocations.openOnlineOptions(this, this.minecraft))));
            } else {
                // Realms News & Invites
                this.addRenderableWidget(VanillaConfigEntries.REALMS_NEWS_AND_INVITES.createButton(this.minecraft.options, rightOffset, 32, 150));
            }
        }
        buttonHeight += 62;

        // Breast Settings (Female Gender Mod)
        this.addBreastSettingsButton(buttonHeight, leftOffset);

        // Skin Customization
        this.addRenderableWidget(new Button(leftOffset, buttonHeight, 150, 20, new TranslatableComponent("options.skinCustomisation"),
                button -> this.minecraft.setScreen(new SkinCustomizationScreen(this, this.minecraft.options))));

        // Music & Sounds
        this.addRenderableWidget(new Button(rightOffset, buttonHeight, 150, 20, new TranslatableComponent("options.sounds"),
                button -> this.minecraft.setScreen(new SoundOptionsScreen(this, this.minecraft.options))));
        buttonHeight += 24;

        // Video Settings
        this.addRenderableWidget(new Button(leftOffset, buttonHeight, 150, 20, new TranslatableComponent("options.video"),
                button -> this.minecraft.setScreen(WidgetLocations.openVideoSettings(this, this.minecraft))));

        // Controls
        this.addRenderableWidget(new Button(rightOffset, buttonHeight, 150, 20, new TranslatableComponent("options.controls"),
                button -> this.minecraft.setScreen(WidgetLocations.openControls(this, this.minecraft))));
        buttonHeight += 24;

        // Language
        this.addRenderableWidget(new Button(leftOffset, buttonHeight, 150, 20, new TranslatableComponent("options.language"),
                button -> this.minecraft.setScreen(WidgetLocations.openLanguage(this, this.minecraft))));

        // Chat Settings
        this.addRenderableWidget(new Button(rightOffset, buttonHeight, 150, 20, new TranslatableComponent("options.chat.title"),
                button -> this.minecraft.setScreen(new ChatOptionsScreen(this, this.minecraft.options))));
        buttonHeight += 24;

        // Resource Packs
        this.addRenderableWidget(new Button(leftOffset, buttonHeight, 150, 20, new TranslatableComponent("options.resourcepack"),
                button -> this.minecraft.setScreen(WidgetLocations.openResourcePacksList(this, this.minecraft, MUIOptionsScreen::updateResourcePacksList))));

        // Accessibility Settings
        this.addRenderableWidget(new Button(rightOffset, buttonHeight, 150, 20, new TranslatableComponent("options.accessibility.title"),
                button -> this.minecraft.setScreen(new AccessibilityOptionsScreen(this, this.minecraft.options))));
        buttonHeight += 24;

        // Super Secret Settings
        this.addRenderableWidget(new Button(leftOffset, buttonHeight, 150, 20, new TranslatableComponent("button.mellowui.super_secret_settings"),
                button -> this.minecraft.setScreen(new SuperSecretSettingsScreen(this))));

        // Credits & Attribution
        this.addRenderableWidget(new Button(rightOffset, buttonHeight, 150, 20, new TranslatableComponent("button.mellowui.credits_and_attribution"),
                button -> this.minecraft.setScreen(new CreditsAndAttributionsScreen(this))));

        // Done button
        WidgetComponents.components(this, this::addRenderableWidget).done(Alignment.CENTER);
    }

    private void addBreastSettingsButton(int buttonHeight, int leftOffset) {
        if (ModList.get().isLoaded("wildfire_gender")) {
            try {
                Class<?> screen = Class.forName("com.wildfire.gui.screen.WardrobeBrowserScreen");
                Screen wardrobeScreen = (Screen) screen.getConstructor(Screen.class, UUID.class).newInstance(this, this.minecraft.getUser().getGameProfile().getId());
                Button settingsButton = this.addRenderableWidget(new ImageSetButton(leftOffset - 24, buttonHeight, 20, 20, GUITextures.BREAST_SETTINGS_SET,
                        button -> this.minecraft.setScreen(wardrobeScreen), (button, stack, mouseX, mouseY) ->
                        this.components.renderTooltip(this, button, new TranslatableComponent("button.mellowui.breast_settings.tooltip" + (this.minecraft.level == null ? ".in_world" : "")), mouseX, mouseY),
                        new TranslatableComponent("button.mellowui.breast_settings")));
                settingsButton.active = this.minecraft.level != null;
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ignored) {
                MellowUI.LOGGER.error(new TranslatableComponent("error.mellowui.compatibility.femalegender_breast_settings").getString());
            }
        }
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        this.components.drawTitle(this.title, this.width);
        super.render(stack, mouseX, mouseY, partialTicks);
    }

    public static void updateResourcePacksList(PackRepository repository) {
        Minecraft minecraft = Minecraft.getInstance();
        List<String> resourcePacks = ImmutableList.copyOf(minecraft.options.resourcePacks);
        minecraft.options.resourcePacks.clear();
        minecraft.options.incompatibleResourcePacks.clear();

        for (Pack packInfo : repository.getSelectedPacks()) {
            if (!packInfo.isFixedPosition()) {
                minecraft.options.resourcePacks.add(packInfo.getId());
                if (!packInfo.getCompatibility().isCompatible()) minecraft.options.incompatibleResourcePacks.add(packInfo.getId());
            }
        }

        minecraft.options.save();
        List<String> updatedResourcePacks = ImmutableList.copyOf(minecraft.options.resourcePacks);
        if (!updatedResourcePacks.equals(resourcePacks)) minecraft.reloadResourcePacks();
    }

    private Component getDifficultyText(Difficulty difficulty) {
        return new TranslatableComponent("options.generic_value", new TranslatableComponent("options.difficulty"), difficulty.getDisplayName());
    }

    private void lockCallback(boolean confirmed) {
        this.minecraft.setScreen(this);
        if (confirmed && this.minecraft.level != null) {
            this.minecraft.getConnection().send(new ServerboundLockDifficultyPacket(true));
            this.lockButton.setLocked(true);
            this.lockButton.active = false;
            this.difficultyButton.active = false;
        }
    }
}
