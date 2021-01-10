package techwolfx.ultimatevirus.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import techwolfx.ultimatevirus.Ultimatevirus;

public abstract class SubCommand {

    public static final Ultimatevirus plugin = Ultimatevirus.getInstance();

    public abstract String getName();

    public abstract String getDesc();

    public abstract String getSyntax();

    public abstract void perform(CommandSender sender, String[] args);

    public void invalidArgs(CommandSender sender){
        sender.sendMessage("§cInvalid arguments. Usage: %syntax%".replace("%syntax%", getSyntax()));
    }
    public void invalidPermission(Player p){
        p.sendMessage("§cYou don't have the permission to execute this command.");
    }
    public void invalidPlayer(CommandSender sender){
        sender.sendMessage("§cCan't find that player.");
    }
}
