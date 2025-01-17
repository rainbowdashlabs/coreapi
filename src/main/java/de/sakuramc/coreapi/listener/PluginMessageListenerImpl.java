package de.sakuramc.coreapi.listener;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import de.sakuramc.coreapi.CoreAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class PluginMessageListenerImpl implements PluginMessageListener {

    private final Gson gson = new Gson();

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        CoreAPI.getInstance().getServer().getConsoleSender().sendMessage(CoreAPI.getInstance().prefix + "Nachricht erhalten öffne GUI");

        if (!channel.equals(CoreAPI.LANG_CHANNEL_ID)) {
            return;
        }

        String jsonString = new String(message, StandardCharsets.UTF_8);
        JsonObject json = gson.fromJson(jsonString, JsonObject.class);
        String type = json.get("type").getAsString();


        if (type.equals("openLanguageGUI")) {
            String uuidStr = json.get("uuid").getAsString();
            UUID playerUUID = UUID.fromString(uuidStr);
            Player targetPlayer = Bukkit.getPlayer(playerUUID);

            if (targetPlayer != null && targetPlayer.isOnline()) {
                LanguageListener.openLanguageSelectionGUI(targetPlayer);
            }
        } else if (type.equals("updatePlayerLanguage")) {
            String uuidStr = json.get("uuid").getAsString();
            String languageCode = json.get("language").getAsString();
            UUID playerUUID = UUID.fromString(uuidStr);
            LanguageListener.updatePlayerLanguageLocally(playerUUID, languageCode);
        }
    }

}
