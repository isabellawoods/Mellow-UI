package melonystudios.mellowui.mixin.forge;

import melonystudios.mellowui.methods.InterfaceMethods;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.config.ModConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Collection;
import java.util.EnumMap;

@OnlyIn(Dist.CLIENT)
@Mixin(value = ModContainer.class, remap = false)
public class MUIModContainerMixin implements InterfaceMethods.ModContainerMethods {
    @Shadow
    @Final
    protected EnumMap<ModConfig.Type, ModConfig> configs;

    @Override
    public Collection<ModConfig> getModConfigs() {
        return this.configs.values();
    }
}
