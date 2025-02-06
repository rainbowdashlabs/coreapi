package de.sakuramc.coreapi.paper;

import de.sakuramc.coreapi.api.CoreAPI;
import de.sakuramc.coreapi.api.language.LanguageAPI;
import de.sakuramc.coreapi.api.modules.ModuleHandler;
import de.sakuramc.coreapi.paper.common.commands.LanguageChangeCommand;
import de.sakuramc.coreapi.paper.common.inventory.LanguageInventory;
import de.sakuramc.coreapi.paper.common.listener.SakuraLanguageListener;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Simon Stögerer
 * copyright - all rights reserved
 * created: 31.01.2025 - 23:05
 */
@Getter
@Accessors(fluent = true)
public class CorePaperService extends JavaPlugin {
    @Getter
    @Accessors(fluent = true)
    private static CorePaperService instance;

    private CoreAPI coreAPI;
    private ModuleHandler moduleHandler;
    private LanguageInventory languageInventory;

    @Override
    public void onLoad() {
    }

    @Override
    public void onEnable() {
        instance = this;
        this.coreAPI = new CoreAPI();
        this.moduleHandler = new ModuleHandler();
        this.moduleHandler.registerModule(LanguageAPI.class, () -> new LanguageAPI("coreapi"));
        this.languageInventory = new LanguageInventory();

        final var lifecycleEventManager = this.getLifecycleManager();

        lifecycleEventManager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final var commands = event.registrar();

            commands.register("language", new LanguageChangeCommand());
        });

        Bukkit.getPluginManager().registerEvents(new SakuraLanguageListener(), this);
    }

    @Override
    public void onDisable() {

    }
}
