package de.sakuramc.coreapi.listener;

import de.sakuramc.coreapi.CoreAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.help.HelpTopic;

public class CommandNotFoundListener implements Listener {

    public CoreAPI instance;

    public CommandNotFoundListener(CoreAPI listener) {
        this.instance = listener;
        this.instance.getServer().getPluginManager().registerEvents(this, listener);
    }

    @EventHandler
    public void onPlayerCommandPreprocessEvent(final PlayerCommandPreprocessEvent event) {
        if (!event.isCancelled()) {
            final Player player = event.getPlayer();

            String cmd = event.getMessage().split(" ")[0];

            HelpTopic topic = Bukkit.getServer().getHelpMap().getHelpTopic(cmd);

            if (topic == null) {
                player.sendMessage(this.instance.prefix + "Der Befehl §8[§3" + cmd + "§8] §7wurde §cnicht §7gefunden.");
                event.setCancelled(true);
            }
        }
    }

}
