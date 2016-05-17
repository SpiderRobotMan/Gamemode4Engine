package com.spiderrobotman.Gamemode4Engine.data;

import com.spiderrobotman.Gamemode4Engine.util.TextUtil;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.HashMap;


public class ConfigManager {

    private final JavaPlugin plugin;
    private HashMap<String, Config> configs = new HashMap<>();

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public Config getConfig(String name) {
        if (!configs.containsKey(name))
            configs.put(name, new Config(name));

        return configs.get(name);
    }

    public Config saveConfig(String name) {
        return getConfig(name).save();
    }

    public Config reloadConfig(String name) {
        return getConfig(name).reload();
    }

    public class Config {

        private String name;
        private File file;
        private YamlConfiguration config;

        Config(String name) {
            this.name = name;
        }

        public Config save() {
            if ((this.config == null) || (this.file == null))
                return this;
            try {
                if (config.getConfigurationSection("").getKeys(true).size() != 0)
                    config.save(this.file);
            } catch (IOException ex) {
                TextUtil.logError("Config save failed! ERROR: " + ex.getMessage());
            }
            return this;
        }

        public YamlConfiguration get() {
            if (this.config == null)
                reload();

            return this.config;
        }

        public Config saveDefaultConfig() {
            file = new File(plugin.getDataFolder(), this.name);

            plugin.saveResource(this.name, false);

            return this;
        }

        Config reload() {
            if (file == null)
                this.file = new File(plugin.getDataFolder(), this.name);

            this.config = YamlConfiguration.loadConfiguration(file);

            Reader defConfigStream;
            try {
                defConfigStream = new InputStreamReader(plugin.getResource(this.name), "UTF8");

                YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
                this.config.setDefaults(defConfig);
            } catch (UnsupportedEncodingException | NullPointerException e) {
                TextUtil.logError("Config reload failed!");
            }
            return this;
        }

        public Config copyDefaults(boolean force) {
            get().options().copyDefaults(force);
            return this;
        }

        public Config set(String key, Object value) {
            get().set(key, value);
            return this;
        }

        public Object get(String key) {
            return get().get(key);
        }
    }

}

