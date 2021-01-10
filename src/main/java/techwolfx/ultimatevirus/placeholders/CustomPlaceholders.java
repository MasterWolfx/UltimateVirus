package techwolfx.ultimatevirus.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import techwolfx.ultimatevirus.utils.PlaceholderUtils;

public class CustomPlaceholders extends PlaceholderExpansion {

    @Override
    public String onRequest(OfflinePlayer player, String identifier) {
        if (identifier.equalsIgnoreCase("isInfected")) {
            return PlaceholderUtils.isInfectedReturnMsg(player.getUniqueId());
        }
        if (identifier.equalsIgnoreCase("infectedTitle")) {
            return PlaceholderUtils.infectedTitleReturnMsg(player.getUniqueId());
        }
        if (identifier.equalsIgnoreCase("totalInfected")) {
            return Integer.toString(PlaceholderUtils.getTotalInfected());
        }
        return null;
    }

    @Override
    public boolean canRegister(){
        return true;
    }

    @Override
    public String getIdentifier() {
        return "ultimatevirus";
    }

    @Override
    public String getAuthor() {
        return "MasterWolfx";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

}
