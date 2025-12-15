package melonystudios.mellowui.config;

import melonystudios.mellowui.config.option.BooleanOption;
import melonystudios.mellowui.config.option.EditPermissionHandlerOption;
import melonystudios.mellowui.config.option.IterableOption;
import melonystudios.mellowui.config.type.ModListSorting;
import net.minecraft.client.ProgressOption;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;

import static melonystudios.mellowui.config.MellowConfigs.CLIENT_CONFIGS;
import static melonystudios.mellowui.element.RenderComponents.TOOLTIP_MAX_WIDTH;
import static net.minecraftforge.common.ForgeConfig.*;

public class ForgeConfigEntries {
    // Tooltips
    public static final MutableComponent SORTING_TOOLTIP = new TranslatableComponent("config.forge.mod_list_sorting.tooltip");

    // Client config
    public static final BooleanOption THREADED_CHUNK_RENDERING = new BooleanOption("config.forge.threaded_chunk_rendering", new TranslatableComponent("config.forge.threaded_chunk_rendering.tooltip"),
            options -> CLIENT.alwaysSetupTerrainOffThread.get(), (options, newValue) -> CLIENT.alwaysSetupTerrainOffThread.set(newValue));
    public static final BooleanOption EXPERIMENTAL_LIGHT_PIPELINE = new BooleanOption("config.forge.experimental_light_pipeline", new TranslatableComponent("config.forge.experimental_light_pipeline.tooltip"),
            options -> CLIENT.experimentalForgeLightPipelineEnabled.get(), (options, newValue) -> CLIENT.experimentalForgeLightPipelineEnabled.set(newValue));
    public static final BooleanOption SHOW_LOAD_WARNINGS = new BooleanOption("config.forge.show_load_warnings", new TranslatableComponent("config.forge.show_load_warnings.tooltip"),
            options -> CLIENT.showLoadWarnings.get(), (options, newValue) -> CLIENT.showLoadWarnings.set(newValue));
    public static final BooleanOption USE_COMBINED_DEPTH_STENCIL_ATTACHMENT = new BooleanOption("config.forge.use_combined_depth_stencil_attachment", new TranslatableComponent("config.forge.use_combined_depth_stencil_attachment.tooltip"),
            options -> CLIENT.useCombinedDepthStencilAttachment.get(), (options, newValue) -> CLIENT.useCombinedDepthStencilAttachment.set(newValue));
    public static final BooleanOption FORCE_SYSTEM_NANO_TIME = new BooleanOption("config.forge.force_system_nano_time", new TranslatableComponent("config.forge.force_system_nano_time.tooltip"),
            options -> CLIENT.forceSystemNanoTime.get(), (options, newValue) -> CLIENT.forceSystemNanoTime.set(newValue));
    public static final BooleanOption COMPRESS_LAN_IPV6_ADDRESSES = new BooleanOption("config.forge.compress_lan_ipv6_addresses", new TranslatableComponent("config.forge.compress_lan_ipv6_addresses.tooltip"),
            options -> CLIENT.compressLanIPv6Addresses.get(), (options, newValue) -> CLIENT.compressLanIPv6Addresses.set(newValue));

    // Server config
    public static final BooleanOption REMOVE_ERRORING_ENTITIES = new BooleanOption("config.forge.remove_erroring_entities", new TranslatableComponent("config.forge.remove_erroring_entities.tooltip"),
            options -> SERVER.removeErroringEntities.get(), (options, newValue) -> SERVER.removeErroringEntities.set(newValue));
    public static final BooleanOption REMOVE_ERRORING_BLOCK_ENTITIES = new BooleanOption("config.forge.remove_erroring_block_entities", new TranslatableComponent("config.forge.remove_erroring_block_entities.tooltip"),
            options -> SERVER.removeErroringBlockEntities.get(), (options, newValue) -> SERVER.removeErroringBlockEntities.set(newValue));
    public static final BooleanOption FULL_BOUNDING_BOX_LADDERS = new BooleanOption("config.forge.full_bounding_box_ladders", new TranslatableComponent("config.forge.full_bounding_box_ladders.tooltip"),
            options -> SERVER.fullBoundingBoxLadders.get(), (options, newValue) -> SERVER.fullBoundingBoxLadders.set(newValue));
    public static final BooleanOption FIX_ADVANCEMENT_LOADING = new BooleanOption("config.forge.fix_advancement_loading", new TranslatableComponent("config.forge.fix_advancement_loading.tooltip"),
            options -> SERVER.fixAdvancementLoading.get(), (options, newValue) -> SERVER.fixAdvancementLoading.set(newValue));
    public static final BooleanOption TREAT_EMPTY_TAGS_AS_AIR = new BooleanOption("config.forge.treat_empty_tags_as_air", new TranslatableComponent("config.forge.treat_empty_tags_as_air.tooltip"),
            options -> SERVER.treatEmptyTagsAsAir.get(), (options, newValue) -> SERVER.treatEmptyTagsAsAir.set(newValue));
    public static final BooleanOption SKIP_EMPTY_SHAPELESS_CHECK = new BooleanOption("config.forge.skip_empty_shapeless_check", new TranslatableComponent("config.forge.skip_empty_shapeless_check.tooltip"),
            options -> SERVER.skipEmptyShapelessCheck.get(), (options, newValue) -> SERVER.skipEmptyShapelessCheck.set(newValue));
    public static final ProgressOption BABY_ZOMBIE_CHANCE = new ProgressOption("config.forge.baby_zombie_chance", 0, 1, 0,
            (options) -> SERVER.zombieBabyChance.get(),
            (options, newValue) -> SERVER.zombieBabyChance.set(newValue),
            (options, slider) -> percentValueLabel("config.forge.baby_zombie_chance", slider.toPct(slider.get(options))),
            minecraft -> minecraft.font.split(new TranslatableComponent("config.forge.baby_zombie_chance.tooltip"), TOOLTIP_MAX_WIDTH));
    public static final ProgressOption BASE_ZOMBIE_SUMMON_CHANCE = new ProgressOption("config.forge.base_zombie_summon_chance", 0, 1, 0,
            (options) -> SERVER.zombieBaseSummonChance.get(),
            (options, newValue) -> SERVER.zombieBaseSummonChance.set(newValue),
            (options, slider) -> percentValueLabel("config.forge.base_zombie_summon_chance", slider.toPct(slider.get(options))),
            minecraft -> minecraft.font.split(new TranslatableComponent("config.forge.base_zombie_summon_chance.tooltip"), TOOLTIP_MAX_WIDTH));
    public static final EditPermissionHandlerOption PERMISSION_HANDLER = new EditPermissionHandlerOption("config.forge.permission_handler",
            new TranslatableComponent("config.forge.permission_handler.tooltip"), SERVER.permissionHandler);

    // Mellow UI's common config
    public static final IterableOption MOD_LIST_SORTING = new IterableOption("config.forge.mod_list_sorting",
            (options, identifier) -> CLIENT_CONFIGS.modListSorting.set(ModListSorting.byId(CLIENT_CONFIGS.modListSorting.get().getId() + identifier)),
            (options, option) -> {
                option.setTooltip(SORTING_TOOLTIP);
                return new TranslatableComponent("config.forge.mod_list_sorting", new TranslatableComponent("config.forge.mod_list_sorting." + CLIENT_CONFIGS.modListSorting.get().toString()));
            });

    public static Component percentValueLabel(String buttonTranslation, double value) {
        return new TranslatableComponent("options.percent_value", new TranslatableComponent(buttonTranslation), (int) (value * 100));
    }
}
