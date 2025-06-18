package melonystudios.mellowui.mixin.update;

import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.config.type.ThreeStyles;
import melonystudios.mellowui.methods.InterfaceMethods;
import melonystudios.mellowui.renderer.LogoRenderer;
import melonystudios.mellowui.renderer.SplashRenderer;
import melonystudios.mellowui.screen.widget.WidgetTextureSet;
import melonystudios.mellowui.screen.forge.MUIModUpdateScreen;
import melonystudios.mellowui.screen.backport.AccessibilityOnboardingScreen;
import melonystudios.mellowui.screen.backport.AttributionsScreen;
import melonystudios.mellowui.screen.MellomedleyTitleScreen;
import melonystudios.mellowui.screen.widget.IconButton;
import melonystudios.mellowui.screen.widget.ImageSetButton;
import melonystudios.mellowui.util.GUITextures;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.AccessibilityScreen;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.renderer.RenderSkybox;
import net.minecraft.realms.RealmsBridgeScreen;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.gui.NotificationModUpdateScreen;
import net.minecraftforge.fml.BrandingControl;
import net.minecraftforge.fml.ModList;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(value = MainMenuScreen.class, priority = 900)
public abstract class UpdatedMainMenuScreen extends Screen implements InterfaceMethods.MainMenuMethods {
    @Mutable @Shadow @Final private RenderSkybox panorama;
    @Shadow(remap = false)
    private NotificationModUpdateScreen modUpdateNotification;
    @Shadow private boolean realmsNotificationsInitialized;
    @Shadow private Screen realmsNotificationsScreen;
    @Shadow @Nullable private String splash;
    @Shadow private int copyrightWidth;
    @Shadow private int copyrightX;
    @Shadow private long fadeInStart;
    @Shadow @Final private boolean fading;
    @Shadow protected abstract void createDemoMenuOptions(int y, int rowHeight);
    @Shadow protected abstract void createNormalMenuOptions(int y, int rowHeight);
    @Shadow protected abstract boolean realmsNotificationsEnabled();
    @Shadow protected abstract void realmsButtonClicked();
    @Unique public boolean keepLogoThroughFade;

    public UpdatedMainMenuScreen(ITextComponent title) {
        super(title);
    }

    @Override
    public boolean keepsLogoThroughFade() {
        return this.keepLogoThroughFade;
    }

    @Override
    public void keepLogoThroughFade(boolean keep) {
        this.keepLogoThroughFade = keep;
    }

    @Inject(method = "<init>(Z)V", at = @At("TAIL"))
    public void constructor(boolean fading, CallbackInfo callback) {
        if (!((InterfaceMethods.PanoramaRendererMethods) MellowUtils.PANORAMA).samePanorama(this.panorama)) {
            MellowUtils.PANORAMA = this.panorama;
        }
    }

    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    public void init(CallbackInfo callback) {
        // Go to Mellomedley's main menu if set.
        if (MellowConfigs.CLIENT_CONFIGS.mainMenuStyle.get() == ThreeStyles.OPTION_3) {
            this.minecraft.setScreen(new MellomedleyTitleScreen(this.fading, MellowConfigs.CLIENT_CONFIGS.onboardAccessibility.get()));
            return;
        }

        // Open accessibility onboarding if it hasn't been shown (Mellomedley's main menu opens this by itself).
        if (MellowConfigs.CLIENT_CONFIGS.onboardAccessibility.get()) {
            this.keepLogoThroughFade(true);
            this.minecraft.setScreen(new AccessibilityOnboardingScreen(() -> this.minecraft.setScreen(this)));
        }

        if (MellowConfigs.CLIENT_CONFIGS.mainMenuStyle.get() == ThreeStyles.OPTION_2) {
            callback.cancel();
            if (this.splash == null && !MellowConfigs.CLIENT_CONFIGS.hideSplashTexts.get()) this.splash = this.minecraft.getSplashManager().getSplash();

            this.copyrightWidth = this.font.width(new TranslationTextComponent("menu.minecraft.credits"));
            this.copyrightX = this.width - this.copyrightWidth - 2;
            int buttonsPos = this.height / 4 + 48;
            Button modsButton = null;

            if (this.minecraft.isDemo()) {
                this.createDemoMenuOptions(buttonsPos, 24);
            } else {
                this.createNormalMenuOptions(buttonsPos, 24);
                // Mods
                ThreeStyles buttonLocation = MellowConfigs.CLIENT_CONFIGS.mainMenuModButton.get();
                if (buttonLocation == ThreeStyles.OPTION_1) {
                    modsButton = this.addButton(new Button(this.width / 2 + 2, buttonsPos + 24 * 2, 98, 20,
                            new TranslationTextComponent("fml.menu.mods"), button -> this.minecraft.setScreen(MellowUtils.modList(this))));
                } else if (buttonLocation == ThreeStyles.OPTION_3) {
                    modsButton = this.addButton(new Button(this.width / 2 - 100, buttonsPos + 24 * 2, 200, 20,
                            new TranslationTextComponent("fml.menu.mods"), button -> this.minecraft.setScreen(MellowUtils.modList(this))));
                } else if (buttonLocation == ThreeStyles.OPTION_2) {
                    modsButton = this.addButton(new ImageSetButton(this.width / 2 + 104, buttonsPos + 24 * 2, 20, 20,
                            GUITextures.MODS_SET, button -> this.minecraft.setScreen(MellowUtils.modList(this)), (button, stack, mouseX, mouseY) ->
                            MellowUtils.renderTooltip(stack, this, button, new TranslationTextComponent("button.mellowui.mods.desc", ModList.get().getMods().size()), mouseX, mouseY),
                            new TranslationTextComponent("fml.menu.mods")));
                }
            }

            this.modUpdateNotification = MUIModUpdateScreen.create(this.minecraft.screen, modsButton, MellowConfigs.CLIENT_CONFIGS.mainMenuModButton.get() == ThreeStyles.OPTION_2);

            // Language
            this.addButton(new ImageButton(this.width / 2 - 124, buttonsPos + 72 + 12, 20, 20, 0, 106, 20,
                    Button.WIDGETS_LOCATION, 256, 256, button -> this.minecraft.setScreen(new LanguageScreen(this, this.minecraft.options, this.minecraft.getLanguageManager())), (button, stack, mouseX, mouseY) ->
                    MellowUtils.renderTooltip(stack, this, button, new TranslationTextComponent("options.language"), mouseX, mouseY),
                    new TranslationTextComponent("narrator.button.language")));

            // Options
            this.addButton(new Button(this.width / 2 - 100, buttonsPos + 72 + 12, 98, 20,
                    new TranslationTextComponent("menu.options"), button -> this.minecraft.setScreen(new OptionsScreen(this, this.minecraft.options))));

            // Quit Game
            this.addButton(new Button(this.width / 2 + 2, buttonsPos + 72 + 12, 98, 20,
                    new TranslationTextComponent("menu.quit"), button -> this.minecraft.stop()));

            // Accessibility Settings
            this.addButton(new ImageButton(this.width / 2 + 104, buttonsPos + 72 + 12, 20, 20, 0, 0, 20,
                    GUITextures.ACCESSIBILITY_BUTTON, 32, 64, button -> this.minecraft.setScreen(new AccessibilityScreen(this, this.minecraft.options)), (button, stack, mouseX, mouseY) ->
                    MellowUtils.renderTooltip(stack, this, button, new TranslationTextComponent("options.accessibility.title"), mouseX, mouseY),
                    new TranslationTextComponent("narrator.button.accessibility")));

            // Realms availability
            this.minecraft.setConnectedToRealms(false);
            if (this.minecraft.options.realmsNotifications && !this.realmsNotificationsInitialized) {
                RealmsBridgeScreen realmsBridge = new RealmsBridgeScreen();
                this.realmsNotificationsScreen = realmsBridge.getNotificationScreen(this);
                this.realmsNotificationsInitialized = true;
            }

            if (this.realmsNotificationsEnabled()) this.realmsNotificationsScreen.init(this.minecraft, this.width, this.height);
        }

        // Switch Style
        this.addButton(new IconButton(this.width - 20, 8, 12, 12, GUITextures.SWITCH_STYLE_SET, new TranslationTextComponent("button.mellowui.switch_style"),
                button -> WidgetTextureSet.switchTitleScreenStyle(), (button, stack, mouseX, mouseY) ->
                MellowUtils.renderTooltip(stack, this, button, new TranslationTextComponent("button.mellowui.switch_style"), mouseX, mouseY)));
    }

    @Inject(method = "createNormalMenuOptions", at = @At("HEAD"), cancellable = true)
    private void createNormalMenuOptions(int y, int rowHeight, CallbackInfo callback) {
        if (MellowConfigs.CLIENT_CONFIGS.mainMenuStyle.get() == ThreeStyles.OPTION_2) {
            callback.cancel();

            // Singleplayer
            this.addButton(new Button(this.width / 2 - 100, y, 200, 20,
                    new TranslationTextComponent("menu.singleplayer"), button -> this.minecraft.setScreen(new WorldSelectionScreen(this))));

            boolean allowsMultiplayer = this.minecraft.allowsMultiplayer();
            Button.ITooltip multiplayerTooltip = allowsMultiplayer ? Button.NO_TOOLTIP : (button, stack, mouseX, mouseY) -> {
                if (!button.active)
                    this.renderTooltip(stack, this.minecraft.font.split(new TranslationTextComponent("title.multiplayer.disabled"), Math.max(this.width / 2 - 43, 170)), mouseX, mouseY);
            };

            // Multiplayer
            this.addButton(new Button(this.width / 2 - 100, y + rowHeight, 200, 20, new TranslationTextComponent("menu.multiplayer"), button -> {
                Screen multiplayerScreen = this.minecraft.options.skipMultiplayerWarning ? new MultiplayerScreen(this) : new MultiplayerWarningScreen(this);
                this.minecraft.setScreen(multiplayerScreen);
            }, multiplayerTooltip)).active = allowsMultiplayer;

            // Realms
            ThreeStyles buttonLocation = MellowConfigs.CLIENT_CONFIGS.mainMenuModButton.get();
            if (buttonLocation != ThreeStyles.OPTION_3) {
                int width = buttonLocation == ThreeStyles.OPTION_1 ? 98 : 200;
                this.addButton(new Button(this.width / 2 - 100, y + rowHeight * 2, width, 20,
                        new TranslationTextComponent("menu.online"), button -> this.realmsButtonClicked(), multiplayerTooltip)).active = allowsMultiplayer;
            }
        }
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        if (MellowConfigs.CLIENT_CONFIGS.mainMenuStyle.get() != ThreeStyles.OPTION_1) {
            if (MellowConfigs.CLIENT_CONFIGS.mainMenuStyle.get() == ThreeStyles.OPTION_3) return;
            callback.cancel();
            if (this.fadeInStart == 0L && this.fading) this.fadeInStart = Util.getMillis();

            float overlayTransparency = this.fading ? (float) (Util.getMillis() - this.fadeInStart) / 1000 : 1;
            MellowUtils.renderPanorama(stack, partialTicks, this.width, this.height, this.fading ? overlayTransparency : 1); // should work the same
            MellowUtils.renderBackgroundWithShaders(partialTicks);
            float buttonAlpha = this.fading ? MathHelper.clamp(overlayTransparency - 1, 0, 1) : 1;
            int textAlpha = MathHelper.ceil(buttonAlpha * 255) << 24;

            switch (MellowConfigs.CLIENT_CONFIGS.logoStyle.get()) {
                case OPTION_1: // Pre 1.19
                    LogoRenderer.renderOldLogo(stack, this, this.width, buttonAlpha, this.keepsLogoThroughFade());
                    break;
                case OPTION_2: // 1.20 and above
                    LogoRenderer.renderUpdatedLogo(stack, this.width, buttonAlpha, this.keepsLogoThroughFade());
                    break;
                case OPTION_3: // Mellomedley's logo
                    LogoRenderer.renderMellomedleyLogo(stack, this.width / 2 - 129, 10, 258, 100, buttonAlpha, this.keepsLogoThroughFade());
                    break;
            }

            if ((textAlpha & 0xFC000000) != 0) {
                ForgeHooksClient.renderMainMenu((MainMenuScreen) this.minecraft.screen, stack, this.font, this.width, this.height, textAlpha);
                if (!MellowConfigs.CLIENT_CONFIGS.hideSplashTexts.get()) {
                    if (MellowConfigs.CLIENT_CONFIGS.splashTextPosition.get()) SplashRenderer.updatedSplash(stack, this.font, this.splash, this.width, textAlpha);
                    else SplashRenderer.defaultSplash(stack, this.font, this.splash, this.width, textAlpha);
                }

                if (!MellowConfigs.CLIENT_CONFIGS.disableBranding.get()) {
                    BrandingControl.forEachLine(true, true, (lineHeight, text) ->
                            drawString(stack, this.font, text, 2, this.height - (10 + lineHeight * (this.font.lineHeight + 1)), 0xFFFFFF | textAlpha));
                    BrandingControl.forEachAboveCopyrightLine((lineHeight, text) ->
                            drawString(stack, this.font, text, this.width - font.width(text), this.height - (10 + (lineHeight + 1) * (this.font.lineHeight + 1)), 0xFFFFFF | textAlpha));
                } else {
                    ITextComponent releaseBranding = new TranslationTextComponent("menu.minecraft." + (this.minecraft.isDemo() ? "demo" : "branding"), SharedConstants.getCurrentVersion().getName(), ModList.get().size());
                    ITextComponent snapshotBranding = new TranslationTextComponent("menu.minecraft." + (this.minecraft.isDemo() ? "demo" : "branding") + ".snapshot", SharedConstants.getCurrentVersion().getName(),
                            this.minecraft.getVersionType(), ModList.get().size());
                    ITextComponent branding = this.minecraft.getVersionType().equalsIgnoreCase("release") ? releaseBranding : snapshotBranding;

                    drawString(stack, this.font, branding, 2, this.height - 10, 0xFFFFFF | textAlpha);
                }
                boolean copyrightTextHovered = mouseX > this.copyrightX && mouseX < this.copyrightX + this.copyrightWidth && mouseY > this.height - 10 && mouseY < this.height;

                drawString(stack, this.font, new TranslationTextComponent("menu.minecraft.credits"), this.copyrightX, this.height - 10, MellowUtils.getSelectableTextColor(copyrightTextHovered, true) | textAlpha);
                if (copyrightTextHovered) {
                    fill(stack, this.copyrightX, this.height - 2, this.copyrightX + this.copyrightWidth, this.height - 1, MellowUtils.getSelectableTextColor(true, true) | textAlpha);
                    fill(stack, this.copyrightX + 1, this.height - 1, this.copyrightX + this.copyrightWidth + 1, this.height, MellowUtils.getSelectableTextShadowColor(true, true) | textAlpha);
                }

                for (Widget widget : this.buttons) widget.setAlpha(buttonAlpha);

                super.render(stack, mouseX, mouseY, partialTicks);
                if (this.realmsNotificationsEnabled() && buttonAlpha >= 1) this.realmsNotificationsScreen.render(stack, mouseX, mouseY, partialTicks);
                ((MUIModUpdateScreen) this.modUpdateNotification).alpha = buttonAlpha;
                this.modUpdateNotification.render(stack, mouseX, mouseY, partialTicks);
            }
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    public void mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> callback) {
        if (MellowConfigs.CLIENT_CONFIGS.mainMenuStyle.get() == ThreeStyles.OPTION_2) {
            callback.cancel();
            if (super.mouseClicked(mouseX, mouseY, button)) {
                callback.setReturnValue(true);
            } else if (this.realmsNotificationsEnabled() && this.realmsNotificationsScreen.mouseClicked(mouseX, mouseY, button)) {
                callback.setReturnValue(true);
            } else {
                if (mouseX > this.copyrightX && mouseX < this.copyrightX + this.copyrightWidth && mouseY > this.height - 10 && mouseY < this.height) {
                    this.minecraft.setScreen(new AttributionsScreen(this));
                    this.minecraft.getSoundManager().play(SimpleSound.forUI(SoundEvents.UI_BUTTON_CLICK, 1));
                }

                callback.setReturnValue(false);
            }
        }
    }
}
