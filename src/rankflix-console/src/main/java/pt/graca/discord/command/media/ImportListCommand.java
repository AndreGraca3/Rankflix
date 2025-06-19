package pt.graca.discord.command.media;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pt.graca.Utils;
import pt.graca.api.service.RankflixService;
import pt.graca.api.service.exceptions.RankflixException;
import pt.graca.discord.command.ICommand;
import pt.graca.infra.excel.ExcelService;

import java.awt.*;
import java.io.IOException;
import java.util.List;

public class ImportListCommand implements ICommand {

    private final RankflixService rankflixService;

    public ImportListCommand(RankflixService rankflixService) {
        this.rankflixService = rankflixService;
    }

    @Override
    public String getName() {
        return "import-list";
    }

    @Override
    public String getDescription() {
        return "Imports (overwrites) a list of media";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.ATTACHMENT, "file", "The file to import", true));
    }

    @Override
    public boolean isAdminCommand() {
        return true;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) throws Exception {
        var currentTime = System.currentTimeMillis();
        var attachment = event.getOption("file").getAsAttachment();

        attachment.getProxy().download().thenAccept(inputStream -> {
            try {
                Workbook workbook = new XSSFWorkbook(inputStream);

                var importResult = ExcelService.importMediaFromWorkbook(workbook);

                rankflixService.importMediasWithWatchersRange(importResult.importedMedia(),
                        importResult.importedUsers());

                event.getHook().sendMessageEmbeds(new EmbedBuilder()
                        .setTitle("List imported successfully")
                        .addField("Total media", String.valueOf(importResult.importedMedia().size()), true)
                        .addField("Total users", String.valueOf(importResult.importedUsers().size()), true)
                        .addField("Import time",
                                Utils.formatMillisToTime(System.currentTimeMillis() - currentTime),
                                true)
                        .setColor(Color.GREEN)
                        .build()
                ).queue();
            } catch (IOException | RankflixException e) {
                event.getHook().sendMessageEmbeds(new EmbedBuilder()
                        .setTitle("Error")
                        .setDescription(e.getMessage())
                        .setColor(Color.RED)
                        .build()
                ).queue();
            }
        });
    }
}
