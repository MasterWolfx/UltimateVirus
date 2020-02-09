package techwolfx.ultimatevirus.commands;

import org.bukkit.entity.Player;

public abstract class SubCommand {

    public abstract String getName();

    public abstract String getDesc();

    public abstract String getSyntax();

    public abstract void perform(Player p, String[] args);

    public void invalidArgs(Player p){
        p.sendMessage("§cInvalid arguments. Usage: %syntax%".replace("%syntax%", getSyntax()));
    }
    public void noPermission(Player p){
        p.sendMessage("§cYou don't have permission to execute this command.");
    }
}
