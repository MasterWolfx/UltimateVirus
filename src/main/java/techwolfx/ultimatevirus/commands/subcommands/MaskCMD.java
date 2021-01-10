package techwolfx.ultimatevirus.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import techwolfx.ultimatevirus.commands.SubCommand;
import techwolfx.ultimatevirus.files.LanguageFile;
import techwolfx.ultimatevirus.utils.UltimatevirusUtils;


public class MaskCMD extends SubCommand {

    private void giveMask(Player p){
        Inventory inv = p.getInventory();
        inv.addItem(UltimatevirusUtils.getMask());
        p.sendMessage(LanguageFile.getLangMsg("MsgOnGiveMask"));
    }

    @Override
    public String getName() {
        return "mask";
    }

    @Override
    public String getDesc() {
        return "Give a mask to a player.";
    }

    @Override
    public String getSyntax() {
        return "/virus mask <player>";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if(sender instanceof Player){
            Player p = (Player) sender;
            if(!p.hasPermission("ultimatevirus.mask")){
                invalidPermission(p);
                return;
            }
        }
        switch (args.length){
            case 1:
                if(sender instanceof Player){
                    Player p = (Player) sender;
                    giveMask(p);
                } else {
                    sender.sendMessage("§cThis command can only be executed by a player.");
                }
                break;
            case 2:
                try{
                    giveMask(Bukkit.getPlayer(args[1]));
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
