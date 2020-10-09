package gtr.common.pipelike.cable.tile;

import gtr.api.capability.GregtechCapabilities;
import gtr.api.capability.IEnergyContainer;
import gtr.api.pipenet.block.material.TileEntityMaterialPipeBase;
import gtr.common.ConfigHolder;
import gtr.common.pipelike.cable.Insulation;
import gtr.common.pipelike.cable.WireProperties;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;

public class TileEntityCable extends TileEntityMaterialPipeBase<Insulation, WireProperties> {

    private IEnergyContainer energyContainer;

    private IEnergyContainer getEnergyContainer() {
        if (energyContainer == null) {
            energyContainer = new CableEnergyContainer(this);
        }
        return energyContainer;
    }

    @Override
    public Class<Insulation> getPipeTypeClass() {
        return Insulation.class;
    }

    @Override
    public boolean supportsTicking() {
        return false;
    }

    @Nullable
    @Override
    public <T> T getCapabilityInternal(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == GregtechCapabilities.CAPABILITY_ENERGY_CONTAINER) {
            return GregtechCapabilities.CAPABILITY_ENERGY_CONTAINER.cast(getEnergyContainer());
        }
        return super.getCapabilityInternal(capability, facing);
    }

}