package techwolfx.ultimatevirus.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import techwolfx.ultimatevirus.Ultimatevirus;

import java.util.ArrayList;
import java.util.List;

public class UltimatevirusUtils {

    private static final Ultimatevirus plugin = Ultimatevirus.getInstance();

    public static ItemStack getMask(){
        String maskName = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("MaskDisplayName"));

        ItemStack mask = new ItemStack(Material.LEATHER_HELMET, 1);
        ItemMeta im = mask.getItemMeta();
        im.setDisplayName(maskName);
        im.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
        im.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        List<String> lore = plugin.getConfig().getStringList("MaskLore");
        List<String> newLore = new ArrayList<>();
        for (String line : lore) {
            newLore.add(ChatColor.translateAlternateColorCodes('&', line));
        }
        im.setLore(newLore);

        mask.setItemMeta(im);
        return mask;
    }

    public static ItemStack getVaxin(){
        String vaxinName = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("VaxinDisplayName"));

        ItemStack vaxin = new ItemStack(Material.POTION, 1);
        ItemMeta im = vaxin.getItemMeta();
        im.setDisplayName(vaxinName);
        im.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
        im.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        List<String> lore = plugin.getConfig().getStringList("VaxinLore");
        List<String> newLore = new ArrayList<>();
        for (String line : lore) {
            newLore.add(ChatColor.translateAlternateColorCodes('&', line));
        }
        im.setLore(newLore);

        vaxin.setItemMeta(im);
        return vaxin;
    }

}
