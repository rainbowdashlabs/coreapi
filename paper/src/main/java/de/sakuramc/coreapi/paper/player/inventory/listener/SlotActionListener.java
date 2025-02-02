package de.sakuramc.coreapi.paper.player.inventory.listener;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A utility class for listening to slot actions in Bukkit inventories.
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
 * SlotActionListener listener = new SlotActionListener(inventory);
 *
 * listener.action(0, player -> {
 *    player.sendMessage("You clicked on the first slot!");
 *    player.closeInventory();
 * });
 *
 * player.openInventory(inventory);
 * }</pre>
 */
@Getter
@Accessors(fluent = true)
public final class SlotActionListener implements Listener {
    private final Inventory inventory;
    private final Map<Integer, Consumer<Player>> actions;
    private final Map<Integer, Consumer<Player>> leftClickActions;
    private final Map<Integer, Consumer<Player>> rightClickActions;

    /**
     * Creates a new slot action listener for the specified inventory.
     *
     * @param inventory the inventory to listen for
     */
    public SlotActionListener(Inventory inventory) {
        this.inventory = inventory;
        this.actions = new HashMap<>();
        this.leftClickActions = new HashMap<>();
        this.rightClickActions = new HashMap<>();
    }

    /**
     * Registers an action to be executed when a player left-clicks a slot in the inventory.
     *
     * @param slot the slot to listen for
     * @param action the action to execute
     */
    public void actionLeftClick(int slot, Consumer<Player> action) {
        this.leftClickActions.put(slot, action);
    }

    /**
     * Registers an action to be executed when a player right-clicks a slot in the inventory.
     *
     * @param slot the slot to listen for
     * @param action the action to execute
     */
    public void actionRightClick(int slot, Consumer<Player> action) {
        this.rightClickActions.put(slot, action);
    }

    /**
     * Registers an action to be executed when a player clicks a slot in the inventory.
     *
     * @param slot the slot to listen for
     * @param action the action to execute
     */
    public void action(int slot, Consumer<Player> action) {
        this.actions.put(slot, action);
    }

    /**
     * Handles the inventory click event.
     *
     * @param event the inventory click event
     */
    @EventHandler
    public void handle(@NotNull InventoryClickEvent event) {
        if (!event.getInventory().equals(this.inventory)) return;

        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();
        var clickType = event.getClick();

        if (actions.containsKey(slot)) {
            actions.get(slot).accept(player);
        } else if (clickType.isLeftClick() && leftClickActions.containsKey(slot)) {
            leftClickActions.get(slot).accept(player);
        } else if (clickType.isRightClick() && rightClickActions.containsKey(slot)) {
            rightClickActions.get(slot).accept(player);
        }

        event.setCancelled(true);
    }
}

