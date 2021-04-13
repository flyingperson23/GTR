package gtr.integration.energistics.items;

public class StandardModMetaItem extends ModMetaItem<ModMetaItem<?>.ModMetaValueItem> {
    public StandardModMetaItem(short metaItemOffset) {
        super(metaItemOffset);
    }

    @Override
    protected ModMetaValueItem constructMetaValueItem(short metaValue, String unlocalizedName) {
        return new ModMetaValueItem(metaValue, unlocalizedName);
    }
}
