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
