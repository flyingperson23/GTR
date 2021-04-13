package gtr.integration.energistics.gui.widgets;

import appeng.api.config.Upgrades;
import appeng.items.materials.ItemMaterial;
import gtr.integration.energistics.render.Textures;
import gtr.api.gui.widgets.SlotWidget;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AE2UpgradeSlotWidget extends CallbackSlotWidget {
    protected Upgrades[] validUpgrades;
    protected Upgrades insertedUpgrade;

    public AE2UpgradeSlotWidget(@Nonnull Upgrades... validUpgrades) {
        super();
        this.validUpgrades = validUpgrades;
    }

    @Nonnull
    @Override
    public SlotWidget createSlotWidget(int x, int y) {
        return super.createSlotWidget(x, y).setBackgroundTexture(Textures.getAE2Sprite(15, 13));
    }

    @Override
    public boolean validateItemStack(@Nonnull ItemStack stack) {
        Item item = stack.getItem();
        if (!(item instanceof ItemMaterial))
            return false;

        Upgrades itemUpgrade = ((ItemMaterial) item).getType(stack);

        if(itemUpgrade == null)
            return false;

        for(Upgrades validUpgrade : validUpgrades)
            if(itemUpgrade == validUpgrade)
                return true;

        return false;
    }

    @Override
    protected void setData(@Nonnull ItemStack slotStack) {
        insertedUpgrade = ((ItemMaterial) slotStack.getItem()).getType(slotStack);
    }

    @Override
    protected void clearData() {
        insertedUpgrade = null;
    }

    @Nullable
    public Upgrades getInsertedUpgrade() {
        return insertedUpgrade;
    }
}
