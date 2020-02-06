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


public class MaskCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player){
            Player p = (Player) sender;
            if(p.hasPermission("ultimatevirus.mask")) {
                switch (args.length){
                    case 0:
                        createMask(p);
                        break;
                    case 1:
                        try{
                            createMask(Bukkit.getPlayer(args[0]));
                        } catch (Exception ex){
                            p.sendMessage("§cCan't find that player.");
                        }
                        break;
                }
            }
        } else {
            if(args.length == 1){
                try{
                    createMask(Bukkit.getPlayer(args[0]));
                } catch (Exception ex){
                    Bukkit.getConsoleSender().sendMessage("§cCan't find that player.");
                }
            } else {
                Bukkit.getConsoleSender().sendMessage("§cInvalid amount of arguments. Usage /mask [Player]");
            }
        }
        return false;
    }

    private void createMask(Player p){
        Inventory inv = p.getInventory();
        String maskName = Ultimatevirus.getInstance().getConfig().getString("MaskDisplayName").replace("&", "§");

        ItemStack mask = new ItemStack(Material.LEATHER_HELMET, 1);
        ItemMeta im = mask.getItemMeta();
        im.setDisplayName(maskName);
        im.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
        im.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        ArrayList<String> lore = new ArrayList<>();
        lore.add(" &2&l* &7Protect yourself from virus.".replace("&", "§"));
        lore.add("&2&oYou must keep this mask ".replace("&", "§"));
        lore.add("&2&oin your inventory.".replace("&", "§"));
        im.setLore(lore);

        mask.setItemMeta(im);
        inv.addItem(mask);
        p.sendMessage("§2You were given an AntiVirus Mask.");
    }
}
