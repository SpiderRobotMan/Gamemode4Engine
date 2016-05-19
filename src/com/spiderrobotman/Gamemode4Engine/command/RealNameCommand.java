package com.spiderrobotman.Gamemode4Engine.command;

import com.spiderrobotman.Gamemode4Engine.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Project: Gamemode4Engine
 * Author: SpiderRobotMan
 * Date: May 18 2016
 * Website: http://www.spiderrobotman.com
 */
public class RealNameCommand implements CommandExecutor {


    private static Player getPlayerFromNick(String nick) {
        String flat = ChatColor.stripColor(nick.replace("&", "§"));

        for (UUID uuid : NickCommand.nicks.keySet()) {
            String comp = ChatColor.stripColor(NickCommand.nicks.get(uuid).replace("&", "§"));
            if (comp.equalsIgnoreCase(flat)) {
                return Bukkit.getPlayer(uuid);
            }
        }
        return null;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command command, String alias, String[] args) {
        if (cs instanceof Player) {
            Player sender = (Player) cs;

            if (args.length == 1) {
                Player p = getPlayerFromNick(args[0]);
                if (p != null) {
                    sender.sendMessage(p.getDisplayName() + ChatColor.GREEN + " is actually " + ChatColor.RESET + p.getName());
                } else {
                    sender.sendMessage(ChatColor.RED + "That player doesn't exist!");
                }
                return true;
            }

            TextUtil.sendCommandFormatError(sender, "/" + alias + " <nick>");
        }

        return true;
    }
}
