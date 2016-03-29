package com.spiderrobotman.Gamemode4Engine.handler;

import org.bukkit.entity.Player;

/**
 * Created by spide on 3/28/2016.
 */
public class BanHandler {

    //
    // Handle player ban checks.
    //

    public boolean isBanned(Player p) {
        if(p.isBanned()) {
            return true;
        }
        return false;
    }

}
