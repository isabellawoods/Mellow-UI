package melonystudios.mellowui.sound;

import net.minecraft.sounds.SoundSource;

public enum MUISoundSource {
    MASTER, MUSIC, RECORDS, WEATHER, BLOCKS, HOSTILE, NEUTRAL, PLAYERS, AMBIENT, VOICE, UI;

    public static MUISoundSource toMUI(SoundSource source) {
        return switch (source) {
            case MUSIC -> MUSIC;
            case RECORDS -> RECORDS;
            case WEATHER -> WEATHER;
            case BLOCKS -> BLOCKS;
            case HOSTILE -> HOSTILE;
            case NEUTRAL -> NEUTRAL;
            case PLAYERS -> PLAYERS;
            case AMBIENT -> AMBIENT;
            case VOICE -> VOICE;
            default -> MASTER;
        };
    }
}
