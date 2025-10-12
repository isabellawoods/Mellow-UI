package melonystudios.mellowui.methods;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import melonystudios.mellowui.util.GUITextures;
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
        /// @return Whether this title screen keeps its logo visible during the fading animation.
        default boolean keepsLogoThroughFade() {
            return false;
        }

        /// Sets whether the title screen keeps its logo visible during the fading animation.
        /// @param keep Whether to keep it visible.
        default void keepLogoThroughFade(boolean keep) {}

        /// @return The panorama overlay texture from the title screen, as it may've been modified by other mods.
        default ResourceLocation getPanoramaOverlay() {
            return GUITextures.PANORAMA_OVERLAY;
        }
    }

    public interface PackRepositoryMethods {
        /// Adds a pack to the list of currently selected resource packs.
        /// @param id The id of the resource pack.
        default boolean addPack(String id) {
            return false;
        }

        /// Removes a pack from the list of currently selected resource packs.
        /// @param id The id of the resource pack.
        default boolean removePack(String id) {
            return false;
        }
    }

    public interface PostChainMethods {
        /// Sets the value of a provided uniform.
        /// @param name The uniform name.
        /// @param value The value of the uniform.
        default void setUniform(String name, float value) {}
    }

    public interface SoundEngineMethods {
        /// Reloads *Minecraft*'s {@linkplain net.minecraft.client.sounds.SoundEngine#reload() **sound engine**}.
        default void reloadSoundEngine() {}
    }

    public interface CubeMapMethods {
        /// @return A *nullable* array of {@linkplain ResourceLocation resource locations} representing all six panorama textures.
        @Nullable
        default ResourceLocation[] getPanoramaTextures() {
            return null;
        }

        /// Sets the textures of a cube map.
        /// @param textures A list of 6 {@linkplain ResourceLocation resource locations} to use.
        default void setPanoramaTextures(List<ResourceLocation> textures) {}
    }

    public interface PanoramaRendererMethods {
        /// @param renderer The panorama to compare.
        /// @return Whether the provided panorama is different from the panorama currently being rendered.
        default boolean differentPanorama(PanoramaRenderer renderer) {
            return false;
        }

        /// Gets the {@linkplain CubeMap cube map} used by the panorama renderer.
        default CubeMap cubeMap() {
            return TitleScreen.CUBE_MAP;
        }
    }

    public interface MusicManagerMethods {
        /// A *nullable* {@linkplain SoundInstance sound instance} of the music currently being played.
        @Nullable
        default SoundInstance mui$getNowPlaying() {
            return null;
        }
    }

    public interface WorldPresetsMethods {
        /// @return A list of all registered world presets.
        default List<WorldPreset> getPresets() {
            return Lists.newArrayList();
        }

        /// @return A map of all registered world presets and their editors.
        default Map<Optional<WorldPreset>, WorldPreset.PresetEditor> getEditors() {
            return Maps.newHashMap();
        }
    }
}
