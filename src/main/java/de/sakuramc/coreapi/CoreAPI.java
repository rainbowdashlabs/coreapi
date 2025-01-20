package de.sakuramc.coreapi;

import com.zaxxer.hikari.HikariDataSource;
import de.chojo.sadu.datasource.DataSourceCreator;
import de.chojo.sadu.mysql.databases.MySql;
import de.sakuramc.coreapi.language.LanguageAPI;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class CoreAPI  {
    @Getter
    @Accessors(fluent = true)
    private static CoreAPI instance;

    private final HikariDataSource dataSource;
    private final LanguageAPI languageAPI;

    public CoreAPI() {
        instance = this;

        this.dataSource = DataSourceCreator.create(MySql.get())
                .configure(config -> config.host("localhost")
                        .port(3306)
                        .user("root")
                        .password("")
                        .database("system")
                )
                .create()
                .withMaximumPoolSize(3)
                .withMinimumIdle(1)
                .build();

        this.languageAPI = new LanguageAPI(dataSource);

        final var welcomeMessage = this.languageAPI().translate("de_DE", "welcome_message");

        System.out.println(welcomeMessage);
    }
}
