package techwolfx.ultimatevirus.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import techwolfx.ultimatevirus.commands.SubCommand;
import techwolfx.ultimatevirus.files.LanguageFile;

public class SetHealthCMD extends SubCommand {
    @Override
    public String getName() {
        return "sethealth";
    }

    @Override
    public String getDesc() {
        return "Set to a custom value the health of a player.";
    }

    @Override
    public String getSyntax() {
        return "/virus sethealth [player:*(for everyone)] [health]";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if(!sender.hasPermission("ultimatevirus.sethealth")){
            return;
        }
        if(args.length != 3){
            invalidArgs(sender);
            return;
        }
        if(args[1].equalsIgnoreCase("*")){
            try{
                for(Player p : Bukkit.getOnlinePlayers()){
                    p.setMaxHealth(Double.parseDouble(args[2]));
                    sender.sendMessage(LanguageFile.getLangMsg("MsgOnSetHealth").replace("%player%", args[1]).replace("%health%", args[2]));
                }
            } catch (Exception ex){
                sender.sendMessage("§cAn error occured: player is offline or invalid health amount.");
            }
            return;
        }
        try{
            Player p = Bukkit.getPlayer(args[1]);
            p.setMaxHealth(Double.parseDouble(args[2]));
            sender.sendMessage(LanguageFile.getLangMsg("MsgOnSetHealth").replace("%player%", args[1]).replace("%health%", args[2]));
        } catch (Exception ex){
            sender.sendMessage("§cAn error occured: player is offline or invalid health amount.");
        }
    }
}
