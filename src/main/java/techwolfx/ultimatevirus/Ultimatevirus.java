package techwolfx.ultimatevirus;


import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;
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
import techwolfx.ultimatevirus.utils.MainProcess;
import techwolfx.ultimatevirus.utils.VersionUtils;
import java.util.Objects;


public final class Ultimatevirus extends JavaPlugin {

    // Ultimatevirus instance
    private static Ultimatevirus instance;
    public static Ultimatevirus getInstance(){
        return instance;
    }

    // SQLite Database
    private Database db;
    public Database getRDatabase() {
        return this.db;
    }

    // Version
    private int version;
    public int getVersion(){
        return version;
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
        version = VersionUtils.getVersionNumber();
        saveDefaultConfig();

        // Setup lang.yml
        Language.langFileSetup();

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
            Bukkit.getConsoleSender().sendMessage("§cCould not find PlaceholderAPI, some commands will not work properly.");
        }

        Bukkit.getConsoleSender().sendMessage("§a[UltimateVirus] Plugin Enabled.");

        BukkitScheduler scheduler = getServer().getScheduler();
        int checkInterval = getConfig().getInt("InfectionSpreadDelay");
        scheduler.scheduleSyncRepeatingTask(this, MainProcess::mainProcess, 0L, checkInterval*20);
    }

    private void maskRecipe(){
        ItemStack maskItem = MaskCMD.getMask();
        ShapedRecipe maskRecipe;

        if(version < 11){
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

        if(version < 11){
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
}
