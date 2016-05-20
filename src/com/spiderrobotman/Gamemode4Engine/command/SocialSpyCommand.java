package com.spiderrobotman.Gamemode4Engine.command;

import com.spiderrobotman.Gamemode4Engine.util.TextUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Project: Gamemode4Engine
 * Author: SpiderRobotMan
 * Date: May 19 2016
 * Website: http://www.spiderrobotman.com
 */
public class SocialSpyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command command, String alias, String[] args) {
        if (cs instanceof Player) {
            Player sender = (Player) cs;
            if (args.length == 0) {
                if (sender.hasPermission("gm4.socialspy")) {
                    if (!MsgCommand.spy.contains(sender.getUniqueId())) {
                        MsgCommand.spy.add(sender.getUniqueId());
                        sender.sendMessage(ChatColor.GREEN + "SocialSpy has been enabled!");
                    } else {
                        MsgCommand.spy.remove(sender.getUniqueId());
                        sender.sendMessage(ChatColor.GREEN + "SocialSpy has been disabled!");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to use socialspy!");
                }
                return true;
            }
            TextUtil.sendCommandFormatError(sender, "/" + alias);
        }
        return true;
    }

}
