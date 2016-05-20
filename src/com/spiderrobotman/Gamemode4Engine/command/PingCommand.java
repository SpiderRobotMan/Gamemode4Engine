package com.spiderrobotman.Gamemode4Engine.command;

import com.spiderrobotman.Gamemode4Engine.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * Project: Gamemode4Engine
 * Author: SpiderRobotMan
 * Date: May 20 2016
 * Website: http://www.spiderrobotman.com
 */
public class PingCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command command, String alias, String[] args) {
        if (cs instanceof Player) {
            Player sender = (Player) cs;
            if (args.length == 0) {
                int ping;
                try {
                    ping = getPlayerPing(sender) / 2;
                } catch (Exception e) {
                    sender.sendMessage(ChatColor.RED + "Sorry, your ping could not be gotten.");
                    return true;
                }
                if (!(ping <= 0)) {
                    sender.sendMessage(ChatColor.GREEN + "Your current ping is: " + ChatColor.GOLD + ping + "ms");
                } else {
                    sender.sendMessage(ChatColor.RED + "Sorry, your ping could not be gotten.");
                }
                return true;
            } else {
                TextUtil.sendCommandFormatError(sender, "/" + alias);
            }
        }
        return true;
    }

    private int getPlayerPing(Player player) throws Exception {
        int ping;

        Class<?> craftPlayer = Class.forName("org.bukkit.craftbukkit." + getServerVersion() + "entity.CraftPlayer");
        Object converted = craftPlayer.cast(player);
        Method handle = converted.getClass().getMethod("getHandle");
        Object entityPlayer = handle.invoke(converted);
        Field pingField = entityPlayer.getClass().getField("ping");
        ping = pingField.getInt(entityPlayer);

        return ping;
    }

    private String getServerVersion() {

        Pattern brand = Pattern.compile("(v|)[0-9][_.][0-9][_.][R0-9]*");
        String version;

        String pkg = Bukkit.getServer().getClass().getPackage().getName();
        String version0 = pkg.substring(pkg.lastIndexOf('.') + 1);

        if (!brand.matcher(version0).matches()) {
            version0 = "";
        }

        version = version0;

        return !"".equals(version) ? version + "." : "";
    }
}
