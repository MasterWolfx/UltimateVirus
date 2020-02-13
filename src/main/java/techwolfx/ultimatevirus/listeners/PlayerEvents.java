package techwolfx.ultimatevirus.listeners;

import org.bukkit.*;
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
        if(!Ultimatevirus.getInstance().getRDatabase().isPlayerRegistered(e.getPlayer().getName())){
            Ultimatevirus.getInstance().getRDatabase().setTokens(e.getPlayer(), e.getPlayer().getUniqueId(),false, 0);
        }
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

                if( Ultimatevirus.getInstance().getRDatabase().isInfected(p.getName()) ){
                    Ultimatevirus.getInstance().setHealthy(p);
                } else {
                    p.sendMessage(Ultimatevirus.getInstance().getLangMsg("ErrorMsgDrinkVaxin"));
                    e.setCancelled(true);
                }
            }
        } catch (Exception ignored){ }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e){
        Player p = e.getPlayer();

        if(Ultimatevirus.getInstance().getRDatabase().isInfected(p.getName())){
            //
            // Add potion effects to an infected player
            //
            if(Ultimatevirus.getInstance().getConfig().getBoolean("EnablePotionEffectsWhenInfected")){
                Random rand = new Random();
                float result = rand.nextFloat();
                if(result <= 2){
                    int time = Ultimatevirus.getInstance().getConfig().getInt("PotionEffectsDuration");
                    for (String s: Ultimatevirus.getInstance().getConfig().getStringList("PotionEffectsWhenInfected")) {
                        // Index 0: EffectType - Index 1: Amplifier
                        String[] splittedEffect = s.split("#");

                        PotionEffectType effect;
                        try {
                            effect = PotionEffectType.getByName(splittedEffect[0]);
                        } catch (Exception ex) {
                            Bukkit.getConsoleSender().sendMessage("§c[UltimateVirus] One of your PotionEffectsWhenInfected was not recognized :(");
                            continue;
                        }
                        p.addPotionEffect(new PotionEffect(effect, time*20, Integer.parseInt(splittedEffect[1])-1));
                    }
                }
            }
            //
            // Generate particles when an infected player moves
            //
            if(Ultimatevirus.getInstance().getConfig().getBoolean("ParticlesWhenInfected")){

                if(Bukkit.getBukkitVersion().contains("1.8")){
                    Effect particle = Effect.valueOf(Ultimatevirus.getInstance().getConfig().getString("InfectionParticleType"));
                    p.getWorld().playEffect(p.getLocation(), particle, 50, 5);

                    // Editable player location: new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ())
                } else {
                    Particle particle = Particle.valueOf(Ultimatevirus.getInstance().getConfig().getString("InfectionParticleType"));
                    p.spawnParticle(particle, p.getLocation(), 5);
                }
            }
        }
    }

}
