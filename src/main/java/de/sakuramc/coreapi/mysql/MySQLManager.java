package de.sakuramc.coreapi.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.sakuramc.coreapi.CoreAPI;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MySQLManager {

    private final HikariDataSource dataSource;
    private final ExecutorService executor;

    public MySQLManager(final String hostname, final int port, final String database, final String username, final String password) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + hostname + ":" + port + "/" + database + "?useSSL=false&serverTimezone=UTC");
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(10); // Maximale Anzahl der Verbindungen im Pool
        config.setMinimumIdle(2); // Minimale Anzahl der Verbindungen im Pool
        config.setIdleTimeout(30000); // Verbindungen, die länger als 30 Sekunden untätig sind, werden geschlossen
        config.setConnectionTimeout(30000); // Timeout für das Warten auf eine Verbindung
        config.setLeakDetectionThreshold(15000); // Leck-Erkennung nach 15 Sekunden

        this.dataSource = new HikariDataSource(config);
        this.executor = Executors.newCachedThreadPool();
    }

    public void connect() {
        try (Connection connection = dataSource.getConnection()) {
            if (connection != null) {
                CoreAPI.getInstance().getServer().getConsoleSender().sendMessage(CoreAPI.getInstance().prefix + "Die Verbindung zur MySQL Datenbank war §aerfolgreich§7!");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void disconnect() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
            CoreAPI.getInstance().getServer().getConsoleSender().sendMessage(CoreAPI.getInstance().prefix + "Die Verbindung wurde §aerfolgreich §7von der MySQL Datenbank getrennt.");
        }
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            CoreAPI.getInstance().getServer().getConsoleSender().sendMessage(CoreAPI.getInstance().prefix + "Der HikariCP Verbindungspool wurde geschlossen.");
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public CompletableFuture<Void> update(String query, Object... params) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
                for (int i = 0; i < params.length; i++) {
                    statement.setObject(i + 1, params[i]);
                }
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }, executor);
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public CompletableFuture<List<Map<String, Object>>> query(String query, Object... params) {
        return CompletableFuture.supplyAsync(() -> {
            List<Map<String, Object>> resultList = new ArrayList<>();
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {

                // Setze die Parameter
                for (int i = 0; i < params.length; i++) {
                    statement.setObject(i + 1, params[i]);
                }

                try (ResultSet resultSet = statement.executeQuery()) {
                    ResultSetMetaData metaData = resultSet.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    while (resultSet.next()) {
                        Map<String, Object> row = new HashMap<>();
                        for (int i = 1; i <= columnCount; i++) {
                            row.put(metaData.getColumnName(i), resultSet.getObject(i));
                        }
                        resultList.add(row);
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return resultList;
        }, executor);
    }

}
