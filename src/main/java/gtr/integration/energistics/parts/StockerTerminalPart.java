package gtr.integration.energistics.parts;

import appeng.api.parts.IPartModel;
import appeng.parts.PartModel;
import appeng.parts.reporting.AbstractPartDisplay;
import gtr.GregTechMod;
import gtr.api.GTValues;
import gtr.integration.energistics.gui.GuiProxy;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nonnull;

public class StockerTerminalPart extends AbstractPartDisplay {
    public static ResourceLocation[] MODELS = new ResourceLocation[]{
            new ResourceLocation(GTValues.MODID, "part/stocker.terminal/on"),		// 0
            new ResourceLocation(GTValues.MODID, "part/stocker.terminal/off"),	// 1
    };
    public static final ResourceLocation MODEL_OFF = MODELS[1];
    public static final ResourceLocation MODEL_ON = MODELS[0];
	public static final IPartModel MODELS_OFF = new PartModel(MODEL_BASE, MODEL_OFF, MODEL_STATUS_OFF);
	public static final IPartModel MODELS_ON = new PartModel(MODEL_BASE, MODEL_ON, MODEL_STATUS_ON);
	public static final IPartModel MODELS_HAS_CHANNEL = new PartModel(MODEL_BASE, MODEL_ON, MODEL_STATUS_HAS_CHANNEL);

    public StockerTerminalPart(final ItemStack is) {
        super(is);
    }

    @Nonnull
	@Override
    public IPartModel getStaticModels() {
        return this.selectModel(MODELS_OFF, MODELS_ON, MODELS_HAS_CHANNEL);
    }

    @Override
    public boolean onPartActivate(final EntityPlayer player, final EnumHand hand, final Vec3d pos) {
        if (super.onPartActivate(player, hand, pos))
            return false;

        if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            BlockPos bPos = this.getTile().getPos();
            player.openGui(GregTechMod.instance, GuiProxy.getOrdinalFromGuiId(0) | this.getSide().ordinal(),
                    player.getEntityWorld(), bPos.getX(), bPos.getY(), bPos.getZ());
        }

        return true;
    }
}