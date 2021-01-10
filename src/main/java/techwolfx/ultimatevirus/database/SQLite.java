package techwolfx.ultimatevirus.database;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.logging.Level;
import techwolfx.ultimatevirus.Ultimatevirus;

public class SQLite extends Database {

    private final String dbname;
    private final String SQLiteCreateTokensTable;

    public SQLite(Ultimatevirus instance){
        super(instance);
        dbname = super.table;
        SQLiteCreateTokensTable = "CREATE TABLE IF NOT EXISTS "+dbname+" (" +
                "`player` varchar(32) NOT NULL," +
                "`uuid` varchar(32) NOT NULL," +
                "`infected` BOOLEAN NOT NULL CHECK (`infected` IN (0,1))," +
                "`online_points` int(10) NOT NULL," +
                "PRIMARY KEY (`player`)" +
                ");";
    }

    public Connection getSQLConnection() {
        File dataFolder = new File(plugin.getDataFolder(), dbname+".db");
        if (!dataFolder.exists()){
            try {
                dataFolder.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "File write error: "+dbname+".db");
            }
        }
        try {
            if(connection != null && !connection.isClosed()){
                return connection;
            }
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
            return connection;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE,"SQLite exception on initialize", ex);
        } catch (ClassNotFoundException ex) {
            plugin.getLogger().log(Level.SEVERE, "You need the SQLite JBDC library. Google it. Put it in /lib folder.");
        }
        return null;
    }

    public void load() {
        connection = getSQLConnection();
        try {
            Statement s = connection.createStatement();
            s.executeUpdate(SQLiteCreateTokensTable);
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        initialize();
    }
}