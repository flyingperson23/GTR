package gtr.integration.theoneprobe.provider;

import gtr.api.capability.GregtechTileCapabilities;
import gtr.api.capability.IControllable;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.TextStyleClass;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

public class ControllableInfoProvider extends CapabilityInfoProvider<IControllable> {

    @Override
    protected Capability<IControllable> getCapability() {
        return GregtechTileCapabilities.CAPABILITY_CONTROLLABLE;
    }

    @Override
    public String getID() {
        return "gtr:controllable_provider";
    }

    @Override
    protected void addProbeInfo(IControllable capability, IProbeInfo probeInfo, TileEntity tileEntity, EnumFacing sideHit) {
        if (!capability.isWorkingEnabled()) {
            probeInfo.text(TextStyleClass.INFOIMP + "{*gtr.top.working_disabled*}");
        }
    }
}
