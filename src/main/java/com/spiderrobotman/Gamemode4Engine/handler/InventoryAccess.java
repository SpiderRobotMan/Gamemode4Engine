package com.spiderrobotman.Gamemode4Engine.handler;

import com.spiderrobotman.Gamemode4Engine.util.TextUtil;
import net.minecraft.server.v1_10_R1.IInventory;
import org.bukkit.craftbukkit.v1_10_R1.inventory.CraftInventory;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;

import java.lang.reflect.Field;

/**
 * Project: Gamemode4Engine
 * Author: SpiderRobotMan
 * Date: May 18 2016
 * Website: http://www.spiderrobotman.com
 */
public class InventoryAccess {
    public InventoryAccess() {
    }

    public boolean check(Inventory inventory, HumanEntity player) {
        IInventory inv = grabInventory(inventory);

        if (inv instanceof SpecialPlayerInventory) {
            if (!player.hasPermission("gm4.openinv.edit")) {
                return false;
            }
        } else if (inv instanceof SpecialEnderChest) {
            if (!player.hasPermission("gm4.openinv.edit")) {
                return false;
            }
        }

        return true;
    }

    private IInventory grabInventory(Inventory inventory) {
        if (inventory instanceof CraftInventory) {
            return ((CraftInventory) inventory).getInventory();
        }

        Class<? extends Inventory> clazz = inventory.getClass();
        IInventory result = null;
        for (Field f : clazz.getDeclaredFields()) {
            f.setAccessible(true);

            if (IInventory.class.isAssignableFrom(f.getDeclaringClass())) {
                try {
                    result = (IInventory) f.get(inventory);
                } catch (Exception e) {
                    TextUtil.logError("Inventory error! ERROR: " + e.getMessage());
                }
            }
        }

        return result;
    }
}
