package melonystudios.mellowui.config.value;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;

public class ValueEntries {
    public static ValueEntry<?> fromForgeConfig(ForgeConfigSpec.ConfigValue<?> config, ResourceLocation id) {
        Object value = config.get();
        if (config instanceof ForgeConfigSpec.BooleanValue && value instanceof Boolean) {
            return new ValueEntry<>((Boolean) value, ValueType.BOOLEAN, id);
        } else if (config instanceof ForgeConfigSpec.IntValue && value instanceof Integer) {
            return new ValueEntry<>((Integer) value, ValueType.INTEGER, id);
        } else if (config instanceof ForgeConfigSpec.LongValue && value instanceof Long) {
            return new ValueEntry<>((Long) value, ValueType.LONG, id);
        } else if (config instanceof ForgeConfigSpec.DoubleValue && value instanceof Double) {
            return new ValueEntry<>((Double) value, ValueType.DOUBLE, id);
        } else if (value instanceof String) {
            return new ValueEntry<>((String) value, ValueType.STRING, id);
        }
        return null;
    }
}
