package com.katsuna.clock.formatters;

import android.content.Context;

import com.katsuna.clock.R;

import java.util.ArrayList;
import java.util.List;

public class DaysFormatter {

    public static String getDays(Context context, boolean monday, boolean tuesday,
                                 boolean wednesday, boolean thursday, boolean friday,
                                 boolean saturday, boolean sunday) {
        List<String> days = new ArrayList<>();
        if (monday) {
            days.add(getString(context, R.string.monday));
        }
        if (tuesday) {
            days.add(getString(context, R.string.tuesday));
        }
        if (wednesday) {
            days.add(getString(context, R.string.wednesday));
        }
        if (thursday) {
            days.add(getString(context, R.string.thursday));
        }
        if (friday) {
            days.add(getString(context, R.string.friday));
        }
        if (saturday) {
            days.add(getString(context, R.string.saturday));
        }
        if (sunday) {
            days.add(getString(context, R.string.sunday));
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < days.size(); i++) {
            sb.append(days.get(i));
            if (days.size() > (i+2)) {
                // we have at least two more to add
                sb.append(", ");
            } else if (days.size() > (i+1)) {
                // we have one more to add
                sb.append(" ").append(getString(context, R.string.and)).append(" ");
            }
        }

        return sb.toString();
    }

    private static String getString(Context context, int resId) {
        return context.getString(resId);
    }
}
