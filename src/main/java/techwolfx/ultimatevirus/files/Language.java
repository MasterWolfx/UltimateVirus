package techwolfx.ultimatevirus.files;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;

public class Language {

    private static File file;
    private static FileConfiguration customFile;

    // Finds or generates the custom config file
    public static void setup(){

        file = new File(Bukkit.getServer().getPluginManager().getPlugin("Ultimatevirus").getDataFolder(), "lang.yml");

        if (!file.exists()){
            try{
                file.createNewFile();
            }catch (IOException ignored){ }
        }
        customFile = YamlConfiguration.loadConfiguration(file);
    }

    public static FileConfiguration get(){
        return customFile;
    }

    public static void save(){
        try{
            customFile.save(file);
        }catch (IOException e){
            System.out.println("Couldn't save file");
        }
    }

    public static void reload(){
        customFile = YamlConfiguration.loadConfiguration(file);
    }

    public static void langFileSetup(){
        setup();
        customFile.addDefault("OnInfection.Title", "&2&nYou got a Virus!");
        customFile.addDefault("OnInfection.Subtitle", "&fFind a Vaxin to restore your Health");

        customFile.addDefault("OnLowMaskHealth.Title", "&c&nWarning");
        customFile.addDefault("OnLowMaskHealth.Subtitle", "&fLow mask durability (%hp% HP)");

        customFile.addDefault("OnMaskBreak.Title", "&c&nWarning");
        customFile.addDefault("OnMaskBreak.Subtitle", "&4Mask broken!");

        customFile.addDefault("InfectedListMsg.Header", "&4&nInfected List &7(%total%)");
        customFile.addDefault("InfectedListMsg.EachLine", "&c&l* &7%player_name%");
        customFile.addDefault("MsgNoInfectedInsideList", "&a&l(!) &aNo player is infected at the moment.");

        customFile.addDefault("MsgOnGiveMask", "&a&l(!) &7You were given an &aAntiVirus Mask&7.");
        customFile.addDefault("MsgOnGiveVaxin", "&a&l(!) &7You were given a &bVaxin&7.");

        customFile.addDefault("MsgOnMaskHit", "&a&l(!) &aYour mask saved you from a virus!");
        customFile.addDefault("MsgOnRecover", "&a&l(!) &aYou recovered from the virus!");
        customFile.addDefault("MsgCheckVirus", "&c&l* &cInfected: &7%ultimatevirus_isInfected%");
        customFile.addDefault("MsgCheckVirusOthers", "&c&l* &cInfected &e(%target%)&c: &7%ultimatevirus_isInfected%");
        customFile.addDefault("MsgHitByInfectedMob", "&c&l(!) &cAn infected mob as hitted you!");
        customFile.addDefault("MsgHitByInfectedMobWithMask", "&c&l(!) &cAn infected mob as hitted you! &e(-%mask_dmg% HP to your mask)");
        customFile.addDefault("MsgOnSetHealth", "&d&l(!) &dSet health of &f%player%&d to &f%health%&d.");
        customFile.addDefault("BroadcastOnPlayerInfection", "&8[&fNEWS&8] &4The health department confirms a new case of the virus. %player% is now infected.");
        customFile.addDefault("BroadcastOnPlayerCure", "&8[&fNEWS&8] &2The health department announces that %player% recovered from the virus.");
        customFile.addDefault("ErrorMsgDrinkVaxin", "&c&l(!) &7You can't drink this, you are not infected!");

        get().options().copyDefaults(true);
        save();
    }

    public static String getLangMsg(String s){
        return ChatColor.translateAlternateColorCodes('&', Language.get().getString(s));
    }
}
