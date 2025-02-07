package de.sakuramc.coreapi.paper.player.items;

import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * A utility class for creating and customizing Bukkit items with a fluent API.
 *
 * <p>Usage Example:</p>
 * <pre>{@code
 * ItemStack item = new ItemCreator(Material.DIAMOND)
 *    .setName("Custom Item")
 *    .setLore("First line", "Second line")
 *    .build();}
 * </pre>
 */
public class ItemCreator {
    private final ItemStack itemStack;

    public ItemCreator(final @NotNull Material material) {
        this(material, 1);
    }

    public ItemCreator(final @NotNull ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemCreator(final @NotNull Material material, final int amount) {
        this.itemStack = new ItemStack(material, amount);
    }

    public ItemCreator(final @NotNull Material material, final int amount, final byte durability) {
        this.itemStack = new ItemStack(material, amount, durability);
    }

    public final ItemCreator setDurability(final short dur) {
        this.itemStack.setDurability(dur);
        return this;
    }

    public final ItemCreator setAmount(final Integer amount) {
        this.itemStack.setAmount(amount);
        return this;
    }

    public final ItemCreator setName(final String name) {
        final ItemMeta im = this.itemStack.getItemMeta();
        im.setDisplayName(name);
        this.itemStack.setItemMeta(im);
        return this;
    }

    public final ItemCreator addUnsafeEnchantment(final Enchantment enchant, final int level) {
        this.itemStack.addUnsafeEnchantment(enchant, level);
        return this;
    }

    public final ItemCreator removeEnchantment(final Enchantment ench) {
        this.itemStack.removeEnchantment(ench);
        return this;
    }

    public final ItemCreator setSkullOwner(final String owner) {
        try {
            final SkullMeta im = (SkullMeta) this.itemStack.getItemMeta();
            im.setOwner(owner);
            this.itemStack.setItemMeta(im);
        } catch (final ClassCastException ignored) {
        }
        return this;
    }

    public final ItemCreator addEnchant(final Enchantment ench, final int level) {
        final ItemMeta im = this.itemStack.getItemMeta();
        im.addEnchant(ench, level, true);
        this.itemStack.setItemMeta(im);
        return this;
    }

    public final ItemCreator addEnchantments(final Map<Enchantment, Integer> enchantments) {
        this.itemStack.addEnchantments(enchantments);
        return this;
    }

    public final ItemCreator setLore(final String... lore) {
        final ItemMeta im = this.itemStack.getItemMeta();
        im.setLore(Arrays.asList(lore));
        this.itemStack.setItemMeta(im);
        return this;
    }

    public final ItemCreator setLore(final List<String> lore) {
        final ItemMeta im = this.itemStack.getItemMeta();
        im.setLore(lore);
        this.itemStack.setItemMeta(im);
        return this;
    }

    public final ItemCreator removeLoreLine(final String line) {
        final ItemMeta im = this.itemStack.getItemMeta();
        final List<String> lore = new ArrayList<>(im.getLore());
        if (!lore.contains(line)) return this;
        lore.remove(line);
        im.setLore(lore);
        this.itemStack.setItemMeta(im);
        return this;
    }

    public final ItemCreator removeLoreLine(final int index) {
        final ItemMeta im = this.itemStack.getItemMeta();
        final List<String> lore = new ArrayList<>(im.getLore());
        if (index < 0 || index >= lore.size()) return this;
        lore.remove(index);
        im.setLore(lore);
        this.itemStack.setItemMeta(im);
        return this;
    }

    public final ItemCreator addLoreLine(final String line) {
        final ItemMeta im = this.itemStack.getItemMeta();
        final List<String> lore = new ArrayList<>();
        if (im.hasLore()) lore.addAll(im.getLore());
        lore.add(line);
        im.setLore(lore);
        this.itemStack.setItemMeta(im);
        return this;
    }

    public final ItemCreator addLoreLine(final String line, final int pos) {
        final ItemMeta im = this.itemStack.getItemMeta();
        final List<String> lore = new ArrayList<>(im.getLore());
        if (pos >= 0 && pos < lore.size()) lore.set(pos, line);
        im.setLore(lore);
        this.itemStack.setItemMeta(im);
        return this;
    }

    public final ItemStack toItemStack() {
        return this.itemStack;
    }

    public final ItemCreator clone() {
        return new ItemCreator(this.itemStack);
    }

    public final ItemStack skull(final String textures, final String name, final String... lore) {
        final var head = new ItemStack(Material.PLAYER_HEAD);
        head.editMeta(SkullMeta.class, skullMeta -> {
            final var uuid = UUID.randomUUID();
            final var playerProfile = Bukkit.createProfile(uuid, uuid.toString().substring(0, 16));
            playerProfile.setProperty(new ProfileProperty("textures", textures));
            skullMeta.setPlayerProfile(playerProfile);

            skullMeta.setDisplayName(name);
            skullMeta.setLore(Arrays.asList(lore));
        });
        return head;
    }
}
