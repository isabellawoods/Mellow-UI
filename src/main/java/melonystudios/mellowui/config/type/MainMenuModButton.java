package melonystudios.mellowui.config.type;

import net.minecraft.util.IStringSerializable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public enum MainMenuModButton implements IStringSerializable {
    ADJACENT(0, "adjacent"),
    ICON(1, "icon"),
    REPLACE_REALMS(2, "replace_realms");

    private final int id;
    private final String name;

    MainMenuModButton(int id, String name) {
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

    public static MainMenuModButton byId(int identifier) {
        switch (identifier) {
            case 1: return ICON;
            case 2: return REPLACE_REALMS;
            case 0: default: return ADJACENT;
        }
    }
}
