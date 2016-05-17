package com.spiderrobotman.Gamemode4Engine.main;

import com.spiderrobotman.Gamemode4Engine.command.BanCommand;
import com.spiderrobotman.Gamemode4Engine.command.KickCommand;
import com.spiderrobotman.Gamemode4Engine.command.TempBanCommand;
import com.spiderrobotman.Gamemode4Engine.command.UnbanCommand;
import com.spiderrobotman.Gamemode4Engine.data.ConfigManager;
import com.spiderrobotman.Gamemode4Engine.data.DatabaseManager;
import com.spiderrobotman.Gamemode4Engine.listeners.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

/**
 * Project: Gamemode4Engine
 * Author: SpiderRobotMan
 * Date: May 09 2016
 * Website: http://www.spiderrobotman.com
 */

public class Gamemode4Engine extends JavaPlugin {

    public static DatabaseManager db;
    public static DatabaseManager adb;
    private static ConfigManager.Config config;
    private static Gamemode4Engine plugin;

    public static Gamemode4Engine plugin() {
        return plugin;
    }

    public void onEnable() {
        plugin = this;
        Bukkit.getLogger().log(Level.INFO, ChatColor.GREEN + "Gamemode4Engine initializing...");
        registerEvents(plugin, new PlayerListener());
        ConfigManager c = new ConfigManager(this);
        config = c.getConfig("config.yml").copyDefaults(true).save();
        db = new DatabaseManager(config.get().getString("main_database.host"), config.get().getString("main_database.port"), config.get().getString("main_database.user"), config.get().getString("main_database.password"), config.get().getString("main_database.database"));
        adb = new DatabaseManager(config.get().getString("access_database.host"), config.get().getString("access_database.port"), config.get().getString("access_database.user"), config.get().getString("access_database.password"), config.get().getString("access_database.database"));

        getCommand("ban").setExecutor(new BanCommand());
        getCommand("tempban").setExecutor(new TempBanCommand());
        getCommand("kick").setExecutor(new KickCommand());
        getCommand("unban").setExecutor(new UnbanCommand());
    }

    private void registerEvents(Plugin p, Listener... listeners) {
        for (Listener l : listeners) {
            Bukkit.getPluginManager().registerEvents(l, p);
        }
    }

}
