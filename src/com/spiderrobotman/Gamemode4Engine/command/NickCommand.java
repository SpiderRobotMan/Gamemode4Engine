package com.spiderrobotman.Gamemode4Engine.command;

import com.spiderrobotman.Gamemode4Engine.main.Gamemode4Engine;
import com.spiderrobotman.Gamemode4Engine.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Project: Gamemode4Engine
 * Author: SpiderRobotMan
 * Date: May 17 2016
 * Website: http://www.spiderrobotman.com
 */
public class NickCommand implements CommandExecutor {

    public static Map<UUID, String> nicks = new ConcurrentHashMap<>();

    private static String getFullName(Player p) {
        String fin = getPrefix(p) + getNickName(p);
        return fin.replace("&", "§") + ChatColor.RESET;
    }

    private static String getNickName(Player p) {
        String nick = "";
        String nick_pre = "~";
        if (!Bukkit.getServer().isPrimaryThread()) {
            nick = Gamemode4Engine.nicks.get().getString(p.getUniqueId().toString());
            nick_pre = Gamemode4Engine.config.get().getString("nickname_prefix");
        } else {
            if (nicks.containsKey(p.getUniqueId())) {
                nick = nicks.get(p.getUniqueId());
            }
        }

        if (nick != null && !nick.isEmpty() && !nick.equalsIgnoreCase(p.getName())) {
            nicks.put(p.getUniqueId(), nick);
            return (nick_pre + nick).replace("&", "§");
        } else {
            return p.getName();
        }
    }

    public static void loadNickNameFromUUID(UUID uuid) {
        if (!Bukkit.getServer().isPrimaryThread()) {
            String nick = Gamemode4Engine.nicks.get().getString(uuid.toString());
            if (nick != null && !nick.isEmpty()) {
                nicks.put(uuid, nick);
            }
        }
    }

    public static void updatePlayerName(Player p) {
        p.setDisplayName(getFullName(p));
        p.setPlayerListName(getFullName(p));
    }

    private static String getPrefix(Player p) {
        YamlConfiguration cf = Gamemode4Engine.config.get();
        String prefix = cf.getString("default_prefix");

        if (p.hasPermission("gm4.rank.admin")) {
            prefix += cf.getString("admin_prefix");
        } else if (p.hasPermission("gm4.rank.mod")) {
            prefix += cf.getString("mod_prefix");
        } else if (p.hasPermission("gm4.rank.cmod")) {
            prefix += cf.getString("cmod_prefix");
        }
        if (p.hasPermission("gm4.rank.patreon")) {
            prefix += cf.getString("patreon_prefix");
        }
        return prefix.replace("&", "§");
    }

    private static List<String> getPossiblePlayerNames(Player exclude) {
        List<String> names = new ArrayList<>();
        for (org.bukkit.World world : Bukkit.getServer().getWorlds()) {
            String worldname = world.getName();

            File playersFolder = new File(worldname + "/playerdata/");
            String[] arr = playersFolder.list((f, s) -> s.endsWith(".dat"));
            for (String a : arr) {
                UUID uuid = UUID.fromString(a.replaceAll(".dat$", ""));
                if (!uuid.equals(exclude.getUniqueId())) {
                    OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
                    if (p != null) {
                        names.add(p.getName());
                        String nick1 = nicks.get(p.getUniqueId());
                        if (nick1 != null) {
                            String nick = ChatColor.stripColor(nick1.replace("&", "§"));
                            if (!nick.isEmpty()) names.add(nick);
                        }
                    }
                }
            }

        }
        return names;
    }

    private static boolean nameMatch(String name, Player exclude) {
        name = ChatColor.stripColor(name.replace("&", "§"));
        int length = name.length();
        for (String pn : getPossiblePlayerNames(exclude)) {
            if (name.toLowerCase().contains(pn.toLowerCase())) {
                int diff = length - pn.length();
                if (!(diff >= 5) || !(diff <= -5)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command command, String alias, String[] args) {
        if (cs instanceof Player) {
            Player sender = (Player) cs;
            if (sender.isOp() || sender.hasPermission("gm4.nickname")) {
                if (args.length <= 0) {
                    TextUtil.sendCommandFormatError(sender, "/" + alias + " <nick [-reset]> [<player>]");
                    return true;
                }
                Gamemode4Engine.plugin().getServer().getScheduler().runTaskAsynchronously(Gamemode4Engine.plugin(), () -> {
                    boolean cont = true;
                    Player target = null;

                    if (args.length >= 1) {
                        String test = ChatColor.stripColor(args[0].toLowerCase().replace("&", "§")).replace("§", "");

                        int color_count = args[0].length() - test.length();
                        int length = args[0].length() - color_count;
                        boolean match = false;
                        if (length < 0) length = 0;

                        if (args.length == 1 && nameMatch(args[0], sender)) match = true;
                        if (args.length == 2) {
                            target = Bukkit.getPlayerExact(args[1]);
                            if (target != null && nameMatch(args[0], target)) match = true;
                        }

                        if (!args[0].matches("\\A\\p{ASCII}*\\z")) {
                            sender.sendMessage(ChatColor.RED + "A nickname can only contain ASCII characters!");
                            cont = false;
                        } else if (length > 15) {
                            sender.sendMessage(ChatColor.RED + "A nickname can only be 15 visible characters long! Yours is " + length + ".");
                            cont = false;
                        } else if (length < 3) {
                            sender.sendMessage(ChatColor.RED + "A nickname must have atleast 3 visible characters! Yours has " + length + ".");
                            cont = false;
                        } else if (!args[0].equalsIgnoreCase("-reset") && match) {
                            sender.sendMessage(ChatColor.RED + "This nickname is too similar to another player!");
                            cont = false;
                        }
                    }
                    if (cont) {
                        if (args.length == 1) {
                            setNickName(sender, args[0]);
                        }
                        if (args.length == 2) {
                            if (args[1].equalsIgnoreCase(sender.getName())) {
                                setNickName(sender, args[0]);
                            } else if (sender.hasPermission("gm4.nickname.others")) {
                                if (target != null) {
                                    setNickName(target, args[0]);
                                    if (!target.hasPermission("gm4.nickname.color")) {
                                        sender.sendMessage(ChatColor.GREEN + "Their nickname has been set to: " + ChatColor.RESET + ChatColor.stripColor(args[0].replace("&", "§")).replace("§", ""));
                                    } else {
                                        sender.sendMessage(ChatColor.GREEN + "Their nickname has been set to: " + ChatColor.RESET + args[0].replace("&", "§"));
                                    }
                                } else {
                                    sender.sendMessage(ChatColor.RED + "Player " + ChatColor.GOLD + args[1] + ChatColor.RED + " not found!");
                                }
                            } else {
                                sender.sendMessage(ChatColor.RED + "You don't have permission to give others nicknames!");
                            }
                        }
                    }
                });
            }
        }
        return true;
    }

    private void setNickName(Player p, String nickname) {
        if (!nickname.equalsIgnoreCase("-reset")) {
            String nick = nickname.replace("&", "§");
            if (!p.hasPermission("gm4.nickname.color")) {
                nick = ChatColor.stripColor(nick).replace("§", "");
            }
            nick = nick.replace("§", "&");
            Gamemode4Engine.nicks.get().set(p.getUniqueId().toString(), nick);
            nicks.put(p.getUniqueId(), nick);
            p.sendMessage(ChatColor.GREEN + "Your nickname has been set to: " + ChatColor.RESET + nick.replace("&", "§"));
        } else {
            Gamemode4Engine.nicks.get().set(p.getUniqueId().toString(), "");
            nicks.put(p.getUniqueId(), "");
            p.sendMessage(ChatColor.GREEN + "Your nickname has been reset to: " + ChatColor.RESET + p.getName());
        }
        Gamemode4Engine.nicks.save();
        updatePlayerName(p);
    }

}