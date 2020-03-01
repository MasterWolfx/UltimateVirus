package techwolfx.ultimatevirus.listeners;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import techwolfx.ultimatevirus.Ultimatevirus;
import java.util.List;
import java.util.Random;

public class PlayerEvents implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        if(!Ultimatevirus.getInstance().getRDatabase().isPlayerRegistered(e.getPlayer().getName())){
            Ultimatevirus.getInstance().getRDatabase().setTokens(e.getPlayer(), e.getPlayer().getUniqueId(),false, 0);
        }
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

    @EventHandler
    public void onMobHit(EntityDamageByEntityEvent e){
        Entity damager = e.getDamager();
        Entity damaged = e.getEntity();

        if(damager instanceof Monster && damaged instanceof Player){

            List<String> enabledMobs = Ultimatevirus.getInstance().getConfig().getStringList("MobTypes");
            String customName = Ultimatevirus.getInstance().getConfig().getString("CustomMobName").replace("&", "§").replace("%mob_type%", damager.getName());
            boolean goAhead = false;
            for (String mob : enabledMobs){
                // Check if the mob had the right custom name
                if(damager.getType() == EntityType.fromName(mob.toUpperCase()) && damager.getCustomName() != null){
                    goAhead = true;
                    break;
                    /*if(damager.getCustomName().equals(customName)){
                        goAhead = true;
                        break;
                    }*/
                }
            }
            if(goAhead){
                Player p = (Player) damaged;
                if(p.hasPermission("ultimatevirus.bypass")){
                    return;
                }
                int maskDmg = Ultimatevirus.getInstance().getConfig().getInt("MaskDmgOnInfectedMobHit");
                p.sendMessage(Ultimatevirus.getInstance().getLangMsg("MsgHitByInfectedMob").replace("%mask_dmg%", Integer.toString(maskDmg)));
                Ultimatevirus.getInstance().maskChecks(p, maskDmg);
            }
        }
    }

    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent e){
        boolean mobInfection = Ultimatevirus.getInstance().getConfig().getBoolean("EnableMobInfection");
        if(!mobInfection){
            return;
        }
        if (!(e.getEntity() instanceof Monster)) {
            return;
        }

        // Check if the mob is in a disabled world
        List<String> disabledWorlds = Ultimatevirus.getInstance().getConfig().getStringList("DisabledWorlds");
        if (disabledWorlds.size() != 0) {
            for (String world : disabledWorlds) {
                if (e.getEntity().getWorld().getName().equals(world)) {
                    return;
                }
            }
        }

        // Check if the mob spawned is between the enabled mobs
        List<String> enabledMobs = Ultimatevirus.getInstance().getConfig().getStringList("MobTypes");
        boolean goAhead = false;

        for (String mob : enabledMobs){
            EntityType mobType = EntityType.fromName(mob.toUpperCase());
            if(e.getEntityType() == mobType){
                goAhead = true;
                break;
            }
        }
        if(!goAhead){
            return;
        }

        String customName = Ultimatevirus.getInstance().getConfig().getString("CustomMobName");

        if (e.getEntity().getCustomName() == null) {
            Random rand = new Random();
            int spreadChance = Ultimatevirus.getInstance().getConfig().getInt("MobInfectionSpreadChance");
            int result = rand.nextInt(100);
            if( result <= spreadChance ){
                e.getEntity().setCustomName(customName.replace("&", "§").replace("%mob_type%", e.getEntity().getName()));
            }
        }
    }
}
