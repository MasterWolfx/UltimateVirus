package techwolfx.ultimatevirus.database;

import techwolfx.ultimatevirus.Ultimatevirus;
import java.util.logging.Level;

public class Error {
    public static void execute(Ultimatevirus plugin, Exception ex){
        plugin.getLogger().log(Level.SEVERE, "Couldn't execute SQL statement: ", ex);
    }
    public static void close(Ultimatevirus plugin, Exception ex){
        plugin.getLogger().log(Level.SEVERE, "Failed to close SQL connection: ", ex);
    }
}