package techwolfx.ultimatevirus.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import techwolfx.ultimatevirus.commands.subcommands.*;

import java.util.ArrayList;


public class CommandManager implements CommandExecutor {

    private ArrayList<SubCommand> subCommands = new ArrayList<>();

    // Constructor
    public CommandManager(){
        subCommands.add(new MaskCMD());
        subCommands.add(new VaxinCMD());
        subCommands.add(new CheckInfectionCMD());
        subCommands.add(new InfectedListCMD());
        subCommands.add(new InfectCMD());
        subCommands.add(new RecoverCMD());
        subCommands.add(new ReloadCMD());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // If the command does not comprise a subcommand or if the subcommand is help
        if(args.length == 0 || args[0].equalsIgnoreCase("help")){
            if(!sender.hasPermission("ultimatevirus.help"))
            sender.sendMessage("§8§m---------------§a§o UltimateVirus §8§m---------------");
            for(SubCommand cmd : subCommands){
                sender.sendMessage("§a" + cmd.getSyntax() + " §8| §7" + cmd.getDesc());
            }
            return false;
        }

        // Checking if the subcommand exists
        for(int i = 0 ; i < getSubCommands().size() ; i++){
            if(args[0].equalsIgnoreCase(getSubCommands().get(i).getName())){
                getSubCommands().get(i).perform(sender, args);
                return false;
            }
        }
        sender.sendMessage("§cSubcommand not found. View all the commands typing: /virus help");

        return false;
    }

    public ArrayList<SubCommand> getSubCommands() {
        return subCommands;
    }
}
