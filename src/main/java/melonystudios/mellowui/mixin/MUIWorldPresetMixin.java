package melonystudios.mellowui.mixin;

import melonystudios.mellowui.methods.InterfaceMethods;
import net.minecraft.client.gui.screens.worldselection.WorldPreset;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mixin(WorldPreset.class)
public class MUIWorldPresetMixin implements InterfaceMethods.WorldPresetsMethods {
    @Shadow
    @Final
    protected static List<WorldPreset> PRESETS;
    @Shadow
    @Final
    protected static Map<Optional<WorldPreset>, WorldPreset.PresetEditor> EDITORS;

    @Override
    public List<WorldPreset> getPresets() {
        return PRESETS;
    }

    @Override
    public Map<Optional<WorldPreset>, WorldPreset.PresetEditor> getEditors() {
        return EDITORS;
    }
}
