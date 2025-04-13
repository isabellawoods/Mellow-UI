package melonystudios.mellowui.util;

import melonystudios.mellowui.MellowUI;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.versions.forge.ForgeVersion;

public class GUITextures {
    // Slots
    public static final ResourceLocation SLOT_HIGHLIGHT_BACK = gui("slot/slot_highlight_back");
    public static final ResourceLocation SLOT_HIGHLIGHT_FRONT = gui("slot/slot_highlight_front");

    // Buttons & Widgets
    public static final ResourceLocation SEPARATOR = gui("button/separator");
    public static final ResourceLocation SEPARATOR_HIGHLIGHTED = gui("button/separator_highlighted");
    public static final ResourceLocation MODS_BUTTON = gui("button/mods");
    public static final ResourceLocation CONFIG_BUTTON = gui("button/config");
    public static final ResourceLocation OPEN_FOLDER_BUTTON = gui("button/open_folder");
    public static final ResourceLocation CHANGELOGS_BUTTON = gui("button/changelogs");

    // Backgrounds
    public static final ResourceLocation MENU_BACKGROUND = gui("menu_background");
    public static final ResourceLocation INWORLD_MENU_BACKGROUND = gui("inworld_menu_background");
    public static final ResourceLocation MENU_LIST_BACKGROUND = gui("menu_list_background");
    public static final ResourceLocation INWORLD_MENU_LIST_BACKGROUND = gui("inworld_menu_list_background");
    public static final ResourceLocation TAB_HEADER_BACKGROUND = gui("tab_header_background");
    public static final ResourceLocation OUT_OF_MEMORY_BACKGROUND = gui("out_of_memory_background");
    public static final ResourceLocation INWORLD_BACKGROUND = gui("inworld_background");

    // Headers & Footers
    public static final ResourceLocation HEADER_SEPARATOR = gui("header_separator");
    public static final ResourceLocation INWORLD_HEADER_SEPARATOR = gui("inworld_header_separator");
    public static final ResourceLocation FOOTER_SEPARATOR = gui("footer_separator");
    public static final ResourceLocation INWORLD_FOOTER_SEPARATOR = gui("inworld_footer_separator");

    // Overlays
    public static final ResourceLocation WORLD_SELECTION_OVERLAY = gui("overlay/world_selection_overlay");
    public static final ResourceLocation SERVER_SELECTION_OVERLAY = gui("overlay/server_selection_overlay");
    public static final ResourceLocation PACK_SELECTION_OVERLAY = gui("overlay/pack_selection_overlay");

    // Vanilla & Forge Textures
    public static final ResourceLocation MISSING_WORLD_ICON = new ResourceLocation("textures/misc/unknown_server.png");
    public static final ResourceLocation WORLD_SELECTION_ICONS = new ResourceLocation("textures/gui/world_selection.png");
    public static final ResourceLocation PACK_SELECTION_ICONS = new ResourceLocation("textures/gui/resource_packs.png");
    public static final ResourceLocation ACCESSIBILITY_BUTTON = new ResourceLocation("textures/gui/accessibility.png");
    public static final ResourceLocation DEFAULT_PACK_ICON = new ResourceLocation("textures/misc/unknown_pack.png");
    public static final ResourceLocation VERSION_CHECKER_ICONS = new ResourceLocation(ForgeVersion.MOD_ID, "textures/gui/version_check_icons.png");

    // Mellomedley
    public static final ResourceLocation MAIN_MENU_GRADIENT = new ResourceLocation("mellomedley", "textures/gui/main_menu_gradient.png");
    public static final ResourceLocation MELLOMEDLEY_LOGO = new ResourceLocation("mellomedley", "textures/gui/mellomedley.png");

    public static ResourceLocation gui(String name) {
        return MellowUI.mellowUI("textures/gui/" + name + ".png");
    }
}
