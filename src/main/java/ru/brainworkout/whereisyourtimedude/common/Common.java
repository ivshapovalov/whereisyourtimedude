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

public class Common {

//    public static ArrayList<Area> areas;
//    public static LinkedList<PracticeHistory> practices = new LinkedList<>();
//    public static Map<Long, LinkedList<PracticeHistory>> DB = new TreeMap<>();
//
//    static {
//        areas = new ArrayList<>();
//
//        ArrayList<Integer> colors = AlphabetColors();
//        for (int i = 0; i < colors.size(); i++
//                ) {
//            areas.add(new Area(colors.get(i), "AREA " + i));
//        }
//
//        areas.add(new Area(Color.GREEN, "AREA 1"));
//        areas.add(new Area(Color.RED, "AREA 2"));
//        areas.add(new Area(Color.YELLOW, "AREA 3"));
//        areas.add(new Area(Color.MAGENTA, "AREA 4"));
//        areas.add(new Area(Color.CYAN, "AREA 5"));
//    }

    public static final String DATE_FORMAT_STRING = "yyyy-MM-dd";
    public static final String TIME_FORMAT_STRING = "HH-mm-ss";

    public static final boolean isDebug = true;

    public static Date ConvertStringToDate(final String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_STRING);
        Date d = null;
        try {
            d = dateFormat.parse(String.valueOf(date));
        } catch (ParseException e) {
            e.printStackTrace();

        }
        return d;
    }

    public static Date ConvertMillisToDate(final long Millis) {
        return new Date(Millis);
    }

    public static String ConvertMillisToStringDate(final long Millis) {
        return ConvertDateToString(new Date(Millis));
    }

    public static String ConvertMillisToStringTime(final long Millis) {
        return ConvertTimeToString(new Date(Millis));
    }

    public static String ConvertDateToString(final Date date) {

        SimpleDateFormat dateformat = new SimpleDateFormat(DATE_FORMAT_STRING);
        String sDate = "";
        try {
            sDate = dateformat.format(date);
        } catch (Exception e) {
        }

        return sDate;

    }

    public static String ConvertTimeToString(final Date date) {

        SimpleDateFormat dateformat = new SimpleDateFormat(TIME_FORMAT_STRING);
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

    public static void setTitleOfActivity(Activity currentActivity) {
        if (Session.sessionUser != null) {
            currentActivity.setTitle(currentActivity.getTitle() + " : " + Session.sessionUser.getName() + "");
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

}