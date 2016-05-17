package com.spiderrobotman.Gamemode4Engine.data;

import com.spiderrobotman.Gamemode4Engine.util.TextUtil;

import java.sql.*;
import java.util.HashMap;
import java.util.UUID;

/**
 * Project: Gamemode4Engine
 * Author: SpiderRobotMan
 * Date: May 09 2016
 * Website: http://www.spiderrobotman.com
 */
public class DatabaseManager {

    private Connection connection;
    private String hostname, port, database, user, password;

    public DatabaseManager(String hostname, String port, String user, String password, String database) {
        this.hostname = hostname;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;
        try {
            Connection c = openConnection();
        } catch (SQLException | ClassNotFoundException e) {
            TextUtil.logError("MySQL cannot connect! ERROR: " + e.getMessage());
        }
    }

    private Connection openConnection() throws SQLException, ClassNotFoundException {
        if (hasConnection()) {
            return this.connection;
        }

        String connectionURL = "jdbc:mysql://" + this.hostname + ":" + this.port;

        if (this.database != null) {
            connectionURL = connectionURL + "/" + this.database;
        }

        this.connection = DriverManager.getConnection(connectionURL, this.user, this.password);
        return this.connection;
    }

    private boolean hasConnection() throws SQLException {
        return this.connection != null && !this.connection.isClosed();
    }

    public boolean updatePlayer(UUID uuid, String name, String ip) {
        try {
            while (!this.hasConnection()) {
                this.openConnection();
            }
            PreparedStatement ps = connection.prepareStatement("REPLACE INTO server_players (uuid, current_name, last_online, ip) VALUES (?, ?, ?, ?);");
            ps.setString(1, uuid.toString());
            ps.setString(2, name);
            ps.setLong(3, System.currentTimeMillis());
            ps.setString(4, ip);

            return (ps.executeUpdate() >= 1);

        } catch (SQLException | ClassNotFoundException e) {
            TextUtil.logError("MySQL cannot set player ban data! ERROR: " + e.getMessage());
        }
        return false;
    }

    public HashMap<String, Object> fetchPlayer(UUID uuid) {
        try {
            while (!this.hasConnection()) {
                this.openConnection();
            }
            PreparedStatement ps = this.connection.prepareStatement("SELECT * FROM server_players WHERE uuid = ?;");
            ps.setString(1, uuid.toString());
            ResultSet res = ps.executeQuery();

            if (res.next()) {
                return resultToMap(res);
            }

        } catch (SQLException | ClassNotFoundException e) {
            TextUtil.logError("MySQL cannot get player data! ERROR: " + e.getMessage());
        }
        return null;
    }

    private HashMap<String, Object> fetchPlayer(String name) {
        try {
            while (!this.hasConnection()) {
                this.openConnection();
            }
            PreparedStatement ps = this.connection.prepareStatement("SELECT * FROM server_players WHERE current_name = ?;");
            ps.setString(1, name);
            ResultSet res = ps.executeQuery();

            if (res.next()) {
                return resultToMap(res);
            }

        } catch (SQLException | ClassNotFoundException e) {
            TextUtil.logError("MySQL cannot get player data! ERROR: " + e.getMessage());
        }
        return null;
    }

    public HashMap<String, Object> fetchPlayerAccess(UUID uuid, String name) {
        try {
            while (!this.hasConnection()) {
                this.openConnection();
            }
            PreparedStatement ps = this.connection.prepareStatement("SELECT * FROM serverWhitelist WHERE UUID = ? OR username = ?;");
            ps.setString(1, uuid.toString());
            ps.setString(2, name);
            ResultSet res = ps.executeQuery();

            if (res.next()) {
                if (res.getString("UUID").isEmpty()) {
                    PreparedStatement ps2 = connection.prepareStatement("UPDATE serverWhitelist SET UUID = ? WHERE username = ?;");
                    ps2.setString(1, uuid.toString());
                    ps2.setString(2, name);
                    ps2.executeUpdate();
                }
                return resultToMap(res);
            }

        } catch (SQLException | ClassNotFoundException e) {
            TextUtil.logError("MySQL cannot get player access data! ERROR: " + e.getMessage());
        }
        return null;
    }

    public HashMap<String, Object> banPlayer(UUID uuid, String by, String reason) {
        try {
            while (!this.hasConnection()) {
                this.openConnection();
            }
            PreparedStatement ps = connection.prepareStatement("INSERT INTO server_bans (uuid, reference_id, reason, banned_by) VALUES (?, ?, ?, ?);");
            ps.setString(1, uuid.toString());
            ps.setString(2, UUID.randomUUID().toString().replace("-", ""));
            ps.setString(3, reason);
            ps.setString(4, by);
            ps.executeUpdate();

            return fetchPlayerBans(uuid);

        } catch (SQLException | ClassNotFoundException e) {
            TextUtil.logError("MySQL cannot set player ban data! ERROR: " + e.getMessage());
        }
        return null;
    }

    public HashMap<String, Object> tempbanPlayer(UUID uuid, String by, String reason, long time) {
        try {
            while (!this.hasConnection()) {
                this.openConnection();
            }
            PreparedStatement ps = connection.prepareStatement("INSERT INTO server_bans (uuid, reference_id, reason, banned_by, type, time_end) VALUES (?, ?, ?, ?, ?, ?);");
            ps.setString(1, uuid.toString());
            ps.setString(2, UUID.randomUUID().toString().replace("-", ""));
            ps.setString(3, reason);
            ps.setString(4, by);
            ps.setString(5, "temporary");
            ps.setLong(6, time);
            ps.executeUpdate();

            return fetchPlayerBans(uuid);

        } catch (SQLException | ClassNotFoundException e) {
            TextUtil.logError("MySQL cannot set player tempban data! ERROR: " + e.getMessage());
        }
        return null;
    }

    public boolean unbanPlayer(String name) {
        try {
            while (!this.hasConnection()) {
                this.openConnection();
            }

            HashMap<String, Object> obj = this.fetchPlayer(name);

            if (obj == null) return false;

            String uuid = (String) obj.get("uuid");

            PreparedStatement ps = connection.prepareStatement("DELETE FROM server_bans WHERE uuid = ?;");
            ps.setString(1, uuid);

            return (ps.executeUpdate() >= 1);

        } catch (SQLException | ClassNotFoundException e) {
            TextUtil.logError("MySQL cannot set player tempban data! ERROR: " + e.getMessage());
        }
        return false;
    }

    public HashMap<String, Object> fetchPlayerBans(UUID uuid) {
        try {
            while (!this.hasConnection()) {
                this.openConnection();
            }
            PreparedStatement ps = this.connection.prepareStatement("SELECT * FROM server_bans WHERE uuid = ?;");
            ps.setString(1, uuid.toString());
            ResultSet res = ps.executeQuery();

            ResultSetMetaData metaData = res.getMetaData();
            int colCount = metaData.getColumnCount();
            long banTime = 0;
            HashMap<String, Object> out = new HashMap<>();

            while (res.next()) {
                if (res.getString("type").equals("permanent")) {
                    return resultToMap(res);
                }
                if (res.getLong("time_end") > System.currentTimeMillis()) {
                    if (res.getLong("time_end") > banTime || banTime == 0) {
                        out.clear();
                        for (int i = 1; i <= colCount; i++) {
                            out.put(metaData.getColumnLabel(i), res.getObject(i));
                        }
                    }
                }
                banTime = res.getLong("time_end");
            }

            if (!out.isEmpty()) return out;

        } catch (SQLException | ClassNotFoundException e) {
            TextUtil.logError("MySQL cannot get player data! ERROR: " + e.getMessage());
        }
        return null;
    }

    private HashMap<String, Object> resultToMap(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int colCount = metaData.getColumnCount();
        HashMap<String, Object> out = new HashMap<>();
        for (int i = 1; i <= colCount; i++) {
            out.put(metaData.getColumnLabel(i), rs.getObject(i));
        }
        return out;
    }

}