package gtr.integration.jei.multiblock.infos;

import com.google.common.collect.Lists;
import gtr.api.GTValues;
import gtr.api.metatileentity.multiblock.MultiblockControllerBase;
import gtr.common.blocks.BlockMetalCasing.MetalCasingType;
import gtr.common.blocks.MetaBlocks;
import gtr.common.metatileentities.MetaTileEntities;
import gtr.integration.jei.multiblock.MultiblockInfoPage;
import gtr.integration.jei.multiblock.MultiblockShapeInfo;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;

import java.util.List;

public class DistillationTowerInfo extends MultiblockInfoPage {

    @Override
    public MultiblockControllerBase getController() {
        return MetaTileEntities.DISTILLATION_TOWER;
    }

    @Override
    public List<MultiblockShapeInfo> getMatchingShapes() {
        MultiblockShapeInfo shapeInfo = MultiblockShapeInfo.builder()
            .aisle("EXX", "XXX", "XXX", "XXX", "XXX", "XXX")
            .aisle("SFX", "X#X", "X#X", "X#X", "X#X", "XXX")
            .aisle("IXX", "HXX", "HXX", "HXX", "HXX", "HXX")
            .where('#', Blocks.AIR.getDefaultState())
            .where('X', MetaBlocks.METAL_CASING.getState(MetalCasingType.STAINLESS_CLEAN))
            .where('S', MetaTileEntities.DISTILLATION_TOWER, EnumFacing.WEST)
            .where('E', MetaTileEntities.ENERGY_INPUT_HATCH[GTValues.EV], EnumFacing.WEST)
            .where('I', MetaTileEntities.ITEM_EXPORT_BUS[GTValues.EV], EnumFacing.WEST)
            .where('F', MetaTileEntities.FLUID_IMPORT_HATCH[GTValues.EV], EnumFacing.DOWN)
            .where('H', MetaTileEntities.FLUID_EXPORT_HATCH[GTValues.EV], EnumFacing.WEST)
            .build();
        return Lists.newArrayList(shapeInfo);
    }

    @Override
    public String[] getDescription() {
        return new String[]{I18n.format("gtr.multiblock.distillation_tower.description")};
    }

    @Override
    public float getDefaultZoom() {
        return 0.7f;
    }

}
