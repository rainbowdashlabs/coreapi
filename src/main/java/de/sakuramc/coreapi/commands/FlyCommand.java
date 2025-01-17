package de.sakuramc.coreapi.commands;

import de.sakuramc.coreapi.CoreAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class FlyCommand implements CommandExecutor {

    public CoreAPI instance;
    private static final Set<Player> noFallDamagePlayers = new HashSet<>();
    private static final ArrayList<Player> flyMode = new ArrayList<>();

    public FlyCommand(final CoreAPI command) {
        this.instance = command;
        Objects.requireNonNull(this.instance.getCommand("fly")).setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(CoreAPI.getInstance().noPlayer);
            return false;
        }

        if (!player.hasPermission("system.fly")) {
            player.sendMessage(instance.noPerm);
            return false;
        }

        CoreAPI.getInstance().teamManager.playerExists(player.getUniqueId()).thenAccept(isInTeam -> {
            if (!isInTeam) {
                CoreAPI.getInstance().teamManager.addPlayerToTeam(player.getUniqueId(), 0, 0, 0, 0, 0);
            }
        });

        if (args.length == 0) {
            if (!flyMode.contains(player)) {
                flyMode.add(player);
                player.setAllowFlight(true);
                player.setFlying(true);
                player.sendMessage(CoreAPI.getInstance().prefix + "Du hast den Flugmodus §aerfolgreich §7aktiviert.");
            } else {
                flyMode.remove(player);
                noFallDamagePlayers.add(player);
                player.setAllowFlight(false);
                player.setFlying(false);
                player.sendMessage(CoreAPI.getInstance().prefix + "Du hast den Flugmodus §aerfolgreich §7deaktiviert");
                player.getServer().getScheduler().runTaskLaterAsynchronously(this.instance, () -> noFallDamagePlayers.remove(player), 100L);
            }
        } else if (args.length == 1) {
            final String name = args[0];
            final Player target = CoreAPI.getInstance().getServer().getPlayer(name);

            if (target == null) {
                player.sendMessage(CoreAPI.getInstance().prefix + "Der Spieler §3" + name + " §7ist derzeit §cnicht §7online.");
                return false;
            }

            if (!flyMode.contains(target)) {
                target.setAllowFlight(true);
                target.setFlying(true);
                target.sendMessage(CoreAPI.getInstance().prefix + "Dein Flugmodus wurde §aerfolgreich §7von dem Spieler §3" + player.getName() + " §7aktiviert.");
            } else {
                noFallDamagePlayers.add(target);
                target.setAllowFlight(false);
                target.setFlying(false);
                target.sendMessage(CoreAPI.getInstance().prefix + "Dein Flugmodus wurde §aerfolgreich §7von dem Spieler §3" + player.getName() + " §7deaktiviert.");
                target.getServer().getScheduler().runTaskLaterAsynchronously(this.instance, () -> noFallDamagePlayers.remove(target), 100L);
            }
        } else {
            player.sendMessage(instance.onUse + "fly oder fly «§aName§8»");
        }

        return true;
    }

    public static Set<Player> getNoFallDamagePlayers() {
        return noFallDamagePlayers;
    }

    public static ArrayList<Player> getFlyMode() {
        return flyMode;
    }
}
