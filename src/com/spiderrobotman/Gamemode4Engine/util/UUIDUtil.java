package com.spiderrobotman.Gamemode4Engine.util;

import com.nametagedit.plugin.utils.UUIDFetcher;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

/**
 * Project: Gamemode4Engine
 * Author: SpiderRobotMan
 * Date: May 18 2016
 * Website: http://www.spiderrobotman.com
 */

public class UUIDUtil {
    private UUIDUtil() {
    }

    private static Player getPlayer(String name) {
        Validate.notNull(name, "Name cannot be null");

        Player found = null;
        String lowerName = name.toLowerCase();
        int delta = Integer.MAX_VALUE;

        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        for (Player player : players) {
            if (player.getName().toLowerCase().startsWith(lowerName)) {
                int curDelta = player.getName().length() - lowerName.length();

                if (curDelta < delta) {
                    found = player;
                    delta = curDelta;
                }

                if (curDelta == 0) break;
            }
        }

        return found;
    }

    @SuppressWarnings("deprecation")
    private static UUID getUUIDLocally(String name) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
        return offlinePlayer.hasPlayedBefore() ? offlinePlayer.getUniqueId() : null;
    }

    /**
     * Returns the UUID of a player by their name.
     *
     * @param name the name of the player to get the UUID of
     * @return the player's UUID or null
     */
    public static UUID getUUIDOf(String name) {
        UUID uuid;
        Player player = getPlayer(name);

        if (player != null) {
            uuid = player.getUniqueId();
        } else {
            if (Bukkit.getServer().getOnlineMode()) {
                if (!Bukkit.getServer().isPrimaryThread()) {
                    UUIDFetcher fetcher = new UUIDFetcher(Collections.singletonList(name));
                    Map<String, UUID> response;

                    try {
                        response = fetcher.call();
                        uuid = response.get(name.toLowerCase());
                    } catch (Exception e) {
                        uuid = getUUIDLocally(name);
                    }
                } else {
                    uuid = getUUIDLocally(name);
                }
            } else {
                uuid = getUUIDLocally(name);
            }
        }

        return uuid;
    }
}
