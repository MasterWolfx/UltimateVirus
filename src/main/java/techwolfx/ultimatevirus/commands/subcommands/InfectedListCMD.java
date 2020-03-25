package techwolfx.ultimatevirus.commands.subcommands;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import techwolfx.ultimatevirus.Ultimatevirus;
import techwolfx.ultimatevirus.commands.SubCommand;

import java.awt.*;
import java.util.List;

public class InfectedListCMD extends SubCommand {

    @Override
    public String getName() {
        return "infected";
    }

    @Override
    public String getDesc() {
        return "Show the list of all the infected players.";
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
            return;
        }
        List<String> infectedList = Ultimatevirus.getInstance().getRDatabase().getInfected();
        if (infectedList.size() < 1){
            sender.sendMessage("§cNo players are infected right now.");
            return;
        }

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Ultimatevirus.getInstance().getConfig().getString("InfectedListMsg.Header").replace("%total%", Integer.toString(infectedList.size()))));
        for(String pName : infectedList){
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Ultimatevirus.getInstance().getConfig().getString("InfectedListMsg.EachLine").replace("%player_name%", pName)));
        }
    }
}
