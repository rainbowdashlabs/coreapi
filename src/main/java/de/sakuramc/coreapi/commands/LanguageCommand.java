package de.sakuramc.coreapi.commands;

import de.sakuramc.coreapi.CoreAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LanguageCommand implements CommandExecutor {

    public CoreAPI instance;

    public LanguageCommand(final CoreAPI command) {
        this.instance = command;
        this.instance.getCommand("language").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(instance.noPlayer);
            return false;
        }

        String langCode = this.instance.languageManager.getPlayerLanguage(player);

        if (args.length == 0) {
            if (langCode.equalsIgnoreCase("de")) {
                this.instance.languageManager.setPlayerLanguage(player, "en");
                String message = this.instance.languageManager.getMessage(langCode, "changeLanguage");
                player.sendMessage(instance.prefix + message);
            } else if (langCode.equalsIgnoreCase("en")) {
                this.instance.languageManager.setPlayerLanguage(player, "de");
                String message = this.instance.languageManager.getMessage(langCode, "changeLanguage");
                player.sendMessage(instance.prefix + message);
            } else {
                String message = this.instance.languageManager.getMessage(langCode, "onUse");
                player.sendMessage(this.instance.prefix + message + "lang");
            }
        } else {
            String message = this.instance.languageManager.getMessage(langCode, "onUse");
            player.sendMessage(this.instance.prefix + message + "lang");
        }

        return true;
    }
}
