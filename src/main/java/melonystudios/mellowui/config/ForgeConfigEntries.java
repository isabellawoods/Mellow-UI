package melonystudios.mellowui.config;

import com.google.common.collect.Lists;
import melonystudios.mellowui.config.option.BooleanOption;
import melonystudios.mellowui.config.option.TextFieldOption;
import net.minecraft.client.ProgressOption;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.server.permission.events.PermissionGatherEvent;

import java.util.List;

import static melonystudios.mellowui.screen.RenderComponents.TOOLTIP_MAX_WIDTH;
import static net.minecraftforge.common.ForgeConfig.*;

public class ForgeConfigEntries {
    // Client config
    public static final BooleanOption THREADED_CHUNK_RENDERING = new BooleanOption("config.forge.threaded_chunk_rendering", new TranslatableComponent("config.forge.threaded_chunk_rendering.desc"),
            options -> CLIENT.alwaysSetupTerrainOffThread.get(), (options, newValue) -> CLIENT.alwaysSetupTerrainOffThread.set(newValue));
    public static final BooleanOption EXPERIMENTAL_LIGHT_PIPELINE = new BooleanOption("config.forge.experimental_light_pipeline", new TranslatableComponent("config.forge.experimental_light_pipeline.desc"),
            options -> CLIENT.experimentalForgeLightPipelineEnabled.get(), (options, newValue) -> CLIENT.experimentalForgeLightPipelineEnabled.set(newValue));
    public static final BooleanOption SHOW_LOAD_WARNINGS = new BooleanOption("config.forge.show_load_warnings", new TranslatableComponent("config.forge.show_load_warnings.desc"),
            options -> CLIENT.showLoadWarnings.get(), (options, newValue) -> CLIENT.showLoadWarnings.set(newValue));
    public static final BooleanOption USE_COMBINED_DEPTH_STENCIL_ATTACHMENT = new BooleanOption("config.forge.use_combined_depth_stencil_attachment", new TranslatableComponent("config.forge.use_combined_depth_stencil_attachment.desc"),
            options -> CLIENT.useCombinedDepthStencilAttachment.get(), (options, newValue) -> CLIENT.useCombinedDepthStencilAttachment.set(newValue));
    public static final BooleanOption FORCE_SYSTEM_NANO_TIME = new BooleanOption("config.forge.force_system_nano_time", new TranslatableComponent("config.forge.force_system_nano_time.desc"),
            options -> CLIENT.forceSystemNanoTime.get(), (options, newValue) -> CLIENT.forceSystemNanoTime.set(newValue));
    public static final BooleanOption COMPRESS_LAN_IPV6_ADDRESSES = new BooleanOption("config.forge.compress_lan_ipv6_addresses", new TranslatableComponent("config.forge.compress_lan_ipv6_addresses.desc"),
            options -> CLIENT.compressLanIPv6Addresses.get(), (options, newValue) -> CLIENT.compressLanIPv6Addresses.set(newValue));

    // Server config
    public static final BooleanOption REMOVE_ERRORING_ENTITIES = new BooleanOption("config.forge.remove_erroring_entities", new TranslatableComponent("config.forge.remove_erroring_entities.desc"),
            options -> SERVER.removeErroringEntities.get(), (options, newValue) -> SERVER.removeErroringEntities.set(newValue));
    public static final BooleanOption REMOVE_ERRORING_BLOCK_ENTITIES = new BooleanOption("config.forge.remove_erroring_block_entities", new TranslatableComponent("config.forge.remove_erroring_block_entities.desc"),
            options -> SERVER.removeErroringBlockEntities.get(), (options, newValue) -> SERVER.removeErroringBlockEntities.set(newValue));
    public static final BooleanOption FULL_BOUNDING_BOX_LADDERS = new BooleanOption("config.forge.full_bounding_box_ladders", new TranslatableComponent("config.forge.full_bounding_box_ladders.desc"),
            options -> SERVER.fullBoundingBoxLadders.get(), (options, newValue) -> SERVER.fullBoundingBoxLadders.set(newValue));
    public static final BooleanOption FIX_ADVANCEMENT_LOADING = new BooleanOption("config.forge.fix_advancement_loading", new TranslatableComponent("config.forge.fix_advancement_loading.desc"),
            options -> SERVER.fixAdvancementLoading.get(), (options, newValue) -> SERVER.fixAdvancementLoading.set(newValue));
    public static final BooleanOption TREAT_EMPTY_TAGS_AS_AIR = new BooleanOption("config.forge.treat_empty_tags_as_air", new TranslatableComponent("config.forge.treat_empty_tags_as_air.desc"),
            options -> SERVER.treatEmptyTagsAsAir.get(), (options, newValue) -> SERVER.treatEmptyTagsAsAir.set(newValue));
    public static final BooleanOption SKIP_EMPTY_SHAPELESS_CHECK = new BooleanOption("config.forge.skip_empty_shapeless_check", new TranslatableComponent("config.forge.skip_empty_shapeless_check.desc"),
            options -> SERVER.skipEmptyShapelessCheck.get(), (options, newValue) -> SERVER.skipEmptyShapelessCheck.set(newValue));
    public static final ProgressOption BABY_ZOMBIE_CHANCE = new ProgressOption("config.forge.baby_zombie_chance", 0, 1, 0,
            (options) -> SERVER.zombieBabyChance.get(),
            (options, newValue) -> SERVER.zombieBabyChance.set(newValue),
            (options, slider) -> percentValueLabel("config.forge.baby_zombie_chance", slider.toPct(slider.get(options))),
            minecraft -> minecraft.font.split(new TranslatableComponent("config.forge.baby_zombie_chance.desc"), TOOLTIP_MAX_WIDTH));
    public static final ProgressOption BASE_ZOMBIE_SUMMON_CHANCE = new ProgressOption("config.forge.base_zombie_summon_chance", 0, 1, 0,
            (options) -> SERVER.zombieBaseSummonChance.get(),
            (options, newValue) -> SERVER.zombieBaseSummonChance.set(newValue),
            (options, slider) -> percentValueLabel("config.forge.base_zombie_summon_chance", slider.toPct(slider.get(options))),
            minecraft -> minecraft.font.split(new TranslatableComponent("config.forge.base_zombie_summon_chance.desc"), TOOLTIP_MAX_WIDTH));
    public static final TextFieldOption PERMISSION_HANDLER = new TextFieldOption("config.forge.permission_handler",
            new TranslatableComponent("config.forge.permission_handler.desc"), SERVER.permissionHandler.get(), SERVER.permissionHandler::set,
            (text, setter) -> {
                List<String> handlerIDs = Lists.newArrayList();
                PermissionGatherEvent.Handler handlerEvent = new PermissionGatherEvent.Handler();
                handlerEvent.getAvailablePermissionHandlerFactories().keySet().forEach(location -> handlerIDs.add(location.toString()));
                if (handlerIDs.contains(text)) setter.accept(text);
            });

    public static Component percentValueLabel(String buttonTranslation, double value) {
        return new TranslatableComponent("options.percent_value", new TranslatableComponent(buttonTranslation), (int) (value * 100));
    }
}
