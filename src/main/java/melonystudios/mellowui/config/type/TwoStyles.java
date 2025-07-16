package melonystudios.mellowui.config.type;

import net.minecraft.util.StringRepresentable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public enum TwoStyles implements StringRepresentable {
    OPTION_1(0, "option_1"),
    OPTION_2(1, "option_2");

    private final int id;
    private final String name;

    TwoStyles(int id, String name) {
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

    public static TwoStyles byId(int identifier) {
        return switch (identifier) {
            case 1 -> OPTION_2;
            default -> OPTION_1;
        };
    }
}
