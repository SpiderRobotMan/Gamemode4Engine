package com.spiderrobotman.Gamemode4Engine.main;

import com.spiderrobotman.Gamemode4Engine.listeners.BlockListener;
import com.spiderrobotman.Gamemode4Engine.listeners.EntityListener;
import com.spiderrobotman.Gamemode4Engine.listeners.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

/**
 * Created by spide on 3/28/2016.
 */

public class Gamemode4Engine extends JavaPlugin {

    private static Gamemode4Engine plugin;

    public void onEnable() {
        plugin = this;
        Bukkit.getLogger().log(Level.INFO, ChatColor.GREEN + "Gamemode4Engine initializing...");
        registerEvents(plugin, new PlayerListener(), new EntityListener(), new BlockListener());
    }

    public void registerEvents(Plugin p, Listener... listeners) {
        for (Listener l : listeners) {
            Bukkit.getPluginManager().registerEvents(l, p);
        }
    }

    public static Gamemode4Engine plugin() {
        return plugin;
    }

}
