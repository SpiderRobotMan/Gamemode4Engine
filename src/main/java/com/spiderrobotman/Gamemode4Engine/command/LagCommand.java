package com.spiderrobotman.Gamemode4Engine.command;

import com.spiderrobotman.Gamemode4Engine.util.TPS;
import com.spiderrobotman.Gamemode4Engine.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Project: Gamemode4Engine
 * Author: SpiderRobotMan
 * Date: May 21 2016
 * Website: http://www.spiderrobotman.com
 */
public class LagCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command command, String alias, String[] args) {
        if (cs instanceof Player) {
            Player sender = (Player) cs;
            if (args.length == 0) {
                int ping;
                double tps = TPS.getTPS();
                int ent = 0;
                int chunks = 0;
                try {
                    ping = PingCommand.getPlayerPing(sender);
                } catch (Exception e) {
                    ping = 0;
                }

                for (World w : Bukkit.getWorlds()) {
                    ent += w.getEntities().size();
                    for (Chunk c : w.getLoadedChunks()) {
                        chunks++;
                    }
                }

                String serverPerf = "&6&lUNAVAILABLE&r";
                String pingStatus = "&4&lBAD&r";
                String entCount;
                String chunkCount;

                if (tps >= 1 && tps <= 7) {
                    serverPerf = "&4&lBAD&r";
                } else if (tps >= 1 && tps <= 11) {
                    serverPerf = "&4&lPOOR&r";
                } else if (tps >= 1 && tps <= 14) {
                    serverPerf = "&9&lDECENT&r";
                } else if (tps >= 1 && tps <= 17) {
                    serverPerf = "&2&lGOOD&r";
                } else if (tps >= 1) {
                    serverPerf = "&a&lEXCELLENT&r";
                }

                if (ping >= 1 && ping <= 50) {
                    pingStatus = "&a&lEXCELLENT&r";
                } else if (ping >= 1 && ping <= 100) {
                    pingStatus = "&2&lGOOD&r";
                } else if (ping >= 1 && ping <= 250) {
                    pingStatus = "&9&lDECENT&r";
                } else if (ping >= 1 && ping <= 350) {
                    pingStatus = "&4&lPOOR&r";
                }

                if (ent <= 1000) {
                    entCount = "&a&lVERY LOW&r";
                } else if (ent <= 2500) {
                    entCount = "&2&lLOW&r";
                } else if (ent <= 4500) {
                    entCount = "&9&lMEDIUM&r";
                } else if (ent <= 6500) {
                    entCount = "&4&lHIGH&r";
                } else {
                    entCount = "&4&lVERY HIGH&r";
                }

                if (chunks <= 2000) {
                    chunkCount = "&a&lVERY LOW&r";
                } else if (chunks <= 5500) {
                    chunkCount = "&2&lLOW&r";
                } else if (chunks <= 9500) {
                    chunkCount = "&9&lMEDIUM&r";
                } else if (chunks <= 13500) {
                    chunkCount = "&4&lHIGH&r";
                } else {
                    chunkCount = "&4&lVERY HIGH&r";
                }

                pingStatus = pingStatus.replace("&", "§");
                serverPerf = serverPerf.replace("&", "§");
                entCount = entCount.replace("&", "§");
                chunkCount = chunkCount.replace("&", "§");

                sender.sendMessage(ChatColor.GOLD + "---------Lag Analysis---------");
                sender.sendMessage(ChatColor.AQUA + "Your connection: " + pingStatus);
                sender.sendMessage(ChatColor.AQUA + "Server performance: " + serverPerf);
                sender.sendMessage(ChatColor.AQUA + "Entity count: " + entCount);
                sender.sendMessage(ChatColor.AQUA + "Chunk count: " + chunkCount);
                return true;
            } else if (args[0].equalsIgnoreCase("+")) {
                if (sender.hasPermission("gm4.lag.plus")) {
                    double tps = TPS.getTPS();
                    int chunks = 0;
                    Map<String, Integer> entities = new HashMap<>();
                    Map<String, Integer> tiles = new HashMap<>();

                    for (World w : Bukkit.getWorlds()) {
                        for (Chunk c : w.getLoadedChunks()) {
                            chunks++;
                            for (BlockState t : c.getTileEntities()) {
                                if (tiles.containsKey(t.getType().toString())) {
                                    tiles.put(t.getType().toString(), tiles.get(t.getType().toString()) + 1);
                                } else {
                                    tiles.put(t.getType().toString(), 1);
                                }
                            }
                        }
                        w.getEntities().stream().filter(e -> e.getType() != EntityType.PLAYER).forEach(e -> {
                            String name = e.getCustomName();
                            if (name != null) {
                                if (entities.containsKey(name)) {
                                    entities.put(name, entities.get(name) + 1);
                                } else {
                                    entities.put(name, 1);
                                }
                            } else {
                                if (entities.containsKey(e.getType().toString())) {
                                    entities.put(e.getType().toString(), entities.get(e.getType().toString()) + 1);
                                } else {
                                    entities.put(e.getType().toString(), 1);
                                }
                            }
                        });
                    }

                    sender.sendMessage(ChatColor.GOLD + "---------LagPlus Analysis---------");
                    sender.sendMessage(ChatColor.AQUA + "Server TPS: " + ChatColor.GOLD + Math.floor(tps * 100.0) / 100.0);
                    sender.sendMessage(ChatColor.AQUA + "Chunk count: " + ChatColor.GOLD + chunks);
                    sender.sendMessage(ChatColor.GREEN + "Tile entities:");
                    for (String t : tiles.keySet()) {
                        sender.sendMessage(ChatColor.GREEN + "--" + ChatColor.BLUE + "" + ChatColor.BOLD + t + ChatColor.GREEN + ": " + ChatColor.GOLD + tiles.get(t));
                    }
                    sender.sendMessage(ChatColor.LIGHT_PURPLE + "Entities:");
                    for (String t : entities.keySet()) {
                        sender.sendMessage(ChatColor.LIGHT_PURPLE + "--" + ChatColor.BLUE + "" + ChatColor.BOLD + t + ChatColor.LIGHT_PURPLE + ": " + ChatColor.GOLD + entities.get(t));
                    }
                    return true;
                } else {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to do a lag+ analysis!");
                    return true;
                }
            }
            TextUtil.sendCommandFormatError(sender, "/" + alias + " [+]");
        }
        return true;
    }
}
