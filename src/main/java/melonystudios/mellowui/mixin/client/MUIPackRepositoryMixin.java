package melonystudios.mellowui.mixin.client;

import com.google.common.collect.Lists;
import melonystudios.mellowui.methods.InterfaceMethods;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
@Mixin(PackRepository.class)
public class MUIPackRepositoryMixin implements InterfaceMethods.PackRepositoryMethods {
    @Shadow
    private Map<String, Pack> available;
    @Shadow
    private List<Pack> selected;

    @Override
    public boolean addPack(String id) {
        Pack pack = this.available.get(id);
        if (pack != null && !this.selected.contains(pack)) {
            List<Pack> list = Lists.newArrayList(this.selected);
            list.add(pack);
            this.selected = list;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean removePack(String id) {
        Pack pack = this.available.get(id);
        if (pack != null && this.selected.contains(pack)) {
            List<Pack> list = Lists.newArrayList(this.selected);
            list.remove(pack);
            this.selected = list;
            return true;
        } else {
            return false;
        }
    }
}
