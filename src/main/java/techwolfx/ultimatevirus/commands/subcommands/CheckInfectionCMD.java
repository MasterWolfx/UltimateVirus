package techwolfx.ultimatevirus.commands.subcommands;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import techwolfx.ultimatevirus.commands.SubCommand;
import techwolfx.ultimatevirus.files.LanguageFile;
import techwolfx.ultimatevirus.utils.PlaceholderUtils;

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

                    if(plugin.arePalceholdersEnabled()){
                        String msg = (LanguageFile.getLangMsg("MsgCheckVirus"));
                        p.sendMessage(PlaceholderAPI.setPlaceholders(p, msg));
                        return;
                    } else {
                        sender.sendMessage(LanguageFile.getLangMsg("MsgCheckVirus").replace("%ultimatevirus_isInfected%", PlaceholderUtils.isInfectedReturnMsg(p.getUniqueId())));
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
                Player p;
                try {
                    p = Bukkit.getPlayer(args[1]);

                    if (plugin.getRDatabase().isPlayerRegistered(p.getUniqueId())) {
                        if (plugin.arePalceholdersEnabled()) {
                            String msg = (LanguageFile.getLangMsg("MsgCheckVirusOthers").replace("%target%", args[1]));

                            if( Bukkit.getOnlinePlayers().contains(p) ){
                                sender.sendMessage(PlaceholderAPI.setPlaceholders(p, msg));
                            } else {
                                sender.sendMessage(PlaceholderAPI.setPlaceholders(Bukkit.getOfflinePlayer(args[1]), msg));
                            }

                        } else {
                            sender.sendMessage(LanguageFile.getLangMsg("MsgCheckVirusOthers").replace("%target%", args[1]).replace("%ultimatevirus_isInfected%", PlaceholderUtils.isInfectedReturnMsg(p.getUniqueId())));
                        }
                    } else {
                        sender.sendMessage("§cThis player is not registered in database.");
                    }
                } catch (Exception e) {
                    invalidPlayer(sender);
                }
                break;
            default:
                invalidArgs(sender);
                break;
        }
    }
}
