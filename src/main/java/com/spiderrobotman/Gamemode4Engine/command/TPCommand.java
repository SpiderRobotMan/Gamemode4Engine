package com.spiderrobotman.Gamemode4Engine.command;

import com.spiderrobotman.Gamemode4Engine.util.TextUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * Project: Gamemode4Engine
 * Author: SpiderRobotMan
 * Date: May 17 2016
 * Website: http://www.spiderrobotman.com
 */
public class TPCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command command, String alias, String[] args) {
        if (cs instanceof Player) {
            Player sender = (Player) cs;
            if (sender.isOp() || sender.hasPermission("gm4.teleport")) {
                if (args.length >= 1) {
                    if (args.length == 1 && args[0].equalsIgnoreCase(sender.getName())) return true;
                    if (!(args.length >= 2)) {
                        Player p = Bukkit.getPlayerExact(args[0]);
                        if (p == null) {
                            sender.sendMessage(ChatColor.RED + "Player " + ChatColor.GOLD + args[0] + ChatColor.RED + " not found!");
                            return true;
                        }
                        if (!isUnsafe(sender, p) || alias.equalsIgnoreCase("tpunsafe")) {
                            sender.teleport(p, PlayerTeleportEvent.TeleportCause.COMMAND);
                        } else {
                            sender.sendMessage(ChatColor.RED + "Unsafe location! Use " + ChatColor.GOLD + "/tpunsafe" + ChatColor.RED + " to bypass protection.");
                        }
                    } else {
                        if (args[0].equalsIgnoreCase(sender.getName()) || sender.hasPermission("gm4.teleport.others")) {
                            Player p1 = Bukkit.getPlayerExact(args[0]);
                            Player p2 = Bukkit.getPlayerExact(args[1]);
                            if (p1 == null) {
                                sender.sendMessage(ChatColor.RED + "Player " + ChatColor.GOLD + args[0] + ChatColor.RED + " not found!");
                                return true;
                            }
                            if (p2 == null) {
                                sender.sendMessage(ChatColor.RED + "Player " + ChatColor.GOLD + args[1] + ChatColor.RED + " not found!");
                                return true;
                            }

                            if (!isUnsafe(p1, p2) || alias.equalsIgnoreCase("tpunsafe")) {
                                p1.teleport(p2, PlayerTeleportEvent.TeleportCause.COMMAND);
                            } else {
                                sender.sendMessage(ChatColor.RED + "Unsafe location! Use " + ChatColor.GOLD + "/tpunsafe" + ChatColor.RED + " to bypass protection.");
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "You don't have permission to teleport other players!");
                            return true;
                        }
                    }
                } else {
                    TextUtil.sendCommandFormatError(sender, "/" + alias + " <player> [<player>]");
                }
            }
        }
        return true;
    }

    private boolean isUnsafe(Player fr, Player p) {
        Location loc = p.getLocation();
        Block block = loc.getWorld().getHighestBlockAt(loc);
        return (fr.getGameMode() == GameMode.ADVENTURE || fr.getGameMode() == GameMode.SURVIVAL) && (block.getType() == Material.LAVA || block.getType() == Material.STATIONARY_LAVA || block.getType() == Material.FIRE || (loc.getY() - block.getY() > 15));
    }
}
