package melonystudios.mellowui.screen.panel;

import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.screen.RenderComponents;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.MavenVersionStringHelper;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;

import java.util.List;

import static melonystudios.mellowui.util.MellowUtils.withColor;

/// Represents a **panel entry** that renders basic information, like mod id and authors, of a mod.
public class InformationPanelEntry extends PanelEntry {
    private final ModInfo mod;
    private final int accentColor;
    private int contentHeight;

    /// Represents a **panel entry** that renders basic information, like mod id and authors, of a mod.
    /// @param panel The parent panel.
    /// @param mod The {@linkplain ModInfo information about the mod}.
    /// @param accentColor The accent color provided by the {@linkplain melonystudios.mellowui.resource.flair.ModListFlair **flair**}.
    public InformationPanelEntry(Panel panel, ModInfo mod, int accentColor) {
        super(panel);
        this.mod = mod;
        this.accentColor = accentColor;
    }

    @Override
    public void renderEntry(MatrixStack stack, RenderComponents components, int x, int y, int width, int height) {
        FontRenderer font = this.panel.getFont();
        this.contentHeight = font.lineHeight;
        int yOffset = y;

        // Version
        ITextComponent version = new TranslationTextComponent("menu.mellowui.mods.version.extended",
                new StringTextComponent(MavenVersionStringHelper.artifactVersionToString(this.mod.getVersion())).withStyle(withColor(0xFFFFFF).withBold(false)))
                .withStyle(withColor(this.accentColor).withBold(true));
        font.drawShadow(stack, version, x, yOffset, 0xFFFFFF);

        // Mod ID
        ITextComponent modID = new TranslationTextComponent("menu.mellowui.mods.mod_id",
                new StringTextComponent(this.mod.getModId()).withStyle(withColor(0xFFFFFF).withBold(false)))
                .withStyle(withColor(this.accentColor).withBold(true));
        font.drawShadow(stack, modID, (float) (width - x / 2 - font.width(modID)), yOffset, 0xFFFFFF);

        // Authors
        yOffset += font.lineHeight;
        ITextComponent authors = this.mod.getConfigElement("authors").map(object -> new TranslationTextComponent("menu.mellowui.mods.authors",
                new StringTextComponent(((String) object)).withStyle(withColor(0xFFFFFF).withBold(false))).withStyle(withColor(this.accentColor).withBold(true))).orElse(null);

        if (authors != null) {
            List<IReorderingProcessor> lines = font.split(authors, x * 2 - 12);
            for (IReorderingProcessor line : lines) {
                font.drawShadow(stack, line, x, yOffset, 0xFFFFFF);
                yOffset += font.lineHeight;
            }
            this.contentHeight += lines.size() * font.lineHeight;
        }

        super.renderEntry(stack, components, x, y, width, height);
    }

    @Override
    public int getContentHeight() {
        return this.contentHeight;
    }
}
