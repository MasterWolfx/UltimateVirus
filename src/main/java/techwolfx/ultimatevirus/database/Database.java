package techwolfx.ultimatevirus.database;

import java.sql.*;
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
    String table = "player_infos";
    public Database(Ultimatevirus instance){
        plugin = instance;
    }

    public abstract Connection getSQLConnection();
    public abstract void load();

    public void initialize(){
        connection = getSQLConnection();
        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + table)){
            ResultSet rs = ps.executeQuery();
            rs.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    private void checkConnection(){
        if(connection == null){
            connection = getSQLConnection();
        }
    }

    /* SET METHODS */
    public void setTokens(Player player, UUID uuid, Boolean infected, Integer points) {
        checkConnection();
        try (PreparedStatement ps = connection.prepareStatement("REPLACE INTO " + table + " (player, uuid, infected, online_points) VALUES(?, ?, ?, ?)")) {
            ps.setString(1, player.getName());
            ps.setString(2, String.valueOf(uuid));
            ps.setInt(3, boolToInt(infected));
            ps.setInt(4, points);
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void setInfected(Player player, boolean infected) {
        checkConnection();
        try (PreparedStatement ps = connection.prepareStatement("UPDATE " + table + " SET infected = '" + boolToInt(infected) + "' WHERE player = '" + player.getName() + "';")){
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void setInfected(UUID uuid, boolean infected) {
        checkConnection();
        try (PreparedStatement ps = connection.prepareStatement("UPDATE " + table + " SET infected = '" + boolToInt(infected) + "' WHERE uuid = '" + uuid.toString() + "';")){
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void setPoints(Player player, int points) {
        checkConnection();
        try (PreparedStatement ps = connection.prepareStatement("UPDATE " + table + " SET online_points = '" + points + "' WHERE player = '" + player.getName() + "';")){
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void setPoints(UUID uuid, int points) {
        checkConnection();
        try (PreparedStatement ps = connection.prepareStatement("UPDATE " + table + " SET online_points = '" + points + "' WHERE uuid = '" + uuid.toString() + "';")){
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /* BOOL METHODS */
    public boolean isPlayerRegistered(String pName){
        checkConnection();
        try (PreparedStatement ps = connection.prepareStatement("SELECT player FROM " + table +";"); ResultSet rs = ps.executeQuery() ) {
            while(rs.next()){
                if(rs.getString("player").equalsIgnoreCase(pName)){
                    return true;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean isPlayerRegistered(UUID uuid){
        checkConnection();
        try (PreparedStatement ps = connection.prepareStatement("SELECT uuid FROM " + table +";"); ResultSet rs = ps.executeQuery() ) {
            while(rs.next()){
                if(rs.getString("uuid").equalsIgnoreCase(uuid.toString())){
                    return true;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean isInfected(String pName) {
        checkConnection();
        try (PreparedStatement ps = connection.prepareStatement("SELECT infected, player FROM " + table + " WHERE player = '" + pName + "';"); ResultSet rs = ps.executeQuery()) {
            //return intToBool(rs.getInt("infected"));
            while (rs.next()) {
                if (rs.getString("player").equalsIgnoreCase(pName)) {
                    return intToBool(rs.getInt("infected"));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean isInfected(UUID uuid) {
        checkConnection();
        try (PreparedStatement ps = connection.prepareStatement("SELECT infected, uuid FROM " + table + " WHERE uuid = '" + uuid.toString() + "';"); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                if (rs.getString("uuid").equals(uuid.toString())) {
                    return intToBool(rs.getInt("infected"));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /* GET METHODS */
    public int getPoints(Player player) {
        checkConnection();
        try (PreparedStatement ps = connection.prepareStatement("SELECT player, online_points FROM " + table + " WHERE player = '" + player.getName() + "';"); ResultSet rs = ps.executeQuery();) {
            while(rs.next()){
                if(rs.getString("player").equals(player.getName())){
                    return rs.getInt("online_points");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public List<String> getInfected() {
        checkConnection();
        try (PreparedStatement ps = connection.prepareStatement("SELECT player,infected FROM " + table + " WHERE infected = 1;"); ResultSet rs = ps.executeQuery(); ){
            List<String> players = new ArrayList<>();
            while(rs.next()){
                players.add(rs.getString("player"));
            }
            players.sort(Comparator.comparing( String::toString ));
            return players;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public int getInfectedNumber() {
        checkConnection();
        try (PreparedStatement ps = connection.prepareStatement("SELECT player,infected FROM " + table + " WHERE infected = 1;"); ResultSet rs = ps.executeQuery();){
            int infected = 0;
            while(rs.next()){
                infected++;
            }
            return infected;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    // Useful methods to convert bool to int and the other way around
    private int boolToInt(boolean x){
        return x ? 1 : 0;
    }
    private boolean intToBool(int x){
        return x == 1;
    }
}
