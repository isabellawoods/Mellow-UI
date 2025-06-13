package melonystudios.mellowui.mixin.updates;

import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.config.type.TwoStyles;
import melonystudios.mellowui.screen.forge.MUIModUpdateScreen;
import melonystudios.mellowui.screen.widget.ImageSetButton;
import melonystudios.mellowui.util.GUITextures;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.gui.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.realms.RealmsBridgeScreen;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.NotificationModUpdateScreen;
import net.minecraftforge.fml.ModList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(value = IngameMenuScreen.class, priority = 900)
public abstract class UpdatedPauseMenuScreen extends Screen {
    @Shadow
    @Final
    private boolean showPauseMenu;
    @Unique
    private NotificationModUpdateScreen modUpdateNotification;

    public UpdatedPauseMenuScreen(ITextComponent title) {
        super(title);
    }

    @Inject(method = "createPauseMenu", at = @At("HEAD"), cancellable = true)
    protected void createPauseMenu(CallbackInfo callback) {
        if (MellowConfigs.CLIENT_CONFIGS.updatePauseMenu.get()) {
            callback.cancel();
            if (this.minecraft == null) return;
            int yOffset = MellowUtils.PAUSE_MENU_Y_OFFSET;

            // Back to Game
            this.addButton(new Button(this.width / 2 - 102, this.height / 2 - 58 + yOffset, 204, 20, new TranslationTextComponent("menu.returnToGame"), button -> {
                this.minecraft.setScreen(null);
                this.minecraft.mouseHandler.grabMouse();
            }));

            // Advancements
            this.addButton(new Button(this.width / 2 - 102, this.height / 2 - 34 + yOffset, 98, 20, new TranslationTextComponent("gui.advancements"), button -> {
                if (this.minecraft.player != null && this.minecraft.player.connection != null)
                    this.minecraft.setScreen(new AdvancementsScreen(this.minecraft.player.connection.getAdvancements()));
            }, (button, stack, mouseX, mouseY) -> {
                if (this.minecraft.level == null) MellowUtils.renderTooltip(stack, this, button, new TranslationTextComponent("error.mellowui.cannot_load_advancements").withStyle(TextFormatting.RED), mouseX, mouseY);
            })).active = this.minecraft.level != null;

            // Statistics
            this.addButton(new Button(this.width / 2 + 4, this.height / 2 - 34 + yOffset, 98, 20, new TranslationTextComponent("gui.stats"), button -> {
                if (this.minecraft.player != null)
                    this.minecraft.setScreen(new StatsScreen(this, this.minecraft.player.getStats()));
            }, (button, stack, mouseX, mouseY) -> {
                if (this.minecraft.level == null) MellowUtils.renderTooltip(stack, this, button, new TranslationTextComponent("error.mellowui.cannot_load_statistics").withStyle(TextFormatting.RED), mouseX, mouseY);
            })).active = this.minecraft.level != null;

            Button modsButton;
            if (MellowConfigs.CLIENT_CONFIGS.pauseMenuModButton.get() == TwoStyles.OPTION_1) {
                // Mods
                modsButton = this.addButton(new Button(this.width / 2 - 102, this.height / 2 - 10 + yOffset, 204, 20, new TranslationTextComponent("fml.menu.mods"), button ->
                        this.minecraft.setScreen(MellowUtils.modList(this))));
            } else {
                String feedbackURL = SharedConstants.getCurrentVersion().isStable() ? "https://aka.ms/javafeedback?ref=game" : "https://aka.ms/snapshotfeedback?ref=game";

                // Give Feedback
                this.addButton(new Button(this.width / 2 - 102, this.height / 2 - 10 + yOffset, 98, 20, new TranslationTextComponent("menu.sendFeedback"), button ->
                        MellowUtils.openLink(this, feedbackURL, false)));

                // Report Bugs
                this.addButton(new Button(this.width / 2 + 4, this.height / 2 - 10 + yOffset, 98, 20, new TranslationTextComponent("menu.reportBugs"), button ->
                        MellowUtils.openLink(this, "https://aka.ms/snapshotbugs?ref=game", false)));

                // Mods
                modsButton = this.addButton(new ImageSetButton(this.width / 2 + 106, this.height / 2 - 10 + yOffset, 20, 20,
                        GUITextures.MODS_SET, button -> this.minecraft.setScreen(MellowUtils.modList(this)), (button, stack, mouseX, mouseY) ->
                        MellowUtils.renderTooltip(stack, this, button, new TranslationTextComponent("button.mellowui.mods.desc", ModList.get().getMods().size()), mouseX, mouseY),
                        new TranslationTextComponent("fml.menu.mods")));
            }

            this.modUpdateNotification = MUIModUpdateScreen.create(this.minecraft.screen, modsButton, MellowConfigs.CLIENT_CONFIGS.pauseMenuModButton.get() == TwoStyles.OPTION_2);

            // Options
            this.addButton(new Button(this.width / 2 - 102, this.height / 2 + 14 + yOffset, 98, 20, new TranslationTextComponent("menu.options"), button ->
                    this.minecraft.setScreen(new OptionsScreen(this, this.minecraft.options))));

            // Open to LAN
            Button openToLan = this.addButton(new Button(this.width / 2 + 4, this.height / 2 + 14 + yOffset, 98, 20, new TranslationTextComponent("menu.shareToLan"), button ->
                    this.minecraft.setScreen(new ShareToLanScreen(this))));
            openToLan.active = this.minecraft.hasSingleplayerServer() && !this.minecraft.getSingleplayerServer().isPublished();

            // Save and Quit to Title | Disconnect
            Button saveAndQuit = this.addButton(new Button(this.width / 2 - 102, this.height / 2 + 38 + yOffset, 204, 20, new TranslationTextComponent("menu.returnToMenu"), button -> {
                boolean isLANServer = this.minecraft.isLocalServer();
                boolean connectedToRealms = this.minecraft.isConnectedToRealms();
                button.active = false;
                this.minecraft.level.disconnect();

                if (isLANServer) {
                    this.minecraft.clearLevel(new DirtMessageScreen(new TranslationTextComponent("menu.savingLevel")));
                } else {
                    this.minecraft.clearLevel();
                }

                if (isLANServer) {
                    this.minecraft.setScreen(new MainMenuScreen());
                } else if (connectedToRealms) {
                    RealmsBridgeScreen realmsBridge = new RealmsBridgeScreen();
                    realmsBridge.switchToRealms(new MainMenuScreen());
                } else {
                    this.minecraft.setScreen(new MultiplayerScreen(new MainMenuScreen()));
                }
            }));

            if (!this.minecraft.isLocalServer()) saveAndQuit.setMessage(new TranslationTextComponent("menu.disconnect"));
            if (this.minecraft.level == null) saveAndQuit.active = false;
        }
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        if (MellowConfigs.CLIENT_CONFIGS.updatePauseMenu.get()) {
            callback.cancel();
            if (this.showPauseMenu) {
                this.renderBackground(stack);
                drawCenteredString(stack, this.font, this.title, this.width / 2, this.height / 2 - 100 + MellowUtils.PAUSE_MENU_Y_OFFSET, 0xFFFFFF);
            } else {
                drawCenteredString(stack, this.font, this.title, this.width / 2, 10 + MellowUtils.PAUSE_MENU_Y_OFFSET, 0xFFFFFF);
            }

            super.render(stack, mouseX, mouseY, partialTicks);
            this.modUpdateNotification.render(stack, mouseX, mouseY, partialTicks);
        }
    }
}
