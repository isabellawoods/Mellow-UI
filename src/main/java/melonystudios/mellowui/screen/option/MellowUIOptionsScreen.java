package melonystudios.mellowui.screen.option;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import melonystudios.mellowui.util.MellowUtils;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SettingsScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.OptionsRowList;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;

import static melonystudios.mellowui.config.MellowConfigEntries.*;

public class MellowUIOptionsScreen extends SettingsScreen {
    public static final List<AbstractOption> SETTINGS = Lists.newArrayList(MONOCHROME_LOADING_SCREEN_COLOR, LEGACY_BUTTON_COLORS, SCROLLING_TEXT);
    public static final List<AbstractOption> MAIN_MENU = Lists.newArrayList(SPLASH_TEXT_COLOR, SPLASH_TEXT_POSITION, DISABLE_BRANDING, MAIN_MENU_MOD_BUTTON, LOGO_STYLE);
    public static final List<AbstractOption> PAUSE_MENU = Lists.newArrayList(PAUSE_MENU_MOD_BUTTON);
    public static final List<AbstractOption> MENU_UPDATES = Lists.newArrayList(UPDATED_SCREEN_BACKGROUND, UPDATED_LIST_BACKGROUND, MAIN_MENU_STYLE, UPDATED_PAUSE_MENU, UPDATED_OPTIONS_MENU, UPDATED_SKIN_CUSTOMIZATION_MENU, UPDATED_MUSIC_AND_SOUNDS_MENU, UPDATED_CONTROLS_MENU, UPDATED_PACK_MENU, UPDATED_ACCESSIBILITY_MENU, UPDATED_OUT_OF_MEMORY_MENU, REPLACE_REALMS_NOTIFICATIONS);
    private OptionsRowList list;

    public MellowUIOptionsScreen(Screen screen, GameSettings options) {
        super(screen, options, new TranslationTextComponent("menu.mellowui.mellow_ui_options.title"));
    }

    @Override
    public void tick() {
        super.tick();
        MONOCHROME_LOADING_SCREEN_COLOR.tick();
        SPLASH_TEXT_COLOR.tick();
    }

    @Override
    protected void init() {
        this.list = new OptionsRowList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
        this.list.addBig(PANORAMA_CAMERA_PITCH);
        this.list.addSmall(SETTINGS.toArray(new AbstractOption[0]));
        this.list.addBig(MAIN_MENU_SEPARATOR);
        this.list.addSmall(MAIN_MENU.toArray(new AbstractOption[0]));
        this.list.addBig(PAUSE_MENU_SEPARATOR);
        this.list.addSmall(PAUSE_MENU.toArray(new AbstractOption[0]));
        this.list.addBig(MENU_UPDATES_SEPARATOR);
        this.list.addSmall(MENU_UPDATES.toArray(new AbstractOption[0]));
        this.children.add(this.list);

        // Done button
        this.addButton(new Button(this.width / 2 - 100, this.height - 25, 200, 20, DialogTexts.GUI_DONE,
                button -> this.minecraft.setScreen(this.lastScreen)));
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        this.list.render(stack, mouseX, mouseY, partialTicks);
        drawCenteredString(stack, this.font, this.title, this.width / 2, MellowUtils.DEFAULT_TITLE_HEIGHT, 0xFFFFFF);
        super.render(stack, mouseX, mouseY, partialTicks);
        List<IReorderingProcessor> processors = tooltipAt(this.list, mouseX, mouseY);
        if (processors != null) this.renderTooltip(stack, processors, mouseX, mouseY);
    }
}
