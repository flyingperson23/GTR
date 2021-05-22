package gtr.common.blocks;

import com.google.common.collect.ImmutableMap;
import gtr.api.GTValues;
import gtr.api.GregTechAPI;
import gtr.api.block.machines.BlockMachine;
import gtr.api.metatileentity.MetaTileEntityHolder;
import gtr.api.render.MetaTileEntityRenderer;
import gtr.api.render.MetaTileEntityTESR;
import gtr.api.unification.OreDictUnifier;
import gtr.api.unification.material.Materials;
import gtr.api.unification.material.type.DustMaterial;
import gtr.api.unification.material.type.DustMaterial.MatFlags;
import gtr.api.unification.material.type.IngotMaterial;
import gtr.api.unification.material.type.Material;
import gtr.api.unification.material.type.SolidMaterial;
import gtr.api.unification.ore.OrePrefix;
import gtr.api.unification.ore.StoneType;
import gtr.common.blocks.foam.BlockFoam;
import gtr.common.blocks.foam.BlockPetrifiedFoam;
import gtr.common.blocks.modelfactories.BakedModelHandler;
import gtr.common.blocks.surfacerock.BlockSurfaceRockDeprecated;
import gtr.common.blocks.surfacerock.BlockSurfaceRockNew;
import gtr.common.blocks.surfacerock.TileEntitySurfaceRock;
import gtr.common.blocks.tileentity.TileEntityCrusherBlade;
import gtr.common.blocks.wood.BlockGregLeaves;
import gtr.common.blocks.wood.BlockGregLog;
import gtr.common.blocks.wood.BlockGregSapling;
import gtr.common.pipelike.cable.BlockCable;
import gtr.common.pipelike.cable.Insulation;
import gtr.common.pipelike.cable.WireProperties;
import gtr.common.pipelike.cable.tile.TileEntityCable;
import gtr.common.pipelike.cable.tile.TileEntityCableTickable;
import gtr.common.pipelike.fluidpipe.BlockFluidPipe;
import gtr.common.pipelike.fluidpipe.FluidPipeProperties;
import gtr.common.pipelike.fluidpipe.FluidPipeType;
import gtr.common.pipelike.fluidpipe.tile.TileEntityFluidPipe;
import gtr.common.pipelike.fluidpipe.tile.TileEntityFluidPipeTickable;
import gtr.common.render.CableRenderer;
import gtr.common.render.FluidPipeRenderer;
import gtr.common.render.tesr.TileEntityCrusherBladeRenderer;
import gtr.common.render.tesr.TileEntityRendererBase.TileEntityRenderBaseItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLog.EnumAxis;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static gtr.api.unification.material.type.SolidMaterial.MatFlags.GENERATE_FRAME;
import static gtr.common.ClientProxy.*;

public class MetaBlocks {

    private MetaBlocks() {
    }

    public static BlockMachine MACHINE;
    public static BlockCable CABLE;
    public static BlockFluidPipe FLUID_PIPE;

    public static BlockBoilerCasing BOILER_CASING;
    public static BlockFireboxCasing BOILER_FIREBOX_CASING;
    public static BlockMetalCasing METAL_CASING;
    public static BlockTurbineCasing TURBINE_CASING;
    public static BlockMachineCasing MACHINE_CASING;
    public static BlockMultiblockCasing MULTIBLOCK_CASING;
    public static BlockWireCoil WIRE_COIL;
    public static BlockWarningSign WARNING_SIGN;


    public static BlockGranite GRANITE;
    public static BlockMineral MINERAL;
    public static BlockConcrete CONCRETE;

    public static BlockFoam FOAM;
    public static BlockFoam REINFORCED_FOAM;
    public static BlockPetrifiedFoam PETRIFIED_FOAM;
    public static BlockPetrifiedFoam REINFORCED_PETRIFIED_FOAM;

    public static BlockGregLog LOG;
    public static BlockGregLeaves LEAVES;
    public static BlockGregSapling SAPLING;

    public static Map<DustMaterial, BlockCompressed> COMPRESSED = new HashMap<>();
    public static Map<IngotMaterial, BlockSurfaceRockDeprecated> SURFACE_ROCKS = new HashMap<>();
    public static BlockSurfaceRockNew SURFACE_ROCK_NEW;
    public static Map<SolidMaterial, BlockFrame> FRAMES = new HashMap<>();
    public static Collection<BlockOre> ORES = new HashSet<>();
    public static Collection<BlockFluidBase> FLUID_BLOCKS = new HashSet<>();

    public static BlockMagnetInhibitor MAGNET_INHIBITOR;

    public static void init() {
        GregTechAPI.MACHINE = MACHINE = new BlockMachine();
        MACHINE.setRegistryName("machine");
        CABLE = new BlockCable();
        CABLE.setRegistryName("cable");
        FLUID_PIPE = new BlockFluidPipe();
        FLUID_PIPE.setRegistryName("fluid_pipe");
        BOILER_CASING = new BlockBoilerCasing();
        BOILER_CASING.setRegistryName("boiler_casing");
        BOILER_FIREBOX_CASING = new BlockFireboxCasing();
        BOILER_FIREBOX_CASING.setRegistryName("boiler_firebox_casing");
        METAL_CASING = new BlockMetalCasing();
        METAL_CASING.setRegistryName("metal_casing");
        TURBINE_CASING = new BlockTurbineCasing();
        TURBINE_CASING.setRegistryName("turbine_casing");
        MACHINE_CASING = new BlockMachineCasing();
        MACHINE_CASING.setRegistryName("machine_casing");
        MULTIBLOCK_CASING = new BlockMultiblockCasing();
        MULTIBLOCK_CASING.setRegistryName("multiblock_casing");
        WIRE_COIL = new BlockWireCoil();
        WIRE_COIL.setRegistryName("wire_coil");
        WARNING_SIGN = new BlockWarningSign();
        WARNING_SIGN.setRegistryName("warning_sign");
        GRANITE = new BlockGranite();
        GRANITE.setRegistryName("granite");
        MINERAL = new BlockMineral();
        MINERAL.setRegistryName("mineral");

        MAGNET_INHIBITOR = new BlockMagnetInhibitor();
        MAGNET_INHIBITOR.setRegistryName("magnet_inhibitor");


        CONCRETE = new BlockConcrete();
        CONCRETE.setRegistryName("concrete");

        FOAM = new BlockFoam(false);
        FOAM.setRegistryName("foam");
        REINFORCED_FOAM = new BlockFoam(true);
        REINFORCED_FOAM.setRegistryName("reinforced_foam");
        PETRIFIED_FOAM = new BlockPetrifiedFoam(false);
        PETRIFIED_FOAM.setRegistryName("petrified_foam");
        REINFORCED_PETRIFIED_FOAM = new BlockPetrifiedFoam(true);
        REINFORCED_PETRIFIED_FOAM.setRegistryName("reinforced_petrified_foam");

        LOG = new BlockGregLog();
        LOG.setRegistryName("log");
        LEAVES = new BlockGregLeaves();
        LEAVES.setRegistryName("leaves");
        SAPLING = new BlockGregSapling();
        SAPLING.setRegistryName("sapling");

        SURFACE_ROCK_NEW = new BlockSurfaceRockNew();
        SURFACE_ROCK_NEW.setRegistryName("surface_rock_new");

        StoneType.init();

        createGeneratedBlock(material -> material instanceof DustMaterial &&
            !OrePrefix.block.isIgnored(material), MetaBlocks::createCompressedBlock);
        createGeneratedBlock(material -> material instanceof IngotMaterial &&
            material.hasFlag(MatFlags.GENERATE_ORE), MetaBlocks::createSurfaceRockBlock);

        for (Material material : Material.MATERIAL_REGISTRY) {
            if (material instanceof DustMaterial &&
                material.hasFlag(DustMaterial.MatFlags.GENERATE_ORE)) {
                createOreBlock((DustMaterial) material);
            }
            if (material instanceof SolidMaterial && material.hasFlag(GENERATE_FRAME)) {
                BlockFrame blockFrame = new BlockFrame((SolidMaterial) material);
                blockFrame.setRegistryName("frame_" + material.toString());
                FRAMES.put((SolidMaterial) material, blockFrame);
            }
            if (material instanceof IngotMaterial) {
                IngotMaterial metalMaterial = (IngotMaterial) material;
                if (metalMaterial.cableProperties != null) {
                    CABLE.addCableMaterial(metalMaterial, metalMaterial.cableProperties);
                }
                if (metalMaterial.fluidPipeProperties != null) {
                    FLUID_PIPE.addPipeMaterial(metalMaterial, metalMaterial.fluidPipeProperties);
                }
            }
        }
        FLUID_PIPE.addPipeMaterial(Materials.Wood, new FluidPipeProperties(310, 20, false));
        CABLE.addCableMaterial(Materials.Superconductor, new WireProperties(Integer.MAX_VALUE, 4, 0));
        registerTileEntity();

        //not sure if that's a good place for that, but i don't want to make a dedicated method for that
        //could possibly override block methods, but since these props don't depend on state why not just use nice and simple vanilla method
        Blocks.FIRE.setFireInfo(LOG, 5, 5);
        Blocks.FIRE.setFireInfo(LEAVES, 30, 60);
    }

    private static int createGeneratedBlock(Predicate<Material> materialPredicate, BiConsumer<Material[], Integer> blockGenerator) {
        Material[] materialBuffer = new Material[16];
        Arrays.fill(materialBuffer, Materials._NULL);
        int currentGenerationIndex = 0;
        for (Material material : Material.MATERIAL_REGISTRY) {
            if (materialPredicate.test(material)) {
                if (currentGenerationIndex > 0 && currentGenerationIndex % 16 == 0) {
                    blockGenerator.accept(materialBuffer, currentGenerationIndex / 16 - 1);
                    Arrays.fill(materialBuffer, Materials._NULL);
                }
                materialBuffer[currentGenerationIndex % 16] = material;
                currentGenerationIndex++;
            }
        }
        if (materialBuffer[0] != Materials._NULL) {
            blockGenerator.accept(materialBuffer, currentGenerationIndex / 16);
        }
        return (currentGenerationIndex / 16) + 1;
    }

    private static void createSurfaceRockBlock(Material[] materials, int index) {
        BlockSurfaceRockDeprecated block = new BlockSurfaceRockDeprecated(materials);
        block.setRegistryName("surface_rock_" + index);
        for (Material material : materials) {
            if (material instanceof IngotMaterial) {
                SURFACE_ROCKS.put((IngotMaterial) material, block);
            }
        }
    }

    private static void createCompressedBlock(Material[] materials, int index) {
        BlockCompressed block = new BlockCompressed(materials);
        block.setRegistryName("compressed_" + index);
        for (Material material : materials) {
            if (material instanceof DustMaterial) {
                COMPRESSED.put((DustMaterial) material, block);
            }
        }
    }

    private static void createOreBlock(DustMaterial material) {
        StoneType[] stoneTypeBuffer = new StoneType[16];
        int generationIndex = 0;
        for (StoneType stoneType : StoneType.STONE_TYPE_REGISTRY) {
            int id = StoneType.STONE_TYPE_REGISTRY.getIDForObject(stoneType), index = id / 16;
            if (index > generationIndex) {
                createOreBlock(material, copyNotNull(stoneTypeBuffer), generationIndex);
                Arrays.fill(stoneTypeBuffer, null);
            }
            stoneTypeBuffer[id % 16] = stoneType;
            generationIndex = index;
        }
        createOreBlock(material, copyNotNull(stoneTypeBuffer), generationIndex);
    }

    private static <T> T[] copyNotNull(T[] src) {
        int nullIndex = ArrayUtils.indexOf(src, null);
        return Arrays.copyOfRange(src, 0, nullIndex == -1 ? src.length : nullIndex);
    }

    private static void createOreBlock(DustMaterial material, StoneType[] stoneTypes, int index) {
        BlockOre block = new BlockOre(material, stoneTypes);
        block.setRegistryName("ore_" + material + "_" + index);
        for (StoneType stoneType : stoneTypes) {
            GregTechAPI.oreBlockTable.computeIfAbsent(material, m -> new HashMap<>()).put(stoneType, block);
        }
        ORES.add(block);
    }

    public static void registerTileEntity() {
        GameRegistry.registerTileEntity(MetaTileEntityHolder.class, new ResourceLocation(GTValues.MODID, "machine"));
        GameRegistry.registerTileEntity(TileEntityCrusherBlade.class, new ResourceLocation(GTValues.MODID, "crusher_blade"));
        GameRegistry.registerTileEntity(TileEntityCable.class, new ResourceLocation(GTValues.MODID, "cable"));
        GameRegistry.registerTileEntity(TileEntityCableTickable.class, new ResourceLocation(GTValues.MODID, "cable_tickable"));
        GameRegistry.registerTileEntity(TileEntityFluidPipe.class, new ResourceLocation(GTValues.MODID, "fluid_pipe"));
        GameRegistry.registerTileEntity(TileEntityFluidPipeTickable.class, new ResourceLocation(GTValues.MODID, "fluid_pipe_active"));
        GameRegistry.registerTileEntity(TileEntitySurfaceRock.class, new ResourceLocation(GTValues.MODID, "surface_rock"));
    }

    @SideOnly(Side.CLIENT)
    public static void registerItemModels() {
        ModelLoader.setCustomMeshDefinition(Item.getItemFromBlock(MACHINE), stack -> MetaTileEntityRenderer.MODEL_LOCATION);
        ModelLoader.setCustomMeshDefinition(Item.getItemFromBlock(CABLE), stack -> CableRenderer.MODEL_LOCATION);
        ModelLoader.setCustomMeshDefinition(Item.getItemFromBlock(FLUID_PIPE), stack -> FluidPipeRenderer.MODEL_LOCATION);
        registerItemModel(BOILER_CASING);
        registerItemModel(BOILER_FIREBOX_CASING);
        registerItemModel(METAL_CASING);
        registerItemModel(TURBINE_CASING);
        registerItemModel(MACHINE_CASING);
        registerItemModel(MULTIBLOCK_CASING);
        registerItemModel(WIRE_COIL);
        registerItemModel(WARNING_SIGN);
        registerItemModel(GRANITE);
        registerItemModel(MINERAL);
        registerItemModel(CONCRETE);
        registerItemModelWithOverride(LOG, ImmutableMap.of(BlockGregLog.LOG_AXIS, EnumAxis.Y));
        registerItemModel(LEAVES);
        registerItemModel(SAPLING);
        registerItemModel(MAGNET_INHIBITOR);

        COMPRESSED.values().stream().distinct().forEach(MetaBlocks::registerItemModel);
        FRAMES.values().forEach(MetaBlocks::registerItemModelWithFilteredProperties);
        ORES.stream().distinct().forEach(MetaBlocks::registerItemModel);
    }

    @SideOnly(Side.CLIENT)
    private static void registerItemModel(Block block) {
        for (IBlockState state : block.getBlockState().getValidStates()) {
            //noinspection ConstantConditions
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block),
                block.getMetaFromState(state),
                new ModelResourceLocation(block.getRegistryName(),
                    statePropertiesToString(state.getProperties())));
        }
    }

    @SideOnly(Side.CLIENT)
    private static void registerItemModelWithFilteredProperties(Block block, IProperty<?>... filteredProperties) {
        for (IBlockState state : block.getBlockState().getValidStates()) {
            HashMap<IProperty<?>, Comparable<?>> stringProperties = new HashMap<>();
            for (IProperty<?> property : filteredProperties) {
                stringProperties.put(property, state.getValue(property));
            }
            //noinspection ConstantConditions
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block),
                block.getMetaFromState(state),
                new ModelResourceLocation(block.getRegistryName(),
                    statePropertiesToString(stringProperties)));
        }
    }


    @SideOnly(Side.CLIENT)
    private static void registerItemModelWithOverride(Block block, Map<IProperty<?>, Comparable<?>> stateOverrides) {
        for (IBlockState state : block.getBlockState().getValidStates()) {
            HashMap<IProperty<?>, Comparable<?>> stringProperties = new HashMap<>(state.getProperties());
            stringProperties.putAll(stateOverrides);
            //noinspection ConstantConditions
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block),
                block.getMetaFromState(state),
                new ModelResourceLocation(block.getRegistryName(),
                    statePropertiesToString(stringProperties)));
        }
    }

    @SideOnly(Side.CLIENT)
    public static void registerStateMappers() {
        ModelLoader.setCustomStateMapper(MACHINE, new DefaultStateMapper() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                return MetaTileEntityRenderer.MODEL_LOCATION;
            }
        });
        ModelLoader.setCustomStateMapper(CABLE, new DefaultStateMapper() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                return CableRenderer.MODEL_LOCATION;
            }
        });
        ModelLoader.setCustomStateMapper(FLUID_PIPE, new DefaultStateMapper() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                return FluidPipeRenderer.MODEL_LOCATION;
            }
        });
        IStateMapper normalStateMapper = new StateMapperBase() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                return new ModelResourceLocation(Block.REGISTRY.getNameForObject(state.getBlock()), "normal");
            }
        };
        ModelLoader.setCustomStateMapper(FOAM, normalStateMapper);
        ModelLoader.setCustomStateMapper(REINFORCED_FOAM, normalStateMapper);
        ModelLoader.setCustomStateMapper(PETRIFIED_FOAM, normalStateMapper);
        ModelLoader.setCustomStateMapper(REINFORCED_PETRIFIED_FOAM, normalStateMapper);
        FRAMES.values().forEach(it -> ModelLoader.setCustomStateMapper(it, normalStateMapper));

        BakedModelHandler modelHandler = new BakedModelHandler();
        MinecraftForge.EVENT_BUS.register(modelHandler);
        FLUID_BLOCKS.forEach(modelHandler::addFluidBlock);
        SURFACE_ROCKS.values().stream().distinct().forEach(block -> modelHandler.addBuiltInBlock(block, "stone"));

        modelHandler.addBuiltInBlock(SURFACE_ROCK_NEW, "stone_andesite");

        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCrusherBlade.class, new TileEntityCrusherBladeRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(MetaTileEntityHolder.class, new MetaTileEntityTESR());
    }

    @SideOnly(Side.CLIENT)
    public static void registerColors() {
        Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(
            FOAM_BLOCK_COLOR, FOAM, REINFORCED_FOAM, PETRIFIED_FOAM, REINFORCED_PETRIFIED_FOAM);

        MetaBlocks.COMPRESSED.values().stream().distinct().forEach(block -> {
            Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(COMPRESSED_BLOCK_COLOR, block);
            Minecraft.getMinecraft().getItemColors().registerItemColorHandler(COMPRESSED_ITEM_COLOR, block);
        });

        MetaBlocks.FRAMES.values().forEach(block -> {
            Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(FRAME_BLOCK_COLOR, block);
            Minecraft.getMinecraft().getItemColors().registerItemColorHandler(FRAME_ITEM_COLOR, block);
        });

        MetaBlocks.ORES.stream().distinct().forEach(block -> {
            Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(ORE_BLOCK_COLOR, block);
            Minecraft.getMinecraft().getItemColors().registerItemColorHandler(ORE_ITEM_COLOR, block);
        });

        MetaBlocks.SURFACE_ROCKS.values().stream().distinct().forEach(block ->
            Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(SURFACE_ROCK_COLOR, block));
    }

    public static void registerOreDict() {
        OreDictUnifier.registerOre(new ItemStack(LOG, 1, GTValues.W), OrePrefix.log, Materials.Wood);
        OreDictUnifier.registerOre(new ItemStack(LEAVES, 1, GTValues.W), "treeLeaves");
        OreDictUnifier.registerOre(new ItemStack(SAPLING, 1, GTValues.W), "treeSapling");
        GameRegistry.addSmelting(LOG, new ItemStack(Items.COAL, 1, 1), 0.15F);

        for (Entry<DustMaterial, BlockCompressed> entry : COMPRESSED.entrySet()) {
            DustMaterial material = entry.getKey();
            BlockCompressed block = entry.getValue();
            ItemStack itemStack = block.getItem(material);
            OreDictUnifier.registerOre(itemStack, OrePrefix.block, material);
        }

        for (Entry<SolidMaterial, BlockFrame> entry : FRAMES.entrySet()) {
            SolidMaterial material = entry.getKey();
            BlockFrame block = entry.getValue();
            for (int i = 0; i < 16; i++) {
                ItemStack itemStack = new ItemStack(block, 1, i);
                OreDictUnifier.registerOre(itemStack, OrePrefix.frameGt, material);
            }
        }

        for (BlockOre blockOre : ORES) {
            DustMaterial material = blockOre.material;
            for (StoneType stoneType : blockOre.STONE_TYPE.getAllowedValues()) {
                if (stoneType == null) continue;
                ItemStack normalStack = blockOre.getItem(blockOre.getDefaultState()
                    .withProperty(blockOre.STONE_TYPE, stoneType));
                OreDictUnifier.registerOre(normalStack, stoneType.processingPrefix, material);
            }
        }
        for (Material pipeMaterial : CABLE.getEnabledMaterials()) {
            for (Insulation insulation : Insulation.values()) {
                ItemStack itemStack = CABLE.getItem(insulation, pipeMaterial);
                OreDictUnifier.registerOre(itemStack, insulation.getOrePrefix(), pipeMaterial);
            }
        }
        for (Material pipeMaterial : FLUID_PIPE.getEnabledMaterials()) {
            for (FluidPipeType fluidPipeType : FluidPipeType.values()) {
                ItemStack itemStack = FLUID_PIPE.getItem(fluidPipeType, pipeMaterial);
                OreDictUnifier.registerOre(itemStack, fluidPipeType.getOrePrefix(), pipeMaterial);
            }
        }
    }

    private static String statePropertiesToString(Map<IProperty<?>, Comparable<?>> properties) {
        StringBuilder stringbuilder = new StringBuilder();

        List<Entry<IProperty<?>, Comparable<?>>> entries = properties.entrySet().stream()
            .sorted(Comparator.comparing(c -> c.getKey().getName()))
            .collect(Collectors.toList());

        for (Map.Entry<IProperty<?>, Comparable<?>> entry : entries) {
            if (stringbuilder.length() != 0) {
                stringbuilder.append(",");
            }

            IProperty<?> property = entry.getKey();
            stringbuilder.append(property.getName());
            stringbuilder.append("=");
            stringbuilder.append(getPropertyName(property, entry.getValue()));
        }

        if (stringbuilder.length() == 0) {
            stringbuilder.append("normal");
        }

        return stringbuilder.toString();
    }

    @SuppressWarnings("unchecked")
    private static <T extends Comparable<T>> String getPropertyName(IProperty<T> property, Comparable<?> value) {
        return property.getName((T) value);
    }
}