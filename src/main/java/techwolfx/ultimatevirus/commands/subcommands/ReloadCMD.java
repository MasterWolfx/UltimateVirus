package techwolfx.ultimatevirus.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import techwolfx.ultimatevirus.Ultimatevirus;
import techwolfx.ultimatevirus.commands.SubCommand;
import techwolfx.ultimatevirus.files.Language;

public class ReloadCMD extends SubCommand {

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDesc() {
        return "Reloads the plugin.";
    }

    @Override
    public String getSyntax() {
        return "/virus reload";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if(sender instanceof Player){
            Player p = (Player) sender;
            if(!p.hasPermission("ultimatevirus.reload")){
                invalidPermission(p);
                return;
            }
        }

        Ultimatevirus.getInstance().reloadConfig();
        Language.reload();
        sender.sendMessage("§aUltimateVirus files reloaded.");
    }
}