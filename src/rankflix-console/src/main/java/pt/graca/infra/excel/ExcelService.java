package pt.graca.infra.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelService {

    private static final int USER_IDS_ROW = 0;
    private static final int USERNAMES_ROW = 1;
    private static final int FIRST_MEDIA_ROW = 2;

    private static final int MEDIA_IDS_COLUMN = 0;
    private static final int MEDIA_TITLES_COLUMN = 1;
    private static final int FIRST_RATE_COLUMN = 2;

    public static List<ExcelMedia> importMedia(String location) throws Exception {
        FileInputStream file = new FileInputStream(location);
        Workbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);

        Map<Integer, List<String>> rowsData = new HashMap<>();
        int i = 0;
        for (Row row : sheet) {
            rowsData.put(i, new ArrayList<String>());
            for (Cell cell : row) {
                switch (cell.getCellType()) {
                    case STRING:
                        rowsData.get(i).add(cell.getStringCellValue());
                        break;
                    case NUMERIC, FORMULA:
                        rowsData.get(i).add(String.valueOf(cell.getNumericCellValue()));
                        break;
                    default:
                        rowsData.get(i).add(" ");
                }
            }
            i++;
        }

        Map<Integer, ExcelUser> cellToUser = new HashMap<>();

        for (int j = 0; j < rowsData.get(USER_IDS_ROW).size(); j++) {
            cellToUser.put(j, new ExcelUser(
                    rowsData.get(USER_IDS_ROW).get(j),
                    rowsData.get(USERNAMES_ROW).get(j)
            ));
        }

        List<ExcelMedia> mediaList = new ArrayList<>();

        for (int j = FIRST_MEDIA_ROW; j < rowsData.size(); j++) {
            List<ExcelRating> ratings = new ArrayList<>();
            var currentMediaRow = rowsData.get(j);

            for (int k = FIRST_RATE_COLUMN; k < currentMediaRow.size() - 1; k++) {
                var ratingValue = currentMediaRow.get(k);
                if (ratingValue.isBlank()) {
                    continue;
                }
                ratings.add(new ExcelRating(cellToUser.get(k - FIRST_RATE_COLUMN), Float.parseFloat(ratingValue)));
            }

            int mediaId = (int) Float.parseFloat(currentMediaRow.get(MEDIA_IDS_COLUMN));
            String mediaTitle = currentMediaRow.get(MEDIA_TITLES_COLUMN);

            // if ratings is empty, add a rating equal to the average rating for each user
            if (ratings.isEmpty()) {
                for (ExcelUser user : cellToUser.values()) {
                    float averageRating = Float.parseFloat(currentMediaRow.getLast());
                    ratings.add(new ExcelRating(user, averageRating));
                }
            }

            mediaList.add(new ExcelMedia(mediaId, mediaTitle, ratings));
        }

        workbook.close();
        return mediaList;
    }
}