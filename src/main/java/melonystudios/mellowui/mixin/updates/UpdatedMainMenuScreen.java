package melonystudios.mellowui.mixin.updates;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.screen.backport.AttributionsScreen;
import melonystudios.mellowui.screen.forge.MUIModUpdateScreen;
import melonystudios.mellowui.screen.updated.MellomedleyMainMenuScreen;
import melonystudios.mellowui.util.GUITextures;
import melonystudios.mellowui.util.MainMenuStyle;
import melonystudios.mellowui.util.MellowUtils;
import melonystudios.mellowui.util.MainMenuModButton;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.AccessibilityScreen;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.renderer.RenderSkybox;
import net.minecraft.realms.RealmsBridgeScreen;
import net.minecraft.util.ResourceLocation;
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
public abstract class UpdatedMainMenuScreen extends Screen {
    @Shadow @Final private static ResourceLocation MINECRAFT_LOGO;
    @Shadow @Final private static ResourceLocation MINECRAFT_EDITION;
    @Shadow @Final private static ResourceLocation PANORAMA_OVERLAY;
    @Mutable @Shadow @Final private RenderSkybox panorama;
    @Shadow(remap = false)
    private NotificationModUpdateScreen modUpdateNotification;
    @Shadow private boolean realmsNotificationsInitialized;
    @Shadow private Screen realmsNotificationsScreen;
    @Shadow @Final private boolean minceraftEasterEgg;
    @Shadow @Nullable private String splash;
    @Shadow private int copyrightWidth;
    @Shadow private int copyrightX;
    @Shadow private long fadeInStart;
    @Shadow @Final private boolean fading;
    @Shadow protected abstract void createDemoMenuOptions(int y, int rowHeight);
    @Shadow protected abstract void createNormalMenuOptions(int y, int rowHeight);
    @Shadow protected abstract boolean realmsNotificationsEnabled();
    @Shadow protected abstract void realmsButtonClicked();

    public UpdatedMainMenuScreen(ITextComponent title) {
        super(title);
    }

    @Inject(method = "<init>(Z)V", at = @At("TAIL"))
    public void constructor(boolean fading, CallbackInfo callback) {
        this.panorama = MellowUtils.PANORAMA;
    }

    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    public void init(CallbackInfo callback) {
        if (MellowConfigs.CLIENT_CONFIGS.mainMenuStyle.get() == MainMenuStyle.MELLOMEDLEY) {
            this.minecraft.setScreen(new MellomedleyMainMenuScreen(this.fading));
            return;
        }

        if (MellowConfigs.CLIENT_CONFIGS.mainMenuStyle.get() == MainMenuStyle.MELLOW_UI) {
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
                MainMenuModButton buttonLocation = MellowConfigs.CLIENT_CONFIGS.mainMenuModButton.get();
                if (buttonLocation == MainMenuModButton.ADJACENT) {
                    modsButton = this.addButton(new Button(this.width / 2 + 2, buttonsPos + 24 * 2, 98, 20,
                            new TranslationTextComponent("fml.menu.mods"), button -> this.minecraft.setScreen(MellowUtils.modList(this))));
                } else if (buttonLocation == MainMenuModButton.REPLACE_REALMS) {
                    modsButton = this.addButton(new Button(this.width / 2 - 100, buttonsPos + 24 * 2, 200, 20,
                            new TranslationTextComponent("fml.menu.mods"), button -> this.minecraft.setScreen(MellowUtils.modList(this))));
                } else if (buttonLocation == MainMenuModButton.ICON) {
                    modsButton = this.addButton(new ImageButton(this.width / 2 + 104, buttonsPos + 24 * 2, 20, 20, 0, 0, 20,
                            GUITextures.MODS_BUTTON, 32, 64, button -> this.minecraft.setScreen(MellowUtils.modList(this)),
                            new TranslationTextComponent("fml.mods.mods")));
                }
            }

            this.modUpdateNotification = MUIModUpdateScreen.create(this.minecraft.screen, modsButton, MellowConfigs.CLIENT_CONFIGS.mainMenuModButton.get() == MainMenuModButton.ICON);

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
    }

    @Inject(method = "createNormalMenuOptions", at = @At("HEAD"), cancellable = true)
    private void createNormalMenuOptions(int y, int rowHeight, CallbackInfo callback) {
        if (MellowConfigs.CLIENT_CONFIGS.mainMenuStyle.get() == MainMenuStyle.MELLOW_UI) {
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
            MainMenuModButton buttonLocation = MellowConfigs.CLIENT_CONFIGS.mainMenuModButton.get();
            if (buttonLocation != MainMenuModButton.REPLACE_REALMS) {
                int width = buttonLocation == MainMenuModButton.ADJACENT ? 98 : 200;
                this.addButton(new Button(this.width / 2 - 100, y + rowHeight * 2, width, 20,
                        new TranslationTextComponent("menu.online"), button -> this.realmsButtonClicked(), multiplayerTooltip)).active = allowsMultiplayer;
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        if (MellowConfigs.CLIENT_CONFIGS.mainMenuStyle.get() != MainMenuStyle.VANILLA) {
            callback.cancel();
            if (this.fadeInStart == 0L && this.fading) this.fadeInStart = Util.getMillis();

            float buttonFadeIn = this.fading ? (float) (Util.getMillis() - this.fadeInStart) / 1000 : 1;
            fill(stack, 0, 0, this.width, this.height, -1);
            this.panorama.render(partialTicks, MathHelper.clamp(buttonFadeIn, 0, 1));
            if (MellowConfigs.CLIENT_CONFIGS.mainMenuStyle.get() == MainMenuStyle.MELLOMEDLEY) return;

            int logoXPos = this.width / 2 - 137;
            this.minecraft.getTextureManager().bind(PANORAMA_OVERLAY);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.color4f(1, 1, 1, this.fading ? (float) MathHelper.ceil(MathHelper.clamp(buttonFadeIn, 0, 1)) : 1);
            blit(stack, 0, 0, this.width, this.height, 0, 0, 16, 128, 16, 128);
            float alpha = this.fading ? MathHelper.clamp(buttonFadeIn - 1, 0, 1) : 1;
            int textAlpha = MathHelper.ceil(alpha * 255) << 24;
            if ((textAlpha & 0xFC000000) != 0) {
                this.minecraft.getTextureManager().bind(MINECRAFT_LOGO);
                RenderSystem.color4f(1, 1, 1, alpha);
                if (this.minceraftEasterEgg) {
                    this.blitOutlineBlack(logoXPos, 30, (i1, i2) -> {
                        this.blit(stack, i1, i2, 0, 0, 99, 44);
                        this.blit(stack, i1 + 99, i2, 129, 0, 27, 44);
                        this.blit(stack, i1 + 99 + 26, i2, 126, 0, 3, 44);
                        this.blit(stack, i1 + 99 + 26 + 3, i2, 99, 0, 26, 44);
                        this.blit(stack, i1 + 155, i2, 0, 45, 155, 44);
                    });
                } else {
                    this.blitOutlineBlack(logoXPos, 30, (i1, i2) -> {
                        this.blit(stack, i1, i2, 0, 0, 155, 44);
                        this.blit(stack, i1 + 155, i2, 0, 45, 155, 44);
                    });
                }

                this.minecraft.getTextureManager().bind(MINECRAFT_EDITION);
                blit(stack, logoXPos + 88, 67, 0, 0, 98, 14, 128, 16);
                ForgeHooksClient.renderMainMenu((MainMenuScreen) this.minecraft.screen, stack, this.font, this.width, this.height, textAlpha);
                if (this.splash != null) {
                    RenderSystem.pushMatrix();
                    RenderSystem.translatef((float) (this.width / 2 + 90), 70, 0);
                    RenderSystem.rotatef(-20, 0, 0, 1);
                    float f2 = 1.8F - MathHelper.abs(MathHelper.sin((float) (Util.getMillis() % 1000L) / 1000 * ((float) Math.PI * 2F)) * 0.1F);
                    f2 = f2 * 100 / (float) (this.font.width(this.splash) + 32);
                    RenderSystem.scalef(f2, f2, f2);
                    drawCenteredString(stack, this.font, this.splash, 0, -8, MellowConfigs.CLIENT_CONFIGS.splashTextColor.get() | textAlpha);
                    RenderSystem.popMatrix();
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

                drawString(stack, this.font, new TranslationTextComponent("menu.minecraft.credits"), this.copyrightX, this.height - 10, 0xFFFFFF | textAlpha);
                if (mouseX > this.copyrightX && mouseX < this.copyrightX + this.copyrightWidth && mouseY > this.height - 10 && mouseY < this.height) {
                    fill(stack, this.copyrightX, this.height - 2, this.copyrightX + this.copyrightWidth, this.height - 1, 0xFFFFFF | textAlpha);
                    fill(stack, this.copyrightX + 1, this.height - 1, this.copyrightX + this.copyrightWidth + 1, this.height, 0x3F3F3F | textAlpha);
                }

                for (Widget widget : this.buttons) widget.setAlpha(alpha);

                super.render(stack, mouseX, mouseY, partialTicks);
                if (this.realmsNotificationsEnabled() && alpha >= 1) this.realmsNotificationsScreen.render(stack, mouseX, mouseY, partialTicks);
                this.modUpdateNotification.render(stack, mouseX, mouseY, partialTicks);
            }
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    public void mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> callback) {
        if (MellowConfigs.CLIENT_CONFIGS.mainMenuStyle.get() == MainMenuStyle.MELLOW_UI) {
            callback.cancel();
            if (super.mouseClicked(mouseX, mouseY, button)) {
                callback.setReturnValue(true);
            } else if (this.realmsNotificationsEnabled() && this.realmsNotificationsScreen.mouseClicked(mouseX, mouseY, button)) {
                callback.setReturnValue(true);
            } else {
                if (mouseX > (double) this.copyrightX && mouseX < (double) (this.copyrightX + this.copyrightWidth) && mouseY > (double) (this.height - 10) && mouseY < (double) this.height) {
                    this.minecraft.setScreen(new AttributionsScreen(this));
                    this.minecraft.getSoundManager().play(SimpleSound.forUI(SoundEvents.UI_BUTTON_CLICK, 1));
                }

                callback.setReturnValue(false);
            }
        }
    }
}
