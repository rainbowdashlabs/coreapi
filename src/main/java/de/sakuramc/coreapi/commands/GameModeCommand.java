package de.sakuramc.coreapi.commands;

import de.sakuramc.coreapi.CoreAPI;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class GameModeCommand implements CommandExecutor {

    public CoreAPI instance;

    public GameModeCommand(final CoreAPI command) {
        this.instance = command;
        Objects.requireNonNull(this.instance.getCommand("gamemode")).setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(instance.noPlayer);
            return false;
        }

        if (!player.hasPermission("system.gamemode")) {
            player.sendMessage(instance.noPerm);
            return false;
        }

        CoreAPI.getInstance().teamManager.playerExists(player.getUniqueId()).thenAccept(isInTeam -> {
            if (!isInTeam) {
                CoreAPI.getInstance().teamManager.addPlayerToTeam(player.getUniqueId(), 0, 0, 0, 0, 0);
            }
        });

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("0") || args[0].equalsIgnoreCase("survival")) {
                if (player.getGameMode() == GameMode.SURVIVAL) {
                    player.sendMessage(instance.prefix + "Du befindest dich bereits im GameMode §3" + player.getGameMode() + "§7.");
                    return false;
                }

                player.setGameMode(GameMode.SURVIVAL);
                player.sendMessage(instance.prefix + "Du wurdest in den GameMode §3" + player.getGameMode() + " §7GameMode gesetzt.");
            } else if (args[0].equalsIgnoreCase("1") || args[0].equalsIgnoreCase("creative")) {
                if (player.getGameMode() == GameMode.CREATIVE) {
                    player.sendMessage(instance.prefix + "Du befindest dich bereits im GameMode §3" + player.getGameMode() + "§7.");
                    return false;
                }

                player.setGameMode(GameMode.CREATIVE);
                player.sendMessage(instance.prefix + "Du wurdest in den GameMode §3" + player.getGameMode() + " §7GameMode gesetzt.");
            } else if (args[0].equalsIgnoreCase("2") || args[0].equalsIgnoreCase("adventure")) {
                if (player.getGameMode() == GameMode.ADVENTURE) {
                    player.sendMessage(instance.prefix + "Du befindest dich bereits im GameMode §3" + player.getGameMode() + "§7.");
                    return false;
                }

                player.setGameMode(GameMode.ADVENTURE);
                player.sendMessage(instance.prefix + "Du wurdest in den GameMode §3" + player.getGameMode() + " §7GameMode gesetzt.");
            } else if (args[0].equalsIgnoreCase("3") || args[0].equalsIgnoreCase("spectator")) {
                if (player.getGameMode() == GameMode.SPECTATOR) {
                    player.sendMessage(instance.prefix + "Du befindest dich bereits im GameMode §3" + player.getGameMode() + "§7.");
                    return false;
                }

                player.setGameMode(GameMode.SPECTATOR);
                player.sendMessage(instance.prefix + "Du wurdest in den GameMode §3" + player.getGameMode() + " §7GameMode gesetzt.");
            } else {
                player.sendMessage(instance.onUse + "gamemode §8«§a0§7, §a1§7, §a2§7, §a3§8»");
            }
        } else if (args.length == 2) {
            final String name = args[1];
            final Player target = instance.getServer().getPlayer(name);

            if (target == null) {
                sender.sendMessage(instance.prefix + "Der Spieler §3" + name + " §7ist derzeit §cnicht §7online.");
                return false;
            }

            if (name.equalsIgnoreCase(player.getName())) {
                if (args[0].equalsIgnoreCase("0") || args[0].equalsIgnoreCase("survival")) {
                    if (player.getGameMode() == GameMode.SURVIVAL) {
                        player.sendMessage(instance.prefix + "Du befindest dich bereits im GameMode §3" + player.getGameMode() + "§7.");
                        return false;
                    }

                    player.setGameMode(GameMode.SURVIVAL);
                    player.sendMessage(instance.prefix + "Du wurdest in den GameMode §3" + player.getGameMode() + " §7GameMode gesetzt.");
                } else if (args[0].equalsIgnoreCase("1") || args[0].equalsIgnoreCase("creative")) {
                    if (player.getGameMode() == GameMode.CREATIVE) {
                        player.sendMessage(instance.prefix + "Du befindest dich bereits im GameMode §3" + player.getGameMode() + "§7.");
                        return false;
                    }

                    player.setGameMode(GameMode.CREATIVE);
                    player.sendMessage(instance.prefix + "Du wurdest in den GameMode §3" + player.getGameMode() + " §7GameMode gesetzt.");
                } else if (args[0].equalsIgnoreCase("2") || args[0].equalsIgnoreCase("adventure")) {
                    if (player.getGameMode() == GameMode.ADVENTURE) {
                        player.sendMessage(instance.prefix + "Du befindest dich bereits im GameMode §3" + player.getGameMode() + "§7.");
                        return false;
                    }

                    player.setGameMode(GameMode.ADVENTURE);
                    player.sendMessage(instance.prefix + "Du wurdest in den GameMode §3" + player.getGameMode() + " §7GameMode gesetzt.");
                } else if (args[0].equalsIgnoreCase("3") || args[0].equalsIgnoreCase("spectator")) {
                    if (player.getGameMode() == GameMode.SPECTATOR) {
                        player.sendMessage(instance.prefix + "Du befindest dich bereits im GameMode §3" + player.getGameMode() + "§7.");
                        return false;
                    }

                    player.setGameMode(GameMode.SPECTATOR);
                    player.sendMessage(instance.prefix + "Du wurdest in den GameMode §3" + player.getGameMode() + " §7GameMode gesetzt.");
                } else {
                    player.sendMessage(instance.onUse + "gamemode §8«§a0§7, §a1§7, §a2§7, §a3§8»");
                }
            }

            if (args[0].equalsIgnoreCase("0") || args[0].equalsIgnoreCase("survival")) {
                if (target.getGameMode() == GameMode.SURVIVAL) {
                    target.sendMessage(instance.prefix + "Du befindest dich bereits im GameMode §3" + target.getGameMode() + "§7.");
                    return false;
                }

                target.setGameMode(GameMode.SURVIVAL);
                target.sendMessage(instance.prefix + "Du wurdest in den GameMode §3" + target.getGameMode() + " §7GameMode gesetzt.");
            } else if (args[0].equalsIgnoreCase("1") || args[0].equalsIgnoreCase("creative")) {
                if (target.getGameMode() == GameMode.CREATIVE) {
                    target.sendMessage(instance.prefix + "Du befindest dich bereits im GameMode §3" + target.getGameMode() + "§7.");
                    return false;
                }

                target.setGameMode(GameMode.CREATIVE);
                target.sendMessage(instance.prefix + "Du wurdest in den GameMode §3" + target.getGameMode() + " §7GameMode gesetzt.");
            } else if (args[0].equalsIgnoreCase("2") || args[0].equalsIgnoreCase("adventure")) {
                if (target.getGameMode() == GameMode.ADVENTURE) {
                    target.sendMessage(instance.prefix + "Du befindest dich bereits im GameMode §3" + target.getGameMode() + "§7.");
                    return false;
                }

                target.setGameMode(GameMode.ADVENTURE);
                target.sendMessage(instance.prefix + "Du wurdest in den GameMode §3" + target.getGameMode() + " §7GameMode gesetzt.");
            } else if (args[0].equalsIgnoreCase("3") || args[0].equalsIgnoreCase("spectator")) {
                if (target.getGameMode() == GameMode.SPECTATOR) {
                    target.sendMessage(instance.prefix + "Du befindest dich bereits im GameMode §3" + target.getGameMode() + "§7.");
                    return false;
                }

                target.setGameMode(GameMode.SPECTATOR);
                target.sendMessage(instance.prefix + "Du wurdest in den GameMode §3" + target.getGameMode() + " §7GameMode gesetzt.");
            } else {
                target.sendMessage(instance.onUse + "gamemode §8«§a0§7, §a1§7, §a2§7, §a3§8»");
            }
        } else {
            player.sendMessage(instance.onUse + "gamemode §8«§a0§7, §a1§7, §a2§7, §a3§8» oder /gamemode §8«§a0§7, §a1§7, §a2§7, §a3§8» §8«§aName§8»");
        }

        return true;
    }
}
