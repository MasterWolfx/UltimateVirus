package techwolfx.ultimatevirus.commands.subcommands;

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
    public void perform(Player p, String[] args) {
        if(!p.hasPermission("ultimatevirus.reload")){
            noPermission(p);
            return;
        }
        Ultimatevirus.getInstance().reloadConfig();
        Language.reload();
        p.sendMessage("§aUltimateVirus reloaded correctly.");
    }
}
