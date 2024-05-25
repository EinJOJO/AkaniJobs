package it.einjojo.jobs.gui;

import it.einjojo.jobs.Job;
import it.einjojo.jobs.Jobs;
import it.einjojo.jobs.player.JobPlayer;
import mc.obliviate.inventory.Icon;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class JobInfoGui extends ExtendedGui {
    private final JobPlayer jobPlayer;

    public JobInfoGui(Player player, JobPlayer jobPlayer) {
        super(player, "job_info", "§cJob Info");
        this.jobPlayer = jobPlayer;
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        quitJobIcon(jobPlayer);
        jobIcon(jobPlayer);
        playOpenSound();
        backToOverviewIcon();
        helpIcon();

    }

    private void jobIcon(JobPlayer jobPlayer) {
        Job currentJob = jobPlayer.currentJob();
        assert currentJob != null;
        addItem(4, new Icon(currentJob.iconMaterial()).setLore(currentJob.descriptionLore()));
    }


    private void quitJobIcon(JobPlayer jobPlayer) {
        addItem(44, new Icon(Material.BARRIER).setName("§7Job verlassen").setLore(
                "§7Klicke hier, um deinen aktuellen Job zu verlassen.",
                "§7Beim Verlassen bleiben XP und Level erhalten.",
                "",
                "§7Du kannst jederzeit einen neuen Job auswählen",
                "§7und dort weitermachen, wo du aufgehört hast."
        ).onClick((click) -> {
            jobPlayer.setCurrentJob(null);
            playClickSound();
            backToOverview();

        }));
    }

    private void backToOverviewIcon() {
        addItem(36, new Icon(Material.ARROW).setName("§7Zurück zur Übersicht").setLore(
                "§7Klicke hier, um zur Übersicht zurückzukehren."
        ).onClick((click) -> {
            playClickSound();
            backToOverview();
        }));
    }

    private void backToOverview() {
        new JobOverviewGui(player, jobPlayer).open();
    }


    private void helpIcon() {
        addItem(40, new Icon(Material.BOOK).setName("§7Hilfestellung").setLore(
                "§7Hier findest du alle Informationen zu deinem aktuellen Job."
        ));
    }
}
