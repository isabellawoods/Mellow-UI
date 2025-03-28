package melonystudios.mellowui.mixin.updates;

import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.screen.forge.MUIModUpdateScreen;
import melonystudios.mellowui.util.GUITextures;
import melonystudios.mellowui.util.MellowUtils;
import melonystudios.mellowui.util.MainMenuModButton;
import net.minecraft.client.gui.AccessibilityScreen;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.renderer.RenderSkybox;
import net.minecraft.realms.RealmsBridgeScreen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.gui.NotificationModUpdateScreen;
import net.minecraftforge.fml.client.gui.screen.ModListScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(value = MainMenuScreen.class, priority = 990)
public abstract class UpdatedMainMenuScreen extends Screen {
    @Mutable @Shadow @Final private RenderSkybox panorama;
    @Shadow(remap = false) private NotificationModUpdateScreen modUpdateNotification;
    @Shadow private boolean realmsNotificationsInitialized;
    @Shadow private Screen realmsNotificationsScreen;
    @Shadow @Nullable private String splash;
    @Shadow private int copyrightWidth;
    @Shadow private int copyrightX;
    @Shadow protected abstract void createDemoMenuOptions(int y, int rowHeight);
    @Shadow protected abstract void createNormalMenuOptions(int y, int rowHeight);
    @Shadow protected abstract boolean realmsNotificationsEnabled();
    @Shadow protected abstract void realmsButtonClicked();

    public UpdatedMainMenuScreen(ITextComponent title) {
        super(title);
    }

    @Inject(method = "<init>(Z)V", at = @At("TAIL"), cancellable = true)
    public void constructor(boolean fading, CallbackInfo callback) {
        callback.cancel();
        this.panorama = MellowUtils.PANORAMA;
    }

    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    public void init(CallbackInfo callback) {
        callback.cancel();
        if (this.splash == null && !MellowConfigs.CLIENT_CONFIGS.hideSplashTexts.get()) this.splash = this.minecraft.getSplashManager().getSplash();

        this.copyrightWidth = this.font.width(new TranslationTextComponent("title.credits"));
        this.copyrightX = this.width - this.copyrightWidth - 2;
        int buttonsPos = this.height / 4 + 48;
        Button modsButton = null;

        if (this.minecraft.isDemo()) {
            this.createDemoMenuOptions(buttonsPos, 24);
        } else {
            this.createNormalMenuOptions(buttonsPos, 24);
            // Mods
            MainMenuModButton buttonLocation = MellowConfigs.CLIENT_CONFIGS.mainMenuModButton.get();
            if (buttonLocation == MainMenuModButton.ADJACENT) {
                modsButton = this.addButton(new Button(this.width / 2 + 2, buttonsPos + 24 * 2, 98, 20,
                        new TranslationTextComponent("fml.menu.mods"), button -> this.minecraft.setScreen(new ModListScreen(this))));
            } else if (buttonLocation == MainMenuModButton.REPLACE_REALMS) {
                modsButton = this.addButton(new Button(this.width / 2 - 100, buttonsPos + 24 * 2, 200, 20,
                        new TranslationTextComponent("fml.menu.mods"), button -> this.minecraft.setScreen(new ModListScreen(this))));
            } else if (buttonLocation == MainMenuModButton.ICON) {
                modsButton = this.addButton(new ImageButton(this.width / 2 + 104, buttonsPos + 24 * 2, 20, 20, 0, 0, 20,
                        GUITextures.MODS_BUTTON, 32, 64, button -> this.minecraft.setScreen(new ModListScreen(this)),
                        new TranslationTextComponent("fml.mods.mods")));
            }
        }

        this.modUpdateNotification = MUIModUpdateScreen.create(this.minecraft.screen, modsButton);

        // Language
        this.addButton(new ImageButton(this.width / 2 - 124, buttonsPos + 72 + 12, 20, 20, 0, 106, 20,
                Button.WIDGETS_LOCATION, 256, 256, button -> this.minecraft.setScreen(new LanguageScreen(this, this.minecraft.options, this.minecraft.getLanguageManager())),
                new TranslationTextComponent("narrator.button.language")));

        // Options
        this.addButton(new Button(this.width / 2 - 100, buttonsPos + 72 + 12, 98, 20,
                new TranslationTextComponent("menu.options"), button -> this.minecraft.setScreen(new OptionsScreen(this, this.minecraft.options))));

        // Quit Game
        this.addButton(new Button(this.width / 2 + 2, buttonsPos + 72 + 12, 98, 20,
                new TranslationTextComponent("menu.quit"), button -> this.minecraft.stop()));

        // Accessibility
        this.addButton(new ImageButton(this.width / 2 + 104, buttonsPos + 72 + 12, 20, 20, 0, 0, 20,
                GUITextures.ACCESSIBILITY_BUTTON, 32, 64, button -> this.minecraft.setScreen(new AccessibilityScreen(this, this.minecraft.options)),
                new TranslationTextComponent("narrator.button.accessibility")));

        // Realms availability
        this.minecraft.setConnectedToRealms(false);
        if (this.minecraft.options.realmsNotifications && !this.realmsNotificationsInitialized) {
            RealmsBridgeScreen realmsBridge = new RealmsBridgeScreen();
            this.realmsNotificationsScreen = realmsBridge.getNotificationScreen(this);
            this.realmsNotificationsInitialized = true;
        }

        if (this.realmsNotificationsEnabled()) {
            this.realmsNotificationsScreen.init(this.minecraft, this.width, this.height);
        }
    }

    @Inject(method = "createNormalMenuOptions", at = @At("HEAD"), cancellable = true)
    private void createNormalMenuOptions(int y, int rowHeight, CallbackInfo callback) {
        callback.cancel();

        // Singleplayer
        this.addButton(new Button(this.width / 2 - 100, y, 200, 20,
                new TranslationTextComponent("menu.singleplayer"), button -> this.minecraft.setScreen(new WorldSelectionScreen(this))));

        boolean allowsMultiplayer = this.minecraft.allowsMultiplayer();
        Button.ITooltip multiplayerTooltip = allowsMultiplayer ? Button.NO_TOOLTIP : (button, stack, mouseX, mouseY) -> {
            if (!button.active) {
                this.renderTooltip(stack, this.minecraft.font.split(new TranslationTextComponent("title.multiplayer.disabled"), Math.max(this.width / 2 - 43, 170)), mouseX, mouseY);
            }
        };

        // Multiplayer
        this.addButton(new Button(this.width / 2 - 100, y + rowHeight, 200, 20, new TranslationTextComponent("menu.multiplayer"), button -> {
            Screen multiplayerScreen = this.minecraft.options.skipMultiplayerWarning ? new MultiplayerScreen(this) : new MultiplayerWarningScreen(this);
            this.minecraft.setScreen(multiplayerScreen);
        }, multiplayerTooltip)).active = allowsMultiplayer;

        // Realms
        MainMenuModButton buttonLocation = MellowConfigs.CLIENT_CONFIGS.mainMenuModButton.get();
        if (buttonLocation != MainMenuModButton.REPLACE_REALMS) {
            int width = buttonLocation == MainMenuModButton.ADJACENT ? 98 : 200;
            this.addButton(new Button(this.width / 2 - 100, y + rowHeight * 2, width, 20,
                    new TranslationTextComponent("menu.online"), button -> this.realmsButtonClicked(), multiplayerTooltip)).active = allowsMultiplayer;
        }
    }
}
