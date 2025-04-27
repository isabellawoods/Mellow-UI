package melonystudios.mellowui.config.type;

import net.minecraft.util.IStringSerializable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public enum MainMenuStyle implements IStringSerializable {
    VANILLA(0, "vanilla"),
    MELLOW_UI(1, "mellowui"),
    MELLOMEDLEY(2, "mellomedley");

    private final int id;
    private final String name;

    MainMenuStyle(int id, String name) {
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

    public static MainMenuStyle byId(int identifier) {
        switch (identifier) {
            case 1: return MELLOW_UI;
            case 2: return MELLOMEDLEY;
            case 0: default: return VANILLA;
        }
    }
}
