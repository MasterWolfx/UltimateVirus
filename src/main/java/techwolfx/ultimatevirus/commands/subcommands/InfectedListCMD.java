package techwolfx.ultimatevirus.commands.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import techwolfx.ultimatevirus.Ultimatevirus;
import techwolfx.ultimatevirus.commands.SubCommand;
import techwolfx.ultimatevirus.files.Language;
import java.util.List;

public class InfectedListCMD extends SubCommand {

    @Override
    public String getName() {
        return "infected";
    }

    @Override
    public String getDesc() {
        return "Show the list of all infected players.";
    }

    @Override
    public String getSyntax() {
        return "/virus infected";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if(!sender.hasPermission("ultimatevirus.infected")){
            return;
        }
        if(args.length != 1){
            invalidArgs(sender);
            return;
        }
        List<String> infectedList = Ultimatevirus.getInstance().getRDatabase().getInfected();

        if (infectedList.size() < 1){
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Language.getLangMsg("MsgNoInfectedInsideList")));
            return;
        }

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Language.getLangMsg("InfectedListMsg.Header").replace("%total%", Integer.toString(infectedList.size()))));
        for(String pName : infectedList){
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Language.getLangMsg("InfectedListMsg.EachLine").replace("%player_name%", pName)));
        }
    }
}
