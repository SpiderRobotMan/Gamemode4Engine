package com.spiderrobotman.Gamemode4Engine.listeners;

import com.spiderrobotman.Gamemode4Engine.handler.SpecialPlayerInventory;
import com.spiderrobotman.Gamemode4Engine.main.Gamemode4Engine;
import com.spiderrobotman.Gamemode4Engine.util.TextUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

/**
 * Project: Gamemode4Engine
 * Author: SpiderRobotMan
 * Date: May 17 2016
 * Website: http://www.spiderrobotman.com
 */
public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent e) {
        HashMap<String, Object> bmap = Gamemode4Engine.db.fetchPlayerBans(e.getUniqueId());

        if (bmap != null) e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, TextUtil.buildBanMessage(bmap));

        HashMap<String, Object> pmap = Gamemode4Engine.adb.fetchPlayerAccess(e.getUniqueId(), e.getName());

        if (pmap == null) {
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, TextUtil.buildAccessMessage(e.getName()));
        } else {
            Gamemode4Engine.db.updatePlayer(e.getUniqueId(), e.getName(), e.getAddress().getHostAddress());
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        String nick = Gamemode4Engine.nicks.get().getString(e.getPlayer().getUniqueId().toString());
        if (nick != null) {
            if (!nick.equalsIgnoreCase(e.getPlayer().getName())) {
                e.getPlayer().setDisplayName(Gamemode4Engine.config.get().getString("nickname_prefix").replace("&", "§") + ChatColor.RESET + nick.replace("&", "§") + ChatColor.RESET);
            } else {
                e.getPlayer().setDisplayName(e.getPlayer().getName());
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        String nick = Gamemode4Engine.nicks.get().getString(e.getPlayer().getUniqueId().toString());
        if (nick != null) {
            if (!nick.equalsIgnoreCase(e.getPlayer().getName())) {
                String displayNick = Gamemode4Engine.config.get().getString("nickname_prefix").replace("&", "§") + ChatColor.RESET + nick.replace("&", "§");
                e.getPlayer().setPlayerListName(displayNick);
                e.setJoinMessage(displayNick + " joined the game");
            } else {
                e.getPlayer().setDisplayName(e.getPlayer().getName());
            }
        }

        final Player player = e.getPlayer();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    return;
                }

                SpecialPlayerInventory inventory = Gamemode4Engine.plugin().getPlayerInventory(player, false);
                if (inventory != null) {
                    inventory.playerOnline(player);
                    player.updateInventory();
                }
            }
        }.runTask(Gamemode4Engine.plugin());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        SpecialPlayerInventory inventory = Gamemode4Engine.plugin().getPlayerInventory(player, false);
        if (inventory != null) {
            if (inventory.playerOffline()) {
                Gamemode4Engine.plugin().removeLoadedInventory(event.getPlayer());
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        HumanEntity player = event.getWhoClicked();

        if (!Gamemode4Engine.plugin().getInventoryAccess().check(inventory, player)) {
            event.setCancelled(true);
        }
    }

}
