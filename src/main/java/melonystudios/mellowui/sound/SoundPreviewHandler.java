package melonystudios.mellowui.sound;

import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

import javax.annotation.Nullable;

public class SoundPreviewHandler {
    @Nullable
    private static SoundInstance activePreview;
    @Nullable
    private static MUISoundSource previousCategory;

    public static void preview(SoundManager manager, MUISoundSource category, float volume) {
        stopOtherCategoryPreview(manager, category);
        if (canPlaySound(manager)) {
            SoundEvent event = switch (category) {
                case RECORDS -> SoundEvents.NOTE_BLOCK_GUITAR;
                case WEATHER -> SoundEvents.LIGHTNING_BOLT_THUNDER;
                case BLOCKS -> SoundEvents.GRASS_PLACE;
                case HOSTILE -> SoundEvents.ZOMBIE_AMBIENT;
                case NEUTRAL -> SoundEvents.COW_AMBIENT;
                case PLAYERS -> SoundEvents.GENERIC_EAT;
                case AMBIENT -> SoundEvents.AMBIENT_CAVE;
                case UI -> SoundEvents.UI_BUTTON_CLICK;
                default -> null;
            };
            if (event != null) {
                activePreview = SimpleSoundInstance.forUI(event, 1, volume);
                manager.play(activePreview);
            }
        }
    }

    private static void stopOtherCategoryPreview(SoundManager manager, MUISoundSource soundSource) {
        if (previousCategory != soundSource) {
            previousCategory = soundSource;
            if (activePreview != null) {
                manager.stop(activePreview);
            }
        }
    }

    private static boolean canPlaySound(SoundManager manager) {
        return activePreview == null || !manager.isActive(activePreview);
    }
}
