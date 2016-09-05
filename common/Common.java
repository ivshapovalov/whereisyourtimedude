package ru.brainworkout.whereisyourtimedude.common;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TableRow;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import ru.brainworkout.whereisyourtimedude.database.entities.User;

public class Common{

    public static ArrayList<Area> areas;
    public static LinkedList<PracticeTimer> practices = new LinkedList<>();
    public static Map<String, LinkedList<PracticeTimer>> DB = new TreeMap<>();

    static {
        areas = new ArrayList<>();

        areas.add(new Area(Color.GREEN, "AREA 1"));
        areas.add(new Area(Color.RED, "AREA 2"));
        areas.add(new Area(Color.YELLOW, "AREA 3"));
        areas.add(new Area(Color.MAGENTA, "AREA 4"));
        areas.add(new Area(Color.CYAN, "AREA 5"));
    }

    public static final String DATE_FORMAT_STRING = "yyyy-MM-dd";
    public static User dbCurrentUser;
    public static final boolean isDebug=true;

    public static Date ConvertStringToDate(final String date, final String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        Date d = null;
        try {
            d = dateFormat.parse(String.valueOf(date));
        } catch (ParseException e) {
            e.printStackTrace();

        }
        return d;
    }

    public static String ConvertDateToString(final Date date, final String format) {

        SimpleDateFormat dateformat = new SimpleDateFormat(format);
        String sDate = "";
        try {
            sDate = dateformat.format(date);
        } catch (Exception e) {
        }

        return sDate;

    }

    public static void blink(final View v) {
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(30);
        anim.setStartOffset(0);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(1);
        v.startAnimation(anim);
    }
//
//
//    public static void setTitleOfActivity(Activity currentActivity) {
//        if (Common.dbCurrentUser != null) {
//            currentActivity.setTitle(currentActivity.getTitle() + " : " + Common.dbCurrentUser.getName() + "");
//        }
//    }
//
    public static void HideEditorButton(Button btEditor) {

        if (btEditor != null) {
            TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
            params.span = 0;
            btEditor.setLayoutParams(params);
        }
    }

}
