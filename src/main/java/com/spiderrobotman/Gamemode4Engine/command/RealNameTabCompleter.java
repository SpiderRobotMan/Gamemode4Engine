package com.spiderrobotman.Gamemode4Engine.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Project: Gamemode4Engine
 * Author: SpiderRobotMan
 * Date: May 18 2016
 * Website: http://www.spiderrobotman.com
 */
public class RealNameTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender cs, Command command, String s, String[] args) {
        if (command.getName().equalsIgnoreCase("realname") && args.length == 1) {
            if (cs instanceof Player) {
                Player sender = (Player) cs;

                List<String> list = new ArrayList<>();

                for (String name : NickCommand.nicks.values()) {

                    String flat = ChatColor.stripColor(name.replace("&", "§"));
                    if (!name.isEmpty() && flat.beginsWith(args[0])) {
                        list.add(flat);
                    }
                }
                return list;
            }
        }

        return null;
    }
}
