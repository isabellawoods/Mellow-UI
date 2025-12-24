package melonystudios.mellowui.screen.update;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
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
import melonystudios.mellowui.screen.backport.OnlineOptionsScreen;
import melonystudios.mellowui.util.Alignment;
import melonystudios.mellowui.util.GUITextures;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AccessibilityScreen;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.LockIconButton;
import net.minecraft.network.play.client.CLockDifficultyPacket;
import net.minecraft.network.play.client.CSetDifficultyPacket;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Difficulty;
import net.minecraftforge.fml.ModList;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.UUID;

public class MUIOptionsScreen extends SettingsScreen {
    private final RenderComponents components = RenderComponents.INSTANCE;
    private Button difficultyButton;
    private LockIconButton lockButton;
    private Difficulty currentDifficulty;

    public MUIOptionsScreen(Screen lastScreen, GameSettings options) {
        super(lastScreen, options, new TranslationTextComponent("options.title").withStyle(TextComponents.titleStyle()));
    }

    @Override
    protected void init() {
        int buttonHeight = 29;
        int leftOffset = this.width / 2 - 154;
        int rightOffset = this.width / 2 + 4;

        // FOV
        this.addButton(AbstractOption.FOV.createButton(this.options, leftOffset, buttonHeight, 150));

        if (this.minecraft.level != null) {
            this.currentDifficulty = this.minecraft.level.getDifficulty();
            // Difficulty
            this.difficultyButton = this.addButton(new Button(rightOffset, buttonHeight, 150, 20, this.getDifficultyText(this.currentDifficulty), button -> {
                this.currentDifficulty = Difficulty.byId(this.currentDifficulty.getId() + 1);
                this.minecraft.getConnection().send(new CSetDifficultyPacket(this.currentDifficulty));
                this.difficultyButton.setMessage(this.getDifficultyText(this.currentDifficulty));
            }));
            if (this.minecraft.hasSingleplayerServer() && !this.minecraft.level.getLevelData().isHardcore()) {
                this.difficultyButton.setWidth(this.difficultyButton.getWidth() - 20);
                // Difficulty Lock
                this.lockButton = this.addButton(new LockIconButton(this.difficultyButton.x + this.difficultyButton.getWidth(), this.difficultyButton.y, button ->
                        this.minecraft.setScreen(new ConfirmScreen(this::lockCallback, new TranslationTextComponent("difficulty.lock.title"),
                                new TranslationTextComponent("difficulty.lock.question",
                                        new TranslationTextComponent("options.difficulty." + this.minecraft.level.getLevelData().getDifficulty().getKey()))))));
                this.lockButton.setLocked(this.minecraft.level.getLevelData().isDifficultyLocked());
                this.lockButton.active = !this.lockButton.isLocked();
                this.difficultyButton.active = !this.lockButton.isLocked();
            } else {
                this.difficultyButton.active = false;
            }
        } else {
            if (MellowConfigs.CLIENT_CONFIGS.replaceRealmsNotifications.get()) {
                // Online...
                this.addButton(new Button(rightOffset, buttonHeight, 150, 20, new TranslationTextComponent("button.mellowui.online"), button ->
                        this.minecraft.setScreen(new OnlineOptionsScreen(this, this.minecraft.options))));
            } else {
                // Realms News & Invites
                this.addButton(VanillaConfigEntries.REALMS_NEWS_AND_INVITES.createButton(this.minecraft.options, rightOffset, 32, 150));
            }
        }
        buttonHeight += 62;

        // Breast Settings (Female Gender Mod)
        this.addBreastSettingsButton(buttonHeight);

        // Skin Customization
        this.addButton(new Button(leftOffset, buttonHeight, 150, 20, new TranslationTextComponent("options.skinCustomisation"),
                button -> this.minecraft.setScreen(new CustomizeSkinScreen(this, this.minecraft.options))));

        // Music & Sounds
        this.addButton(new Button(rightOffset, buttonHeight, 150, 20, new TranslationTextComponent("options.sounds"),
                button -> this.minecraft.setScreen(new OptionsSoundsScreen(this, this.minecraft.options))));
        buttonHeight += 24;

        // Video Settings
        this.addButton(new Button(leftOffset, buttonHeight, 150, 20, new TranslationTextComponent("options.video"),
                button -> this.minecraft.setScreen(WidgetLocations.openVideoSettings(this, this.minecraft))));

        // Controls
        this.addButton(new Button(rightOffset, buttonHeight, 150, 20, new TranslationTextComponent("options.controls"),
                button -> this.minecraft.setScreen(WidgetLocations.openControls(this, this.minecraft))));
        buttonHeight += 24;

        // Language
        this.addButton(new Button(leftOffset, buttonHeight, 150, 20, new TranslationTextComponent("options.language"),
                button -> this.minecraft.setScreen(WidgetLocations.openLanguage(this, this.minecraft))));

        // Chat Settings
        this.addButton(new Button(rightOffset, buttonHeight, 150, 20, new TranslationTextComponent("options.chat.title"),
                button -> this.minecraft.setScreen(new ChatOptionsScreen(this, this.minecraft.options))));
        buttonHeight += 24;

        // Resource Packs
        this.addButton(new Button(leftOffset, buttonHeight, 150, 20, new TranslationTextComponent("options.resourcepack"),
                button -> this.minecraft.setScreen(WidgetLocations.openResourcePacksList(this, this.minecraft, MUIOptionsScreen::updateResourcePacksList))));

        // Accessibility Settings
        this.addButton(new Button(rightOffset, buttonHeight, 150, 20, new TranslationTextComponent("options.accessibility.title"),
                button -> this.minecraft.setScreen(new AccessibilityScreen(this, this.minecraft.options))));
        buttonHeight += 24;

        // Super Secret Settings
        this.addButton(new Button(leftOffset, buttonHeight, 150, 20, new TranslationTextComponent("button.mellowui.super_secret_settings"),
                button -> this.minecraft.setScreen(new SuperSecretSettingsScreen(this))));

        // Credits & Attribution
        this.addButton(new Button(rightOffset, buttonHeight, 150, 20, new TranslationTextComponent("button.mellowui.credits_and_attribution"),
                button -> this.minecraft.setScreen(new CreditsAndAttributionsScreen(this))));

        // Done button
        WidgetComponents.components(this, this::addButton).done(Alignment.CENTER);
    }

    private void addBreastSettingsButton(int buttonHeight) {
        if (ModList.get().isLoaded("femalegender") || ModList.get().isLoaded("wildfire_gender")) {
            try {
                Class<?> screen = Class.forName(ModList.get().isLoaded("femalegender") ? "melonystudios.femalegender.gui.screen.WardrobeScreen" : "com.wildfire.gui.screen.WardrobeBrowserScreen");
                Screen wardrobeScreen = (Screen) screen.getConstructor(Screen.class, UUID.class).newInstance(this, this.minecraft.getUser().getGameProfile().getId());
                Button settingsButton = this.addButton(new ImageSetButton(this.width / 2 - 180, buttonHeight, 20, 20, GUITextures.BREAST_SETTINGS_SET,
                        button -> this.minecraft.setScreen(wardrobeScreen), (button, stack, mouseX, mouseY) ->
                        this.components.renderTooltip(this, button, new TranslationTextComponent("button.mellowui.breast_settings.tooltip" + (this.minecraft.level == null ? ".in_world" : "")), mouseX, mouseY),
                        new TranslationTextComponent("button.mellowui.breast_settings")));
                settingsButton.active = this.minecraft.level != null;
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ignored) {
                MellowUI.LOGGER.error(new TranslationTextComponent("error.mellowui.compatibility.femalegender_breast_settings").getString());
            }
        }
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        this.components.drawTitle(this.title, this.width);
        super.render(stack, mouseX, mouseY, partialTicks);
    }

    public static void updateResourcePacksList(ResourcePackList repository) {
        Minecraft minecraft = Minecraft.getInstance();
        List<String> resourcePacks = ImmutableList.copyOf(minecraft.options.resourcePacks);
        minecraft.options.resourcePacks.clear();
        minecraft.options.incompatibleResourcePacks.clear();

        for (ResourcePackInfo packInfo : repository.getSelectedPacks()) {
            if (!packInfo.isFixedPosition()) {
                minecraft.options.resourcePacks.add(packInfo.getId());
                if (!packInfo.getCompatibility().isCompatible()) minecraft.options.incompatibleResourcePacks.add(packInfo.getId());
            }
        }

        minecraft.options.save();
        List<String> updatedResourcePacks = ImmutableList.copyOf(minecraft.options.resourcePacks);
        if (!updatedResourcePacks.equals(resourcePacks)) minecraft.reloadResourcePacks();
    }

    private ITextComponent getDifficultyText(Difficulty difficulty) {
        return new TranslationTextComponent("options.generic_value", new TranslationTextComponent("options.difficulty"), difficulty.getDisplayName());
    }

    private void lockCallback(boolean confirmed) {
        this.minecraft.setScreen(this);
        if (confirmed && this.minecraft.level != null) {
            this.minecraft.getConnection().send(new CLockDifficultyPacket(true));
            this.lockButton.setLocked(true);
            this.lockButton.active = false;
            this.difficultyButton.active = false;
        }
    }
}
