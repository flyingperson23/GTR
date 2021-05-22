package gtr.integration.jei.utils;

import gtr.api.items.metaitem.MetaItem;
import gtr.api.items.metaitem.MetaItem.MetaValueItem;
import mezz.jei.api.ISubtypeRegistry.ISubtypeInterpreter;
import net.minecraft.item.ItemStack;

public class MetaItemSubtypeHandler implements ISubtypeInterpreter {
    @Override
    public String apply(ItemStack itemStack) {
        MetaItem<?> metaItem = (MetaItem<?>) itemStack.getItem();
        MetaValueItem metaValueItem = metaItem.getItem(itemStack);
        String additionalData = "";
        if (metaValueItem != null) {
            additionalData = metaValueItem.getSubItemHandler().getItemSubType(itemStack);
        }
        return String.format("%d;%s", itemStack.getMetadata(), additionalData);
    }
}