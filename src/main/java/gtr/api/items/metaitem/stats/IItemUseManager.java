package gtr.api.items.metaitem.stats;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IItemUseManager extends IItemComponent {

    default boolean canStartUsing(ItemStack stack, EntityPlayer player) {
        return true;
    }

    default void onItemUseStart(ItemStack stack, EntityPlayer player) {}

    EnumAction getUseAction(ItemStack stack);

    int getMaxItemUseDuration(ItemStack stack);

    default void onItemUsingTick(ItemStack stack, EntityPlayer player, int count) {}

    default void onPlayerStoppedItemUsing(ItemStack stack, EntityPlayer player, int timeLeft, World world) {}

    default ItemStack onItemUseFinish(ItemStack stack, EntityPlayer player) {
        return stack;
    }
}
