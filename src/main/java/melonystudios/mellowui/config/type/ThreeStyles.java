package melonystudios.mellowui.config.type;

import net.minecraft.util.IStringSerializable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public enum ThreeStyles implements IStringSerializable {
    OPTION_1(0, "option_1"),
    OPTION_2(1, "option_2"),
    OPTION_3(2, "option_3");

    private final int id;
    private final String name;

    ThreeStyles(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public int getId() {
        return this.id;
    }

    @Override
    @Nonnull
    public String getSerializedName() {
        return this.name;
    }

    public static ThreeStyles byId(int identifier) {
        switch (identifier) {
            case 1: return OPTION_2;
            case 2: return OPTION_3;
            case 0: default: return OPTION_1;
        }
    }
}
