package techwolfx.ultimatevirus.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import techwolfx.ultimatevirus.Ultimatevirus;

import java.util.Random;


public class PlayerEvents implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        Ultimatevirus.getInstance().getPlayersOnline().add(e.getPlayer().getName());
        if(Ultimatevirus.getInstance().getConfig().getBoolean("Debug"))
        Bukkit.getConsoleSender().sendMessage("Current List (Join): " + Ultimatevirus.getInstance().getPlayersOnline());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e){
        Ultimatevirus.getInstance().getPlayersOnline().remove(e.getPlayer().getName());
        if(Ultimatevirus.getInstance().getConfig().getBoolean("Debug"))
        Bukkit.getConsoleSender().sendMessage("Current List (Quit): " + Ultimatevirus.getInstance().getPlayersOnline());
    }

    @EventHandler
    public void onDrinkPotion(PlayerItemConsumeEvent e){
        ItemStack item = e.getItem();
        String vaxinName = Ultimatevirus.getInstance().getConfig().getString("VaxinDisplayName").replace("&", "§");
        try{
            if(item.getType() == Material.POTION && item.getItemMeta().getDisplayName().equals(vaxinName)) {
                Player p = e.getPlayer();

                if( Ultimatevirus.getInstance().checkInfection(p.getName()) ){
                    Ultimatevirus.getInstance().setHealthy(p);
                } else {
                    p.sendMessage("§cYou can't drink this, you are not infected!");
                    e.setCancelled(true);
                }
            }
        } catch (Exception ignored){ }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e){
        Player p = e.getPlayer();

        if(Ultimatevirus.getInstance().getConfig().getBoolean(p.getName())){
            Random rand = new Random();
            float result = rand.nextFloat();
            if(result <= 2){
                p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 10*20, 2));
            }
        }
    }
}
