package gtr.api.recipes.crafttweaker;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.BracketHandler;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.mc1120.item.MCItemStack;
import crafttweaker.zenscript.IBracketHandler;
import gtr.api.items.materialitem.MaterialMetaItem;
import gtr.api.items.metaitem.MetaItem;
import gtr.api.items.metaitem.MetaItem.MetaValueItem;
import gtr.api.unification.OreDictUnifier;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.expression.ExpressionCallStatic;
import stanhebben.zenscript.expression.ExpressionString;
import stanhebben.zenscript.parser.Token;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.type.natives.IJavaMethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@BracketHandler
@ZenRegister
public class MetaItemBracketHandler implements IBracketHandler {
    private static final Map<String, ItemStack> metaItemNames = new HashMap<>();

    private final IJavaMethod method;

    public MetaItemBracketHandler() {
        this.method = CraftTweakerAPI.getJavaMethod(MetaItemBracketHandler.class, "getMetaItem", String.class);
    }

    public static IItemStack getMetaItem(String name) {
        ItemStack item = metaItemNames.get(name);
        if(item != null) {
            return new MCItemStack(item);
        } else {
            return null;
        }
    }


    @SuppressWarnings("unchecked")
    public static void rebuildComponentRegistry() {
        metaItemNames.clear();
        for (MetaItem<?> item : MetaItem.getMetaItems()) {
            if (item instanceof MaterialMetaItem) {
                for(ItemStack entry : ((MaterialMetaItem) item).getEntries()) {
                    metaItemNames.put(OreDictUnifier.getPrefix(entry).name() + OreDictUnifier.getMaterial(entry).material.toCamelCaseString(), entry);
                }
            }
            for(MetaValueItem entry : item.getAllItems()) {
                if (!entry.unlocalizedName.equals("meta_item")) {
                    metaItemNames.put(entry.unlocalizedName, entry.getStackForm());
                }
            }
        }
    }

    @Override
    public IZenSymbol resolve(IEnvironmentGlobal environment, List<Token> tokens) {
        if ((tokens.size() < 3)) return null;
        if (!tokens.get(0).getValue().equalsIgnoreCase("metaitem")) return null;
        if (!tokens.get(1).getValue().equals(":")) return null;
        StringBuilder nameBuilder = new StringBuilder();
        for (int i = 2; i < tokens.size(); i++) {
            nameBuilder.append(tokens.get(i).getValue());
        }
        return position -> new ExpressionCallStatic(position, environment, method,
            new ExpressionString(position, nameBuilder.toString()));
    }

}
