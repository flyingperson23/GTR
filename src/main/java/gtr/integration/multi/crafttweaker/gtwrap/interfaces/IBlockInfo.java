package gtr.integration.multi.crafttweaker.gtwrap.interfaces;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.block.IBlock;
import crafttweaker.api.block.IBlockState;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.world.IFacing;
import gtr.api.util.BlockInfo;
import gtr.integration.multi.crafttweaker.construction.MultiblockShapeInfoBuilder;
import gtr.integration.multi.crafttweaker.gtwrap.impl.MCBlockInfo;
import net.minecraft.util.EnumFacing;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenProperty;

import javax.annotation.Nonnull;

/**
 * This is used to define what block should show in the JEI preview.
 *
 * It is used in {@link MultiblockShapeInfoBuilder#where(String, IBlockInfo)}.
 *
 * @see MultiblockShapeInfoBuilder
 * @see IMultiblockShapeInfo
 */
@ZenClass("mods.gregtech.multiblock.IBlockInfo")
@ZenRegister
public interface IBlockInfo {

    @Nonnull
    BlockInfo getInternal();

    /**
     * Get an {@link IBlockInfo} from a block.
     *
     * Equivalent to {@code block as IBlockInfo}.
     *
     * @param block The block to display.
     * @return An {@link IBlockInfo} that represents the given block.
     */
    @ZenMethod
    static IBlockInfo fromBlock(@Nonnull IBlock block) {
        return new MCBlockInfo(new BlockInfo(CraftTweakerMC.getBlock(block.getDefinition())));
    }

    /**
     * Get an {@link IBlockInfo} from a block state.
     *
     * Equivalent to {@code state as IBlockInfo}.
     *
     * @param state The block state to display.
     * @return An {@link IBlockInfo} that represents the given block.
     */
    @ZenMethod
    static IBlockInfo fromState(@Nonnull IBlockState state) {
        return new MCBlockInfo(new BlockInfo(CraftTweakerMC.getBlockState(state)));
    }

    /**
     * Get an {@link IBlockInfo} from a controller's string ID.
     *
     * @param id The id of the meta tile entity to display.
     * @param facing (Optional) The direction this should face. {@code IFacing.west()} by default.
     * @return An {@link IBlockInfo} that represents the given block.
     */
    @ZenMethod
    static IBlockInfo controller(String id, @Optional @Nonnull IFacing facing) {
        return new MCBlockInfo(new MCBlockInfo.ControllerInfo(
                java.util.Optional.ofNullable(facing)
                        .map(IFacing::getInternal)
                        .filter(EnumFacing.class::isInstance)
                        .map(EnumFacing.class::cast)
                        .orElse(EnumFacing.WEST),
                id));
    }

    /**
     * An {@link IBlockInfo} that displays nothing.
     */
    @ZenProperty IBlockInfo EMPTY = new MCBlockInfo(BlockInfo.EMPTY);

}
