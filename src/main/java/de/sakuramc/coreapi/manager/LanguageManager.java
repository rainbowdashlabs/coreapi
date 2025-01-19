package de.sakuramc.coreapi.manager;

import de.sakuramc.coreapi.CoreAPI;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class LanguageManager {

    private CoreAPI instance;
    private Map<String, YamlConfiguration> languages = new HashMap<>();
    public final ConcurrentMap<UUID, String> playerLanguages = new ConcurrentHashMap<>();
    private final PlayerManager playerManager;

    public LanguageManager(final CoreAPI instance, final PlayerManager playerManager) {
        this.instance = instance;
        this.playerManager = playerManager;
    }

    public void loadLanguages() {
        languages.clear();
        File langFolder = new File(this.instance.getDataFolder(), "lang");

        if (!langFolder.exists()) {
            langFolder.mkdirs();
            this.instance.saveResource("lang/lang_de.yml", false);
            this.instance.saveResource("lang/lang_en.yml", false);
        }

        for (File file : langFolder.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".yml")) {
                String langCode = file.getName().substring(5, 7);
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                languages.put(langCode, config);
            }
        }

        this.instance.getServer().getConsoleSender().sendMessage(this.instance.prefix + "Sprachen geladen: §3" + languages.keySet());
    }

    public YamlConfiguration getLanguageConfig(String langCode) {
        return languages.get(langCode);
    }

    public void loadPlayerLanguage(final Player player) {
        UUID uuid = player.getUniqueId();
        CompletableFuture.runAsync(() -> {
            String language = this.playerManager.getPlayerLanguageFromDatabase(uuid);
            if (language == null) {
                language = "de";
                this.playerManager.setPlayerLanguageInDatabase(uuid, language);
            }
            playerLanguages.put(uuid, language);
        });
    }

    public String getPlayerLanguage(final Player player) {
        return playerLanguages.getOrDefault(player.getUniqueId(), "de");
    }

    public void setPlayerLanguage(final Player player, final String langCode) {
        UUID uuid = player.getUniqueId();
        playerLanguages.put(uuid, langCode);
        CompletableFuture.runAsync(() -> {
            this.playerManager.setPlayerLanguageInDatabase(uuid, langCode);
        });
    }

    @SuppressWarnings("deprecation")
    public String getMessage(String langCode, String key) {
        YamlConfiguration configuration = languages.get(langCode);
        if (configuration != null && configuration.contains(key)) {
            return ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(configuration.getString(key)));
        } else {
            configuration = languages.get("de");
            if (configuration != null) {
                return ChatColor.translateAlternateColorCodes('&', configuration.getString(key, this.instance.prefix + "Es fehlt die Nachricht für den key §3" + key));
            } else {
                return this.instance.prefix + "Es fehlt die Nachricht für den Key §3" + key;
            }
        }
    }

    public void reloadLanguages() {
        loadLanguages();
    }

}
