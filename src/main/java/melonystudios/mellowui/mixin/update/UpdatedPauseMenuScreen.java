package melonystudios.mellowui.mixin.update;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.realmsclient.RealmsMainScreen;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.config.type.ThreeStyles;
import melonystudios.mellowui.methods.InterfaceMethods;
import melonystudios.mellowui.screen.MusicToast;
import melonystudios.mellowui.screen.RenderComponents;
import melonystudios.mellowui.screen.backport.FeedbackScreen;
import melonystudios.mellowui.screen.widget.ImageSetModButton;
import melonystudios.mellowui.screen.widget.ModButton;
import melonystudios.mellowui.util.GUITextures;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.*;
import net.minecraft.client.gui.screens.achievement.StatsScreen;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.social.SocialInteractionsScreen;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(value = PauseScreen.class, priority = 900)
public abstract class UpdatedPauseMenuScreen extends Screen {
    @Unique
    private final RenderComponents components = RenderComponents.INSTANCE;
    @Shadow
    @Final
    private boolean showPauseMenu;

    public UpdatedPauseMenuScreen(Component title) {
        super(title);
    }

    @Inject(method = "createPauseMenu", at = @At("HEAD"), cancellable = true)
    protected void createPauseMenu(CallbackInfo callback) {
        if (MellowConfigs.CLIENT_CONFIGS.updatePauseMenu.get()) {
            if (this.minecraft == null) return;
            callback.cancel();
            int yOffset = MellowUtils.PAUSE_MENU_Y_OFFSET;

            MusicManager manager = this.minecraft.getMusicManager();
            SoundInstance currentMusic = ((InterfaceMethods.MusicManagerMethods) manager).mui$getNowPlaying();
            if (currentMusic != null) MusicToast.addOrUpdate(currentMusic.getSound().getPath(), true, this.minecraft.getToasts());

            // Back to Game
            this.addRenderableWidget(new Button(this.width / 2 - 102, this.height / 2 - 58 + yOffset, 204, 20, new TranslatableComponent("menu.returnToGame"), button -> {
                this.minecraft.setScreen(null);
                this.minecraft.mouseHandler.grabMouse();
            }));

            // Advancements
            this.addRenderableWidget(new Button(this.width / 2 - 102, this.height / 2 - 34 + yOffset, 98, 20, new TranslatableComponent("gui.advancements"), button -> {
                if (this.minecraft.player != null && this.minecraft.player.connection != null)
                    this.minecraft.setScreen(new AdvancementsScreen(this.minecraft.player.connection.getAdvancements()));
            }, (button, stack, mouseX, mouseY) -> {
                if (this.minecraft.level == null) this.components.renderTooltip(this, button, new TranslatableComponent("error.mellowui.cannot_load_advancements").withStyle(ChatFormatting.RED), mouseX, mouseY);
            })).active = this.minecraft.level != null;

            // Statistics
            this.addRenderableWidget(new Button(this.width / 2 + 4, this.height / 2 - 34 + yOffset, 98, 20, new TranslatableComponent("gui.stats"), button -> {
                if (this.minecraft.player != null)
                    this.minecraft.setScreen(new StatsScreen(this, this.minecraft.player.getStats()));
            }, (button, stack, mouseX, mouseY) -> {
                if (this.minecraft.level == null) this.components.renderTooltip(this, button, new TranslatableComponent("error.mellowui.cannot_load_statistics").withStyle(ChatFormatting.RED), mouseX, mouseY);
            })).active = this.minecraft.level != null;

            ThreeStyles buttonStyle = MellowConfigs.CLIENT_CONFIGS.pauseMenuModButton.get();
            if (buttonStyle == ThreeStyles.OPTION_1 && !this.minecraft.isDemo()) {
                // Feedback
                this.addRenderableWidget(new Button(this.width / 2 - 102, this.height / 2 - 10 + yOffset, 98, 20, new TranslatableComponent("button.mellowui.feedback"), button ->
                        this.minecraft.setScreen(new FeedbackScreen(this))));

                // Mods
                this.addRenderableWidget(new ModButton(this.width / 2 + 4, this.height / 2 - 10 + yOffset, 98, 20, new TranslatableComponent("fml.menu.mods"), button ->
                        this.minecraft.setScreen(MellowUtils.modList(this))));
            } else if (buttonStyle == ThreeStyles.OPTION_3 && !this.minecraft.isDemo()) {
                // Mods
                this.addRenderableWidget(new ModButton(this.width / 2 - 102, this.height / 2 - 10 + yOffset, 204, 20, new TranslatableComponent("fml.menu.mods"), button ->
                        this.minecraft.setScreen(MellowUtils.modList(this))));
            } else {
                String feedbackURL = SharedConstants.getCurrentVersion().isStable() ? "https://aka.ms/javafeedback?ref=game" : "https://aka.ms/snapshotfeedback?ref=game";

                // Give Feedback
                this.addRenderableWidget(new Button(this.width / 2 - 102, this.height / 2 - 10 + yOffset, 98, 20, new TranslatableComponent("menu.sendFeedback"), button ->
                        MellowUtils.openLink(this, feedbackURL, false)));

                // Report Bugs
                this.addRenderableWidget(new Button(this.width / 2 + 4, this.height / 2 - 10 + yOffset, 98, 20, new TranslatableComponent("menu.reportBugs"), button ->
                        MellowUtils.openLink(this, "https://aka.ms/snapshotbugs?ref=game", false)));

                // Mods
                if (this.minecraft.isDemo()) return;
                this.addRenderableWidget(new ImageSetModButton(this.width / 2 + 106, this.height / 2 - 10 + yOffset, 20, 20,
                        GUITextures.MODS_SET, button -> this.minecraft.setScreen(MellowUtils.modList(this)), (button, stack, mouseX, mouseY) ->
                        this.components.renderTooltip(this, button, new TranslatableComponent("button.mellowui.mods.desc", ModList.get().getMods().size()), mouseX, mouseY),
                        new TranslatableComponent("fml.menu.mods")).renderOnCorner(true));
            }

            // Options
            this.addRenderableWidget(new Button(this.width / 2 - 102, this.height / 2 + 14 + yOffset, 98, 20, new TranslatableComponent("menu.options"), button ->
                    this.minecraft.setScreen(new OptionsScreen(this, this.minecraft.options))));

            if (this.minecraft.hasSingleplayerServer() && !this.minecraft.getSingleplayerServer().isPublished()) {
                // Open to LAN
                this.addRenderableWidget(new Button(this.width / 2 + 4, this.height / 2 + 14 + yOffset, 98, 20, new TranslatableComponent("menu.shareToLan"), button ->
                        this.minecraft.setScreen(new ShareToLanScreen(this))));
            } else {
                // Player Reporting (social interactions)
                this.addRenderableWidget(new Button(this.width / 2 + 4, this.height / 2 + 14 + yOffset, 98, 20, new TranslatableComponent("button.mellowui.player_reporting"), button ->
                        this.minecraft.setScreen(new SocialInteractionsScreen()))).active = this.minecraft.level != null;
            }

            // Save and Quit to Title | Disconnect
            Button saveAndQuit = this.addRenderableWidget(new Button(this.width / 2 - 102, this.height / 2 + 38 + yOffset, 204, 20, new TranslatableComponent("menu.returnToMenu"), button -> {
                boolean isLANServer = this.minecraft.isLocalServer();
                boolean connectedToRealms = this.minecraft.isConnectedToRealms();
                button.active = false;
                this.minecraft.level.disconnect();

                if (isLANServer) {
                    this.minecraft.clearLevel(new GenericDirtMessageScreen(new TranslatableComponent("menu.savingLevel")));
                } else {
                    this.minecraft.clearLevel();
                }

                TitleScreen titleScreen = new TitleScreen();
                if (isLANServer) {
                    this.minecraft.setScreen(titleScreen);
                } else if (connectedToRealms) {
                    this.minecraft.setScreen(new RealmsMainScreen(titleScreen));
                } else {
                    this.minecraft.setScreen(new JoinMultiplayerScreen(titleScreen));
                }
            }));

            if (!this.minecraft.isLocalServer()) saveAndQuit.setMessage(new TranslatableComponent("menu.disconnect"));
            if (this.minecraft.level == null) saveAndQuit.active = false;
        }
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
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
