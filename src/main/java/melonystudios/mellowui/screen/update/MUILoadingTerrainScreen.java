package melonystudios.mellowui.screen.update;

import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.util.CompatUtils;
import melonystudios.mellowui.util.MellowUtils;
import melonystudios.mellowui.util.shader.ShaderManager;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.function.BooleanSupplier;

@OnlyIn(Dist.CLIENT)
public class MUILoadingTerrainScreen extends Screen {
    public static final ITextComponent DOWNLOADING_TERRAIN_TEXT = new TranslationTextComponent("multiplayer.downloadingTerrain");
    public static final long CHUNK_LOADING_START_WAIT_LIMIT_MS = 30000L;
    private final long createdAt;
    private final BooleanSupplier worldReceived;
    private final MUILoadingTerrainScreen.Reason reason;
    @Nullable
    private TextureAtlasSprite cachedNetherPortalSprite;
    @Nullable
    private TextureAtlasSprite cachedEverbrightPortalSprite;
    @Nullable
    private TextureAtlasSprite cachedEverdawnPortalSprite;

    public MUILoadingTerrainScreen(BooleanSupplier levelReceived, Reason reason) {
        super(NarratorChatListener.NO_TITLE);
        this.worldReceived = levelReceived;
        this.reason = reason;
        this.createdAt = System.currentTimeMillis();
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack, 0);
        super.render(stack, mouseX, mouseY, partialTicks);
        drawCenteredString(stack, this.font, DOWNLOADING_TERRAIN_TEXT, this.width / 2, this.height / 2 - 50, 0xFFFFFF);
    }

    @Override
    public void renderBackground(MatrixStack stack, int vOffset) {
        if (this.minecraft == null) return;
        float partialTicks = this.minecraft.getDeltaFrameTime();
        switch (this.reason) {
            case NETHER_PORTAL:
                this.minecraft.getTextureManager().bind(PlayerContainer.BLOCK_ATLAS);
                blit(stack, 0, 0, -90, this.width, this.height, this.getNetherPortalSprite());
                MellowUtils.renderBackgroundWithShaders(partialTicks);
                break;
            case END_PORTAL:
                MellowUtils.renderPanorama(stack, partialTicks, this.width, this.height, 1); // in case the aljanstone texture is transparent ~isa 20-6-25
                ShaderManager.fillEndPortal(stack, 0, 0, this.width, this.height, -90);
                MellowUtils.renderBackgroundWithShaders(partialTicks);
                break;
            case ALJAN_PORTAL_STAND: // From Back Math
                MellowUtils.renderTiledBackground(stack, new ResourceLocation("backmath", "textures/block/aljanstone.png"), 64, this.width, this.height, vOffset);
                MellowUtils.renderBackgroundWithShaders(partialTicks);
                break;
            case EVERBRIGHT_PORTAL: // From Blue Skies
                this.minecraft.getTextureManager().bind(PlayerContainer.BLOCK_ATLAS);
                blit(stack, 0, 0, -90, this.width, this.height, this.getEverbrightPortalSprite());
                MellowUtils.renderBackgroundWithShaders(partialTicks);
                break;
            case EVERDAWN_PORTAL: // From Blue Skies
                this.minecraft.getTextureManager().bind(PlayerContainer.BLOCK_ATLAS);
                blit(stack, 0, 0, -90, this.width, this.height, this.getEverdawnPortalSprite());
                MellowUtils.renderBackgroundWithShaders(partialTicks);
                break;
            case OTHER:
            default:
                MellowUtils.renderPanorama(stack, partialTicks, this.width, this.height, 1);
                MellowUtils.renderBlurredBackground(partialTicks);
                this.renderDirtBackground(vOffset);
        }
    }

    private TextureAtlasSprite getNetherPortalSprite() {
        if (this.cachedNetherPortalSprite == null) {
            this.cachedNetherPortalSprite = this.minecraft.getBlockRenderer().getBlockModelShaper().getParticleIcon(Blocks.NETHER_PORTAL.defaultBlockState());
        }
        return this.cachedNetherPortalSprite;
    }

    private TextureAtlasSprite getEverbrightPortalSprite() {
        if (CompatUtils.EVERBRIGHT_PORTAL == null) return null;
        if (this.cachedEverbrightPortalSprite == null) {
            this.cachedEverbrightPortalSprite = this.minecraft.getBlockRenderer().getBlockModelShaper().getParticleIcon(CompatUtils.EVERBRIGHT_PORTAL.defaultBlockState());
        }
        return this.cachedEverbrightPortalSprite;
    }

    private TextureAtlasSprite getEverdawnPortalSprite() {
        if (CompatUtils.EVERDAWN_PORTAL == null) return null;
        if (this.cachedEverdawnPortalSprite == null) {
            this.cachedEverdawnPortalSprite = this.minecraft.getBlockRenderer().getBlockModelShaper().getParticleIcon(CompatUtils.EVERDAWN_PORTAL.defaultBlockState());
        }
        return this.cachedEverdawnPortalSprite;
    }

    @Override
    public void tick() {
        if (this.worldReceived.getAsBoolean() || System.currentTimeMillis() > this.createdAt + CHUNK_LOADING_START_WAIT_LIMIT_MS) {
            this.onClose();
        }
    }

    @Override
    public void onClose() {
        NarratorChatListener.INSTANCE.sayNow(new TranslationTextComponent("narrator.ready_to_play").getString());
        super.onClose();
    }

    @OnlyIn(Dist.CLIENT)
    public enum Reason {
        NETHER_PORTAL,
        END_PORTAL,
        ALJAN_PORTAL_STAND,
        EVERBRIGHT_PORTAL,
        EVERDAWN_PORTAL,
        OTHER
    }
}
