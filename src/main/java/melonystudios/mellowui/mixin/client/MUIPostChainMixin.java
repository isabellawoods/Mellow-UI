package melonystudios.mellowui.mixin.client;

import melonystudios.mellowui.methods.InterfaceMethods;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(PostChain.class)
public class MUIPostChainMixin implements InterfaceMethods.PostChainMethods {
    @Shadow
    @Final
    private List<PostPass> passes;

    public void setUniform(String name, float value) {
        for (PostPass shader : this.passes) shader.getEffect().safeGetUniform(name).set(value);
    }
}
