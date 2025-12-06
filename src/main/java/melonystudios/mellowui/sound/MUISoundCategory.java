package melonystudios.mellowui.sound;

import net.minecraft.util.SoundCategory;

public enum MUISoundCategory {
    MASTER, MUSIC, RECORDS, WEATHER, BLOCKS, HOSTILE, NEUTRAL, PLAYERS, AMBIENT, VOICE, UI;

    public static MUISoundCategory toMUI(SoundCategory source) {
        switch (source) {
            case MUSIC: return MUSIC;
            case RECORDS: return RECORDS;
            case WEATHER: return WEATHER;
            case BLOCKS: return BLOCKS;
            case HOSTILE: return HOSTILE;
            case NEUTRAL: return NEUTRAL;
            case PLAYERS: return PLAYERS;
            case AMBIENT: return AMBIENT;
            case VOICE: return VOICE;
            default: return MASTER;
        }
    }
}
