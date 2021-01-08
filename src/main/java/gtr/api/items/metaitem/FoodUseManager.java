package gtr.api.items.metaitem;

import gtr.api.items.metaitem.stats.IFoodBehavior;
import gtr.api.items.metaitem.stats.IItemBehaviour;
import gtr.api.items.metaitem.stats.IItemUseManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;

public class FoodUseManager implements IItemBehaviour, IItemUseManager {

    private final IFoodBehavior foodStats;

    public FoodUseManager(IFoodBehavior foodStats) {
        this.foodStats = foodStats;
    }

    public IFoodBehavior getFoodStats() {
        return foodStats;
    }

    @Override
    public void onItemUseStart(ItemStack stack, EntityPlayer player) {
    }

    @Override
    public boolean canStartUsing(ItemStack stack, EntityPlayer player) {
        return player.getFoodStats().needFood() || foodStats.alwaysEdible(stack, player);
    }

    @Override
    public EnumAction getUseAction(ItemStack itemStack) {
        return foodStats.getFoodAction(itemStack);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack itemStack) {
        return 32;
    }

    @Override
    public void onItemUsingTick(ItemStack stack, EntityPlayer player, int count) {
    }

    @Override
    public void onPlayerStoppedItemUsing(ItemStack stack, EntityPlayer player, int timeLeft, World w) {
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, EntityPlayer player) {
        stack.shrink(1);
        player.getFoodStats().addStats(foodStats.getFoodLevel(stack, player), foodStats.getSaturation(stack, player));
        foodStats.onEaten(stack, player);
        return stack;
    }

    @Override
    public void addInformation(ItemStack itemStack, List<String> lines) {
        foodStats.addInformation(itemStack, lines);
    }
}
