package gtr.common.pipelike.fluidpipe;

import gtr.api.pipenet.block.material.IMaterialPipeType;
import gtr.api.unification.ore.OrePrefix;

public enum FluidPipeType implements IMaterialPipeType<FluidPipeProperties> {

    TINY_OPAQUE("tiny", 0.25f, 1, OrePrefix.pipeTiny, true),
    SMALL_OPAQUE("small", 0.375f, 2, OrePrefix.pipeSmall, true),
    MEDIUM_OPAQUE("medium", 0.50f, 4, OrePrefix.pipeMedium, true),
    LARGE_OPAQUE("large", 0.75f, 8, OrePrefix.pipeLarge, true);


    public final String name;
    public final float thickness;
    public final int capacityMultiplier;
    public final OrePrefix orePrefix;
    public final boolean opaque;

    FluidPipeType(String name, float thickness, int capacityMultiplier, OrePrefix orePrefix, boolean opaque) {
        this.name = name;
        this.thickness = thickness;
        this.capacityMultiplier = capacityMultiplier;
        this.orePrefix = orePrefix;
        this.opaque = opaque;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public float getThickness() {
        return thickness;
    }

    @Override
    public OrePrefix getOrePrefix() {
        return orePrefix;
    }

    @Override
    public FluidPipeProperties modifyProperties(FluidPipeProperties baseProperties) {
        return new FluidPipeProperties(
            baseProperties.maxFluidTemperature,
            baseProperties.throughput * capacityMultiplier,
            baseProperties.gasProof);
    }

    @Override
    public boolean isPaintable() {
        return true;
    }
}
