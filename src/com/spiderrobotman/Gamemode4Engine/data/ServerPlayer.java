package com.spiderrobotman.Gamemode4Engine.data;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Created by spide on 3/28/2016.
 */
public class ServerPlayer{

    private String name;
    private UUID uuid;
    private boolean online = true;

    public ServerPlayer(UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);
        if(p == null) {
            this.online = false;
        }
    }

    public String getName() {
        return this.name;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public boolean online() {
        return this.online;
    }

}
