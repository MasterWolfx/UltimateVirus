package techwolfx.ultimatevirus.commands.subcommands;

import org.bukkit.Bukkit;
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
    public void perform(Player p, String[] args) {
        switch (args.length){
            case 1:
                p.sendMessage(Ultimatevirus.getInstance().getLangMsg("MsgCheckVirus").replace("%result%", Ultimatevirus.getInstance().getRDatabase().isInfected(p.getName()) ? "true" : "false"));
                break;
            case 2:
                if(!p.hasPermission("ultimatevirus.checkothers")){
                    noPermission(p);
                    break;
                }
                try{
                    Bukkit.getPlayer(args[1]).sendMessage(Ultimatevirus.getInstance().getLangMsg("MsgCheckVirus").replace("%result%", Ultimatevirus.getInstance().getRDatabase().isInfected(args[1]) ? "true" : "false"));
                } catch (Exception ex){
                    p.sendMessage("§cCan't find that player.");
                }
                break;
            default:
                invalidArgs(p);
                break;
        }
    }
}
