package gtr.common.metatileentities.multi.electric;

import gtr.api.capability.IMultipleTankHandler;
import gtr.api.capability.impl.MultiblockRecipeLogic;
import gtr.api.gui.IUIHolder;
import gtr.api.gui.ModularUI;
import gtr.api.gui.resources.TextureArea;
import gtr.api.gui.widgets.*;
import gtr.api.metatileentity.MetaTileEntity;
import gtr.api.metatileentity.MetaTileEntityHolder;
import gtr.api.metatileentity.multiblock.IMultiblockPart;
import gtr.api.metatileentity.multiblock.MultiblockAbility;
import gtr.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gtr.api.multiblock.BlockPattern;
import gtr.api.multiblock.FactoryBlockPattern;
import gtr.api.multiblock.PatternMatchContext;
import gtr.api.recipes.CountableIngredient;
import gtr.api.recipes.Recipe;
import gtr.api.recipes.RecipeMaps;
import gtr.api.render.ICubeRenderer;
import gtr.api.render.Textures;
import gtr.common.blocks.BlockMetalCasing.MetalCasingType;
import gtr.common.blocks.BlockWireCoil;
import gtr.common.blocks.BlockWireCoil.CoilType;
import gtr.common.blocks.MetaBlocks;
import mezz.jei.Internal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MetaTileEntityMultiFurnace extends RecipeMapMultiblockController implements IUIHolder {

    private static final MultiblockAbility<?>[] ALLOWED_ABILITIES = {
        MultiblockAbility.IMPORT_ITEMS, MultiblockAbility.EXPORT_ITEMS, MultiblockAbility.INPUT_ENERGY
    };

    protected int heatingCoilLevel;
    protected int heatingCoilDiscount;

    public MetaTileEntityMultiFurnace(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, RecipeMaps.FURNACE_RECIPES);
        this.recipeMapWorkable = new MultiFurnaceWorkable(this);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder holder) {
        return new MetaTileEntityMultiFurnace(metaTileEntityId);
    }


    @Override
    protected ModularUI createUI(EntityPlayer entityPlayer) {

        JeiOpenWidget recipes = new JeiOpenWidget(130, 59, 18, 18, this.recipeMap) {
            @Override
            protected void triggerButton() {
                Internal.getRuntime().getRecipesGui().showCategories(Collections.singletonList("minecraft.smelting"));
                playButtonClickSound();
            }
        };

        StructureFormedWidget structureFormed = new StructureFormedWidget(155, 66, 13, 13, this::isStructureFormed);

        ActiveWidget active = new ActiveWidget(131, 42, 16, 16, () -> isStructureFormed() && recipeMapWorkable.isActive() && recipeMapWorkable.isWorkingEnabled());

        AdvancedTextWidget text = new AdvancedTextWidget(10, 7, this::addDisplayText, 0xFFFFFF)
            .setMaxWidthLimit(156)
            .setClickHandler(this::handleDisplayClick);

        SlotWidget coilWidget = null;
        if (getWorld().getBlockState(getPos().offset(EnumFacing.UP)).getBlock() instanceof BlockWireCoil) {
            CoilType coil = getWorld().getBlockState(getPos().offset(EnumFacing.UP)).getValue(((BlockWireCoil) getWorld().getBlockState(getPos().offset(EnumFacing.UP)).getBlock()).VARIANT);

            coilWidget = new SlotWidget(new IItemHandlerModifiable() {
                @Override
                public void setStackInSlot(int slot, @Nonnull ItemStack stack) {

                }

                @Override
                public int getSlots() {
                    return 1;
                }

                @Nonnull
                @Override
                public ItemStack getStackInSlot(int slot) {
                    return MetaBlocks.WIRE_COIL.getItemVariant(coil);
                }

                @Nonnull
                @Override
                public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                    return stack;
                }

                @Nonnull
                @Override
                public ItemStack extractItem(int slot, int amount, boolean simulate) {
                    return ItemStack.EMPTY;
                }

                @Override
                public int getSlotLimit(int slot) {
                    return 1;
                }
            }, 0, 151, 4, false, false);


        }




        ModularUI.Builder builder = ModularUI.defaultBuilder();
        builder.image(0, 0, 176, 166, TextureArea.fullImage("textures/gui/multiblock/multi_furnace.png"));
        builder.widget(text);

        return builder.widget(recipes).widget(structureFormed).widget(active).widget(coilWidget).bindPlayerInventory(entityPlayer.inventory, Textures.EMPTY, 7, 83)
            .build(this, entityPlayer);
    }

    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        if (isStructureFormed()) {
            textList.add(new TextComponentTranslation("gtr.multiblock.multi_furnace.heating_coil_level", heatingCoilLevel));
            textList.add(new TextComponentTranslation("gtr.multiblock.multi_furnace.heating_coil_discount", heatingCoilDiscount));
        }
        super.addDisplayText(textList);
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        CoilType coilType = context.getOrDefault("CoilType", CoilType.CUPRONICKEL);
        this.heatingCoilLevel = coilType.getLevel();
        this.heatingCoilDiscount = coilType.getEnergyDiscount();
    }

    @Override
    public void invalidateStructure() {
        super.invalidateStructure();
        this.heatingCoilLevel = 0;
        this.heatingCoilDiscount = 0;
    }

    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
            .aisle("XXX", "CCC", "XXX")
            .aisle("XXX", "C#C", "XXX")
            .aisle("XSX", "CCC", "XXX")
            .setAmountAtLeast('L', 9)
            .where('S', selfPredicate())
            .where('L', statePredicate(getCasingState()))
            .where('X', statePredicate(getCasingState()).or(abilityPartPredicate(ALLOWED_ABILITIES)))
            .where('C', MetaTileEntityElectricBlastFurnace.heatingCoilPredicate())
            .where('#', isAirPredicate())
            .build();
    }

    public IBlockState getCasingState() {
        return MetaBlocks.METAL_CASING.getState(MetalCasingType.INVAR_HEATPROOF);
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return Textures.HEAT_PROOF_CASING;
    }

    @Override
    public boolean isRemote() {
        return getWorld().isRemote;
    }

    @Override
    public void markAsDirty() {
        markDirty();
    }

    protected class MultiFurnaceWorkable extends MultiblockRecipeLogic {

        public MultiFurnaceWorkable(RecipeMapMultiblockController tileEntity) {
            super(tileEntity);
        }

        @Override
        protected void trySearchNewRecipe() {
            long maxVoltage = getMaxVoltage();
            Recipe currentRecipe = null;
            IItemHandlerModifiable importInventory = getInputInventory();
            IMultipleTankHandler importFluids = getInputTank();
            boolean dirty = checkRecipeInputsDirty(importInventory, importFluids);
            //inverse of logic in normal AbstractRecipeLogic
            //for MultiSmelter, we can reuse previous recipe if inputs didn't change
            //otherwise, we need to recompute it for new ingredients
            //but technically, it means we can cache multi smelter recipe, but changing inputs have more priority
            if(dirty || forceRecipeRecheck) {
                this.forceRecipeRecheck = false;
                //else, try searching new recipe for given inputs
                currentRecipe = findRecipe(maxVoltage, importInventory, importFluids);
                if (currentRecipe != null) {
                    this.previousRecipe = currentRecipe;
                }
            } else if (previousRecipe != null && previousRecipe.matches(false, importInventory, importFluids)) {
                //if previous recipe still matches inputs, try to use it
                currentRecipe = previousRecipe;
            }
            if (currentRecipe != null && setupAndConsumeRecipeInputs(currentRecipe)) {
                setupRecipe(currentRecipe);
            }
        }

        @Override
        protected Recipe findRecipe(long maxVoltage, IItemHandlerModifiable inputs, IMultipleTankHandler fluidInputs) {
            int currentItemsEngaged = 0;
            int maxItemsLimit = 32 * heatingCoilLevel;
            ArrayList<CountableIngredient> recipeInputs = new ArrayList<>();
            ArrayList<ItemStack> recipeOutputs = new ArrayList<>();
            for (int index = 0; index < inputs.getSlots(); index++) {
                ItemStack stackInSlot = inputs.getStackInSlot(index);
                if (stackInSlot.isEmpty())
                    continue;
                Recipe matchingRecipe = recipeMap.findRecipe(maxVoltage,
                    Collections.singletonList(stackInSlot), Collections.emptyList(), 0);
                CountableIngredient inputIngredient = matchingRecipe == null ? null : matchingRecipe.getInputs().get(0);
                if (inputIngredient != null && (maxItemsLimit - currentItemsEngaged) >= inputIngredient.getCount()) {
                    ItemStack outputStack = matchingRecipe.getOutputs().get(0).copy();
                    int overclockAmount = Math.min(stackInSlot.getCount() / inputIngredient.getCount(),
                        (maxItemsLimit - currentItemsEngaged) / inputIngredient.getCount());
                    recipeInputs.add(new CountableIngredient(inputIngredient.getIngredient(),
                        inputIngredient.getCount() * overclockAmount));
                    if (!outputStack.isEmpty()) {
                        outputStack.setCount(outputStack.getCount() * overclockAmount);
                        recipeOutputs.add(outputStack);
                    }
                    currentItemsEngaged += inputIngredient.getCount() * overclockAmount;
                }

                if (currentItemsEngaged >= maxItemsLimit) break;
            }
            return recipeInputs.isEmpty() ? null : recipeMap.recipeBuilder()
                .inputsIngredients(recipeInputs)
                .outputs(recipeOutputs)
                .EUt(Math.max(1, 16 / heatingCoilDiscount))
                .duration((int) Math.max(1.0, 256 * (currentItemsEngaged / (maxItemsLimit * 1.0))))
                .build().getResult();
        }
    }

}
