package de.sakuramc.coreapi.bootstrap;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Simon Stögerer
 * copyright - all rights reserved
 * created: 19.01.2025 - 18:58
 */

public class SpigotBootstrap extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().info("SakuraMC CoreAPI is now enabled!");
    }
}
