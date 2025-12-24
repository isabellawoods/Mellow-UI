package melonystudios.mellowui.screen.forge;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.element.RenderComponents;
import melonystudios.mellowui.element.text.TextComponents;
import melonystudios.mellowui.element.widget.WidgetComponents;
import melonystudios.mellowui.util.Alignment;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SettingsScreen;
import net.minecraft.client.gui.widget.list.OptionsRowList;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;

import static melonystudios.mellowui.config.ForgeConfigEntries.*;

public class ForgeServerOptionsScreen extends SettingsScreen {
    public static final List<AbstractOption> SETTINGS = Lists.newArrayList(BASE_ZOMBIE_SUMMON_CHANCE, BABY_ZOMBIE_CHANCE, REMOVE_ERRORING_ENTITIES, REMOVE_ERRORING_BLOCK_ENTITIES, FIX_VANILLA_CASCADING,
            LOG_CASCADING_WORLD_GENERATION, FULL_BOUNDING_BOX_LADDERS, FIX_ADVANCEMENT_LOADING, TREAT_EMPTY_TAGS_AS_AIR);
    private final RenderComponents components = RenderComponents.INSTANCE;
    private OptionsRowList list;

    public ForgeServerOptionsScreen(Screen screen, GameSettings options) {
        super(screen, options, TextComponents.buildScreenSubtitle("forge" , "Forge", new TranslationTextComponent("menu.forge.server_options.title")));
    }

    @Override
    protected void init() {
        WidgetComponents components = WidgetComponents.components(this, this::addButton);
        this.list = components.optionsList(33, this.height - 33);
        this.list.addBig(DIMENSION_UNLOAD_QUEUE_DELAY);
        this.list.addSmall(SETTINGS.toArray(new AbstractOption[0]));
        this.children.add(this.list);

        // Done button
        components.done(Alignment.CENTER);
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        this.list.render(stack, mouseX, mouseY, partialTicks);
        this.components.drawTitle(this.title, this.width);
        super.render(stack, mouseX, mouseY, partialTicks);
        List<IReorderingProcessor> tooltip = tooltipAt(this.list, mouseX, mouseY);
        if (tooltip != null) this.renderTooltip(stack, tooltip, mouseX, mouseY);
    }
}
