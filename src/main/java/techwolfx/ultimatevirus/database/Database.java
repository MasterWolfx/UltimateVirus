package techwolfx.ultimatevirus.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.entity.Player;
import techwolfx.ultimatevirus.Ultimatevirus;

public abstract class Database {
    Ultimatevirus plugin;
    Connection connection;
    // The name of the table we created back in SQLite class.
    public String table = "player_infos";
    public int tokens = 0;
    public Database(Ultimatevirus instance){
        plugin = instance;
    }

    public abstract Connection getSQLConnection();

    public abstract void load();

    public void initialize(){
        connection = getSQLConnection();
        try{
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + table);
            ResultSet rs = ps.executeQuery();
            close(ps,rs);

        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "Unable to retreive connection", ex);
        }
    }

    public void close(PreparedStatement ps, ResultSet rs){
        try {
            if (ps != null)
                ps.close();
            if (rs != null)
                rs.close();
        } catch (SQLException ex) {
            Error.close(plugin, ex);
        }
    }


    // Main methods
    public void setTokens(Player player, UUID uuid, Boolean infected, Integer points) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("REPLACE INTO " + table + " (player, uuid, infected, online_points) VALUES(?, ?, ?, ?)"); // IMPORTANT. In SQLite class, We made 3 colums. player, Kills, Total.
            ps.setString(1, player.getName());                                                               // YOU MUST put these into this line!! And depending on how many
            // colums you put (say you made 5) All 5 need to be in the brackets
            // Seperated with comma's (,) AND there needs to be the same amount of
            // question marks in the VALUES brackets. Right now i only have 3 colums
            // So VALUES (?,?,?) If you had 5 colums VALUES(?,?,?,?,?)
            ps.setString(2, String.valueOf(uuid));
            ps.setInt(3, boolToInt(infected)); // This sets the value in the database. The colums go in order. Player is ID 1, kills is ID 2, Total would be 3 and so on. you can use
            // setInt, setString and so on. tokens and total are just variables sent in, You can manually send values in as well. p.setInt(2, 10) <-
            // This would set the players kills instantly to 10. Sorry about the variable names, It sets their kills to 10 i just have the variable called
            // Tokens from another plugin :/
            ps.setInt(4, points);
            ps.executeUpdate();
            return;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return;
    }

    public boolean isPlayerRegistered(String player){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT player FROM " + table +";");

            rs = ps.executeQuery();
            while(rs.next()){
                if(rs.getString("player").equalsIgnoreCase(player)){
                    return true;
                }
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return false;
    }

    public void setInfected(Player player, boolean infected) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();

            ps = conn.prepareStatement("UPDATE " + table + " SET infected = '" + boolToInt(infected) + "' WHERE player = '" + player.getName() + "';");
            ps.executeUpdate();
            return;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return;
    }
    public boolean isInfected(String pName) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT player, infected FROM " + table + ";");

            rs = ps.executeQuery();
            while(rs.next()){
                if(rs.getString("player").equalsIgnoreCase(pName)){
                    return intToBool(rs.getInt("infected"));
                }
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return false;
    }

    public void setPoints(Player player, int points) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            /*
            UPDATE table
            SET infected = 1,
            column_2 = new_value_2
            WHERE
            player = player
             */

            ps = conn.prepareStatement("UPDATE " + table + " SET online_points = '" + points + "' WHERE player = '" + player.getName() + "';");
            ps.executeUpdate();
            return;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return;
    }
    public int getPoints(Player player) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + table + " WHERE player = '" + player.getName() + "';");

            rs = ps.executeQuery();
            while(rs.next()){
                if(rs.getString("player").equals(player.getName())){
                    return rs.getInt("online_points");
                }
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return 0;
    }

    public List<String> getInfected() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + table + " WHERE infected = 1;");

            rs = ps.executeQuery();
            List<String> players = new ArrayList<>();
            while(rs.next()){
                players.add(rs.getString("player"));
            }
            players.sort(Comparator.comparing( String::toString ));
            return players;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return null;
    }

    // Useful methods to convert bool to int and the other way around
    private int boolToInt(boolean x){
        return x ? 1 : 0;
    }
    private boolean intToBool(int x){
        return x==1;
    }
}
