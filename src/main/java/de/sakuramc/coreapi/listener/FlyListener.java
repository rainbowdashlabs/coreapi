package de.sakuramc.coreapi.listener;

import de.sakuramc.coreapi.CoreAPI;
import de.sakuramc.coreapi.commands.FlyCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class FlyListener implements Listener {

    private final CoreAPI instance;

    public FlyListener(CoreAPI listener) {
        this.instance = listener;
        this.instance.getServer().getPluginManager().registerEvents(this, listener);
    }

    @EventHandler
    public void onEntityDamageEvent(final EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            final Player player = (Player) event.getEntity();

            if (FlyCommand.getNoFallDamagePlayers().contains(player)) {
                event.setCancelled(true);
            }
        }
    }

}
