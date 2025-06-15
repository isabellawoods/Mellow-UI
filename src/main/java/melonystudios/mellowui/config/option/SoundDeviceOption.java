package melonystudios.mellowui.config.option;

import melonystudios.mellowui.config.MellowConfigs;
import melonystudios.mellowui.methods.InterfaceMethods;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.settings.IteratableOption;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SoundDeviceOption extends IteratableOption {
    private static final int OPEN_AL_SOFT_PREFIX_LENGTH = "OpenAL Soft on ".length();
    private static int CURRENT_INDEX = 0;

    public SoundDeviceOption() {
        super("config.minecraft.sound_device", (options, newValue) -> {
            Minecraft minecraft = Minecraft.getInstance();
            List<String> soundDevices = Stream.concat(Stream.of(""), ((InterfaceMethods.SoundEngineMethods) minecraft.getSoundManager()).getAvailableSoundDevices().stream()).collect(Collectors.toList());
            if (CURRENT_INDEX >= soundDevices.size()) CURRENT_INDEX = 0;
            MellowConfigs.CLIENT_CONFIGS.soundDevice.set(soundDevices.get(CURRENT_INDEX += newValue));

        }, (options, button) -> {
            String soundDevice = MellowConfigs.CLIENT_CONFIGS.soundDevice.get();
            return "".equals(soundDevice) ? new TranslationTextComponent("config.minecraft.sound_device.default") : soundDevice.startsWith("OpenAL Soft on ") ?
                    new StringTextComponent(soundDevice.substring(OPEN_AL_SOFT_PREFIX_LENGTH)) : new StringTextComponent(soundDevice);
        });
    }

    public void toggle() {
        Minecraft minecraft = Minecraft.getInstance();

        SoundHandler manager = minecraft.getSoundManager();
        ((InterfaceMethods.SoundHandlerMethods) manager).reloadSoundEngine();
        manager.play(SimpleSound.forUI(SoundEvents.UI_BUTTON_CLICK, 1));
    }

    @Override
    @Nonnull
    public Widget createButton(GameSettings options, int x, int y, int width) {
        return new Button(x, y, width, 20, new TranslationTextComponent("config.minecraft.sound_device", this.getButtonMessage()), button -> {
            this.toggle();
            button.setMessage(new TranslationTextComponent("config.minecraft.sound_device", this.getButtonMessage()));
        });
    }

    private ITextComponent getButtonMessage() {
        String soundDevice = MellowConfigs.CLIENT_CONFIGS.soundDevice.get();
        return "".equals(soundDevice) ? new TranslationTextComponent("config.minecraft.sound_device.default") : soundDevice.startsWith("OpenAL Soft on ") ?
                new StringTextComponent(soundDevice.substring(OPEN_AL_SOFT_PREFIX_LENGTH)) : new StringTextComponent(soundDevice);
    }
}
