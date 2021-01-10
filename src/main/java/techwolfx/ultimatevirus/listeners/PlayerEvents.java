package techwolfx.ultimatevirus.listeners;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import techwolfx.ultimatevirus.utils.MainProcess;
import techwolfx.ultimatevirus.Ultimatevirus;
import techwolfx.ultimatevirus.files.LanguageFile;
import techwolfx.ultimatevirus.utils.UltimatevirusUtils;

import java.util.Random;

public class PlayerEvents implements Listener {

    private final Ultimatevirus plugin;

    public PlayerEvents(Ultimatevirus plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        if (!plugin.getRDatabase().isPlayerRegistered(p.getUniqueId())) {
            plugin.getRDatabase().setTokens(p, p.getUniqueId(),false, 0);
        }
    }

    @EventHandler
    public void onDrinkPotion(PlayerItemConsumeEvent e){
        ItemStack item = e.getItem();
        try{
            if(item.isSimilar(UltimatevirusUtils.getVaxin())) {
                Player p = e.getPlayer();

                if( plugin.getRDatabase().isInfected(p.getUniqueId()) ){
                    MainProcess.setHealthy(p, true);
                } else {
                    p.sendMessage(LanguageFile.getLangMsg("ErrorMsgDrinkVaxin"));
                    e.setCancelled(true);
                }
            }
        } catch (Exception ignored){ }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e){

        Player p = e.getEntity();
        if(plugin.getRDatabase().isInfected(p.getUniqueId()) && plugin.getConfig().getBoolean("RemoveVirusOnPlayerDeath")){
            MainProcess.setHealthy(p, false);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e){
        Player p = e.getPlayer();

        if(plugin.getRDatabase().isInfected(p.getUniqueId())){
            //
            // Add potion effects to an infected player
            //
            if(plugin.getConfig().getBoolean("EnablePotionEffectsWhenInfected")){
                Random rand = new Random();
                float result = rand.nextFloat();
                if(result <= 2){
                    int time = plugin.getConfig().getInt("PotionEffectsDuration");
                    for (String s: plugin.getConfig().getStringList("PotionEffectsWhenInfected")) {
                        // Index 0: EffectType - Index 1: Amplifier
                        String[] splittedEffect = s.split("#");

                        PotionEffectType effect;
                        try {
                            effect = PotionEffectType.getByName(splittedEffect[0]);
                        } catch (Exception ex) {
                            Bukkit.getConsoleSender().sendMessage("§c[UltimateVirus] Potion effect called: "+splittedEffect[0]+" can't be found:");
                            Bukkit.getConsoleSender().sendMessage(ex.getMessage());
                            continue;
                        }
                        int prob;
                        if(splittedEffect.length == 2){
                            prob = 100;
                        } else {
                            prob = Integer.parseInt(splittedEffect[2]);
                        }
                        if(rand.nextInt(100) <=  prob){
                            p.addPotionEffect(new PotionEffect(effect, time*20, Integer.parseInt(splittedEffect[1])-1));
                        }
                    }
                }
            }
            //
            // Generate particles when an infected player moves
            //
            if(plugin.getConfig().getBoolean("ParticlesWhenInfected")){

                if(plugin.getVersion() < 9){
                    Effect particle = Effect.valueOf(plugin.getConfig().getString("InfectionParticleType"));
                    p.getWorld().playEffect(p.getLocation(), particle, 50, 15);

                    // Editable player location: new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ())
                } else {
                    Particle particle = Particle.valueOf(plugin.getConfig().getString("InfectionParticleType"));
                    p.spawnParticle(particle, p.getLocation(), 5);
                }
            }
        }
    }
}
