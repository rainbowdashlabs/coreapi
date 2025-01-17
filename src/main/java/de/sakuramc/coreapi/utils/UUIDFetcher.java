package de.sakuramc.coreapi.utils;

import de.sakuramc.coreapi.mysql.MySQLManager;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UUIDFetcher {

    private final MySQLManager mySQLManager;

    public UUIDFetcher(MySQLManager mySQLManager) {
        this.mySQLManager = mySQLManager;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public CompletableFuture<Optional<String>> getUUID(final String playerName) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<Map<String, Object>> result = mySQLManager.query(String.format("SELECT uuid FROM player_data WHERE name = '%s'", playerName)).join();

                if (!result.isEmpty()) {
                    return Optional.of((String) result.get(0).get("uuid"));
                } else {
                    return Optional.empty();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return Optional.empty();
        });
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public CompletableFuture<Optional<String>> getName(final UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<Map<String, Object>> result = mySQLManager.query(String.format("SELECT name FROM player_data WHERE uuid = '%s'", uuid)).join();

                if (!result.isEmpty()) {
                    return Optional.of((String) result.get(0).get("name"));
                } else {
                    return Optional.empty();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return Optional.empty();
        });
    }

    @SuppressWarnings({"CallToPrintStackTrace"})
    public CompletableFuture<Void> registerPlayer(final UUID uuid, final String playerName) {
        return CompletableFuture.runAsync(() -> {
            try {
                mySQLManager.update(String.format("INSERT INTO player_data (uuid, name) VALUES ('%s', '%s')", uuid, playerName));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public CompletableFuture<Void> updatePlayerName(final UUID uuid, final String newPlayerName) {
        return CompletableFuture.runAsync(() -> {
            try {
                mySQLManager.update(String.format("UPDATE player_data SET name = '%s' WHERE uuid = '%s'", newPlayerName, uuid));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public CompletableFuture<Boolean> isUUIDRegistered(final UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<Map<String, Object>> result = mySQLManager.query(String.format("SELECT uuid FROM player_data WHERE uuid = '%s'", uuid)).join();
                return !result.isEmpty();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        });
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public CompletableFuture<Boolean> hasNameChanged(final UUID uuid, final String currentPlayerName) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<Map<String, Object>> result = mySQLManager.query(String.format("SELECT name FROM player_data WHERE uuid = '%s'", uuid)).join();

                if (!result.isEmpty()) {
                    String storedName = (String) result.get(0).get("name");
                    return !storedName.equalsIgnoreCase(currentPlayerName);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        });
    }

}
