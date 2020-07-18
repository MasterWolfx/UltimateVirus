package techwolfx.ultimatevirus.listeners;

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
import techwolfx.ultimatevirus.files.Language;
import java.util.List;
import java.util.Random;

public class MobEvents implements Listener {

    @EventHandler
    public void onMobHit(EntityDamageByEntityEvent e){
        boolean mobInfection = Ultimatevirus.getInstance().getConfig().getBoolean("EnableMobInfection");
        if(!mobInfection){
            return;
        }

        Entity damager = e.getDamager();
        Entity damaged = e.getEntity();

        if(damager instanceof Monster && damaged instanceof Player){

            List<String> enabledMobs = Ultimatevirus.getInstance().getConfig().getStringList("MobTypes");
            String customName = Ultimatevirus.getInstance().getConfig().getString("CustomMobName").replace("&", "§").replace("%mob_type%", capitalizeMobTypeName( damager.getType().getName() ));
            boolean goAhead = false;
            for (String mob : enabledMobs){
                // Check if the mob has the right custom name
                if(damager.getType() == EntityType.fromName(mob.toUpperCase()) && damager.getCustomName() != null){
                    if(damager.getCustomName().equals(customName)){
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
                int maskDmg = Ultimatevirus.getInstance().getConfig().getInt("MaskDmgOnInfectedMobHit");
                // Send hit message only if the player is not infected
                if(Ultimatevirus.getInstance().getConfig().getBoolean("PreventSpamInfectedMobHit")){
                    if(!Ultimatevirus.getInstance().getRDatabase().isInfected(p.getName())){
                        p.sendMessage(Language.getLangMsg("MsgHitByInfectedMob").replace("%mask_dmg%", Integer.toString(maskDmg)));
                    }
                } else {
                    if(MainProcess.hasMask(p)){
                        p.sendMessage(Language.getLangMsg("MsgHitByInfectedMobWithMask").replace("%mask_dmg%", Integer.toString(maskDmg)));
                    } else {
                        p.sendMessage(Language.getLangMsg("MsgHitByInfectedMob"));
                    }
                }

                MainProcess.maskChecks(p, maskDmg);
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

            if ( result <= spreadChance ){
                e.getEntity().setCustomName(customName.replace("&", "§").replace("%mob_type%", capitalizeMobTypeName( e.getEntity().getType().getName() ) ) );
            }

        }
    }

    private String capitalizeMobTypeName(String s){
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
