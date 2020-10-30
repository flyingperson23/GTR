package gtr.integration.multi.crafttweaker.functions;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.formatting.IFormattedText;
import gtr.integration.multi.crafttweaker.gtwrap.interfaces.IControllerTile;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Called when adding text to the multiblock UI.
 */
@FunctionalInterface
@ZenClass("mods.gregtech.multiblock.functions.IDisplayTextFunction")
@ZenRegister
public interface IDisplayTextFunction {

    /**
     * Implement this with a function.
     *
     * @param controller The multiblock controller.
     * @return A list of {@link IFormattedText}s to add to the UI.
     */
    @ZenMethod
    List<IFormattedText> addDisplayText(@Nonnull IControllerTile controller);

}
