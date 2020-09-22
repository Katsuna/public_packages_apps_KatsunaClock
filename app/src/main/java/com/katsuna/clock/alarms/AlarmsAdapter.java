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
package com.katsuna.clock.alarms;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.katsuna.clock.R;
import com.katsuna.clock.data.Alarm;
import com.katsuna.clock.data.AlarmStatus;
import com.katsuna.clock.formatters.AlarmFormatter;
import com.katsuna.commons.entities.OpticalParams;
import com.katsuna.commons.entities.SizeProfileKeyV2;
import com.katsuna.commons.entities.UserProfile;
import com.katsuna.commons.utils.ColorAdjusterV2;
import com.katsuna.commons.utils.IUserProfileProvider;
import com.katsuna.commons.utils.SizeAdjuster;
import com.katsuna.commons.utils.SizeCalcV2;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

class AlarmsAdapter extends BaseAdapter {

    private final AlarmItemListener mItemListener;
    private final IUserProfileProvider mUserProfileProvider;
    private List<Alarm> mAlarms;
    private Alarm mAlarmFocused;

    AlarmsAdapter(List<Alarm> tasks, AlarmItemListener itemListener,
                  IUserProfileProvider userProfileProvider) {
        setList(tasks);
        mItemListener = itemListener;
        mUserProfileProvider = userProfileProvider;
    }

    public void focusOnAlarm(Alarm alarm, boolean focus) {
        mAlarmFocused = focus ? alarm : null;
        notifyDataSetChanged();
    }

    public void reloadAlarm(Alarm alarm) {
        int itemIndex = mAlarms.indexOf(alarm);
        if (itemIndex != -1) {
            mAlarms.set(itemIndex, alarm);
        }

        notifyDataSetChanged();
    }

    public void replaceData(List<Alarm> alarms) {
        setList(alarms);
        notifyDataSetChanged();
    }

    private void setList(List<Alarm> alarms) {
        mAlarms = checkNotNull(alarms);
    }

    @Override
    public int getCount() {
        return mAlarms.size();
    }

    @Override
    public Alarm getItem(int i) {
        return mAlarms.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        UserProfile userProfile = mUserProfileProvider.getProfile();

        View rowView = view;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        if (rowView == null) {
            inflater = LayoutInflater.from(viewGroup.getContext());
            if (userProfile.isRightHanded) {
                rowView = inflater.inflate(R.layout.alarm, viewGroup, false);
            } else {
                rowView = inflater.inflate(R.layout.alarm_lh, viewGroup, false);
            }
        }

        final Alarm alarm = getItem(i);

        final Context context = viewGroup.getContext();
        AlarmFormatter alarmFormatter = new AlarmFormatter(context, alarm);

        ImageView alarmTypeIcon = rowView.findViewById(R.id.alarm_type_image);
        Drawable icon = context.getDrawable(alarmFormatter.getAlarmTypeIconResId());
        alarmTypeIcon.setImageDrawable(icon);

        TextView title = rowView.findViewById(R.id.alarm_title);
        title.setText(alarmFormatter.getTitle());

        TextView alarmDays = rowView.findViewById(R.id.alarm_days);
        String days = alarmFormatter.getDaysFrequency();
        if (TextUtils.isEmpty(days)) {
            alarmDays.setVisibility(View.GONE);
        } else {
            alarmDays.setText(days);
            alarmDays.setVisibility(View.VISIBLE);
        }

        TextView ringTime = rowView.findViewById(R.id.alarm_ring_time);
        ringTime.setText(alarmFormatter.getRingInTime());

        CardView alarmCard = rowView.findViewById(R.id.alarm_container_card);
        alarmCard.setCardBackgroundColor(ContextCompat.getColor(context,
                alarmFormatter.getCardHandleColor(userProfile)));

        View alarmCardInner = rowView.findViewById(R.id.alarm_container_card_inner);
        alarmCardInner.setBackgroundColor(ContextCompat.getColor(context,
                alarmFormatter.getCardInnerColor(userProfile)));
        alarmCardInner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemListener.onAlarmFocus(alarm, !alarm.equals(mAlarmFocused));
            }
        });

        View actionsContainer = rowView.findViewById(R.id.alarm_buttons_container);
        if (alarm.equals(mAlarmFocused)) {
            actionsContainer.setVisibility(View.VISIBLE);
        } else {
            actionsContainer.setVisibility(View.GONE);
        }

        ViewGroup buttonsWrapper = rowView.findViewById(R.id.action_buttons_wrapper);
        if (userProfile.isRightHanded) {
            View buttonsView = inflater.inflate(R.layout.action_buttons_rh, buttonsWrapper,
                    false);
            buttonsWrapper.removeAllViews();
            buttonsWrapper.addView(buttonsView);
        } else {
            View buttonsView = inflater.inflate(R.layout.action_buttons_lh, buttonsWrapper,
                    false);
            buttonsWrapper.removeAllViews();
            buttonsWrapper.addView(buttonsView);
        }

        Button editButton = rowView.findViewById(R.id.button_edit);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemListener.onAlarmEdit(alarm);
            }
        });

        final Button turnOffButton = rowView.findViewById(R.id.button_turn_off);
        if (alarm.getAlarmStatus() == AlarmStatus.ACTIVE) {
            turnOffButton.setText(R.string.turn_off);
        } else {
            turnOffButton.setText(R.string.turn_on);
        }
        turnOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alarm.getAlarmStatus() == AlarmStatus.ACTIVE) {
                    mItemListener.onAlarmStatusUpdate(alarm, AlarmStatus.INACTIVE);
                } else {
                    mItemListener.onAlarmStatusUpdate(alarm, AlarmStatus.ACTIVE);
                }
            }
        });

        TextView deleteText = rowView.findViewById(R.id.txt_delete);
        deleteText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemListener.onAlarmDelete(alarm);
            }
        });

        // adjust buttons
        OpticalParams opticalParams = SizeCalcV2.getOpticalParams(SizeProfileKeyV2.BUTTON,
                userProfile.opticalSizeProfile);
        SizeAdjuster.adjustText(context, editButton, opticalParams);
        SizeAdjuster.adjustText(context, turnOffButton, opticalParams);

        // more text
        opticalParams = SizeCalcV2.getOpticalParams(SizeProfileKeyV2.BUTTON,
                userProfile.opticalSizeProfile);
        SizeAdjuster.adjustText(context, deleteText, opticalParams);

        ColorAdjusterV2.adjustButtons(context, userProfile, editButton, turnOffButton, deleteText);


        return rowView;
    }
}