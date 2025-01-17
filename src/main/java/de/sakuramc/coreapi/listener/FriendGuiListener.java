package de.sakuramc.coreapi.listener;

import de.sakuramc.coreapi.CoreAPI;
import de.sakuramc.coreapi.item.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class FriendGuiListener implements Listener {

    public CoreAPI instance;

    public FriendGuiListener(final CoreAPI listener) {
        this.instance = listener;
        this.instance.getServer().getPluginManager().registerEvents(this, listener);
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onInventoryClickEvent(final InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();

        if (event.getView().getTitle().startsWith("§8┃ §7Deine §aFreunde §7(")) {
            event.setCancelled(true);

            if (!(event.getWhoClicked() instanceof Player)) {
                return;
            }

            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem == null || clickedItem.getType() == Material.AIR) {
                return;
            }

            String displayName = clickedItem.getItemMeta().getDisplayName();

            if (displayName.startsWith("§3")) {
                String friendName = ChatColor.stripColor(displayName).substring(3).trim();

                player.closeInventory();
                Inventory inventory = CoreAPI.getInstance().getServer().createInventory(player, 9*3, "§8┃ §3" + friendName);

                ItemBuilder itemBuilder = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE, 1).setName("§8#");

                for (int i = 0; i < 9*3; i++) {
                    inventory.setItem(i, itemBuilder.build());
                }

                player.openInventory(inventory);
            }
        }
    }

}
