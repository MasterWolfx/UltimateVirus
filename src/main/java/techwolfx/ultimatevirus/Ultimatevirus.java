package techwolfx.ultimatevirus;


import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitScheduler;
import techwolfx.ultimatevirus.commands.CmdTabCompletion;
import techwolfx.ultimatevirus.commands.CommandManager;
import techwolfx.ultimatevirus.commands.subcommands.MaskCMD;
import techwolfx.ultimatevirus.commands.subcommands.VaxinCMD;
import techwolfx.ultimatevirus.database.Database;
import techwolfx.ultimatevirus.database.SQLite;
import techwolfx.ultimatevirus.files.Language;
import techwolfx.ultimatevirus.listeners.MobEvents;
import techwolfx.ultimatevirus.listeners.PlayerEvents;
import techwolfx.ultimatevirus.placeholders.CustomPlaceholders;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;


public final class Ultimatevirus extends JavaPlugin {

    // Players Online List
    private ArrayList<String> playersOnline = new ArrayList<>();

    // Ultimatevirs instance
    private static Ultimatevirus instance;
    public static Ultimatevirus getInstance(){
        return instance;
    }

    // SQLite Database
    private Database db;
    public Database getRDatabase() {
        return this.db;
    }

    // lang.yml
    private void langFileSetup(){
        Language.setup();
        Language.get().addDefault("TitleOnInfection", "&2&nYou got a Virus!");
        Language.get().addDefault("SubtitleOnInfection", "&fFind a Vaxin to restore your Health");

        Language.get().addDefault("TitleOnLowMaskHealth", "&c&nWarning");
        Language.get().addDefault("SubtitleOnLowMaskHealth", "&fLow mask durability (%hp% HP)");

        Language.get().addDefault("TitleOnMaskBreak", "&c&nWarning");
        Language.get().addDefault("SubtitleOnMaskBreak", "&4Mask broken!");

        Language.get().addDefault("MsgOnGiveMask", "&a&l(!) &7You were given an &aAntiVirus Mask&7.");
        Language.get().addDefault("MsgOnGiveVaxin", "&a&l(!) &7You were given a &bVaxin&7.");

        Language.get().addDefault("MsgOnMaskHit", "&a&l(!) &aYour mask saved you from a virus!");
        Language.get().addDefault("MsgOnRecover", "&a&l(!) &aYou recovered from the virus!");

        Language.get().addDefault("MsgCheckVirus", "&c&l* &cInfected: &7%ultimatevirus_isInfected%");
        Language.get().addDefault("MsgCheckVirusOthers", "&c&l* &cInfected &e(%target%)&c: &7%ultimatevirus_isInfected%");
        Language.get().addDefault("MsgHitByInfectedMob", "&c&l(!) &cAn infected mob as hitted you! &e(-%mask_dmg% HP to your mask)");
        Language.get().addDefault("ErrorMsgDrinkVaxin", "&c&l(!) &7You can't drink this, you are not infected!");
        Language.get().addDefault("BroadcastOnPlayerInfection", "&8[&fNEWS&8] &4The health department confirms a new case of the virus. %player% is now infected.");
        Language.get().addDefault("BroadcastOnPlayerCure", "&8[&fNEWS&8] &2The health department announces that %player% recovered from the virus.");
        Language.get().addDefault("MsgNoInfectedInsideList", "&a&l(!) &aNo player is infected at the moment.");
        Language.get().addDefault("MsgOnSetHealth", "&d&l(!) &dSet health of &f%player%&d to &f%health%&d.");

        Language.get().options().copyDefaults(true);
        Language.save();
    }
    public String getLangMsg(String s){
        return Language.get().getString(s).replace("&", "§");
    }

    // Placeholders
    public CustomPlaceholders myPlaceholder = null;

    // Enable the plugin
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new MobEvents(), this);
        getServer().getPluginManager().registerEvents(new PlayerEvents(), this);
        Objects.requireNonNull(getCommand("virus")).setExecutor(new CommandManager());
        Objects.requireNonNull(getCommand("virus")).setTabCompleter(new CmdTabCompletion());
        instance = this;
        saveDefaultConfig();

        // Creating lang.yml
        langFileSetup();

        // Enabling database
        this.db = new SQLite(this);
        this.db.load();

        // Enabling custom recipes
        if(getConfig().getBoolean("EnableMaskRecipe"))
            maskRecipe();
        if(getConfig().getBoolean("EnableVaxinRecipe"))
            vaxinRecipe();

        // Check if PlaceholderApi is enabled, and hook it
        if(Ultimatevirus.getInstance().getServer().getPluginManager().getPlugin("PlaceholderAPI") != null){
            Bukkit.getConsoleSender().sendMessage("§a[UltimateVirus] Hooked PlaceholderAPI.");
            myPlaceholder = new CustomPlaceholders(this);
        } else {
            //throw new RuntimeException("§cCould not find PlaceholderAPI, some commands will not work properly.");
            Bukkit.getConsoleSender().sendMessage("§cCould not find PlaceholderAPI, some commands will not work properly.");
        }

        // Enabling database
        this.db = new SQLite(this);
        this.db.load();

        Bukkit.getConsoleSender().sendMessage("§a[UltimateVirus] Plugin Enabled.");

        BukkitScheduler scheduler = getServer().getScheduler();
        int checkInterval = getConfig().getInt("InfectionSpreadDelay");

        scheduler.scheduleSyncRepeatingTask(this, this::mainProcess, 0L, checkInterval*20);
    }

    boolean debug = getConfig().getBoolean("Debug");

    private void mainProcess(){
        int minOnlinePlayers = getConfig().getInt("MinOnlinePlayers");

        updateOnlinePlayersList();
        // Check if the minimum amount of players is online
        if(playersOnline.size() < minOnlinePlayers){
            return;
        }

        try {
            if (debug)
                Bukkit.getConsoleSender().sendMessage("Starting Main Process...");

            // Pick a random player between online players
            Random rand = new Random();
            String pickedPlayerName = playersOnline.get(rand.nextInt(playersOnline.size()));
            Player p = Bukkit.getPlayer(pickedPlayerName);

            // Check if the pickedPlayer is in survival mode
            if(getConfig().getBoolean("InfectOnlyOnSurvivalGamemode")){
                if(p.getGameMode() != GameMode.SURVIVAL){
                    if(debug)
                        Bukkit.getConsoleSender().sendMessage("This player is not in gamemode survival");
                    return;
                }
            }

            // Check if the pickedPlayer is already infected
            if(getRDatabase().isInfected(pickedPlayerName)){
                if(debug)
                    Bukkit.getConsoleSender().sendMessage("Player already infected: " + pickedPlayerName);
                return;
            }

            // Check if the pickedPlayer is in a disabled world
            List<String> disabledWorlds = getConfig().getStringList("DisabledWorlds");
            if (disabledWorlds.size() != 0) {
                for (String world : disabledWorlds) {
                    if (p.getWorld().getName().equals(world)) {
                        return;
                    }
                }
            }
            if(p.isOp() && !getConfig().getBoolean("InfectOpPlayers")){
                if(debug)
                    Bukkit.getConsoleSender().sendMessage("This player is op and he cant be infected (config.yml): " + pickedPlayerName);
                return;
            }
            // Check if the picked player has the bypass permission
            if(!p.isOp() && p.hasPermission("ultimatevirus.bypass")){
                if(debug)
                    Bukkit.getConsoleSender().sendMessage("This player has the bypass permission: " + pickedPlayerName);
                return;
            }

            int infectionProb = getConfig().getInt("InfectionPercentage");

            // Pick a random integer number between 0 to 100
            int result = rand.nextInt(100);
            if(debug)
                Bukkit.getConsoleSender().sendMessage("RANDOM NUMBER: " + result);

            if(result <= infectionProb + getRDatabase().getPoints(p) + getChanceAddPerNearInfectedPlayer(p)){
                int maskDmg = getConfig().getInt("MaskDmgOnVirusSave");
                maskChecks(p, maskDmg);
            } else {
                // The player avoided the virus, adding online points to his stats
                int pointsAddition = getConfig().getInt("OnlinePointsAddition");
                if(getRDatabase().getPoints(p) + infectionProb + pointsAddition <= 100){
                    getRDatabase().setPoints(p, getRDatabase().getPoints(p) + pointsAddition);
                } else if(getRDatabase().getPoints(p) + infectionProb + pointsAddition > 100){
                    getRDatabase().setPoints(p, 100 - infectionProb);
                }

                if(debug)
                    Bukkit.getConsoleSender().sendMessage("Virus avoided, skipping item check");
            }
        } catch (Exception ignored){ }
    }

    private void updateOnlinePlayersList(){
        playersOnline.clear();
        for( Player p : Bukkit.getOnlinePlayers() ){
            playersOnline.add(p.getName());
        }
    }

    // Get the amount of infected players near the pickedPlayer
    private int getChanceAddPerNearInfectedPlayer(Player target){
        int chanceAddNearInfected = getConfig().getInt("ChanceAdditionWhenNearInfected");
        int distance = getConfig().getInt("SpreadDistanceBetweenPlayers");
        int result = 0;
        for(Player nearPlayer : Bukkit.getOnlinePlayers()){
            // If the healthy player is inside the range of an infected player
            if(nearPlayer.getLocation().distance(target.getLocation()) <= distance){
                result += chanceAddNearInfected;
            }
        }
        if(debug){
            Bukkit.getConsoleSender().sendMessage("Infection chance added to the player per near infected player: " + (result-chanceAddNearInfected) );
        }
        return result-chanceAddNearInfected;
    }

    private void maskRecipe(){
        ItemStack maskItem = MaskCMD.getMask();
        ShapedRecipe maskRecipe;

        if(getServer().getVersion().contains("1.8") || getServer().getVersion().contains("1.9") || getServer().getVersion().contains("1.10")){
            maskRecipe = new ShapedRecipe(maskItem);
        } else {
            NamespacedKey key = new NamespacedKey(this, "virus_mask");
            maskRecipe = new ShapedRecipe(key, maskItem);
        }

        maskRecipe.shape("***","%%%","LLL");

        maskRecipe.setIngredient('*', Material.STRING);
        maskRecipe.setIngredient('%', Material.PAPER);
        maskRecipe.setIngredient('L', Material.LEATHER);

        getServer().addRecipe(maskRecipe);
        Bukkit.getConsoleSender().sendMessage("§a[UltimateVirus] Enabled custom Mask recipe.");
    }
    private void vaxinRecipe(){
        ItemStack vaxinItem = VaxinCMD.getVaxin();
        ShapedRecipe vaxinRecipe;

        if(getServer().getVersion().contains("1.8") || getServer().getVersion().contains("1.9") || getServer().getVersion().contains("1.10")){
            vaxinRecipe = new ShapedRecipe(vaxinItem);
        } else {
            NamespacedKey key = new NamespacedKey(this, "virus_vaxin");
            vaxinRecipe = new ShapedRecipe(key, vaxinItem);
        }

        vaxinRecipe.shape("ESW","SPS","RSB");

        vaxinRecipe.setIngredient('P', Material.POTION);
        vaxinRecipe.setIngredient('E', Material.FERMENTED_SPIDER_EYE);
        vaxinRecipe.setIngredient('S', Material.SUGAR);
        vaxinRecipe.setIngredient('B', Material.BROWN_MUSHROOM);
        vaxinRecipe.setIngredient('R', Material.RED_MUSHROOM);
        vaxinRecipe.setIngredient('W', Material.EGG);

        getServer().addRecipe(vaxinRecipe);
        Bukkit.getConsoleSender().sendMessage("§a[UltimateVirus] Enabled custom Vaxin recipe.");
    }

    public void setInfected(Player p){
        getRDatabase().setPoints(p, 0);
        getRDatabase().setInfected(p, true);
        p.sendTitle(getLangMsg("TitleOnInfection"), getLangMsg("SubtitleOnInfection"));
        int maxHealth = getConfig().getInt("MaximumHealthWhenInfected");
        p.getPlayer().setMaxHealth(maxHealth);
        if(getConfig().getBoolean("BroadcastOnPlayerInfection")){
            Bukkit.broadcastMessage(getLangMsg("BroadcastOnPlayerInfection").replace("%player%", p.getName()));
        }
    }
    public void setHealthy(Player p){
        getRDatabase().setInfected(p, false);
        p.setMaxHealth(20);
        p.sendMessage(getLangMsg("MsgOnRecover"));
        for(PotionEffect effect : p.getActivePotionEffects()){
            p.removePotionEffect(effect.getType());
        }
        if(getConfig().getBoolean("BroadcastOnPlayerCure")){
            Bukkit.broadcastMessage(getLangMsg("BroadcastOnPlayerCure").replace("%player%", p.getName()));
        }
    }

    public String isInfectedReturnMsg(String pName){
        return getRDatabase().isInfected(pName) ? getConfig().getString("ultimatevirus_isInfected.ReturnMsgWhenTrue").replace("&", "§") : getConfig().getString("ultimatevirus_isInfected.ReturnMsgWhenFalse").replace("&", "§");
    }
    public String infectedTitleReturnMsg(String pName){
        return getRDatabase().isInfected(pName) ? getConfig().getString("ultimatevirus_infectedTitle.ReturnMsgWhenTrue").replace("&", "§") : getConfig().getString("ultimatevirus_infectedTitle.ReturnMsgWhenFalse").replace("&", "§");
    }

    public void maskChecks(Player p, int maskDmg){
        boolean msgOnMaskHit = getConfig().getBoolean("MsgOnMaskHit");
        int hpStartWarnings = getConfig().getInt("MaskLowHpWarnings");

        Inventory inv = p.getInventory();
        // Look for a mask inside player inventory: if mask is found, return
        for(int i = 0 ; i < inv.getSize() ; i++){
            if(debug)
                Bukkit.getConsoleSender().sendMessage("Checking item " + i);
            ItemStack item = inv.getItem(i);
            if(item != null){
                if(item.isSimilar(MaskCMD.getMask())){
                    // Checking mask durability: if it is negative, destroy it
                    if(item.getDurability() < item.getType().getMaxDurability()-maskDmg){
                        item.setDurability((short)(item.getDurability()+maskDmg));
                        if(item.getDurability() >= item.getType().getMaxDurability() - hpStartWarnings){
                            p.sendTitle(getLangMsg("TitleOnLowMaskHealth"), getLangMsg("SubtitleOnLowMaskHealth").replace("%hp%", Short.toString((short)(item.getType().getMaxDurability() - item.getDurability()))) );
                        }
                    } else {
                        inv.remove(item);
                        p.sendTitle(getLangMsg("TitleOnMaskBreak"), getLangMsg("SubtitleOnMaskBreak"));
                    }
                    if(msgOnMaskHit){
                        p.sendMessage(getLangMsg("MsgOnMaskHit"));
                    }
                    return;
                }
            }
        }
        setInfected(p);
    }
}
