package com.katsuna.clock.alarms;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.katsuna.clock.R;
import com.katsuna.clock.data.Alarm;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class AlarmsAdapter extends BaseAdapter {

    private List<Alarm> mAlarms;
    private final AlarmItemListener mItemListener;

    public AlarmsAdapter(List<Alarm> tasks, AlarmItemListener itemListener) {
        setList(tasks);
        mItemListener = itemListener;
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

        TextView title = rowView.findViewById(R.id.alarm_title);
        title.setText(alarm.getDescription());

        TextView description = rowView.findViewById(R.id.alarm_description);
        description.setText(alarm.getDescription());

        TextView days = rowView.findViewById(R.id.alarm_description);
        days.setText(alarm.getDescription());

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
                mItemListener.onAlarmTurnOff(alarm);
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