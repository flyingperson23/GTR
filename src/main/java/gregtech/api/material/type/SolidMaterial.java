package gregtech.api.material.type;

import com.google.common.collect.ImmutableList;
import gregtech.api.material.Element;
import gregtech.api.material.MaterialIconSet;
import gregtech.api.objects.MaterialStack;
import gregtech.api.util.EnchantmentData;
import net.minecraft.enchantment.Enchantment;

import java.util.ArrayList;

import static gregtech.api.material.type.DustMaterial.MatFlags.GENERATE_PLATE;
import static gregtech.api.material.type.Material.MatFlags.createFlag;
import static gregtech.api.material.type.SolidMaterial.MatFlags.GENERATE_GEAR;
import static gregtech.api.material.type.SolidMaterial.MatFlags.GENERATE_LONG_ROD;
import static gregtech.api.material.type.SolidMaterial.MatFlags.GENERATE_ROD;

public class SolidMaterial extends DustMaterial {

    public static final class MatFlags {

        public static final long GENERATE_ROD = createFlag(20);
        public static final long GENERATE_GEAR = createFlag(21);
        public static final long GENERATE_LONG_ROD = createFlag(22);

        /**
         * If this Material is grindable with a simple Mortar
         */
        public static final long MORTAR_GRINDABLE = createFlag(24);

    }

    /**
     * Speed of tools made from this material
     * Default value is 1.0f
     */
    public final float toolSpeed;

    /**
     * Quality (tier) of tools made from this material
     * Equal to 0 for materials that can't be used for tools
     */
    public final int toolQuality;

    /**
     * Durability of tools made from this material
     * Equal to 0 for materials that can't be used for tools
     */
    public final int toolDurability;

    /**
     * Enchantment to be applied to tools made from this material
     */
    public final ArrayList<EnchantmentData> toolEnchantments = new ArrayList<>();

    /**
     * Material specified here will be required as handle to make tool
     * from this material
     */
    public SolidMaterial handleMaterial;

    /**
     * Macerating any item of this material will result material
     * specified in this field
     */
    public DustMaterial macerateInto = this;

    public SolidMaterial(int metaItemSubId, String name, String defaultLocalName, int materialRGB, MaterialIconSet materialIconSet, ImmutableList<MaterialStack> materialComponents, long materialGenerationFlags, Element element, float toolSpeed, int toolQuality, int toolDurability) {
        super(metaItemSubId, name, defaultLocalName, materialRGB, materialIconSet, materialComponents, materialGenerationFlags, element);
        this.toolSpeed = toolSpeed;
        this.toolQuality = toolQuality;
        this.toolDurability = toolDurability;
        this.directSmelting = this;
    }

    public SolidMaterial(int metaItemSubId, String name, String defaultLocalName, int materialRGB, MaterialIconSet materialIconSet, ImmutableList<MaterialStack> materialComponents, long materialGenerationFlags, Element element) {
        this(metaItemSubId, name, defaultLocalName, materialRGB, materialIconSet, materialComponents, materialGenerationFlags, element, 0, 0, 0);
    }

    public SolidMaterial(int metaItemSubId, String name, String defaultLocalName, int materialRGB, MaterialIconSet materialIconSet, ImmutableList<MaterialStack> materialComponents, long materialGenerationFlags) {
        this(metaItemSubId, name, defaultLocalName, materialRGB, materialIconSet, materialComponents, materialGenerationFlags, null, 0, 0, 0);
    }

    @Override
    protected long verifyMaterialBits(long generationBits) {
        if((generationBits & GENERATE_GEAR) > 0) {
            generationBits |= GENERATE_PLATE;
            generationBits |= GENERATE_ROD;
        }
        if((generationBits & GENERATE_LONG_ROD) > 0) {
            generationBits |= GENERATE_ROD;
        }
        return super.verifyMaterialBits(generationBits);
    }

    public SolidMaterial setMaceratingInto(DustMaterial macerateInto) {
        this.macerateInto = macerateInto;
        return this;
    }

    public void addEnchantmentForTools(Enchantment enchantment, int level) {
        toolEnchantments.add(new EnchantmentData(enchantment, level));
    }

}