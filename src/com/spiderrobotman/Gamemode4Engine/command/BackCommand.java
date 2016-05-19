package com.spiderrobotman.Gamemode4Engine.command;

import com.spiderrobotman.Gamemode4Engine.util.TextUtil;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Project: Gamemode4Engine
 * Author: SpiderRobotMan
 * Date: May 18 2016
 * Website: http://www.spiderrobotman.com
 */
public class BackCommand implements CommandExecutor {

    private static Map<UUID, Location> locationHistory = new ConcurrentHashMap<>();

    public static void updateLocation(Player p, Location loc) {
        locationHistory.put(p.getUniqueId(), loc);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command command, String alias, String[] args) {
        if (cs instanceof Player) {
            Player sender = (Player) cs;
            if (sender.isOp() || sender.hasPermission("gm4.back")) {
                if (args.length == 0) {
                    if (locationHistory.containsKey(sender.getUniqueId())) {
                        Location loc = locationHistory.get(sender.getUniqueId());
                        if (!isUnsafe(sender, loc) || alias.equalsIgnoreCase("backunsafe")) {
                            sender.teleport(loc);
                            sender.sendMessage(ChatColor.GREEN + "Returning you to your last known location! [" + ChatColor.GOLD + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ChatColor.GREEN + "]");
                        } else {
                            sender.sendMessage(ChatColor.RED + "Unsafe location! Use " + ChatColor.GOLD + "/backunsafe" + ChatColor.RED + " to bypass protection.");
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "You have no location history!");
                    }
                    return true;
                }
                TextUtil.sendCommandFormatError(sender, "/" + alias);
            }
        }
        return true;
    }

    private boolean isUnsafe(Player fr, Location loc) {
        Block block = loc.getWorld().getHighestBlockAt(loc);
        return (fr.getGameMode() == GameMode.ADVENTURE || fr.getGameMode() == GameMode.SURVIVAL) && (block.getType() == Material.LAVA || block.getType() == Material.STATIONARY_LAVA || block.getType() == Material.FIRE || (loc.getY() - block.getY() > 15));
    }
}
