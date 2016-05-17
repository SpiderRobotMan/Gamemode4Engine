package com.spiderrobotman.Gamemode4Engine.listeners;

import com.spiderrobotman.Gamemode4Engine.main.Gamemode4Engine;
import com.spiderrobotman.Gamemode4Engine.util.TextUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

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

}
