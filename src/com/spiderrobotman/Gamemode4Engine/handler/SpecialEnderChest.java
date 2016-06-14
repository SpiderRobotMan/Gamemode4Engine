package com.spiderrobotman.Gamemode4Engine.handler;

/**
 * Project: Gamemode4Engine
 * Author: SpiderRobotMan
 * Date: May 18 2016
 * Website: http://www.spiderrobotman.com
 */

import net.minecraft.server.v1_10_R1.InventoryEnderChest;
import net.minecraft.server.v1_10_R1.InventorySubcontainer;
import net.minecraft.server.v1_10_R1.ItemStack;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_10_R1.inventory.CraftInventory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.lang.reflect.Field;

public class SpecialEnderChest extends InventorySubcontainer {

    private final CraftInventory inventory = new CraftInventory(this);
    private final InventoryEnderChest enderChest;
    private CraftPlayer owner;
    private boolean playerOnline;

    public SpecialEnderChest(Player p, boolean online) {
        this(p, ((CraftPlayer) p).getHandle().getEnderChest(), online);
    }

    private SpecialEnderChest(Player p, InventoryEnderChest enderChest, boolean online) {
        super(enderChest.getName(), enderChest.hasCustomName(), enderChest.getSize());
        this.owner = (CraftPlayer) p;
        this.enderChest = enderChest;
        this.playerOnline = online;
        reflectContents(getClass().getSuperclass(), this, this.enderChest.getContents());
    }

    private void saveOnExit() {
        if (transaction.isEmpty() && !playerOnline) {
            owner.saveData();
        }
    }

    private void reflectContents(Class clazz, InventorySubcontainer enderChest, ItemStack[] items) {
        try {
            Field itemsField = clazz.getDeclaredField("items");
            itemsField.setAccessible(true);
            itemsField.set(enderChest, items);
        } catch (NoSuchFieldException | SecurityException | IllegalAccessException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private void linkInventory(InventoryEnderChest inventory) {
        reflectContents(inventory.getClass(), inventory, this.items);
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

    public void playerOnline(Player p) {
        if (!playerOnline) {
            owner = (CraftPlayer) p;
            linkInventory(((CraftPlayer) p).getHandle().getEnderChest());
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
    public InventoryHolder getOwner() {
        return this.owner;
    }

    @Override
    public void update() {
        super.update();
        enderChest.update();
    }
}
