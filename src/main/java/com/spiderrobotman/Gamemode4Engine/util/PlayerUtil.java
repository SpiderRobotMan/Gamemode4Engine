package com.spiderrobotman.Gamemode4Engine.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Project: Gamemode4Engine
 * Author: SpiderRobotMan
 * Date: Jun 14 2016
 * Website: http://www.spiderrobotman.com
 */
public class PlayerUtil {

    public static Player getPlayerFromString(String player) {
        Player p = Bukkit.getPlayerExact(player);
        if (p != null) {
            return p;
        } else {
            if (UUIDUtil.isUUID(player)) {
                UUID uuid = UUID.fromString(player);
                p = Bukkit.getPlayer(uuid);
                if (p == null) {
                    p = (Player) Bukkit.getOfflinePlayer(uuid);
                    if (p == null) {
                        p = null;
                    }
                }
                return p;
            }
        }
        return null;
    }

}
