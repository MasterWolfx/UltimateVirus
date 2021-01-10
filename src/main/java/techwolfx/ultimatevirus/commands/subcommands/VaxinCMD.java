package techwolfx.ultimatevirus.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import techwolfx.ultimatevirus.commands.SubCommand;
import techwolfx.ultimatevirus.files.LanguageFile;
import techwolfx.ultimatevirus.utils.UltimatevirusUtils;

public class VaxinCMD extends SubCommand {

    private void giveVaxin(Player p){
        Inventory inv = p.getInventory();

        inv.addItem(UltimatevirusUtils.getVaxin());
        p.sendMessage(LanguageFile.getLangMsg("MsgOnGiveVaxin"));
    }

    @Override
    public String getName() {
        return "vaxin";
    }

    @Override
    public String getDesc() {
        return "Give a vaxin to a plyer.";
    }

    @Override
    public String getSyntax() {
        return "/virus vaxin <player>";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if(sender instanceof Player){
            Player p = (Player) sender;
            if(!p.hasPermission("ultimatevirus.vaxin")){
                invalidPermission(p);
                return;
            }
        }
        switch (args.length){
            case 1:
                if(sender instanceof Player){
                    Player p = (Player) sender;
                    giveVaxin(p);
                } else {
                    sender.sendMessage("§cThis command can only be executed by a player.");
                }
                break;
            case 2:
                try{
                    giveVaxin(Bukkit.getPlayer(args[1]));
                } catch (Exception ex){
                    invalidPlayer(sender);
                }
                break;
            default:
                invalidArgs(sender);
                break;
        }
    }
}
