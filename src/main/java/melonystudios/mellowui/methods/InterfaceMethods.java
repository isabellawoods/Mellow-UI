package melonystudios.mellowui.methods;

import net.minecraft.client.audio.ISound;
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

public class InterfaceMethods {
    @OnlyIn(Dist.CLIENT)
    public enum DeviceCheckState {
        ONGOING,
        CHANGE_DETECTED,
        NO_CHANGE
    }

    public interface MainMenuMethods {
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

        default List<String> getAvailableSoundDevices() {
            List<String> devices = ALUtil.getStringList(0L, 4115);
            return devices == null ? Collections.emptyList() : devices;
        }
    }

    public interface SoundSystemMethods {
        default boolean isCurrentDeviceDisconnected() {
            return false;
        }

        default boolean hasDefaultDeviceChanged() {
            return false;
        }

        default String getCurrentDeviceName() {
            return "";
        }
    }

    public interface CubeMapMethods {
        @Nullable
        default ResourceLocation[] getPanoramaTextures() {
            return null;
        }
    }

    public interface PanoramaRendererMethods {
        default boolean samePanorama(RenderSkybox panoramaRenderer) {
            return false;
        }

        default RenderSkyboxCube cubeMap() {
            return MainMenuScreen.CUBE_MAP;
        }
    }

    public interface MusicManagerMethods {
        @Nullable
        default ISound mui$getNowPlaying() {
            return null;
        }
    }
}
