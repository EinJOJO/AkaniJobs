package it.einjojo.jobs;

import org.bukkit.Material;

public enum Job {
    MINER(Material.GOLDEN_PICKAXE, new String[]{
            "",
            "§7Abbau von Erzen und Steinen",
            "Freischaltbare Fähigkeiten:",
            "§7- Schnelleres Abbauen von Erzen",
            "§7- Erhöhte Dropchance von Erzen",
            ""
    }),
    FARMER(Material.NETHERITE_HOE, new String[]{
            "",
            "§7Anbau von Pflanzen",
            "§fFreischaltbare Fähigkeiten:",
            "§7- §eSchnelleres Wachstum deiner Pflanzen",
            "§7- §eErhöhte Dropchance von Samen",
            ""
    }),
    WOODCUTTER(Material.IRON_AXE, new String[]{
            "",
            "§7Holzfäller",
            "§fFreischaltbare Fähigkeiten:",
            "§7- §eSchnelleres Fällen von Bäumen",
            "§7- §eErhöhte Dropchance von Holz",
            ""
    }),
    HUNTER(Material.STONE_SWORD, new String[]{
            "",
            "§7Jagd",
            "§fFreischaltbare Fähigkeiten:",
            "§7- §eErhöhte Dropchance von Fleisch",
            "§7- §eSneak-One-Hit-Kills bei Tieren",
            ""
    }),
    FISHER(Material.FISHING_ROD, new String[]{
            "",
            "§7Fischerei",
            "§fFreischaltbare Fähigkeiten:",
            "§7- §eErhöhte Dropchance von Fisch",
            "§7- §eSchnelleres Angeln",
            ""
    });
    private final Material iconMaterial;
    private final String[] descriptionLore;


    Job(Material iconMaterial, String[] descriptionLore) {
        this.iconMaterial = iconMaterial;
        this.descriptionLore = descriptionLore;
    }

    public Material iconMaterial() {
        return iconMaterial;
    }

    public String[] descriptionLore() {
        return descriptionLore;
    }
}
