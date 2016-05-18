package com.spiderrobotman.Gamemode4Engine.command;

import com.spiderrobotman.Gamemode4Engine.main.Gamemode4Engine;
import com.spiderrobotman.Gamemode4Engine.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Project: Gamemode4Engine
 * Author: SpiderRobotMan
 * Date: May 17 2016
 * Website: http://www.spiderrobotman.com
 */
public class NickCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command command, String alias, String[] args) {
        if (cs instanceof Player) {
            Player sender = (Player) cs;
            if (sender.isOp() || sender.hasPermission("gm4.nickname")) {
                if (args.length >= 1) {
                    String nickname = args[0];
                    if (nickname.length() > 48) {
                        sender.sendMessage(ChatColor.RED + "Nickname cannot exceed 48 characters!");
                        return true;
                    }
                    if (!(args.length >= 2)) {
                        setNickName(sender, nickname);
                        return true;
                    } else {
                        if (args[1].equalsIgnoreCase(sender.getName()) || sender.hasPermission("gm4.nickname.others")) {
                            Player p = Bukkit.getPlayerExact(args[1]);
                            if (p == null) {
                                sender.sendMessage(ChatColor.RED + "Player " + ChatColor.GOLD + args[1] + ChatColor.RED + " not found!");
                                return true;
                            }
                            setNickName(p, nickname);
                            sender.sendMessage(ChatColor.GREEN + "Nickname " + nickname.replace("&", "§") + ChatColor.GREEN + " has been set to " + ChatColor.GOLD + p.getName());
                            return true;
                        } else {
                            sender.sendMessage(ChatColor.RED + "You don't have permission to nickname other players!");
                            return true;
                        }
                    }
                } else {
                    TextUtil.sendCommandFormatError(sender, "/nick <nick> [<player>]");
                }
            }
        }
        return true;
    }

    private void setNickName(Player p, String nickname) {
        String prefix = "";
        String nick = nickname.replace("&", "§");
        if (nickname.equals("-reset")) {
            p.sendMessage(ChatColor.GREEN + "Your nickname has been removed!");
            nick = p.getName();
        } else {
            prefix = Gamemode4Engine.config.get().getString("nickname_prefix").replace("&", "§") + ChatColor.RESET;
            p.sendMessage(ChatColor.GREEN + "Your nickname has been set to " + prefix + nick);
        }
        Gamemode4Engine.nicks.get().set(p.getUniqueId().toString(), nick.replace("§", "&"));
        Gamemode4Engine.nicks.save();
        p.setPlayerListName(prefix + nick);
        p.setCustomName(prefix + nick);
    }
}
