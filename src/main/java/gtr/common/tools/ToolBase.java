package gtr.common.tools;

import gtr.api.items.toolitem.IToolStats;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;

public abstract class ToolBase implements IToolStats {

    @Override
    public boolean canMineBlock(IBlockState block, ItemStack stack) {
        return false;
    }

    @Override
    public boolean canApplyEnchantment(ItemStack stack, Enchantment enchantment) {
        return false;
    }

    @Override
    public int getToolDamagePerBlockBreak(ItemStack stack) {
        return 1;
    }

    @Override
    public int getToolDamagePerDropConversion(ItemStack stack) {
        return 1;
    }

    @Override
    public int getToolDamagePerContainerCraft(ItemStack stack) {
        return 8;
    }

    @Override
    public int getToolDamagePerEntityAttack(ItemStack stack) {
        return 2;
    }

}
