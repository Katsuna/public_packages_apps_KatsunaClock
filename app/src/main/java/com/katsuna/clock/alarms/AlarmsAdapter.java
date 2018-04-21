package com.katsuna.clock.alarms;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
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

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

class AlarmsAdapter extends BaseAdapter {

    private final AlarmItemListener mItemListener;
    private List<Alarm> mAlarms;
    private Alarm mAlarmFocused;

    AlarmsAdapter(List<Alarm> tasks, AlarmItemListener itemListener) {
        setList(tasks);
        mItemListener = itemListener;
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
        View rowView = view;
        if (rowView == null) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            rowView = inflater.inflate(R.layout.alarm, viewGroup, false);
        }

        final Alarm alarm = getItem(i);

        final Context context = viewGroup.getContext();
        AlarmFormatter alarmFormatter = new AlarmFormatter(context, alarm);

        ImageView alarmTypeIcon = rowView.findViewById(R.id.alarm_type_image);
        Drawable icon = context.getDrawable(alarmFormatter.getAlarmTypeIconResId());
        alarmTypeIcon.setImageDrawable(icon);

        TextView title = rowView.findViewById(R.id.alarm_title);
        title.setText(alarmFormatter.getTitle());

        TextView description = rowView.findViewById(R.id.alarm_description);
        description.setText(alarmFormatter.getDescription());

        TextView days = rowView.findViewById(R.id.alarm_days);
        days.setText(alarmFormatter.getDays());

        CardView alarmCard = rowView.findViewById(R.id.alarm_container_card);
        alarmCard.setCardBackgroundColor(ContextCompat.getColor(context,
                alarmFormatter.getCardHandleColor()));

        View alarmCardInner = rowView.findViewById(R.id.alarm_container_card_inner);
        alarmCardInner.setBackgroundColor(ContextCompat.getColor(context,
                alarmFormatter.getCardInnerColor()));
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

        Button editButton = rowView.findViewById(R.id.button_edit);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemListener.onAlarmEdit(alarm);
            }
        });

        Button turnOffButton = rowView.findViewById(R.id.button_turn_off);
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

        return rowView;
    }
}