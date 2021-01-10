package techwolfx.ultimatevirus;

import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import techwolfx.ultimatevirus.commands.CmdTabCompletion;
import techwolfx.ultimatevirus.commands.CommandManager;
import techwolfx.ultimatevirus.database.Database;
import techwolfx.ultimatevirus.database.SQLite;
import techwolfx.ultimatevirus.files.LanguageFile;
import techwolfx.ultimatevirus.listeners.MobEvents;
import techwolfx.ultimatevirus.listeners.PlayerEvents;
import techwolfx.ultimatevirus.placeholders.CustomPlaceholders;
import techwolfx.ultimatevirus.utils.MainProcess;
import techwolfx.ultimatevirus.utils.UltimatevirusUtils;
import techwolfx.ultimatevirus.utils.VersionUtils;
import java.util.List;
import java.util.Objects;

public final class Ultimatevirus extends JavaPlugin {

    // UltimateVirus instance
    private static Ultimatevirus instance;
    public static Ultimatevirus getInstance(){
        return instance;
    }

    // SQLite Database
    private Database db;
    public Database getRDatabase() {
        return this.db;
    }

    // Server version
    private int version;
    public int getVersion(){
        return version;
    }

    // Placeholders
    private boolean placeholdersEnabled;
    public boolean arePalceholdersEnabled() {
        return placeholdersEnabled;
    }

    // Enable the plugin
    @Override
    public void onEnable() {
        instance = this;
        version = VersionUtils.getVersionNumber();

        // Setup config.yml
        saveDefaultConfig();

        // Setup lang.yml
        LanguageFile.langFileSetup();

        // Enabling database
        this.db = new SQLite(this);
        this.db.load();

        // Enabling custom recipes
        if (getConfig().getBoolean("EnableMaskRecipe"))
            loadMaskCrafting();
        if (getConfig().getBoolean("EnableVaxinRecipe"))
            loadVaxinCrafting();

        // Check if PlaceholderAPI is enabled, and hook it
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null){
            Bukkit.getConsoleSender().sendMessage("§a[UltimateVirus] Hooked PlaceholderAPI.");
            new CustomPlaceholders().register();
            this.placeholdersEnabled = true;
        } else {
            Bukkit.getConsoleSender().sendMessage("§cCould not find PlaceholderAPI, some commands will not work properly.");
            this.placeholdersEnabled = false;
        }

        // Register commands and events
        getServer().getPluginManager().registerEvents(new MobEvents(this), this);
        getServer().getPluginManager().registerEvents(new PlayerEvents(this), this);
        Objects.requireNonNull(getCommand("virus")).setExecutor(new CommandManager());
        Objects.requireNonNull(getCommand("virus")).setTabCompleter(new CmdTabCompletion());

        Bukkit.getConsoleSender().sendMessage("§a[UltimateVirus] Plugin Enabled.");

        int checkInterval = getConfig().getInt("InfectionSpreadDelay");
        getServer().getScheduler().scheduleSyncRepeatingTask(this, MainProcess::mainProcess, 0L, checkInterval*20);
    }

    private void loadMaskCrafting(){
        List<String> pattern = getConfig().getStringList("custom-craftings.mask.pattern");
        List<String> items = getConfig().getStringList("custom-craftings.mask.ingredients");

        if (pattern.size() != 3) {
            Bukkit.getConsoleSender().sendMessage("§c[UltimateVirus] Mask crafting pattern is incorrect, ignoring it.");
            return;
        }

        ItemStack maskItem = UltimatevirusUtils.getMask();
        ShapedRecipe maskRecipe;

        if (version < 11) {
            maskRecipe = new ShapedRecipe(maskItem);
        } else {
            NamespacedKey key = new NamespacedKey(this, "virusMask");
            maskRecipe = new ShapedRecipe(key, maskItem);
        }

        maskRecipe.shape(pattern.get(0),pattern.get(1),pattern.get(2));

        try {
            for(String item : items){
                String[] splittedItem = item.split(":");
                char key = splittedItem[0].charAt(0);
                if(key == 'X'){
                    maskRecipe.setIngredient('X', Material.AIR);
                } else {
                    maskRecipe.setIngredient(key, Material.getMaterial(splittedItem[1]), Integer.parseInt(splittedItem[2]));
                }
            }
        } catch (Exception ex){
            Bukkit.getLogger().warning("An error occurred while enabling custom Mask crafting: ");
            ex.printStackTrace();
            return;
        }

        getServer().addRecipe(maskRecipe);
        Bukkit.getConsoleSender().sendMessage("§a[UltimateVirus] Enabled custom Mask crafting.");
    }

    private void loadVaxinCrafting(){
        List<String> pattern = getConfig().getStringList("custom-craftings.vaxin.pattern");
        List<String> items = getConfig().getStringList("custom-craftings.vaxin.ingredients");

        if(pattern.size() != 3){
            Bukkit.getConsoleSender().sendMessage("§c[UltimateVirus] Vaxin crafting pattern is incorrect, ignoring it.");
            return;
        }

        ItemStack vaxinItem = UltimatevirusUtils.getVaxin();
        ShapedRecipe vaxinRecipe;

        if(version < 11){
            vaxinRecipe = new ShapedRecipe(vaxinItem);
        } else {
            NamespacedKey key = new NamespacedKey(this, "virusVaxin");
            vaxinRecipe = new ShapedRecipe(key, vaxinItem);
        }

        vaxinRecipe.shape(pattern.get(0),pattern.get(1),pattern.get(2));

        try{
            for(String item : items){
                String[] splittedItem = item.split(":");
                char key = splittedItem[0].charAt(0);
                if(key == 'X'){
                    vaxinRecipe.setIngredient('X', Material.AIR);
                } else {
                    vaxinRecipe.setIngredient(key, Material.getMaterial(splittedItem[1]), Integer.parseInt(splittedItem[2]));
                }
            }
        } catch (Exception ex){
            Bukkit.getLogger().warning("An error occurred while enabling custom Vaxin crafting: ");
            ex.printStackTrace();
            return;
        }

        getServer().addRecipe(vaxinRecipe);
        Bukkit.getConsoleSender().sendMessage("§a[UltimateVirus] Enabled custom Vaxin crafting.");
    }

}
