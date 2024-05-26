package it.einjojo.jobs.gui;

import mc.obliviate.inventory.Gui;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ExtendedGui extends Gui {
    public ExtendedGui(@NotNull Player player, @NotNull String id, String title) {
        super(player, id, title, 5);
    }
    public ExtendedGui(@NotNull Player player, @NotNull String id, String title, int rows) {
        super(player, id, title, rows);
    }



    public void playClickSound() {
        player.playSound(player, Sound.ENTITY_CHICKEN_EGG, 0.5f, 1.2f);
    }

    public void playOpenSound() {
        player.playSound(player, Sound.BLOCK_BARREL_OPEN, 0.5f, 1.2f);
    }


}
