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

    public ServerPlayer(UUID id) {
        Player p = Bukkit.getPlayer(id);
        if(p == null || !p.isOnline()) {
            this.online = false;
            //get data from database
        } else {
            this.name = p.getName();
        }
    }

    public ServerPlayer(Player p) {
        this.uuid = p.getUniqueId();
        if(!p.isOnline()) {
            this.online = false;
            //get data from database
        } else {
            this.name = p.getName();
        }
    }

    public String getName() {
        return this.name;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(this.uuid);
    }

    public boolean isOnline() {
        return this.online;
    }

    public boolean hasAccess() {
        return true;
    }

}
