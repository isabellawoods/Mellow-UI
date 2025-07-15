package melonystudios.mellowui.sound;

import melonystudios.mellowui.MellowUI;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class MUISounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MellowUI.MOD_ID);

    public static final RegistryObject<SoundEvent> LIST_ENTRY_SELECTED = SOUNDS.register("ui.list_entry.selected", () -> new SoundEvent(MellowUI.mellowUI("ui.list_entry.selected")));
    // eventually I'll add the actual sounds for these (the ore ui hardcore toggle sounds)
    public static final RegistryObject<SoundEvent> HARDCORE_TOGGLE = SOUNDS.register("ui.hardcore.toggle", () -> new SoundEvent(MellowUI.mellowUI("ui.hardcore.toggle")));
    public static final RegistryObject<SoundEvent> HARDCORE_TURN_OFF = SOUNDS.register("ui.hardcore.off", () -> new SoundEvent(MellowUI.mellowUI("ui.hardcore.off")));
    public static final RegistryObject<SoundEvent> HARDCORE_TURN_ON = SOUNDS.register("ui.hardcore.on", () -> new SoundEvent(MellowUI.mellowUI("ui.hardcore.on")));
}
