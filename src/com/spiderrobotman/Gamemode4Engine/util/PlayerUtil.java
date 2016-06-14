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

    public static Player getPlayerFromString(String name) {
        Player p = Bukkit.getPlayerExact(name);
        if (p != null) {
            return p;
        } else {
            if (name.matches("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[34][0-9a-fA-F]{3}-[89ab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}")) {
                UUID uuid = UUID.fromString(name);
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
