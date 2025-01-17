package de.sakuramc.coreapi.listener;

import de.sakuramc.coreapi.CoreAPI;
import de.sakuramc.coreapi.commands.FlyCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    public CoreAPI instance;

    public PlayerQuitListener(final CoreAPI listener) {
        this.instance = listener;
        this.instance.getServer().getPluginManager().registerEvents(this, listener);
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onPlayerQuitEvent(final PlayerQuitEvent event) {
        event.setQuitMessage(null);

        final Player player = event.getPlayer();

        if (FlyCommand.getFlyMode().contains(player)) {
            FlyCommand.getFlyMode().remove(player);
        }

        this.instance.languageManager.playerLanguages.remove(player.getUniqueId());
    }

}
