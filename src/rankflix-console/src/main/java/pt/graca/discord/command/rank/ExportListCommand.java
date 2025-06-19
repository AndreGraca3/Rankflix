package pt.graca.discord.command.rank;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.utils.FileUpload;
import pt.graca.Utils;
import pt.graca.api.domain.media.Media;
import pt.graca.api.domain.user.User;
import pt.graca.api.service.RankflixService;
import pt.graca.discord.command.ICommand;
import pt.graca.infra.exporters.ExcelExporter;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ExportListCommand implements ICommand {

    public ExportListCommand(RankflixService rankflixService, ExcelExporter excelExporter) {
        this.rankflixService = rankflixService;
        this.excelExporter = excelExporter;
    }

    private final RankflixService rankflixService;
    private final ExcelExporter excelExporter;

    @Override
    public String getName() {
        return "export-list";
    }

    @Override
    public String getDescription() {
        return "Exports a list of media";
    }

    @Override
    public List<OptionData> getOptions() {
        return new ArrayList<>();
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception {
        var currentTime = System.currentTimeMillis();

        List<Media> media = rankflixService.getAllMedia(null, null);
        List<User> users = rankflixService.getAllUsers(media.stream().map(m -> m.watchers).flatMap(List::stream).map(w -> w.userId).toList());

        if (media.isEmpty()) {
            event.getHook()
                    .sendMessageEmbeds(new EmbedBuilder()
                            .setTitle("Nothing to export yet")
                            .setColor(Color.GRAY)
                            .build())
                    .queue();

            return;
        }

        excelExporter.exportList(media, users, file -> {
            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setTitle("List exported successfully")
                    .addField("Export time", Utils.formatMillisToTime(System.currentTimeMillis() - currentTime), true)
                    .setColor(Color.GREEN);

            event.getHook().sendMessageEmbeds(embedBuilder.build())
                    .addFiles(FileUpload.fromData(file, file.getName()))
                    .queue();
        });
    }
}