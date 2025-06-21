package melonystudios.mellowui.util;

import melonystudios.mellowui.MellowUI;
import melonystudios.mellowui.screen.widget.WidgetTextureSet;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.versions.forge.ForgeVersion;

public class GUITextures {
    // Texture Sets
    public static final WidgetTextureSet SWITCH_STYLE_SET = new WidgetTextureSet(gui("widget/icon/switch_style"), gui("widget/icon/switch_style_highlighted"), gui("widget/icon/switch_style_disabled"));
    public static final WidgetTextureSet MODS_SET = new WidgetTextureSet(gui("widget/icon/mods"), gui("widget/icon/mods_highlighted"));
    public static final WidgetTextureSet CONFIGURE_SET = new WidgetTextureSet(gui("widget/icon/configure"), gui("widget/icon/configure_highlighted"), gui("widget/icon/configure_disabled"));
    public static final WidgetTextureSet OPEN_FOLDER_SET = new WidgetTextureSet(gui("widget/icon/open_folder"), gui("widget/icon/open_folder_highlighted"));
    public static final WidgetTextureSet CHANGELOGS_SET = new WidgetTextureSet(gui("widget/icon/changelogs"), gui("widget/icon/changelogs_highlighted"));
    public static final WidgetTextureSet ACCESSIBILITY_SET = new WidgetTextureSet(gui("widget/icon/accessibility"), gui("widget/icon/accessibility_highlighted"));
    public static final WidgetTextureSet LANGUAGE_SET = new WidgetTextureSet(gui("widget/icon/language"), gui("widget/icon/language_highlighted"));

    // Slots
    public static final ResourceLocation SLOT_HIGHLIGHT_BACK = gui("miscellaneous/slot_highlight_back");
    public static final ResourceLocation SLOT_HIGHLIGHT_FRONT = gui("miscellaneous/slot_highlight_front");

    // Buttons & Widgets
    public static final ResourceLocation SEPARATOR = gui("widget/separator");
    public static final ResourceLocation SEPARATOR_HIGHLIGHTED = gui("widget/separator_highlighted");
    public static final ResourceLocation TAB = gui("widget/tab");
    public static final ResourceLocation TAB_HIGHLIGHTED = gui("widget/tab_highlighted");
    public static final ResourceLocation TAB_SELECTED = gui("widget/tab_selected");
    public static final ResourceLocation TAB_SELECTED_HIGHLIGHTED = gui("widget/tab_selected_highlighted");
    public static final ResourceLocation MOD_ENTRY = gui("widget/mod_entry");
    public static final ResourceLocation MOD_ENTRY_HIGHLIGHTED = gui("widget/mod_entry_highlighted");

    // Backgrounds
    public static final ResourceLocation MENU_BACKGROUND = gui("background/menu");
    public static final ResourceLocation INWORLD_MENU_BACKGROUND = gui("background/inworld_menu");
    public static final ResourceLocation MENU_LIST_BACKGROUND = gui("background/menu_list");
    public static final ResourceLocation INWORLD_MENU_LIST_BACKGROUND = gui("background/inworld_menu_list");
    public static final ResourceLocation TAB_HEADER_BACKGROUND = gui("background/tab_header");
    public static final ResourceLocation OUT_OF_MEMORY_BACKGROUND = gui("background/out_of_memory");
    public static final ResourceLocation ACCESSIBILITY_ONBOARDING_BACKGROUND = gui("background/accessibility_onboarding");
    public static final ResourceLocation INWORLD_GRADIENT = gui("background/inworld_gradient");

    // Headers & Footers
    public static final ResourceLocation HEADER_SEPARATOR = gui("background/header_separator");
    public static final ResourceLocation INWORLD_HEADER_SEPARATOR = gui("background/inworld_header_separator");
    public static final ResourceLocation FOOTER_SEPARATOR = gui("background/footer_separator");
    public static final ResourceLocation INWORLD_FOOTER_SEPARATOR = gui("background/inworld_footer_separator");

    // Overlays
    public static final ResourceLocation WORLD_SELECTION_OVERLAY = gui("miscellaneous/world_selection_overlay");
    public static final ResourceLocation SERVER_SELECTION_OVERLAY = gui("miscellaneous/server_selection_overlay");
    public static final ResourceLocation PACK_SELECTION_OVERLAY = gui("miscellaneous/pack_selection_overlay");

    // Toasts
    public static final ResourceLocation NOW_PLAYING_TOAST = gui("toast/now_playing");
    public static final ResourceLocation MUSIC_NOTES = gui("toast/music_notes");

    // Vanilla & Forge Textures
    public static final ResourceLocation MISSING_WORLD_ICON = new ResourceLocation("textures/misc/unknown_server.png");
    public static final ResourceLocation WORLD_SELECTION_ICONS = new ResourceLocation("textures/gui/world_selection.png");
    public static final ResourceLocation PACK_SELECTION_ICONS = new ResourceLocation("textures/gui/resource_packs.png");
    public static final ResourceLocation ACCESSIBILITY_BUTTON = new ResourceLocation("textures/gui/accessibility.png");
    public static final ResourceLocation DEFAULT_PACK_ICON = new ResourceLocation("textures/misc/unknown_pack.png");
    public static final ResourceLocation VERSION_CHECKER_ICONS = new ResourceLocation(ForgeVersion.MOD_ID, "textures/gui/version_check_icons.png");
    public static final ResourceLocation PANORAMA_OVERLAY = new ResourceLocation("textures/gui/title/background/panorama_overlay.png");
    public static final ResourceLocation OPTIONS_BACKGROUND = new ResourceLocation("textures/gui/options_background.png");

    // Mellomedley
    public static final ResourceLocation MAIN_MENU_GRADIENT = new ResourceLocation("mellomedley", "textures/gui/main_menu_gradient.png");
    public static final ResourceLocation MELLOMEDLEY_LOGO = new ResourceLocation("mellomedley", "textures/gui/mellomedley.png");

    // Other locations (not really textures, but I'll put these here anyway)
    // by default points to a copy of the vanilla "blur" shader (because I don't know how to port the updated blur shader, however this works fine)
    public static final ResourceLocation MUI_HIGH_CONTRAST = MellowUI.mellowUI("high_contrast");
    public static final ResourceLocation LIBRARY_HIGH_CONTRAST = new ResourceLocation("melonylib", "high_contrast"); // adding this for compatibility with my older mods

    public static ResourceLocation gui(String name) {
        return MellowUI.mellowUI("textures/gui/" + name + ".png");
    }
}
