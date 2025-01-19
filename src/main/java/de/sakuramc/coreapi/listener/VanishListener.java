package de.sakuramc.coreapi.listener;

import de.sakuramc.coreapi.CoreAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Objects;

public class VanishListener implements PluginMessageListener {

    @SuppressWarnings("CallToPrintStackTrace")
    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] message) {
        if (!channel.equalsIgnoreCase("sakuramc:vanish")) return;

        try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(message))) {
            String subChannel = in.readUTF();
            if (subChannel.equals("SetVanishAndTeleport")) {
                String executingPlayerName = in.readUTF();
                String targetPlayerName = in.readUTF();
                String sender = in.readUTF();
                Player executingPlayer = Bukkit.getPlayerExact(executingPlayerName);
                Player targetPlayer = Bukkit.getPlayerExact(targetPlayerName);

                if (executingPlayer != null && targetPlayer != null) {
                    teleportPlayerToTarget(executingPlayer, targetPlayer);
                    setVanish(executingPlayer);

                    sendAcknowledgment(sender, executingPlayerName, targetPlayerName);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setVanish(Player executingPlayer) {

    }

    private void teleportPlayerToTarget(Player executingPlayer, Player targetPlayer) {

    }

    @SuppressWarnings("CallToPrintStackTrace")
    private void sendAcknowledgment(final String sender, final String executingPlayerName, final String targetPlayer) {
        final Player player = CoreAPI.getInstance().getServer().getPlayer(sender);
        Objects.requireNonNull(player).sendMessage(CoreAPI.getInstance().prefix + "Du wurdest §aerfolgreich §7zu dem Spieler §3" + targetPlayer + " §7auf den Server §3" + player.getServer().getName() + " §7teleportiert und in den Vanish gesetzt.");
    }

}
