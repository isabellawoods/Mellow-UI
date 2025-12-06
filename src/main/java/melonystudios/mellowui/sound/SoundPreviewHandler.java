package melonystudios.mellowui.sound;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;

import javax.annotation.Nullable;

public class SoundPreviewHandler {
    @Nullable
    private static ISound activePreview;
    @Nullable
    private static MUISoundCategory previousCategory;

    public static void preview(SoundHandler handler, MUISoundCategory category, float volume) {
        stopOtherCategoryPreview(handler, category);
        if (canPlaySound(handler)) {
            SoundEvent event;
            switch (category) {
                case RECORDS:
                    event = SoundEvents.NOTE_BLOCK_GUITAR;
                    break;
                case WEATHER:
                    event = SoundEvents.LIGHTNING_BOLT_THUNDER;
                    break;
                case BLOCKS:
                    event = SoundEvents.GRASS_PLACE;
                    break;
                case HOSTILE:
                    event = SoundEvents.ZOMBIE_AMBIENT;
                    break;
                case NEUTRAL:
                    event = SoundEvents.COW_AMBIENT;
                    break;
                case PLAYERS:
                    event = SoundEvents.GENERIC_EAT;
                    break;
                case AMBIENT:
                    event = SoundEvents.AMBIENT_CAVE;
                    break;
                case UI:
                    event = SoundEvents.UI_BUTTON_CLICK;
                    break;
                default: event = null;
            }
            if (event != null) {
                activePreview = SimpleSound.forUI(event, 1, volume);
                handler.play(activePreview);
            }
        }
    }

    private static void stopOtherCategoryPreview(SoundHandler handler, MUISoundCategory soundSource) {
        if (previousCategory != soundSource) {
            previousCategory = soundSource;
            if (activePreview != null) {
                handler.stop(activePreview);
            }
        }
    }

    private static boolean canPlaySound(SoundHandler handler) {
        return activePreview == null || !handler.isActive(activePreview);
    }
}
