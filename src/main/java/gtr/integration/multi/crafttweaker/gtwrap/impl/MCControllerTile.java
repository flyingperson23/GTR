package gtr.integration.multi.crafttweaker.gtwrap.impl;

import crafttweaker.api.data.IData;
import gtr.integration.multi.crafttweaker.CustomMultiblock;
import gtr.integration.multi.crafttweaker.gtwrap.interfaces.*;
import gtr.integration.multi.gregtech.tile.TileControllerCustom;

import javax.annotation.Nonnull;

public class MCControllerTile extends MCMetaTileEntity implements IControllerTile {

    public MCControllerTile(@Nonnull TileControllerCustom inner) {
        super(inner);
    }

    @Override
    @Nonnull
    public TileControllerCustom getInternal() {
        return (TileControllerCustom) super.getInternal();
    }

    @Override
    public CustomMultiblock getMultiblock() {
        return getInternal().multiblock;
    }

    @Override
    public IIEnergyContainer getEnergyContainer() {
        return new MCIEnergyContainer(getInternal().getEnergyContainer());
    }


    @Override
    public IIItemHandlerModifiable getInputInventory() {
        return new MCIItemHandlerModifiable(getInternal().getInputInventory());
    }

    @Override
    public IIItemHandlerModifiable getOutputInventory() {
        return new MCIItemHandlerModifiable(getInternal().getOutputInventory());
    }

    @Override
    public IIMultipleTankHandler getInputFluidInventory() {
        return new MCIMultipleTankHandler(getInternal().getInputFluidInventory());
    }

    @Override
    public IIMultipleTankHandler getOutputFluidInventory() {
        return new MCIMultipleTankHandler(getInternal().getOutputFluidInventory());
    }

    @Override
    public void invalidateStructure() {
        getInternal().invalidateStructure();
    }

    @Override
    public void update() {
        getInternal().update();
    }

    @Override
    public Object[] getAbilities(IMultiblockAbility ability) {
        return getInternal().getAbilities(ability.getInternal()).toArray();
    }

    @Override
    public boolean isStructureFormed() {
        return getInternal().isStructureFormed();
    }

    @Override
    public IData getExtraData() {
        return getInternal().persistentData;
    }

    @Override
    public void setExtraData(IData data) {
        getInternal().persistentData = data;
    }

}
