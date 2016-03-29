package com.spiderrobotman.Gamemode4Engine.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.logging.Level;

/**
 * Created by spide on 3/28/2016.
 */
public class TextUtil {

    public static void logInfo(String text) {
        Bukkit.getLogger().log(Level.INFO, ChatColor.GREEN + "[GM4Engine INFO] " + text);
    }

    public static void logWarning(String text) {
        Bukkit.getLogger().log(Level.WARNING, ChatColor.GOLD + "[GM4Engine WARN] " + text);
    }

    public static void logError(String text) {
        Bukkit.getLogger().log(Level.SEVERE, ChatColor.RED + "[GM4Engine ERROR] " + text);
    }

}
