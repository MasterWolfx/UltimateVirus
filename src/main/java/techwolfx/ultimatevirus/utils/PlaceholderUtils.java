package techwolfx.ultimatevirus.utils;

import org.bukkit.ChatColor;
import techwolfx.ultimatevirus.Ultimatevirus;

import java.util.UUID;

public class PlaceholderUtils {

    private static final Ultimatevirus plugin = Ultimatevirus.getInstance();

    /* Placeholders returns */
    public static String isInfectedReturnMsg(UUID uuid){
        return plugin.getRDatabase().isInfected(uuid) ?
                ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("ultimatevirus_isInfected.ReturnMsgWhenTrue"))
                :
                ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("ultimatevirus_isInfected.ReturnMsgWhenFalse"));
    }

    public static String infectedTitleReturnMsg(UUID uuid){
        return plugin.getRDatabase().isInfected(uuid) ?
                plugin.getConfig().getString("ultimatevirus_infectedTitle.ReturnMsgWhenTrue").replace("&", "§")
                :
                plugin.getConfig().getString("ultimatevirus_infectedTitle.ReturnMsgWhenFalse").replace("&", "§");
    }

    public static int getTotalInfected(){
        return plugin.getRDatabase().getInfectedNumber();
    }

}
