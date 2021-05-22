package gtr.common.tools;

import gtr.api.capability.GregtechCapabilities;
import gtr.api.items.toolitem.IToolStats;
import gtr.api.items.toolitem.ToolHooks;
import gtr.api.items.toolitem.ToolMetaItem;
import gtr.api.items.toolitem.ToolMetaItem.MetaToolValueItem;
import gtr.api.unification.OreDictUnifier;
import gtr.api.unification.material.type.SolidMaterial;
import gtr.api.util.function.Task;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLog;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class TreeChopTask implements Task {

    private static final int MAX_BLOCKS_SEARCH_PER_TICK = 1024;
    private static final int MAX_BLOCKS_TO_SEARCH = 8192;
    private final Stack<Pair<MultiFacing, Boolean>> moveStack = new Stack<>();
    private boolean isLastBlockLeaves = false;
    private final MutableBlockPos currentPos = new MutableBlockPos();
    private final BlockPos startBlockPos;
    private final Set<BlockPos> visitedBlockPos = new HashSet<>();
    private final List<BlockPos> woodBlockPos = new ArrayList<>();

    private boolean finishedSearchingBlocks = false;
    private int currentWoodBlockIndex = 0;
    private final World world;
    private final EntityPlayerMP player;
    private final ItemStack itemStack;

    public TreeChopTask(BlockPos startPos, World world, EntityPlayerMP player, ItemStack toolStack) {
        this.startBlockPos = startPos.toImmutable();
        this.currentPos.setPos(startPos);
        this.woodBlockPos.add(startPos.toImmutable());
        this.world = world;
        this.itemStack = toolStack.copy();
        this.player = player;
    }

    @Override
    public boolean run() {
        ItemStack itemInMainHand = this.player.getHeldItemMainhand();

        boolean isPlayerNear = player.world == world && currentPos.distanceSq(player.posX, currentPos.getY(), player.posZ) <= 1024;
        boolean isPlayerConnected = player.connection.netManager.isChannelOpen() && isPlayerNear;
        if (!isPlayerConnected || itemInMainHand.isEmpty() || !isItemEqual(itemInMainHand)) {
            return false;
        }

        ToolMetaItem<?> toolMetaItem = (ToolMetaItem<?>) itemStack.getItem();
        MetaToolValueItem toolValueItem = toolMetaItem.getItem(itemStack);
        if (toolValueItem == null) {
            return false;
        }
        IToolStats toolStats = toolValueItem.getToolStats();
        int damagePerBlockBreak = toolStats.getToolDamagePerBlockBreak(itemStack);

        if(!finishedSearchingBlocks) {
            this.finishedSearchingBlocks = !attemptSearchWoodBlocks() ||
                this.visitedBlockPos.size() >= MAX_BLOCKS_TO_SEARCH;
            if(finishedSearchingBlocks) {
                this.woodBlockPos.sort(new WoodBlockComparator());
            }
            return true;
        }
        if(toolMetaItem.isUsable(itemInMainHand, damagePerBlockBreak) && tryBreakAny()) {
            toolMetaItem.damageItem(itemInMainHand, damagePerBlockBreak, false);
            return true;
        }
        return false;
    }

    private class WoodBlockComparator implements Comparator<BlockPos> {

        @Override
        public int compare(BlockPos o1, BlockPos o2) {
            int a = -Integer.compare(o1.getY(), o2.getY());
            if(a != 0) {
                return a;
            }
            return Integer.compare(distance(o1), distance(o2));
        }

        private int distance(BlockPos pos) {
            int diffX = pos.getX() - startBlockPos.getX();
            int diffZ = pos.getZ() - startBlockPos.getZ();
            return diffX * diffX + diffZ * diffZ;
        }
    }

    private boolean isItemEqual(ItemStack heldItem) {
        if (heldItem.getItem() != itemStack.getItem() ||
            heldItem.getItemDamage() != itemStack.getItemDamage() ||
            !(heldItem.getItem() instanceof ToolMetaItem<?>)) {
            return false;
        }
        SolidMaterial heldToolMaterial = ToolMetaItem.getToolMaterial(heldItem);
        SolidMaterial toolMaterial = ToolMetaItem.getToolMaterial(itemStack);
        return toolMaterial == heldToolMaterial;
    }

    private boolean tryBreakAny() {
        if(woodBlockPos.size() > currentWoodBlockIndex) {
            BlockPos woodPos = woodBlockPos.get(currentWoodBlockIndex++);
            IBlockState blockState = this.world.getBlockState(woodPos);
            if(isLogBlock(blockState) == 1) {
                destroyBlock(woodPos, itemStack.hasCapability(GregtechCapabilities.CAPABILITY_ELECTRIC_ITEM, null));
                return true;
            }
            return true;
        }
        return false;
    }

    public boolean destroyBlock(BlockPos pos, boolean inInv) {
        IBlockState iblockstate = this.world.getBlockState(pos);
        Block block = iblockstate.getBlock();

        if (block.isAir(iblockstate, this.world, pos)) {
            return false;
        } else {
            this.world.playEvent(2001, pos, Block.getStateId(iblockstate));

            if (inInv) {
                NonNullList<ItemStack> drops = NonNullList.create();
                block.getDrops(drops, world, pos, iblockstate, 0);
                for (ItemStack s : drops) {
                    ToolHooks.giveItemToPlayerSilent(player, s, -1);
                }
            } else {
                block.dropBlockAsItem(this.world, pos, iblockstate, 0);
            }

            return this.world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
        }
    }

    private boolean attemptSearchWoodBlocks() {
        int blocksSearchedNow = 0;
        int validWoodBlocksFound = 0;
        main: while (blocksSearchedNow <= MAX_BLOCKS_SEARCH_PER_TICK) {
            //try to iterate neighbour blocks
            blocksSearchedNow++;
            for (MultiFacing facing : MultiFacing.VALUES) {
                //move at facing
                facing.move(currentPos);

                if(!visitedBlockPos.contains(currentPos)) {
                    IBlockState blockState = this.world.getBlockState(currentPos);
                    int blockType = isLogBlock(blockState);
                    boolean currentIsLeavesBlock = isLastBlockLeaves;
                    if (blockType == 1 || (blockType == 2 && !isLastBlockLeaves)) {
                        this.isLastBlockLeaves = blockType == 2;
                        BlockPos immutablePos = currentPos.toImmutable();
                        this.visitedBlockPos.add(immutablePos);
                        if(blockType == 1) {
                            this.woodBlockPos.add(immutablePos);
                        }
                        validWoodBlocksFound++;
                        moveStack.add(Pair.of(facing.getOpposite(), currentIsLeavesBlock));
                        continue main;
                    }
                }

                //move back if it wasn't a tree block
                facing.getOpposite().move(currentPos);
            }
            //we didn't found any matching block in neighbours - move back
            if (!moveStack.isEmpty()) {
                Pair<MultiFacing, Boolean> prevData = moveStack.pop();
                prevData.getLeft().move(currentPos);
                this.isLastBlockLeaves = prevData.getRight();
            } else break;
        }
        return validWoodBlocksFound > 0;
    }

    public static int isLogBlock(IBlockState blockState) {
        if(blockState.getMaterial() == Material.AIR) {
            return 0;
        }
        if(blockState.getBlock() instanceof BlockLog) {
            return 1;
        } else if(blockState.getBlock() instanceof BlockLeaves) {
            return 2;
        }
        Item itemBlock = Item.getItemFromBlock(blockState.getBlock());
        ItemStack blockStack = new ItemStack(itemBlock, 1, blockState.getBlock().damageDropped(blockState));
        Set<String> blocks = OreDictUnifier.getOreDictionaryNames(blockStack);
        if(blocks.contains("logWood")) {
            return 1;
        } else if(blocks.contains("treeLeaves")) {
            return 2;
        } else return 0;
    }

    private enum MultiFacing {
        DOWN(1, new Vec3i(0, -1, 0)),
        UP(0, new Vec3i(0, 1, 0)),
        NORTH(3, new Vec3i(0, 0, -1)),
        SOUTH(2, new Vec3i(0, 0, 1)),
        WEST(5, new Vec3i(-1, 0, 0)),
        EAST(4, new Vec3i(1, 0, 0)),

        NORTH_DOWN(11, new Vec3i(0, -1, -1)),
        SOUTH_DOWN(10, new Vec3i(0, -1, 1)),
        WEST_DOWN(13, new Vec3i(-1, -1, 0)),
        EAST_DOWN(12, new Vec3i(1, -1, 0)),

        NORTH_UP(7, new Vec3i(0, 1, -1)),
        SOUTH_UP(6, new Vec3i(0, 1, 1)),
        WEST_UP(9, new Vec3i(-1, 1, 0)),
        EAST_UP(8, new Vec3i(1, 1, 0)),

        NORTH_WEST_DOWN(21, new Vec3i(-1, -1, -1)),
        NORTH_EAST_DOWN(20, new Vec3i(-1, -1, 1)),
        SOUTH_WEST_DOWN(19, new Vec3i(-1, -1, 1)),
        SOUTH_EAST_DOWN(18, new Vec3i(1, -1, 1)),

        NORTH_WEST_UP(17, new Vec3i(-1, 1, -1)),
        NORTH_EAST_UP(16, new Vec3i(1, 1, -1)),
        SOUTH_WEST_UP(15, new Vec3i(1, 1, -1)),
        SOUTH_EAST_UP(14, new Vec3i(1, 1, 1));

        private final int oppositeIndex;
        private final Vec3i direction;
        private static final MultiFacing[] VALUES = values();

        MultiFacing(int oppositeIndex, Vec3i direction) {
            this.oppositeIndex = oppositeIndex;
            this.direction = direction;
        }

        public void move(MutableBlockPos blockPos) {
            blockPos.setPos(blockPos.getX() + direction.getX(),
                blockPos.getY() + direction.getY(),
                blockPos.getZ() + direction.getZ());
        }

        public MultiFacing getOpposite() {
            return VALUES[oppositeIndex];
        }
    }

}