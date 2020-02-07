package techwolfx.ultimatevirus;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import techwolfx.ultimatevirus.commands.CheckInfectionCMD;
import techwolfx.ultimatevirus.commands.MaskCMD;
import techwolfx.ultimatevirus.commands.ReloadCMD;
import techwolfx.ultimatevirus.commands.VaxinCMD;
import techwolfx.ultimatevirus.database.Database;
import techwolfx.ultimatevirus.database.SQLite;
import techwolfx.ultimatevirus.files.Language;
import techwolfx.ultimatevirus.listeners.PlayerEvents;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public final class Ultimatevirus extends JavaPlugin {

    private ArrayList<String> playersOnline = new ArrayList<>();
    private static Ultimatevirus instance;
    private Database db;

    public ArrayList<String> getPlayersOnline(){
        return playersOnline;
    }
    public static Ultimatevirus getInstance(){
        return instance;
    }
    public Database getRDatabase() {
        return this.db;
    }

    private void langFileSetup(){
        Language.setup();
        Language.get().addDefault("TitleOnInfection", "&2&nYou got a Virus!");
        Language.get().addDefault("SubtitleOnInfection", "&fFind a Vaxin to restore your Health");
        Language.get().addDefault("TitleOnLowMaskHealth", "&c&nWarning");
        Language.get().addDefault("SubtitleOnLowMaskHealth", "&fLow mask durability (%hp% HP)");
        Language.get().addDefault("TitleOnMaskBreak", "&c&nWarning");
        Language.get().addDefault("SubtitleOnMaskBreak", "&4Mask broken!");
        Language.get().addDefault("MsgOnRecover", "&aYou recovered yourself from the virus!");
        Language.get().addDefault("MsgOnMaskHit", "&a&l(!) &aYour mask saved you from a virus!");
        Language.get().addDefault("MsgOn", "&a&l(!) &aYour mask saved you from a virus!");
        Language.get().options().copyDefaults(true);
        Language.save();
    }

    public String getLangMsg(String s){
        return Language.get().getString(s).replace("&", "§");
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new PlayerEvents(), this);
        Objects.requireNonNull(getCommand("mask")).setExecutor(new MaskCMD());
        Objects.requireNonNull(getCommand("vaxin")).setExecutor(new VaxinCMD());
        Objects.requireNonNull(getCommand("virusreload")).setExecutor(new ReloadCMD());
        Objects.requireNonNull(getCommand("checkvirus")).setExecutor(new CheckInfectionCMD());
        instance = this;
        saveDefaultConfig();

        // lang.yml Setup
        langFileSetup();

        this.db = new SQLite(this);
        this.db.load();
        Bukkit.getConsoleSender().sendMessage("§a[UltimateVirus] Plugin Enabled.");

        BukkitScheduler scheduler = getServer().getScheduler();
        int checkInterval = getConfig().getInt("InfectionSpreadDelay");

        scheduler.scheduleSyncRepeatingTask(this, this::mainProcess, 0L, checkInterval *20);

    }

    public void setInfected(Player p){
        getRDatabase().setPoints(p, 0);
        getRDatabase().setInfected(p, true);
        p.sendTitle(getLangMsg("TitleOnInfection"), getLangMsg("SubtitleOnInfection"));
        p.getPlayer().setMaxHealth(2);
        for(int i = 0 ; i < 5 ;i++) {
            p.getWorld().playEffect(new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY() + 2, p.getLocation().getZ()), Effect.MAGIC_CRIT, 50, 5);
        }
    }

    public void setHealthy(Player p){
        getRDatabase().setInfected(p, false);
        p.sendMessage(getLangMsg("MsgOnRecover"));
        p.removePotionEffect(PotionEffectType.CONFUSION);
        p.setMaxHealth(20);
        for(int i = 0 ; i < 5 ; i++){
            p.getWorld().playEffect(new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY()+2, p.getLocation().getZ()), Effect.HAPPY_VILLAGER, 50, 5);
        }
    }

    private void mainProcess(){
        int minOnlinePlayers = getConfig().getInt("MinOnlinePlayers");
        boolean debug = getConfig().getBoolean("Debug");
        if(getPlayersOnline().size() < minOnlinePlayers){
            return;
        }
        try{
            if(debug)
                Bukkit.getConsoleSender().sendMessage("Starting Main Process...");

            Random rand = new Random();
            // Pick a random player between online players
            String pickedPlayerName = playersOnline.get(rand.nextInt(playersOnline.size()));

            // Check if the picked player has the bypass permission
            if(Bukkit.getPlayer(pickedPlayerName).hasPermission("ultimatevirus.bypass")){
                if(debug)
                    Bukkit.getConsoleSender().sendMessage("This player is op: " + pickedPlayerName);
                return;
            }
            if(getRDatabase().isInfected(pickedPlayerName)){
                if(debug)
                    Bukkit.getConsoleSender().sendMessage("Player already infected: " + pickedPlayerName);
                return;
            }

            Player p = Bukkit.getPlayer(pickedPlayerName);
            assert p != null;
            Inventory inv = p.getInventory();
            String maskName = getConfig().getString("MaskDisplayName");
            int infectionProb = getConfig().getInt("InfectionPercentage");
            int result = rand.nextInt(100);
            boolean msgOnMaskHit = getConfig().getBoolean("MsgOnMaskHit");
            boolean canInfectPlayer = true;
            if(debug)
                Bukkit.getConsoleSender().sendMessage("RANDOM NUMBER: " + result);

            if(result <= infectionProb + getRDatabase().getPoints(p)){
                // Look for a mask inside player inventory
                for(int i = 0 ; i < inv.getSize() ; i++){
                    if(debug)
                        Bukkit.getConsoleSender().sendMessage("Checking item " + i);
                    ItemStack item = inv.getItem(i);
                    if(item != null){
                        // If found mask canInfectPlayer = false
                        if(item.getType() == Material.LEATHER_HELMET && item.getItemMeta().getDisplayName().equals(maskName.replace("&", "§"))){
                            if(item.getDurability() < item.getType().getMaxDurability()-1){
                                item.setDurability((short)(item.getDurability()+ 1));
                                if(item.getDurability() > item.getType().getMaxDurability()-5){
                                    p.sendTitle(getLangMsg("TitleOnLowMaskHealth"), getLangMsg("SubtitleOnLowMaskHealth").replace("%hp%", Short.toString((short)(item.getType().getMaxDurability() - item.getDurability()))) );
                                }
                            } else {
                                inv.remove(item);
                                p.sendTitle(getLangMsg("TitleOnMaskBreak"), getLangMsg("SubtitleOnMaskBreak"));
                            }
                            if(msgOnMaskHit){
                                p.sendMessage(getLangMsg("MsgOnMaskHit"));
                            }
                            canInfectPlayer = false;
                            break;
                        }
                    }
                }
                if(canInfectPlayer){
                    setInfected(p);
                }
            } else {
                int pointsAddition = getConfig().getInt("OnlinePointsAddition");
                if(getRDatabase().getPoints(p) + infectionProb + pointsAddition < 100){
                    getRDatabase().setPoints(p, getRDatabase().getPoints(p) + pointsAddition);
                }

                if(debug)
                    Bukkit.getConsoleSender().sendMessage("Virus avoided, skipping item check");
            }
        } catch (Exception ignored){ }
    }
}
