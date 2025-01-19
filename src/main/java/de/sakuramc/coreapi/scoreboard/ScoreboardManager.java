package de.sakuramc.coreapi.scoreboard;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import de.sakuramc.coreapi.CoreAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class ScoreboardManager implements IScoreboardManager {

    private final CoreAPI instance;
    private final ProtocolManager protocolManager;
    private final Map<Player, ScoreboardBuilder.Scoreboard> activeScoreboards = new HashMap<>();

    public ScoreboardManager(CoreAPI plugin) {
        this.instance = plugin;
        this.protocolManager = ProtocolLibrary.getProtocolManager();

        // Event Listener für Scoreboard-Pakete hinzufügen (optional)
        protocolManager.addPacketListener(new PacketAdapter(instance, PacketType.Play.Server.SCOREBOARD_OBJECTIVE, PacketType.Play.Server.SCOREBOARD_SCORE) {
            @Override
            public void onPacketSending(PacketEvent event) {
                // Handle packets if necessary
            }

            @Override
            public void onPacketReceiving(PacketEvent event) {
                // Handle packets if necessary
            }
        });
    }

    @Override
    public void sendScoreboard(Player player, ScoreboardBuilder.Scoreboard scoreboard) {
        activeScoreboards.put(player, scoreboard);
        sendTitle(player, scoreboard.getTitle());
        sendLines(player, scoreboard.getLines());
    }

    @Override
    public void removeScoreboard(Player player) {
        // Entferne alle Zeilen
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.SCOREBOARD_OBJECTIVE);
        packet.getStrings().write(0, "sidebar"); // Objective name
        packet.getStrings().write(1, ""); // Display name (empty to remove)
        packet.getIntegers().write(0, 1); // Action: remove (1 für remove, 0 für create/update)

        try {
            protocolManager.sendServerPacket(player, packet);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        // Entferne das Scoreboard aus der aktiven Liste
        activeScoreboards.remove(player);
    }

    private void sendTitle(Player player, String title) {
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.SCOREBOARD_OBJECTIVE);
        packet.getStrings().write(0, "sidebar"); // Objective name
        packet.getStrings().write(1, ChatColor.translateAlternateColorCodes('&', title)); // Display name
        packet.getIntegers().write(0, 0); // Action: create or update

        try {
            protocolManager.sendServerPacket(player, packet);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void sendLines(Player player, java.util.List<ScoreboardBuilder.ScoreboardLine> lines) {
        for (ScoreboardBuilder.ScoreboardLine line : lines) {
            sendLine(player, line.getText(), line.getScore());
        }
    }

    private void sendLine(Player player, String text, int score) {
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.SCOREBOARD_SCORE);
        packet.getStrings().write(0, ChatColor.translateAlternateColorCodes('&', text)); // Eintrag
        packet.getStrings().write(1, "sidebar"); // Objective-Name
        packet.getIntegers().write(0, score); // Punktzahl
        packet.getScoreboardActions().write(0, EnumWrappers.ScoreboardAction.CHANGE); // Aktion: CHANGE oder REMOVE
        try {
            protocolManager.sendServerPacket(player, packet);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * Aktualisiert das Scoreboard eines Spielers.
     *
     * @param player Spieler, dessen Scoreboard aktualisiert werden soll.
     */
    public void updateScoreboard(Player player) {
        ScoreboardBuilder.Scoreboard scoreboard = activeScoreboards.get(player);
        if (scoreboard != null) {
            sendScoreboard(player, scoreboard);
        }
    }

    /**
     * Entfernt alle aktiven Scoreboards, z.B. beim Deaktivieren des Plugins.
     */
    public void removeAllScoreboards() {
        for (Player player : activeScoreboards.keySet()) {
            removeScoreboard(player);
        }
    }

    /**
     * Aktualisiert alle aktiven Scoreboards.
     */
    public void updateAllScoreboards() {
        for (Player player : activeScoreboards.keySet()) {
            updateScoreboard(player);
        }
    }
}
