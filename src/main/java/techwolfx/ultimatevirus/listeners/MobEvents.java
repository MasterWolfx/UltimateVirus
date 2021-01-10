package techwolfx.ultimatevirus.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import techwolfx.ultimatevirus.utils.MainProcess;
import techwolfx.ultimatevirus.Ultimatevirus;
import techwolfx.ultimatevirus.files.LanguageFile;
import java.util.List;
import java.util.Random;

public class MobEvents implements Listener {

    private final Ultimatevirus plugin;

    public MobEvents(Ultimatevirus plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    public void onMobHit(EntityDamageByEntityEvent e){
        boolean mobInfection = plugin.getConfig().getBoolean("EnableMobInfection");
        if(!mobInfection){
            return;
        }

        Entity damager = e.getDamager();
        Entity damaged = e.getEntity();

        if(damager instanceof Monster && damaged instanceof Player){

            List<String> enabledMobs = plugin.getConfig().getStringList("MobTypes");
            String customName = ChatColor.translateAlternateColorCodes('&',
                    plugin.getConfig().getString("CustomMobName").replace("%mob_type%", capitalizeMobTypeName( damager.getType().getName() )) );

            boolean goAhead = false;

            for (String mob : enabledMobs){
                // Check if the mob has the right custom name
                if (damager.getType() == EntityType.fromName(mob.toUpperCase()) && damager.getCustomName() != null){
                    if (damager.getCustomName().equals(customName)){
                        goAhead = true;
                        break;
                    }
                }
            }
            // The mob has the right custom name:
            if(goAhead){
                Player p = (Player) damaged;
                if(p.hasPermission("ultimatevirus.bypass")){
                    return;
                }
                int maskDmg = plugin.getConfig().getInt("MaskDmgOnInfectedMobHit");
                // Send hit message only if the player is not infected
                if(plugin.getConfig().getBoolean("PreventSpamInfectedMobHit")){
                    if(!plugin.getRDatabase().isInfected(p.getUniqueId())){
                        p.sendMessage(LanguageFile.getLangMsg("MsgHitByInfectedMob").replace("%mask_dmg%", Integer.toString(maskDmg)));
                    }
                } else {
                    if(MainProcess.hasMask(p)){
                        p.sendMessage(LanguageFile.getLangMsg("MsgHitByInfectedMobWithMask").replace("%mask_dmg%", Integer.toString(maskDmg)));
                    } else {
                        p.sendMessage(LanguageFile.getLangMsg("MsgHitByInfectedMob"));
                    }
                }

                MainProcess.maskChecks(p, maskDmg);
            }
        }
    }

    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent e){
        boolean mobInfection = plugin.getConfig().getBoolean("EnableMobInfection");
        if(!mobInfection){
            return;
        }
        if (!(e.getEntity() instanceof Monster)) {
            return;
        }

        // Check if the mob is in a disabled world
        List<String> disabledWorlds = plugin.getConfig().getStringList("DisabledWorlds");
        if (disabledWorlds.size() != 0) {
            for (String world : disabledWorlds) {
                if (e.getEntity().getWorld().getName().equals(world)) {
                    return;
                }
            }
        }

        // Check if the mob spawned is between the enabled mobs
        List<String> enabledMobs = plugin.getConfig().getStringList("MobTypes");
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

        String customName = plugin.getConfig().getString("CustomMobName");

        if (e.getEntity().getCustomName() == null) {

            Random rand = new Random();
            int spreadChance = plugin.getConfig().getInt("MobInfectionSpreadChance");
            int result = rand.nextInt(100);

            if ( result <= spreadChance ){
                e.getEntity().setCustomName(customName.replace("&", "§").replace("%mob_type%", capitalizeMobTypeName( e.getEntity().getType().getName() ) ) );
            }

        }
    }

    private String capitalizeMobTypeName(String s){
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
