package gtr.loaders.dungeon;

import gtr.api.unification.OreDictUnifier;
import gtr.api.unification.material.Materials;
import gtr.api.unification.ore.OrePrefix;
import gtr.api.util.GTLog;
import gtr.common.ConfigHolder;
import gtr.common.items.MetaItems;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.loot.LootTableList;

public class DungeonLootLoader {

    public static void init() {
        GTLog.logger.info("Registering dungeon loot...");
        ChestGenHooks.init();
        if (ConfigHolder.increaseDungeonLoot) {
            ChestGenHooks.addRolls(LootTableList.CHESTS_SPAWN_BONUS_CHEST, 2, 4);
            ChestGenHooks.addRolls(LootTableList.CHESTS_SIMPLE_DUNGEON, 1, 3);
            ChestGenHooks.addRolls(LootTableList.CHESTS_DESERT_PYRAMID, 2, 4);
            ChestGenHooks.addRolls(LootTableList.CHESTS_JUNGLE_TEMPLE, 4, 8);
            ChestGenHooks.addRolls(LootTableList.CHESTS_JUNGLE_TEMPLE_DISPENSER, 0, 2);
            ChestGenHooks.addRolls(LootTableList.CHESTS_ABANDONED_MINESHAFT, 1, 3);
            ChestGenHooks.addRolls(LootTableList.CHESTS_VILLAGE_BLACKSMITH, 2, 6);
            ChestGenHooks.addRolls(LootTableList.CHESTS_STRONGHOLD_CROSSING, 2, 4);
            ChestGenHooks.addRolls(LootTableList.CHESTS_STRONGHOLD_CORRIDOR, 2, 4);
            ChestGenHooks.addRolls(LootTableList.CHESTS_STRONGHOLD_LIBRARY, 4, 8);
            ChestGenHooks.addRolls(LootTableList.CHESTS_NETHER_BRIDGE, 2, 5);
            ChestGenHooks.addRolls(LootTableList.CHESTS_END_CITY_TREASURE, 5, 7);
            ChestGenHooks.addRolls(LootTableList.CHESTS_WOODLAND_MANSION, 4, 6);
        }

        ChestGenHooks.addItem(LootTableList.CHESTS_SPAWN_BONUS_CHEST, MetaItems.BOTTLE_PURPLE_DRINK.getStackForm(), 8, 16, 2);

        ChestGenHooks.addItem(LootTableList.CHESTS_SIMPLE_DUNGEON, MetaItems.BOTTLE_PURPLE_DRINK.getStackForm(), 8, 16, 40);
        ChestGenHooks.addItem(LootTableList.CHESTS_SIMPLE_DUNGEON, OreDictUnifier.get(OrePrefix.ingot, Materials.Silver, 1), 1, 6, 30);
        ChestGenHooks.addItem(LootTableList.CHESTS_SIMPLE_DUNGEON, OreDictUnifier.get(OrePrefix.ingot, Materials.Lead, 1), 1, 6, 7);
        ChestGenHooks.addItem(LootTableList.CHESTS_SIMPLE_DUNGEON, OreDictUnifier.get(OrePrefix.ingot, Materials.Steel, 1), 1, 6, 15);
        ChestGenHooks.addItem(LootTableList.CHESTS_SIMPLE_DUNGEON, OreDictUnifier.get(OrePrefix.ingot, Materials.Bronze, 1), 1, 6, 15);
        ChestGenHooks.addItem(LootTableList.CHESTS_SIMPLE_DUNGEON, OreDictUnifier.get(OrePrefix.ingot, Materials.Manganese, 1), 1, 6, 15);
        ChestGenHooks.addItem(LootTableList.CHESTS_SIMPLE_DUNGEON, OreDictUnifier.get(OrePrefix.gem, Materials.Emerald, 1), 1, 6, 5);
        ChestGenHooks.addItem(LootTableList.CHESTS_SIMPLE_DUNGEON, OreDictUnifier.get(OrePrefix.gem, Materials.Ruby, 1), 1, 6, 5);
        ChestGenHooks.addItem(LootTableList.CHESTS_SIMPLE_DUNGEON, OreDictUnifier.get(OrePrefix.gem, Materials.Sapphire, 1), 1, 6, 5);
        ChestGenHooks.addItem(LootTableList.CHESTS_SIMPLE_DUNGEON, OreDictUnifier.get(OrePrefix.gem, Materials.Peridot, 1), 1, 6, 5);
        ChestGenHooks.addItem(LootTableList.CHESTS_SIMPLE_DUNGEON, OreDictUnifier.get(OrePrefix.gem, Materials.Olivine, 1), 1, 6, 5);
        ChestGenHooks.addItem(LootTableList.CHESTS_SIMPLE_DUNGEON, OreDictUnifier.get(OrePrefix.dust, Materials.Aluminium, 1), 1, 6, 10);
        ChestGenHooks.addItem(LootTableList.CHESTS_SIMPLE_DUNGEON, OreDictUnifier.get(OrePrefix.dust, Materials.Neodymium, 1), 1, 6, 10);
        ChestGenHooks.addItem(LootTableList.CHESTS_SIMPLE_DUNGEON, OreDictUnifier.get(OrePrefix.dust, Materials.Chrome, 1), 1, 3, 10);

        ChestGenHooks.addItem(LootTableList.CHESTS_DESERT_PYRAMID, OreDictUnifier.get(OrePrefix.ingot, Materials.Silver, 1), 4, 16, 6);
        ChestGenHooks.addItem(LootTableList.CHESTS_DESERT_PYRAMID, OreDictUnifier.get(OrePrefix.ingot, Materials.Platinum, 1), 2, 8, 3);
        ChestGenHooks.addItem(LootTableList.CHESTS_DESERT_PYRAMID, OreDictUnifier.get(OrePrefix.gem, Materials.Ruby, 1), 2, 8, 1);
        ChestGenHooks.addItem(LootTableList.CHESTS_DESERT_PYRAMID, OreDictUnifier.get(OrePrefix.gem, Materials.Sapphire, 1), 2, 8, 3);
        ChestGenHooks.addItem(LootTableList.CHESTS_DESERT_PYRAMID, OreDictUnifier.get(OrePrefix.gem, Materials.Peridot, 1), 2, 8, 4);
        ChestGenHooks.addItem(LootTableList.CHESTS_DESERT_PYRAMID, OreDictUnifier.get(OrePrefix.gem, Materials.Olivine, 1), 2, 8, 4);

        ChestGenHooks.addItem(LootTableList.CHESTS_JUNGLE_TEMPLE, OreDictUnifier.get(OrePrefix.ingot, Materials.Bronze, 1), 4, 16, 12);
        ChestGenHooks.addItem(LootTableList.CHESTS_JUNGLE_TEMPLE, OreDictUnifier.get(OrePrefix.gem, Materials.Ruby, 1), 2, 8, 2);
        ChestGenHooks.addItem(LootTableList.CHESTS_JUNGLE_TEMPLE, OreDictUnifier.get(OrePrefix.gem, Materials.Sapphire, 1), 2, 8, 2);
        ChestGenHooks.addItem(LootTableList.CHESTS_JUNGLE_TEMPLE, OreDictUnifier.get(OrePrefix.gem, Materials.Peridot, 1), 2, 8, 2);
        ChestGenHooks.addItem(LootTableList.CHESTS_JUNGLE_TEMPLE, OreDictUnifier.get(OrePrefix.gem, Materials.Olivine, 1), 2, 8, 2);

        ChestGenHooks.addItem(LootTableList.CHESTS_JUNGLE_TEMPLE_DISPENSER, new ItemStack(Items.FIRE_CHARGE, 1), 2, 8, 15);

        ChestGenHooks.addItem(LootTableList.CHESTS_ABANDONED_MINESHAFT, OreDictUnifier.get(OrePrefix.ingot, Materials.Silver, 1), 1, 4, 6);
        ChestGenHooks.addItem(LootTableList.CHESTS_ABANDONED_MINESHAFT, OreDictUnifier.get(OrePrefix.ingot, Materials.Lead, 1), 1, 4, 3);
        ChestGenHooks.addItem(LootTableList.CHESTS_ABANDONED_MINESHAFT, OreDictUnifier.get(OrePrefix.ingot, Materials.Steel, 1), 1, 4, 6);
        ChestGenHooks.addItem(LootTableList.CHESTS_ABANDONED_MINESHAFT, OreDictUnifier.get(OrePrefix.ingot, Materials.Bronze, 1), 1, 4, 6);
        ChestGenHooks.addItem(LootTableList.CHESTS_ABANDONED_MINESHAFT, OreDictUnifier.get(OrePrefix.gem, Materials.Sapphire, 1), 1, 4, 2);
        ChestGenHooks.addItem(LootTableList.CHESTS_ABANDONED_MINESHAFT, OreDictUnifier.get(OrePrefix.gem, Materials.Peridot, 1), 1, 4, 2);
        ChestGenHooks.addItem(LootTableList.CHESTS_ABANDONED_MINESHAFT, OreDictUnifier.get(OrePrefix.gem, Materials.Olivine, 1), 1, 4, 2);
        ChestGenHooks.addItem(LootTableList.CHESTS_ABANDONED_MINESHAFT, OreDictUnifier.get(OrePrefix.gem, Materials.Ruby, 1), 1, 4, 2);
        ChestGenHooks.addItem(LootTableList.CHESTS_ABANDONED_MINESHAFT, OreDictUnifier.get(OrePrefix.gem, Materials.Emerald, 1), 1, 4, 2);

        ChestGenHooks.addItem(LootTableList.CHESTS_VILLAGE_BLACKSMITH, OreDictUnifier.get(OrePrefix.dust, Materials.Chrome, 1), 1, 4, 3);
        ChestGenHooks.addItem(LootTableList.CHESTS_VILLAGE_BLACKSMITH, OreDictUnifier.get(OrePrefix.dust, Materials.Neodymium, 1), 2, 8, 3);
        ChestGenHooks.addItem(LootTableList.CHESTS_VILLAGE_BLACKSMITH, OreDictUnifier.get(OrePrefix.ingot, Materials.Manganese, 1), 2, 8, 6);
        ChestGenHooks.addItem(LootTableList.CHESTS_VILLAGE_BLACKSMITH, OreDictUnifier.get(OrePrefix.ingot, Materials.Steel, 1), 4, 12, 6);
        ChestGenHooks.addItem(LootTableList.CHESTS_VILLAGE_BLACKSMITH, OreDictUnifier.get(OrePrefix.ingot, Materials.Bronze, 1), 4, 12, 6);
        ChestGenHooks.addItem(LootTableList.CHESTS_VILLAGE_BLACKSMITH, OreDictUnifier.get(OrePrefix.ingot, Materials.Brass, 1), 4, 12, 6);
        ChestGenHooks.addItem(LootTableList.CHESTS_VILLAGE_BLACKSMITH, OreDictUnifier.get(OrePrefix.ingot, Materials.DamascusSteel, 1), 4, 12, 1);
    }
}
