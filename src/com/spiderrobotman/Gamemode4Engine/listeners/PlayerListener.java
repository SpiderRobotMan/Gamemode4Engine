package com.spiderrobotman.Gamemode4Engine.listeners;

import com.spiderrobotman.Gamemode4Engine.handler.AccessHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;

/**
 * Created by spide on 3/28/2016.
 */
public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent e) {
        AccessHandler.handlePlayerLogin(e);
    }

    @EventHandler
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent e) {
        AccessHandler.handlePlayerPreLogin(e);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {

    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e) {
    }
}
