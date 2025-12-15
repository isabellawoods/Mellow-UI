package melonystudios.mellowui.backport;

import com.google.common.collect.Maps;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static net.minecraft.util.FastColor.ARGB32.*;

public class ColorLerper {
    public static final DyeColor[] MUSIC_NOTE_COLORS = new DyeColor[]{
            DyeColor.WHITE,
            DyeColor.LIGHT_GRAY,
            DyeColor.LIGHT_BLUE,
            DyeColor.BLUE,
            DyeColor.CYAN,
            DyeColor.GREEN,
            DyeColor.LIME,
            DyeColor.YELLOW,
            DyeColor.ORANGE,
            DyeColor.PINK,
            DyeColor.RED,
            DyeColor.MAGENTA
    };

    public static int getLerpedColor(ColorLerper.Type type, float time) {
        int i = Mth.floor(time);
        int j = i / type.colorDuration;
        int k = type.colors.length;
        int l = j % k;
        int i1 = (j + 1) % k;
        float f = (i % type.colorDuration + Mth.frac(time)) / type.colorDuration;
        int j1 = type.getColor(type.colors[l]);
        int k1 = type.getColor(type.colors[i1]);
        return lerp(f, j1, k1);
    }

    public static int lerp(float delta, int color1, int color2) {
        int alpha = lerpInt(delta, alpha(color1), alpha(color2));
        int red = lerpInt(delta, red(color1), red(color2));
        int green = lerpInt(delta, green(color1), green(color2));
        int blue = lerpInt(delta, blue(color1), blue(color2));
        return color(alpha, red, green, blue);
    }

    public static int lerpInt(float delta, int start, int end) {
        return start + Mth.floor(delta * (end - start));
    }

    static int getModifiedColor(DyeColor color, float brightness) {
        if (color == DyeColor.WHITE) {
            return 0xFFE6E6E6;
        } else {
            int diffuseColor = color(0, (int) (color.getTextureDiffuseColors()[0] * 255), (int) (color.getTextureDiffuseColors()[1] * 255), (int) (color.getTextureDiffuseColors()[2] * 255));
            return color(255, Mth.floor(red(diffuseColor) * brightness), Mth.floor(green(diffuseColor) * brightness), Mth.floor(blue(diffuseColor) * brightness));
        }
    }

    @OnlyIn(Dist.CLIENT)
    public enum Type {
        MUSIC_NOTE(30, ColorLerper.MUSIC_NOTE_COLORS, 1.25F);

        final int colorDuration;
        private final Map<DyeColor, Integer> colorByDye;
        final DyeColor[] colors;

        Type(int colorDuration, DyeColor[] colors, float brightness) {
            this.colorDuration = colorDuration;
            this.colorByDye = Maps.newHashMap(Arrays.stream(colors)
                    .collect(Collectors.toMap(color -> color,
                            color -> ColorLerper.getModifiedColor(color, brightness))
                    ));
            this.colors = colors;
        }

        public final int getColor(DyeColor dye) {
            return this.colorByDye.get(dye);
        }
    }
}
