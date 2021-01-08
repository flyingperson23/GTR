package gtr.api.recipes;

import com.google.common.base.Preconditions;
import gtr.api.GTValues;
import gtr.api.items.ToolDictNames;
import gtr.api.items.metaitem.MetaItem;
import gtr.api.recipes.recipes.DummyRecipe;
import gtr.api.unification.OreDictUnifier;
import gtr.api.unification.material.Materials;
import gtr.api.unification.material.type.Material;
import gtr.api.unification.ore.OrePrefix;
import gtr.api.unification.stack.MaterialStack;
import gtr.api.unification.stack.UnificationEntry;
import gtr.api.util.DummyContainer;
import gtr.api.util.GTLog;
import gtr.api.util.ShapedOreEnergyTransferRecipe;
import gtr.api.util.world.DummyWorld;
import gtr.common.MetaFluids;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ModHandler {

    /**
     * Returns if that Liquid is Water or Distilled Water
     */
    public static boolean isWater(FluidStack fluid) {
        return new FluidStack(FluidRegistry.WATER, 1).isFluidEqual(fluid)
            || new FluidStack(MetaFluids.DISTILLED_WATER, 1).isFluidEqual(fluid);
    }

    /**
     * Returns a Liquid Stack with given amount of Water.
     */
    public static FluidStack getWater(int amount) {
        return new FluidStack(FluidRegistry.WATER, amount);
    }

    /**
     * Returns a Liquid Stack with given amount of distilled Water.
     */
    public static FluidStack getDistilledWater(int amount) {
        return new FluidStack(MetaFluids.DISTILLED_WATER, amount);
    }

    public static FluidStack getPahoehoe(int amount) {
        return new FluidStack(MetaFluids.PAHOEHOE, amount);
    }

    /**
     * Returns if that Liquid is Lava
     */
    public static boolean isLava(FluidStack fluid) {
        return new FluidStack(FluidRegistry.LAVA, 0).isFluidEqual(fluid);
    }

    /**
     * Returns a Liquid Stack with given amount of Lava.
     */
    public static FluidStack getLava(int amount) {
        return new FluidStack(FluidRegistry.LAVA, amount);
    }

    /**
     * Returns if that Liquid is Steam
     */
    public static boolean isSteam(FluidStack fluid) {
        return getSteam(1).isFluidEqual(fluid);
    }

    /**
     * Returns a Liquid Stack with given amount of Steam.
     */
    public static FluidStack getSteam(int amount) {
        return Objects.requireNonNull(Materials.Steam.getFluid(amount));
    }

    public static boolean isMaterialWood(Material material) {
        return material == Materials.Wood;
    }

    public static ItemStack getBurningFuelRemainder(Random random, ItemStack fuelStack) {
        float remainderChance;
        ItemStack remainder;
        if (OreDictUnifier.getOreDictionaryNames(fuelStack).contains("fuelCoke")) {
            remainder = OreDictUnifier.get(OrePrefix.dust, Materials.Ash);
            remainderChance = 0.5f;
        } else {
            MaterialStack materialStack = OreDictUnifier.getMaterial(fuelStack);
            if (materialStack == null)
                return ItemStack.EMPTY;
            else if (materialStack.material == Materials.Charcoal) {
                remainder = OreDictUnifier.get(OrePrefix.dust, Materials.Ash);
                remainderChance = 0.3f;
            } else if (materialStack.material == Materials.Coal) {
                remainder = OreDictUnifier.get(OrePrefix.dust, Materials.DarkAsh);
                remainderChance = 0.35f;
            } else if (materialStack.material == Materials.Lignite) {
                remainder = OreDictUnifier.get(OrePrefix.dust, Materials.DarkAsh);
                remainderChance = 0.35f;
            } else if (materialStack.material == Materials.Coke) {
                remainder = OreDictUnifier.get(OrePrefix.dust, Materials.Ash);
                remainderChance = 0.5f;
            } else return ItemStack.EMPTY;
        }
        return random.nextFloat() <= remainderChance ? remainder : ItemStack.EMPTY;
    }

    ///////////////////////////////////////////////////
    //        Furnace Smelting Recipe Helpers        //
    ///////////////////////////////////////////////////

    public static void addSmeltingRecipe(UnificationEntry input, ItemStack output) {
        List<ItemStack> allStacks = OreDictUnifier.getAll(input);
        for (ItemStack inputStack : allStacks) {
            addSmeltingRecipe(inputStack, output);
        }
    }

    /**
     * Just simple Furnace smelting
     */
    public static void addSmeltingRecipe(ItemStack input, ItemStack output) {
        boolean skip = false;
        if (input.isEmpty()) {
            GTLog.logger.error("Input cannot be an empty ItemStack", new IllegalArgumentException());
            skip = true;
            RecipeMap.setFoundInvalidRecipe(true);
        }
        if (output.isEmpty()) {
            GTLog.logger.error("Output cannot be an empty ItemStack", new IllegalArgumentException());
            skip = true;
            RecipeMap.setFoundInvalidRecipe(true);
        }
        if (skip) return;
        FurnaceRecipes recipes = FurnaceRecipes.instance();

        if (recipes.getSmeltingResult(input).isEmpty()) {
            //register only if there is no recipe with duplicate input
            recipes.addSmeltingRecipe(input, output, 0.0f);
        }
    }

    ///////////////////////////////////////////////////
    //              Crafting Recipe Helpers          //
    ///////////////////////////////////////////////////

    /**
     * Adds Shaped Crafting Recipes.
     * <p/>
     * MetaValueItem's are converted to ItemStack via {@link MetaItem.MetaValueItem#getStackForm()} method.
     * <p/>
     * For Enums - {@link Enum#name()} is called.
     * <p/>
     * For UnificationEntry - {@link UnificationEntry#toString()} is called.
     * <p/>
     * Lowercase Letters are reserved for Tools. They are as follows:
     * <p/>
     * <ul>
     * <li>'b' -  ToolDictNames.craftingToolBlade</li>
     * <li>'c' -  ToolDictNames.craftingToolCrowbar</li>
     * <li>'d' -  ToolDictNames.craftingToolScrewdriver</li>
     * <li>'f' -  ToolDictNames.craftingToolFile</li>
     * <li>'h' -  ToolDictNames.craftingToolHardHammer</li>
     * <li>'i' -  ToolDictNames.craftingToolSolderingIron</li>
     * <li>'j' -  ToolDictNames.craftingToolSolderingMetal</li>
     * <li>'k' -  ToolDictNames.craftingToolKnife</li>
     * <li>'m' -  ToolDictNames.craftingToolMortar</li>
     * <li>'p' -  ToolDictNames.craftingToolDrawplate</li>
     * <li>'r' -  ToolDictNames.craftingToolSoftHammer</li>
     * <li>'s' -  ToolDictNames.craftingToolSaw</li>
     * <li>'w' -  ToolDictNames.craftingToolWrench</li>
     * <li>'x' -  ToolDictNames.craftingToolWireCutter</li>
     * </ul>
     */
    public static void addMirroredShapedRecipe(String regName, ItemStack result, Object... recipe) {
        result = OreDictUnifier.getUnificated(result);
        boolean skip = false;
        if (result.isEmpty()) {
            GTLog.logger.error("Result cannot be an empty ItemStack. Recipe: {}", regName);
            GTLog.logger.error("Stacktrace:", new IllegalArgumentException());
            skip = true;
        }
        skip |= validateRecipe(regName, recipe);
        if (skip) {
            RecipeMap.setFoundInvalidRecipe(true);
            return;
        }

        IRecipe shapedOreRecipe = new ShapedOreRecipe(new ResourceLocation(GTValues.MODID, "general"), result.copy(), finalizeShapedRecipeInput(recipe))
            .setMirrored(true)
            .setRegistryName(regName);
        ForgeRegistries.RECIPES.register(shapedOreRecipe);
    }

    /**
     * Adds Shaped Crafting Recipes.
     * <p/>
     * MetaValueItem's are converted to ItemStack via {@link MetaItem.MetaValueItem#getStackForm()} method.
     * <p/>
     * For Enums - {@link Enum#name()} is called.
     * <p/>
     * For UnificationEntry - {@link UnificationEntry#toString()} is called.
     * <p/>
     * Lowercase Letters are reserved for Tools. They are as follows:
     * <p/>
     * <ul>
     * <li>'b' -  ToolDictNames.craftingToolBlade</li>
     * <li>'c' -  ToolDictNames.craftingToolCrowbar</li>
     * <li>'d' -  ToolDictNames.craftingToolScrewdriver</li>
     * <li>'f' -  ToolDictNames.craftingToolFile</li>
     * <li>'h' -  ToolDictNames.craftingToolHardHammer</li>
     * <li>'i' -  ToolDictNames.craftingToolSolderingIron</li>
     * <li>'j' -  ToolDictNames.craftingToolSolderingMetal</li>
     * <li>'k' -  ToolDictNames.craftingToolKnife</li>
     * <li>'m' -  ToolDictNames.craftingToolMortar</li>
     * <li>'p' -  ToolDictNames.craftingToolDrawplate</li>
     * <li>'r' -  ToolDictNames.craftingToolSoftHammer</li>
     * <li>'s' -  ToolDictNames.craftingToolSaw</li>
     * <li>'w' -  ToolDictNames.craftingToolWrench</li>
     * <li>'x' -  ToolDictNames.craftingToolWireCutter</li>
     * </ul>
     */
    public static void addShapedRecipe(String regName, ItemStack result, Object... recipe) {
        boolean skip = false;
        if (result.isEmpty()) {
            GTLog.logger.error("Result cannot be an empty ItemStack. Recipe: {}", regName);
            GTLog.logger.error("Stacktrace:", new IllegalArgumentException());
            skip = true;
        }
        skip |= validateRecipe(regName, recipe);
        if (skip) {
            RecipeMap.setFoundInvalidRecipe(true);
            return;
        }

        IRecipe shapedOreRecipe = new ShapedOreRecipe(null, result.copy(), finalizeShapedRecipeInput(recipe))
            .setMirrored(false) //make all recipes not mirrored by default
            .setRegistryName(regName);
        ForgeRegistries.RECIPES.register(shapedOreRecipe);
    }

    public static void addShapedEnergyTransferRecipe(String regName, ItemStack result, Predicate<ItemStack> chargePredicate, boolean transferMaxCharge, Object... recipe) {
        boolean skip = false;
        if (result.isEmpty()) {
            GTLog.logger.error("Result cannot be an empty ItemStack. Recipe: {}", regName);
            GTLog.logger.error("Stacktrace:", new IllegalArgumentException());
            skip = true;
        }
        skip |= validateRecipe(regName, recipe);
        if (skip) {
            RecipeMap.setFoundInvalidRecipe(true);
            return;
        }

        IRecipe shapedOreRecipe = new ShapedOreEnergyTransferRecipe(null, result.copy(), chargePredicate, transferMaxCharge, finalizeShapedRecipeInput(recipe))
            .setMirrored(false) //make all recipes not mirrored by default
            .setRegistryName(regName);
        ForgeRegistries.RECIPES.register(shapedOreRecipe);
    }

    private static boolean validateRecipe(String regName, Object... recipe) {
        boolean skip = false;
        if (recipe == null) {
            GTLog.logger.error("Recipe cannot be null", new IllegalArgumentException());
            GTLog.logger.error("Stacktrace:", new IllegalArgumentException());
            skip = true;
        } else if (recipe.length == 0) {
            GTLog.logger.error("Recipe cannot be empty", new IllegalArgumentException());
            GTLog.logger.error("Stacktrace:", new IllegalArgumentException());
            skip = true;
        } else if (Arrays.asList(recipe).contains(null) || Arrays.asList(recipe).contains(ItemStack.EMPTY)) {
            GTLog.logger.error("Recipe cannot contain null elements or Empty ItemStacks. Recipe: {}",
                Arrays.stream(recipe)
                    .map(o -> o == null ? "NULL" : o)
                    .map(o -> o == ItemStack.EMPTY ? "EMPTY STACK" : o)
                    .map(Object::toString)
                    .map(s -> "\"" + s + "\"")
                    .collect(Collectors.joining(", ")));
            GTLog.logger.error("Stacktrace:", new IllegalArgumentException());
            skip = true;
        } else if (ForgeRegistries.RECIPES.containsKey(new ResourceLocation(GTValues.MODID, regName))) {
            GTLog.logger.error("Tried to register recipe, {}, with duplicate key. Recipe: {}", regName,
                Arrays.stream(recipe)
                    .map(Object::toString)
                    .map(s -> "\"" + s + "\"")
                    .collect(Collectors.joining(", ")));
            GTLog.logger.error("Stacktrace:", new IllegalArgumentException());
            skip = true;
        }
        return skip;
    }

    public static Object[] finalizeShapedRecipeInput(Object... recipe) {
        for (byte i = 0; i < recipe.length; i++) {
            recipe[i] = finalizeIngredient(recipe[i]);
        }
        int idx = 0;
        ArrayList<Object> recipeList = new ArrayList<>(Arrays.asList(recipe));

        while (recipe[idx] instanceof String) {
            StringBuilder s = new StringBuilder((String) recipe[idx++]);
            while (s.length() < 3) s.append(" ");
            if (s.length() > 3) throw new IllegalArgumentException();
            for (char c : s.toString().toCharArray()) {
                String toolName = getToolNameByCharacter(c);
                if (toolName != null) {
                    recipeList.add(c);
                    recipeList.add(toolName);
                }
            }
        }
        return recipeList.toArray();
    }
    
    public static Object finalizeIngredient(Object ingredient) {
        if (ingredient instanceof MetaItem.MetaValueItem) {
            ingredient = ((MetaItem<?>.MetaValueItem) ingredient).getStackForm();
        } else if (ingredient instanceof Enum) {
            ingredient = ((Enum<?>) ingredient).name();
        } else if (ingredient instanceof UnificationEntry) {
            ingredient = ingredient.toString();
        } else if (!(ingredient instanceof ItemStack
            || ingredient instanceof Item
            || ingredient instanceof Block
            || ingredient instanceof String
            || ingredient instanceof Character
            || ingredient instanceof Boolean)) {
            throw new IllegalArgumentException(ingredient.getClass().getSimpleName() + " type is not suitable for crafting input.");
        }
        return ingredient;
    }

    /**
     * Add Shapeless Crafting Recipes
     */
    public static void addShapelessRecipe(String regName, ItemStack result, Object... recipe) {
        boolean skip = false;
        if (result.isEmpty()) {
            GTLog.logger.error("Result cannot be an empty ItemStack. Recipe: {}", regName);
            GTLog.logger.error("Stacktrace:", new IllegalArgumentException());
            skip = true;
        }
        skip |= validateRecipe(regName, recipe);
        if (skip) {
            RecipeMap.setFoundInvalidRecipe(true);
            return;
        }

        for (byte i = 0; i < recipe.length; i++) {
            if (recipe[i] instanceof MetaItem.MetaValueItem) {
                recipe[i] = ((MetaItem<?>.MetaValueItem) recipe[i]).getStackForm();
            } else if (recipe[i] instanceof Enum) {
                recipe[i] = ((Enum<?>) recipe[i]).name();
            } else if (recipe[i] instanceof UnificationEntry) {
                recipe[i] = recipe[i].toString();
            } else if (recipe[i] instanceof Character) {
                String toolName = getToolNameByCharacter((char) recipe[i]);
                if (toolName == null) {
                    throw new IllegalArgumentException("Tool name is not found for char " + recipe[i]);
                }
                recipe[i] = toolName;
            } else if (!(recipe[i] instanceof ItemStack
                || recipe[i] instanceof Item
                || recipe[i] instanceof Block
                || recipe[i] instanceof String)) {
                throw new IllegalArgumentException(recipe.getClass().getSimpleName() + " type is not suitable for crafting input.");
            }
        }

        IRecipe shapelessRecipe = new ShapelessOreRecipe(null, result.copy(), recipe)
            .setRegistryName(regName);

        try {
            //workaround for MC bug that makes all shaped recipe inputs that have enchanted items
            //or renamed ones on input fail, even if all ingredients match it
            Field field = ShapelessOreRecipe.class.getDeclaredField("isSimple");
            field.setAccessible(true);
            field.setBoolean(shapelessRecipe, false);
        } catch (ReflectiveOperationException exception) {
            GTLog.logger.error("Failed to mark shapeless recipe as complex", exception);
        }

        ForgeRegistries.RECIPES.register(shapelessRecipe);
    }

    private @Nullable
    static String getToolNameByCharacter(char character) {
        switch (character) {
            case 'b':
                return ToolDictNames.craftingToolBlade.name();
            case 'c':
                return ToolDictNames.craftingToolCrowbar.name();
            case 'd':
                return ToolDictNames.craftingToolScrewdriver.name();
            case 'f':
                return ToolDictNames.craftingToolFile.name();
            case 'h':
                return ToolDictNames.craftingToolHardHammer.name();
            case 'i':
                return ToolDictNames.craftingToolSolderingIron.name();
            case 'j':
                return ToolDictNames.craftingToolSolderingMetal.name();
            case 'k':
                return ToolDictNames.craftingToolKnife.name();
            case 'm':
                return ToolDictNames.craftingToolMortar.name();
            case 'p':
                return ToolDictNames.craftingToolDrawplate.name();
            case 'r':
                return ToolDictNames.craftingToolSoftHammer.name();
            case 's':
                return ToolDictNames.craftingToolSaw.name();
            case 'w':
                return ToolDictNames.craftingToolWrench.name();
            case 'x':
                return ToolDictNames.craftingToolWireCutter.name();
            default:
                return null;
        }
    }

    public static Collection<ItemStack> getAllSubItems(ItemStack item) {
        //match subtypes only on wildcard damage value items
        if (item.getItemDamage() != GTValues.W)
            return Collections.singleton(item);
        NonNullList<ItemStack> stackList = NonNullList.create();
        CreativeTabs[] visibleTags = item.getItem().getCreativeTabs();
        for (CreativeTabs creativeTab : visibleTags) {
            NonNullList<ItemStack> thisList = NonNullList.create();
            item.getItem().getSubItems(creativeTab, thisList);
            loop:
            for (ItemStack newStack : thisList) {
                for (ItemStack alreadyExists : stackList) {
                    if (ItemStack.areItemStacksEqual(alreadyExists, newStack))
                        continue loop; //do not add equal item stacks
                }
                stackList.add(newStack);
            }
        }
        return stackList;
    }

    ///////////////////////////////////////////////////
    //              Recipe Remove Helpers            //
    ///////////////////////////////////////////////////

    public static boolean removeFurnaceSmelting(UnificationEntry input) {
        boolean result = false;
        List<ItemStack> allStacks = OreDictUnifier.getAll(input);
        for (ItemStack inputStack : allStacks) {
            result |= removeFurnaceSmelting(inputStack);
        }
        return result;
    }

    /**
     * Removes a Smelting Recipe
     */
    public static boolean removeFurnaceSmelting(ItemStack input) {
        if (input.isEmpty()) {
            GTLog.logger.error("Cannot remove furnace recipe with empty input.");
            GTLog.logger.error("Stacktrace:", new IllegalArgumentException());
            RecipeMap.setFoundInvalidRecipe(true);
            return false;
        }
        for (ItemStack stack : FurnaceRecipes.instance().getSmeltingList().keySet()) {
            if (ItemStack.areItemStacksEqual(input, stack)) {
                FurnaceRecipes.instance().getSmeltingList().remove(stack);
                return true;
            }
        }
        return false;
    }

    public static int removeRecipes(Item output) {
        return removeRecipes(recipe -> {
            ItemStack recipeOutput = recipe.getRecipeOutput();
            return !recipeOutput.isEmpty() && recipeOutput.getItem() == output;
        });
    }

    public static int removeRecipes(ItemStack output) {
        return removeRecipes(recipe -> ItemStack.areItemStacksEqual(recipe.getRecipeOutput(), output));
    }

    public static <R extends IRecipe> int removeRecipes(Class<R> recipeClass) {
        return removeRecipes(recipeClass::isInstance);
    }

    public static int removeRecipes(Predicate<IRecipe> predicate) {
        int recipesRemoved = 0;

        IForgeRegistry<IRecipe> registry = ForgeRegistries.RECIPES;
        List<IRecipe> toRemove = new ArrayList<>();

        for (IRecipe recipe : registry) {
            if (predicate.test(recipe)) {
                toRemove.add(recipe);
                recipesRemoved++;
            }
        }

        toRemove.forEach(recipe -> registry.register(new DummyRecipe().setRegistryName(recipe.getRegistryName())));

        return recipesRemoved;
    }

    public static void removeRecipeByName(ResourceLocation location) {
        ForgeRegistries.RECIPES.register(new DummyRecipe().setRegistryName(location));
    }

    ///////////////////////////////////////////////////
    //            Get Recipe Output Helpers          //
    ///////////////////////////////////////////////////

    public static Pair<IRecipe, ItemStack> getRecipeOutput(World world, ItemStack... recipe) {
        if (recipe == null || recipe.length == 0)
            return ImmutablePair.of(null, ItemStack.EMPTY);

        if (world == null) world = DummyWorld.INSTANCE;

        InventoryCrafting craftingGrid = new InventoryCrafting(new DummyContainer(), 3, 3);

        for (int i = 0; i < 9 && i < recipe.length; i++) {
            ItemStack recipeStack = recipe[i];
            if (recipeStack != null && !recipeStack.isEmpty()) {
                craftingGrid.setInventorySlotContents(i, recipeStack);
            }
        }

        for (IRecipe tmpRecipe : CraftingManager.REGISTRY) {
            if (tmpRecipe.matches(craftingGrid, world)) {
                ItemStack itemStack = tmpRecipe.getCraftingResult(craftingGrid);
                return ImmutablePair.of(tmpRecipe, itemStack);
            }
        }

        return ImmutablePair.of(null, ItemStack.EMPTY);
    }

    public static ItemStack getSmeltingOutput(ItemStack input) {
        if (input.isEmpty()) return ItemStack.EMPTY;
        return OreDictUnifier.getUnificated(FurnaceRecipes.instance().getSmeltingResult(input));
    }

    public static void addRCFurnaceRecipe(UnificationEntry input, ItemStack output, int duration) {
        List<ItemStack> allStacks = OreDictUnifier.getAll(input);
        for (ItemStack inputStack : allStacks) {
            addRCFurnaceRecipe(inputStack, output, duration);
        }
    }

    public static void addRCFurnaceRecipe(ItemStack input, ItemStack output, int duration) {
        Preconditions.checkNotNull(input);
        Preconditions.checkNotNull(output);
        Preconditions.checkArgument(duration > 0, "Duration should be positive!");
//        if(GTValues.isModLoaded("railcraft")) {
//            addRCFurnaceRecipeInternal(input, output, duration);
//        }
    }

//    @Optional.Method(modid = "railcraft")
//    private static void addRCFurnaceRecipeInternal(ItemStack input, ItemStack output, int duration) {
//        RailcraftCraftingManager.blastFurnace.addRecipe(input, true, false, duration, output);
//    }

}
