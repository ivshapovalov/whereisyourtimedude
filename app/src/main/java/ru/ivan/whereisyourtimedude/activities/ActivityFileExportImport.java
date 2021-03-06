package ru.ivan.whereisyourtimedude.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import ru.ivan.whereisyourtimedude.R;
import ru.ivan.whereisyourtimedude.common.Session;
import ru.ivan.whereisyourtimedude.database.entities.Area;
import ru.ivan.whereisyourtimedude.database.entities.DetailedPracticeHistory;
import ru.ivan.whereisyourtimedude.database.entities.Options;
import ru.ivan.whereisyourtimedude.database.entities.Practice;
import ru.ivan.whereisyourtimedude.database.entities.PracticeHistory;
import ru.ivan.whereisyourtimedude.database.entities.Project;
import ru.ivan.whereisyourtimedude.database.entities.User;
import ru.ivan.whereisyourtimedude.database.interfaces.SavingIntoDB;
import ru.ivan.whereisyourtimedude.database.manager.SQLiteDatabaseManager;

import static ru.ivan.whereisyourtimedude.common.Common.*;
import static ru.ivan.whereisyourtimedude.common.Session.sessionBackgroundChronometer;
import static ru.ivan.whereisyourtimedude.common.Session.sessionCurrentUser;

public class ActivityFileExportImport extends AbstractActivity {

    private long mDateFrom;
    private long mDateTo;
    private boolean mIncludeHistory;
    private final SQLiteDatabaseManager DB = new SQLiteDatabaseManager(this);
    private static final String SYMBOL_SPLIT = ";";

    private StringBuilder message = new StringBuilder();
    private StringBuilder messageErrors = new StringBuilder();
    private List<User> users = new ArrayList<>();
    private List<Options> options = new ArrayList<>();
    private List<Area> areas = new ArrayList<>();
    private List<Project> projects = new ArrayList<>();
    private List<Practice> practices = new ArrayList<>();
    private List<PracticeHistory> practiceHistories = new ArrayList<>();
    private List<DetailedPracticeHistory> detailedPracticeHistories = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_export_import);
        getIntentParams();
        updateScreen();
        setTitleOfActivity(this);

    }

    private void getIntentParams() {
        Intent intent = getIntent();
        long mCurrentDateInMillis = intent.getLongExtra("CurrentDateInMillis", 0);
        long mCurrentDateToInMillis = intent.getLongExtra("CurrentDateToInMillis", 0);
        mDateFrom = mCurrentDateInMillis;
        mDateTo = mCurrentDateToInMillis;
    }

    /*********************
     * WRITE
     ****/

    public void exportToXLSFile() {

        getTablesFromDB();

        Map<String, List<String[]>> dataSheets = createDataArray();
        writeToFile(dataSheets);
    }

    private void getTablesFromDB() {
        if (mDateFrom == 0) {
            mDateFrom = Long.MIN_VALUE;
        }
        if (mDateTo == 0) {
            mDateTo = Long.MAX_VALUE;
        }

        users = DB.getAllUsers();
        options = DB.getAllOptions();
        areas = DB.getAllAreas();
        projects = DB.getAllProjects();
        practices = DB.getAllPractices();
        if (mIncludeHistory) {
            practiceHistories = DB.getAllPracticeHistoryByDates(mDateFrom, mDateTo);
            detailedPracticeHistories = DB.getAllDetailedPracticeHistoryByDates(mDateFrom, mDateTo);
        }
    }

    private Map<String, List<String[]>> createDataArray() {
        Map<String, List<String[]>> dataSheets = new HashMap<>();
        message = new StringBuilder();
        List<String[]> data = new ArrayList<>();

        data.add(new StringBuilder().append("ID").append(SYMBOL_SPLIT)
                .append("NAME").append(SYMBOL_SPLIT)
                .append("IS_CURRENT").append(SYMBOL_SPLIT).toString().split(SYMBOL_SPLIT));
        int countEntities = 1;
        for (User currentEntity : users
                ) {
            countEntities++;
            data.add(new StringBuilder()
                    .append(currentEntity.getId()).append(SYMBOL_SPLIT)
                    .append(currentEntity.getName()).append(SYMBOL_SPLIT)
                    .append(currentEntity.isCurrentUser()).append(SYMBOL_SPLIT)
                    .toString().split(SYMBOL_SPLIT));
        }
        message.append("TABLE 'users' - ").append(countEntities).append(" rows").append('\n');
        dataSheets.put("users", data);

        data = new ArrayList<>();
        data.add(new StringBuilder()
                .append("ID").append(SYMBOL_SPLIT)
                .append("ID_USER").append(SYMBOL_SPLIT)
                .append("RECOVERY_ON_RUN_SWITCH").append(SYMBOL_SPLIT)
                .append("DISPLAY_NOTIFICATION_TIMER_SWITCH").append(SYMBOL_SPLIT)
                .append("SAVE_INTERVAL").append(SYMBOL_SPLIT)
                .append("CHRONO_IS_WORKING").append(SYMBOL_SPLIT)
                .append("ROWS_NUMBER_IN_LISTS").append(SYMBOL_SPLIT)
                .toString().split(SYMBOL_SPLIT));
        countEntities = 1;
        for (Options currentEntity : options
                ) {
            data.add(new StringBuilder()
                    .append(currentEntity.getId()).append(SYMBOL_SPLIT)
                    .append(currentEntity.getUserId()).append(SYMBOL_SPLIT)
                    .append(currentEntity.getRecoveryOnRunSwitch()).append(SYMBOL_SPLIT)
                    .append(currentEntity.getDisplayNotificationTimerSwitch()).append(SYMBOL_SPLIT)
                    .append(currentEntity.getSaveInterval()).append(SYMBOL_SPLIT)
                    .append(currentEntity.getChronoIsWorking()).append(SYMBOL_SPLIT)
                    .append(currentEntity.getRowNumberInLists()).append(SYMBOL_SPLIT)
                    .toString().split(SYMBOL_SPLIT));
            countEntities++;
        }
        message.append("TABLE 'options' - ").append(countEntities).append(" rows").append('\n');
        dataSheets.put("options", data);

        data = new ArrayList<>();
        data.add(new StringBuilder()
                .append("ID").append(SYMBOL_SPLIT)
                .append("ID_USER").append(SYMBOL_SPLIT)
                .append("NAME").append(SYMBOL_SPLIT)
                .append("COLOR").append(SYMBOL_SPLIT)
                .toString().split(SYMBOL_SPLIT));
        countEntities = 1;
        for (Area currentEntity : areas
                ) {
            countEntities++;
            data.add(new StringBuilder()
                    .append(currentEntity.getId()).append(SYMBOL_SPLIT)
                    .append(currentEntity.getUserId()).append(SYMBOL_SPLIT)
                    .append(currentEntity.getName()).append(SYMBOL_SPLIT)
                    .append(currentEntity.getColor()).append(SYMBOL_SPLIT)
                    .toString().split(SYMBOL_SPLIT));
        }
        message.append("TABLE 'areas' - ").append(countEntities).append(" rows").append('\n');
        dataSheets.put("areas", data);

        data = new ArrayList<>();
        data.add(new StringBuilder()
                .append("ID").append(SYMBOL_SPLIT)
                .append("ID_USER").append(SYMBOL_SPLIT)
                .append("ID_AREA").append(SYMBOL_SPLIT)
                .append("NAME").append(SYMBOL_SPLIT)
                .toString().split(SYMBOL_SPLIT));
        countEntities = 1;
        for (Project currentEntity : projects
                ) {
            countEntities++;
            data.add(new StringBuilder()
                    .append(currentEntity.getId()).append(SYMBOL_SPLIT)
                    .append(currentEntity.getUserId()).append(SYMBOL_SPLIT)
                    .append(currentEntity.getAreaId()).append(SYMBOL_SPLIT)
                    .append(currentEntity.getName()).append(SYMBOL_SPLIT).toString()
                    .split(SYMBOL_SPLIT));
        }
        message.append("TABLE 'projects' - ").append(countEntities).append(" rows").append('\n');
        dataSheets.put("projects", data);

        data = new ArrayList<>();
        data.add(new StringBuilder()
                .append("ID").append(SYMBOL_SPLIT)
                .append("ID_USER").append(SYMBOL_SPLIT)
                .append("ID_PROJECT").append(SYMBOL_SPLIT)
                .append("NAME").append(SYMBOL_SPLIT)
                .append("IS_ACTIVE").append(SYMBOL_SPLIT)
                .toString().split(SYMBOL_SPLIT));

        countEntities = 1;
        for (Practice currentEntity : practices
                ) {
            countEntities++;
            data.add(new StringBuilder()
                    .append(currentEntity.getId()).append(SYMBOL_SPLIT)
                    .append(currentEntity.getUserId()).append(SYMBOL_SPLIT)
                    .append(currentEntity.getProjectId()).append(SYMBOL_SPLIT)
                    .append(currentEntity.getName()).append(SYMBOL_SPLIT)
                    .append(currentEntity.getIsActive()).append(SYMBOL_SPLIT)
                    .toString().split(SYMBOL_SPLIT));
        }
        message.append("TABLE 'practices' - ").append(countEntities).append(" rows").append('\n');
        dataSheets.put("practices", data);

        if (mIncludeHistory) {
            data = new ArrayList<>();
            data.add(new StringBuilder()
                    .append("ID").append(SYMBOL_SPLIT)
                    .append("ID_USER").append(SYMBOL_SPLIT)
                    .append("ID_PRACTICE").append(SYMBOL_SPLIT)
                    .append("DURATION").append(SYMBOL_SPLIT)
                    .append("LAST_TIME").append(SYMBOL_SPLIT)
                    .append("DATE").append(SYMBOL_SPLIT)
                    .toString().split(SYMBOL_SPLIT));

            countEntities = 1;
            for (PracticeHistory currentEntity : practiceHistories
                    ) {
                countEntities++;
                data.add(new StringBuilder()
                        .append(currentEntity.getId()).append(SYMBOL_SPLIT)
                        .append(currentEntity.getUserId()).append(SYMBOL_SPLIT)
                        .append(currentEntity.getPracticeId()).append(SYMBOL_SPLIT)
                        .append(currentEntity.getDuration()).append(SYMBOL_SPLIT)
                        .append(currentEntity.getLastTime()).append(SYMBOL_SPLIT)
                        .append(currentEntity.getDate()).append(SYMBOL_SPLIT)
                        .toString().split(SYMBOL_SPLIT));

            }
            message.append("TABLE 'practice_history' - ").append(countEntities).append(" rows").append('\n');
            dataSheets.put("practice_history", data);

            data = new ArrayList<>();
            data.add(new StringBuilder()
                    .append("ID").append(SYMBOL_SPLIT)
                    .append("ID_USER").append(SYMBOL_SPLIT)
                    .append("ID_PRACTICE").append(SYMBOL_SPLIT)
                    .append("DURATION").append(SYMBOL_SPLIT)
                    .append("TIME").append(SYMBOL_SPLIT)
                    .append("DATE").append(SYMBOL_SPLIT)
                    .toString().split(SYMBOL_SPLIT));

            countEntities = 1;
            for (DetailedPracticeHistory currentEntity : detailedPracticeHistories
                    ) {
                countEntities++;
                data.add(new StringBuilder()
                        .append(currentEntity.getId()).append(SYMBOL_SPLIT)
                        .append(currentEntity.getUserId()).append(SYMBOL_SPLIT)
                        .append(currentEntity.getPracticeId()).append(SYMBOL_SPLIT)
                        .append(currentEntity.getDuration()).append(SYMBOL_SPLIT)
                        .append(currentEntity.getTime()).append(SYMBOL_SPLIT)
                        .append(currentEntity.getDate()).append(SYMBOL_SPLIT)
                        .toString().split(SYMBOL_SPLIT));
            }
            message.append("TABLE 'detailed_practice_history' - ").append(countEntities).append(" rows").append('\n');
            dataSheets.put("detailed_practice_history", data);
        }

        return dataSheets;
    }

    private void writeToFile(Map<String, List<String[]>> dataSheets) {

        File exportDir = new File(Environment.getExternalStorageDirectory(), "");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }
        File file = new File(exportDir, "wiytd.xls");
        try {

            if (file.createNewFile()) {
                //System.out.println("File is created!");

            } else {
                //System.out.println("File already exists.");
            }

            Workbook book = new HSSFWorkbook();

            for (Map.Entry<String, List<String[]>> dataSheet : dataSheets.entrySet()) {
                addSheetWithData(dataSheet.getValue(), book, dataSheet.getKey());
            }

            book.write(new FileOutputStream(file));
            book.close();

            displayMessage("Successfully write to file \n" + Environment.getExternalStorageDirectory().toString() + '\n'
                        + " tables \n" + message.toString());

        } catch (Exception e) {
            displayMessage("Wile with data not created in " + Environment.getExternalStorageDirectory().toString());

        }
    }

    private void addSheetWithData(List<String[]> data, Workbook book, String sheetName) {
        Sheet sheet = book.createSheet(sheetName);
        Cell cName;
        Font font = book.getFontAt((short) 0);
        CellStyle boldStyle = book.createCellStyle();
        boldStyle.setFillForegroundColor(HSSFColor.GREY_40_PERCENT.index);
        boldStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        boldStyle.setFont(font);

        CellStyle usualStyle = book.createCellStyle();
        usualStyle.setFont(font);

        Row row = sheet.createRow(0);
        for (int j = 0; j < data.get(0).length; j++) {
            cName = row.createCell(j);
            cName.setCellStyle(boldStyle);
            cName.setCellValue(data.get(0)[j]);
        }

        for (int i = 1; i < data.size(); i++) {
            row = sheet.createRow(i);
            for (int j = 0; j < data.get(i).length; j++) {
                cName = row.createCell(j);
                cName.setCellStyle(usualStyle);
                try {
                    cName.setCellValue(Long.valueOf(data.get(i)[j]));
                } catch (Exception e) {
                    cName.setCellValue((data.get(i)[j]));
                }
                cName.setCellStyle(usualStyle);
            }
        }
        book.getSheetAt(0).setPrintGridlines(true);
    }

    /*********************
     * READ
     */

    private void readFromFile(File file) {
        List<String> tables = new ArrayList<>();
        tables.add("users");
        tables.add("options");
        tables.add("areas");
        tables.add("projects");
        tables.add("practices");
        if (mIncludeHistory) {
            tables.add("practice_history");
            tables.add("detailed_practice_history");
        }

        try

        {
            StringBuilder errorMessage = new StringBuilder();
            Map<String, List<String[]>> sheets = new LinkedHashMap<>();
            HSSFWorkbook myExcelBook = new HSSFWorkbook(new FileInputStream(file));
            for (String table : tables
                    ) {
                HSSFSheet myExcelSheet = myExcelBook.getSheet(table);
                if (myExcelSheet == null) {
                    errorMessage.append("Missed sheet - '" + table + "'").append("\n");
                }
                List<String[]> data = ReadDataFromSheets(myExcelSheet);
                sheets.put(table, data);
            }

            if (errorMessage.length() != 0) {
                errorMessage.append("Data didn't load from ").append(Environment.getExternalStorageDirectory().toString())
                        .append("/wiytd.xls");
                displayMessage(errorMessage.toString());
            }
            myExcelBook.close();
            writeDataToDB(sheets);

        } catch (Exception e) {
            displayMessage("Data didn't load from " + Environment.getExternalStorageDirectory().toString() + "/wiytd.xls");
            e.printStackTrace();
        }
    }

    private List<String[]> ReadDataFromSheets(HSSFSheet myExcelSheet) {

        List<String[]> data = new ArrayList<>();
        HSSFRow currentRow = myExcelSheet.getRow(0);

        int mColumn = 0;
        int mColumnCount = 0;

        while (true) {
            try {
                String name = currentRow.getCell(mColumn).getStringCellValue();
                if ("".equals(name)) {
                    mColumnCount = mColumn;
                    break;
                }
                mColumn++;
            } catch (Exception e) {
                mColumnCount = mColumn;
                break;
            }
        }
        int mRow = 0;
        int mRowCount = 0;
        mColumn = 0;
        while (true) {
            currentRow = myExcelSheet.getRow(mRow);
            try {
                String name = "";
                if (currentRow.getCell(mColumn).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                    name = String.valueOf(currentRow.getCell(mColumn).getNumericCellValue());
                } else if (currentRow.getCell(mColumn).getCellType() == Cell.CELL_TYPE_STRING) {
                    name = currentRow.getCell(mColumn).getStringCellValue();
                }
                if ("".equals(name)) {
                    mRowCount = mRow;
                    break;
                }
                mRow++;
            } catch (Exception e) {
                mRowCount = mRow;
                break;
            }
        }
        StringBuilder mNewString = new StringBuilder();
        for (mRow = 0; mRow < mRowCount; mRow++) {
            currentRow = myExcelSheet.getRow(mRow);
            if (mRow != 0) {
                String[] entries = mNewString.toString().split(SYMBOL_SPLIT);
                data.add(entries);
                mNewString = new StringBuilder();
            }
            for (mColumn = 0; mColumn < mColumnCount; mColumn++) {
                try {
                    if (currentRow.getCell(mColumn).getCellType() == HSSFCell.CELL_TYPE_BLANK) {
                        mNewString.append("").append(SYMBOL_SPLIT);
                    } else if (currentRow.getCell(mColumn).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        int num = (int) currentRow.getCell(mColumn).getNumericCellValue();
                        mNewString.append(num).append(SYMBOL_SPLIT);
                    } else if (currentRow.getCell(mColumn).getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        String name = currentRow.getCell(mColumn).getStringCellValue();
                        mNewString.append(name).append(SYMBOL_SPLIT);
                    }
                } catch (Exception e) {
                    mNewString.append(0).append(SYMBOL_SPLIT);
                }
            }
        }
        String[] entries = mNewString.toString().split(SYMBOL_SPLIT);
        data.add(entries);
        return data;
    }

    private void writeDataToDB(Map<String, List<String[]>> sheets) throws Exception {

        for (Map.Entry<String, List<String[]>> entry : sheets.entrySet()
                ) {
            switch (entry.getKey()) {
                case "users":
                    createUsers(entry.getValue());
                    break;
                case "options":
                    createOptions(entry.getValue());
                    break;
                case "areas":
                    createAreas(entry.getValue());
                    break;
                case "projects":
                    createProjects(entry.getValue());
                    break;
                case "practices":
                    createPractices(entry.getValue());
                    break;
                case "practice_history":
                    createPracticeHistory(entry.getValue());
                    break;
                case "detailed_practice_history":
                    createDetailedPracticeHistory(entry.getValue());
                    break;
            }
        }
    }

    private void createDetailedPracticeHistory(List<String[]> rows) {
        for (int i = 1; i < rows.size(); i++) {
            int id = Integer.valueOf(rows.get(i)[0]);
            int idUser = Integer.valueOf(rows.get(i)[1]);
            int idPractice = Integer.valueOf(rows.get(i)[2]);
            long duration = Long.valueOf(rows.get(i)[3]);
            long time = Long.valueOf(rows.get(i)[4]);
            long date = Long.valueOf(rows.get(i)[5]);
            DetailedPracticeHistory detailedPracticeHistory = new DetailedPracticeHistory.Builder(id)
                    .addDuration(duration)
                    .addTime(time)
                    .addDate(date)
                    .build();
            if (DB.containsPractice(idPractice)) {
                detailedPracticeHistory.setPractice(DB.getPractice(idPractice));
            }
            detailedPracticeHistory.setUser(DB.getUser(idUser));
            detailedPracticeHistory.dbSave(DB);
        }
    }

    private void createPracticeHistory(List<String[]> rows) {
        for (int i = 1; i < rows.size(); i++) {
            int id = Integer.valueOf(rows.get(i)[0]);
            int idUser = Integer.valueOf(rows.get(i)[1]);
            int idPractice = Integer.valueOf(rows.get(i)[2]);
            long duration = Long.valueOf(rows.get(i)[3]);
            long lastTime = Long.valueOf(rows.get(i)[4]);
            long date = Long.valueOf(rows.get(i)[5]);
            PracticeHistory practiceHistory = new PracticeHistory.Builder(id)
                    .addDuration(duration)
                    .addLastTime(lastTime)
                    .addDate(date)
                    .build();
            if (DB.containsPractice(idPractice)) {
                practiceHistory.setPractice(DB.getPractice(idPractice));
            }
            practiceHistory.setUser(DB.getUser(idUser));
            practiceHistory.dbSave(DB);
        }
    }

    private void createPractices(List<String[]> rows) {
        for (int i = 1; i < rows.size(); i++) {
            int id = Integer.valueOf(rows.get(i)[0]);
            int idUser = Integer.valueOf(rows.get(i)[1]);
            int idProject = Integer.valueOf(rows.get(i)[2]);
            String name = rows.get(i)[3];
            int isActive = Integer.valueOf(rows.get(i)[4]);
            Practice practice = new Practice.Builder(id)
                    .addName(name)
                    .addIsActive(isActive)
                    .build();
            if (DB.containsProject(idProject)) {
                practice.setProject(DB.getProject(idProject));
            }
            practice.setUser(DB.getUser(idUser));
            practice.dbSave(DB);
        }
    }

    private void createProjects(List<String[]> rows) {
        for (int i = 1; i < rows.size(); i++) {
            int id = Integer.valueOf(rows.get(i)[0]);
            int idUser = Integer.valueOf(rows.get(i)[1]);
            int idArea = Integer.valueOf(rows.get(i)[2]);
            String name = rows.get(i)[3];
            Project project = new Project.Builder(id)
                    .addName(name)
                    .build();
            if (DB.containsArea(idArea)) {
                project.setArea(DB.getArea(idArea));
            }
            project.setUser(DB.getUser(idUser));
            project.dbSave(DB);
        }
    }

    private void createAreas(List<String[]> rows) {
        for (int i = 1; i < rows.size(); i++) {
            int id = Integer.valueOf(rows.get(i)[0]);
            int idUser = Integer.valueOf(rows.get(i)[1]);
            String name = rows.get(i)[2];
            int color = Integer.valueOf(rows.get(i)[3]);
            Area area = new Area.Builder(id)
                    .addName(name)
                    .addColor(color)
                    .build();
            area.setUser(DB.getUser(idUser));
            area.dbSave(DB);
        }
    }

    private void createOptions(List<String[]> rows) {
        for (int i = 1; i < rows.size(); i++) {
            int id = Integer.valueOf(rows.get(i)[0]);
            int idUser = Integer.valueOf(rows.get(i)[1]);
            int recoveryOnRunSwitch = Integer.valueOf(rows.get(i)[2]);
            int displayNotificationTimerSwitch = Integer.valueOf(rows.get(i)[3]);
            int saveInterval = Integer.valueOf(rows.get(i)[4]);
            int chronoIsWorking = Integer.valueOf(rows.get(i)[5]);
            int rowNumberInLists = Integer.valueOf(rows.get(i)[6]);
            Options options = new Options.Builder(id)
                    .addRecoverySwitch(recoveryOnRunSwitch)
                    .addDisplaySwitch(displayNotificationTimerSwitch)
                    .addSaveInterval(saveInterval)
                    .addChronoIsWorking(chronoIsWorking)
                    .addRowsNumberInLists(rowNumberInLists)
                    .build();
            options.setUser(DB.getUser(idUser));
            options.dbSave(DB);
        }
    }

    private void createUsers(List<String[]> rows) {
        for (int i = 1; i < rows.size(); i++) {
            int id = Integer.valueOf(rows.get(i)[0]);
            String name = rows.get(i)[1];
            int isCurrent = Integer.valueOf(rows.get(i)[2]);
            User user = new User.Builder(id).addName(name).addIsCurrentUser(isCurrent).build();
            user.dbSave(DB);
            if (user.isCurrentUser() == 1) {
                Session.sessionCurrentUser = user;
            }
        }
    }

    public void loadFromXLSFile() {

        File exportDir = new File(Environment.getExternalStorageDirectory(), "");
        if (exportDir.exists()) {
            File file = new File(exportDir, "wiytd.xls");
            if (file.exists()) {
                readFromFile(file);
            }
        }
    }

    /**************************************/

    private void updateScreen() {

        int mDayFromID = getResources().getIdentifier("tvDayFrom", "id", getPackageName());
        TextView etDayFrom = (TextView) findViewById(mDayFromID);
        if (etDayFrom != null) {
            if (mDateFrom == 0) {
                etDayFrom.setText("");
            } else {
                etDayFrom.setText(convertMillisToStringDate(mDateFrom));
            }
        }

        int mDayToID = getResources().getIdentifier("tvDayTo", "id", getPackageName());
        TextView etDayTo = (TextView) findViewById(mDayToID);
        if (etDayTo != null) {
            if (mDateTo == 0) {
                etDayTo.setText("");
            } else {
                etDayTo.setText(convertMillisToStringDate(mDateTo));
            }
        }

        RadioGroup radiogroup = (RadioGroup) findViewById(R.id.rgIncludeHistory);

        if (radiogroup != null) {
            radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId) {
                        case -1:
                            break;
                        case R.id.rbIncludeHistoryYes:
                            mIncludeHistory = true;
                            break;
                        case R.id.rbIncludeHistoryNo:
                            mIncludeHistory = false;
                            break;
                        default:
                            mIncludeHistory = false;
                            break;
                    }
                }
            });
        }
    }

    private boolean backgroundChronometerIsWorking() {
        if (sessionBackgroundChronometer != null && sessionBackgroundChronometer.isTicking()) {
            Toast toast = Toast.makeText(ActivityFileExportImport.this,
                    "Stop chronometer. You can't load data when chrono is working!", Toast.LENGTH_SHORT);
            toast.show();
            return true;
        }
        return false;
    }

    /****************
     * FORM ACTIONS
     ***************************/

    public void btExportToXLSFile_onClick(View view) {

        blink(view, this);
        exportToXLSFile();

    }

    private void day_onClick(boolean isBeginDate) {

        Intent intent = new Intent(ActivityFileExportImport.this, ActivityCalendarView.class);
        intent.putExtra("IsBeginDate", isBeginDate);
        intent.putExtra("CurrentActivity", "ActivityFileExportImport");

        int mDayFromID = getResources().getIdentifier("tvDayFrom", "id", getPackageName());
        TextView tvDayFrom = (TextView) findViewById(mDayFromID);
        intent.putExtra("CurrentDateInMillis", 0);
        intent.putExtra("CurrentDateToInMillis", "");
        if (tvDayFrom != null) {
            if (!"".equals(String.valueOf(tvDayFrom.getText()).trim())) {
                intent.putExtra("CurrentDateInMillis", convertStringToDate(String.valueOf(tvDayFrom.getText())).getTime());
            }
        }
        int mDayToID = getResources().getIdentifier("tvDayTo", "id", getPackageName());
        TextView tvDayTo = (TextView) findViewById(mDayToID);
        if (tvDayTo != null) {
            if (!"".equals(String.valueOf(tvDayTo.getText()).trim())) {
                intent.putExtra("CurrentDateToInMillis", convertStringToDate(String.valueOf(tvDayTo.getText())).getTime());
            }
        }
        startActivity(intent);
    }

    public void tvDayFrom_onClick(View view) {

        blink(view, this);
        day_onClick(true);
    }

    public void tvDayTo_onClick(View view) {

        blink(view, this);
        day_onClick(false);
    }

    public void btDayFromClear_onClick(final View view) {

        blink(view, this);
        int mDayFromID = getResources().getIdentifier("tvDayFrom", "id", getPackageName());
        TextView tvDayFrom = (TextView) findViewById(mDayFromID);
        if (tvDayFrom != null) {
            tvDayFrom.setText("");
        }

    }

    public void btDayToClear_onClick(final View view) {

        blink(view, this);
        int mDayToID = getResources().getIdentifier("tvDayTo", "id", getPackageName());
        TextView tvDayTo = (TextView) findViewById(mDayToID);
        if (tvDayTo != null) {
            tvDayTo.setText("");
        }
    }

    public void onBackPressed() {

        Intent intent = new Intent(getApplicationContext(), ActivityTools.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    public void btClose_onClick(View view) {

        blink(view, this);
        Intent intent = new Intent(ActivityFileExportImport.this, ActivityTools.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    public void btImportFromXLSFile_onClick(View view) {
        blink(view, this);
        importFromFile(new XLSloader());
    }

    public void importFromFile(final Fileloader loader) {

        if (backgroundChronometerIsWorking()) return;
        new AlertDialog.Builder(this)
                .setMessage("Do you want to clear database and load data from file?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            SQLiteDatabase db = DB.getWritableDatabase();
                            DB.ClearDB(db);
                            sessionCurrentUser = null;

                            loader.load();
                            db.close();

                            if (Session.sessionBackgroundChronometer != null && Session.sessionBackgroundChronometer.getService() != null) {
                                sessionBackgroundChronometer.getService().stopForeground(true);
                                sessionBackgroundChronometer.getService().stopSelf();
                            }
                            String message="Database cleared and filled by file data!";
                            Toast toast = Toast.makeText(ActivityFileExportImport.this,
                                    message, Toast.LENGTH_SHORT);
                            toast.show();
                            displayMessage(message);
                            setTitleOfActivity(ActivityFileExportImport.this);

                        } catch (Exception e) {
                            String message="Unable to connect to database!";
                            Toast toast = Toast.makeText(ActivityFileExportImport.this,
                                    message, Toast.LENGTH_SHORT);
                            toast.show();
                            displayMessage(message);
                        }
                    }
                }).setNegativeButton("No", null).show();
    }

    public void btExportToJSONFile_onClick(View view) {
        blink(view, this);

        getTablesFromDB();

        File fileZIP;
        try {

            fileZIP = new File(Environment.getExternalStorageDirectory() + "/wiytd.zip");
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(fileZIP));
            Gson gson = new Gson();
            ZipEntry e;

            e = new ZipEntry("users.txt");
            out.putNextEntry(e);
            String jsonUserList = gson.toJson(users);
            byte[] data = jsonUserList.toString().getBytes();
            out.write(data, 0, data.length);
            out.closeEntry();

            e = new ZipEntry("options.txt");
            out.putNextEntry(e);
            String jsonOptionsList = gson.toJson(options);
            data = jsonOptionsList.toString().getBytes();
            out.write(data, 0, data.length);
            out.closeEntry();

            e = new ZipEntry("areas.txt");
            out.putNextEntry(e);
            String jsonAreaList = gson.toJson(areas);
            data = jsonAreaList.toString().getBytes();
            out.write(data, 0, data.length);
            out.closeEntry();

            e = new ZipEntry("projects.txt");
            out.putNextEntry(e);
            String jsonProjectList = gson.toJson(projects);
            data = jsonProjectList.toString().getBytes();
            out.write(data, 0, data.length);
            out.closeEntry();

            e = new ZipEntry("practices.txt");
            out.putNextEntry(e);
            String jsonPracticeList = gson.toJson(practices);
            data = jsonPracticeList.toString().getBytes();
            out.write(data, 0, data.length);
            out.closeEntry();

            if (mIncludeHistory) {
                e = new ZipEntry("practice_history.txt");
                out.putNextEntry(e);
                String jsonPracticeHistoryList = gson.toJson(practiceHistories);
                data = jsonPracticeHistoryList.toString().getBytes();
                out.write(data, 0, data.length);
                out.closeEntry();

                e = new ZipEntry("detailed_practice_history.txt");
                out.putNextEntry(e);
                String jsonDetailedPracticeHistoryList = gson.toJson(detailedPracticeHistories);
                data = jsonDetailedPracticeHistoryList.toString().getBytes();
                out.write(data, 0, data.length);
                out.closeEntry();
            }

            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void btImportFromJSONFile_onClick(View view) {
        blink(view, this);

        importFromFile(new JSONloader());

    }

    public void btBackupBD_onClick(View view) {
        try {
            File dbFile = getApplicationContext().getDatabasePath("wiytd");
            FileInputStream fis = new FileInputStream(dbFile);

            String outFileName = Environment.getExternalStorageDirectory() + "/wiytd_copy.db";

            OutputStream output = new FileOutputStream(outFileName);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            output.flush();
            output.close();
            fis.close();
            String message="The database is successfully copied to " + outFileName;
            Toast toast = Toast.makeText(ActivityFileExportImport.this,
                    message, Toast.LENGTH_SHORT);
            toast.show();
            displayMessage(message);
        } catch (Exception e) {
            String message="Database copy failed!";
            Toast toast = Toast.makeText(ActivityFileExportImport.this,
                    message, Toast.LENGTH_SHORT);
            toast.show();
            displayMessage(message);
        }

    }

    private interface Fileloader {
        void load();
    }

    private class XLSloader implements Fileloader {
        @Override
        public void load() {
            loadFromXLSFile();
        }
    }

    private class JSONloader implements Fileloader {
        @Override
        public void load() {
            byte[] buffer = new byte[1024];
            try {
                ZipInputStream zis = new ZipInputStream(new FileInputStream(Environment.getExternalStorageDirectory()
                        + "/wiytd.zip"));
                ZipEntry ze;
                int read = 0;
                Gson gson = new Gson();
                while ((ze = zis.getNextEntry()) != null) {
                    if (ze.getName().equals("users.txt")) {
                        StringBuilder jsonUserList = new StringBuilder();
                        while ((read = zis.read(buffer, 0, 1024)) >= 0) {
                            jsonUserList.append(new String(buffer, 0, read));
                        }
                        Type type = new TypeToken<List<User>>() {
                        }.getType();
                        List<SavingIntoDB> userList = gson.fromJson(jsonUserList.toString(), type);
                        saveToDB(userList);
                    } else if (ze.getName().equals("options.txt")) {
                        StringBuilder jsonOptionsList = new StringBuilder();
                        while ((read = zis.read(buffer, 0, 1024)) >= 0) {
                            jsonOptionsList.append(new String(buffer, 0, read));
                        }
                        Type type = new TypeToken<List<Options>>() {
                        }.getType();
                        List<SavingIntoDB> optionsList = gson.fromJson(jsonOptionsList.toString(), type);
                        saveToDB(optionsList);
                    } else if (ze.getName().equals("areas.txt")) {
                        StringBuilder jsonAreaList = new StringBuilder();
                        while ((read = zis.read(buffer, 0, 1024)) >= 0) {
                            jsonAreaList.append(new String(buffer, 0, read));
                        }
                        Type type = new TypeToken<List<Area>>() {
                        }.getType();
                        List<SavingIntoDB> areaList = gson.fromJson(jsonAreaList.toString(), type);
                        saveToDB(areaList);
                    } else if (ze.getName().equals("projects.txt")) {
                        StringBuilder jsonProjectList = new StringBuilder();
                        while ((read = zis.read(buffer, 0, 1024)) >= 0) {
                            jsonProjectList.append(new String(buffer, 0, read));
                        }
                        Type type = new TypeToken<List<Project>>() {
                        }.getType();
                        List<SavingIntoDB> projectList = gson.fromJson(jsonProjectList.toString(), type);
                        saveToDB(projectList);
                    } else if (ze.getName().equals("practices.txt")) {
                        StringBuilder jsonPracticeList = new StringBuilder();
                        while ((read = zis.read(buffer, 0, 1024)) >= 0) {
                            jsonPracticeList.append(new String(buffer, 0, read));
                        }
                        Type type = new TypeToken<List<Practice>>() {
                        }.getType();
                        List<SavingIntoDB> practiceList = gson.fromJson(jsonPracticeList.toString(), type);
                        saveToDB(practiceList);
                    } else if (ze.getName().equals("practice_history.txt")) {
                        if (mIncludeHistory) {
                            StringBuilder jsonPracticeHistoryList = new StringBuilder();
                            while ((read = zis.read(buffer, 0, 1024)) >= 0) {
                                jsonPracticeHistoryList.append(new String(buffer, 0, read));
                            }
                            Type type = new TypeToken<List<PracticeHistory>>() {
                            }.getType();
                            List<SavingIntoDB> practiceHistoryList = gson.fromJson(jsonPracticeHistoryList.toString(), type);
                            saveToDB(practiceHistoryList);
                        }

                    } else if (ze.getName().equals("detailed_practice_history.txt")) {
                        if (mIncludeHistory) {
                            StringBuilder jsonDetailedPracticeHistoryList = new StringBuilder();
                            while ((read = zis.read(buffer, 0, 1024)) >= 0) {
                                jsonDetailedPracticeHistoryList.append(new String(buffer, 0, read));
                            }
                            Type type = new TypeToken<List<DetailedPracticeHistory>>() {
                            }.getType();
                            List<SavingIntoDB> detailedPracticeHistoryList = gson.fromJson(jsonDetailedPracticeHistoryList.toString(), type);
                            saveToDB(detailedPracticeHistoryList);
                        }
                    }
                }
                zis.closeEntry();
                zis.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void saveToDB(List<SavingIntoDB> entities) {
        for (SavingIntoDB entity : entities
                ) {
            entity.dbSave(DB);
        }
    }

    private class XMLloader implements Fileloader {
        @Override
        public void load() {

        }
    }

    private void displayMessage(String message) {
        int idMessage = getResources().getIdentifier("tvMessage", "id", getPackageName());
        TextView tvMessage = (TextView) findViewById(idMessage);
        if (tvMessage!=null) {
            tvMessage.setText(message);
        }
    }
}

