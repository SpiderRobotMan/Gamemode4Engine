package com.spiderrobotman.Gamemode4Engine.handler;

import com.spiderrobotman.Gamemode4Engine.data.ServerPlayer;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * Created by spide on 3/28/2016.
 */
public class AccessHandler {

    //
    // Make sure players are allowed to join the server.
    //

    public static void handlePlayerLogin(PlayerLoginEvent e) {
        Player p = e.getPlayer();
        ServerPlayer sp = new ServerPlayer(p);
    }

    public static void handlePlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        ServerPlayer sp = new ServerPlayer(p);
        if(!sp.hasAccess()) {
            p.setGameMode(GameMode.SPECTATOR);
        }
    }

    public static void handlePlayerMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        ServerPlayer sp = new ServerPlayer(p);
        if(!e.getTo().getBlock().isEmpty() && !sp.hasAccess()) {
            e.setCancelled(true);
        }
    }

    public static void handlePlayerTeleport(PlayerTeleportEvent e) {
        Player p = e.getPlayer();
        ServerPlayer sp = new ServerPlayer(p);
        if(e.getCause().equals(PlayerTeleportEvent.TeleportCause.SPECTATE) && !sp.hasAccess()) {
            e.setCancelled(true);
        }
    }

}
