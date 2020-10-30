package gtr.integration.multi.crafttweaker.functions;

import crafttweaker.annotations.ZenRegister;
import gtr.integration.multi.crafttweaker.gtwrap.interfaces.IControllerTile;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import javax.annotation.Nonnull;

/**
 * Called when the controller block is about to be removed.
 */
@FunctionalInterface
@ZenClass("mods.gregtech.multiblock.functions.IRemovalFunction")
@ZenRegister
public interface IRemovalFunction {

    /**
     * Implement this with a function.
     *
     * @param controller The controller about to be removed.
     */
    @ZenMethod
    void onRemoval(@Nonnull IControllerTile controller);

}
