package melonystudios.mellowui.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.BooleanOption;
import net.minecraft.client.settings.SliderPercentageOption;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import static melonystudios.mellowui.screen.RenderComponents.TOOLTIP_MAX_WIDTH;
import static net.minecraftforge.common.ForgeConfig.*;

public class ForgeConfigEntries {
    // Client config
    public static final BooleanOption DISABLE_STAIR_SLAB_CULLING = new BooleanOption("config.forge.disable_stair_slab_culling", new TranslationTextComponent("config.forge.disable_stair_slab_culling.desc"),
            options -> CLIENT.disableStairSlabCulling.get(), (options, newValue) -> CLIENT.disableStairSlabCulling.set(newValue));
    public static final BooleanOption ZOOM_MISSING_MODEL_TEXT_IN_GUIS = new BooleanOption("config.forge.zoom_missing_text_in_guis", new TranslationTextComponent("config.forge.zoom_missing_text_in_guis.desc"),
            options -> CLIENT.zoomInMissingModelTextInGui.get(), (options, newValue) -> CLIENT.zoomInMissingModelTextInGui.set(newValue));
    public static final BooleanOption THREADED_CHUNK_RENDERING = new BooleanOption("config.forge.threaded_chunk_rendering", new TranslationTextComponent("config.forge.threaded_chunk_rendering.desc"),
            options -> CLIENT.alwaysSetupTerrainOffThread.get(), (options, newValue) -> CLIENT.alwaysSetupTerrainOffThread.set(newValue));
    public static final BooleanOption EXPERIMENTAL_LIGHT_PIPELINE = new BooleanOption("config.forge.experimental_light_pipeline", new TranslationTextComponent("config.forge.experimental_light_pipeline.desc"),
            options -> CLIENT.experimentalForgeLightPipelineEnabled.get(), (options, newValue) -> CLIENT.experimentalForgeLightPipelineEnabled.set(newValue));
    public static final BooleanOption FORGE_LIGHT_PIPELINE = new BooleanOption("config.forge.forge_light_pipeline", new TranslationTextComponent("config.forge.forge_light_pipeline.desc"),
            options -> CLIENT.forgeLightPipelineEnabled.get(), (options, newValue) -> CLIENT.forgeLightPipelineEnabled.set(newValue));
    public static final BooleanOption SHOW_LOAD_WARNINGS = new BooleanOption("config.forge.show_load_warnings", new TranslationTextComponent("config.forge.show_load_warnings.desc"),
            options -> CLIENT.showLoadWarnings.get(), (options, newValue) -> CLIENT.showLoadWarnings.set(newValue));
    public static final BooleanOption FORGE_CLOUDS = new BooleanOption("config.forge.gpu_cloud_rendering", new TranslationTextComponent("config.forge.gpu_cloud_rendering.desc"),
            options -> CLIENT.forgeCloudsEnabled.get(), (options, newValue) -> CLIENT.forgeCloudsEnabled.set(newValue));
    public static final BooleanOption SELECTIVE_RESOURCE_LOADING = new BooleanOption("config.forge.selective_resource_loading", new TranslationTextComponent("config.forge.selective_resource_loading.desc"),
            options -> CLIENT.selectiveResourceReloadEnabled.get(), (options, newValue) -> CLIENT.selectiveResourceReloadEnabled.set(newValue));
    public static final BooleanOption USE_COMBINED_DEPTH_STENCIL_ATTACHMENT = new BooleanOption("config.forge.use_combined_depth_stencil_attachment", new TranslationTextComponent("config.forge.use_combined_depth_stencil_attachment.desc"),
            options -> CLIENT.useCombinedDepthStencilAttachment.get(), (options, newValue) -> CLIENT.useCombinedDepthStencilAttachment.set(newValue));

    // Server config
    public static final BooleanOption REMOVE_ERRORING_ENTITIES = new BooleanOption("config.forge.remove_erroring_entities", new TranslationTextComponent("config.forge.remove_erroring_entities.desc"),
            options -> SERVER.removeErroringEntities.get(), (options, newValue) -> SERVER.removeErroringEntities.set(newValue));
    public static final BooleanOption REMOVE_ERRORING_BLOCK_ENTITIES = new BooleanOption("config.forge.remove_erroring_block_entities", new TranslationTextComponent("config.forge.remove_erroring_block_entities.desc"),
            options -> SERVER.removeErroringTileEntities.get(), (options, newValue) -> SERVER.removeErroringTileEntities.set(newValue));
    public static final BooleanOption FIX_VANILLA_CASCADING = new BooleanOption("config.forge.fix_vanilla_cascading", new TranslationTextComponent("config.forge.fix_vanilla_cascading.desc"),
            options -> SERVER.fixVanillaCascading.get(), (options, newValue) -> SERVER.fixVanillaCascading.set(newValue));
    public static final BooleanOption LOG_CASCADING_WORLD_GENERATION = new BooleanOption("config.forge.log_cascading_world_generation", new TranslationTextComponent("config.forge.log_cascading_world_generation.desc"),
            options -> SERVER.logCascadingWorldGeneration.get(), (options, newValue) -> SERVER.logCascadingWorldGeneration.set(newValue));
    public static final BooleanOption FULL_BOUNDING_BOX_LADDERS = new BooleanOption("config.forge.full_bounding_box_ladders", new TranslationTextComponent("config.forge.full_bounding_box_ladders.desc"),
            options -> SERVER.fullBoundingBoxLadders.get(), (options, newValue) -> SERVER.fullBoundingBoxLadders.set(newValue));
    public static final BooleanOption FIX_ADVANCEMENT_LOADING = new BooleanOption("config.forge.fix_advancement_loading", new TranslationTextComponent("config.forge.fix_advancement_loading.desc"),
            options -> SERVER.fixAdvancementLoading.get(), (options, newValue) -> SERVER.fixAdvancementLoading.set(newValue));
    public static final BooleanOption TREAT_EMPTY_TAGS_AS_AIR = new BooleanOption("config.forge.treat_empty_tags_as_air", new TranslationTextComponent("config.forge.treat_empty_tags_as_air.desc"),
            options -> SERVER.treatEmptyTagsAsAir.get(), (options, newValue) -> SERVER.treatEmptyTagsAsAir.set(newValue));
    public static final SliderPercentageOption BABY_ZOMBIE_CHANCE = new SliderPercentageOption("config.forge.baby_zombie_chance", 0, 1, 0,
            (options) -> SERVER.zombieBabyChance.get(),
            (options, newValue) -> SERVER.zombieBabyChance.set(newValue),
            (options, slider) -> {
                slider.setTooltip(Minecraft.getInstance().font.split(new TranslationTextComponent("config.forge.baby_zombie_chance.desc"), TOOLTIP_MAX_WIDTH));
                return percentValueLabel("config.forge.baby_zombie_chance", slider.toPct(slider.get(options)));
            });
    public static final SliderPercentageOption BASE_ZOMBIE_SUMMON_CHANCE = new SliderPercentageOption("config.forge.base_zombie_summon_chance", 0, 1, 0,
            (options) -> SERVER.zombieBaseSummonChance.get(),
            (options, newValue) -> SERVER.zombieBaseSummonChance.set(newValue),
            (options, slider) -> {
                slider.setTooltip(Minecraft.getInstance().font.split(new TranslationTextComponent("config.forge.base_zombie_summon_chance.desc"), TOOLTIP_MAX_WIDTH));
                return percentValueLabel("config.forge.base_zombie_summon_chance", slider.toPct(slider.get(options)));
            });
    public static final SliderPercentageOption DIMENSION_UNLOAD_QUEUE_DELAY = new SliderPercentageOption("config.forge.dimension_unload_queue_delay", 0, 100, 1,
            (options) -> Double.valueOf(SERVER.dimensionUnloadQueueDelay.get()),
            (options, newValue) -> SERVER.dimensionUnloadQueueDelay.set((int) Math.round(newValue)),
            (options, slider) -> {
                slider.setTooltip(Minecraft.getInstance().font.split(new TranslationTextComponent("config.forge.dimension_unload_queue_delay.desc"), TOOLTIP_MAX_WIDTH));
                return new TranslationTextComponent("config.forge.dimension_unload_queue_delay", new TranslationTextComponent("config.forge.dimension_unload_queue_delay.ticks", Math.round(slider.get(options))));
            });

    public static ITextComponent percentValueLabel(String buttonTranslation, double value) {
        return new TranslationTextComponent("options.percent_value", new TranslationTextComponent(buttonTranslation), (int) (value * 100));
    }
}
