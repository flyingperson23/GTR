package gtr.integration.multi.crafttweaker.functions;

import crafttweaker.annotations.ZenRegister;
import gtr.integration.multi.crafttweaker.gtwrap.interfaces.IRecipeLogic;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import javax.annotation.Nonnull;

/**
 * Called every time the worktable updates, probably.
 */
@FunctionalInterface
@ZenClass("mods.gregtech.recipe.functions.IUpdateFunction")
@ZenRegister
public interface IUpdateFunction {

    /**
     * Implement this with a function.
     */
    @ZenMethod
    void run(@Nonnull IRecipeLogic logic);

}
