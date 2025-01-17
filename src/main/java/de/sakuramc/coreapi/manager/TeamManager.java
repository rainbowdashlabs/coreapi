package de.sakuramc.coreapi.manager;

import de.sakuramc.coreapi.mysql.MySQLManager;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class TeamManager {

    public MySQLManager mySQLManager;

    public TeamManager(MySQLManager mySQLManager) {
        this.mySQLManager = mySQLManager;
    }

    public CompletableFuture<Boolean> playerExists(final UUID uuid) {
        return mySQLManager.query(String.format("SELECT COUNT(*) AS count FROM team WHERE uuid = '%s'", uuid.toString())).thenApply(resultList -> !resultList.isEmpty() && (Long) resultList.get(0).get("count") > 0);
    }

    public CompletableFuture<Void> addPlayerToTeam(final UUID uuid, final int ban_count, final int mute_count, final int report_count, final int commandSpy, final int notify) {
        return mySQLManager.update(String.format("INSERT INTO team(uuid, ban_count, mute_count, report_count, commandSpy, notify) VALUES ('%s', '%s', '%s', '%s', '%s', '%s');", uuid.toString(), ban_count, mute_count, report_count, commandSpy, notify));
    }

    public CompletableFuture<Void> removePlayerFromTeam(final UUID uuid) {
        return mySQLManager.update(String.format("DELETE FROM team WHERE uuid = '%s'", uuid.toString()));
    }

    public CompletableFuture<Integer> getBanCount(final UUID uuid) {
        return mySQLManager.query(String.format("SELECT ban_count FROM team WHERE uuid = '%s'", uuid.toString())).thenApply(resultList -> {
            if (resultList.isEmpty() || resultList.get(0).get("ban_count") == null) {
                return 0;
            } else {
                return ((Integer) resultList.get(0).get("ban_count"));
            }
        });
    }

    public CompletableFuture<Integer> getNotifyState(final UUID uuid) {
        return mySQLManager.query(String.format("SELECT notify FROM team WHERE uuid = '%s'", uuid.toString())).thenApply(resultList -> {
            if (resultList.isEmpty() || resultList.get(0).get("notify") == null) {
                return 0;
            } else {
                return ((Integer) resultList.get(0).get("notify"));
            }
        });
    }

    public CompletableFuture<Void> setNotifyState(final UUID uuid, final Integer state) {
        return mySQLManager.update(String.format("UPDATE team SET notify = '%s' WHERE uuid = '%s'", state, uuid));
    }

    public CompletableFuture<Void> setBans(final UUID uuid, final Integer amount) {
        return mySQLManager.update(String.format("UPDATE team SET ban_count = '%s' WHERE uuid = '%s'", amount, uuid.toString()));
    }

    public CompletableFuture<Void> addBan(final UUID uuid) {
        getBanCount(uuid).thenAccept(currentBans -> {
            if (currentBans != -1.0) {
                setBans(uuid, currentBans + 1);
            }
        });

        return null;
    }

    public CompletableFuture<Integer> getMuteCount(final UUID uuid) {
        return mySQLManager.query(String.format("SELECT mute_count FROM team WHERE uuid = '%s'", uuid.toString())).thenApply(resultList -> {
            if (resultList.isEmpty() || resultList.get(0).get("mute_count") == null) {
                return 0;
            } else {
                return ((Integer) resultList.get(0).get("mute_count"));
            }
        });
    }

    public CompletableFuture<Void> setMutes(final UUID uuid, final Integer amount) {
        return mySQLManager.update(String.format("UPDATE team SET mute_count = '%s' WHERE uuid = '%s'", amount, uuid.toString()));
    }

    public CompletableFuture<Void> addMute(final UUID uuid) {
        getMuteCount(uuid).thenAccept(currentBans -> {
            if (currentBans != -1.0) {
                setMutes(uuid, currentBans + 1);
            }
        });

        return null;
    }

    public CompletableFuture<Integer> getReportCount(final UUID uuid) {
        return mySQLManager.query(String.format("SELECT report_count FROM team WHERE uuid = '%s'", uuid.toString())).thenApply(resultList -> {
            if (resultList.isEmpty() || resultList.get(0).get("report_count") == null) {
                return 0;
            } else {
                return ((Integer) resultList.get(0).get("report_count"));
            }
        });
    }

    public CompletableFuture<Void> setReports(final UUID uuid, final Integer amount) {
        return mySQLManager.update(String.format("UPDATE team SET report_count = '%s' WHERE uuid = '%s'", amount, uuid.toString()));
    }

    public CompletableFuture<Void> addReport(final UUID uuid) {
        getReportCount(uuid).thenAccept(currentBans -> {
            if (currentBans != -1.0) {
                setReports(uuid, currentBans + 1);
            }
        });

        return null;
    }

    public CompletableFuture<Integer> getCommandSpy(final UUID uuid) {
        return mySQLManager.query(String.format("SELECT commandSpy FROM team WHERE uuid = '%s'", uuid.toString())).thenApply(resultList -> {
            if (resultList.isEmpty() || resultList.get(0).get("commandSpy") == null) {
                return 0;
            } else {
                return ((Integer) resultList.get(0).get("commandSpy"));
            }
        });
    }

    public CompletableFuture<Void> activateCommandSpy(final UUID uuid) {
        return mySQLManager.update(String.format("UPDATE team SET commandSpy = '1' WHERE uuid = '%s'", uuid.toString()));
    }

    public CompletableFuture<Void> deactivateCommandSpy(final UUID uuid) {
        return mySQLManager.update(String.format("UPDATE team SET commandSpy = '0' WHERE uuid = '%s'", uuid.toString()));
    }

    public CompletableFuture<Set<UUID>> getAllTeamUUIDs() {
        return mySQLManager.query("SELECT uuid FROM team;").thenApply(resultList -> {
            Set<UUID> uuids = new HashSet<>();
            resultList.forEach(row -> {
                String uuidString = (String) row.get("uuid");
                try {
                    UUID uuid = UUID.fromString(uuidString);
                    uuids.add(uuid);
                } catch (IllegalArgumentException e) {
                    System.err.println("Ungültige UUID in der Datenbank: " + uuidString);
                }
            });
            return uuids;
        });
    }


}
