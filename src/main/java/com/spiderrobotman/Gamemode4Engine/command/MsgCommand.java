package com.spiderrobotman.Gamemode4Engine.command;

import com.spiderrobotman.Gamemode4Engine.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Project: Gamemode4Engine
 * Author: SpiderRobotMan
 * Date: May 19 2016
 * Website: http://www.spiderrobotman.com
 */
public class MsgCommand implements CommandExecutor {

    public static Map<UUID, UUID> history = new ConcurrentHashMap<>();
    static List<UUID> spy = new CopyOnWriteArrayList<>();

    public static void sendMessage(Player sender, Player target, String message) {
        sender.sendMessage(ChatColor.GREEN + "Me" + ChatColor.GOLD + "" + ChatColor.BOLD + " >>> " + ChatColor.RESET + target.getDisplayName() + ChatColor.RESET + ": " + message);
        target.sendMessage(sender.getDisplayName() + ChatColor.GOLD + "" + ChatColor.BOLD + " >>> " + ChatColor.RESET + "" + ChatColor.GREEN + "Me" + ChatColor.RESET + ": " + message);

        Bukkit.getOnlinePlayers().stream().filter(p -> spy.contains(p.getUniqueId()) && !sender.getUniqueId().equals(p.getUniqueId()) && !target.getUniqueId().equals(p.getUniqueId())).forEach(p -> p.sendMessage(sender.getDisplayName() + ChatColor.GOLD + "" + ChatColor.BOLD + " >>> " + ChatColor.RESET + target.getDisplayName() + ChatColor.RESET + ": " + message));
    }

    @Override
    public boolean onCommand(CommandSender cs, Command command, String alias, String[] args) {
        if (cs instanceof Player) {
            Player sender = (Player) cs;
            if (args.length >= 1) {
                if (alias.equalsIgnoreCase("r") || alias.equalsIgnoreCase("reply")) {
                    if (history.containsKey(sender.getUniqueId())) {
                        Player target = Bukkit.getPlayer(history.get(sender.getUniqueId()));
                        if (target != null) {
                            history.put(target.getUniqueId(), sender.getUniqueId());
                            StringBuilder builder = new StringBuilder();
                            for (String arg : args) {
                                builder.append(arg).append(" ");
                            }
                            sendMessage(sender, target, builder.toString().trim());
                        } else {
                            sender.sendMessage(ChatColor.RED + "The player you're trying to reply to is offline!");
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "You have nobody to reply to!");
                    }
                    return true;
                } else if (args.length >= 2) {
                    Player target = Bukkit.getPlayerExact(args[0]);
                    if (target != null) {
                        history.put(sender.getUniqueId(), target.getUniqueId());
                        history.put(target.getUniqueId(), sender.getUniqueId());
                        StringBuilder builder = new StringBuilder();
                        for (int i = 1; i < args.length; i++) {
                            builder.append(args[i]).append(" ");
                        }
                        sendMessage(sender, target, builder.toString().trim());
                    } else {
                        sender.sendMessage(ChatColor.RED + "Player " + ChatColor.GOLD + args[0] + ChatColor.RED + " not found!");
                    }
                    return true;
                }
            }
            TextUtil.sendCommandFormatError(sender, "/" + alias + " [<player>] <message>");
        }
        return true;
    }
}
