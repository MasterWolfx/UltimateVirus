package techwolfx.ultimatevirus.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CmdTabCompletion implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(args.length == 1){
            CommandManager cm = new CommandManager();
            List<String> subCmds = new ArrayList<>();
            for (int i = 0 ; i < cm.getSubCommands().size() ; i++){
                subCmds.add(cm.getSubCommands().get(i).getName());
            }
            return subCmds;
        }
        return null;
    }
}
