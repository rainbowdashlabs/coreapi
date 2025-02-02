package de.sakuramc.coreapi.api.language;

import de.chojo.sadu.mapper.RowMapperRegistry;
import de.chojo.sadu.postgresql.mapper.PostgresqlMapper;
import de.chojo.sadu.queries.api.call.Call;
import de.chojo.sadu.queries.api.configuration.QueryConfiguration;
import de.sakuramc.coreapi.api.CoreAPI;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Getter
@Accessors(fluent = true)
public final class LanguageAPI {
    private final String pluginName;
    private final Map<Locale, Properties> languageCache;
    private final Locale defaultLocale = Locale.ENGLISH;
    private final Path languageDirectory;
    private final QueryConfiguration queryConfiguration;
    private final MiniMessage miniMessage;
    /**
     * creates a new instance of the LanguageAPI.
     *
     * @param pluginName The name of the plugin.
     */
    public LanguageAPI(final @NotNull String pluginName) {
        this.pluginName = pluginName;
        this.languageDirectory = Path.of("plugins", pluginName, "languages");
        this.languageCache = new HashMap<>();
        this.miniMessage = MiniMessage.miniMessage();
        this.queryConfiguration = QueryConfiguration.builder(CoreAPI.instance().dataSource())
                .setExceptionHandler(err -> System.out.println("An error occurred while executing a query"))
                .setThrowExceptions(true)
                .setAtomic(true)
                .setRowMapperRegistry(new RowMapperRegistry().register(PostgresqlMapper.getDefaultMapper()))
                .build();

        this.ensureLanguageFileExist();
        this.loadAllLanguages();
    }


    /**
     * defines the language of a player.
     *
     * @param uuid  The UUID of the player.
     * @param locale The locale of the player.
     */
    public void setLanguage(@NotNull UUID uuid, @NotNull Locale locale) {
        queryConfiguration.query("INSERT INTO player_languages (uuid, language) VALUES (:uuid, :language) ON CONFLICT (uuid) DO UPDATE SET language = :language")
                .single(Call.of()
                        .bind("uuid", uuid.toString())
                        .bind("language", locale.toString()))
                .update();
    }

    /**
     * returns the language of a player.
     *
     * @param uuid The UUID of the player.
     * @return The language of the player.
     */
    public Locale language(@NotNull UUID uuid) {
        final var dbLocale = queryConfiguration.query("SELECT language FROM player_languages WHERE uuid = :uuid")
                .single(Call.of().bind("uuid", uuid.toString()))
                .map(row -> {
                    try {
                        return Locale.forLanguageTag(row.getString("language").replace('_', '-'));
                    } catch (Exception e) {
                        return null;
                    }
                }).first().orElse(null);

        final var selectedLocale = (dbLocale != null) ? dbLocale : defaultLocale;

        return languageCache.keySet().stream()
                .filter(l -> l.toString().equalsIgnoreCase(selectedLocale.toString()) ||
                        l.toString().startsWith(selectedLocale.toString()))
                .findFirst()
                .orElse(defaultLocale);

    }

    /**
     * ensures that the language file exists.
     */
    private void ensureLanguageFileExist() {
        if (!Files.exists(this.languageDirectory)) {
            try {
                Files.createDirectories(languageDirectory);
            } catch (IOException exception) {
                System.out.println("Could not create language directory. " + exception.getMessage());
                return;
            }
        }

        for (final var locale : List.of(Locale.GERMANY, Locale.US)) {
            final var path = this.languageDirectory.resolve(locale.toString() + ".properties");
            if (!Files.exists(path)) {
                copyDefaultLanguageFile(locale);
            }
        }
    }


    /**
     * copies the default language file.
     *
     * @param locale The locale of the player.
     */
    private void copyDefaultLanguageFile(Locale locale) {
        try (final var resource = getClass().getClassLoader().getResourceAsStream("languages/" + locale + ".properties")) {
            if (resource == null) {
                System.out.println("Language file not found in resources: " + locale);
                return;
            }
            Files.copy(resource, languageDirectory.resolve(locale + ".properties"), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Copied default language file: " + locale);
        } catch (IOException e) {
            System.out.println("Error copying default language file: " + e.getMessage());
        }
    }

    /**
     * loads all languages.
     */
    private void loadAllLanguages() {
        try {
            Files.list(languageDirectory)
                    .filter(path -> path.toString().endsWith(".properties"))
                    .forEach(this::loadLanguageFile);
        } catch (IOException e) {
            System.out.println("Error loading language files: " + e.getMessage());
        }
    }

    /**
     * loads the language file.
     *
     * @param filePath The path of the file.
     */
    private void loadLanguageFile(@NotNull Path filePath) {
        final var fileName = filePath.getFileName().toString();
        final var locale = Locale.forLanguageTag(fileName.replace(".properties", "").replace('_', '-'));

        final var properties = new Properties();

        try (final var reader = Files.newBufferedReader(filePath)) {
            properties.load(reader);
            languageCache.put(locale, properties);

            System.out.println("Loaded language file: " + locale);
        } catch (IOException e) {
            System.out.println("Error loading language file " + locale + ": " + e.getMessage());
        }
    }

    /**
     * translates a message.
     *
     * @param uuid The UUID of the player.
     * @param key The key of the message.
     * @param args The arguments of the message.
     * @return The translated message.
     */
    public @NotNull Component translate(final @NotNull UUID uuid, final @NotNull String key, final @NotNull Object... args) {
        var localeLanguage = this.language(uuid);

        var properties = this.languageCache.get(localeLanguage);

        if (properties == null) {
            System.out.println("Locale not found in cache, trying fallback: " + localeLanguage);
            Locale finalLocaleLanguage = localeLanguage;
            localeLanguage = languageCache.keySet().stream()
                    .filter(l -> l.toString().startsWith(finalLocaleLanguage.getISO3Language().toString()))
                    .findFirst()
                    .orElse(defaultLocale);
            properties = this.languageCache.get(localeLanguage);
        }

        final var translation = properties.getProperty(key, key);

        return this.miniMessage.deserialize(String.format(translation, args));
    }

    /**
     * reloads all languages.
     */
    public void reloadLanguages() {
        languageCache.clear();
        loadAllLanguages();
    }
}
