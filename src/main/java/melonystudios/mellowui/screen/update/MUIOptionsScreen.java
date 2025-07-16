package melonystudios.mellowui.screen.update;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.config.VanillaConfigEntries;
import melonystudios.mellowui.screen.SuperSecretSettingsScreen;
import melonystudios.mellowui.screen.backport.AttributionsScreen;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.LockIconButton;
import net.minecraft.client.gui.screens.*;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ServerboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ServerboundLockDifficultyPacket;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.Difficulty;

import java.util.List;

public class MUIOptionsScreen extends OptionsSubScreen {
    private Button difficultyButton;
    private LockIconButton lockButton;
    private Difficulty currentDifficulty;

    public MUIOptionsScreen(Screen lastScreen, Options options) {
        super(lastScreen, options, new TranslatableComponent("options.title"));
    }

    @Override
    protected void init() {
        int buttonHeight = 32;
        // FOV
        this.addRenderableWidget(Option.FOV.createButton(this.options, this.width / 2 - 155, buttonHeight, 150));

        if (this.minecraft.level != null) {
            this.currentDifficulty = this.minecraft.level.getDifficulty();
            // Difficulty
            this.difficultyButton = this.addRenderableWidget(new Button(this.width / 2 + 5, buttonHeight, 150, 20, this.getDifficultyText(this.currentDifficulty), button -> {
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
                this.addRenderableWidget(new Button(this.width / 2 + 5, buttonHeight, 150, 20, new TranslatableComponent("options.online"), button ->
                        this.minecraft.setScreen(MellowUtils.onlineOptions(this, this.minecraft))));
            } else {
                // Realms News & Invites
                this.addRenderableWidget(VanillaConfigEntries.REALMS_NEWS_AND_INVITES.createButton(this.minecraft.options, this.width / 2 + 5, 32, 150));
            }
        }
        buttonHeight += 54;

        // Skin Customization
        this.addRenderableWidget(new Button(this.width / 2 - 155, buttonHeight, 150, 20, new TranslatableComponent("options.skinCustomisation"),
                button -> this.minecraft.setScreen(new SkinCustomizationScreen(this, this.minecraft.options))));

        // Music & Sounds
        this.addRenderableWidget(new Button(this.width / 2 + 5, buttonHeight, 150, 20, new TranslatableComponent("options.sounds"),
                button -> this.minecraft.setScreen(new SoundOptionsScreen(this, this.minecraft.options))));
        buttonHeight += 25;

        // Video Settings
        this.addRenderableWidget(new Button(this.width / 2 - 155, buttonHeight, 150, 20, new TranslatableComponent("options.video"),
                button -> this.minecraft.setScreen(MellowUtils.videoSettings(this, this.minecraft))));

        // Controls
        this.addRenderableWidget(new Button(this.width / 2 + 5, buttonHeight, 150, 20, new TranslatableComponent("options.controls"),
                button -> this.minecraft.setScreen(MellowUtils.controls(this, this.minecraft))));
        buttonHeight += 25;

        // Language
        this.addRenderableWidget(new Button(this.width / 2 - 155, buttonHeight, 150, 20, new TranslatableComponent("options.language"),
                button -> this.minecraft.setScreen(new LanguageSelectScreen(this, this.minecraft.options, this.minecraft.getLanguageManager()))));

        // Chat Settings
        this.addRenderableWidget(new Button(this.width / 2 + 5, buttonHeight, 150, 20, new TranslatableComponent("options.chat.title"),
                button -> this.minecraft.setScreen(new ChatOptionsScreen(this, this.minecraft.options))));
        buttonHeight += 25;

        // Resource Packs
        this.addRenderableWidget(new Button(this.width / 2 - 155, buttonHeight, 150, 20, new TranslatableComponent("options.resourcepack"),
                button -> this.minecraft.setScreen(MellowUtils.resourcePackList(this, this.minecraft, MUIOptionsScreen::updateResourcePacksList))));

        // Accessibility Settings
        this.addRenderableWidget(new Button(this.width / 2 + 5, buttonHeight, 150, 20, new TranslatableComponent("options.accessibility.title"),
                button -> this.minecraft.setScreen(new AccessibilityOptionsScreen(this, this.minecraft.options))));
        buttonHeight += 25;

        // Super Secret Settings
        this.addRenderableWidget(new Button(this.width / 2 - 155, buttonHeight, 150, 20, new TranslatableComponent("button.mellowui.super_secret_settings"),
                button -> this.minecraft.setScreen(new SuperSecretSettingsScreen(this))));

        // Credits & Attribution
        this.addRenderableWidget(new Button(this.width / 2 + 5, buttonHeight, 150, 20, new TranslatableComponent("button.mellowui.credits_and_attribution"),
                button -> this.minecraft.setScreen(new AttributionsScreen(this))));

        // Done button
        this.addRenderableWidget(new Button(this.width / 2 - 100, this.height - 25, 200, 20, CommonComponents.GUI_DONE,
                button -> this.minecraft.setScreen(this.lastScreen)));
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        drawCenteredString(stack, this.font, this.title, this.width / 2, MellowUtils.DEFAULT_TITLE_HEIGHT, 0xFFFFFF);
        super.render(stack, mouseX, mouseY, partialTicks);
    }

    public static void updateResourcePacksList(PackRepository packList) {
        Minecraft minecraft = Minecraft.getInstance();
        List<String> resourcePacks = ImmutableList.copyOf(minecraft.options.resourcePacks);
        minecraft.options.resourcePacks.clear();
        minecraft.options.incompatibleResourcePacks.clear();

        for (Pack packInfo : packList.getSelectedPacks()) {
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
