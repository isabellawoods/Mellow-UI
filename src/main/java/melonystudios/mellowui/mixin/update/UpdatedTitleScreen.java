package melonystudios.mellowui.mixin.update;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.exception.RealmsServiceException;
import melonystudios.mellowui.backport.TitleScreen32BitWarning;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.config.WidgetConfigs;
import melonystudios.mellowui.config.type.FourStyles;
import melonystudios.mellowui.config.type.ThreeStyles;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.text.MultiLineLabel;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.element.widget.ImageSetModButton;
import melonystudios.mellowui.element.widget.ModButton;
import melonystudios.mellowui.element.widget.text.PlainTextButton;
import melonystudios.mellowui.methods.InterfaceMethods;
import melonystudios.mellowui.renderer.LogoRenderer;
import melonystudios.mellowui.renderer.SplashRenderer;
import melonystudios.mellowui.resource.panorama.Panoramas;
import melonystudios.mellowui.resource.theme.Themes;
import melonystudios.mellowui.screen.MellomedleyTitleScreen;
import melonystudios.mellowui.screen.MellowCustomizationScreen;
import melonystudios.mellowui.screen.backport.AccessibilityOnboardingScreen;
import melonystudios.mellowui.screen.backport.CreditsAndAttributionsScreen;
import melonystudios.mellowui.util.GUITextures;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.gui.AccessibilityScreen;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.renderer.RenderSkybox;
import net.minecraft.realms.RealmsBridgeScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.BrandingControl;
import net.minecraftforge.fml.ModList;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

@Mixin(value = MainMenuScreen.class, priority = 900)
public abstract class UpdatedTitleScreen extends Screen implements InterfaceMethods.TitleScreenMethods {
    @Unique private final RenderComponents components = RenderComponents.INSTANCE;
    @Shadow @Final private static ResourceLocation PANORAMA_OVERLAY;
    @Mutable @Shadow @Final private RenderSkybox panorama;
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
    @Unique @Nullable private TitleScreen32BitWarning warning32Bit;
    @Unique private final RealmsClient client;
    @Unique public boolean keepLogoThroughFade;

    public UpdatedTitleScreen(ITextComponent title) {
        super(title);
        this.client = RealmsClient.create();
    }

    @Override
    public boolean keepsLogoThroughFade() {
        return this.keepLogoThroughFade;
    }

    @Override
    public void keepLogoThroughFade(boolean keep) {
        this.keepLogoThroughFade = keep;
    }

    @Override
    public ResourceLocation getPanoramaOverlay() {
        return PANORAMA_OVERLAY;
    }

    @Inject(method = "<init>(Z)V", at = @At("TAIL"))
    public void constructor(boolean fading, CallbackInfo callback) {
        this.components.replacePanorama(this.panorama, true);
    }

    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    public void init(CallbackInfo callback) {
        Themes.selectTheme(Themes.theme(), MellowConfigs.CLIENT_CONFIGS.selectedTheme.get());
        Panoramas.selectPanorama(Panoramas.panorama(), MellowConfigs.CLIENT_CONFIGS.selectedPanorama.get());

        // Go to Mellomedley's main menu if set.
        if (MellowConfigs.CLIENT_CONFIGS.titleStyle.get() == ThreeStyles.OPTION_3) {
            this.minecraft.setScreen(new MellomedleyTitleScreen(this.fading, MellowConfigs.CLIENT_CONFIGS.onboardAccessibility.get()));
            return;
        }

        // Open accessibility onboarding if it hasn't been shown (Mellomedley's main menu opens this by itself).
        if (MellowConfigs.CLIENT_CONFIGS.onboardAccessibility.get()) {
            this.keepLogoThroughFade(true);
            this.minecraft.setScreen(new AccessibilityOnboardingScreen(() -> this.minecraft.setScreen(this)));
            return;
        }
        LogoRenderer.rerollEasterEgg();

        if (MellowConfigs.CLIENT_CONFIGS.titleStyle.get() == ThreeStyles.OPTION_2) {
            callback.cancel();
            if (this.splash == null && !MellowConfigs.CLIENT_CONFIGS.hideSplashTexts.get()) this.splash = this.minecraft.getSplashManager().getSplash();

            this.copyrightWidth = this.font.width(new TranslationTextComponent("menu.minecraft.credits"));
            this.copyrightX = this.width - this.copyrightWidth - 2;
            FourStyles buttonStyle = MellowConfigs.CLIENT_CONFIGS.mainMenuModButton.get();
            int buttonsPos = buttonStyle == FourStyles.OPTION_4 && !this.minecraft.isDemo() ? this.height / 4 + 30 : this.height / 4 + 48;
            int heightOffset = this.minecraft.isDemo() ? -24 : (buttonStyle == FourStyles.OPTION_4 ? 24 : 0);

            if (this.minecraft.isDemo()) {
                this.createDemoMenuOptions(buttonsPos, 24);
            } else {
                this.createNormalMenuOptions(buttonsPos, 24);
                // Mods
                if (buttonStyle == FourStyles.OPTION_1) {
                    this.addButton(new ModButton(this.width / 2 + 2, buttonsPos + 48, 98, 20,
                            new TranslationTextComponent("fml.menu.mods"), button -> this.minecraft.setScreen(MellowUtils.modList(this))));
                } else if (buttonStyle == FourStyles.OPTION_3) {
                    this.addButton(new ModButton(this.width / 2 - 100, buttonsPos + 48, 200, 20,
                            new TranslationTextComponent("fml.menu.mods"), button -> this.minecraft.setScreen(MellowUtils.modList(this))));
                } else if (buttonStyle == FourStyles.OPTION_2) {
                    this.addButton(new ImageSetModButton(this.width / 2 + 104, buttonsPos + 48, 20, 20,
                            GUITextures.MODS_SET, button -> this.minecraft.setScreen(MellowUtils.modList(this)), (button, stack, mouseX, mouseY) ->
                            this.components.renderTooltip(this, button, new TranslationTextComponent("button.mellowui.mods.tooltip", ModList.get().getMods().size()), mouseX, mouseY),
                            new TranslationTextComponent("fml.menu.mods")).renderOnCorner(true));
                } else if (buttonStyle == FourStyles.OPTION_4) {
                    this.addButton(new ModButton(this.width / 2 - 100, buttonsPos + 72, 200, 20,
                            new TranslationTextComponent("fml.menu.mods"), button -> this.minecraft.setScreen(MellowUtils.modList(this))));
                }
            }

            // Language
            this.addButton(new ImageButton(this.width / 2 - 124, buttonsPos + 84 + heightOffset, 20, 20, 0, 106, 20,
                    Button.WIDGETS_LOCATION, 256, 256, button -> this.minecraft.setScreen(new LanguageScreen(this, this.minecraft.options, this.minecraft.getLanguageManager())), (button, stack, mouseX, mouseY) ->
                    this.components.renderTooltip(this, button, new TranslationTextComponent("options.language"), mouseX, mouseY),
                    new TranslationTextComponent("narrator.button.language")));

            // Options
            this.addButton(new Button(this.width / 2 - 100, buttonsPos + 84 + heightOffset, 98, 20,
                    new TranslationTextComponent("menu.options"), button -> this.minecraft.setScreen(new OptionsScreen(this, this.minecraft.options))));

            // Quit Game
            this.addButton(new Button(this.width / 2 + 2, buttonsPos + 84 + heightOffset, 98, 20,
                    new TranslationTextComponent("menu.quit"), button -> this.minecraft.stop()));

            // Accessibility Settings
            this.addButton(new ImageButton(this.width / 2 + 104, buttonsPos + 84 + heightOffset, 20, 20, 0, 0, 20,
                    GUITextures.ACCESSIBILITY_BUTTON, 32, 64, button -> this.minecraft.setScreen(new AccessibilityScreen(this, this.minecraft.options)), (button, stack, mouseX, mouseY) ->
                    this.components.renderTooltip(this, button, new TranslationTextComponent("options.accessibility.title"), mouseX, mouseY),
                    new TranslationTextComponent("narrator.button.accessibility")));

            // Copyright text
            this.addButton(new PlainTextButton(this.copyrightX, this.height - 10, this.copyrightWidth, 10,
                    new TranslationTextComponent("menu.minecraft.credits"), button -> this.minecraft.setScreen(new CreditsAndAttributionsScreen(this)), this.font));

            // Realms availability
            this.minecraft.setConnectedToRealms(false);
            if (this.minecraft.options.realmsNotifications && !this.realmsNotificationsInitialized) {
                RealmsBridgeScreen realmsBridge = new RealmsBridgeScreen();
                this.realmsNotificationsScreen = realmsBridge.getNotificationScreen(this);
                this.realmsNotificationsInitialized = true;
            }

            if (this.realmsNotificationsEnabled()) this.realmsNotificationsScreen.init(this.minecraft, this.width, this.height);

            if (!this.minecraft.is64Bit()) {
                CompletableFuture<Boolean> subscriptionFuture = this.warning32Bit != null ? this.warning32Bit.realmsSubscriptionFuture() : CompletableFuture.supplyAsync(this::hasRealmsSubscription, Util.backgroundExecutor());
                this.warning32Bit = new TitleScreen32BitWarning(MultiLineLabel.create(this.font, 350, 2, new TranslationTextComponent("menu.minecraft.32bit_deprecation")), this.width / 2, buttonsPos - 36, subscriptionFuture);
            }
        }

        // Switch Style
        this.addButton(this.components.switchStyle(button -> MellowUtils.switchTitleScreenStyle(this.minecraft), this.width - 21, 8));

        // Customize
        this.addButton(this.components.customize(button -> this.minecraft.setScreen(new MellowCustomizationScreen(this, this.minecraft.options)), this.width - 21, 21));
    }

    /// Adds Singleplayer and Multiplayer buttons on Main Menu for players who have bought the game.
    @Inject(method = "createNormalMenuOptions", at = @At("HEAD"), cancellable = true)
    private void createNormalMenuOptions(int y, int rowHeight, CallbackInfo callback) {
        if (MellowConfigs.CLIENT_CONFIGS.titleStyle.get() == ThreeStyles.OPTION_2) {
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
            FourStyles buttonLocation = MellowConfigs.CLIENT_CONFIGS.mainMenuModButton.get();
            if (buttonLocation != FourStyles.OPTION_3) {
                int width = buttonLocation == FourStyles.OPTION_1 ? 98 : 200;
                this.addButton(new Button(this.width / 2 - 100, y + rowHeight * 2, width, 20,
                        new TranslationTextComponent("menu.online"), button -> this.realmsButtonClicked(), multiplayerTooltip)).active = allowsMultiplayer;
            }
        }
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        if (MellowConfigs.CLIENT_CONFIGS.titleStyle.get() != ThreeStyles.OPTION_1) {
            if (MellowConfigs.CLIENT_CONFIGS.titleStyle.get() == ThreeStyles.OPTION_3) return;
            callback.cancel();
            if (this.fadeInStart == 0L && this.fading) this.fadeInStart = Util.getMillis();

            float overlayTransparency = this.fading ? (float) (Util.getMillis() - this.fadeInStart) / 1000 : 1;
            this.components.renderPanorama(partialTicks, this.width, this.height, this.fading ? overlayTransparency : 1);
            if (MellowConfigs.CLIENT_CONFIGS.fadingBlur.get()) this.components.renderBlurredBackground(partialTicks, false);
            else this.components.renderBackgroundShaders(partialTicks);
            float buttonAlpha = this.fading ? MathHelper.clamp(overlayTransparency - 1, 0, 1) : 1;
            int textAlpha = MathHelper.ceil(buttonAlpha * 255) << 24;
            this.components.renderLogo(this, this.width, this.height, buttonAlpha, this.keepsLogoThroughFade());

            if ((textAlpha & 0xFC000000) != 0) {
                int textColor = TextComponents.selectableColor(false, true);

                // 32-bit deprecation warning
                if (this.warning32Bit != null) {
                    this.components.renderCenteredLabelBackground(new TranslationTextComponent("menu.minecraft.32bit_deprecation"), this.warning32Bit.x(), this.warning32Bit.y(), 9, 2, 350, 2, WidgetConfigs.WIDGET_CONFIGS.warning32BitColor.get() | 85 << 24);
                    this.warning32Bit.label().renderCentered(stack, this.warning32Bit.x(), this.warning32Bit.y(), 9, textColor | textAlpha);
                }

                // Forge's beta warning
                this.components.renderForgeBetaText(this.width, 3, textColor, textAlpha);

                // Title screen icons background
                this.components.renderTitleScreenIconsBackground(this.width - 22, 7, buttonAlpha);

                // Splashes
                if (!MellowConfigs.CLIENT_CONFIGS.hideSplashTexts.get()) {
                    if (MellowConfigs.CLIENT_CONFIGS.splashTextPosition.get()) SplashRenderer.updatedSplash(stack, this.font, this.splash, this.width, textAlpha);
                    else SplashRenderer.defaultSplash(stack, this.font, this.splash, this.width, textAlpha);
                }

                // Text
                if (MellowConfigs.CLIENT_CONFIGS.brandingLines.get()) {
                    BrandingControl.forEachLine(true, true, (lineHeight, text) ->
                            drawString(stack, this.font, text, 2, this.height - (10 + lineHeight * (this.font.lineHeight + 1)), textColor | textAlpha));
                    BrandingControl.forEachAboveCopyrightLine((lineHeight, text) ->
                            drawString(stack, this.font, text, this.width - font.width(text), this.height - (10 + (lineHeight + 1) * (this.font.lineHeight + 1)), textColor | textAlpha));
                } else {
                    ITextComponent releaseBranding = new TranslationTextComponent("menu.minecraft." + (this.minecraft.isDemo() ? "demo" : "branding"), SharedConstants.getCurrentVersion().getName(), ModList.get().size());
                    ITextComponent snapshotBranding = new TranslationTextComponent("menu.minecraft." + (this.minecraft.isDemo() ? "demo" : "branding") + ".snapshot", SharedConstants.getCurrentVersion().getName(),
                            this.minecraft.getVersionType(), ModList.get().size());
                    ITextComponent branding = this.minecraft.getVersionType().equalsIgnoreCase("release") ? releaseBranding : snapshotBranding;

                    drawString(stack, this.font, branding, 2, this.height - 10, textColor | textAlpha);
                }

                for (Widget widget : this.buttons) widget.setAlpha(buttonAlpha);
                super.render(stack, mouseX, mouseY, partialTicks);
                if (this.realmsNotificationsEnabled() && buttonAlpha >= 1) this.realmsNotificationsScreen.render(stack, mouseX, mouseY, partialTicks);
            }
        }
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;ceil(F)I"))
    public void renderBackgroundShaders(MatrixStack stack, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        if (MellowConfigs.CLIENT_CONFIGS.fadingBlur.get()) this.components.renderBlurredBackground(partialTicks, false);
        else this.components.renderBackgroundShaders(partialTicks);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    public void mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> callback) {
        if (MellowConfigs.CLIENT_CONFIGS.titleStyle.get() == ThreeStyles.OPTION_2) {
            callback.setReturnValue(super.mouseClicked(mouseX, mouseY, button) || (this.realmsNotificationsEnabled() && this.realmsNotificationsScreen.mouseClicked(mouseX, mouseY, button)));
        }
    }

    @Unique
    private boolean hasRealmsSubscription() {
        try {
            return this.client.listWorlds().servers.stream().anyMatch(server -> server.ownerUUID != null && !server.expired && server.ownerUUID.equals(this.minecraft.getUser().getUuid()));
        } catch (RealmsServiceException exception) {
            return false;
        }
    }
}
