package gtr.integration.jei.multiblock;

import gtr.api.metatileentity.multiblock.MultiblockControllerBase;

import java.util.List;

public abstract class MultiblockInfoPage {

    public abstract MultiblockControllerBase getController();

    public abstract List<MultiblockShapeInfo> getMatchingShapes();

    public abstract String[] getDescription();

    public float getDefaultZoom() {
        return 1.0f;
    }

}
