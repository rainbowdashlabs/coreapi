package de.sakuramc.coreapi.paper.player.inventory;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * A utility class for creating Bukkit inventories.
 *
 */
public interface SakuraInventory {
    /**
     * Creates a new inventory for the specified player with the specified size and name.
     *
     * @param player the player to create the inventory for
     * @param size the size of the inventory
     * @param name the name of the inventory
     */
    void createInventory(@NotNull final Player player, final int size, @NotNull final Component name);

    /**
     * Adds a border to the specified inventory with the specified items.
     *
     * @param items the items to add to the border
     * @param size the size of the inventory
     */
    void addInventoryBorder(@NotNull Map<Integer, ItemStack> items, int size);

    /**
     * Checks if the specified slot is a border slot in the specified inventory size.
     *
     * @param slot the slot to check
     * @param size the size of the inventory
     * @return {@code true} if the slot is a border slot, otherwise {@code false}
     */
    default boolean isBorderSlot(int slot, int size) {
        return (slot < 9 || slot >= size - 9 || slot % 9 == 0 || (slot + 1) % 9 == 0);
    }
}
