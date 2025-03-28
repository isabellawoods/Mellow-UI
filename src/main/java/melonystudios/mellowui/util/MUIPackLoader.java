package melonystudios.mellowui.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.resources.IPackNameDecorator;
import net.minecraft.resources.PackCompatibility;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

@OnlyIn(Dist.CLIENT)
public class MUIPackLoader {
    private final ResourcePackList repository;
    private final List<ResourcePackInfo> selectedPacks;
    private final List<ResourcePackInfo> unselectedPacks;
    private final Function<ResourcePackInfo, ResourceLocation> iconGetter;
    private final Runnable onListChanged;
    private final Consumer<ResourcePackList> output;

    public MUIPackLoader(Runnable onChanged, Function<ResourcePackInfo, ResourceLocation> iconGetter, ResourcePackList packRepository, Consumer<ResourcePackList> outputList) {
        this.onListChanged = onChanged;
        this.iconGetter = iconGetter;
        this.repository = packRepository;
        this.selectedPacks = Lists.newArrayList(packRepository.getSelectedPacks());
        Collections.reverse(this.selectedPacks);
        this.unselectedPacks = Lists.newArrayList(packRepository.getAvailablePacks());
        this.unselectedPacks.removeAll(this.selectedPacks);
        this.output = outputList;
    }

    public Stream<IPack> getUnselectedPacks() {
        return this.unselectedPacks.stream().map(DisabledPack::new);
    }

    public Stream<IPack> getSelectedPacks() {
        return this.selectedPacks.stream().map(EnabledPack::new);
    }

    public void commit() {
        this.repository.setSelected(Lists.reverse(this.selectedPacks).stream().map(ResourcePackInfo::getId).collect(ImmutableList.toImmutableList()));
        this.output.accept(this.repository);
    }

    public void findNewPacks() {
        this.repository.reload();
        this.selectedPacks.retainAll(this.repository.getAvailablePacks());
        this.unselectedPacks.clear();
        this.unselectedPacks.addAll(this.repository.getAvailablePacks());
        this.unselectedPacks.removeAll(this.selectedPacks);
    }

    @OnlyIn(Dist.CLIENT)
    abstract class AbstractPack implements IPack {
        private final ResourcePackInfo pack;

        public AbstractPack(ResourcePackInfo packInfo) {
            this.pack = packInfo;
        }

        protected abstract List<ResourcePackInfo> getSelfList();

        protected abstract List<ResourcePackInfo> getOtherList();

        public ResourceLocation getIconTexture() {
            return MUIPackLoader.this.iconGetter.apply(this.pack);
        }

        public PackCompatibility getCompatibility() {
            return this.pack.getCompatibility();
        }

        public ITextComponent getTitle() {
            return this.pack.getTitle();
        }

        public ITextComponent getDescription() {
            return this.pack.getDescription();
        }

        public IPackNameDecorator getPackSource() {
            return this.pack.getPackSource();
        }

        public boolean isFixedPosition() {
            return this.pack.isFixedPosition();
        }

        public boolean isRequired() {
            return this.pack.isRequired();
        }

        protected void toggleSelection() {
            this.getSelfList().remove(this.pack);
            this.pack.getDefaultPosition().insert(this.getOtherList(), this.pack, Function.identity(), true);
            MUIPackLoader.this.onListChanged.run();
        }

        protected void move(int offset) {
            List<ResourcePackInfo> packInfos = this.getSelfList();
            int packIndex = packInfos.indexOf(this.pack);
            packInfos.remove(packIndex);
            packInfos.add(packIndex + offset, this.pack);
            MUIPackLoader.this.onListChanged.run();
        }

        public boolean canMoveUp() {
            List<ResourcePackInfo> packInfos = this.getSelfList();
            int packIndex = packInfos.indexOf(this.pack);
            return packIndex > 0 && !packInfos.get(packIndex - 1).isFixedPosition();
        }

        public void moveUp() {
            this.move(-1);
        }

        public boolean canMoveDown() {
            List<ResourcePackInfo> packInfos = this.getSelfList();
            int packIndex = packInfos.indexOf(this.pack);
            return packIndex >= 0 && packIndex < packInfos.size() - 1 && !packInfos.get(packIndex + 1).isFixedPosition();
        }

        public void moveDown() {
            this.move(1);
        }

        @Override
        public boolean notHidden() {
            return !pack.isHidden();
        }
    }

    @OnlyIn(Dist.CLIENT)
    class DisabledPack extends AbstractPack {
        public DisabledPack(ResourcePackInfo packInfo) {
            super(packInfo);
        }

        protected List<ResourcePackInfo> getSelfList() {
            return MUIPackLoader.this.unselectedPacks;
        }

        protected List<ResourcePackInfo> getOtherList() {
            return MUIPackLoader.this.selectedPacks;
        }

        public boolean isSelected() {
            return false;
        }

        public void select() {
            this.toggleSelection();
        }

        public void unselect() {}
    }

    @OnlyIn(Dist.CLIENT)
    class EnabledPack extends AbstractPack {
        public EnabledPack(ResourcePackInfo packInfo) {
            super(packInfo);
        }

        protected List<ResourcePackInfo> getSelfList() {
            return MUIPackLoader.this.selectedPacks;
        }

        protected List<ResourcePackInfo> getOtherList() {
            return MUIPackLoader.this.unselectedPacks;
        }

        public boolean isSelected() {
            return true;
        }

        public void select() {}

        public void unselect() {
            this.toggleSelection();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public interface IPack {
        ResourceLocation getIconTexture();

        PackCompatibility getCompatibility();

        ITextComponent getTitle();

        ITextComponent getDescription();

        IPackNameDecorator getPackSource();

        default ITextComponent getExtendedDescription() {
            return this.getPackSource().decorate(this.getDescription());
        }

        boolean isFixedPosition();

        boolean isRequired();

        void select();

        void unselect();

        void moveUp();

        void moveDown();

        boolean isSelected();

        default boolean canSelect() {
            return !this.isSelected();
        }

        default boolean canUnselect() {
            return this.isSelected() && !this.isRequired();
        }

        boolean canMoveUp();

        boolean canMoveDown();

        default boolean notHidden() {
            return true;
        }
    }
}
