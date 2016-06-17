package com.spiderrobotman.Gamemode4Engine.listeners;

import com.spiderrobotman.Gamemode4Engine.command.*;
import com.spiderrobotman.Gamemode4Engine.handler.SpecialPlayerInventory;
import com.spiderrobotman.Gamemode4Engine.main.Gamemode4Engine;
import com.spiderrobotman.Gamemode4Engine.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Project: Gamemode4Engine
 * Author: SpiderRobotMan
 * Date: May 17 2016
 * Website: http://www.spiderrobotman.com
 */
public class PlayerListener implements Listener {

    private static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    @EventHandler
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent e) {
        try {
            if (!Gamemode4Engine.adb.hasConnection() || !Gamemode4Engine.db.hasConnection()) {
                e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.GOLD + "Sorry " + e.getName() + "\n" + ChatColor.RED + "It seems our database is offline!\n\n" + ChatColor.AQUA + "Try again in a few seconds.");
                return;
            }
        } catch (SQLException e1) {
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.GOLD + "Sorry " + e.getName() + "\n" + ChatColor.RED + "It seems our database is offline!\n\n" + ChatColor.AQUA + "Try again in a few seconds.");
            TextUtil.logError("MySQL database connection could not be checked! ERROR: " + e1.getMessage());
            return;
        }

        HashMap<String, Object> bmap = Gamemode4Engine.db.fetchPlayerBans(e.getUniqueId());

        if (bmap != null) e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, TextUtil.buildBanMessage(bmap));

        HashMap<String, Object> pmap = Gamemode4Engine.adb.fetchPlayerAccess(e.getUniqueId(), e.getName());

        if (pmap == null) {
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, TextUtil.buildAccessMessage(e.getName()));
        } else {
            Gamemode4Engine.db.updatePlayer(e.getUniqueId(), e.getName(), e.getAddress().getHostAddress());

            final String uuid = e.getUniqueId().toString();
            final String name = e.getName();
            if ((boolean) pmap.get("patreon")) {
                Gamemode4Engine.plugin().getServer().getScheduler().runTask(Gamemode4Engine.plugin(), () -> {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pex user " + uuid + " add gm4.rank.patreon");
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pex user " + uuid + " add gm4.nickname");
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "scoreboard players set " + name + " patrons 1");
                });
            } else {
                Gamemode4Engine.plugin().getServer().getScheduler().runTask(Gamemode4Engine.plugin(), () -> {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pex user " + uuid + " remove gm4.rank.patreon");
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pex user " + uuid + " remove gm4.nickname");
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "scoreboard players reset " + name + " patrons");
                });
            }

        }

        if (e.getLoginResult() == AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            NickCommand.loadNickNameFromUUID(e.getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent e) {
        Player p = e.getPlayer();
        if (!RestrictCommand.canBypassRestrict(e.getPlayer())) {
            e.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.GOLD + "Sorry " + e.getPlayer().getName() + "\n\n" + ChatColor.RED + "Server access is restricted!");
        }
        if (e.getResult() == PlayerLoginEvent.Result.KICK_FULL) {
            if (p.hasPermission("gm4.rank.patreon") || p.hasPermission("gm4.rank.cmod") || p.hasPermission("gm4.rank.mod") || p.hasPermission("gm4.rank.admin")) {
                e.allow();
            } else {
                e.disallow(PlayerLoginEvent.Result.KICK_FULL, ChatColor.GOLD + "Sorry " + p.getName() + "\n\n" + ChatColor.RED + "The server is full!");
            }
        }
        if (p.hasPermission("gm4.openinv.bypass")) {
            final String uuid = p.getUniqueId().toString();
            Gamemode4Engine.plugin().getServer().getScheduler().runTaskAsynchronously(Gamemode4Engine.plugin(), () -> {
                Gamemode4Engine.protect.get().set(uuid, true);
                Gamemode4Engine.protect.save();
            });
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        NickCommand.updatePlayerName(e.getPlayer());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        final Player player = e.getPlayer();
        NickCommand.updatePlayerName(player);
        e.setJoinMessage(ChatColor.YELLOW + e.getPlayer().getDisplayName() + ChatColor.YELLOW + " joined the game");

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

        if (WatchCommand.previous.containsKey(e.getPlayer().getUniqueId())) {
            {
                e.getPlayer().teleport(WatchCommand.previous.get(e.getPlayer().getUniqueId()));
                WatchCommand.previous.remove(e.getPlayer().getUniqueId());
                e.getPlayer().setGameMode(GameMode.SURVIVAL);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        SpecialPlayerInventory inventory = Gamemode4Engine.plugin().getPlayerInventory(player, false);
        if (inventory != null) {
            if (inventory.playerOffline()) {
                Gamemode4Engine.plugin().removeLoadedInventory(e.getPlayer());
            }
        }
        Gamemode4Engine.plugin().getServer().getScheduler().runTaskAsynchronously(Gamemode4Engine.plugin(), () -> Gamemode4Engine.db.updatePlayer(e.getPlayer().getUniqueId(), e.getPlayer().getName(), e.getPlayer().getAddress().getHostString()));

        e.setQuitMessage(ChatColor.YELLOW + e.getPlayer().getDisplayName() + ChatColor.YELLOW + " left the game");

        NickCommand.nicks.remove(e.getPlayer().getUniqueId());
        MsgCommand.history.remove(e.getPlayer().getUniqueId());
        if (WatchCommand.watching.containsKey(e.getPlayer().getUniqueId())) {
            e.getPlayer().setGameMode(GameMode.SURVIVAL);
            WatchCommand.watching.remove(e.getPlayer().getUniqueId());
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

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        if (e.getCause() == PlayerTeleportEvent.TeleportCause.COMMAND || e.getCause() == PlayerTeleportEvent.TeleportCause.PLUGIN) {
            BackCommand.updateLocation(e.getPlayer(), e.getFrom());
        }
        if ((e.getCause() == PlayerTeleportEvent.TeleportCause.SPECTATE || e.getCause() == PlayerTeleportEvent.TeleportCause.COMMAND) && WatchCommand.watching.containsKey(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.RED + "You may not teleport while watching a player!");
        }
        if (WatchCommand.watching.containsValue(e.getPlayer().getUniqueId())) {
            Player p1 = Bukkit.getPlayer(getKeyByValue(WatchCommand.watching, e.getPlayer().getUniqueId()));
            p1.teleport(e.getTo());
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (WatchCommand.watching.containsValue(e.getPlayer().getUniqueId())) {
            Player p1 = Bukkit.getPlayer(getKeyByValue(WatchCommand.watching, e.getPlayer().getUniqueId()));
            if (p1.getSpectatorTarget() == null) {
                if (p1.getLocation().distance(e.getTo()) > 25) {
                    Location newLoc = e.getTo().clone();
                    Location trgLoc = e.getFrom().clone();
                    double xdiff = newLoc.getX() - trgLoc.getX();
                    double ydiff = newLoc.getY() - trgLoc.getY();
                    double zdiff = newLoc.getZ() - trgLoc.getZ();
                    newLoc.add(xdiff, ydiff, zdiff);
                    p1.teleport(p1.getLocation().add(xdiff, ydiff, zdiff));
                }
            }
        }
        if (WatchCommand.watching.containsKey(e.getPlayer().getUniqueId())) {
            if (e.getTo().distance(Bukkit.getPlayer(WatchCommand.watching.get(e.getPlayer().getUniqueId())).getLocation()) > 25) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        BackCommand.updateLocation(e.getEntity().getPlayer(), e.getEntity().getLocation());
    }

}
