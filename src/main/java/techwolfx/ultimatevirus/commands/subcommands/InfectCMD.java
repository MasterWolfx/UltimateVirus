package techwolfx.ultimatevirus.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import techwolfx.ultimatevirus.Ultimatevirus;
import techwolfx.ultimatevirus.commands.SubCommand;


public class InfectCMD extends SubCommand {
    @Override
    public String getName() {
        return "infect";
    }

    @Override
    public String getDesc() {
        return "Infect a player.";
    }

    @Override
    public String getSyntax() {
        return "/virus infect [player]";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if(!sender.hasPermission("ultimatevirus.infect")){
            Player p = (Player) sender;
            invalidPermission(p);
            return;
        }
        if(args.length != 2){
            invalidArgs(sender);
            return;
        }
        // If player already infected
        if(!Ultimatevirus.getInstance().getRDatabase().isInfected(args[1])){
            try{
                Player target = Bukkit.getPlayer(args[1]);
                Ultimatevirus.getInstance().setInfected(target);
                msgOnOnStatusChange(sender, target.getName());
            } catch (Exception exx){
                invalidPlayer(sender);
            }
        } else {
            sender.sendMessage("§cError: that player is already infected.");
        }
    }

    private void msgOnOnStatusChange(CommandSender sender, String targetName){
        sender.sendMessage("§a%target%'s infection status is now set to %status%".replace("%target%", targetName)
                .replace("%status%", Ultimatevirus.getInstance().getRDatabase().isInfected(targetName) ? "true" : "false"));
    }
}