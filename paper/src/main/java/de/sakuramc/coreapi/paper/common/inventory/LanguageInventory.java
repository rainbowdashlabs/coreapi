package de.sakuramc.coreapi.paper.common.inventory;

import de.sakuramc.coreapi.api.CoreAPI;
import de.sakuramc.coreapi.paper.CorePaperService;
import de.sakuramc.coreapi.paper.player.inventory.InventoryCreator;
import de.sakuramc.coreapi.paper.player.inventory.SakuraInventory;
import de.sakuramc.coreapi.paper.player.inventory.listener.SlotActionListener;
import de.sakuramc.coreapi.paper.player.items.ItemCreator;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Simon Stögerer
 * copyright - all rights reserved
 * created: 01.02.2025 - 10:02
 */

@Getter
@Accessors(fluent = true)
public final class LanguageInventory implements SakuraInventory {
    private Inventory inventory;

    private final Map<Integer, ItemStack> items = new HashMap<>() {{
        final var germanSkull = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWU3ODk5YjQ4MDY4NTg2OTdlMjgzZjA4NGQ5MTczZmU0ODc4ODY0NTM3NzQ2MjZiMjRiZDhjZmVjYzc3YjNmIn19fQ==";
        final var englishSkull = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2QxNWQ1NjYyMDJhYzBlNzZjZDg5Nzc1OWRmNWQwMWMxMWY5OTFiZDQ2YzVjOWEwNDM1N2VhODllZTc1In19fQ==";

        put(12, new ItemCreator(Material.PLAYER_HEAD).skull(germanSkull, "§cDeutsch"));
        put(14, new ItemCreator(Material.PLAYER_HEAD).skull(englishSkull, "§cEnglish"));
    }};

    @Override
    public void createInventory(@NotNull Player player, int size, @NotNull Component name) {
        addInventoryBorder(this.items, size);
        this.inventory = new InventoryCreator().name(name).size(size).items(this.items).build();


        final var slotAction = new SlotActionListener(this.inventory);

        slotAction.action(12, p -> {
            CorePaperService.instance().languageAPI().setLanguage(p.getUniqueId(), Locale.GERMANY);

            p.sendMessage(CoreAPI.instance().languageAPI().translate(p.getUniqueId(), "inventory.language.changed"));
        });

        slotAction.action(14, p -> {
            CorePaperService.instance().languageAPI().setLanguage(p.getUniqueId(), Locale.ENGLISH);

            p.sendMessage(CoreAPI.instance().languageAPI().translate(p.getUniqueId(), "inventory.language.changed"));
        });

        Bukkit.getPluginManager().registerEvents(slotAction, CorePaperService.instance());
    }

    @Override
    public void addInventoryBorder(@NotNull Map<Integer, ItemStack> items, int size) {
        for (int i = 0; i < size; i++) {
            if (isBorderSlot(i, size) && !items.containsKey(i)) {
                items.put(i, new ItemCreator(Material.BLACK_STAINED_GLASS_PANE).setName(" ").toItemStack());
            }
        }
    }
}
