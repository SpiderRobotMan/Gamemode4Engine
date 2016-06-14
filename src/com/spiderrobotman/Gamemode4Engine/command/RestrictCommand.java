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
 * Date: May 19 2016
 * Website: http://www.spiderrobotman.com
 */
public class RestrictCommand implements CommandExecutor {

    public static String restrictTo;

    public static boolean canBypassRestrict(Player p) {
        if (p.hasPermission("gm4.restrict.bypass")) return true;
        if (restrictTo != null) {
            if (!restrictTo.equalsIgnoreCase("-reset")) {
                String[] split = restrictTo.toLowerCase().split("\\|");
                for (String comp : split) {
                    TextUtil.logInfo("gm4.rank." + comp);
                    if (p.hasPermission("gm4.rank." + comp)) return true;
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    @Override
    public boolean onCommand(CommandSender cs, Command command, String alias, String[] args) {
        if (cs instanceof Player) {
            Player sender = (Player) cs;
            if (sender.isOp() || sender.hasPermission("gm4.restrict")) {
                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("-reset")) {
                        restrictTo = "-reset";
                        sender.sendMessage(ChatColor.GREEN + "Server restriction has been removed!");
                        save(restrictTo);
                        return true;
                    }
                    restrictTo = args[0];
                    Bukkit.getOnlinePlayers().stream().filter(p -> !canBypassRestrict(p)).forEach(p -> p.kickPlayer(ChatColor.GOLD + "Sorry " + p.getName() + "\n\n" + ChatColor.RED + "Server access has been restricted!"));
                    save(restrictTo);
                    return true;
                }
                TextUtil.sendCommandFormatError(sender, "/" + alias + " <ranks [-reset]>");
            }
        }

        return true;
    }

    private void save(String restrictTo) {
        Gamemode4Engine.plugin().getServer().getScheduler().runTaskAsynchronously(Gamemode4Engine.plugin(), () -> {
            Gamemode4Engine.config.get().set("restrict", restrictTo);
            Gamemode4Engine.config.save();
        });
    }
}
