package it.einjojo.jobs.gui;

import mc.obliviate.inventory.Gui;
import mc.obliviate.inventory.Icon;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ConfirmGui extends Gui {
    private final Consumer<Boolean> onConfirm;
    private final String confirm;

    public ConfirmGui(@NotNull Player player, String confirm, Consumer<Boolean> onConfirm) {
        super(player, "confirm", "§6Triff eine Entscheidung!", 3);
        this.onConfirm = onConfirm;
        this.confirm = confirm;
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        Icon NO = new Icon(Material.RED_CONCRETE).setName("§cNein").onClick((click) -> {
            player.closeInventory();
            onConfirm.accept(false);

        });
        Icon INFO = new Icon(Material.BOOK).setName("§7Bestätigung").setLore("", confirm, "§7Drücke §aJa §7oder §cNein").onClick((click) -> {
        });
        Icon YES = new Icon(Material.GREEN_CONCRETE).setName("§aJa").onClick((click) -> {
            player.closeInventory();
            onConfirm.accept(true);
        });
        fillColumn(NO, 0);
        fillColumn(NO, 1);
        fillColumn(NO, 2);
        fillColumn(NO, 3);
        addItem(13, INFO);
        fillColumn(YES, 5);
        fillColumn(YES, 6);
        fillColumn(YES, 7);
        fillColumn(YES, 8);
    }


}
