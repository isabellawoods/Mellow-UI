package melonystudios.mellowui.util.shader;

import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Optional;

public class PostEffect {
    private final ResourceLocation assetID;
    private final int shaderID;
    @Nullable
    private final String[] uniforms;

    public PostEffect(String assetID, int shaderID) {
        this(new ResourceLocation(assetID), shaderID);
    }

    public PostEffect(String assetID, int shaderID, @Nullable String... uniforms) {
        this(new ResourceLocation(assetID), shaderID, uniforms);
    }

    public PostEffect(ResourceLocation assetID, int shaderID, @Nullable String... uniforms) {
        this.assetID = assetID;
        this.shaderID = shaderID;
        this.uniforms = uniforms;
    }

    public ResourceLocation assetID() {
        return this.assetID;
    }

    public int shaderIdentifier() {
        return this.shaderID;
    }

    public Optional<String[]> uniforms() {
        return Optional.ofNullable(this.uniforms);
    }

    public ResourceLocation getPostEffectFile() {
        return new ResourceLocation(this.assetID.getNamespace(), "shaders/post/" + this.assetID.getPath() + ".json");
    }
}
