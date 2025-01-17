package de.sakuramc.coreapi.commands;

import de.sakuramc.coreapi.CoreAPI;
import de.sakuramc.coreapi.utils.clan.Clan;
import de.sakuramc.coreapi.utils.clan.ClanRank;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.Optional;

public class ClanCommand implements CommandExecutor {

    public CoreAPI instance;

    public ClanCommand(final CoreAPI command) {
        this.instance = command;
        Objects.requireNonNull(this.instance.getCommand("clan")).setExecutor(this);
    }

    @SuppressWarnings("CallToPrintStackTrace")
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(this.instance.noPlayer);
            return false;
        }

        if (args.length == 4) {
            if (args[0].equalsIgnoreCase("create")) {
                CoreAPI.getInstance().playerManager.getCoins(player.getUniqueId()).thenAccept(coins -> {
                    if (coins < 2000) {
                        player.sendMessage(CoreAPI.getInstance().prefix + "Du hast §cnicht §7genügend Coins um einen Clan zu erstellen! Du brauchst noch §e" + (2000 - coins) + "§7!");
                        return;
                    }

                    CoreAPI.getInstance().clanManager.isMember(player.getUniqueId().toString()).thenAccept(isPlayerInClan -> {
                        if (isPlayerInClan) {
                            player.sendMessage(CoreAPI.getInstance().prefix + "Du befindest dich bereits in einem Clan.");
                            return;
                        }

                        final String name = args[1];
                        final String tag = args[2];
                        final String leaderRoleName = args[3];

                        CoreAPI.getInstance().clanManager.getClanByName(name).thenAccept(existsClanName -> {
                            if (existsClanName != null) {
                                player.sendMessage(CoreAPI.getInstance().prefix + "Es wurde bereits ein Clan mit den Name §3" + name + " §7gefunden!");
                                return;
                            }

                            if (name.length() > 16) {
                                player.sendMessage(CoreAPI.getInstance().prefix + "Der Name deines Clans darf §cnicht §7länger als wie §e16 §7Zeichen sein.");
                                return;
                            }

                            if (name.length() < 4) {
                                player.sendMessage(CoreAPI.getInstance().prefix + "Der Name deines Clans darf §cnicht §7kürzer als wie §e5 §7Zeichen sein.");
                                return;
                            }

                            if (tag.length() > 4) {
                                player.sendMessage(CoreAPI.getInstance().prefix + "Der Tag deines Clans darf §cnicht §7länger als wie 4 Zeichen sein.");
                                return;
                            }

                            if (tag.length() < 2) {
                                player.sendMessage(CoreAPI.getInstance().prefix + "Der Tag deines Clans darf §cnicht §7kürzer als wie 2 Zeichen sein.");
                                return;
                            }

                            Clan clan = new Clan();

                            clan.setName(name);
                            clan.setTag(tag);
                            clan.setCreationDate(new Timestamp(System.currentTimeMillis()));

                            CoreAPI.getInstance().clanManager.createClan(clan).thenRun(() -> {
                                CoreAPI.getInstance().clanManager.getClanByName(name).thenAccept(createdClan -> {
                                    if (createdClan == null) {
                                        player.sendMessage(CoreAPI.getInstance().prefix + "Es ist ein Fehler aufgetreten beim Erstellen des Clans.");
                                        return;
                                    }

                                    int clanId = createdClan.getClanId();

                                    ClanRank leaderRank = new ClanRank();
                                    leaderRank.setClanId(clanId);
                                    leaderRank.setRankName(leaderRoleName);
                                    leaderRank.setPermissions("{\"permission\": \"all\"}");

                                    CoreAPI.getInstance().clanManager.createRank(clanId, leaderRank).thenRun(() -> {
                                        CoreAPI.getInstance().clanManager.getRanks(clanId).thenAccept(ranks -> {
                                            Optional<ClanRank> leaderRankOpt = ranks.stream().filter(rank -> rank.getRankName().equalsIgnoreCase(leaderRoleName)).findFirst();

                                            if (!leaderRankOpt.isPresent()) {
                                                player.sendMessage(CoreAPI.getInstance().prefix + "Fehler beim Erstellen beim Leader-Rang.");
                                                return;
                                            }

                                            int rankId = leaderRankOpt.get().getRankId();

                                            CoreAPI.getInstance().clanManager.addMember(clanId, player.getUniqueId().toString(), rankId).thenRun(() -> {
                                                 player.sendMessage("§7=============== §8[§d§lSakuraMC§8] §7===============");
                                                 player.sendMessage(" ");
                                                 player.sendMessage("§8┃ §7Name §8» §3" + name);
                                                 player.sendMessage("§8┃ §7Tag §8» §3" + tag);
                                                 player.sendMessage("§8┃ §7Inhaber §8» §3" + player.getName());
                                                 player.sendMessage("§8┃ §7Inhaber Rolenname §8» §3" + leaderRoleName);
                                                 player.sendMessage(" ");
                                                 player.sendMessage("§7=============== §8[§d§lSakuraMC§8] §7===============");
                                                 CoreAPI.getInstance().playerManager.removeCoins(player.getUniqueId(), 2000);
                                            }).exceptionally(ex -> {
                                                ex.printStackTrace();
                                                player.sendMessage(CoreAPI.getInstance().prefix + "Es ist ein Fehler beim hinzufügen zum Clan.");
                                                return null;
                                            });
                                        }).exceptionally(ex -> {
                                            ex.printStackTrace();
                                            player.sendMessage(CoreAPI.getInstance().prefix + "Es ist ein Fehler aufgetreten beim Laden der Ränge.");
                                            return null;
                                        });
                                    }).exceptionally(ex -> {
                                        ex.printStackTrace();
                                        player.sendMessage(CoreAPI.getInstance().prefix + "Es ist ein Fehler aufgetreten beim erstellen des Rangs.");
                                        return null;
                                    });
                                }).exceptionally(ex -> {
                                    ex.printStackTrace();
                                    player.sendMessage(CoreAPI.getInstance().prefix + "Es ist ein Fehler aufgetreten beim überprüfen des Clan Namens.");
                                    return null;
                                });
                            }).exceptionally(ex -> {
                                ex.printStackTrace();
                                player.sendMessage(CoreAPI.getInstance().prefix + "Es ist ein Fehler aufgetreten beim erstellen vom Clan.");
                                return null;
                            });
                        });
                    });
                });
            }
        }

        return true;
    }
}
