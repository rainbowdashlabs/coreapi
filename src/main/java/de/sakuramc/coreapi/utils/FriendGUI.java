package de.sakuramc.coreapi.utils;

import de.sakuramc.coreapi.CoreAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class FriendGUI {

    private static final int ITEMS_PER_PAGE = 45;
    private final Map<UUID, Integer> playerPages = new ConcurrentHashMap<>();

    private CoreAPI instance;

    public FriendGUI(final CoreAPI instance) {
        this.instance = instance;
    }

    @SuppressWarnings("deprecation")
    /*public void openFriendList(Player player, int page) {
        this.instance.playerManager.getFriendsFromDatabase(player.getUniqueId()).thenAccept(friends -> {
            List<CompletableFuture<FriendInfo>> futures = friends.stream()
                    .map(this::fetchFriendInfo)
                    .collect(Collectors.toList());

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).thenRun(() -> {
                List<FriendInfo> friendInfos = futures.stream()
                        .map(CompletableFuture::join)
                        .peek(info -> Bukkit.getLogger().info("Sortiert: " + info.getPlayerName() + " - Online: " + info.isOnline()))
                        .sorted(Comparator
                                .comparing(FriendInfo::isOnline)
                                .reversed()
                                .thenComparing(FriendInfo::getPlayerName, String.CASE_INSENSITIVE_ORDER))
                        .collect(Collectors.toList());

                // Zusätzliche Debugging-Ausgabe
                Bukkit.getLogger().info("Total Friends: " + friendInfos.size());

                int totalPages = (int) Math.ceil((double) friendInfos.size() / ITEMS_PER_PAGE);
                int currentPage = Math.max(1, Math.min(page, totalPages)); // Neue Variable
                playerPages.put(player.getUniqueId(), currentPage);

                int startIndex = (currentPage - 1) * ITEMS_PER_PAGE;
                int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, friendInfos.size());

                List<FriendInfo> pageItems = friendInfos.subList(startIndex, endIndex);

                Bukkit.getScheduler().runTask(instance, () -> {
                    Inventory inv = Bukkit.createInventory(null, 54, "§8┃ §7Deine §aFreunde §7(Seite §a" + currentPage + "§7/§c" + totalPages + "§7)");
                    for (FriendInfo info : pageItems) {
                        addFriendItem(inv, info);
                    }
                    if (currentPage < totalPages) {
                        inv.setItem(53, createNavigationItem("§aWeiter", Material.PAPER));
                    }
                    if (currentPage > 1) {
                        inv.setItem(45, createNavigationItem("§cZurück", Material.PAPER));
                    }
                    player.openInventory(inv);
                });
            });
        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }*/

    public int getCurrentPage(Player player) {
        return playerPages.getOrDefault(player.getUniqueId(), 1);
    }

    /*private CompletableFuture<FriendInfo> fetchFriendInfo(UUID friendUUID) {
        ICloudPlayerManager cloudPlayerManager = CloudAPI.getInstance().getCloudPlayerManager();
        ICommunicationPromise<ICloudPlayer> promise = cloudPlayerManager.getCloudPlayer(friendUUID);

        CompletableFuture<ICloudPlayer> future = new CompletableFuture<>();
        promise.addListener(result -> {
            if (result.isSuccess()) {
                future.complete((ICloudPlayer) result.get());
            } else {
                future.complete(null);
            }
        });

        return future.thenCompose(cloudPlayer -> {
            if (cloudPlayer != null && cloudPlayer.isOnline()) {
                Bukkit.getLogger().info("Friend online: " + cloudPlayer.getName());
                return CompletableFuture.completedFuture(new FriendInfo(
                        friendUUID,
                        cloudPlayer.getName(),
                        cloudPlayer.getConnectedServerName(),
                        "§8┃ §aONLINE",
                        true
                ));
            } else {
                return this.instance.playerManager.getLastSeenFromDatabase(friendUUID).thenCompose(lastSeenDate -> {
                    String lastSeen = "§8┃ §7Zuletzt online am " + lastSeenDate;
                    return getPlayerName(friendUUID).thenApply(playerName -> new FriendInfo(
                            friendUUID,
                            playerName,
                            "§cOffline",
                            lastSeen,
                            false
                    ));
                }).exceptionally(ex -> {
                    ex.printStackTrace();
                    return new FriendInfo(friendUUID, "Unknown", "§cOffline", "§8┃ §7Unbekannt", false);
                });
            }
        });
    }*/

    private void addFriendItem(Inventory inv, FriendInfo info) {
        ItemStack head = new ItemStack(info.isOnline() ? Material.PLAYER_HEAD : Material.ZOMBIE_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setDisplayName("§3" + info.getPlayerName());
        meta.setLore(Arrays.asList("§8┃ §7Server: §3" + info.getServerName(), info.getLastSeen()));

        if (info.isOnline()) {
            PlayerProfile profile = Bukkit.createPlayerProfile(info.getUuid(), info.getPlayerName());
            profile.update().thenAcceptAsync(updatedProfile -> {
                meta.setOwnerProfile(updatedProfile);
                head.setItemMeta(meta);
                Bukkit.getScheduler().runTask(instance, () -> inv.addItem(head));
            }, runnable -> Bukkit.getScheduler().runTask(instance, runnable));
        } else {
            head.setItemMeta(meta);
            Bukkit.getScheduler().runTask(instance, () -> inv.addItem(head));
        }
    }

    @SuppressWarnings("deprecation")
    private ItemStack createNavigationItem(String name, Material material) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }

    private CompletableFuture<String> getPlayerName(UUID playerUUID) {
        String playerName = Bukkit.getOfflinePlayer(playerUUID).getName();
        if (playerName != null) {
            return CompletableFuture.completedFuture(playerName);
        } else {
            return this.instance.uuidFetcher.getName(playerUUID).thenApply(optionalName -> optionalName.orElse("Unknown"));
        }
    }

    /**
     * Klasse zur Speicherung der Freundezahlen.
     */
    public static class FriendCounts {
        private final int online;
        private final int total;

        public FriendCounts(int online, int total) {
            this.online = online;
            this.total = total;
        }

        public int getOnline() {
            return online;
        }

        public int getTotal() {
            return total;
        }
    }

    /**
     * Innere Klasse zur Speicherung von Freundinformationen.
     */
    private static class FriendInfo {
        private final UUID uuid;
        private final String playerName;
        private final String serverName;
        private final String lastSeen;
        private final boolean online;

        public FriendInfo(UUID uuid, String playerName, String serverName, String lastSeen, boolean online) {
            this.uuid = uuid;
            this.playerName = playerName;
            this.serverName = serverName;
            this.lastSeen = lastSeen;
            this.online = online;
        }

        public UUID getUuid() {
            return uuid;
        }

        public String getPlayerName() {
            return playerName;
        }

        public String getServerName() {
            return serverName;
        }

        public String getLastSeen() {
            return lastSeen;
        }

        public boolean isOnline() {
            return online;
        }
    }
}
