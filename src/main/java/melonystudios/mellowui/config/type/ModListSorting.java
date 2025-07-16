package melonystudios.mellowui.config.type;

import net.minecraft.util.StringRepresentable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.StringUtils;
import net.minecraftforge.forgespi.language.IModInfo;

import javax.annotation.Nonnull;
import java.util.Comparator;

@OnlyIn(Dist.CLIENT)
public enum ModListSorting implements StringRepresentable, Comparator<IModInfo> {
    NONE(0, "none"),
    A_TO_Z(1, "a_to_z") {
        @Override
        protected int compareName(String name1, String name2) {
            return name1.compareTo(name2);
        }
    },
    Z_TO_A(2, "z_to_a") {
        @Override
        protected int compareName(String name1, String name2) {
            return name2.compareTo(name1);
        }
    };

    private final int id;
    private final String name;

    ModListSorting(int id, String name) {
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

    protected int compareName(String name1, String name2) {
        return 0;
    }

    @Override
    public int compare(IModInfo mod1, IModInfo mod2) {
        String name1 = StringUtils.toLowerCase(mod1.getDisplayName());
        String name2 = StringUtils.toLowerCase(mod2.getDisplayName());
        return this.compareName(name1, name2);
    }

    public static ModListSorting byId(int identifier) {
        return switch (identifier) {
            case 1 -> A_TO_Z;
            case 2 -> Z_TO_A;
            default -> NONE;
        };
    }
}
