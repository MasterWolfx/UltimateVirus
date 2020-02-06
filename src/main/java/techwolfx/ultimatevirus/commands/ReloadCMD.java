package techwolfx.ultimatevirus.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import techwolfx.ultimatevirus.Ultimatevirus;
import techwolfx.ultimatevirus.files.InfectedList;
import techwolfx.ultimatevirus.files.Language;

public class ReloadCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender.hasPermission("ultimatevirus.reload")){
            Ultimatevirus.getInstance().reloadConfig();
            InfectedList.reload();
            Language.reload();
            sender.sendMessage("§aUltimateVirus reloaded correctly.");
        }
        return false;
    }
}
