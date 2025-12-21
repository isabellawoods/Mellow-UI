package melonystudios.mellowui.resource.theme;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.google.gson.JsonObject;
import melonystudios.mellowui.methods.InterfaceMethods;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.config.ModConfig;

import java.util.List;
import java.util.stream.Collectors;

public class ForgeConfigOverride {
    private final ResourceLocation configFile;
    private final ConfigOverrideEntry entry;

    public ForgeConfigOverride(ResourceLocation configFile) {
        this.configFile = configFile;
        this.entry = this.getConfigEntry();
    }

    private ConfigOverrideEntry getConfigEntry() {
        List<? extends ModContainer> containers = ModList.get().getMods().stream()
                .filter(info -> info.getModId().equals(this.configFile.getNamespace()))
                .map(info -> ModList.get().getModContainerById(info.getModId()).orElse(null))
                .collect(Collectors.toList());

        for (ModContainer container : containers) {
            if (container !=  null) return ((InterfaceMethods.ModContainerMethods) container).getModConfigs().stream()
                    .filter(config -> {
                        // MellowUI.LOGGER.debug(MARKER, "[{}] {} config file name is {}", config.getModId(), config.getType(), config.getFileName());
                        return config.getFileName().contains(this.configFile.getPath());
                    })
                    .map(ConfigOverrideEntry::new)
                    .findFirst().orElse(null);
        }
        return null;
    }

    public ResourceLocation configFile() {
        return this.configFile;
    }

    public ConfigOverrideEntry overrideEntry() {
        return this.entry;
    }

    public void toJSON(JsonObject object) {
        object.add(this.configFile().toString(), this.overrideEntry().toJSON());
    }

    public void fromJSON(JsonObject object) {
        this.overrideEntry().fromJSON(object);
    }

    public static class ConfigOverrideEntry {
        private final ModConfig config;

        public ConfigOverrideEntry(ModConfig config) {
            this.config = config;
        }

        public JsonObject toJSON() {
            JsonObject object = new JsonObject();
            for (CommentedConfig.Entry entry : this.config.getConfigData().entrySet()) {
                Object value = entry.getRawValue();

                if (value instanceof Number) object.addProperty(entry.getKey(), (Number) entry.getRawValue());
                else object.addProperty(entry.getKey(), (String) entry.getRawValue());
            }
            return object;
        }

        public ConfigOverrideEntry fromJSON(JsonObject object) {
            return this;
        }
    }
}
