/**
* Copyright (C) 2020 Manos Saratsis
*
* This file is part of Katsuna.
*
* Katsuna is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Katsuna is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with Katsuna.  If not, see <https://www.gnu.org/licenses/>.
*/
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
