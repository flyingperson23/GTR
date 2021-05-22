package gtr.common.metatileentities.multi.electric.generator;


import gtr.api.metatileentity.multiblock.MultiblockAbility;
import gtr.api.recipes.machines.FuelRecipeMap;
import gtr.common.metatileentities.electric.multiblockpart.MetaTileEntityRotorHolder;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public abstract class RotorHolderMultiblockController extends FueledMultiblockController {

    public static final MultiblockAbility<MetaTileEntityRotorHolder> ABILITY_ROTOR_HOLDER = new MultiblockAbility<>();

    public RotorHolderMultiblockController(ResourceLocation metaTileEntityId, FuelRecipeMap recipeMap, long maxVoltage) {
        super(metaTileEntityId, recipeMap, maxVoltage);
    }

    public MetaTileEntityRotorHolder getRotorHolder() {
        return getAbilities(ABILITY_ROTOR_HOLDER).get(0);
    }

    public List<MetaTileEntityRotorHolder> getRotorHolders() {
        return getAbilities(ABILITY_ROTOR_HOLDER);
    }

    @Override
    protected void updateFormedValid() {
        if (isRotorFaceFree()) {
            super.updateFormedValid();
        }
    }

    @Override
    public void invalidateStructure() {
        getRotorHolder().resetRotorSpeed();
        super.invalidateStructure();
    }

    /**
     * @return true if turbine is formed and it's face is free and contains
     * only air blocks in front of rotor holder
     */
    public boolean isRotorFaceFree() {
        return isStructureFormed() && getAbilities(ABILITY_ROTOR_HOLDER).get(0).isFrontFaceFree();
    }

    /**
     * @return true if structure formed, workable is active and front face is free
     */
    public boolean isActive() {
        return isRotorFaceFree() && workableHandler.isActive() && workableHandler.isWorkingEnabled();
    }

    public abstract int getRotorSpeedIncrement();

    public abstract int getRotorSpeedDecrement();
}