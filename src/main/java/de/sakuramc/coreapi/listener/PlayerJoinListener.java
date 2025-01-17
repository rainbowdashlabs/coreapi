package de.sakuramc.coreapi.listener;

import de.sakuramc.coreapi.CoreAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    public CoreAPI instance;

    public PlayerJoinListener(final CoreAPI listener) {
        this.instance = listener;
        this.instance.getServer().getPluginManager().registerEvents(this, listener);
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onPlayerJoinEvent(final PlayerJoinEvent event) {
        event.setJoinMessage(null);

        final Player player = event.getPlayer();

        this.instance.languageManager.loadPlayerLanguage(player);
    }
}
