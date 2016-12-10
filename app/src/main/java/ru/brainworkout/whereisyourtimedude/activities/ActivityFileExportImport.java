package ru.brainworkout.whereisyourtimedude.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ru.brainworkout.whereisyourtimedude.R;
import ru.brainworkout.whereisyourtimedude.common.Session;
import ru.brainworkout.whereisyourtimedude.database.entities.Area;
import ru.brainworkout.whereisyourtimedude.database.entities.DetailedPracticeHistory;
import ru.brainworkout.whereisyourtimedude.database.entities.Options;
import ru.brainworkout.whereisyourtimedude.database.entities.Practice;
import ru.brainworkout.whereisyourtimedude.database.entities.PracticeHistory;
import ru.brainworkout.whereisyourtimedude.database.entities.Project;
import ru.brainworkout.whereisyourtimedude.database.entities.User;
import ru.brainworkout.whereisyourtimedude.database.manager.SQLiteDatabaseManager;

import static ru.brainworkout.whereisyourtimedude.common.Common.*;

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

    public void exportToFile() {

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
        practiceHistories = DB.getAllPracticeHistory();
        detailedPracticeHistories = DB.getAllDetailedPracticeHistory();

        Map<String, List<String[]>> dataSheets = createDataArray();
        writeToFile(dataSheets);
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

            int mPath = getResources().getIdentifier("tvPathToFiles", "id", getPackageName());
            TextView tvPath = (TextView) findViewById(mPath);
            if (tvPath != null) {
                tvPath.setText("");
                tvPath.setText("В файл XLS по пути \n" + Environment.getExternalStorageDirectory().toString() + '\n'
                        + " успешно выгружены таблицы \n" + message.toString());
            }
        } catch (Exception e) {
            int mPath = getResources().getIdentifier("tvPathToFiles", "id", getPackageName());
            TextView tvPath = (TextView) findViewById(mPath);
            if (tvPath != null) {
                tvPath.setText("Файл не выгружен в " + Environment.getExternalStorageDirectory().toString());
            }
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
            int mPath = getResources().getIdentifier("tvPathToFiles", "id", getPackageName());
            TextView tvPath = (TextView) findViewById(mPath);
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

            if (tvPath != null && errorMessage.length() != 0) {
                errorMessage.append("Data didn't load from ").append(Environment.getExternalStorageDirectory().toString())
                        .append("/wiytd.xls");
                tvPath.setText(errorMessage.toString());
            }
            myExcelBook.close();
            writeDataToDB(sheets);

        } catch (Exception e) {
            int mPath = getResources().getIdentifier("tvPathToFiles", "id", getPackageName());
            TextView tvPath = (TextView) findViewById(mPath);
            if (tvPath != null) {
                tvPath.setText("Data didn't load from " + Environment.getExternalStorageDirectory().toString() + "/wiytd.xls");
            }
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

        for (Map.Entry<String,List<String[]>> entry:sheets.entrySet()
             ) {
            switch (entry.getKey()) {
                case "users": createUsers(entry.getValue()); break;
                case "options": createOptions(entry.getValue());break;
                case "areas": createAreas(entry.getValue());break;
                case "projects": createProjects(entry.getValue());break;
                case "practices": createPractices(entry.getValue());break;
                case "practice_history": createPracticeHistory(entry.getValue());break;
                case "detailed_practice_history": createDetailedPracticeHistory(entry.getValue());break;
            }
        }
    }

    private void createDetailedPracticeHistory(List<String[]> rows) {
        for (int i = 1; i <rows.size() ; i++) {
            int id=Integer.valueOf(rows.get(i)[0]);
            int idUser=Integer.valueOf(rows.get(i)[1]);
            int idPractice=Integer.valueOf(rows.get(i)[2]);
            long duration=Long.valueOf(rows.get(i)[3]);
            long time=Long.valueOf(rows.get(i)[4]);
            long date=Long.valueOf(rows.get(i)[5]);
            DetailedPracticeHistory detailedPracticeHistory=new DetailedPracticeHistory.Builder(id)
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
        for (int i = 1; i <rows.size() ; i++) {
            int id=Integer.valueOf(rows.get(i)[0]);
            int idUser=Integer.valueOf(rows.get(i)[1]);
            int idPractice=Integer.valueOf(rows.get(i)[2]);
            long duration=Long.valueOf(rows.get(i)[3]);
            long lastTime=Long.valueOf(rows.get(i)[4]);
            long date=Long.valueOf(rows.get(i)[5]);
            PracticeHistory practiceHistory=new PracticeHistory.Builder(id)
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
        for (int i = 1; i <rows.size() ; i++) {
            int id=Integer.valueOf(rows.get(i)[0]);
            int idUser=Integer.valueOf(rows.get(i)[1]);
            int idProject=Integer.valueOf(rows.get(i)[2]);
            String name=rows.get(i)[3];
            int isActive=Integer.valueOf(rows.get(i)[4]);
            Practice practice=new Practice.Builder(id)
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
        for (int i = 1; i <rows.size() ; i++) {
            int id=Integer.valueOf(rows.get(i)[0]);
            int idUser=Integer.valueOf(rows.get(i)[1]);
            int idArea=Integer.valueOf(rows.get(i)[2]);
            String name=rows.get(i)[3];
            Project project =new Project.Builder(id)
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
        for (int i = 1; i <rows.size() ; i++) {
            int id=Integer.valueOf(rows.get(i)[0]);
            int idUser=Integer.valueOf(rows.get(i)[1]);
            String name=rows.get(i)[2];
            int color=Integer.valueOf(rows.get(i)[3]);
            Area area =new Area.Builder(id)
                    .addName(name)
                    .addColor(color)
                    .build();
            area.setUser(DB.getUser(idUser));
            area.dbSave(DB);
        }

    }

    private void createOptions(List<String[]> rows) {
        for (int i = 1; i <rows.size() ; i++) {
            int id=Integer.valueOf(rows.get(i)[0]);
            int idUser=Integer.valueOf(rows.get(i)[1]);
            int recoveryOnRunSwitch=Integer.valueOf(rows.get(i)[2]);
            int displayNotificationTimerSwitch=Integer.valueOf(rows.get(i)[3]);
            int saveInterval=Integer.valueOf(rows.get(i)[4]);
            int chronoIsWorking=Integer.valueOf(rows.get(i)[5]);
            Options options =new Options.Builder(id)
                    .addRecoverySwitch(recoveryOnRunSwitch)
                    .addDisplaySwitch(displayNotificationTimerSwitch)
                    .addSaveInterval(saveInterval)
                    .addChronoIsWorking(chronoIsWorking).build();
            options.setUser(DB.getUser(idUser));
            options.dbSave(DB);
        }
    }

    private void createUsers(List<String[]> rows) {
        for (int i = 1; i <rows.size() ; i++) {
            int id=Integer.valueOf(rows.get(i)[0]);
            String name=rows.get(i)[1];
            int isCurrent=Integer.valueOf(rows.get(i)[2]);
            User user=new User.Builder(id).addName(name).addIsCurrentUser(isCurrent).build();
            user.dbSave(DB);
            if (user.isCurrentUser()==1) {
                Session.sessionCurrentUser=user;
            }
        }

    }

    public void loadFromFile() {

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

    /****************
     * FORM ACTIONS
     ***************************/

    public void btExportToFile_onClick(View view) {

        blink(view, this);
        exportToFile();

    }

    public void btImportFromFile_onClick(View view) {

        blink(view, this);
        loadFromFile();

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

}

