package techwolfx.ultimatevirus.commands.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import techwolfx.ultimatevirus.commands.SubCommand;
import techwolfx.ultimatevirus.files.LanguageFile;
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
        List<String> infectedList = plugin.getRDatabase().getInfected();

        if (infectedList.size() < 1){
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', LanguageFile.getLangMsg("MsgNoInfectedInsideList")));
            return;
        }

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', LanguageFile.getLangMsg("InfectedListMsg.Header").replace("%total%", Integer.toString(infectedList.size()))));
        for(String pName : infectedList){
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', LanguageFile.getLangMsg("InfectedListMsg.EachLine").replace("%player_name%", pName)));
        }
    }
}
