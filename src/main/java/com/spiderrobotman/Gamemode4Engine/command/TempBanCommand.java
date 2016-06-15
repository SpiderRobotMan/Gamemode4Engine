package com.spiderrobotman.Gamemode4Engine.command;

import com.spiderrobotman.Gamemode4Engine.main.Gamemode4Engine;
import com.spiderrobotman.Gamemode4Engine.util.PlayerUtil;
import com.spiderrobotman.Gamemode4Engine.util.TextUtil;
import com.spiderrobotman.Gamemode4Engine.util.UUIDUtil;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Project: Gamemode4Engine
 * Author: SpiderRobotMan
 * Date: May 17 2016
 * Website: http://www.spiderrobotman.com
 */
public class TempBanCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command command, String alias, String[] args) {
        if (cs instanceof Player) {
            Player sender = (Player) cs;

            if (sender.isOp() || sender.hasPermission("gm4.tempban")) {
                if (args.length >= 2) {

                    long time = 0;
                    String[] ts = args[1].split(":");

                    for (String tss : ts) {
                        int l = tss.length();
                        String end = tss.substring(tss.length() - 1);
                        switch (end) {
                            case "d":
                                time += (Long.parseLong(tss.substring(0, tss.length() - 1)) * 86400000);
                                break;
                            case "h":
                                time += (Long.parseLong(tss.substring(0, tss.length() - 1)) * 3600000);
                                break;
                            case "m":
                                time += (Long.parseLong(tss.substring(0, tss.length() - 1)) * 60000);
                                break;
                            case "s":
                                time += (Long.parseLong(tss.substring(0, tss.length() - 1)) * 1000);
                                break;
                        }
                    }

                    if (time != 0) {
                        time += System.currentTimeMillis();
                    } else {
                        sender.sendMessage(ChatColor.RED + "Invalid time provided!");
                        TextUtil.sendCommandFormatError(sender, "/" + alias + " <player> <time> [<reason>]");
                        return true;
                    }

                    Player target = PlayerUtil.getPlayerFromString(args[0]);
                    if (target != null) {
                        if (!target.hasPermission("gm4.tempban.bypass")) {
                            banPlayer(target.getUniqueId(), sender.getDisplayName(), TextUtil.buildFromArray(1, args), time, target.isOnline());
                        } else {
                            sender.sendMessage(ChatColor.RED + "That player cannot be banned!");
                        }

                    } else {
                        sender.sendMessage(ChatColor.GOLD + "Offline player lookup...");

                        Gamemode4Engine.plugin().getServer().getScheduler().runTaskAsynchronously(Gamemode4Engine.plugin(), () -> {
                            Map<UUID, String> users = UUIDUtil.getPossibleUUIDs(args[0]);
                            if (users != null) {
                                String suggest = "/tempban %*p*% " + TextUtil.buildFromArray(1, args);

                                ComponentBuilder cb = new ComponentBuilder("Players that had/have the name " + args[0] + ":").color(net.md_5.bungee.api.ChatColor.AQUA);

                                for (Map.Entry<UUID, String> entry : users.entrySet()) {
                                    String lastPlayed = TextUtil.millisToString(System.currentTimeMillis() - (long) Gamemode4Engine.db.fetchPlayer(entry.getKey()).getOrDefault("last_online", System.currentTimeMillis()), true);
                                    cb.append("\n--" + entry.getKey().toString())
                                            .color(net.md_5.bungee.api.ChatColor.BLUE)
                                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                                    new ComponentBuilder("Current Name:\n").color(net.md_5.bungee.api.ChatColor.GOLD).bold(true)
                                                            .append(entry.getValue() + "\n\n").color(net.md_5.bungee.api.ChatColor.YELLOW).bold(false)
                                                            .append("Last Played:\n").color(net.md_5.bungee.api.ChatColor.GOLD).bold(true)
                                                            .append(lastPlayed).color(net.md_5.bungee.api.ChatColor.YELLOW).bold(false).create()))
                                            .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, suggest.replace("%*p*%", entry.getKey().toString())));
                                }
                                sender.sendMessage(cb.create());
                            } else {
                                sender.sendMessage(ChatColor.RED + "No players found!");
                            }
                        });
                    }


                    /*
                    Player target = Bukkit.getPlayerExact(args[0]);
                    if (target == null) {
                        sender.sendMessage(ChatColor.RED + "Player not found!");
                        return true;
                    }
                    if (!target.hasPermission("gm4.tempban.bypass")) {
                        final UUID trg = target.getUniqueId();
                        final String send = sender.getDisplayName();
                        long time = 0;
                        String[] ts = args[1].split(":");

                        for (String tss : ts) {
                            int l = tss.length();
                            String end = tss.substring(tss.length() - 1);
                            switch (end) {
                                case "d":
                                    time += (Long.parseLong(tss.substring(0, tss.length() - 1)) * 86400000);
                                    break;
                                case "h":
                                    time += (Long.parseLong(tss.substring(0, tss.length() - 1)) * 3600000);
                                    break;
                                case "m":
                                    time += (Long.parseLong(tss.substring(0, tss.length() - 1)) * 60000);
                                    break;
                                case "s":
                                    time += (Long.parseLong(tss.substring(0, tss.length() - 1)) * 1000);
                                    break;
                            }
                        }

                        if (time != 0) {
                            time += System.currentTimeMillis();
                        } else {
                            TextUtil.sendCommandFormatError(sender, "/" + alias + " <player> <time> [<reason>]");
                            return true;
                        }

                        String reasonbuild = "";
                        if (args.length >= 3) {
                            StringBuilder builder = new StringBuilder();
                            for (int i = 2; i < args.length; i++) {
                                builder.append(args[i]).append(" ");
                            }
                            reasonbuild = builder.toString().trim();
                        }

                        final String reas = reasonbuild;
                        final long t = time;

                        Gamemode4Engine.plugin().getServer().getScheduler().runTaskAsynchronously(Gamemode4Engine.plugin(), () -> {
                            final HashMap<String, Object> data = Gamemode4Engine.db.tempbanPlayer(trg, send, reas, t);

                            Gamemode4Engine.plugin().getServer().getScheduler().runTask(Gamemode4Engine.plugin(), () -> Bukkit.getPlayer(trg).kickPlayer(TextUtil.buildBanMessage(data)));
                        });
                        return true;
                    } else {
                        sender.sendMessage(ChatColor.RED + "Player can not be banned!");
                        return true;
                    }
                    */
                } else {
                    TextUtil.sendCommandFormatError(sender, "/" + alias + " <player> <time> [<reason>]");
                }
            }
        }
        return true;
    }

    private void banPlayer(UUID target, String sender, String reason, long time, boolean isOnline) {
        HashMap<String, Object> data = Gamemode4Engine.db.tempbanPlayer(target, sender, reason, time);
        if (isOnline)
            Gamemode4Engine.plugin().getServer().getScheduler().runTask(Gamemode4Engine.plugin(), () -> Bukkit.getPlayer(target).kickPlayer(TextUtil.buildBanMessage(data)));
    }
}
