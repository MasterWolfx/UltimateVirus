package techwolfx.ultimatevirus.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import techwolfx.ultimatevirus.Ultimatevirus;
import techwolfx.ultimatevirus.commands.SubCommand;
import techwolfx.ultimatevirus.utils.MainProcess;

public class RecoverCMD extends SubCommand {
    @Override
    public String getName() {
        return "recover";
    }

    @Override
    public String getDesc() {
        return "Recover a player from the virus.";
    }

    @Override
    public String getSyntax() {
        return "/virus recover [player]";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {

        if(!sender.hasPermission("ultimatevirus.recover")){
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

            // If player is infected
            if(plugin.getRDatabase().isInfected(p.getUniqueId())){
                msgOnOnStatusChange(sender, p);
                MainProcess.setHealthy(p, true);
            } else {
                sender.sendMessage("§cError: "+p.getName()+" is not infected and can't be recovered.");
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
