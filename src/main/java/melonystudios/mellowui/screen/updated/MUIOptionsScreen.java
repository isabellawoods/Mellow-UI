package melonystudios.mellowui.screen.updated;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.config.VanillaConfigEntries;
import melonystudios.mellowui.util.ShaderManager;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.AccessibilityScreen;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.LockIconButton;
import net.minecraft.network.play.client.CLockDifficultyPacket;
import net.minecraft.network.play.client.CSetDifficultyPacket;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Difficulty;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;

import java.util.List;
import java.util.Random;

public class MUIOptionsScreen extends SettingsScreen {
    private Button difficultyButton;
    private LockIconButton lockButton;
    private Difficulty currentDifficulty;

    public MUIOptionsScreen(Screen lastScreen, GameSettings options) {
        super(lastScreen, options, new TranslationTextComponent("options.title"));
    }

    @Override
    protected void init() {
        int buttonHeight = 32;
        // FOV
        this.addButton(AbstractOption.FOV.createButton(this.options, this.width / 2 - 155, buttonHeight, 150));

        if (this.minecraft.level != null) {
            this.currentDifficulty = this.minecraft.level.getDifficulty();
            // Difficulty
            this.difficultyButton = this.addButton(new Button(this.width / 2 + 5, buttonHeight, 150, 20, this.getDifficultyText(this.currentDifficulty), button -> {
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
                this.addButton(new Button(this.width / 2 + 5, buttonHeight, 150, 20, new TranslationTextComponent("button.mellowui.online"), button ->
                        this.minecraft.setScreen(new OnlineOptionsScreen(this, this.minecraft.options))));
            } else {
                // Realms Notifications
                this.addButton(VanillaConfigEntries.REALMS_NEWS_AND_INVITES.createButton(this.minecraft.options, this.width / 2 + 5, 32, 150));
            }
        }
        buttonHeight += 54;

        // Skin Customization
        this.addButton(new Button(this.width / 2 - 155, buttonHeight, 150, 20, new TranslationTextComponent("options.skinCustomisation"),
                button -> this.minecraft.setScreen(new CustomizeSkinScreen(this, this.minecraft.options))));

        // Music & Sounds
        this.addButton(new Button(this.width / 2 + 5, buttonHeight, 150, 20, new TranslationTextComponent("options.sounds"),
                button -> this.minecraft.setScreen(new OptionsSoundsScreen(this, this.minecraft.options))));
        buttonHeight += 25;

        // Video Settings
        this.addButton(new Button(this.width / 2 - 155, buttonHeight, 150, 20, new TranslationTextComponent("options.video"),
                button -> this.minecraft.setScreen(new VideoSettingsScreen(this, this.minecraft.options))));

        // Controls
        this.addButton(new Button(this.width / 2 + 5, buttonHeight, 150, 20, new TranslationTextComponent("options.controls"),
                button -> this.minecraft.setScreen(MellowUtils.controls(this, this.minecraft))));
        buttonHeight += 25;

        // Language
        this.addButton(new Button(this.width / 2 - 155, buttonHeight, 150, 20, new TranslationTextComponent("options.language"),
                button -> this.minecraft.setScreen(new LanguageScreen(this, this.minecraft.options, this.minecraft.getLanguageManager()))));

        // Chat Settings
        this.addButton(new Button(this.width / 2 + 5, buttonHeight, 150, 20, new TranslationTextComponent("options.chat.title"),
                button -> this.minecraft.setScreen(new ChatOptionsScreen(this, this.minecraft.options))));
        buttonHeight += 25;

        // Resource Packs
        this.addButton(new Button(this.width / 2 - 155, buttonHeight, 150, 20, new TranslationTextComponent("options.resourcepack"),
                button -> this.minecraft.setScreen(MellowUtils.resourcePackList(this, this.minecraft, MUIOptionsScreen::updateResourcePacksList))));

        // Accessibility Settings
        this.addButton(new Button(this.width / 2 + 5, buttonHeight, 150, 20, new TranslationTextComponent("options.accessibility.title"),
                button -> this.minecraft.setScreen(new AccessibilityScreen(this, this.minecraft.options))));
        buttonHeight += 25;

        // Super Secret Settings
        Random random = new Random();
        this.addButton(new Button(this.width / 2 - 155, buttonHeight, 150, 20, new TranslationTextComponent("button.mellowui.super_secret_settings"), button -> {
            if (hasControlDown()) {
                ShaderManager.resetShaders(this.minecraft);
            } else {
                SoundEvent[] allSounds = ForgeRegistries.SOUND_EVENTS.getValues().toArray(new SoundEvent[0]);
                SoundEvent sound = allSounds[random.nextInt(allSounds.length)];
                float pitch = randomBetween(random, 0.01F, 2);
                this.minecraft.getSoundManager().play(SimpleSound.forUI(sound, pitch, 1));
                ShaderManager.cycleShader(this.minecraft);

                if (this.minecraft.getLaunchedVersion().contains("melony-studios-dev")) {
                    LogManager.getLogger().debug("Played sound '{}' at {} pitch", sound.getLocation(), pitch);
                }
            }
        }));

        // Credits & Attribution
        this.addButton(new Button(this.width / 2 + 5, buttonHeight, 150, 20, new TranslationTextComponent("button.mellowui.credits_and_attribution"),
                button -> this.minecraft.setScreen(new AttributionsScreen(this))));

        // Done button
        this.addButton(new Button(this.width / 2 - 100, this.height - 25, 200, 20, DialogTexts.GUI_DONE,
                button -> this.minecraft.setScreen(this.lastScreen)));
    }

    public static float randomBetween(Random rand, float minimum, float maximum) {
        return rand.nextFloat() * (maximum - minimum) + minimum;
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        drawCenteredString(stack, this.font, this.title, this.width / 2, MellowUtils.DEFAULT_TITLE_HEIGHT, 0xFFFFFF);
        super.render(stack, mouseX, mouseY, partialTicks);
    }

    public static void updateResourcePacksList(ResourcePackList packList) {
        Minecraft minecraft = Minecraft.getInstance();
        List<String> resourcePacks = ImmutableList.copyOf(minecraft.options.resourcePacks);
        minecraft.options.resourcePacks.clear();
        minecraft.options.incompatibleResourcePacks.clear();

        for (ResourcePackInfo packInfo : packList.getSelectedPacks()) {
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
