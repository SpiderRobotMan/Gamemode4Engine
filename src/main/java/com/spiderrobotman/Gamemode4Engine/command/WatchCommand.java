package com.spiderrobotman.Gamemode4Engine.command;

import com.spiderrobotman.Gamemode4Engine.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Project: Gamemode4Engine
 * Author: SpiderRobotMan
 * Date: Jun 15 2016
 * Website: http://www.spiderrobotman.com
 */
public class WatchCommand implements CommandExecutor {

    public static Map<UUID, UUID> watching = new HashMap<>();
    public static Map<UUID, Location> previous = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender cs, Command command, String alias, String[] args) {
        if (cs instanceof Player) {
            Player sender = (Player) cs;

            if (sender.isOp() || sender.hasPermission("gm4.watch")) {
                if (args.length >= 1) {
                    if (args[0].equalsIgnoreCase("-reset")) {
                        if (watching.containsKey(sender.getUniqueId())) {
                            watching.remove(sender.getUniqueId());
                            sender.teleport(previous.get(sender.getUniqueId()));
                            previous.remove(sender.getUniqueId());
                            sender.setGameMode(GameMode.SURVIVAL);
                        } else {
                            sender.sendMessage(ChatColor.RED + "You aren't watching anyone!");
                            return true;
                        }
                    } else {
                        if (!watching.containsKey(sender.getUniqueId())) {
                            Player target = Bukkit.getPlayerExact(args[0]);
                            if (target != null) {
                                previous.put(sender.getUniqueId(), sender.getLocation());
                                sender.teleport(target, PlayerTeleportEvent.TeleportCause.SPECTATE);
                                watching.put(sender.getUniqueId(), target.getUniqueId());
                                sender.setGameMode(GameMode.SPECTATOR);
                            } else {
                                sender.sendMessage(ChatColor.RED + "Player not found!");
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "You are already watching someone!");
                            return true;
                        }
                    }

                } else {
                    TextUtil.sendCommandFormatError(sender, "/" + alias + " <player>");
                }
            }
        }
        return true;
    }


}
