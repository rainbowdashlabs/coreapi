package de.sakuramc.coreapi.commands;

import de.sakuramc.coreapi.CoreAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class FriendCommand implements CommandExecutor {

    public CoreAPI instance;

    public FriendCommand(final CoreAPI command) {
        this.instance = command;
        Objects.requireNonNull(this.instance.getCommand("friend")).setExecutor(this);
    }

    @SuppressWarnings({"CallToPrintStackTrace"})
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(instance.noPlayer);
            return false;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                //this.instance.friendGUI.openFriendList(player, 1);
            } else {
                player.sendMessage(CoreAPI.getInstance().onUse + "friend list");
            }
        } else if (args.length == 2) {
            final String name = args[1];

            if (args[0].equalsIgnoreCase("add")) {
                /*ICommunicationPromise<ICloudPlayer> cloudPlayerPromise = CloudAPI.getInstance().getCloudPlayerManager().getCloudPlayer(name);

                CompletableFuture<Void> future = new CompletableFuture<>();

                cloudPlayerPromise.addResultListener(cloudPlayer -> {
                    if (cloudPlayer != null) {
                        UUID uuid = cloudPlayer.getUniqueId();

                        // Jetzt kannst du mit der UUID weiterarbeiten
                        CoreAPI.getInstance().playerManager.playerExists(uuid).thenCompose(playerExists -> {
                            if (!playerExists) {
                                player.sendMessage(CoreAPI.getInstance().prefix + "Der Spieler §3" + name + " §7war noch §cnie §7auf diesem Netzwerk.");
                                return CompletableFuture.completedFuture(null);
                            }

                            return CoreAPI.getInstance().playerManager.areFriends(player.getUniqueId(), uuid).thenCompose(areFriends -> {
                                if (areFriends) {
                                    player.sendMessage(CoreAPI.getInstance().prefix + "Du bist bereits mit dem Spieler §3" + name + " §7befreundet.");
                                    return CompletableFuture.completedFuture(null);
                                }

                                return this.instance.playerManager.sendFriendRequest(player.getUniqueId(), uuid).thenCompose(v -> {
                                    player.sendMessage(CoreAPI.getInstance().prefix + "Du hast dem Spieler §3" + name + " §7eine Freundschaftsanfrage geschickt.");
                                    cloudPlayer.sendMessage(CoreAPI.getInstance().prefix + "Du hast eine Freundschaftsanfrage von §3" + player.getName() + " §7erhalten.");
                                    cloudPlayer.sendMessage(CoreAPI.getInstance().prefix + "Verwende: /friend accept " + player.getName());
                                    cloudPlayer.sendMessage(CoreAPI.getInstance().prefix + "Verwende: /friend deny " + player.getName());
                                    return CompletableFuture.completedFuture(null);
                                });
                            });
                        }).exceptionally(ex -> {
                            ex.printStackTrace();
                            return null;
                        });
                    } else {
                        // Spieler wurde nicht gefunden
                        player.sendMessage(CoreAPI.getInstance().prefix + "Der Spieler §3" + name + " §7konnte nicht gefunden werden.");
                    }
                    future.complete(null);
                    return null;
                }).addFailureListener(ex -> {
                    ex.printStackTrace();
                    player.sendMessage(CoreAPI.getInstance().prefix + "Fehler beim Abrufen des Spielers §3" + name + "§7.");
                    future.completeExceptionally(ex);
                    return null;
                });
            } else if (args[0].equalsIgnoreCase("remove")) {
                CoreAPI.getInstance().uuidFetcher.getUUID(name).thenAccept(optionalUUID -> {
                    if (optionalUUID.isPresent()) {
                        String uuid = optionalUUID.get();

                        CoreAPI.getInstance().playerManager.areFriends(player.getUniqueId(), UUID.fromString(uuid)).thenAccept(areFriends -> {
                            if (!areFriends) {
                                player.sendMessage(CoreAPI.getInstance().prefix + "Du bist mit dem Spieler §3" + name + " §7aktuell §cnicht §7befreundet.");
                                return;
                            }

                            CoreAPI.getInstance().playerManager.removeFriend(player.getUniqueId(), UUID.fromString(uuid)).thenRun(() -> {
                                player.sendMessage(CoreAPI.getInstance().prefix + "Du hast §aerfolgreich §7den Spieler §3" + name + " §7als Freund entfernt.");
                            });
                        });
                    }
                });
            } else if (args[0].equalsIgnoreCase("accept")) {
                ICommunicationPromise<ICloudPlayer> cloudPlayerPromise = CloudAPI.getInstance().getCloudPlayerManager().getCloudPlayer(name);

                CompletableFuture<Void> future = new CompletableFuture<>();

                cloudPlayerPromise.addResultListener(cloudPlayer -> {
                    if (cloudPlayer != null) {
                        UUID uuid = cloudPlayer.getUniqueId();

                        CoreAPI.getInstance().playerManager.hasPendingFriendRequest(uuid, player.getUniqueId()).thenAccept(hasPendingFriendRequest -> {
                            if (!hasPendingFriendRequest) {
                                player.sendMessage(CoreAPI.getInstance().prefix + "Der Spieler §3" + name + " §7hat dir §ckeine §7Freundschaftsanfrage geschickt.");
                                return;
                            }

                            CoreAPI.getInstance().playerManager.acceptFriendRequest(uuid, player.getUniqueId()).thenRun(() -> {
                                player.sendMessage(CoreAPI.getInstance().prefix + "Du hast §aerfolgreich §7die Freundschaftsanfrage von dem Spieler §3" + name + " §7angenommen.");
                                cloudPlayer.sendMessage(CoreAPI.getInstance().prefix + "Der Spieler §3" + player.getName() + " §7hat deine Freundschaftsanfrage angenommen.");
                            });
                        });
                    }
                    future.complete(null);
                    return null;
                }).addFailureListener(ex -> {
                    ex.printStackTrace();
                    player.sendMessage(CoreAPI.getInstance().prefix + "Fehler beim Abrufen des Spielers §3" + name);
                    future.completeExceptionally(ex);
                    return null;
                });
            } else if (args[0].equalsIgnoreCase("deny")) {
                ICommunicationPromise<ICloudPlayer> cloudPlayerPromise = CloudAPI.getInstance().getCloudPlayerManager().getCloudPlayer(name);

                CompletableFuture<Void> future = new CompletableFuture<>();

                cloudPlayerPromise.addResultListener(cloudPlayer -> {
                    if (cloudPlayer != null) {
                        UUID uuid = cloudPlayer.getUniqueId();

                        CoreAPI.getInstance().playerManager.hasPendingFriendRequest(uuid, player.getUniqueId()).thenAccept(hasPendingFriendRequest -> {
                            if (!hasPendingFriendRequest) {
                                player.sendMessage(CoreAPI.getInstance().prefix + "Der Spieler §3" + name + " §7hat dir §ckeine §7Freundschaftsanfrage geschickt.");
                                return;
                            }

                            CoreAPI.getInstance().playerManager.denyFriendRequest(uuid, player.getUniqueId()).thenRun(() -> {
                                player.sendMessage(CoreAPI.getInstance().prefix + "Du hast §aerfolgreich §7die Freundschaftsanfrage von dem Spieler §3" + name + " §7abgelehnt.");
                                cloudPlayer.sendMessage(CoreAPI.getInstance().prefix + "Der Spieler §3" + player.getName() + " §7hat deine Freundschaftsanfrage abgelehnt.");
                            });
                        });
                    }
                    future.complete(null);
                    return null;
                }).addFailureListener(ex -> {
                    ex.printStackTrace();
                    player.sendMessage(CoreAPI.getInstance().prefix + "Fehler beim Abrufen des Spielers §3" + name);
                    future.completeExceptionally(ex);
                    return null;
                });*/
            } else {
                player.sendMessage(CoreAPI.getInstance().onUse + "friend §8«§alist§8» oder /friend §8«§aadd§7, §aremove§7, §aaccept§7, §adeny§8» «§aName§8»");
            }
        } else {
            player.sendMessage(CoreAPI.getInstance().onUse + "friend §8«§alist§8» oder /friend §8«§aadd§7, §aremove§7, §aaccept§7, §adeny§8» «§aName§8»");
        }

        return true;
    }
}
