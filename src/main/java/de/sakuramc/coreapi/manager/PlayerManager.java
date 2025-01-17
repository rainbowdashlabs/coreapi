package de.sakuramc.coreapi.manager;

import de.sakuramc.coreapi.CoreAPI;
import de.sakuramc.coreapi.mysql.MySQLManager;
import de.sakuramc.coreapi.utils.FriendGUI;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class PlayerManager {

    public MySQLManager mySQLManager;

    private final Map<UUID, Long> playerOnlineTimes = new HashMap<>();

    public PlayerManager(final MySQLManager mySQLManager) {
        this.mySQLManager = mySQLManager;
    }

    public CompletableFuture<Boolean> playerExists(final UUID uuid) {
        return mySQLManager.query(String.format("SELECT COUNT(*) AS count FROM user_data WHERE uuid = '%s'", uuid.toString())).thenApply(resultList -> !resultList.isEmpty() && (Long) resultList.get(0).get("count") > 0);
    }

    public CompletableFuture<Double> getCoins(final UUID uuid) {
        return mySQLManager.query(String.format("SELECT coins FROM user_data WHERE uuid = '%s'", uuid.toString())).thenApply(resultList -> {
            if (resultList.isEmpty() || resultList.get(0).get("coins") == null) {
                return 0.0;
            } else {
                return ((Double) resultList.get(0).get("coins"));
            }
        });
    }

    public CompletableFuture<Double> getSakuras(final UUID uuid) {
        return mySQLManager.query(String.format("SELECT sakuras FROM user_data WHERE uuid = '%s'", uuid.toString())).thenApply(resultList -> {
            if (resultList.isEmpty() || resultList.get(0).get("sakuras") == null) {
                return 0.0;
            } else {
                return ((Double) resultList.get(0).get("sakuras"));
            }
        });
    }

    public String getPlayerLanguageFromDatabase(final UUID uuid) {
        String query = "SELECT lang FROM user_data WHERE uuid = ?";
        return mySQLManager.query(query, uuid.toString()).thenApply(resultList -> {
            if (resultList.isEmpty() || resultList.get(0).get("lang") == null) {
                return null;
            }  else {
                return (String) resultList.get(0).get("lang");
            }
        }).join();
    }

    public void setPlayerLanguageInDatabase(final UUID uuid, final String language) {
        String query = "UPDATE user_data SET lang = ? WHERE uuid = ?";
        mySQLManager.update(query, language, uuid.toString());
    }

    public CompletableFuture<Void> setCoins(final UUID uuid, final Double amount) {
        return mySQLManager.update(String.format("UPDATE user_data SET coins = '%s' WHERE uuid = '%s';", amount, uuid.toString()));
    }

    public CompletableFuture<Void> addCoins(final UUID uuid, final double amount) {
        getCoins(uuid).thenAccept(currentCoins -> {
            if (currentCoins != -1.0) {
                setCoins(uuid, currentCoins + amount);
            }
        });

        return null;
    }

    public CompletableFuture<List<Map<String, Object>>> getTopCoins(final int limit, final int offset) {
        return mySQLManager.query(String.format("SELECT uuid, coins FROM user_data ORDER BY coins DESC LIMIT %d OFFSET %d;", limit, offset));
    }

    public CompletableFuture<List<Map<String, Object>>> getTopSakuras(final int limit, final int offset) {
        return mySQLManager.query(String.format("SELECT uuid, sakuras FROM user_data ORDER BY sakuras DESC LIMIT %d OFFSET %d;", limit, offset));
    }

    public CompletableFuture<Integer> getOnlineTimeFromDatabase(final UUID uuid) {
        return mySQLManager.query(String.format("SELECT onlineTime FROM user_data WHERE uuid = '%s'", uuid.toString())).thenApply(resultList -> {
            if (resultList.isEmpty() || resultList.get(0).get("onlineTime") == null) {
                return 0;
            } else {
                return ((Integer) resultList.get(0).get("onlineTime"));
            }
        });
    }

    public void playerJoined(final Player player) {
        getOnlineTimeFromDatabase(player.getUniqueId()).thenAccept(storedOnlineTime -> {
            playerOnlineTimes.put(player.getUniqueId(), System.currentTimeMillis() - storedOnlineTime);
        });
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public CompletableFuture<Void> playerLeft(final Player player) {
        return CompletableFuture.runAsync(() -> {
            UUID uuid = player.getUniqueId();
            long joinTime = playerOnlineTimes.getOrDefault(uuid, System.currentTimeMillis());
            long onlineTime = System.currentTimeMillis() - joinTime;

            mySQLManager.update(String.format("UPDATE user_data SET onlineTime = '%s' WHERE uuid = '%s'", onlineTime, player.getUniqueId()));
            playerOnlineTimes.remove(uuid);
        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }


    public long getCurrentOnlineTime(final Player player) {
        return System.currentTimeMillis() - playerOnlineTimes.getOrDefault(player.getUniqueId(), System.currentTimeMillis());
    }

    public CompletableFuture<Void> removeCoins(final UUID uuid, final double amount) {
        getCoins(uuid).thenAccept(currentCoins -> {
            if (currentCoins != -1.0) {
                setCoins(uuid, currentCoins - amount);
            }
        });

        return null;
    }

    public CompletableFuture<String> getPlayerGroupFromDatabase(final UUID uuid) {
        return mySQLManager.query(String.format("SELECT `group` FROM panelUsers WHERE uuid = '%s'", uuid.toString()))
                .thenCompose(resultList -> {
                    if (resultList.isEmpty() || resultList.get(0).get("group") == null) {
                        return CoreAPI.getInstance().uuidFetcher.getName(uuid)
                                .thenApply(optionalName -> CoreAPI.getInstance().prefix + "Der Spieler §3" + optionalName.orElse("Unbekannt") + " §7wurde §cnicht §7gefunden.");
                    } else {
                        return CompletableFuture.completedFuture((String) resultList.get(0).get("group"));
                    }
                });
    }

    public CompletableFuture<Void> updatePanelGroup(final UUID uuid, final String groupName) {
        return mySQLManager.update(String.format("UPDATE panelUsers SET `group` = '%s' WHERE uuid = '%s'", groupName, uuid.toString()));
    }

    public CompletableFuture<Void> setLastSeen(final UUID uuid) {
        return CompletableFuture.runAsync(() -> {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = new Date();

            String query = "UPDATE user_data SET lastSeen = '%s' WHERE uuid = '%s';";
            mySQLManager.update(String.format(query, simpleDateFormat.format(date), uuid.toString()));
        });
    }

    public CompletableFuture<String> getLastSeenFromDatabase(final UUID uuid) {
        return mySQLManager.query(String.format("SELECT lastSeen FROM user_data WHERE uuid = '%s'", uuid.toString()))
                .thenCompose(resultList -> {
                    if (resultList.isEmpty() || resultList.get(0).get("lastSeen") == null) {
                        return CoreAPI.getInstance().uuidFetcher.getName(uuid)
                                .thenApply(optionalName -> CoreAPI.getInstance().prefix + "Der Spieler §3" + optionalName.orElse("Unbekannt") + " §7wurde §cnicht §7gefunden.");
                    } else {
                        return CompletableFuture.completedFuture((String) resultList.get(0).get("lastSeen"));
                    }
                });
    }

    public CompletableFuture<Map<String, Object>> getPlayerInfo(final UUID uuid) {
        return mySQLManager.query(String.format("SELECT * FROM user_data WHERE uuid = '%s'", uuid.toString()))
                .thenApply(resultList -> {
                    if (resultList.isEmpty()) {
                        return null;
                    } else {
                        return resultList.get(0);
                    }
                });
    }

    public CompletableFuture<List<Map<String, Object>>> getAllPlayersInfo() {
        return mySQLManager.query("SELECT * FROM user_data");
    }

    public CompletableFuture<Void> sendFriendRequest(UUID senderUUID, UUID receiverUUID) {
        String query = "INSERT INTO FriendshipRequests (sender_uuid, receiver_uuid) VALUES (?, ?)";
        return mySQLManager.update(query, senderUUID.toString(), receiverUUID.toString());
    }

    public CompletableFuture<Void> acceptFriendRequest(UUID senderUUID, UUID receiverUUID) {
        String updateRequest = "UPDATE FriendshipRequests SET status = 'accepted' WHERE sender_uuid = ? AND receiver_uuid = ?";
        String insertFriendship = "INSERT INTO Friendships (user_uuid_1, user_uuid_2, status) VALUES (?, ?, 'accepted')";
        return mySQLManager.update(updateRequest, senderUUID.toString(), receiverUUID.toString())
                .thenCompose(v -> mySQLManager.update(insertFriendship, senderUUID.toString(), receiverUUID.toString()));
    }

    public CompletableFuture<Void> denyFriendRequest(UUID senderUUID, UUID receiverUUID) {
        String query = "UPDATE FriendshipRequests SET status = 'declined' WHERE sender_uuid = ? AND receiver_uuid = ?";
        return mySQLManager.update(query, senderUUID.toString(), receiverUUID.toString());
    }

    public CompletableFuture<Void> removeFriend(UUID userUUID, UUID friendUUID) {
        String query = "DELETE FROM Friendships WHERE (user_uuid_1 = ? AND user_uuid_2 = ?) OR (user_uuid_1 = ? AND user_uuid_2 = ?)";
        return mySQLManager.update(query, userUUID.toString(), friendUUID.toString(), friendUUID.toString(), userUUID.toString());
    }

    public CompletableFuture<Boolean> areFriends(UUID playerUUID1, UUID playerUUID2) {
        String query = "SELECT COUNT(*) AS count FROM Friendships " +
                "WHERE ((user_uuid_1 = ? AND user_uuid_2 = ?) OR (user_uuid_1 = ? AND user_uuid_2 = ?)) " +
                "AND status = 'accepted'";
        return mySQLManager.query(query, playerUUID1.toString(), playerUUID2.toString(), playerUUID2.toString(), playerUUID1.toString())
                .thenApply(result -> {
                    if (result.isEmpty()) {
                        return false;
                    }
                    Map<String, Object> row = result.get(0);
                    long count = (long) row.get("count");
                    return count > 0;
                });
    }

    public CompletableFuture<Integer> getAnzahlOffenerAnfragen(UUID playerUUID) {
        String query = "SELECT COUNT(*) AS count FROM FriendshipRequests WHERE receiver_uuid = ? AND status = 'pending'";
        return mySQLManager.query(query, playerUUID.toString()).thenApply(result -> {
            if (result.isEmpty()) {
                return 0;
            }
            Map<String, Object> row = result.get(0);
            return ((Long) row.get("count")).intValue();
        });
    }

    public CompletableFuture<List<UUID>> getFriendsFromDatabase(UUID playerUUID) {
        String query = "SELECT user_uuid_1, user_uuid_2 FROM Friendships WHERE (user_uuid_1 = ? OR user_uuid_2 = ?) AND status = 'accepted'";
        return mySQLManager.query(query, playerUUID.toString(), playerUUID.toString()).thenApply(results -> {
            return results.stream()
                    .map(row -> {
                        String uuid1 = (String) row.get("user_uuid_1");
                        String uuid2 = (String) row.get("user_uuid_2");
                        if (uuid1.equals(playerUUID.toString())) {
                            return UUID.fromString(uuid2);
                        } else {
                            return UUID.fromString(uuid1);
                        }
                    })
                    .collect(Collectors.toList());
        });
    }

    public CompletableFuture<Boolean> hasPendingFriendRequest(UUID senderUUID, UUID receiverUUID) {
        String query = "SELECT COUNT(*) AS count FROM FriendshipRequests WHERE sender_uuid = ? AND receiver_uuid = ? AND status = 'pending'";
        return mySQLManager.query(query, senderUUID.toString(), receiverUUID.toString()).thenApply(result -> {
            if (result.isEmpty()) {
                return false;
            }
            Map<String, Object> row = result.get(0);
            long count = (long) row.get("count");
            return count > 0;
        });
    }

}

