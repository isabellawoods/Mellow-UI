package melonystudios.mellowui.element.panel;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import melonystudios.mellowui.backport.cursor.CursorTypes;
import melonystudios.mellowui.element.RenderComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FocusableGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.List;

/// A **panel** is essentially a {@linkplain net.minecraft.client.gui.widget.list.AbstractList list} but focused on text and images.
@OnlyIn(Dist.CLIENT)
public class Panel extends FocusableGui implements IRenderable {
    protected final RenderComponents components = RenderComponents.INSTANCE;
    protected final Minecraft minecraft = Minecraft.getInstance();
    protected final FontRenderer font = this.minecraft.font;
    public final Screen parentScreen;
    protected final ITextComponent title;
    public final int x;
    public final int y;
    public final int width;
    public final int height;
    private boolean scrolling;
    private double scrollAmount;
    private final int barWidth = 6;
    private final int barLeft;
    protected List<PanelEntry> entries = Lists.newArrayList();
    protected final List<IGuiEventListener> children = Lists.newArrayList();
    protected final List<Widget> widgets = Lists.newArrayList();

    /// A **panel** is essentially a {@linkplain net.minecraft.client.gui.widget.list.AbstractList list} but focused on text and images.
    /// @param x The x position of this panel.
    /// @param y The y position of this panel.
    /// @param width The width of this panel.
    /// @param height The height of this panel.
    /// @param parentScreen The screen this panel is located in.
    /// @param title The name of this panel.
    public Panel(int x, int y, int width, int height, Screen parentScreen, ITextComponent title) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.parentScreen = parentScreen;
        this.title = title;
        this.barLeft = this.width - this.x / 2 - 1;
    }

    /// Renders the contents of this panel.
    /// @param relativeY The relative y-coordinate of this panel, used when scrolling upwards.
    /// @param mouseX The x-coordinate of the mouse cursor.
    /// @param mouseY The y-coordinate of the mouse cursor.
    /// @param partialTicks The partial tick time.
    public void renderContents(int relativeY, int mouseX, int mouseY, float partialTicks) {
        int yOffset = relativeY + 3;

        for (PanelEntry entry : this.entries) {
            entry.renderEntry(this.components, this.x + 3, yOffset, this.width - (this.getMaxScroll() > 0 ? 2 : 0), this.height);
            yOffset += entry.getContentHeight() + 3;
        }
    }

     /// Renders the graphical user interface (GUI) element.
     /// @param stack The {@link MatrixStack} used for rendering.
    /// @param mouseX The x-coordinate of the mouse cursor.
    /// @param mouseY The y-coordinate of the mouse cursor.
    /// @param partialTicks The partial tick time.
    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        int relativeY = (int) (this.y - this.scrollAmount);

        // Panel entries
        this.components.enableScissor(this.x, this.y, this.width, this.height + this.y);
        this.renderContents(relativeY, mouseX, mouseY, partialTicks);
        for (Widget widget : this.widgets) widget.render(stack, mouseX, mouseY, partialTicks);
        this.components.disableScissor();

        // Scroller
        int maxScroll = this.getMaxScroll();
        int y1 = this.y + this.height;
        if (maxScroll > 0) {
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuilder();
            RenderSystem.enableBlend();
            RenderSystem.disableTexture();
            int scrollX0 = this.getScrollbarPosition(); // checkerzon e checkerzinho ~sophie 27-9-25
            int scrollX1 = scrollX0 + this.barWidth;
            int scrollY0 = this.y;
            int i1 = (int) ((float) ((y1 - scrollY0) * (y1 - scrollY0)) / (float) this.getContentHeight());
            i1 = MathHelper.clamp(i1, 32, y1 - scrollY0 - 8);
            int i2 = (int) (this.scrollAmount * (y1 - scrollY0 - i1) / maxScroll + scrollY0);
            if (i2 < scrollY0) i2 = scrollY0;

            buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            buffer.vertex(scrollX0 + 1, y1, 0).uv(0, 1).color(0, 0, 0, 191).endVertex();
            buffer.vertex(scrollX1 - 1, y1, 0).uv(1, 1).color(0, 0, 0, 191).endVertex();
            buffer.vertex(scrollX1 - 1, scrollY0, 0).uv(1, 0).color(0, 0, 0, 191).endVertex();
            buffer.vertex(scrollX0 + 1, scrollY0, 0).uv(0, 0).color(0, 0, 0, 191).endVertex();
            buffer.vertex(scrollX0, (i2 + i1), 0).uv(0, 1).color(128, 128, 128, 191).endVertex();
            buffer.vertex(scrollX1, (i2 + i1), 0).uv(1, 1).color(128, 128, 128, 191).endVertex();
            buffer.vertex(scrollX1, i2, 0).uv(1, 0).color(128, 128, 128, 255).endVertex();
            buffer.vertex(scrollX0, i2, 0).uv(0, 0).color(128, 128, 128, 255).endVertex();
            buffer.vertex(scrollX0, (i2 + i1 - 1), 0).uv(0, 1).color(192, 192, 192, 255).endVertex();
            buffer.vertex((scrollX1 - 1), (i2 + i1 - 1), 0).uv(1, 1).color(192, 192, 192, 255).endVertex();
            buffer.vertex((scrollX1 - 1), i2, 0).uv(1, 0).color(192, 192, 192, 255).endVertex();
            buffer.vertex(scrollX0, i2, 0).uv(0, 0).color(192, 192, 192, 255).endVertex();
            tessellator.end();
            RenderSystem.disableBlend();
        }

        // Cursor
        if (maxScroll > 0 && this.isWithinScrollerArea(mouseX, mouseY)) this.components.requestCursor(this.scrolling ? CursorTypes.RESIZE_NS : CursorTypes.POINTING_HAND);
    }

    protected <T extends Widget> T addWidget(T widget) {
        this.widgets.add(widget);
        return this.addEntry(widget);
    }

    protected <T extends IGuiEventListener> T addEntry(T entry) {
        this.children.add(entry);
        return entry;
    }

    public void init() {}

    @Override
    @Nonnull
    public List<? extends IGuiEventListener> children() {
        return this.children;
    }

    public List<Widget> widgets() {
        return this.widgets;
    }

    public Minecraft getMinecraft() {
        return this.minecraft;
    }

    public FontRenderer getFont() {
        return this.font;
    }

    public <T extends PanelEntry> boolean addEntry(T entry) {
        return this.entries.add(entry);
    }

    public void clearEntries() {
        this.entries.clear();
    }

    public void setScrollAmount(double scrollAmount) {
        this.scrollAmount = scrollAmount;
    }

    public int getMaxScroll() {
        return MathHelper.clamp(this.getContentHeight() - this.height, 0, Integer.MAX_VALUE);
    }

    /// Gets the total height, in **Minecraft Pixels** (`mpx`), of all the content being rendered.
    ///
    /// This is different from the {@linkplain PanelEntry#getContentHeight() entry's `getContentHeight()`} as it is made to better match the scroll height in-game.
    public int getContentHeight() {
        int height = 0;
        for (PanelEntry entry : this.entries) height += entry.getContentHeight();
        return height + 32;
    }

    private void applyScrollLimits() {
        int maxScroll = this.getMaxScroll();
        if (maxScroll < 0) maxScroll /= 2;

        if (this.scrollAmount < 0) this.scrollAmount = 0;
        if (this.scrollAmount > maxScroll) this.scrollAmount = maxScroll;
    }

    public int getScrollAmount() {
        return 20;
    }

    protected int getScrollbarPosition() {
        return this.width - this.x / 2 - 1;
    }

    private boolean isWithinScrollerArea(int mouseX, int mouseY) {
        return mouseX >= this.getScrollbarPosition() && mouseY >= this.y && mouseX < this.getScrollbarPosition() + 6 && mouseY < this.y + this.height;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= (double) this.x && mouseY >= (double) this.y && mouseX < (double) (this.x + this.width) && mouseY < (double) (this.y + this.height);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
        if (scroll != 0) {
            this.scrollAmount += -scroll * this.getScrollAmount();
            this.applyScrollLimits();
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) return true;

        this.scrolling = button == 0 && mouseX >= this.barLeft && mouseX < this.barLeft + this.barWidth;
        if (this.scrolling) return true;

        if (mouseX >= (double) this.x && mouseY >= (double) this.y && mouseX < (double) (this.x + this.width) && mouseY < (double) (this.y + this.height)) {
            return this.clickPanel(mouseX, mouseY, button);
        }
        return false;
    }

    private boolean clickPanel(double mouseX, double mouseY, int button) {
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int i) {
        if (super.mouseReleased(mouseX, mouseY, i)) return true;
        boolean scrolling = this.scrolling;
        this.scrolling = false;
        return scrolling;
    }

    private int getBarHeight() {
        int barHeight = (this.height * this.height) / this.getContentHeight();
        if (barHeight < 32) barHeight = 32;
        if (barHeight > this.height - 4) barHeight = this.height - 4;
        return barHeight;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.scrolling) {
            int maxScroll = this.height - this.getBarHeight();
            double moved = deltaY / maxScroll;
            this.scrollAmount += this.getMaxScroll() * moved;
            this.applyScrollLimits();
            return true;
        }
        return false;
    }
}
