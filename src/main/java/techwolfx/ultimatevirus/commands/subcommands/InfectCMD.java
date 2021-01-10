package techwolfx.ultimatevirus.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import techwolfx.ultimatevirus.Ultimatevirus;
import techwolfx.ultimatevirus.commands.SubCommand;
import techwolfx.ultimatevirus.utils.MainProcess;


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

        Player p;
        try {
            p = Bukkit.getPlayer(args[1]);

            // If player is not infected
            if(!plugin.getRDatabase().isInfected(p.getUniqueId())){
                MainProcess.setInfected(p);
                msgOnOnStatusChange(sender, p);
            } else {
                sender.sendMessage("§cError: "+p.getName()+" is already infected.");
            }
        } catch (Exception ex){
            invalidPlayer(sender);
        }
    }

    private void msgOnOnStatusChange(CommandSender sender, Player p){
        sender.sendMessage("§a%target% is now %status%.".replace("%target%", p.getName())
                .replace("%status%", plugin.getRDatabase().isInfected(p.getUniqueId()) ? "infected" : "healthy"));
    }
}
