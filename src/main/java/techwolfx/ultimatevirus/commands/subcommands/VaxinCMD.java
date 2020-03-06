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

public class VaxinCMD extends SubCommand {

    public static ItemStack getVaxin(){
        String vaxinName = Ultimatevirus.getInstance().getConfig().getString("VaxinDisplayName").replace("&", "§");

        ItemStack vaxin = new ItemStack(Material.POTION, 1);
        ItemMeta im = vaxin.getItemMeta();
        im.setDisplayName(vaxinName);
        im.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
        im.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        List<String> lore = Ultimatevirus.getInstance().getConfig().getStringList("VaxinLore");
        List<String> newLore = new ArrayList<>();
        for (String line : lore) {
            newLore.add(ChatColor.translateAlternateColorCodes('&', line));
        }
        im.setLore(newLore);

        vaxin.setItemMeta(im);
        return vaxin;
    }

    private void giveVaxin(Player p){
        Inventory inv = p.getInventory();

        inv.addItem(getVaxin());
        p.sendMessage(Ultimatevirus.getInstance().getLangMsg("MsgOnGiveVaxin"));
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
