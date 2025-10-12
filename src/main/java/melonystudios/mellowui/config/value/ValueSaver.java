package melonystudios.mellowui.config.value;

import net.minecraftforge.common.ForgeConfigSpec;

public abstract class ValueSaver<T> {
    public abstract void saveValue(T value);

    public static class ForgeConfigSaver<T> extends ValueSaver<T> {
        private final ForgeConfigSpec.ConfigValue<T> config;

        public ForgeConfigSaver(ForgeConfigSpec.ConfigValue<T> config) {
            this.config = config;
        }

        @Override
        public void saveValue(T value) {
            this.config.set(value);
        }
    }
}
