package com.spiderrobotman.Gamemode4Engine.command;

import com.spiderrobotman.Gamemode4Engine.main.Gamemode4Engine;
import com.spiderrobotman.Gamemode4Engine.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

/**
 * Project: Gamemode4Engine
 * Author: SpiderRobotMan
 * Date: May 17 2016
 * Website: http://www.spiderrobotman.com
 */
public class BanCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command command, String alias, String[] args) {
        if (cs instanceof Player) {
            Player sender = (Player) cs;

            if (sender.isOp() || sender.hasPermission("gm4.ban")) {
                if (args.length >= 1) {
                    Player target = Bukkit.getPlayerExact(args[0]);
                    if (target == null) {
                        sender.sendMessage(ChatColor.RED + "Player not found!");
                        return true;
                    }
                    if (!target.hasPermission("gm4.ban.bypass")) {
                        final UUID trg = target.getUniqueId();
                        final String send = sender.getDisplayName();
                        String reasonbuild = "";
                        if (args.length >= 2) {
                            StringBuilder builder = new StringBuilder();
                            for (int i = 1; i < args.length; i++) {
                                builder.append(args[i]).append(" ");
                            }
                            reasonbuild = builder.toString().trim();
                        }

                        final String reas = reasonbuild;

                        Gamemode4Engine.plugin().getServer().getScheduler().runTaskAsynchronously(Gamemode4Engine.plugin(), () -> {
                            final HashMap<String, Object> data = Gamemode4Engine.db.banPlayer(trg, send, reas);

                            Gamemode4Engine.plugin().getServer().getScheduler().runTask(Gamemode4Engine.plugin(), () -> Bukkit.getPlayer(trg).kickPlayer(TextUtil.buildBanMessage(data)));
                        });
                        return true;
                    } else {
                        sender.sendMessage(ChatColor.RED + "Player can not be banned!");
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
