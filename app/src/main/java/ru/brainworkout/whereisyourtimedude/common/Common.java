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
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import ru.brainworkout.whereisyourtimedude.activities.ActivityChrono;
import ru.brainworkout.whereisyourtimedude.activities.ActivityMain;
import ru.brainworkout.whereisyourtimedude.database.entities.Area;
import ru.brainworkout.whereisyourtimedude.database.entities.Options;
import ru.brainworkout.whereisyourtimedude.database.entities.Practice;
import ru.brainworkout.whereisyourtimedude.database.entities.Project;
import ru.brainworkout.whereisyourtimedude.database.entities.User;
import ru.brainworkout.whereisyourtimedude.database.manager.DatabaseManager;

import static ru.brainworkout.whereisyourtimedude.common.Session.sessionCurrentUser;

public class Common {

    public static final String SYMBOL_DATE_FORMAT = "yyyy-MM-dd";
    public static final String SYMBOL_TIME_FORMAT = "HH:mm:ss";
    public static final String SYMBOL_EDIT = "►";
   // public static final String SYMBOL_PLAY = "►";
  //  public static final String SYMBOL_STOP = "■";

    public static final boolean isDebug = true;



    public static String convertStackTraceToString(StackTraceElement[] stackTraceElements) {
        StringBuilder message = new StringBuilder();
        int min=Math.min(stackTraceElements.length,4);
        for (int i = 2; i < min; i++) {
            StackTraceElement element = stackTraceElements[i];
            message.append(element.getClassName()).append(": ").append(element.getMethodName()).append("\n");
        }
        return message.toString();
    }

    public static Date ConvertStringToDate(final String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(SYMBOL_DATE_FORMAT);
        Date d = null;
        try {
            d = dateFormat.parse(String.valueOf(date));
        } catch (ParseException e) {
            e.printStackTrace();

        }
        return d;
    }

    public static String ConvertMillisToStringDate(final long millis) {
        return ConvertDateToStringDate(new Date(millis));
    }

    public static String ConvertMillisToStringTime(final long millis) {
        return ConvertDateToStringTime(new Date(millis));
    }

    public static String ConvertMillisToStringWithAllTime(long millis) {

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

    public static String ConvertMillisToStringDateTime(final long millis) {
        return ConvertDateToStringDateTime(new Date(millis));
    }

    public static String ConvertDateToStringDateTime(final Date date) {

        SimpleDateFormat dateformat = new SimpleDateFormat(SYMBOL_DATE_FORMAT + " " + SYMBOL_TIME_FORMAT);
        String sDate = "";
        try {
            sDate = dateformat.format(date);
        } catch (Exception e) {
        }

        return sDate;

    }

    public static String ConvertDateToStringTime(final Date date) {

        SimpleDateFormat dateformat = new SimpleDateFormat(SYMBOL_TIME_FORMAT);
        String sDate = "";
        try {
            sDate = dateformat.format(date);
        } catch (Exception e) {
        }

        return sDate;

    }

    public static String ConvertDateToStringDate(final Date date) {

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
//            if (Session.sessionBackgroundChronometer.isTicking()) {
//                currentActivity.setTitle(Session.sessionCurrentUser.getName() + ":" + currentActivity.getTitle() + " ("+Session.sessionBackgroundChronometer.getGlobalChronometerCountInSeconds()+")");
//            } else {
                currentActivity.setTitle(currentActivity.getTitle()+"("+Session.sessionCurrentUser.getName() + ")");

//            }
        }
    }

    public static void HideEditorButton(Button btEditor) {

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

    public static void DefaultTestFilling(DatabaseManager DB) {

        Random random= new Random();
        final int USERS_COUNT=5;
        final int AREAS_COUNT=10;
        final int PROJECTS_COUNT=20;
        final int PRACTICES_COUNT=50;

        int maxUser=DB.getUserMaxNumber();
        //Users
        for (int i = 1; i <= USERS_COUNT; i++) {
            User a=new User.Builder(maxUser+i).addName("User "+ i).build();
            a.dbSave(DB);
        }
        int currentUserIndex=random.nextInt(USERS_COUNT)+maxUser+1;

        User currentUser=DB.getUser(currentUserIndex);
        currentUser.setIsCurrentUser(1);
        currentUser.dbSave(DB);
        Session.sessionCurrentUser =currentUser;


        Options options=new Options.Builder(DB).addSaveInterval(1).addDisplaySwitch(1).addChronoIsWorking(0).build();
        options.dbSave(DB);

        //Areas
        for (int i = 1; i <= AREAS_COUNT; i++) {
            Area a=new Area.Builder(DB).addName("Область  "+ i).addColor(AlphabetColors().get(i)).build();
            //Area a=new Area.Builder(DB).addName("Область  "+ i).build();
            //a.setIdUser(currentUserIndex);
            a.dbSave(DB);
        }

        //Projects
        for (int i = 1; i <= PROJECTS_COUNT; i++) {
            int idArea=random.nextInt(AREAS_COUNT)+1;
            //System.out.println("idArea="+idArea);
            Project a=new Project.Builder(DB).addName("Проект "+ i).addIdArea(idArea).build();
            //a.setIdUser(currentUserIndex);
            a.dbSave(DB);
        }

        //Practices
        for (int i = 1; i <= PRACTICES_COUNT; i++) {
            int idProject=random.nextInt(PROJECTS_COUNT)+1;
            //System.out.println("idProject="+idProject);
            Practice a=new Practice.Builder(DB).addName("Занятие "+ i).addIDProject(idProject).addIsActive(1).build();
            //a.setIdUser(currentUserIndex);
            a.dbSave(DB);
        }


    }
}
