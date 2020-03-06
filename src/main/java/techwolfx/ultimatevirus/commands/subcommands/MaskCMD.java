package techwolfx.ultimatevirus.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import techwolfx.ultimatevirus.Ultimatevirus;
import techwolfx.ultimatevirus.commands.SubCommand;
import java.util.ArrayList;
import java.util.List;


public class MaskCMD extends SubCommand {

    public static ItemStack getMask(){
        String maskName = Ultimatevirus.getInstance().getConfig().getString("MaskDisplayName").replace("&", "§");

        ItemStack mask = new ItemStack(Material.LEATHER_HELMET, 1);
        ItemMeta im = mask.getItemMeta();
        im.setDisplayName(maskName);
        im.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
        im.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        List<String> lore = Ultimatevirus.getInstance().getConfig().getStringList("MaskLore");
        List<String> newLore = new ArrayList<>();
        for (String line : lore) {
            newLore.add(ChatColor.translateAlternateColorCodes('&', line));
        }
        im.setLore(newLore);

        mask.setItemMeta(im);
        return mask;
    }

    private void giveMask(Player p){
        Inventory inv = p.getInventory();
        inv.addItem(getMask());
        p.sendMessage(Ultimatevirus.getInstance().getLangMsg("MsgOnGiveMask"));
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
