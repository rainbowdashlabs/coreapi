package de.sakuramc.coreapi.scoreboard;

import org.bukkit.entity.Player;

public interface IScoreboardManager {

    void sendScoreboard(final Player player, ScoreboardBuilder.Scoreboard scoreboard);
    void removeScoreboard(final Player player);

}
