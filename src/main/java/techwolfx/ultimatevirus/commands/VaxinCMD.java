package techwolfx.ultimatevirus.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import techwolfx.ultimatevirus.Ultimatevirus;
import java.util.ArrayList;

public class VaxinCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player){
            Player p = (Player) sender;
            if(p.hasPermission("ultimatevirus.vaxin")){
                switch (args.length){
                    case 0:
                        createVaxin(p);
                        break;
                    case 1:
                        try{
                            createVaxin(Bukkit.getPlayer(args[0]));
                        } catch (Exception ex){
                            p.sendMessage("§cCan't find that player.");
                        }
                        break;
                }
            }
        } else {
            if(args.length == 1){
                try{
                    createVaxin(Bukkit.getPlayer(args[0]));
                } catch (Exception ex){
                    Bukkit.getConsoleSender().sendMessage("§cCan't find that player.");
                }
            } else {
                Bukkit.getConsoleSender().sendMessage("§cInvalid amount of arguments. Usage /vaxin [Player]");
            }
        }
        return false;
    }

    private void createVaxin(Player p){
        Inventory inv = p.getInventory();
        String vaxinName = Ultimatevirus.getInstance().getConfig().getString("VaxinDisplayName").replace("&", "§");

        ItemStack vaxin = new ItemStack(Material.POTION, 1);
        ItemMeta im = vaxin.getItemMeta();
        im.setDisplayName(vaxinName);
        im.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
        im.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        ArrayList<String> lore = new ArrayList<>();
        lore.add(" &b&l* &7Recover yourself from a virus.".replace("&", "§"));
        lore.add("&b&oYou can drink this vaxin ".replace("&", "§"));
        lore.add("&b&oonly if u are infected.".replace("&", "§"));
        im.setLore(lore);

        vaxin.setItemMeta(im);
        inv.addItem(vaxin);
        p.sendMessage("§bYou were given a Vaxin.");
    }
}
