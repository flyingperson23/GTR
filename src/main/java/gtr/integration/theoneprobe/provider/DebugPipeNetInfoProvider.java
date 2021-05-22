package gtr.integration.theoneprobe.provider;

import gtr.api.metatileentity.MetaTileEntity;
import gtr.api.metatileentity.MetaTileEntityHolder;
import gtr.api.pipenet.Node;
import gtr.api.pipenet.PipeNet;
import gtr.api.pipenet.block.BlockPipe;
import gtr.api.pipenet.tile.IPipeTile;
import gtr.api.pipenet.tile.TileEntityPipeBase;
import gtr.common.ConfigHolder;
import gtr.common.pipelike.fluidpipe.BlockFluidPipe;
import gtr.common.pipelike.fluidpipe.tile.TileEntityFluidPipeTickable;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Map;

public class DebugPipeNetInfoProvider implements IProbeInfoProvider {
    @Override
    public String getID() {
        return "gtr:debug_pipe_net_provider";
    }

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        if (mode == ProbeMode.DEBUG && ConfigHolder.debug) {
            TileEntity tileEntity = world.getTileEntity(data.getPos());
            if (tileEntity instanceof MetaTileEntityHolder) {
                MetaTileEntity metaTileEntity = ((MetaTileEntityHolder) tileEntity).getMetaTileEntity();
                if (metaTileEntity != null) {
                    ArrayList<String> arrayList = new ArrayList<>();
                    arrayList.add("MetaTileEntity Id: " + metaTileEntity.metaTileEntityId);
                    metaTileEntity.addDebugInfo(arrayList);
                    arrayList.forEach(probeInfo::text);
                }
            }
            if (tileEntity instanceof TileEntityPipeBase) {
                IPipeTile<?, ?> pipeTile = (IPipeTile<?, ?>) tileEntity;
                BlockPipe<?, ?, ?> blockPipe = pipeTile.getPipeBlock();
                PipeNet<?> pipeNet = blockPipe.getWorldPipeNet(world).getNetFromPos(data.getPos());
                if (pipeNet != null) {
                    probeInfo.text("Net: " + pipeNet.hashCode());
                    probeInfo.text("Node Info: ");
                    StringBuilder builder = new StringBuilder();
                    Map<BlockPos, ? extends Node<?>> nodeMap = pipeNet.getAllNodes();
                    Node<?> node = nodeMap.get(data.getPos());
                    builder.append("{").append("active: ").append(node.isActive)
                        .append(", mark: ").append(node.mark)
                        .append(", blocked: ").append(node.blockedConnections).append("}");
                    probeInfo.text(builder.toString());
                }
                probeInfo.text("tile blocked: " + pipeTile.getBlockedConnections());
                if (blockPipe instanceof BlockFluidPipe) {
                    if (pipeTile instanceof TileEntityFluidPipeTickable) {
                        probeInfo.text("tile active: " + ((TileEntityFluidPipeTickable) pipeTile).isActive());
                    }
                }
            }
        }
    }
}