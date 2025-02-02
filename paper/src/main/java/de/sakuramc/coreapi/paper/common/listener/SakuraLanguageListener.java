package de.sakuramc.coreapi.paper.common.listener;

import de.sakuramc.coreapi.paper.CorePaperService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * @author Simon Stögerer
 * copyright - all rights reserved
 * created: 01.02.2025 - 10:56
 */

public final class SakuraLanguageListener implements Listener {
    @EventHandler
    public void handle(@NotNull PlayerJoinEvent event) {
        CorePaperService.instance().languageAPI().setLanguage(event.getPlayer().getUniqueId(), Locale.US);
    }
}
