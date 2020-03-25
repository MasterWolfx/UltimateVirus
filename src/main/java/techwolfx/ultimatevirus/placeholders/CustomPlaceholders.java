package techwolfx.ultimatevirus.placeholders;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderHook;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import techwolfx.ultimatevirus.Ultimatevirus;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Listener;

public class CustomPlaceholders implements Listener {

    private Ultimatevirus plugin;

    public CustomPlaceholders(Ultimatevirus plugin){
        this.plugin = plugin;
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
                    return plugin.isInfectedReturnMsg(p.getName());
                }
                if(params.equalsIgnoreCase("infectedTitle")){
                    return plugin.infectedTitleReturnMsg(p.getName());
                }
                return null;
            }

            @Override
            public String onPlaceholderRequest(Player p, String params) {
                if(p == null){
                    return null;
                }
                if(params.equalsIgnoreCase("isInfected")){
                    return plugin.isInfectedReturnMsg(p.getName());
                }
                if(params.equalsIgnoreCase("infectedTitle")){
                    return plugin.infectedTitleReturnMsg(p.getName());
                }
                return null;
            }
        });
    }
}
