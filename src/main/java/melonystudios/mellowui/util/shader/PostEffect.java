package melonystudios.mellowui.util.shader;

import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Optional;

/// Represents a single **post effect** that can be rendered in the world or on the panorama.
public class PostEffect {
    private final ResourceLocation assetID;
    private final int shaderID;
    @Nullable
    private final String[] uniforms;

    /// Represents a single **post effect** that can be rendered in the world or on the panorama.
    /// @param assetID A resource location under the `minecraft` namespace of the post effect's name;
    /// @param shaderID The post effect's unique identifier.
    public PostEffect(String assetID, int shaderID) {
        this(new ResourceLocation(assetID), shaderID);
    }

    /// Represents a single **post effect** that can be rendered in the world or on the panorama.
    /// @param assetID A resource location under the `minecraft` namespace of the post effect's name;
    /// @param shaderID The post effect's unique identifier.
    /// @param uniforms A list of **uniforms** this shader requires to render.
    public PostEffect(String assetID, int shaderID, @Nullable String... uniforms) {
        this(new ResourceLocation(assetID), shaderID, uniforms);
    }

    /// Represents a single **post effect** that can be rendered in the world or on the panorama.
    /// @param assetID A resource location of the post effect's name;
    /// @param shaderID The post effect's unique identifier.
    /// @param uniforms A list of **uniforms** this shader requires to render.
    public PostEffect(ResourceLocation assetID, int shaderID, @Nullable String... uniforms) {
        this.assetID = assetID;
        this.shaderID = shaderID;
        this.uniforms = uniforms;
    }

    /// @return The asset id of this post effect.
    public ResourceLocation assetID() {
        return this.assetID;
    }

    /// @return The unique identifier of this post effect.
    public int shaderIdentifier() {
        return this.shaderID;
    }

    /// @return An {@linkplain Optional optional} array of uniforms that this shader requires to render.
    public Optional<String[]> uniforms() {
        return Optional.ofNullable(this.uniforms);
    }

    /// @return A resource location of this post effect's associated file, at `<namespace>:shaders/post/<effect>.json`.
    public ResourceLocation getPostEffectFile() {
        return new ResourceLocation(this.assetID.getNamespace(), "shaders/post/" + this.assetID.getPath() + ".json");
    }
}
