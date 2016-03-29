package com.spiderrobotman.Gamemode4Engine.data;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;

import static com.spiderrobotman.Gamemode4Engine.main.Gamemode4Engine.plugin;
import static com.spiderrobotman.Gamemode4Engine.util.TextUtil.logError;
import static com.spiderrobotman.Gamemode4Engine.util.TextUtil.logInfo;

/**
 * Created by spide on 3/28/2016.
 */
public class ConfigManager {

    public static JSONObject config;
    private static File f = new File(plugin().getDataFolder() + "/config.json");

    public static boolean init() {
        if(!injectDefaults(f)) {
            return false;
        }
        config = readFile(f);
        if(config == null) {
            return false;
        }
        return true;
    }

    public static JSONObject getConfig() {
        return config;
    }

    public static boolean updateConfig() {
        JSONObject file = readFile(f);
        if(file != null && file != config) {
            config = file;
            return true;
        }
        return false;
    }

    public static boolean writeFile(File f, JSONObject obj) {
        if(!f.exists()) {
            if(f.mkdir()) {
                logInfo("config.json successfully created.");
            }
        }
        if(obj == null) {
            return false;
        }
        try {
            FileWriter file = new FileWriter(f);
            file.write(obj.toJSONString());
            file.flush();
            file.close();
            return true;
        } catch (IOException e) {
            logError("config.json cannot be written!");
            return false;
        }
    }

    public static JSONObject readFile(File f) {
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(f));
            return (JSONObject) obj;
        } catch (FileNotFoundException e) {
            logError("config.json not found!");
        } catch (IOException e) {
            logError("config.json cannot be read!");
        } catch (ParseException e) {
            logError("config.json invalid format!");
        }
        return null;
    }

    public static boolean injectDefaults(File f) {
        InputStream inputStream = plugin().getClass().getResourceAsStream("config.json");
        JSONParser jsonParser = new JSONParser();
        JSONObject defaults;
        try {
            defaults = (JSONObject)jsonParser.parse(new InputStreamReader(inputStream, "UTF-8"));
        } catch (IOException e) {
            logError("config.json defaults cannot be written!");
            return false;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }

        return writeFile(f, defaults);
    }

}
