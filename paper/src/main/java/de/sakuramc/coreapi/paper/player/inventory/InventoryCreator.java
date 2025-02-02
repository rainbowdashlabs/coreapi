package de.sakuramc.coreapi.paper.player.inventory;

import lombok.Builder;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * A utility class for creating and customizing Bukkit inventories with a fluent API.
 * This class allows you to set the size, name, owner, and items of an inventory before building it.
 *
 * <p>Usage Example:</p>
 * <pre>{@code
 * Inventory inventory = new InventoryCreator()
 *     .size(45)
 *     .name("Custom Inventory")
 *     .owner(player)
 *     .items(Map.of(
 *         0, Material.DIAMOND,
 *         1, Material.GOLD_INGOT
 *     ))
 *     .build();
 *
 * player.openInventory(inventory);
 * }</pre>
 *
 * <p><strong>Note:</strong> You can customize the item creation by using an {@code ItemBuilder} if required.</p>
 */
public final class InventoryCreator {
    private Integer size;
    private Component name;
    private Player owner;
    private Map<Integer, ItemStack> items;
    private InventoryType type;

    /**
     * Sets the size of the inventory.
     *
     * @param size the size of the inventory (must be a multiple of 9 and between 9 and 54 inclusive)
     * @return the current {@code InventoryCreator} instance for method chaining
     */
    public InventoryCreator size(@NotNull Integer size) {
        this.size = size;
        return this;
    }

    /**
     * Sets the inventory type.
     *
     * @param type the {@link InventoryType} of the inventory
     * @return the current {@code InventoryCreator} instance for method chaining
     */
    public InventoryCreator type(@NotNull InventoryType type) {
        this.type = type;
        return this;
    }

    /**
     * Sets the name (title) of the inventory.
     *
     * @param name the title of the inventory
     * @return the current {@code InventoryCreator} instance for method chaining
     */
    public InventoryCreator name(@NotNull Component name) {
        this.name = name;
        return this;
    }

    /**
     * Sets the owner of the inventory.
     *
     * @param owner the {@link Player} who owns the inventory
     * @return the current {@code InventoryCreator} instance for method chaining
     */
    public InventoryCreator owner(@Nullable Player owner) {
        this.owner = owner;
        return this;
    }

    /**
     * Sets the items to be added to the inventory.
     *
     * @param items a map of slot indexes to {@link ItemStack} items
     * @return the current {@code InventoryCreator} instance for method chaining
     */
    public InventoryCreator items(@Nullable Map<Integer, ItemStack> items) {
        this.items = items;
        return this;
    }

    /**
     * Builds and returns the {@link Inventory} instance with the configured properties.
     *
     * @return the constructed {@link Inventory} instance
     */
    public @NotNull Inventory build() {
        Inventory inventory;
        if (type != null) {
            inventory = Bukkit.createInventory(owner, type, name);
        } else {
            inventory = Bukkit.createInventory(owner, size, name);
        }

        if (items != null) {
            for (var entry : items.entrySet()) {
                inventory.setItem(entry.getKey(), entry.getValue());
            }
        }

        return inventory;
    }
}