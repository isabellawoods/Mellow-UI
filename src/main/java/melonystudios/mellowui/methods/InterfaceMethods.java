package melonystudios.mellowui.methods;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import melonystudios.mellowui.util.GUITextures;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.gui.screen.BiomeGeneratorTypeScreens;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.renderer.RenderSkybox;
import net.minecraft.client.renderer.RenderSkyboxCube;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.openal.ALUtil;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InterfaceMethods {
    @OnlyIn(Dist.CLIENT)
    public enum DeviceCheckState {
        ONGOING,
        CHANGE_DETECTED,
        NO_CHANGE
    }

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
        /// Reloads *Minecraft*'s {@linkplain net.minecraft.client.audio.SoundEngine#reload **sound engine**}.
        default void reloadSoundEngine() {}

        /// @return A list of all available sound devices to use.
        default List<String> getAvailableSoundDevices() {
            List<String> devices = ALUtil.getStringList(0L, 4115);
            return devices == null ? Collections.emptyList() : devices;
        }
    }

    public interface SoundSystemMethods {
        /// @return Whether the current audio device is disconnected
        default boolean isCurrentDeviceDisconnected() {
            return false;
        }

        /// @return Whether the default audio device has changed.
        default boolean hasDefaultDeviceChanged() {
            return false;
        }

        /// @return The name of the current audio device.
        default String getCurrentDeviceName() {
            return "";
        }
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
        /// Whether the provided panorama is different from the panorama currently being rendered.
        default boolean differentPanorama(RenderSkybox renderer) {
            return false;
        }

        /// Gets the {@linkplain RenderSkyboxCube cube map} used by the panorama renderer.
        default RenderSkyboxCube cubeMap() {
            return MainMenuScreen.CUBE_MAP;
        }
    }

    public interface MusicManagerMethods {
        /// A *nullable* {@linkplain ISound sound instance} of the music currently being played.
        @Nullable
        default ISound mui$getNowPlaying() {
            return null;
        }
    }

    public interface WorldPresetsMethods {
        /// @return A list of all registered world types.
        default List<BiomeGeneratorTypeScreens> getPresets() {
            return Lists.newArrayList();
        }

        /// @return A map of all registered world types and their screen factories.
        default Map<Optional<BiomeGeneratorTypeScreens>, BiomeGeneratorTypeScreens.IFactory> getEditors() {
            return Maps.newHashMap();
        }
    }
}
