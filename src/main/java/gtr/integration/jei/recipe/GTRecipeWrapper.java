package gtr.integration.jei.recipe;

import codechicken.lib.util.ItemNBTUtils;
import gtr.api.recipes.CountableIngredient;
import gtr.api.recipes.Recipe;
import gtr.api.recipes.Recipe.ChanceEntry;
import gtr.api.recipes.RecipeMap;
import gtr.api.unification.OreDictUnifier;
import gtr.api.util.ItemStackHashStrategy;
import gtr.integration.jei.utils.JEIHelpers;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

import java.util.*;
import java.util.stream.Collectors;

public class GTRecipeWrapper implements IRecipeWrapper {

    private static final int lineHeight = 10;
    private final RecipeMap<?> recipeMap;
    private final Recipe recipe;


    private final Hash.Strategy<ItemStack> strategy = ItemStackHashStrategy.comparingAllButCount();

    private final Set<ItemStack> notConsumedInput = new ObjectOpenCustomHashSet<>(strategy);
    private final Map<ItemStack, ChanceEntry> chanceOutput = new Object2ObjectOpenCustomHashMap<>(strategy);
    private final List<FluidStack> notConsumedFluidInput = new ArrayList<>();


    public GTRecipeWrapper(RecipeMap<?> recipeMap, Recipe recipe) {
        this.recipeMap = recipeMap;
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        if (!recipe.getInputs().isEmpty()) {
            List<CountableIngredient> recipeInputs = recipe.getInputs();
            List<List<ItemStack>> matchingInputs = new ArrayList<>(recipeInputs.size());
            for (CountableIngredient ingredient : recipeInputs) {
                List<ItemStack> ingredientValues = Arrays.stream(ingredient.getIngredient().getMatchingStacks())
                    .map(ItemStack::copy)
                    .sorted(OreDictUnifier.getItemStackComparator())
                    .collect(Collectors.toList());
                ingredientValues.forEach(stack -> {
                    if (ingredient.getCount() == 0) {
                        notConsumedInput.add(stack);
                        stack.setCount(1);
                    } else stack.setCount(ingredient.getCount());
                });
                matchingInputs.add(ingredientValues);
            }
            ingredients.setInputLists(VanillaTypes.ITEM, matchingInputs);
        }

        if (!recipe.getFluidInputs().isEmpty()) {
            List<FluidStack> recipeInputs = recipe.getFluidInputs()
                .stream().map(FluidStack::copy)
                .collect(Collectors.toList());
            recipeInputs.forEach(stack -> {
                if (stack.amount == 0) {
                    notConsumedFluidInput.add(stack);
                    stack.amount = 1;
                }
            });
            ingredients.setInputs(VanillaTypes.FLUID, recipeInputs);
        }

        if (!recipe.getOutputs().isEmpty() || !recipe.getChancedOutputs().isEmpty()) {
            List<ItemStack> recipeOutputs = recipe.getOutputs()
                .stream().map(ItemStack::copy).collect(Collectors.toList());
            List<ChanceEntry> chancedOutputs = recipe.getChancedOutputs();
            for (ChanceEntry chancedEntry : chancedOutputs) {
                ItemStack chancedStack = chancedEntry.getItemStack();
                chanceOutput.put(chancedStack, chancedEntry);
                recipeOutputs.add(chancedStack);
            }
            recipeOutputs.sort(Comparator.comparingInt(stack -> {
                ChanceEntry chanceEntry = chanceOutput.get(stack);
                if (chanceEntry == null)
                    return 0;
                return chanceEntry.getChance();
            }));
            ingredients.setOutputs(VanillaTypes.ITEM, recipeOutputs);
        }

        if (!recipe.getFluidOutputs().isEmpty()) {
            List<FluidStack> recipeOutputs = recipe.getFluidOutputs()
                .stream().map(FluidStack::copy).collect(Collectors.toList());
            ingredients.setOutputs(VanillaTypes.FLUID, recipeOutputs);
        }
    }

    public void addTooltip(int slotIndex, boolean input, Object ingredient, List<String> tooltip) {
        boolean notConsumed = false;
        ChanceEntry entry = null;
        if (ingredient instanceof FluidStack) {
            FluidStack fluidStack = ((FluidStack) ingredient);
            if (notConsumedFluidInput.contains(fluidStack))
                notConsumed = true;
        } else if (ingredient instanceof ItemStack) {
            ItemStack itemStack = ((ItemStack) ingredient);
            if (notConsumedInput.contains(itemStack))
                notConsumed = true;
            else entry = chanceOutput.get(itemStack);
        } else {
            throw new IllegalArgumentException("Unknown ingredient type: " + ingredient.getClass());
        }
        if (entry != null) {
            double chance = entry.getChance() / 100.0;
            double boost = entry.getBoostPerTier() / 100.0;
            tooltip.add(I18n.format("gtr.recipe.chance", chance, boost));
        } else if (notConsumed) {
            tooltip.add(I18n.format("gtr.recipe.not_consumed"));
        }
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        int yPosition = recipeHeight - getPropertyListHeight();
        if (recipe.getEUt() > 0) {
            minecraft.fontRenderer.drawString(I18n.format("gtr.recipe.total", Math.abs((long) recipe.getEUt()) * recipe.getDuration()), 0, yPosition, 0x111111);
            minecraft.fontRenderer.drawString(I18n.format(recipe.getEUt() >= 0 ? "gtr.recipe.eu" : "gtr.recipe.eu_inverted", Math.abs(recipe.getEUt()), JEIHelpers.getMinTierForVoltage(recipe.getEUt())), 0, yPosition += lineHeight, 0x111111);
        }
        minecraft.fontRenderer.drawString(I18n.format("gtr.recipe.duration", recipe.getDuration() / 20f), 0, yPosition += lineHeight, 0x111111);

        for (String propertyKey : recipe.getPropertyKeys()) {
            minecraft.fontRenderer.drawString(I18n.format("gtr.recipe." + propertyKey,
                recipe.<Object>getProperty(propertyKey)), 0, yPosition += lineHeight, 0x111111);
        }
    }

    private int getPropertyListHeight() {
        return (recipe.getPropertyKeys().size() + 3) * lineHeight;
    }

}