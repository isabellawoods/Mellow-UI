package melonystudios.mellowui.config.type;

import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.renderer.LogoRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public enum LogoStyles implements IStringSerializable {
    PRE_ONE_SIXTEEN(0, "pre_one_sixteen", (stack, screen, useDefaultValues, x, y, width, height, transparency, keepLogoThroughFade) -> {
        if (useDefaultValues) LogoRenderer.renderPre116Logo(stack, screen, width, transparency, 30, keepLogoThroughFade);
        else LogoRenderer.renderPre116Logo(stack, screen, width, transparency, height, keepLogoThroughFade);
    }),
    ONE_SIXTEEN(1, "one_sixteen", (stack, screen, useDefaultValues, x, y, width, height, transparency, keepLogoThroughFade) -> {
        if (useDefaultValues) LogoRenderer.render116Logo(stack, screen, width, transparency, 30, keepLogoThroughFade);
        else LogoRenderer.render116Logo(stack, screen, width, transparency, height, keepLogoThroughFade);
    }),
    ONE_TWENTY(2, "one_twenty", (stack, screen, useDefaultValues, x, y, width, height, transparency, keepLogoThroughFade) -> {
        LogoRenderer.renderUpdatedLogo(stack, width, transparency, keepLogoThroughFade);
    }),
    MELLOMEDLEY(3, "mellomedley", (stack, screen, useDefaultValues, x, y, width, height, transparency, keepLogoThroughFade) -> {
        if (useDefaultValues) LogoRenderer.renderMellomedleyLogo(stack, width / 2 - 129, 10, 258, 100, transparency, keepLogoThroughFade);
        else LogoRenderer.renderMellomedleyLogo(stack, x, y, width, height, transparency, keepLogoThroughFade);
    });

    private final int id;
    private final String name;
    private final StyleRenderer renderer;

    LogoStyles(int id, String name, StyleRenderer renderer) {
        this.id = id;
        this.name = name;
        this.renderer = renderer;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public int getId() {
        return this.id;
    }

    /// Renders a **logo**.
    /// @param stack The {@link MatrixStack} instance used for rendering.
    /// @param screen The screen to render in.
    /// @param useDefaultValues Whether to render the logos using their default parameters, instead of using the ones provided. **Logos are rendered better when this is `true`.**
    /// @param x The x-position of the logo (*Mellomedley* only).
    /// @param y The y-position of the logo (*Mellomedley* only).
    /// @param width The width of the screen (for *Minecraft logos*) or the texture (*Mellomedley*'s logo).
    /// @param height The height of the screen (for *Minecraft logos*) or the texture (*Mellomedley*'s logo).
    /// @param transparency The transparency of the logo, usually ranging from `0` to `1`.
    /// @param keepLogoThroughFade Whether to keep the logo visible during the fading animation.
    public void renderLogo(MatrixStack stack, Screen screen, boolean useDefaultValues, int x, int y, int width, int height, float transparency, boolean keepLogoThroughFade) {
        this.renderer.renderLogo(stack, screen, useDefaultValues, x, y, width, height, transparency, keepLogoThroughFade);
    }

    @Override
    @Nonnull
    public String getSerializedName() {
        return this.name;
    }

    public static LogoStyles byId(int identifier) {
        switch (identifier) {
            case 1: return ONE_SIXTEEN;
            case 2: return ONE_TWENTY;
            case 3: return MELLOMEDLEY;
            case 0: default: return PRE_ONE_SIXTEEN;
        }
    }

    /// Helper interface for rendering **logo styles**.
    public interface StyleRenderer {
        /// Renders a **logo**.
        /// @param stack The {@link MatrixStack} instance used for rendering.
        /// @param screen The screen to render in.
        /// @param useDefaultValues Whether to render the logos using their default parameters, instead of using the ones provided. **Logos are rendered better when this is `true`.**
        /// @param x The x-position of the logo (*Mellomedley* only).
        /// @param y The y-position of the logo (*Mellomedley* only).
        /// @param width The width of the screen (for *Minecraft logos*) or the texture (*Mellomedley*'s logo).
        /// @param height The height of the screen (for *Minecraft logos*) or the texture (*Mellomedley*'s logo).
        /// @param transparency The transparency of the logo, usually ranging from `0` to `1`.
        /// @param keepLogoThroughFade Whether to keep the logo visible during the fading animation.
        void renderLogo(MatrixStack stack, Screen screen, boolean useDefaultValues, int x, int y, int width, int height, float transparency, boolean keepLogoThroughFade);
    }
}
