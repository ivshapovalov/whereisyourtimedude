package ru.brainworkout.whereisyourtimedude.common;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Vibrator;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TableRow;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import ru.brainworkout.whereisyourtimedude.database.entities.Area;
import ru.brainworkout.whereisyourtimedude.database.entities.DetailedPracticeHistory;
import ru.brainworkout.whereisyourtimedude.database.entities.Options;
import ru.brainworkout.whereisyourtimedude.database.entities.Practice;
import ru.brainworkout.whereisyourtimedude.database.entities.PracticeHistory;
import ru.brainworkout.whereisyourtimedude.database.entities.Project;
import ru.brainworkout.whereisyourtimedude.database.entities.User;
import ru.brainworkout.whereisyourtimedude.database.manager.SQLiteDatabaseManager;

public class Common {

    public static final String SYMBOL_DATE_FORMAT = "yyyy-MM-dd";
    public static final String SYMBOL_TIME_FORMAT = "HH:mm:ss";
    public static final String SYMBOL_EDIT = "►";
    public static final String SYMBOL_PLAY = "►";
    public static final String SYMBOL_STOP = "■";

    public static final boolean isDebug = true;

    public static String convertStackTraceToString(StackTraceElement[] stackTraceElements) {
        StringBuilder message = new StringBuilder();
        int min = Math.min(stackTraceElements.length, 4);
        for (int i = 2; i < min; i++) {
            StackTraceElement element = stackTraceElements[i];
            message.append(element.getClassName()).append(": ").append(element.getMethodName()).append("\n");
        }
        return message.toString();
    }

    public static Date convertStringToDate(final String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(SYMBOL_DATE_FORMAT);
        Date d = null;
        try {
            d = dateFormat.parse(String.valueOf(date));
        } catch (ParseException e) {
            e.printStackTrace();

        }
        return d;
    }

    public static String convertMillisToStringDate(final long millis) {
        return convertDateToStringDate(new Date(millis));
    }

    public static String convertMillisToStringTime(final long millis) {
        return convertDateToStringTime(new Date(millis));
    }

    public static String convertMillisToStringWithAllTime(long millis) {

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        long hours = TimeUnit.MILLISECONDS.toHours(millis) - TimeUnit.DAYS.toHours(days);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) -
                TimeUnit.DAYS.toMinutes(days) - TimeUnit.HOURS.toMinutes(hours);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis));
        //millis -= TimeUnit.SECONDS.toMillis(second);
        String timeString = String.format("%02d:%02d:%02d:%02d", days, hours, minutes, seconds);
        return timeString;
    }

    public static String convertMillisToStringDateTime(final long millis) {
        return convertDateToStringDateTime(new Date(millis));
    }

    public static String convertDateToStringDateTime(final Date date) {

        SimpleDateFormat dateformat = new SimpleDateFormat(SYMBOL_DATE_FORMAT + " " + SYMBOL_TIME_FORMAT);
        String sDate = "";
        try {
            sDate = dateformat.format(date);
        } catch (Exception e) {
        }

        return sDate;

    }

    public static String convertDateToStringTime(final Date date) {

        SimpleDateFormat dateformat = new SimpleDateFormat(SYMBOL_TIME_FORMAT);
        String sDate = "";
        try {
            sDate = dateformat.format(date);
        } catch (Exception e) {
        }

        return sDate;

    }

    public static String convertDateToStringDate(final Date date) {

        SimpleDateFormat dateformat = new SimpleDateFormat(SYMBOL_DATE_FORMAT);
        String sDate = "";
        try {
            sDate = dateformat.format(date);
        } catch (Exception e) {
        }

        return sDate;

    }

    public static void blink(final View v, Activity activity) {

        long mills = 100L;
        Vibrator vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(mills);

        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(30);
        anim.setStartOffset(0);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(1);
        v.startAnimation(anim);
    }

    public static void setTitleOfActivity(Activity currentActivity) {
        if (Session.sessionCurrentUser != null) {
            CharSequence title = currentActivity.getTitle();
            if (title.toString().contains("(")) {
                title = title.subSequence(0, title.toString().indexOf("("));
            }
            title = title + "(" + Session.sessionCurrentUser.getName() + ")";
            currentActivity.setTitle(title);
        } else {
            CharSequence title = currentActivity.getTitle();
            if (title.toString().contains("(")) {
                title = title.subSequence(0, title.toString().indexOf("("));
            }
            currentActivity.setTitle(title);
        }
    }

    public static long getBeginOfCurrentDateInMillis() {
        Calendar calendar = Calendar.getInstance();
        calendar.clear(Calendar.HOUR);
        calendar.clear(Calendar.HOUR_OF_DAY);
        calendar.clear(Calendar.MINUTE);
        calendar.clear(Calendar.SECOND);
        calendar.clear(Calendar.MILLISECOND);
        return calendar.getTimeInMillis();
    }

    public static void hideEditorButton(Button btEditor) {

        if (btEditor != null) {
            TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
            params.span = 0;
            btEditor.setLayoutParams(params);
        }
    }

    static ArrayList<Integer> AlphabetColors() {
        ArrayList<Integer> AlphabetColors = new ArrayList<>();
        AlphabetColors.add(Color.parseColor("#FF2020"));//
        AlphabetColors.add(Color.parseColor("#000080"));//
        AlphabetColors.add(Color.parseColor("#006400"));//
        AlphabetColors.add(Color.parseColor("#FF1493"));//
        AlphabetColors.add(Color.parseColor("#7CFC00"));//
        AlphabetColors.add(Color.parseColor("#00BFFF"));//
        AlphabetColors.add(Color.parseColor("#FFFF00"));//
        AlphabetColors.add(Color.parseColor("#9C9C9C"));//
        AlphabetColors.add(Color.parseColor("#CD5C5C"));//
        AlphabetColors.add(Color.parseColor("#836FFF"));
        AlphabetColors.add(Color.parseColor("#08bf57"));//
        AlphabetColors.add(Color.parseColor("#AFEEEE"));//
        AlphabetColors.add(Color.parseColor("#EE9A00"));//
        AlphabetColors.add(Color.parseColor("#dac612"));//
        AlphabetColors.add(Color.parseColor("#551A8B"));//
        AlphabetColors.add(Color.parseColor("#FF83FA"));//
        AlphabetColors.add(Color.parseColor("#20B2AA"));//
        AlphabetColors.add(Color.parseColor("#FFDEAD"));//
        AlphabetColors.add(Color.parseColor("#6B8E23"));//
        AlphabetColors.add(Color.parseColor("#dac612"));//
        AlphabetColors.add(Color.parseColor("#A56A2A"));//
        AlphabetColors.add(Color.parseColor("#790a04"));//

        return AlphabetColors;
    }

    public static void defaultTestFilling(SQLiteDatabaseManager DB) {

        Random random = new Random();
        final int USERS_COUNT = 5;
        final int AREAS_COUNT = 10;
        final int PROJECTS_COUNT = 10;
        final int PRACTICES_COUNT = 100;

        int maxUser = DB.getUserMaxNumber();
        //Users
        for (int i = 1; i <= USERS_COUNT; i++) {
            User a = new User.Builder(maxUser + i).addName("User " + i).build();
            a.dbSave(DB);
        }
        int currentUserIndex = random.nextInt(USERS_COUNT) + maxUser + 1;

        User currentUser = DB.getUser(currentUserIndex);
        currentUser.setIsCurrentUser(1);
        currentUser.dbSave(DB);
        Session.sessionCurrentUser = currentUser;
        Options options = new Options.Builder(DB).addSaveInterval(1).addDisplaySwitch(1).addChronoIsWorking(0).build();
        options.dbSave(DB);

        //Areas
        for (int i = 1; i <= AREAS_COUNT; i++) {
            Area area = new Area.Builder(DB).addName("Область  " + i).addColor(AlphabetColors().get(i)).build();
            area.dbSave(DB);
        }

        //Projects
        for (int i = 1; i <= PROJECTS_COUNT; i++) {
            int idArea = random.nextInt(AREAS_COUNT) + 1;
            Project project = new Project.Builder(DB).addName("Проект " + i).addArea(DB.getArea(idArea)).build();
            project.dbSave(DB);
        }

        //Practices
        for (int i = 1; i <= PRACTICES_COUNT; i++) {
            int idProject = random.nextInt(PROJECTS_COUNT) + 1;
            Practice practice = new Practice.Builder(DB).addName("Занятие " + i).addProject(DB.getProject(idProject)).addIsActive(1).build();
            practice.dbSave(DB);
        }

        //Detailed practice history count
        final int DETAILED_PRACTICE_HISTORY_COUNT = 150;
        final int DETAILED_PRACTICE_HISTORY_DAYS_BEFORE_TODAY=2;
        final int DETAILED_PRACTICE_HISTORY_MAX_DURATION_IN_SECONDS = 300;

        int practice_history_number=1;
        int detailed_practice_history_number=1;
        List<DetailedPracticeHistory> detailedPracticeHistoryList=new ArrayList<>();
        long currentDaysInMillis=  getBeginOfCurrentDateInMillis();
        for (int day = 1; day <= DETAILED_PRACTICE_HISTORY_DAYS_BEFORE_TODAY; day++) {
            long practiceDay=currentDaysInMillis-day*3600*24*1000;
            long nextDay=practiceDay+3600*24*1000;

            Map<Integer,PracticeHistory> practiceHistories=new HashMap<>();

            long time=practiceDay;
            while (true) {
                int idPractice = random.nextInt(PRACTICES_COUNT) + 1;
                long  durationInSeconds=random.nextInt(DETAILED_PRACTICE_HISTORY_MAX_DURATION_IN_SECONDS);

                if (time+durationInSeconds*1000>=nextDay) {
                    break;
                }
                DetailedPracticeHistory detailedPracticeHistory = new DetailedPracticeHistory.Builder(detailed_practice_history_number++)
                        .addPractice(DB.getPractice(idPractice))
                        .addDate(practiceDay)
                        .addTime(time)
                        .addDuration(durationInSeconds)
                        .build();
                detailedPracticeHistoryList.add(detailedPracticeHistory);
                PracticeHistory practiceHistory = new PracticeHistory.Builder(practice_history_number++)
                        .addPractice(DB.getPractice(idPractice))
                        .addDate(practiceDay)
                        .addLastTime(time)
                        .addDuration(durationInSeconds)
                        .build();
                //search in map
                time+=durationInSeconds*1000;
                if (practiceHistories.containsKey(idPractice)) {
                    PracticeHistory existingPracticeHistory=practiceHistories.get(idPractice);
                    existingPracticeHistory.setLastTime(time);
                    existingPracticeHistory.setDuration(existingPracticeHistory.getDuration()+durationInSeconds);

                } else {
                    practiceHistories.put(idPractice,practiceHistory);
                }
                }
            for (Map.Entry<Integer,PracticeHistory> entry :practiceHistories.entrySet()
                    ) {
                entry.getValue().dbSave(DB);
            }
        }
        for (DetailedPracticeHistory currentDetailedPracticeHistory:detailedPracticeHistoryList
             ) {
            currentDetailedPracticeHistory.dbSave(DB);
        }
    }

    public static TableRow.LayoutParams paramsTextViewWithSpanInList(int i) {
        TableRow.LayoutParams paramsTextView = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
        paramsTextView.span=i;
        return paramsTextView;
    }
}
