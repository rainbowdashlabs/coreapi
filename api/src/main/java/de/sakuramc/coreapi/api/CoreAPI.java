package de.sakuramc.coreapi.api;

import com.zaxxer.hikari.HikariDataSource;
import de.chojo.sadu.datasource.DataSourceCreator;
import de.chojo.sadu.postgresql.databases.PostgreSql;
import de.sakuramc.coreapi.api.language.LanguageAPI;
import de.sakuramc.coreapi.api.modules.ModuleHandler;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * The core API of the SakuraMC network.
 *
 * <p>This class is used to access the core API of the SakuraMC network.</p>
 */

@Getter
@Accessors(fluent = true)
public final class CoreAPI  {
    @Getter
    @Accessors(fluent = true)
    private static CoreAPI instance;

    private final HikariDataSource dataSource;

    public CoreAPI() {
        instance = this;

        this.dataSource = DataSourceCreator.create(PostgreSql.get())
                .configure(config -> config.host("localhost")
                        .port(5432)
                        .user("postgres")
                        .password("Test1234!")
                        .database("postgres")
                        .currentSchema("coreapi")
                        .applicationName("CoreAPI")
                        .driverClass()
                )
                .create()
                .build();
    }

    public static void main(String[] args) {
        new CoreAPI();
    }
}
