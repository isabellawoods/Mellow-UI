package melonystudios.mellowui.screen.update;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import melonystudios.mellowui.util.CompatUtils;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.EndPortalTileEntityRenderer;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
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
                // this.fillRenderType(stack, RenderType.endPortal(2), 0, 0, this.width, this.height, 0);
                this.minecraft.getTextureManager().bind(EndPortalTileEntityRenderer.END_PORTAL_LOCATION);
                blit(stack, 0, 0, 0, 0, this.width, this.height, this.width, this.height);
                MellowUtils.renderBackgroundWithShaders(partialTicks);
                break;
            case ALJAN_PORTAL_STAND: // From Back Math
                RenderSystem.color4f(0.25F, 0.25F, 0.25F, 1);
                this.minecraft.getTextureManager().bind(new ResourceLocation("backmath", "textures/block/aljanstone.png"));
                blit(stack, 0, 0, 0, 0, this.width, this.height, 32, 32);
                RenderSystem.color4f(1, 1, 1, 1);
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

    // this currently doesn't work because shader rendering in 1.16 is hard ~isa 27-4-25
    private void fillRenderType(MatrixStack stack, RenderType renderType, int x0, int y0, int x1, int y1, int z) {
        IRenderTypeBuffer buffer = this.minecraft.renderBuffers().bufferSource();
        IVertexBuilder builder = buffer.getBuffer(renderType);
        Matrix4f matrix4F = stack.last().pose();
        builder.vertex(matrix4F, (float) x0, (float) y0, (float) z).color(1, 1, 1, 1F).uv(x0, y0).endVertex();
        builder.vertex(matrix4F, (float) x0, (float) y1, (float) z).color(1, 1, 1, 1F).uv(x0, y1).endVertex();
        builder.vertex(matrix4F, (float) x1, (float) y1, (float) z).color(1, 1, 1, 1F).uv(x1, y1).endVertex();
        builder.vertex(matrix4F, (float) x1, (float) y0, (float) z).color(1, 1, 1, 1F).uv(x1, y0).endVertex();
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
