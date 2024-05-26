package it.einjojo.jobs.gui;

import it.einjojo.jobs.Job;
import it.einjojo.jobs.player.JobPlayer;
import mc.obliviate.inventory.Icon;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemFlag;

public class JobOverviewGui extends ExtendedGui {
    private final JobPlayer jobPlayer;

    private final GuiFactory guiFactory;

    public JobOverviewGui(Player player, JobPlayer jobPlayer, GuiFactory guiFactory) {
        super(player, "jobs_overview", "§cJobs Übersicht");
        this.jobPlayer = jobPlayer;
        this.guiFactory = guiFactory;

    }


    @Override
    public void onOpen(InventoryOpenEvent openEvent) {
        playOpenSound();
        placeIcons(jobPlayer);
    }

    private void placeIcons(JobPlayer jobPlayer) {

        var mine = new Icon(Job.MINER.iconMaterial())
                .setName("§7Miner")
                .setLore(Job.MINER.descriptionLore())
                .onClick((click) -> {
                    selectJob(Job.MINER, jobPlayer);
                });
        var farm = new Icon(Job.FARMER.iconMaterial())
                .setName("§7Farmer")
                .setLore(Job.FARMER.descriptionLore())
                .onClick((click) -> {
                    selectJob(Job.FARMER, jobPlayer);
                });
        var fish = new Icon(Job.FISHER.iconMaterial())
                .setName("§7Fischer")
                .setLore(Job.FISHER.descriptionLore())
                .onClick((click) -> {
                    selectJob(Job.FISHER, jobPlayer);
                });
        var hunter = new Icon(Job.HUNTER.iconMaterial())
                .setName("§7Jäger")
                .setLore(Job.HUNTER.descriptionLore())
                .onClick((click) -> {
                    selectJob(Job.HUNTER, jobPlayer);
                });
        var woodcutter = new Icon(Material.IRON_AXE)
                .setName("§7Holzfäller")
                .setLore(Job.WOODCUTTER.descriptionLore())
                .onClick((click) -> {
                    selectJob(Job.WOODCUTTER, jobPlayer);
                });
        Job currentJob = jobPlayer.currentJob();
        var toBeMarkedJobItem = currentJob != null ? switch (currentJob) {
            case MINER -> mine;
            case FARMER -> farm;
            case FISHER -> fish;
            case HUNTER -> hunter;
            case WOODCUTTER -> woodcutter;
        } : null;
        if (toBeMarkedJobItem != null) {
            toBeMarkedJobItem.enchant(Enchantment.DURABILITY, 1);
            toBeMarkedJobItem.hideFlags(ItemFlag.HIDE_ENCHANTS);
        }
        addItem(18, mine);
        addItem(20, farm);
        addItem(22, fish);
        addItem(24, hunter);
        addItem(26, woodcutter);
    }

    private void selectJob(Job job, JobPlayer jobPlayer) {
        playClickSound();
        if (jobPlayer.currentJob() == job) {
            return;
        }
        if (jobPlayer.currentJob() != null) {
            new ConfirmGui(player, "§7Bist du sicher, dass du deinen aktuellen Job verlassen möchtest?", (confirm) -> {
                playClickSound();
                if (confirm) {
                    jobPlayer.setCurrentJob(job);
                    guiFactory.createJobInfoGui(player, jobPlayer).open();
                } else {
                    guiFactory.createJobOverviewGui(player, jobPlayer).open();
                }
            }).open();
        } else {
            jobPlayer.setCurrentJob(job);
            guiFactory.createJobInfoGui(player, jobPlayer).open();
        }

    }
}
