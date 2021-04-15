package gtr.common.blocks;

import gtr.api.capability.GregtechCapabilities;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving.SpawnPlacementType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nullable;

public class BlockTurbineCasing extends VariantBlock<BlockTurbineCasing.TurbineCasingType> {

    public BlockTurbineCasing() {
        super(Material.IRON);
        setTranslationKey("turbine_casing");
        setHardness(5.0f);
        setResistance(10.0f);
        setSoundType(SoundType.METAL);
        setHarvestLevel("wrench", 2);
        setDefaultState(getState(TurbineCasingType.BRONZE_GEARBOX));
    }

    @Override
    public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, SpawnPlacementType type) {
        return false;
    }

    @Override
    public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player) {
        return player.getHeldItemMainhand().hasCapability(GregtechCapabilities.CAPABILITY_WRENCH, null);
    }

    @Nullable
    @Override
    public String getHarvestTool(IBlockState state) {
        return "wrench";
    }

    public enum TurbineCasingType implements IStringSerializable {

        BRONZE_GEARBOX("bronze_gearbox"),
        STEEL_GEARBOX("steel_gearbox"),
        TITANIUM_GEARBOX("titanium_gearbox"),

        STEEL_TURBINE_CASING("steel_turbine_casing"),
        TITANIUM_TURBINE_CASING("titanium_turbine_casing"),
        STAINLESS_TURBINE_CASING("stainless_turbine_casing"),
        TUNGSTENSTEEL_TURBINE_CASING("tungstensteel_turbine_casing");

        private final String name;

        TurbineCasingType(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return this.name;
        }
    }

}
