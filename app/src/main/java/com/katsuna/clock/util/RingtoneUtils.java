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
package com.katsuna.clock.util;

import android.content.Context;
import android.database.Cursor;
import android.media.RingtoneManager;

import java.util.ArrayList;
import java.util.List;

public class RingtoneUtils {

    public static List<KatsunaRingtone> getAllRingtones(Context context) {
        List<KatsunaRingtone> ringtones = new ArrayList<>();


        RingtoneManager manager = new RingtoneManager(context);
        manager.setType(RingtoneManager.TYPE_RINGTONE);
        Cursor cursor = manager.getCursor();

        while (cursor.moveToNext()) {
            long id = cursor.getLong(RingtoneManager.ID_COLUMN_INDEX);
            String title = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
            String uri = cursor.getString(RingtoneManager.URI_COLUMN_INDEX);
            KatsunaRingtone ringtone = new KatsunaRingtone(id, title, uri);
            ringtones.add(ringtone);
        }

        return ringtones;
    }

}
