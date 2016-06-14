package com.spiderrobotman.Gamemode4Engine.handler;

import net.minecraft.server.v1_10_R1.ContainerUtil;
import net.minecraft.server.v1_10_R1.EntityHuman;
import net.minecraft.server.v1_10_R1.ItemStack;
import net.minecraft.server.v1_10_R1.PlayerInventory;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_10_R1.inventory.CraftInventory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.lang.reflect.Field;

/**
 * Project: Gamemode4Engine
 * Author: SpiderRobotMan
 * Date: May 18 2016
 * Website: http://www.spiderrobotman.com
 */

public class SpecialPlayerInventory extends PlayerInventory {

    private final CraftInventory inventory = new CraftInventory(this);
    private final ItemStack[] extra = new ItemStack[4];
    private CraftPlayer owner;
    private ItemStack[][] arrays;
    private boolean playerOnline;

    public SpecialPlayerInventory(Player p, boolean online) {
        super(((CraftPlayer) p).getHandle());
        this.owner = (CraftPlayer) p;
        this.playerOnline = online;
        reflectContents(getClass().getSuperclass(), player.inventory, this);
    }

    private void reflectContents(Class clazz, PlayerInventory src, PlayerInventory dest) {
        try {
            Field itemsField = clazz.getDeclaredField("items");
            itemsField.setAccessible(true);
            itemsField.set(dest, src.items);

            Field armorField = clazz.getDeclaredField("armor");
            armorField.setAccessible(true);
            armorField.set(dest, src.armor);

            Field extraSlotsField = clazz.getDeclaredField("extraSlots");
            extraSlotsField.setAccessible(true);
            extraSlotsField.set(dest, src.extraSlots);
        } catch (NoSuchFieldException | SecurityException | IllegalAccessException | IllegalArgumentException e) {
            e.printStackTrace();
        }

        arrays = new ItemStack[][]{this.items, this.armor, this.extraSlots, this.extra};
    }

    private void linkInventory(PlayerInventory inventory) {
        reflectContents(inventory.getClass(), inventory, this);
    }

    public Inventory getBukkitInventory() {
        return inventory;
    }

    private boolean inventoryRemovalCheck(boolean save) {
        boolean offline = transaction.isEmpty() && !playerOnline;

        if (offline && save) {
            owner.saveData();
        }

        return offline;
    }

    public void playerOnline(Player player) {
        if (!playerOnline) {
            owner = (CraftPlayer) player;
            this.player = owner.getHandle();
            linkInventory(owner.getHandle().inventory);
            playerOnline = true;
        }
    }

    public boolean playerOffline() {
        playerOnline = false;
        return inventoryRemovalCheck(false);
    }

    @Override
    public void onClose(CraftHumanEntity who) {
        super.onClose(who);
        inventoryRemovalCheck(true);
    }

    @Override
    public ItemStack[] getContents() {
        ItemStack[] contents = new ItemStack[getSize()];
        System.arraycopy(this.items, 0, contents, 0, this.items.length);
        System.arraycopy(this.armor, 0, contents, this.items.length, this.armor.length);
        System.arraycopy(this.extraSlots, 0, contents, this.items.length + this.armor.length, this.extraSlots.length);
        return contents;
    }

    @Override
    public int getSize() {
        return super.getSize() + 4;
    }

    @Override
    public ItemStack getItem(int i) {
        ItemStack[] is = null;
        ItemStack[][] contents = this.arrays;
        int j = contents.length;

        for (ItemStack[] is2 : contents) {
            if (i < is2.length) {
                is = is2;
                break;
            }

            i -= is2.length;
        }

        if (is == this.items) {
            i = getReversedItemSlotNum(i);
        } else if (is == this.armor) {
            i = getReversedArmorSlotNum(i);
        }

        return is == null ? null : is[i];
    }

    @Override
    public ItemStack splitStack(int i, int j) {
        ItemStack[] is = null;
        ItemStack[][] contents = this.arrays;
        int k = contents.length;

        for (ItemStack[] is2 : contents) {
            if (i < is2.length) {
                is = is2;
                break;
            }

            i -= is2.length;
        }

        if (is == this.items) {
            i = getReversedItemSlotNum(i);
        } else if (is == this.armor) {
            i = getReversedArmorSlotNum(i);
        }

        return is != null && is[i] != null ? ContainerUtil.a(is, i, j) : null;
    }

    @Override
    public ItemStack splitWithoutUpdate(int i) {
        ItemStack[] is = null;
        ItemStack[][] contents = this.arrays;
        int j = contents.length;

        for (ItemStack[] is2 : contents) {
            if (i < is2.length) {
                is = is2;
                break;
            }

            i -= is2.length;
        }

        if (is != null && is[i] != null) {
            if (is == this.items) {
                i = getReversedItemSlotNum(i);
            } else if (is == this.armor) {
                i = getReversedArmorSlotNum(i);
            }

            Object object = is[i];
            is[i] = null;
            return (ItemStack) object;
        } else {
            return null;
        }
    }

    @Override
    public void setItem(int i, ItemStack itemStack) {
        ItemStack[] is = null;
        ItemStack[][] contents = this.arrays;
        int j = contents.length;

        for (ItemStack[] is2 : contents) {
            if (i < is2.length) {
                is = is2;
                break;
            }

            i -= is2.length;
        }

        if (is != null) {
            if (is == this.items) {
                i = getReversedItemSlotNum(i);
            } else if (is == this.armor) {
                i = getReversedArmorSlotNum(i);
            } else if (is == this.extra) {
                owner.getHandle().drop(itemStack, true);
                itemStack = null;
            }

            is[i] = itemStack;

            owner.getHandle().defaultContainer.b();
        }
    }

    private int getReversedItemSlotNum(int i) {
        return (i >= 27) ? (i - 27) : (i + 9);
    }

    private int getReversedArmorSlotNum(int i) {
        if (i == 0) return 3;
        if (i == 1) return 2;
        if (i == 2) return 1;
        return (i == 3) ? 0 : i;
    }

    @Override
    public boolean hasCustomName() {
        return true;
    }

    @Override
    public String getName() {
        return player.getName();
    }

    @Override
    public boolean a(EntityHuman entityhuman) {
        return true;
    }

    @Override
    public void update() {
        super.update();
        player.inventory.update();
    }

}
