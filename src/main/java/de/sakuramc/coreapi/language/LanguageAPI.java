package de.sakuramc.coreapi.language;

import com.zaxxer.hikari.HikariDataSource;
import de.chojo.sadu.mapper.RowMapperRegistry;
import de.chojo.sadu.mysql.mapper.MySqlMapper;
import de.chojo.sadu.queries.api.call.Call;
import de.chojo.sadu.queries.api.configuration.QueryConfiguration;
import de.sakuramc.coreapi.CoreAPI;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

/**
 * @author Simon Stögerer
 * copyright - all rights reserved
 * created: 20.01.2025 - 23:09
 */

@Getter
@Accessors(fluent = true)
public final class LanguageAPI {
    private final Map<String, Properties> languageCache = new HashMap<>();
    private final String defaultLocale = "en_US";
    private final Path languageDirectory = Paths.get("plugins/CoreAPI/languages");
    private final QueryConfiguration queryConfiguration;

    public LanguageAPI(HikariDataSource dataSource) {
        this.queryConfiguration = QueryConfiguration.builder(dataSource)
                .setExceptionHandler(err -> System.out.println("An error occurred while executing a query"))
                .setThrowExceptions(true)
                .setAtomic(true)
                .setRowMapperRegistry(new RowMapperRegistry().register(MySqlMapper.getDefaultMapper()))
                .build();

        ensureLanguageFilesExist();
        loadAllLanguages();
    }

    public void setLanguage(UUID uuid, String language) {
        queryConfiguration.query("INSERT INTO player_languages (uuid, language) VALUES (:uuid, :language) ON DUPLICATE KEY UPDATE language = :language")
                .single(Call.of()
                        .bind("uuid", uuid.toString())
                        .bind("language", language))
                .update();
    }

    public String getLanguage(UUID uuid) {
        return queryConfiguration.query("SELECT language FROM player_languages WHERE uuid = :uuid")
                .single(Call.of().bind("uuid", uuid.toString()))
                .map(row -> row.getString("language"))
                .first().orElse(defaultLocale);
    }

    private void ensureLanguageFilesExist() {
        if (!Files.exists(languageDirectory)) {
            try {
                Files.createDirectories(languageDirectory);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        String[] languages = {"de_DE", "en_US"};
        for (String locale : languages) {
            Path filePath = languageDirectory.resolve(locale + ".properties");
            if (!Files.exists(filePath)) {
                copyDefaultLanguageFile(locale);
            }
        }
    }

    private void copyDefaultLanguageFile(String locale) {
        try (InputStream resource = getClass().getClassLoader().getResourceAsStream("languages/" + locale + ".properties")) {
            if (resource == null) {
                System.err.println("Language file not found in resources: " + locale);
                return;
            }
            Files.copy(resource, languageDirectory.resolve(locale + ".properties"), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Copied default language file: " + locale);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadAllLanguages() {
        try {
            Files.list(languageDirectory)
                    .filter(path -> path.toString().endsWith(".properties"))
                    .forEach(this::loadLanguageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadLanguageFile(Path filePath) {
        String fileName = filePath.getFileName().toString();
        String locale = fileName.replace(".properties", "");

        Properties properties = new Properties();
        try {
            properties.load(Files.newInputStream(filePath));
            languageCache.put(locale, properties);

            System.out.println("Loaded language file: " + locale);
            properties.forEach((key, value) -> System.out.println("Key: " + key + " -> Value: " + value));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String translate(String locale, String key, Object... args) {
        System.out.println("Translating: " + key + " to " + locale);

        Properties properties = languageCache.get(locale);
        if (properties == null) {
            System.err.println("No properties found for locale: " + locale);
            return key;
        }

        String translation = properties.getProperty(key);
        if (translation == null) {
            System.err.println("Translation key not found: " + key + " in locale: " + locale);
            return key;
        }

        System.out.println("Translation found: " + translation);
        return String.format(translation, args);
    }

    public void reloadLanguages() {
        languageCache.clear();
        loadAllLanguages();
    }
}
