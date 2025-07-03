package melonystudios.mellowui.mixin.update;

import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.config.type.ThreeStyles;
import melonystudios.mellowui.methods.InterfaceMethods;
import melonystudios.mellowui.screen.MusicToast;
import melonystudios.mellowui.screen.backport.FeedbackScreen;
import melonystudios.mellowui.screen.widget.ImageSetModButton;
import melonystudios.mellowui.screen.widget.ModButton;
import melonystudios.mellowui.util.GUITextures;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.gui.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.social.SocialInteractionsScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.realms.RealmsBridgeScreen;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(value = IngameMenuScreen.class, priority = 900)
public abstract class UpdatedPauseMenuScreen extends Screen {
    @Shadow
    @Final
    private boolean showPauseMenu;

    public UpdatedPauseMenuScreen(ITextComponent title) {
        super(title);
    }

    @Inject(method = "createPauseMenu", at = @At("HEAD"), cancellable = true)
    protected void createPauseMenu(CallbackInfo callback) {
        if (MellowConfigs.CLIENT_CONFIGS.updatePauseMenu.get()) {
            if (this.minecraft == null) return;
            callback.cancel();
            int yOffset = MellowUtils.PAUSE_MENU_Y_OFFSET;

            MusicTicker manager = this.minecraft.getMusicManager();
            ISound currentMusic = ((InterfaceMethods.MusicManagerMethods) manager).mui$getNowPlaying();
            if (currentMusic != null) MusicToast.addOrUpdate(currentMusic.getSound().getPath(), true, this.minecraft.getToasts());

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

            ThreeStyles buttonStyle = MellowConfigs.CLIENT_CONFIGS.pauseMenuModButton.get();
            if (buttonStyle == ThreeStyles.OPTION_1 && !this.minecraft.isDemo()) {
                // Feedback
                this.addButton(new Button(this.width / 2 - 102, this.height / 2 - 10 + yOffset, 98, 20, new TranslationTextComponent("button.mellowui.feedback"), button ->
                        this.minecraft.setScreen(new FeedbackScreen(this))));

                // Mods
                this.addButton(new ModButton(this.width / 2 + 4, this.height / 2 - 10 + yOffset, 98, 20, new TranslationTextComponent("fml.menu.mods"), button ->
                        this.minecraft.setScreen(MellowUtils.modList(this))));
            } else if (buttonStyle == ThreeStyles.OPTION_3 && !this.minecraft.isDemo()) {
                // Mods
                this.addButton(new ModButton(this.width / 2 - 102, this.height / 2 - 10 + yOffset, 204, 20, new TranslationTextComponent("fml.menu.mods"), button ->
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
                if (this.minecraft.isDemo()) return;
                this.addButton(new ImageSetModButton(this.width / 2 + 106, this.height / 2 - 10 + yOffset, 20, 20,
                        GUITextures.MODS_SET, button -> this.minecraft.setScreen(MellowUtils.modList(this)), (button, stack, mouseX, mouseY) ->
                        MellowUtils.renderTooltip(stack, this, button, new TranslationTextComponent("button.mellowui.mods.desc", ModList.get().getMods().size()), mouseX, mouseY),
                        new TranslationTextComponent("fml.menu.mods")).renderOnCorner(true));
            }

            // Options
            this.addButton(new Button(this.width / 2 - 102, this.height / 2 + 14 + yOffset, 98, 20, new TranslationTextComponent("menu.options"), button ->
                    this.minecraft.setScreen(new OptionsScreen(this, this.minecraft.options))));

            if (this.minecraft.hasSingleplayerServer() && !this.minecraft.getSingleplayerServer().isPublished()) {
                // Open to LAN
                this.addButton(new Button(this.width / 2 + 4, this.height / 2 + 14 + yOffset, 98, 20, new TranslationTextComponent("menu.shareToLan"), button ->
                        this.minecraft.setScreen(new ShareToLanScreen(this))));
            } else {
                // Player Reporting (social interactions)
                this.addButton(new Button(this.width / 2 + 4, this.height / 2 + 14 + yOffset, 98, 20, new TranslationTextComponent("button.mellowui.player_reporting"), button ->
                        this.minecraft.setScreen(new SocialInteractionsScreen()))).active = this.minecraft.level != null;
            }

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
        }
    }
}
