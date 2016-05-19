package com.spiderrobotman.Gamemode4Engine.command;

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
public class KickCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command command, String alias, String[] args) {
        if (cs instanceof Player) {
            Player sender = (Player) cs;

            if (sender.isOp() || sender.hasPermission("gm4.kick")) {
                if (args.length >= 1) {
                    Player target = Bukkit.getPlayerExact(args[0]);
                    if (target == null) {
                        sender.sendMessage(ChatColor.RED + "Player not found!");
                        return true;
                    }
                    if (!target.hasPermission("gm4.kick.bypass")) {
                        if (args.length >= 2) {
                            StringBuilder builder = new StringBuilder();
                            for (int i = 1; i < args.length; i++) {
                                builder.append(args[i]).append(" ");
                            }
                            target.kickPlayer(TextUtil.buildKickMessage(sender.getDisplayName(), builder.toString().trim()));
                        } else {
                            target.kickPlayer(TextUtil.buildKickMessage(sender.getDisplayName(), ""));
                        }
                        return true;
                    } else {
                        sender.sendMessage(ChatColor.RED + "Player can not be kicked!");
                        return true;
                    }
                } else {
                    TextUtil.sendCommandFormatError(sender, "/" + alias + " <player> [<reason>]");
                }
            }
        }
        return true;
    }
}
