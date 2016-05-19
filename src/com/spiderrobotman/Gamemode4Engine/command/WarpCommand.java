package com.spiderrobotman.Gamemode4Engine.command;

import com.spiderrobotman.Gamemode4Engine.main.Gamemode4Engine;
import com.spiderrobotman.Gamemode4Engine.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Project: Gamemode4Engine
 * Author: SpiderRobotMan
 * Date: May 19 2016
 * Website: http://www.spiderrobotman.com
 */
public class WarpCommand implements CommandExecutor {

    static Map<String, Location> warps = new ConcurrentHashMap<>();

    private static Location getWarpLocation(String name) {
        String warp_loc = Gamemode4Engine.warps.get().getString(name + ".pos");
        String warp_world = Gamemode4Engine.warps.get().getString(name + ".world");
        if (warp_loc == null || warp_world == null) return null;

        String[] split = warp_loc.split("\\|");
        return new Location(Bukkit.getWorld(warp_world), Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]));
    }

    private static void setWarpLocation(Location loc, String name) {
        name = name.toLowerCase();
        Gamemode4Engine.warps.get().set(name + ".world", loc.getWorld().getName());
        Gamemode4Engine.warps.get().set(name + ".pos", loc.getX() + "|" + loc.getY() + "|" + loc.getZ());
        Gamemode4Engine.warps.save();
        updateWarpMemory();
    }

    private static void unsetWarpLocation(String name) {
        Gamemode4Engine.warps.get().set(name, null);
        Gamemode4Engine.warps.save();
        updateWarpMemory();
    }

    public static void updateWarpMemory() {
        Gamemode4Engine.plugin().getServer().getScheduler().runTaskAsynchronously(Gamemode4Engine.plugin(), () -> {
            warps.clear();
            for (String name : Gamemode4Engine.warps.get().getKeys(false)) {
                Location loc = getWarpLocation(name);
                if (loc != null) warps.put(name, loc);
            }
        });
    }

    @Override
    public boolean onCommand(CommandSender cs, Command command, String alias, String[] args) {
        if (cs instanceof Player) {
            Player sender = (Player) cs;
            if (sender.isOp() || sender.hasPermission("gm4.warp")) {
                if (args.length <= 0) {
                    TextUtil.sendCommandFormatError(sender, "/" + alias + " <name [set, list]>");
                }
                Gamemode4Engine.plugin().getServer().getScheduler().runTaskAsynchronously(Gamemode4Engine.plugin(), () -> {
                    if (args.length >= 1) {
                        if (args[0].equalsIgnoreCase("set") && sender.hasPermission("gm4.warp.set")) {
                            if (args.length == 2) {
                                if (args[1].equalsIgnoreCase("list") || args[1].equalsIgnoreCase("set") || args[1].equalsIgnoreCase("unset")) {
                                    sender.sendMessage(ChatColor.RED + "You cannot set a warp with that name!");
                                } else {
                                    Location loc = sender.getLocation();
                                    setWarpLocation(loc, args[1]);
                                    sender.sendMessage(ChatColor.GREEN + "Warp " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " has been set! [" + ChatColor.GOLD + loc.getWorld().getName() + ", " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ChatColor.GREEN + "]");
                                }
                            } else {
                                TextUtil.sendCommandFormatError(sender, "/" + alias + " set <name>");
                            }
                        } else if (args[0].equalsIgnoreCase("unset") && sender.hasPermission("gm4.warp.set")) {
                            if (args.length == 2) {
                                unsetWarpLocation(args[1]);
                                sender.sendMessage(ChatColor.GOLD + args[1] + ChatColor.GREEN + " has been unset!");
                            } else {
                                TextUtil.sendCommandFormatError(sender, "/" + alias + " unset <name>");
                            }
                        } else if (args[0].equalsIgnoreCase("list")) {
                            if (args.length != 2) {
                                if (!warps.keySet().isEmpty()) {
                                    sender.sendMessage(ChatColor.GREEN + "Warp list:");
                                    sender.sendMessage(ChatColor.GOLD + String.join(", ", warps.keySet()));
                                } else {
                                    sender.sendMessage(ChatColor.RED + "There are no warps to list!");
                                }
                            } else {
                                TextUtil.sendCommandFormatError(sender, "/" + alias);
                            }
                        } else if (args.length != 2) {
                            Gamemode4Engine.plugin().getServer().getScheduler().runTask(Gamemode4Engine.plugin(), () -> {
                                Location loc = getWarpLocation(args[0]);
                                if (sender.isOnline()) {
                                    if (loc != null) {
                                        sender.sendMessage(ChatColor.GREEN + "Warping you to " + ChatColor.GOLD + args[0] + ChatColor.GREEN + " [" + ChatColor.GOLD + loc.getWorld().getName() + ", " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ChatColor.GREEN + "]");
                                        sender.teleport(loc);
                                    } else {
                                        sender.sendMessage(ChatColor.RED + "Warp " + ChatColor.GOLD + args[0] + ChatColor.RED + " does not exist!");
                                    }
                                }
                            });
                        }
                    }
                });
            }
        }

        return true;
    }

}
