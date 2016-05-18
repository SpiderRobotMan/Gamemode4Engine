package com.spiderrobotman.Gamemode4Engine.handler;

/**
 * Project: Gamemode4Engine
 * Author: SpiderRobotMan
 * Date: May 18 2016
 * Website: http://www.spiderrobotman.com
 */

import net.minecraft.server.v1_9_R2.InventoryEnderChest;
import net.minecraft.server.v1_9_R2.InventorySubcontainer;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_9_R2.inventory.CraftInventory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

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
        this.items = this.enderChest.getContents();
        this.playerOnline = online;
    }

    private void saveOnExit() {
        if (transaction.isEmpty() && !playerOnline) {
            owner.saveData();
        }
    }

    private void linkInventory(InventoryEnderChest inventory) {
        inventory.items = this.items;
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
