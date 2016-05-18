package com.spiderrobotman.Gamemode4Engine.command;

import com.spiderrobotman.Gamemode4Engine.main.Gamemode4Engine;
import com.spiderrobotman.Gamemode4Engine.util.TextUtil;
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
public class UnbanCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command command, String alias, String[] args) {
        if (cs instanceof Player) {
            Player sender = (Player) cs;

            if (sender.isOp() || sender.hasPermission("gm4.unban")) {
                if (args.length >= 1) {
                    if (Gamemode4Engine.db.unbanPlayer(args[0])) {
                        sender.sendMessage(ChatColor.GREEN + "Player " + ChatColor.GOLD + args[0] + ChatColor.GREEN + " has been unbanned!");
                        return true;
                    } else {
                        sender.sendMessage(ChatColor.RED + "Player " + ChatColor.GOLD + args[0] + ChatColor.RED + " is not currently banned!");
                        return true;
                    }
                } else {
                    TextUtil.sendCommandFormatError(sender, "/unban <player>");
                }
            }
        }
        return true;
    }
}
