package melonystudios.mellowui.mixin.client;

import com.google.common.collect.Lists;
import melonystudios.mellowui.methods.InterfaceMethods;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
@Mixin(ResourcePackList.class)
public class MUIPackRepositoryMixin implements InterfaceMethods.PackRepositoryMethods {
    @Shadow
    private Map<String, ResourcePackInfo> available;
    @Shadow
    private List<ResourcePackInfo> selected;

    public boolean addPack(String id) {
        ResourcePackInfo pack = this.available.get(id);
        if (pack != null && !this.selected.contains(pack)) {
            List<ResourcePackInfo> list = Lists.newArrayList(this.selected);
            list.add(pack);
            this.selected = list;
            return true;
        } else {
            return false;
        }
    }

    public boolean removePack(String id) {
        ResourcePackInfo pack = this.available.get(id);
        if (pack != null && this.selected.contains(pack)) {
            List<ResourcePackInfo> list = Lists.newArrayList(this.selected);
            list.remove(pack);
            this.selected = list;
            return true;
        } else {
            return false;
        }
    }
}
