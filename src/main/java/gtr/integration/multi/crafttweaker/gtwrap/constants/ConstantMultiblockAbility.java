package gtr.integration.multi.crafttweaker.gtwrap.constants;

import crafttweaker.annotations.ZenRegister;
import gtr.api.metatileentity.multiblock.MultiblockAbility;
import gtr.integration.multi.crafttweaker.gtwrap.impl.MCMultiblockAbility;
import gtr.integration.multi.crafttweaker.gtwrap.interfaces.IMultiblockAbility;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenProperty;

@ZenClass("mods.gregtech.multiblock.MultiblockAbility")
@ZenRegister
public class ConstantMultiblockAbility {

    /** */ @ZenProperty public static final IMultiblockAbility EXPORT_ITEMS = new MCMultiblockAbility<>(MultiblockAbility.EXPORT_ITEMS);
    /** */ @ZenProperty public static final IMultiblockAbility IMPORT_ITEMS = new MCMultiblockAbility<>(MultiblockAbility.IMPORT_ITEMS);
    /** */ @ZenProperty public static final IMultiblockAbility EXPORT_FLUIDS = new MCMultiblockAbility<>(MultiblockAbility.EXPORT_FLUIDS);
    /** */ @ZenProperty public static final IMultiblockAbility IMPORT_FLUIDS = new MCMultiblockAbility<>(MultiblockAbility.IMPORT_FLUIDS);
    /** */ @ZenProperty public static final IMultiblockAbility INPUT_ENERGY = new MCMultiblockAbility<>(MultiblockAbility.INPUT_ENERGY);
    /** */ @ZenProperty public static final IMultiblockAbility OUTPUT_ENERGY = new MCMultiblockAbility<>(MultiblockAbility.OUTPUT_ENERGY);

}
