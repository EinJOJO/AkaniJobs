package it.einjojo.jobs.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.PaperCommandManager;
import co.aikar.commands.annotation.*;
import com.google.common.base.Preconditions;
import it.einjojo.jobs.Job;
import it.einjojo.jobs.Jobs;
import it.einjojo.jobs.JobsPlugin;
import it.einjojo.jobs.gui.JobInfoGui;
import it.einjojo.jobs.gui.JobOverviewGui;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

@CommandAlias("jobs")
public class JobsCommand extends BaseCommand {
    private static final LegacyComponentSerializer LEGACY_COMPONENT_SERIALIZER = LegacyComponentSerializer.legacySection();
    private final Jobs jobs;
    private final JavaPlugin plugin;

    public JobsCommand(PaperCommandManager commandManager, Jobs jobs, JavaPlugin plugin) {
        Preconditions.checkNotNull(jobs);
        Preconditions.checkNotNull(plugin);
        this.plugin = plugin;
        this.jobs = jobs;
        commandManager.registerCommand(this);
        commandManager.getCommandCompletions().registerStaticCompletion("jobTypes", () -> Arrays.stream(Job.values()).map(Job::name).toList());
        commandManager.getCommandContexts().registerContext(Job.class, c -> {
            try {
                return Job.valueOf(c.popFirstArg());
            } catch (IllegalArgumentException e) {
                throw new InvalidCommandArgument("Invalid job");
            }

        });
    }

    @Default
    @Description("open job gui")
    public void openJobGui(Player player) {
        jobs.jobPlayers().get(player.getUniqueId()).thenAccept(jobPlayer -> {
            Bukkit.getScheduler().runTask(plugin, () -> {
                if (jobPlayer.currentJob() != null) {
                    new JobInfoGui(player, jobPlayer).open();
                } else {
                    new JobOverviewGui(player, jobPlayer).open();
                }
            });
        }).exceptionally((ex) -> {
            ex.fillInStackTrace();
            sendMessage(player, "§cEin Fehler ist aufgetreten: " + ex.getMessage());
            return null;
        });
    }

    @Subcommand("join")
    @CommandCompletion("@jobTypes")
    @Description("join a job")
    @Syntax("<job>")
    public void joinJob(Player sender, Job job) {
        jobs.jobPlayers().get(sender.getUniqueId())
                .thenAccept(jobPlayer -> {
                    jobPlayer.setCurrentJob(job);
                    sendMessage(sender, "§7Du hast den Job §a" + job.name() + " §7ausgewählt.");
                })
                .exceptionally((ex) -> {
                    ex.fillInStackTrace();
                    sendMessage(sender, "§cEin Fehler ist aufgetreten.");
                    return null;
                });
    }

    public void sendMessage(Player player, String message) {
        player.sendMessage(JobsPlugin.PREFIX.appendSpace().append(LEGACY_COMPONENT_SERIALIZER.deserialize(message)));
    }
}
