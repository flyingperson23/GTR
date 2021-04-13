package gtr.integration.energistics.gui.widgets;

import gtr.api.gui.GuiTextures;
import gtr.api.gui.IRenderContext;
import gtr.api.gui.resources.TextureArea;
import gtr.api.gui.widgets.SlotWidget;
import gtr.api.util.Position;
import gtr.api.util.Size;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;

public class BackgroundSlotWidget extends SlotWidget {
    protected TextureArea BorderTexture = GuiTextures.SLOT;
    public BackgroundSlotWidget(IItemHandler itemHandler, int slotIndex, int xPosition, int yPosition, boolean canTakeItems, boolean canPutItems) {
        super(itemHandler, slotIndex, xPosition, yPosition, canTakeItems, canPutItems);
    }

    @SuppressWarnings("unused")
    public BackgroundSlotWidget(IItemHandler itemHandler, int slotIndex, int xPosition, int yPosition) {
        this(itemHandler, slotIndex, xPosition, yPosition, true, true);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void drawInBackground(int mouseX, int mouseY, IRenderContext context) {
        if(!isEnabled())
            return;

        Position pos = getPosition();
        Size size = getSize();

        BorderTexture.draw(pos.x, pos.y, size.width, size.height);

        if(backgroundTexture == null)
            return;

        for (TextureArea backgroundTexture : this.backgroundTexture) {
            backgroundTexture.draw(pos.x + 1, pos.y + 1, size.width - 2, size.height - 2);
            backgroundTexture.draw(pos.x + 1, pos.y + 1, size.width - 2, size.height - 2);
        }
    }
}
