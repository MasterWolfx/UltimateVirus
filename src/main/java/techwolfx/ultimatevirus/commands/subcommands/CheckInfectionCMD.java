package techwolfx.ultimatevirus.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import techwolfx.ultimatevirus.Ultimatevirus;
import techwolfx.ultimatevirus.commands.SubCommand;

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
                    p.sendMessage(Ultimatevirus.getInstance().getLangMsg("MsgCheckVirus").replace("%result%", Ultimatevirus.getInstance().getRDatabase().isInfected(p.getName()) ? "true" : "false"));
                } else {
                    sender.sendMessage("§cThis command can only be executed by a player.");
                }
                break;
            case 2:
                if(sender instanceof Player){
                    Player p = (Player) sender;
                    if(!p.hasPermission("ultimatevirus.checkothers")){
                        noPermission(p);
                        break;
                    }
                }
                try{
                    sender.sendMessage(Ultimatevirus.getInstance().getLangMsg("MsgCheckVirusOthers").replace("%result%", Ultimatevirus.getInstance().getRDatabase().isInfected(args[1]) ? "true" : "false").replace("%target%", args[1]));
                } catch (Exception ex){
                    sender.sendMessage("§cCan't find that player.");
                }
                break;
            default:
                invalidArgs(sender);
                break;
        }
    }
}
