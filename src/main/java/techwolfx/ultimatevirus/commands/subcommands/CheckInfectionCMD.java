package techwolfx.ultimatevirus.commands.subcommands;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import techwolfx.ultimatevirus.Ultimatevirus;
import techwolfx.ultimatevirus.commands.SubCommand;
import techwolfx.ultimatevirus.files.Language;
import techwolfx.ultimatevirus.utils.MainProcess;

public class CheckInfectionCMD extends SubCommand {

    @Override
    public String getName() {
        return "check";
    }

    @Override
    public String getDesc() {
        return "Check if you (or another player) have a virus.";
    }

    @Override
    public String getSyntax() {
        return "/virus check <player>";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {

        switch (args.length){
            case 1:
                if(sender instanceof Player){
                    Player p = (Player) sender;

                    if(!p.hasPermission("ultimatevirus.check")){
                        invalidPermission(p);
                        break;
                    }

                    if(Ultimatevirus.getInstance().myPlaceholder != null){
                        String msg = (Language.getLangMsg("MsgCheckVirus"));
                        p.sendMessage(PlaceholderAPI.setPlaceholders(p, msg));
                        return;
                    } else {
                        sender.sendMessage(Language.getLangMsg("MsgCheckVirus").replace("%ultimatevirus_isInfected%", MainProcess.isInfectedReturnMsg(p.getName())));
                    }
                } else {
                    sender.sendMessage("§cThis command can only be executed by a player.");
                }
                break;
            case 2:
                if(sender instanceof Player){
                    Player p = (Player) sender;
                    if(!p.hasPermission("ultimatevirus.checkothers")){
                        invalidPermission(p);
                        break;
                    }
                }
                // Check if player is registered in database
                if(Ultimatevirus.getInstance().getRDatabase().isPlayerRegistered(args[1])){
                    if(Ultimatevirus.getInstance().myPlaceholder != null){
                        String msg = (Language.getLangMsg("MsgCheckVirusOthers").replace("%target%", args[1]));
                        if(Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(args[1]))){
                            sender.sendMessage(PlaceholderAPI.setPlaceholders(Bukkit.getPlayer(args[1]), msg));
                        } else {
                            sender.sendMessage(PlaceholderAPI.setPlaceholders(Bukkit.getOfflinePlayer(args[1]), msg));
                        }

                    } else{
                        sender.sendMessage(Language.getLangMsg("MsgCheckVirusOthers").replace("%target%", args[1]).replace("%ultimatevirus_isInfected%", MainProcess.isInfectedReturnMsg(args[1])));
                    }
                } else {
                    sender.sendMessage("§cThis player is not registered in database.");
                }
                break;
            default:
                invalidArgs(sender);
                break;
        }
    }
}
