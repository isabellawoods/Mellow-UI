package melonystudios.mellowui.util;

import net.minecraft.util.IStringSerializable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public enum ModListSorting implements IStringSerializable {
    NONE(0, "none"),
    A_TO_Z(1, "a_to_z"),
    Z_TO_A(2, "z_to_a");

    private final int id;
    private final String name;

    ModListSorting(int id,String name) {
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

    public static ModListSorting byId(int identifier) {
        switch (identifier) {
            case 1: return A_TO_Z;
            case 2: return Z_TO_A;
            case 0: default: return NONE;
        }
    }
}
