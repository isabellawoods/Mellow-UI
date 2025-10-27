package melonystudios.mellowui.screen.panel;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.screen.RenderComponents;
import melonystudios.mellowui.util.text.TextComponents;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.MavenVersionStringHelper;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static melonystudios.mellowui.util.text.TextComponents.withColor;

/// Represents a **panel entry** that renders basic information, like mod id and authors, of a mod.
public class InformationPanelEntry extends PanelEntry {
    public static final List<String> WARN_ONCE_MODS = Lists.newArrayList();
    private final ModInfo mod;
    private final int accentColor;
    private int contentHeight;

    /// Represents a **panel entry** that renders basic information, like mod id and authors, of a mod.
    /// @param panel The parent panel.
    /// @param mod The {@linkplain ModInfo information about the mod}.
    /// @param accentColor The accent color provided by the {@linkplain melonystudios.mellowui.resource.flair.Flair **flair**}.
    public InformationPanelEntry(Panel panel, ModInfo mod, int accentColor) {
        super(panel);
        this.mod = mod;
        this.accentColor = accentColor;
    }

    @Override
    @SuppressWarnings("unchecked")
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
        ITextComponent authors = this.mod.getConfigElement("authors").map(object -> {
            if (object instanceof String) {
                return new TranslationTextComponent("menu.mellowui.mods.authors",
                        new StringTextComponent(((String) object)).withStyle(withColor(0xFFFFFF).withBold(false)))
                        .withStyle(withColor(this.accentColor).withBold(true));
            } else if (object instanceof ArrayList) {
                try {
                    ArrayList<String> authorList = (ArrayList<String>) object;
                    IFormattableTextComponent component = new StringTextComponent(authorList.stream().collect(Collectors.joining(I18n.get("menu.mellowui.delimiter"))))
                            .withStyle(withColor(0xFFFFFF).withBold(false));
                    return new TranslationTextComponent("menu.mellowui.mods.authors", component).withStyle(withColor(this.accentColor).withBold(true));
                } catch (Exception exception) {
                    if (!WARN_ONCE_MODS.contains(this.mod.getModId())) {
                        WARN_ONCE_MODS.add(this.mod.getModId());
                        MellowUI.logger("InformationPanelEntry").error(TextComponents.translate("panel.mellowui.mod_information.broken_authors", "Mod '%s' has a broken \"authors\" field! Please report to Mellow UI about this", this.mod.getModId()), exception);
                    }
                }
            }
            return null;
        }).orElse(null);

        if (authors != null) {
            List<IReorderingProcessor> lines = font.split(authors, x * 2 - 16);
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
