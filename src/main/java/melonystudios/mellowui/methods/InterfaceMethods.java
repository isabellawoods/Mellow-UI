package melonystudios.mellowui.methods;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.worldselection.WorldPreset;
import net.minecraft.client.renderer.CubeMap;
import net.minecraft.client.renderer.PanoramaRenderer;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InterfaceMethods {
    public interface TitleScreenMethods {
        default boolean keepsLogoThroughFade() {
            return false;
        }

        default void keepLogoThroughFade(boolean keep) {}
    }

    public interface PackRepositoryMethods {
        default boolean addPack(String id) {
            return false;
        }

        default boolean removePack(String id) {
            return false;
        }
    }

    public interface PostChainMethods {
        default void setUniform(String name, float value) {}
    }

    public interface SoundEngineMethods {
        default void reloadSoundEngine() {}
    }

    public interface CubeMapMethods {
        @Nullable
        default ResourceLocation[] getPanoramaTextures() {
            return null;
        }
    }

    public interface PanoramaRendererMethods {
        default boolean samePanorama(PanoramaRenderer panoramaRenderer) {
            return false;
        }

        default CubeMap cubeMap() {
            return TitleScreen.CUBE_MAP;
        }
    }

    public interface MusicManagerMethods {
        @Nullable
        default SoundInstance mui$getNowPlaying() {
            return null;
        }
    }

    public interface WorldPresetsMethods {
        default List<WorldPreset> getPresets() {
            return Lists.newArrayList();
        }

        default Map<Optional<WorldPreset>, WorldPreset.PresetEditor> getEditors() {
            return Maps.newHashMap();
        }
    }
}
