package com.spiderrobotman.Gamemode4Engine.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Project: Gamemode4Engine
 * Author: SpiderRobotMan
 * Date: May 19 2016
 * Website: http://www.spiderrobotman.com
 */
public class WarpTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender cs, Command command, String s, String[] args) {
        if (command.getName().equalsIgnoreCase("warp") && args.length >= 1) {
            if (cs instanceof Player) {
                Player sender = (Player) cs;

                List<String> list = WarpCommand.warps.keySet().stream().map(String::toLowerCase).collect(Collectors.toList());

                if (args.length == 1) {
                    list.add("list");
                    list.add("set");
                    list.add("unset");
                    return list;
                }

                if (args.length == 2 && args[0].equalsIgnoreCase("unset")) {
                    return list;
                }
            }
        }
        return null;
    }
}