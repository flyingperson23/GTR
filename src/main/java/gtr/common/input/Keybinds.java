package gtr.common.input;

import gtr.common.armor.ArmorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;

public class Keybinds {
    // Just registery of all used keys
    public static final List<Key> REGISTERY = new ArrayList<>();
    // Logical server-sided variable, where is state of any keys from players
    public static final Map<EntityPlayer, List<Key>> PLAYER_KEYS = new HashMap<>();

    @SideOnly(Side.CLIENT)
    private static List<KeyBinding> bindings;

    @SideOnly(Side.CLIENT)
    public static void initBinds() {
        bindings = Arrays.asList(null,
                null,
                Minecraft.getMinecraft().gameSettings.keyBindForward,
                Minecraft.getMinecraft().gameSettings.keyBindJump,
                Minecraft.getMinecraft().gameSettings.keyBindSneak,
                Minecraft.getMinecraft().gameSettings.keyBindSprint,
                null,
                Minecraft.getMinecraft().gameSettings.keyBindLeft,
                Minecraft.getMinecraft().gameSettings.keyBindRight,
                Minecraft.getMinecraft().gameSettings.keyBindBack);
    }

    @SideOnly(Side.CLIENT)
    public static void registerClient() {
        for (Key key : REGISTERY) {
            if (!ArrayUtils.contains(Minecraft.getMinecraft().gameSettings.keyBindings, key.getBind()))
                ClientRegistry.registerKeyBinding(key.getBind());
        }
    }

    public static void register() {
        for (EnumKey type : EnumKey.values()) {
            if (ArmorUtils.SIDE.isClient()) {
                REGISTERY.add(new Key(type, bindings.get(type.getID())));
            } else {
                REGISTERY.add(new Key(type));
            }
        }
    }
}
