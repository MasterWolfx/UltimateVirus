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
        // If player already infected
        if(Ultimatevirus.getInstance().getRDatabase().isInfected(args[1])){
            try{
                Player target = Bukkit.getPlayer(args[1]);
                MainProcess.setHealthy(target);
                msgOnOnStatusChange(sender, target.getName());
            } catch (Exception exx){
                invalidPlayer(sender);
            }
        } else {
            sender.sendMessage("§cError: that player can't be recovered.");
        }
    }

    private void msgOnOnStatusChange(CommandSender sender, String targetName){
        sender.sendMessage("§a%target% is now %status%.".replace("%target%", targetName)
                .replace("%status%", Ultimatevirus.getInstance().getRDatabase().isInfected(targetName) ? "infected" : "healthy"));
    }
}
