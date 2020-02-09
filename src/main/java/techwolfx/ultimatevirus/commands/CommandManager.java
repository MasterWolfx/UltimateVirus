package techwolfx.ultimatevirus.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import techwolfx.ultimatevirus.commands.subcommands.CheckInfectionCMD;
import techwolfx.ultimatevirus.commands.subcommands.MaskCMD;
import techwolfx.ultimatevirus.commands.subcommands.ReloadCMD;
import techwolfx.ultimatevirus.commands.subcommands.VaxinCMD;

import java.util.ArrayList;
import java.util.List;

public class CommandManager implements CommandExecutor {

    private ArrayList<SubCommand> subCommands = new ArrayList<>();

    // Constructor
    public CommandManager(){
        subCommands.add(new ReloadCMD());
        subCommands.add(new MaskCMD());
        subCommands.add(new VaxinCMD());
        subCommands.add(new CheckInfectionCMD());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player){
            Player p = (Player) sender;

            // If the command does not comprise a subcommand or if the subcommand is help
            if(args.length == 0 || args[0].equalsIgnoreCase("help")){
                p.sendMessage("§8§m---------------§a§o UltimateVirus §8§m---------------");
                for(SubCommand cmd : subCommands){
                    p.sendMessage("§a" + cmd.getSyntax() + " §8| §7" + cmd.getDesc());
                }
                return false;
            }

            // Checking if the subcommand exists
            for(int i = 0 ; i < getSubCommands().size() ; i++){
                if(args[0].equalsIgnoreCase(getSubCommands().get(i).getName())){
                    getSubCommands().get(i).perform(p, args);
                    return false;
                }
            }
            p.sendMessage("§cSubcommand not found. View all the commands typing: /virus help");
        }
        return false;
    }

    public ArrayList<SubCommand> getSubCommands() {
        return subCommands;
    }
}
