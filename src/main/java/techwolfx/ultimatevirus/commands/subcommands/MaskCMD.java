package techwolfx.ultimatevirus.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import techwolfx.ultimatevirus.Ultimatevirus;
import techwolfx.ultimatevirus.commands.SubCommand;
import java.util.ArrayList;


public class MaskCMD extends SubCommand {

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
    public void perform(Player p, String[] args) {
        if(!p.hasPermission("ultimatevirus.mask")){
            noPermission(p);
            return;
        }
        switch (args.length){
            case 1:
                createMask(p);
                break;
            case 2:
                try{
                    createMask(Bukkit.getPlayer(args[1]));
                } catch (Exception ex){
                    p.sendMessage("§cCan't find that player.");
                }
                break;
            default:
                invalidArgs(p);
                break;
        }
    }
}
