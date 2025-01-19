package de.sakuramc.coreapi.commands;

import de.sakuramc.coreapi.CoreAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ReloadLanguageCommand implements CommandExecutor {

    public CoreAPI instance;

    public void CoreAPI(CoreAPI command) {
        this.instance = command;
        this.instance.getCommand("reloadlanguage").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
        }

        return true;
    }
}
