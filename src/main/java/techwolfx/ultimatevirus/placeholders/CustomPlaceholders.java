package techwolfx.ultimatevirus.placeholders;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderHook;
import org.bukkit.entity.Player;
import techwolfx.ultimatevirus.Ultimatevirus;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Listener;
import techwolfx.ultimatevirus.utils.PlaceholderUtils;

public class CustomPlaceholders implements Listener {

    public CustomPlaceholders(Ultimatevirus plugin){
        registerPlaceholders();
        plugin.getServer().getPluginManager().registerEvents(this,plugin);
    }

    private void registerPlaceholders() {
        PlaceholderAPI.registerPlaceholderHook("ultimatevirus", new PlaceholderHook() {

            @Override
            public String onRequest(OfflinePlayer p, String params) {
                if(p == null){
                    return null;
                }
                if(params.equalsIgnoreCase("isInfected")){
                    return PlaceholderUtils.isInfectedReturnMsg(p.getName());
                }
                if(params.equalsIgnoreCase("infectedTitle")){
                    return PlaceholderUtils.infectedTitleReturnMsg(p.getName());
                }
                if(params.equalsIgnoreCase("totalInfected")){
                    return Integer.toString(PlaceholderUtils.getTotalInfected());
                }
                return null;
            }

            @Override
            public String onPlaceholderRequest(Player p, String params) {
                if(p == null){
                    return null;
                }
                if(params.equalsIgnoreCase("isInfected")){
                    return PlaceholderUtils.isInfectedReturnMsg(p.getName());
                }
                if(params.equalsIgnoreCase("infectedTitle")){
                    return PlaceholderUtils.infectedTitleReturnMsg(p.getName());
                }
                if(params.equalsIgnoreCase("totalInfected")){
                    return Integer.toString(PlaceholderUtils.getTotalInfected());
                }
                return null;
            }
        });
    }
}
