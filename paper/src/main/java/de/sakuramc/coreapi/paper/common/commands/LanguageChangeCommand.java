package de.sakuramc.coreapi.paper.common.commands;

import de.sakuramc.coreapi.paper.CorePaperService;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;

/**
 * @author Simon Stögerer
 * copyright - all rights reserved
 * created: 01.02.2025 - 00:43
 */

public class LanguageChangeCommand implements BasicCommand {

    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] args) {
        final var player = (Player) commandSourceStack.getExecutor();

        CorePaperService.instance().languageInventory().createInventory(player, 9*3, CorePaperService.instance().languageAPI().translate(player.getUniqueId(), "inventory.language.title"));
        player.openInventory(CorePaperService.instance().languageInventory().inventory());
    }
}
