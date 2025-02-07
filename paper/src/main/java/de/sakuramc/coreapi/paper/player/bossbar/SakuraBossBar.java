package de.sakuramc.coreapi.paper.player.bossbar;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a customizable boss bar in the SakuraMC CoreAPI.
 * Allows setting a title, progress, color, and overlay style before building.
 *
 * <p>Usage Example:</p>
 * <pre>{@code
 * BossBar bossBar = new SakuraBossBar()
 *    .title(Component.text("Custom Title"))
 *    .progress(0.5f)
 *    .color(BossBar.Color.RED)
 *    .overlay(BossBar.Overlay.NOTCHED_10)
 *    .build();}
 *    </pre>
 */
@Getter
@Accessors(fluent = true)
public final class SakuraBossBar {

    private Component title = Component.text("Default Title");
    private float progress = 1.0f;
    private BossBar.Color color = BossBar.Color.BLUE;
    private BossBar.Overlay overlay = BossBar.Overlay.PROGRESS;

    /**
     * Sets the title of the boss bar.
     *
     * @param title the title of the boss bar
     * @return the boss bar instance
     */
    public SakuraBossBar title(final @NotNull Component title) {
        this.title = title;
        return this;
    }

    /**
     * Sets the progress of the boss bar.
     *
     * @param progress the progress of the boss bar
     * @return the boss bar instance
     */
    public SakuraBossBar progress(final float progress) {
        this.progress = Math.max(0.0f, Math.min(progress, 1.0f));
        return this;
    }

    /**
     * Sets the color of the boss bar.
     *
     * @param color the color of the boss bar
     * @return the boss bar instance
     */
    public SakuraBossBar color(final @NotNull BossBar.Color color) {
        this.color = color;
        return this;
    }

    /**
     * Sets the overlay style of the boss bar.
     *
     * @param overlay the overlay style of the boss bar
     * @return the boss bar instance
     */
    public SakuraBossBar overlay(final @NotNull BossBar.Overlay overlay) {
        this.overlay = overlay;
        return this;
    }

    /**
     * Builds the boss bar with the specified properties.
     *
     * @return the boss bar instance
     */
    public @NotNull BossBar build() {
        return BossBar.bossBar(title, progress, color, overlay);
    }
}
