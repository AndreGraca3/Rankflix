package pt.graca.infra.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pt.graca.api.domain.Review;
import pt.graca.api.domain.media.Media;
import pt.graca.api.domain.media.MediaWatcher;
import pt.graca.api.domain.user.User;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ExcelService {

    private static final int USER_DISCORD_IDS_ROW_IDX = 2;
    private static final int USER_USERNAMES_ROW_IDX = 3;
    private static final int FIRST_USER_RATE_COLUMN_IDX = getColumnIdx('E');

    private static final int FIRST_MEDIA_ROW_IDX = 4;
    private static final int MEDIA_IDS_COLUMN_IDX = getColumnIdx('C');
    private static final int MEDIA_TITLES_COLUMN_IDX = getColumnIdx('D');

    public static ExcelImportResult importMediaFromWorkbook(Workbook workbook) {
        Sheet sheet = workbook.getSheetAt(0);

        Row userIdsRow = sheet.getRow(USER_DISCORD_IDS_ROW_IDX);
        Row usernamesRow = sheet.getRow(USER_USERNAMES_ROW_IDX);

        HashMap<Integer, User> columnIdxToUser = new HashMap<>();
        var userIdsCount = userIdsRow.getPhysicalNumberOfCells();
        var averageRateColumnIdx = FIRST_USER_RATE_COLUMN_IDX + userIdsCount;

        for (int i = FIRST_USER_RATE_COLUMN_IDX; i < averageRateColumnIdx; i++) {
            var userIdCell = userIdsRow.getCell(i);
            String discordId = userIdCell.getStringCellValue();

            String username = usernamesRow.getCell(i).getStringCellValue();
            columnIdxToUser.put(i, new User(discordId, username));
        }

        List<Media> mediaList = new ArrayList<>();

        for (int i = FIRST_MEDIA_ROW_IDX; i <= sheet.getLastRowNum() - 1; i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            String mediaId = row.getCell(MEDIA_IDS_COLUMN_IDX).getStringCellValue();
            String title = row.getCell(MEDIA_TITLES_COLUMN_IDX).getStringCellValue();

            List<MediaWatcher> watchers = new ArrayList<>();

            for (int j = FIRST_USER_RATE_COLUMN_IDX; j < averageRateColumnIdx; j++) {
                var ratingCell = row.getCell(j);
                User user = columnIdxToUser.get(j);

                if (ratingCell.getCellType() == CellType.BLANK) {
                    if (!isYellowBackground(ratingCell)) continue;
                    watchers.add(new MediaWatcher(user.id, null));
                    continue;
                }

                // get rating and/or comment -> format: [rating] - [comment]
                String ratingAndComment = ratingCell.getCellType() == CellType.NUMERIC ?
                        String.valueOf(ratingCell.getNumericCellValue())
                        : ratingCell.getStringCellValue();

                String[] ratingAndCommentArr = ratingAndComment.split(" - ");
                float rating = Float.parseFloat(ratingAndCommentArr[0].replace(",", "."));
                String comment = ratingAndCommentArr.length > 1 ? ratingAndCommentArr[1] : null;

                watchers.add(new MediaWatcher(user.id, new Review(rating, comment)));
            }

            var averageRating = (float) row.getCell(averageRateColumnIdx).getNumericCellValue();

            var media = new Media(mediaId, title, averageRating, watchers, true);
            mediaList.add(media);
        }

        return new ExcelImportResult(mediaList, columnIdxToUser.values().stream().toList());
    }

    public static Workbook generateWorkbook(List<Media> mediaList, List<User> users) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Media");

        // Create header rows
        Row userDiscordIdsRow = sheet.createRow(USER_DISCORD_IDS_ROW_IDX);
        Row usernamesRow = sheet.createRow(USER_USERNAMES_ROW_IDX);

        int usersCount = users.size();
        int averageRateColumnIdx = FIRST_USER_RATE_COLUMN_IDX + usersCount;

        // Create basic cell styles
        CellStyle defaultStyle = workbook.createCellStyle();
        defaultStyle.setWrapText(true);
        defaultStyle.setAlignment(HorizontalAlignment.CENTER);
        defaultStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        defaultStyle.setBorderTop(BorderStyle.THIN);
        defaultStyle.setBorderBottom(BorderStyle.THIN);
        defaultStyle.setBorderLeft(BorderStyle.THIN);
        defaultStyle.setBorderRight(BorderStyle.THIN);
        defaultStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00"));

        CellStyle redStyle = workbook.createCellStyle();
        redStyle.cloneStyleFrom(defaultStyle);
        redStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
        redStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle yellowStyle = workbook.createCellStyle();
        yellowStyle.cloneStyleFrom(defaultStyle);
        yellowStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        yellowStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Write Discord IDs and usernames
        for (int i = 0; i < usersCount; i++) {
            User user = users.get(i);
            int colIdx = FIRST_USER_RATE_COLUMN_IDX + i;

            Cell discordIdCell = userDiscordIdsRow.createCell(colIdx);
            discordIdCell.setCellValue(user.discordId);
            discordIdCell.setCellStyle(defaultStyle);

            Cell usernameCell = usernamesRow.createCell(colIdx);
            usernameCell.setCellValue(user.username);
            usernameCell.setCellStyle(defaultStyle);
        }

        // Write media rows
        int rowIdx = FIRST_MEDIA_ROW_IDX;
        for (Media media : mediaList) {
            Row row = sheet.createRow(rowIdx++);

            Cell idCell = row.createCell(MEDIA_IDS_COLUMN_IDX);
            idCell.setCellValue(media.id);
            idCell.setCellStyle(defaultStyle);

            Cell titleCell = row.createCell(MEDIA_TITLES_COLUMN_IDX);
            titleCell.setCellValue(media.title);
            titleCell.setCellStyle(defaultStyle);

            for (int i = 0; i < usersCount; i++) {
                User user = users.get(i);
                MediaWatcher watcher = media.getWatcherByUserId(user.id);

                Cell cell = row.createCell(FIRST_USER_RATE_COLUMN_IDX + i);

                if (watcher == null) {
                    cell.setCellStyle(redStyle);
                } else if (watcher.review != null) {
                    cell.setCellValue(watcher.review.rating + (watcher.review.comment != null ? " - " + watcher.review.comment : ""));
                    cell.setCellStyle(defaultStyle);
                } else {
                    cell.setCellStyle(yellowStyle);
                }
            }

            // Average rating
            Cell avgCell = row.createCell(averageRateColumnIdx);
            avgCell.setCellValue(media.averageRating);
            avgCell.setCellStyle(defaultStyle);
        }

        // Autosize columns
        int totalColumns = averageRateColumnIdx + 1;
        int maxWidth = 10000;  // Max width in units (approx 10000 = ~100 chars wide)

        for (int i = 0; i < totalColumns; i++) {
            sheet.autoSizeColumn(i);
            int currentWidth = sheet.getColumnWidth(i);
            if (currentWidth > maxWidth) {
                sheet.setColumnWidth(i, maxWidth);
            }
        }

        // Add row below the last media to write user averages
        Row avgRow = sheet.createRow(rowIdx);

        for (int i = 0; i < usersCount; i++) {
            User user = users.get(i);
            int colIdx = FIRST_USER_RATE_COLUMN_IDX + i;

            // Collect all ratings given by this user
            double total = 0;
            int count = 0;

            for (Media media : mediaList) {
                MediaWatcher watcher = media.getWatcherByUserId(user.id);
                if (watcher != null && watcher.review != null) {
                    total += watcher.review.rating;
                    count++;
                }
            }

            double average = count > 0 ? total / count : 0;

            Cell avgCell = avgRow.createCell(colIdx);
            avgCell.setCellValue(average);
            avgCell.setCellStyle(defaultStyle);
        }

        return workbook;
    }

    public static void saveFile(Workbook workbook, String fileLocation) throws IOException {
        try (OutputStream outputStream = new FileOutputStream(fileLocation)) {
            workbook.write(outputStream);
        }
    }

    private static boolean isYellowBackground(Cell cell) {
        if (cell == null) return false;

        CellStyle style = cell.getCellStyle();
        if (!(style instanceof XSSFCellStyle)) return false;

        XSSFColor color = ((XSSFCellStyle) style).getFillForegroundXSSFColor();
        if (color == null || color.getRGB() == null) return false;

        return Arrays.equals(color.getRGB(), new byte[]{(byte) 255, (byte) 255, 0});
    }

    private static int getColumnIdx(char column) {
        return column - 'A';
    }
}