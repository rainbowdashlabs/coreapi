package de.sakuramc.coreapi.item;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Ein Builder für ItemStacks, der eine fluente API zum Erstellen und Anpassen von Items bietet.
 */
public class ItemBuilder {

    private final ItemStack item;
    private ItemMeta meta;

    /**
     * Erstellt einen neuen ItemBuilder mit dem angegebenen Material.
     *
     * @param material Das Material des Items.
     */
    public ItemBuilder(Material material) {
        this(material, 1);
    }

    /**
     * Erstellt einen neuen ItemBuilder mit dem angegebenen Material und der Menge.
     *
     * @param material Das Material des Items.
     * @param amount   Die Menge des Items.
     */
    public ItemBuilder(Material material, int amount) {
        this.item = new ItemStack(material, amount);
        this.meta = item.getItemMeta();
    }

    /**
     * Setzt den Displaynamen des Items.
     *
     * @param name Der gewünschte Displayname.
     * @return Der aktuelle ItemBuilder.
     */
    @SuppressWarnings("deprecation")
    public ItemBuilder setName(String name) {
        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        }
        return this;
    }

    /**
     * Fügt dem Item eine Lore (Beschreibung) hinzu.
     *
     * @param lore Die Lore-Zeilen.
     * @return Der aktuelle ItemBuilder.
     */
    @SuppressWarnings("deprecation")
    public ItemBuilder setLore(String... lore) {
        if (meta != null) {
            List<String> coloredLore = new ArrayList<>();
            for (String line : lore) {
                coloredLore.add(ChatColor.translateAlternateColorCodes('&', line));
            }
            meta.setLore(coloredLore);
        }
        return this;
    }

    /**
     * Fügt dem Item eine Lore (Beschreibung) hinzu.
     *
     * @param lore Die Liste der Lore-Zeilen.
     * @return Der aktuelle ItemBuilder.
     */
    @SuppressWarnings("deprecation")
    public ItemBuilder setLore(List<String> lore) {
        if (meta != null) {
            List<String> coloredLore = new ArrayList<>();
            for (String line : lore) {
                coloredLore.add(ChatColor.translateAlternateColorCodes('&', line));
            }
            meta.setLore(coloredLore);
        }
        return this;
    }

    /**
     * Fügt dem Item eine Verzauberung hinzu.
     *
     * @param enchantment Die Verzauberung.
     * @param level       Die Stufe der Verzauberung.
     * @return Der aktuelle ItemBuilder.
     */
    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        if (meta != null) {
            meta.addEnchant(enchantment, level, true);
        }
        return this;
    }

    /**
     * Fügt dem Item mehrere Verzauberungen hinzu.
     *
     * @param enchantments Eine Map von Verzauberungen und ihren Stufen.
     * @return Der aktuelle ItemBuilder.
     */
    public ItemBuilder addEnchantments(Map<Enchantment, Integer> enchantments) {
        if (meta != null) {
            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                meta.addEnchant(entry.getKey(), entry.getValue(), true);
            }
        }
        return this;
    }

    /**
     * Fügt dem Item einen ItemFlag hinzu.
     *
     * @param flag Der ItemFlag, der hinzugefügt werden soll.
     * @return Der aktuelle ItemBuilder.
     */
    public ItemBuilder addItemFlag(ItemFlag flag) {
        if (meta != null) {
            meta.addItemFlags(flag);
        }
        return this;
    }

    /**
     * Setzt das Item als unzerstörbar.
     *
     * @param unbreakable true, wenn das Item unzerstörbar sein soll, sonst false.
     * @return Der aktuelle ItemBuilder.
     */
    public ItemBuilder setUnbreakable(boolean unbreakable) {
        if (meta != null) {
            meta.setUnbreakable(unbreakable);
        }
        return this;
    }

    /**
     * Setzt den Besitzer eines Kopf-Items.
     *
     * @param ownerUUID Die UUID des Besitzers.
     * @return Der aktuelle ItemBuilder.
     */
    @SuppressWarnings("deprecation")
    public ItemBuilder setSkullOwner(UUID ownerUUID) {
        if (meta != null && meta instanceof SkullMeta) {
            SkullMeta skullMeta = (SkullMeta) meta;
            skullMeta.setOwnerProfile(Bukkit.createPlayerProfile(ownerUUID, null));
            this.meta = skullMeta;
        }
        return this;
    }

    /**
     * Setzt den Besitzer eines Kopf-Items anhand des Spielernamens.
     *
     * @param ownerName Der Name des Besitzers.
     * @return Der aktuelle ItemBuilder.
     */
    @SuppressWarnings("deprecation")
    public ItemBuilder setSkullOwner(String ownerName) {
        if (meta != null && meta instanceof SkullMeta) {
            SkullMeta skullMeta = (SkullMeta) meta;
            skullMeta.setOwner(ownerName);
            this.meta = skullMeta;
        }
        return this;
    }

    /**
     * Gibt das erstellte ItemStack-Objekt zurück.
     *
     * @return Das erstellte ItemStack.
     */
    public ItemStack build() {
        if (meta != null) {
            item.setItemMeta(meta);
        }
        return item;
    }
}
