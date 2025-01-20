package de.sakuramc.coreapi.commands;

import de.sakuramc.coreapi.CoreAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Simon Stögerer
 * copyright - all rights reserved
 * created: 20.01.2025 - 23:17
 */

public class ChangeLanguageCommand implements CommandExecutor {

    // Mapping von Sprachennamen auf I18N-Codes
    private static final Map<String, String> languageMap = new HashMap<>();

    static {
        languageMap.put("deutsch", "de_DE");
        languageMap.put("englisch", "en_US");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Dieser Befehl kann nur von Spielern verwendet werden.");
            return true;
        }

        Player player = (Player) sender;
        UUID playerId = player.getUniqueId();

        if (args.length != 1) {
            player.sendMessage(CoreAPI.instance().languageAPI().translate(
                    CoreAPI.instance().languageAPI().getLanguage(playerId),
                    "language.command.usage"
            ));
            return true;
        }

        String inputLanguage = args[0].toLowerCase();

        // Überprüfen, ob der eingegebene Wert ein Sprachcode oder ein Sprachname ist
        String newLanguage = languageMap.getOrDefault(inputLanguage, inputLanguage);

        if (!languageMap.containsValue(newLanguage)) {
            player.sendMessage(CoreAPI.instance().languageAPI().translate(
                    CoreAPI.instance().languageAPI().getLanguage(playerId),
                    "language.command.invalid"
            ));
            return true;
        }

        // Sprache in der Datenbank aktualisieren
        CoreAPI.instance().languageAPI().setLanguage(playerId, newLanguage);

        // Erfolgsnachricht in der neuen Sprache anzeigen
        player.sendMessage(CoreAPI.instance().languageAPI().translate(
                newLanguage,
                "language.command.changed",
                inputLanguage
        ));

        return true;
    }
}