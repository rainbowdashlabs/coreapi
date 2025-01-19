package de.sakuramc.coreapi.manager;

import de.sakuramc.coreapi.mysql.MySQLManager;
import de.sakuramc.coreapi.utils.clan.Clan;
import de.sakuramc.coreapi.utils.clan.ClanRank;
import de.sakuramc.coreapi.utils.clan.ClanMember;
import de.sakuramc.coreapi.utils.clan.ClanChatMessage;
import de.sakuramc.coreapi.utils.clan.ClanBank;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class ClanManager {

    private final MySQLManager mySQLManager;

    public ClanManager(final MySQLManager mySQLManager) {
        this.mySQLManager = mySQLManager;
    }

    // =======================
    // Clan-Verwaltung
    // =======================

    /**
     * Erstellt einen neuen Clan.
     *
     * @param clan Der Clan, der erstellt werden soll.
     * @return CompletableFuture<Void>
     */
    public CompletableFuture<Void> createClan(Clan clan) {
        String query = "INSERT INTO clans(name, tag) VALUES (?, ?)";
        return mySQLManager.update(query, clan.getName(), clan.getTag())
                .thenCompose(aVoid -> getClanByName(clan.getName()))
                .thenCompose(createdClan -> {
                    if (createdClan != null) {
                        return initializeClanBank(createdClan.getClanId());
                    }
                    return CompletableFuture.failedFuture(new RuntimeException("Clan creation failed"));
                });
    }


    /**
     * Initialisiert das Bankkonto für einen neuen Clan.
     *
     * @param clanId Die ID des Clans.
     * @return CompletableFuture<Void>
     */
    private CompletableFuture<Void> initializeClanBank(int clanId) {
        String query = "INSERT INTO clan_bank(clan_id, balance) VALUES (?, 0.00)";
        return mySQLManager.update(query, clanId);
    }

    /**
     * Holt einen Clan anhand seiner ID.
     *
     * @param clanId Die ID des Clans.
     * @return CompletableFuture<Clan>
     */
    public CompletableFuture<Clan> getClanById(int clanId) {
        String query = "SELECT * FROM clans WHERE clan_id = ?";
        return mySQLManager.query(query, clanId).thenApply(results -> {
            if (results.isEmpty()) return null;
            Map<String, Object> row = results.get(0);
            Clan clan = new Clan();
            clan.setClanId((int) row.get("clan_id"));
            clan.setName((String) row.get("name"));
            clan.setTag((String) row.get("tag"));
            clan.setCreationDate((Timestamp) row.get("creation_date"));
            return clan;
        });
    }

    /**
     * Holt einen Clan anhand seines Namens.
     *
     * @param name Der Name des Clans.
     * @return CompletableFuture<Clan>
     */
    public CompletableFuture<Clan> getClanByName(String name) {
        String query = "SELECT * FROM clans WHERE name = ?";
        return mySQLManager.query(query, name).thenApply(results -> {
            if (results.isEmpty()) return null;
            Map<String, Object> row = results.get(0);
            Clan clan = new Clan();
            clan.setClanId((int) row.get("clan_id"));
            clan.setName((String) row.get("name"));
            clan.setTag((String) row.get("tag"));
            clan.setCreationDate((Timestamp) row.get("creation_date"));
            return clan;
        });
    }

    /**
     * Aktualisiert die Informationen eines Clans.
     *
     * @param clan Der Clan mit aktualisierten Informationen.
     * @return CompletableFuture<Void>
     */
    public CompletableFuture<Void> updateClan(Clan clan) {
        String query = "UPDATE clans SET name = ?, tag = ? WHERE clan_id = ?";
        return mySQLManager.update(query, clan.getName(), clan.getTag(), clan.getClanId());
    }

    /**
     * Löscht einen Clan anhand seiner ID.
     *
     * @param clanId Die ID des Clans.
     * @return CompletableFuture<Void>
     */
    public CompletableFuture<Void> deleteClan(int clanId) {
        String query = "DELETE FROM clans WHERE clan_id = ?";
        return mySQLManager.update(query, clanId);
    }

    // =======================
    // Rang- und Berechtigungsmanagement
    // =======================

    /**
     * Erstellt einen neuen Rang für einen Clan.
     *
     * @param clanId     Die ID des Clans.
     * @param clanRank   Der Rang, der erstellt werden soll.
     * @return CompletableFuture<Void>
     */
    public CompletableFuture<Void> createRank(int clanId, ClanRank clanRank) {
        String query = "INSERT INTO clan_ranks(clan_id, rank_name, permissions) VALUES (?, ?, ?)";
        return mySQLManager.update(query, clanId, clanRank.getRankName(), clanRank.getPermissions())
                .thenRun(() -> {
                    // Optional: Weitere Aktionen nach der Erstellung eines Rangs
                });
    }

    /**
     * Holt alle Ränge eines Clans.
     *
     * @param clanId Die ID des Clans.
     * @return CompletableFuture<List<ClanRank>>
     */
    public CompletableFuture<List<ClanRank>> getRanks(int clanId) {
        String query = "SELECT * FROM clan_ranks WHERE clan_id = ?";
        return mySQLManager.query(query, clanId).thenApply(results -> {
            List<ClanRank> ranks = new ArrayList<>();
            for (Map<String, Object> row : results) {
                ClanRank rank = new ClanRank();
                rank.setRankId((int) row.get("rank_id"));
                rank.setClanId((int) row.get("clan_id"));
                rank.setRankName((String) row.get("rank_name"));
                rank.setPermissions((String) row.get("permissions"));
                ranks.add(rank);
            }
            return ranks;
        });
    }

    /**
     * Aktualisiert einen Rang.
     *
     * @param clanRank Der Rang mit aktualisierten Informationen.
     * @return CompletableFuture<Void>
     */
    public CompletableFuture<Void> updateRank(ClanRank clanRank) {
        String query = "UPDATE clan_ranks SET rank_name = ?, permissions = ? WHERE rank_id = ?";
        return mySQLManager.update(query, clanRank.getRankName(), clanRank.getPermissions(), clanRank.getRankId());
    }

    /**
     * Löscht einen Rang anhand seiner ID.
     *
     * @param rankId Die ID des Rangs.
     * @return CompletableFuture<Void>
     */
    public CompletableFuture<Void> deleteRank(int rankId) {
        String query = "DELETE FROM clan_ranks WHERE rank_id = ?";
        return mySQLManager.update(query, rankId);
    }

    // =======================
    // Mitgliederverwaltung
    // =======================

    /**
     * Fügt ein Mitglied zu einem Clan hinzu.
     *
     * @param clanId      Die ID des Clans.
     * @param playerUuid  Die UUID des Spielers.
     * @param rankId      Die ID des Rangs.
     * @return CompletableFuture<Void>
     */
    public CompletableFuture<Void> addMember(int clanId, String playerUuid, int rankId) {
        String query = "INSERT INTO clan_members(player_uuid, clan_id, rank_id) VALUES (?, ?, ?)";
        return mySQLManager.update(query, playerUuid, clanId, rankId);
    }

    /**
     * Entfernt ein Mitglied aus einem Clan.
     *
     * @param playerUuid Die UUID des Spielers.
     * @return CompletableFuture<Void>
     */
    public CompletableFuture<Void> removeMember(String playerUuid) {
        String query = "UPDATE clan_members SET clan_id = NULL, rank_id = NULL WHERE player_uuid = ?";
        return mySQLManager.update(query, playerUuid);
    }

    /**
     * Befördert ein Mitglied zu einem höheren Rang.
     *
     * @param playerUuid Die UUID des Spielers.
     * @param newRankId  Die ID des neuen Rangs.
     * @return CompletableFuture<Void>
     */
    public CompletableFuture<Void> promoteMember(String playerUuid, int newRankId) {
        String query = "UPDATE clan_members SET rank_id = ? WHERE player_uuid = ?";
        return mySQLManager.update(query, newRankId, playerUuid);
    }

    /**
     * Demotiert ein Mitglied zu einem niedrigeren Rang.
     *
     * @param playerUuid Die UUID des Spielers.
     * @param newRankId  Die ID des neuen Rangs.
     * @return CompletableFuture<Void>
     */
    public CompletableFuture<Void> demoteMember(String playerUuid, int newRankId) {
        String query = "UPDATE clan_members SET rank_id = ? WHERE player_uuid = ?";
        return mySQLManager.update(query, newRankId, playerUuid);
    }

    /**
     * Holt alle Mitglieder eines Clans.
     *
     * @param clanId Die ID des Clans.
     * @return CompletableFuture<List<ClanMember>>
     */
    public CompletableFuture<List<ClanMember>> getMembers(int clanId) {
        String query = "SELECT * FROM clan_members WHERE clan_id = ?";
        return mySQLManager.query(query, clanId).thenApply(results -> {
            List<ClanMember> members = new ArrayList<>();
            for (Map<String, Object> row : results) {
                ClanMember member = new ClanMember();
                member.setMemberId((int) row.get("member_id"));
                member.setPlayerUuid((String) row.get("player_uuid"));
                member.setClanId((Integer) row.get("clan_id"));
                member.setRankId((Integer) row.get("rank_id"));
                member.setJoinDate((Timestamp) row.get("join_date"));
                members.add(member);
            }
            return members;
        });
    }

    // =======================
    // Clan-Chat
    // =======================

    /**
     * Fügt eine Chatnachricht zu einem Clan hinzu.
     *
     * @param clanId    Die ID des Clans.
     * @param memberId  Die ID des Mitglieds, das die Nachricht sendet.
     * @param message   Die Nachricht.
     * @return CompletableFuture<Void>
     */
    public CompletableFuture<Void> addChatMessage(int clanId, int memberId, String message) {
        String query = "INSERT INTO clan_chat(clan_id, member_id, message) VALUES (?, ?, ?)";
        return mySQLManager.update(query, clanId, memberId, message);
    }

    /**
     * Holt die letzten n Chatnachrichten eines Clans.
     *
     * @param clanId Die ID des Clans.
     * @param limit  Die maximale Anzahl der Nachrichten.
     * @return CompletableFuture<List<ClanChatMessage>>
     */
    public CompletableFuture<List<ClanChatMessage>> getChatMessages(int clanId, int limit) {
        String query = "SELECT * FROM clan_chat WHERE clan_id = ? ORDER BY timestamp DESC LIMIT ?";
        return mySQLManager.query(query, clanId, limit).thenApply(results -> {
            List<ClanChatMessage> messages = new ArrayList<>();
            for (Map<String, Object> row : results) {
                ClanChatMessage chatMessage = new ClanChatMessage();
                chatMessage.setMessageId((int) row.get("message_id"));
                chatMessage.setClanId((int) row.get("clan_id"));
                chatMessage.setMemberId((int) row.get("member_id"));
                chatMessage.setMessage((String) row.get("message"));
                chatMessage.setTimestamp((Timestamp) row.get("timestamp"));
                messages.add(chatMessage);
            }
            return messages;
        });
    }

    // =======================
    // Clan-Bank
    // =======================

    /**
     * Holt den aktuellen Kontostand eines Clans.
     *
     * @param clanId Die ID des Clans.
     * @return CompletableFuture<Double>
     */
    public CompletableFuture<Double> getClanBalance(int clanId) {
        String query = "SELECT balance FROM clan_bank WHERE clan_id = ?";
        return mySQLManager.query(query, clanId).thenApply(results -> {
            if (results.isEmpty()) return 0.0;
            return (Double) results.get(0).get("balance");
        });
    }

    /**
     * Fügt einen Betrag zum Kontostand eines Clans hinzu.
     *
     * @param clanId Der Clan, dem der Betrag hinzugefügt werden soll.
     * @param amount Der Betrag.
     * @return CompletableFuture<Void>
     */
    public CompletableFuture<Void> addToBalance(int clanId, double amount) {
        String query = "UPDATE clan_bank SET balance = balance + ? WHERE clan_id = ?";
        return mySQLManager.update(query, amount, clanId);
    }

    /**
     * Zieht einen Betrag vom Kontostand eines Clans ab.
     *
     * @param clanId Der Clan, von dem der Betrag abgezogen werden soll.
     * @param amount Der Betrag.
     * @return CompletableFuture<Void>
     */
    public CompletableFuture<Void> subtractFromBalance(int clanId, double amount) {
        String query = "UPDATE clan_bank SET balance = balance - ? WHERE clan_id = ?";
        return mySQLManager.update(query, amount, clanId);
    }

    // =======================
    // Zusätzliche Hilfsmethoden
    // =======================

    /**
     * Holt den Clan eines Spielers anhand seiner UUID.
     *
     * @param playerUuid Die UUID des Spielers.
     * @return CompletableFuture<Clan>
     */
    public CompletableFuture<Clan> getClanByPlayer(String playerUuid) {
        String query = "SELECT c.* FROM clans c JOIN clan_members cm ON c.clan_id = cm.clan_id WHERE cm.player_uuid = ?";
        return mySQLManager.query(query, playerUuid).thenApply(results -> {
            if (results.isEmpty()) return null;
            Map<String, Object> row = results.get(0);
            Clan clan = new Clan();
            clan.setClanId((int) row.get("clan_id"));
            clan.setName((String) row.get("name"));
            clan.setTag((String) row.get("tag"));
            clan.setCreationDate((Timestamp) row.get("creation_date"));
            return clan;
        });
    }

    /**
     * Prüft, ob ein Spieler Mitglied eines Clans ist.
     *
     * @param playerUuid Die UUID des Spielers.
     * @return CompletableFuture<Boolean>
     */
    public CompletableFuture<Boolean> isMember(String playerUuid) {
        String query = "SELECT * FROM clan_members WHERE player_uuid = ?";
        return mySQLManager.query(query, playerUuid).thenApply(results -> !results.isEmpty());
    }
}
