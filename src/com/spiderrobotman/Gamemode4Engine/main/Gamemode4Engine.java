package com.spiderrobotman.Gamemode4Engine.main;

import com.spiderrobotman.Gamemode4Engine.command.*;
import com.spiderrobotman.Gamemode4Engine.data.ConfigManager;
import com.spiderrobotman.Gamemode4Engine.data.DatabaseManager;
import com.spiderrobotman.Gamemode4Engine.handler.InventoryAccess;
import com.spiderrobotman.Gamemode4Engine.handler.PlayerData;
import com.spiderrobotman.Gamemode4Engine.handler.SpecialEnderChest;
import com.spiderrobotman.Gamemode4Engine.handler.SpecialPlayerInventory;
import com.spiderrobotman.Gamemode4Engine.listeners.PlayerListener;
import com.spiderrobotman.Gamemode4Engine.util.TPS;
import com.spiderrobotman.Gamemode4Engine.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Project: Gamemode4Engine
 * Author: SpiderRobotMan
 * Date: May 09 2016
 * Website: http://www.spiderrobotman.com
 */

public class Gamemode4Engine extends JavaPlugin {

    public static DatabaseManager db;
    public static DatabaseManager adb;
    public static ConfigManager.Config config;
    public static ConfigManager.Config nicks;
    public static ConfigManager.Config warps;
    public static ConfigManager.Config protect;
    private static Gamemode4Engine plugin;
    private final Map<UUID, SpecialPlayerInventory> inventories = new HashMap<>();
    private final Map<UUID, SpecialEnderChest> enderChests = new HashMap<>();
    private PlayerData playerLoader;
    private InventoryAccess inventoryAccess;

    public static Gamemode4Engine plugin() {
        return plugin;
    }

    public void onEnable() {
        plugin = this;
        TextUtil.logInfo("starting engine....");
        TextUtil.logInfo("registering listeners...");
        registerEvents(plugin, new PlayerListener());

        TextUtil.logInfo("defining configuration files...");
        ConfigManager c = new ConfigManager(this);
        config = c.getConfig("config.yml").copyDefaults(true).save();
        nicks = c.getConfig("nicks.yml").copyDefaults(true).save();
        warps = c.getConfig("warps.yml").copyDefaults(true).save();
        protect = c.getConfig("protected.yml").copyDefaults(true).save();
        TextUtil.logInfo("opening database connections...");
        db = new DatabaseManager(config.get().getString("main_database.host"), config.get().getString("main_database.port"), config.get().getString("main_database.user"), config.get().getString("main_database.password"), config.get().getString("main_database.database"));
        adb = new DatabaseManager(config.get().getString("access_database.host"), config.get().getString("access_database.port"), config.get().getString("access_database.user"), config.get().getString("access_database.password"), config.get().getString("access_database.database"));
        TextUtil.logInfo("updating data...");
        playerLoader = new PlayerData();
        inventoryAccess = new InventoryAccess();

        WarpCommand.updateWarpMemory();
        RestrictCommand.restrictTo = config.get().getString("restrict");

        TextUtil.logInfo("registering commands...");
        getCommand("ban").setExecutor(new BanCommand());
        getCommand("tempban").setExecutor(new TempBanCommand());
        getCommand("kick").setExecutor(new KickCommand());
        getCommand("unban").setExecutor(new UnbanCommand());
        getCommand("tp").setExecutor(new TPCommand());
        getCommand("nick").setExecutor(new NickCommand());
        getCommand("openinv").setExecutor(new OpenInvCommand());
        getCommand("openend").setExecutor(new OpenEndCommand());
        getCommand("back").setExecutor(new BackCommand());
        getCommand("realname").setExecutor(new RealNameCommand());
        getCommand("realname").setTabCompleter(new RealNameTabCompleter());
        getCommand("warp").setExecutor(new WarpCommand());
        getCommand("warp").setTabCompleter(new WarpTabCompleter());
        getCommand("msg").setExecutor(new MsgCommand());
        getCommand("socialspy").setExecutor(new SocialSpyCommand());
        getCommand("restrict").setExecutor(new RestrictCommand());
        getCommand("ping").setExecutor(new PingCommand());
        getCommand("lag").setExecutor(new LagCommand());

        TextUtil.logInfo("starting TPS monitor task...");
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new TPS(), 100L, 1L);

        TextUtil.logInfo("starting MySQL keep alive task...");
        this.getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
            try {
                Gamemode4Engine.db.openConnection().createStatement().executeQuery("SELECT 1;");
                Gamemode4Engine.adb.openConnection().createStatement().executeQuery("SELECT 1;");
            } catch (SQLException e) {
                TextUtil.logWarning("MySQL failed to keep connection alive! ERROR: " + e.getMessage());
            }
        }, 200L, 200L);
        TextUtil.logInfo("initialization complete!");
    }

    public void onDisable() {
        TextUtil.logWarning("closing database connections...");
        db.closeConnection();
        adb.closeConnection();

        TextUtil.logWarning("saving configs...");
        config.save();
        nicks.save();
        warps.save();
        protect.save();


    }

    private void registerEvents(Plugin p, Listener... listeners) {
        for (Listener l : listeners) {
            Bukkit.getPluginManager().registerEvents(l, p);
        }
    }

    public SpecialPlayerInventory getPlayerInventory(Player player, boolean createIfNull) {
        SpecialPlayerInventory inventory = inventories.get(player.getUniqueId());
        if (inventory == null && createIfNull) {
            inventory = new SpecialPlayerInventory(player, player.isOnline());
            inventories.put(player.getUniqueId(), inventory);
        }

        return inventory;
    }

    public SpecialEnderChest getPlayerEnderChest(Player player, boolean createIfNull) {
        SpecialEnderChest enderChest = enderChests.get(player.getUniqueId());
        if (enderChest == null && createIfNull) {
            enderChest = new SpecialEnderChest(player, player.isOnline());
            enderChests.put(player.getUniqueId(), enderChest);
        }

        return enderChest;
    }

    public void removeLoadedInventory(Player player) {
        if (inventories.containsKey(player.getUniqueId())) {
            inventories.remove(player.getUniqueId());
        }
    }

    public PlayerData getPlayerLoader() {
        return playerLoader;
    }

    public InventoryAccess getInventoryAccess() {
        return inventoryAccess;
    }

}
