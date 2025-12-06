package melonystudios.mellowui.backport;

import com.google.common.collect.Maps;
import net.minecraft.item.DyeColor;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static net.minecraft.util.ColorHelper.PackedColor.*;

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
        int i = MathHelper.floor(time);
        int j = i / type.colorDuration;
        int k = type.colors.length;
        int l = j % k;
        int i1 = (j + 1) % k;
        float f = (i % type.colorDuration + MathHelper.frac(time)) / type.colorDuration;
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
        return start + MathHelper.floor(delta * (end - start));
    }

    static int getModifiedColor(DyeColor color, float brightness) {
        if (color == DyeColor.WHITE) {
            return -1644826;
        } else {
            int diffuseColor = color.getColorValue();
            return color(255, MathHelper.floor(red(diffuseColor) * brightness), MathHelper.floor(green(diffuseColor) * brightness), MathHelper.floor(blue(diffuseColor) * brightness));
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
