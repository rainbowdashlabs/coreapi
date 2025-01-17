package de.sakuramc.coreapi.listener;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import de.sakuramc.coreapi.CoreAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class LanguageListener implements Listener {

    private final Gson gson = new Gson();

    public CoreAPI instance;

    public LanguageListener(final CoreAPI listener) {
        this.instance = listener;
    }

    public static void openLanguageSelectionGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 9, "Sprache auswählen");

        ItemStack germanHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta germanMeta = (SkullMeta) germanHead.getItemMeta();
        germanMeta.setDisplayName("Deutsch");
        germanHead.setItemMeta(germanMeta);

        ItemStack englishHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta englishMeta = (SkullMeta) englishHead.getItemMeta();
        englishMeta.setDisplayName("English");
        englishHead.setItemMeta(englishMeta);

        gui.setItem(2, germanHead);
        gui.setItem(6, englishHead);

        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals("Sprache auswählen")) {
            return;
        }

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || clickedItem.getType() != Material.PLAYER_HEAD) {
            return;
        }

        String selectedLanguage = null;
        if (clickedItem.getItemMeta().getDisplayName().equals("Deutsch")) {
            selectedLanguage = "de";
        } else if (clickedItem.getItemMeta().getDisplayName().equals("English")) {
            selectedLanguage = "en";
        }

        if (selectedLanguage != null) {
            sendLanguageSelection(player, selectedLanguage);

            player.closeInventory();

            player.sendMessage("Sprache auf " + (selectedLanguage.equals("de") ? "Deutsch" : "English") + " gesetzt.");
        }
    }

    private void sendLanguageSelection(Player player, String languageCode) {
        UUID playerUUID = player.getUniqueId();

        JsonObject json = new JsonObject();
        json.addProperty("type", "languageSelected");
        json.addProperty("uuid", playerUUID.toString());
        json.addProperty("language", languageCode);

        String message = gson.toJson(json);
        player.sendPluginMessage(CoreAPI.getInstance(), CoreAPI.LANG_CHANNEL_ID, message.getBytes(StandardCharsets.UTF_8));

        CoreAPI.getInstance().getLogger().info("Spieler " + player.getName() + " hat die Sprache " + languageCode + " ausgewählt.");
    }

    public static void updatePlayerLanguageLocally(UUID playerUUID, String languageCode) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null || !player.isOnline()) {
            CoreAPI.getInstance().getLogger().info("Spieler mit UUID " + playerUUID + " ist nicht online.");
            return;
        }

        // Beispielhafte Implementierung:
        if (languageCode.equalsIgnoreCase("de")) {
            player.sendMessage("Sprache auf Deutsch gesetzt.");
            // Weitere Anpassungen basierend auf der Sprache
        } else if (languageCode.equalsIgnoreCase("en")) {
            player.sendMessage("Language set to English.");
            // Weitere Anpassungen basierend auf der Sprache
        }
    }

}
