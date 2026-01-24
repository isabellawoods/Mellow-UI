package melonystudios.mellowui.resource.posteffect;

import melonystudios.mellowui.util.ShaderManager;
import net.minecraft.resources.ResourceLocation;

/// Represents a single **post-processing effect** that can be rendered in the world or on the panorama.
/// @param assetID  A resource location of the post-processing effect's name.
/// @param uniforms A list of **uniforms** this shader requires to render.
public record PostEffect(ResourceLocation assetID, String... uniforms) {
    /// Represents a single **post-processing effect** that can be rendered in the world or on the panorama.
    ///
    /// @param assetID A resource location under the `minecraft` namespace of the post-processing effect's name.
    public PostEffect(String assetID) {
        this(new ResourceLocation(assetID));
    }

    /// Represents a single **post-processing effect** that can be rendered in the world or on the panorama.
    ///
    /// @param assetID  A resource location under the `minecraft` namespace of the post-processing effect's name.
    /// @param uniforms A list of **uniforms** this shader requires to render.
    public PostEffect(String assetID, String... uniforms) {
        this(new ResourceLocation(assetID), uniforms);
    }

    /// @return The asset id of this post-processing effect.
    @Override
    public ResourceLocation assetID() {
        return this.assetID;
    }

    /// @return An array of uniforms that this effect requires to render.
    @Override
    public String[] uniforms() {
        return this.uniforms;
    }

    /// @return Whether this post-processing effect is the default blur shader.
    public boolean isDefault() {
        return this.assetID().toString().equals(ShaderManager.MUI_BLUR_ID);
    }

    /// @return A resource location of this post-processing effect's associated file, at `<namespace>:shaders/post/<effect>.json`.
    public ResourceLocation getPostEffectFile() {
        return new ResourceLocation(this.assetID.getNamespace(), "shaders/post/" + this.assetID.getPath() + ".json");
    }
}
