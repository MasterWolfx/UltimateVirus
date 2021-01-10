package techwolfx.ultimatevirus.utils;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import techwolfx.ultimatevirus.Ultimatevirus;
import techwolfx.ultimatevirus.files.LanguageFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainProcess {

    private static final Ultimatevirus pl = Ultimatevirus.getInstance();
    private static final ArrayList<String> playersOnline = new ArrayList<>();
    private static boolean debug;

    public static void mainProcess(){
        debug = pl.getConfig().getBoolean("Debug");
        final int minOnlinePlayers = pl.getConfig().getInt("MinOnlinePlayers");

        updateOnlinePlayersList();

        // Check if the minimum amount of players is online
        if (playersOnline.size() < minOnlinePlayers){
            return;
        }

        try {
            if (debug) Bukkit.getConsoleSender().sendMessage("Starting Main Process...");

            // Pick a random player between online players
            Random rand = new Random();
            String pickedPlayerName = playersOnline.get(rand.nextInt(playersOnline.size()));
            Player p = Bukkit.getPlayer(pickedPlayerName);

            // Check if the pickedPlayer is in survival mode
            if(pl.getConfig().getBoolean("InfectOnlyOnSurvivalGamemode") && p.getGameMode() != GameMode.SURVIVAL){
                if (debug) Bukkit.getConsoleSender().sendMessage("This player is not in gamemode survival");
                return;
            }

            // Check if the pickedPlayer is already infected
            if(pl.getRDatabase().isInfected(p.getUniqueId())){
                if (debug) Bukkit.getConsoleSender().sendMessage("Player already infected: " + pickedPlayerName);
                return;
            }

            // Check if the pickedPlayer is in a disabled world
            List<String> disabledWorlds = pl.getConfig().getStringList("DisabledWorlds");
            if (disabledWorlds.size() != 0) {
                for (String world : disabledWorlds) {
                    if (p.getWorld().getName().equals(world)) {
                        return;
                    }
                }
            }

            if(p.isOp() && !pl.getConfig().getBoolean("InfectOpPlayers")){
                if(debug) Bukkit.getConsoleSender().sendMessage("This player is op and he cant be infected (config.yml): " + pickedPlayerName);
                return;
            }

            // Check if the picked player has the bypass permission
            if(!p.isOp() && p.hasPermission("ultimatevirus.bypass")){
                if(debug) Bukkit.getConsoleSender().sendMessage("This player has the bypass permission: " + pickedPlayerName);
                return;
            }

            int infectionProb = pl.getConfig().getInt("InfectionPercentage");

            // Pick a random integer number between 0 to 100
            int result = rand.nextInt(100);
            if (debug) Bukkit.getConsoleSender().sendMessage("RANDOM NUMBER: " + result);

            if ( result <= infectionProb + pl.getRDatabase().getPoints(p.getUniqueId()) + getChanceAddPerNearInfectedPlayer(p) ){
                int maskDmg = pl.getConfig().getInt("MaskDmgOnVirusSave");
                maskChecks(p, maskDmg);
            } else {
                // The player avoided the virus, adding online points to his stats
                int pointsAddition = pl.getConfig().getInt("OnlinePointsAddition");

                if ( pl.getRDatabase().getPoints(p.getUniqueId()) + infectionProb + pointsAddition <= 100){

                    pl.getRDatabase().setPoints(p.getUniqueId(), pl.getRDatabase().getPoints(p.getUniqueId()) + pointsAddition);

                } else if ( pl.getRDatabase().getPoints(p.getUniqueId()) + infectionProb + pointsAddition > 100){

                    pl.getRDatabase().setPoints(p.getUniqueId(), 100 - infectionProb);

                }

                if(debug) Bukkit.getConsoleSender().sendMessage("Virus avoided, skipping item check");
            }
        } catch (Exception ignored){ }
    }

    private static void updateOnlinePlayersList(){
        playersOnline.clear();
        for ( Player p : Bukkit.getOnlinePlayers() ){
            playersOnline.add(p.getName());
        }
    }

    /* Get the amount of infected players near the pickedPlayer */
    private static int getChanceAddPerNearInfectedPlayer(Player target){
        int chanceAddNearInfected = pl.getConfig().getInt("ChanceAdditionWhenNearInfected");
        int distance = pl.getConfig().getInt("SpreadDistanceBetweenPlayers");
        int result = 0;
        for(Player nearPlayer : Bukkit.getOnlinePlayers()){
            // If the healthy player is inside the range of an infected player
            if( nearPlayer.getLocation().distance(target.getLocation()) <= distance && pl.getRDatabase().isInfected(nearPlayer.getUniqueId()) ){
                result += chanceAddNearInfected;
            }
        }
        if (debug) Bukkit.getConsoleSender().sendMessage("Infection chance added to the player per near infected player: " + (result) );

        return result;
    }

    public static void setInfected(Player p){
        if(!pl.getRDatabase().isInfected(p.getUniqueId())){
            p.sendTitle(LanguageFile.getLangMsg("OnInfection.Title"), LanguageFile.getLangMsg("OnInfection.Subtitle"));
            if(pl.getConfig().getBoolean("BroadcastOnPlayerInfection")){
                Bukkit.broadcastMessage(LanguageFile.getLangMsg("BroadcastOnPlayerInfection").replace("%player%", p.getName()));
            }
        }

        int maxHealth = pl.getConfig().getInt("MaximumHealthWhenInfected");
        p.getPlayer().setMaxHealth(maxHealth);

        pl.getRDatabase().setPoints(p.getUniqueId(), 0);
        pl.getRDatabase().setInfected(p.getUniqueId(), true);
    }

    public static void setHealthy(Player p, boolean displayMsgs){
        pl.getRDatabase().setInfected(p.getUniqueId(), false);
        p.setMaxHealth(20);
        for(PotionEffect effect : p.getActivePotionEffects()){
            p.removePotionEffect(effect.getType());
        }
        if(displayMsgs){
            p.sendMessage(LanguageFile.getLangMsg("MsgOnRecover"));
            if(pl.getConfig().getBoolean("BroadcastOnPlayerCure")){
                Bukkit.broadcastMessage(LanguageFile.getLangMsg("BroadcastOnPlayerCure").replace("%player%", p.getName()));
            }
        }
    }

    @SuppressWarnings("deprecation")
    public static void maskChecks(Player p, int maskDmg){
        boolean msgOnMaskHit = pl.getConfig().getBoolean("MsgOnMaskHit");
        int hpStartWarnings = pl.getConfig().getInt("MaskLowHpWarnings");

        Inventory inv = p.getInventory();
        // Look for a mask inside player inventory: if mask is found, return
        for (int i = 0 ; i < inv.getSize() ; i++){

            if (debug) Bukkit.getConsoleSender().sendMessage("Checking item " + i);

            ItemStack item = inv.getItem(i);
            if ( item != null && item.isSimilar(UltimatevirusUtils.getMask()) ) {

                // Checking mask durability: if it is negative, destroy it
                if(item.getDurability() < item.getType().getMaxDurability() - maskDmg){
                    item.setDurability((short)(item.getDurability() + maskDmg));

                    // Mask low HP check
                    if (item.getDurability() >= item.getType().getMaxDurability() - hpStartWarnings){
                        p.sendTitle(LanguageFile.getLangMsg("OnLowMaskHealth.Title"), LanguageFile.getLangMsg("OnLowMaskHealth.Subtitle").replace("%hp%", Short.toString((short)(item.getType().getMaxDurability() - item.getDurability()))) );
                    }

                } else {
                    inv.remove(item);
                    p.sendTitle(LanguageFile.getLangMsg("OnMaskBreak.Title"), LanguageFile.getLangMsg("OnMaskBreak.Subtitle"));
                }

                if(msgOnMaskHit) p.sendMessage(LanguageFile.getLangMsg("MsgOnMaskHit"));

                return;

            }
        }

        setInfected(p);
    }

    public static boolean hasMask(Player p){
        Inventory inv = p.getInventory();
        for(int i = 0 ; i < inv.getSize() ; i++){
            ItemStack item = inv.getItem(i);
            if(item != null) {
                if (item.isSimilar(UltimatevirusUtils.getMask())) {
                    return true;
                }
            }
        }
        return false;
    }

}
