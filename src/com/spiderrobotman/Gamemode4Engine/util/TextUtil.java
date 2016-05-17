package com.spiderrobotman.Gamemode4Engine.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import static org.bukkit.ChatColor.*;

/**
 * Project: Gamemode4Engine
 * Author: SpiderRobotMan
 * Date: May 12 2016
 * Website: http://www.spiderrobotman.com
 */
public class TextUtil {

    public static void logInfo(String text) {
        Bukkit.getLogger().log(Level.INFO, GREEN + "[GM4Engine INFO] " + text);
    }

    public static void logWarning(String text) {
        Bukkit.getLogger().log(Level.WARNING, GOLD + "[GM4Engine WARN] " + text);
    }

    public static void logError(String text) {
        Bukkit.getLogger().log(Level.SEVERE, RED + "[GM4Engine ERROR] " + text);
    }

    public static void sendCommandFormatError(Player p, String format) {
        p.sendMessage(RED + "Incorrect command format: " + GOLD + format);
    }

    private static String millisToString(long millis) {
        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        StringBuilder sb = new StringBuilder(64);
        if (days > 0) {
            sb.append(days);
            sb.append(" Days ");
        }
        if (hours > 0) {
            sb.append(hours);
            sb.append(" Hours ");
        }
        if (minutes > 0) {
            sb.append(minutes);
            sb.append(" Minutes ");
        }
        sb.append(seconds);
        sb.append(" Seconds");

        return (sb.toString());
    }

    public static String buildBanMessage(HashMap<String, Object> bdata) {
        String time = (bdata.get("type").equals("permanent")) ? "Forever" : TextUtil.millisToString((long) bdata.get("time_end") - System.currentTimeMillis());
        return DARK_RED + "You've been banned!\n" +
                GOLD + "Reason: " + RESET + bdata.get("reason") + "\n" +
                GOLD + "Banned By: " + RESET + bdata.get("banned_by") + "\n\n" +
                GOLD + "Time Remaining: " + RED + time;
    }

    public static String buildKickMessage(String by, String reason) {
        if (!reason.isEmpty()) {
            return DARK_RED + "You've been kicked!\n" +
                    GOLD + "Reason: " + RESET + reason + "\n" +
                    GOLD + "Kicked By: " + RESET + by;
        } else {
            return DARK_RED + "You've been kicked!\n" +
                    GOLD + "Kicked By: " + RESET + by;
        }
    }

    public static String buildAccessMessage(String name) {
        return GOLD + "Sorry " + name + "\n" +
                GOLD + "You're not whitelisted!" + RESET + "\n" +
                GREEN + "Apply to join here: http://www.gm4.co";
    }

}
