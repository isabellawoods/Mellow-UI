package melonystudios.mellowui.mixin;

import melonystudios.mellowui.methods.InterfaceMethods;
import net.minecraft.client.gui.screen.BiomeGeneratorTypeScreens;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mixin(BiomeGeneratorTypeScreens.class)
public class MUIWorldPresetsMixin implements InterfaceMethods.WorldPresetsMethods {
    @Shadow
    @Final
    protected static List<BiomeGeneratorTypeScreens> PRESETS;
    @Shadow
    @Final
    protected static Map<Optional<BiomeGeneratorTypeScreens>, BiomeGeneratorTypeScreens.IFactory> EDITORS;

    @Override
    public List<BiomeGeneratorTypeScreens> getPresets() {
        return PRESETS;
    }

    @Override
    public Map<Optional<BiomeGeneratorTypeScreens>, BiomeGeneratorTypeScreens.IFactory> getEditors() {
        return EDITORS;
    }
}
