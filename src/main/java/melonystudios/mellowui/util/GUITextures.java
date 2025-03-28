package melonystudios.mellowui.util;

import melonystudios.mellowui.MellowUI;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.versions.forge.ForgeVersion;

public class GUITextures {
    // Slots
    public static final ResourceLocation SLOT_HIGHLIGHT_BACK = gui("slot/slot_highlight_back");
    public static final ResourceLocation SLOT_HIGHLIGHT_FRONT = gui("slot/slot_highlight_front");

    // Buttons & Widgets
    public static final ResourceLocation TEXT_FIELD = gui("button/text_field");
    public static final ResourceLocation TEXT_FIELD_HIGHLIGHTED = gui("button/text_field_highlighted");
    public static final ResourceLocation MODS_BUTTON = gui("button/mods");

    // Backgrounds
    public static final ResourceLocation MENU_BACKGROUND = gui("menu_background");
    public static final ResourceLocation INWORLD_MENU_BACKGROUND = gui("inworld_menu_background");
    public static final ResourceLocation MENU_LIST_BACKGROUND = gui("menu_list_background");
    public static final ResourceLocation INWORLD_MENU_LIST_BACKGROUND = gui("inworld_menu_list_background");
    public static final ResourceLocation TAB_HEADER_BACKGROUND = gui("tab_header_background");
    public static final ResourceLocation INWORLD_BACKGROUND = gui("inworld_background");

    // Headers & Footers
    public static final ResourceLocation HEADER_SEPARATOR = gui("header_separator");
    public static final ResourceLocation INWORLD_HEADER_SEPARATOR = gui("inworld_header_separator");
    public static final ResourceLocation FOOTER_SEPARATOR = gui("footer_separator");
    public static final ResourceLocation INWORLD_FOOTER_SEPARATOR = gui("inworld_footer_separator");

    // Overlays
    public static final ResourceLocation WORLD_SELECTION_OVERLAY = gui("overlay/world_selection_overlay");
    public static final ResourceLocation SERVER_SELECTION_OVERLAY = gui("overlay/server_selection_overlay");
    public static final ResourceLocation PACK_SELECTION_OVERLAY = gui("overlay/resource_pack_overlay");

    // Vanilla & Forge Textures
    public static final ResourceLocation MISSING_WORLD_ICON = new ResourceLocation("textures/misc/unknown_server.png");
    public static final ResourceLocation WORLD_SELECTION_ICONS = new ResourceLocation("textures/gui/world_selection.png");
    public static final ResourceLocation PACK_SELECTION_ICONS = new ResourceLocation("textures/gui/resource_packs.png");
    public static final ResourceLocation ACCESSIBILITY_BUTTON = new ResourceLocation("textures/gui/accessibility.png");
    public static final ResourceLocation DEFAULT_PACK_ICON = new ResourceLocation("textures/misc/unknown_pack.png");
    public static final ResourceLocation VERSION_CHECKER_ICONS = new ResourceLocation(ForgeVersion.MOD_ID, "textures/gui/version_check_icons.png");

    public static ResourceLocation gui(String name) {
        return MellowUI.mellowUI("textures/gui/" + name + ".png");
    }
}
