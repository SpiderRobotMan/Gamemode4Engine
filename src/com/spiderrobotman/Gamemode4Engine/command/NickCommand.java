package com.spiderrobotman.Gamemode4Engine.command;

import com.spiderrobotman.Gamemode4Engine.main.Gamemode4Engine;
import com.spiderrobotman.Gamemode4Engine.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

/**
 * Project: Gamemode4Engine
 * Author: SpiderRobotMan
 * Date: May 17 2016
 * Website: http://www.spiderrobotman.com
 */
public class NickCommand implements CommandExecutor {

    public static String getFullName(Player p) {
        String fin = getPrefix(p) + getNickName(p);
        return fin.replace("&", "§");
    }

    public static String getNickName(Player p) {
        String nick = Gamemode4Engine.nicks.get().getString(p.getUniqueId().toString());
        String nick_pre = Gamemode4Engine.config.get().getString("nickname_prefix");
        if (nick != null && !nick.isEmpty() && !nick.equalsIgnoreCase(p.getName())) {
            return (nick_pre + nick).replace("&", "§");
        } else {
            return p.getName();
        }
    }

    public static void updatePlayerName(Player p) {
        p.setDisplayName(getFullName(p));
        p.setPlayerListName(getFullName(p));
    }

    private static String getPrefix(Player p) {
        YamlConfiguration cf = Gamemode4Engine.config.get();
        String prefix = cf.getString("default_prefix");

        if (p.hasPermission("gm4.rank.admin")) {
            prefix += cf.getString("admin_prefix");
        }
        if (p.hasPermission("gm4.rank.mod")) {
            prefix += cf.getString("mod_prefix");
        }
        if (p.hasPermission("gm4.rank.cmod")) {
            prefix += cf.getString("cmod_prefix");
        }
        if (p.hasPermission("gm4.rank.patreon")) {
            prefix += cf.getString("patreon_prefix");
        }
        return prefix.replace("&", "§");
    }

    @Override
    public boolean onCommand(CommandSender cs, Command command, String alias, String[] args) {
        if (cs instanceof Player) {
            Player sender = (Player) cs;
            if (sender.isOp() || sender.hasPermission("gm4.nickname")) {
                if (args.length == 1) {
                    setNickName(sender, args[0]);
                    return true;
                }
                if (args.length == 2) {
                    if (args[1].equalsIgnoreCase(sender.getName())) {
                        setNickName(sender, args[0]);
                        return true;
                    }

                    Player target = Bukkit.getPlayerExact(args[1]);
                    if (target != null) {
                        setNickName(target, args[0]);
                        return true;
                    } else {
                        sender.sendMessage(ChatColor.RED + "Player " + ChatColor.GOLD + args[1] + ChatColor.RED + " not found!");
                        return true;
                    }
                }

                TextUtil.sendCommandFormatError(sender, "/nick <nick> [<player>]");
            }
        }
        return true;
    }

    public void setNickName(Player p, String nickname) {
        if (!nickname.equalsIgnoreCase("-reset")) {
            String nick = nickname.replace("§", "&");
            Gamemode4Engine.nicks.get().set(p.getUniqueId().toString(), nick);
            p.sendMessage(ChatColor.GREEN + "Your nickname has been set to: " + ChatColor.RESET + nick);
        } else {
            Gamemode4Engine.nicks.get().set(p.getUniqueId().toString(), "");
            p.sendMessage(ChatColor.GREEN + "Your nickname has been reset to: " + ChatColor.RESET + p.getName());
        }
        Gamemode4Engine.nicks.save();
        updatePlayerName(p);
    }

}
