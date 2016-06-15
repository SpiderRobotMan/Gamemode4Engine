package com.spiderrobotman.Gamemode4Engine.command;

import com.spiderrobotman.Gamemode4Engine.handler.SpecialEnderChest;
import com.spiderrobotman.Gamemode4Engine.main.Gamemode4Engine;
import com.spiderrobotman.Gamemode4Engine.util.UUIDUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Project: Gamemode4Engine
 * Author: SpiderRobotMan
 * Date: May 18 2016
 * Website: http://www.spiderrobotman.com
 */
public class OpenEndCommand implements CommandExecutor {

    private final Map<UUID, UUID> openEnderHistory = new ConcurrentHashMap<>();

    @Override
    public boolean onCommand(CommandSender cs, Command command, String alias, String[] args) {
        if (cs instanceof Player) {
            final Player player = (Player) cs;
            if (player.isOp() || player.hasPermission("gm4.openinv")) {

                UUID history = openEnderHistory.get(player.getUniqueId());
                if (history == null) {
                    history = player.getUniqueId();
                    openEnderHistory.put(player.getUniqueId(), history);
                }

                final UUID uuid;

                // Read from history if target is not named
                if (args.length < 1) {
                    if (history != null) {
                        uuid = history;
                    } else {
                        player.sendMessage(ChatColor.RED + "OpenEnder history is empty!");
                        return true;
                    }
                } else {
                    uuid = UUIDUtil.getPlayerUUID(args[0]);
                    if (uuid == null) {
                        player.sendMessage(ChatColor.RED + "Player not found!");
                        return true;
                    }
                }

                final UUID playerUUID = player.getUniqueId();

                Player target = Bukkit.getPlayer(uuid);
                if (target == null) {
                    // Targeted player was not found online, start asynchronous lookup in files
                    Bukkit.getScheduler().runTaskAsynchronously(Gamemode4Engine.plugin(), () -> {
                        // Try loading the player's data asynchronously
                        final Player target1 = Gamemode4Engine.plugin().getPlayerLoader().loadPlayer(uuid);
                        if (target1 == null) {
                            player.sendMessage(ChatColor.RED + "Player not found!");
                            return;
                        }

                        // Open target's inventory synchronously
                        Bukkit.getScheduler().runTask(Gamemode4Engine.plugin(), () -> {
                            Player player1 = Bukkit.getPlayer(playerUUID);
                            // If sender is no longer online after loading the target, abort!
                            if (player1 == null) {
                                return;
                            }

                            openInventory(player1, target1);
                        });
                    });
                } else {
                    openInventory(player, target);
                }
            }
        }
        return true;
    }

    private void openInventory(Player player, Player target) {
        if (target == null) {
            player.sendMessage(ChatColor.RED + "Player not found!");
            return;
        }

        if (!player.hasPermission("gm4.openinv.override") && Gamemode4Engine.protect.get().getBoolean(target.getUniqueId().toString(), false)) {
            player.sendMessage(ChatColor.RED + target.getDisplayName() + "'s ender chest is protected!");
            return;
        }

        openEnderHistory.put(player.getUniqueId(), target.getUniqueId());

        SpecialEnderChest enderChest = Gamemode4Engine.plugin().getPlayerEnderChest(target, true);
        player.openInventory(enderChest.getBukkitInventory());
    }
}
