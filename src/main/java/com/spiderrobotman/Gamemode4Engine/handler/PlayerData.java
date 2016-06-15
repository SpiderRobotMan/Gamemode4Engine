package com.spiderrobotman.Gamemode4Engine.handler;

import com.mojang.authlib.GameProfile;
import com.spiderrobotman.Gamemode4Engine.util.TextUtil;
import net.minecraft.server.v1_10_R1.EntityPlayer;
import net.minecraft.server.v1_10_R1.MinecraftServer;
import net.minecraft.server.v1_10_R1.PlayerInteractManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_10_R1.CraftServer;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Project: Gamemode4Engine
 * Author: SpiderRobotMan
 * Date: May 18 2016
 * Website: http://www.spiderrobotman.com
 */
public class PlayerData {

    public PlayerData() {
    }

    public Player loadPlayer(UUID uuid) {
        try {
            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            if (player == null || !player.hasPlayedBefore()) {
                return null;
            }

            GameProfile profile = new GameProfile(uuid, player.getName());
            MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
            EntityPlayer entity = new EntityPlayer(server, server.getWorldServer(0), profile, new PlayerInteractManager(server.getWorldServer(0)));

            Player target = entity.getBukkitEntity();

            if (target != null) {
                target.loadData();

                return target;
            }
        } catch (Exception e) {
            TextUtil.logError("Player uuid data could not be found! ERROR: " + e.getMessage());
        }

        return null;
    }

}
