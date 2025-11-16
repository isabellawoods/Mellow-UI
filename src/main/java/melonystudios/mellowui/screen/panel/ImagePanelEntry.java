package melonystudios.mellowui.screen.panel;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import melonystudios.mellowui.renderer.LogoRenderer;
import melonystudios.mellowui.screen.RenderComponents;
import melonystudios.mellowui.screen.update.MellowModListScreen;
import melonystudios.mellowui.util.text.TextComponents;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.GuiUtils;
import net.minecraftforge.common.util.Size2i;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.resource.PathResourcePack;
import net.minecraftforge.resource.ResourcePackLoader;

import java.io.IOException;
import java.io.InputStream;

/// Represents a **panel entry** that renders the logo of a mod.
public class ImagePanelEntry extends PanelEntry {
    private final IModInfo mod;

    /// Represents a **panel entry** that renders the logo of a mod.
    /// @param panel The parent panel.
    /// @param mod The {@linkplain IModInfo information about the mod}.
    public ImagePanelEntry(Panel panel, IModInfo mod) {
        super(panel);
        this.mod = mod;
    }

    @Override
    public void renderEntry(PoseStack stack, RenderComponents components, int x, int y, int width, int height) {
        if (this.panel.parentScreen instanceof MellowModListScreen screen) {
            Pair<ResourceLocation, Size2i> logoData = screen.isLogoLoaded() ? screen.getLogoData() : this.loadModLogo();

            // Rendering
            ResourceLocation location = logoData.getFirst();
            Size2i dimensions = logoData.getSecond();
            int logoWidth = dimensions.width;
            int logoHeight = dimensions.height;

            RenderSystem.enableBlend();
            RenderSystem.setShaderTexture(0, location);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            GuiUtils.drawInscribedRect(stack, x, y, x * 2 - 15, 50, logoWidth, logoHeight, true, false);
            RenderSystem.disableBlend();

            screen.setModLogo(logoData);
        }
        super.renderEntry(stack, components, x, y, width, height);
    }

    /// Gets the logo for a specified mod.
    /// @return A {@link Pair} containing the location of the logo texture, and its {@linkplain Size2i size},
    /// or the updated *Minecraft* logo if the mod id is `minecraft`.
    public Pair<ResourceLocation, Size2i> loadModLogo() {
        return this.mod.getLogoFile().map(fileName -> {
            TextureManager manager = this.panel.getMinecraft().getTextureManager();
            PathResourcePack resourcePack = ResourcePackLoader.getPackFor(this.mod.getModId())
                    .orElse(ResourcePackLoader.getPackFor("forge")
                            .orElseThrow(() -> new RuntimeException(TextComponents.translate("error.mellowui.cannot_find_forge", "Failed to find Forge, WHAT!"))));

            if (this.mod.getModId().equals("minecraft")) {
                return Pair.of(LogoRenderer.MINECRAFT_LOGO, new Size2i(1024, 256));
            }

            try {
                InputStream logoFile = resourcePack.getRootResource(fileName);
                NativeImage logo = NativeImage.read(logoFile);

                return Pair.of(manager.register("logo_" + this.mod.getModId(), new DynamicTexture(logo) {
                    @Override
                    public void upload() {
                        this.bind();
                        NativeImage pixels = this.getPixels();
                        if (pixels != null) {
                            pixels.upload(0, 0, 0, 0, 0, pixels.getWidth(), pixels.getHeight(), ImagePanelEntry.this.mod.getLogoBlur(), false, false, false);
                        }
                    }
                }), new Size2i(logo.getWidth(), logo.getHeight()));
            } catch (IOException ignored) {}
            return Pair.<ResourceLocation, Size2i>of(null, new Size2i(0, 0));
        }).orElse(Pair.of(null, new Size2i(0, 0)));
    }

    @Override
    public int getContentHeight() {
        return 50;
    }
}
