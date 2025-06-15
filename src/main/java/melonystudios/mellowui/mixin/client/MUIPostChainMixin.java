package melonystudios.mellowui.mixin.client;

import melonystudios.mellowui.methods.InterfaceMethods;
import net.minecraft.client.shader.Shader;
import net.minecraft.client.shader.ShaderGroup;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(ShaderGroup.class)
public class MUIPostChainMixin implements InterfaceMethods.PostChainMethods {
    @Shadow
    @Final
    private List<Shader> passes;

    public void setUniform(String name, float value) {
        for (Shader shader : this.passes) shader.getEffect().safeGetUniform(name).set(value);
    }
}
