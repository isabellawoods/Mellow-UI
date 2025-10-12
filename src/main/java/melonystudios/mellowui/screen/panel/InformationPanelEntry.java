package melonystudios.mellowui.screen.panel;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.screen.RenderComponents;
import net.minecraft.client.gui.Font;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.common.util.MavenVersionStringHelper;
import net.minecraftforge.forgespi.language.IModInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static melonystudios.mellowui.util.MellowUtils.withColor;

/// Represents a **panel entry** that renders basic information, like mod id and authors, of a mod.
public class InformationPanelEntry extends PanelEntry {
    public static final List<String> WARN_ONCE_MODS = Lists.newArrayList();
    private final IModInfo mod;
    private final int accentColor;
    private int contentHeight;

    /// Represents a **panel entry** that renders basic information, like mod id and authors, of a mod.
    /// @param panel The parent panel.
    /// @param mod The {@linkplain IModInfo information about the mod}.
    /// @param accentColor The accent color provided by the **flair**.
    public InformationPanelEntry(Panel panel, IModInfo mod, int accentColor) {
        super(panel);
        this.mod = mod;
        this.accentColor = accentColor;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void renderEntry(PoseStack stack, RenderComponents components, int x, int y, int width, int height) {
        Font font = this.panel.getFont();
        this.contentHeight = font.lineHeight;
        int yOffset = y;

        // Version
        Component version = new TranslatableComponent("menu.mellowui.mods.version.extended",
                new TextComponent(MavenVersionStringHelper.artifactVersionToString(this.mod.getVersion())).withStyle(withColor(0xFFFFFF).withBold(false)))
                .withStyle(withColor(this.accentColor).withBold(true));
        font.drawShadow(stack, version, x, yOffset, 0xFFFFFF);

        // Mod ID
        Component modID = new TranslatableComponent("menu.mellowui.mods.mod_id",
                new TextComponent(this.mod.getModId()).withStyle(withColor(0xFFFFFF).withBold(false)))
                .withStyle(withColor(this.accentColor).withBold(true));
        font.drawShadow(stack, modID, (float) (width - x / 2 - font.width(modID)), yOffset, 0xFFFFFF);

        // Authors
        yOffset += font.lineHeight;
        Component authors = this.mod.getConfig().getConfigElement("authors").map(object -> {
            if (object instanceof String) {
                return new TranslatableComponent("menu.mellowui.mods.authors",
                        new TextComponent(((String) object)).withStyle(withColor(0xFFFFFF).withBold(false)))
                        .withStyle(withColor(this.accentColor).withBold(true));
            } else if (object instanceof ArrayList) {
                try {
                    ArrayList<String> authorList = (ArrayList<String>) object;
                    MutableComponent component = new TextComponent(authorList.stream().collect(Collectors.joining(I18n.get("menu.mellowui.delimiter"))))
                            .withStyle(withColor(0xFFFFFF).withBold(false));
                    return new TranslatableComponent("menu.mellowui.mods.authors", component).withStyle(withColor(this.accentColor).withBold(true));
                } catch (Exception exception) {
                    if (!WARN_ONCE_MODS.contains(this.mod.getModId())) {
                        WARN_ONCE_MODS.add(this.mod.getModId());
                        MellowUI.logger("InformationPanelEntry").error(I18n.get("panel.mellowui.mod_information.broken_authors"), exception);
                    }
                }
            }
            return null;
        }).orElse(null);

        if (authors != null) {
            List<FormattedCharSequence> lines = font.split(authors, x * 2 - 12);
            for (FormattedCharSequence line : lines) {
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
