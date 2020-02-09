package techwolfx.ultimatevirus.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import techwolfx.ultimatevirus.Ultimatevirus;
import techwolfx.ultimatevirus.commands.SubCommand;

import java.util.ArrayList;

public class VaxinCMD extends SubCommand {

    public static ItemStack getVaxin(){
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
        return vaxin;
    }

    private void createVaxin(Player p){
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
    public void perform(Player p, String[] args) {
        if(!p.hasPermission("ultimatevirus.vaxin")){
            noPermission(p);
            return;
        }
        switch (args.length){
            case 1:
                createVaxin(p);
                break;
            case 2:
                try{
                    createVaxin(Bukkit.getPlayer(args[1]));
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
