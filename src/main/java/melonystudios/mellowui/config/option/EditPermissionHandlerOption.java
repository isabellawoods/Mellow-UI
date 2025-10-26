package melonystudios.mellowui.config.option;

import com.google.common.collect.Lists;
import melonystudios.mellowui.screen.popup.EditValueScreen;
import melonystudios.mellowui.widget.EditButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.server.permission.events.PermissionGatherEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EditPermissionHandlerOption extends EditConfigOption {
    public EditPermissionHandlerOption(String translation, @Nullable Component tooltipComponent, ForgeConfigSpec.ConfigValue<?> config) {
        super(translation, tooltipComponent, config);
    }

    @Override
    @NotNull
    public AbstractWidget createButton(Options options, int x, int y, int width) {
        Minecraft minecraft = Minecraft.getInstance();
        if (this.tooltipComponent != null) this.setTooltip(this.tooltipComponent);

        return new EditButton(x, y, width, 20, this.getCaption(), this.tooltipComponent, new TranslatableComponent("button.mellowui.edit"), this.config,
                button -> minecraft.setScreen(this.getEditScreen(minecraft)));
    }

    @NotNull
    private EditValueScreen getEditScreen(Minecraft minecraft) {
        EditValueScreen screen = new EditValueScreen(minecraft.screen, this.getCaption(), this.config, this.config.get(), false);
        screen.configSaver(value -> {
            List<String> handlerIDs = Lists.newArrayList();
            PermissionGatherEvent.Handler handlerEvent = new PermissionGatherEvent.Handler();
            handlerEvent.getAvailablePermissionHandlerFactories().keySet().forEach(location -> handlerIDs.add(location.toString()));
            if (handlerIDs.contains(value)) screen.saveConfigValue(value);
        });
        return screen;
    }
}
