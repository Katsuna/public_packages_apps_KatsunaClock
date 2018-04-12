package com.katsuna.clock.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.gson.Gson;

import java.util.UUID;

@Entity(tableName = "alarms")
public final class Alarm {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "entryid")
    private final String mId;

    @NonNull
    @ColumnInfo(name = "type")
    private final AlarmType mAlarmType;

    @NonNull
    @ColumnInfo(name = "hour")
    private final Integer mHour;

    @NonNull
    @ColumnInfo(name = "minute")
    private final Integer mMinute;

    @Nullable
    @ColumnInfo(name = "description")
    private final String mDescription;

    @ColumnInfo(name = "monday_enabled")
    private final boolean mMondayEnabled;

    @ColumnInfo(name = "tuesday_enabled")
    private final boolean mTuesdayEnabled;

    @ColumnInfo(name = "wednesday_enabled")
    private final boolean mWednesdayEnabled;

    @ColumnInfo(name = "thursday_enabled")
    private final boolean mThursdayEnabled;

    @ColumnInfo(name = "friday_enabled")
    private final boolean mFridayEnabled;

    @ColumnInfo(name = "saturday_enabled")
    private final boolean mSaturdayEnabled;

    @ColumnInfo(name = "sunday_enabled")
    private final boolean mSundayEnabled;

    @NonNull
    @ColumnInfo(name = "status")
    private AlarmStatus mAlarmStatus = AlarmStatus.ACTIVE;

    /**
     * Use this constructor to create easily a new active Alarm without day recurrence.
     * All the other alarm values get the default one.
     *
     * @param alarmType   type of the alarm
     * @param description description of the alarm
     */
    @Ignore
    public Alarm(@NonNull AlarmType alarmType, @Nullable String description) {
        this(UUID.randomUUID().toString(), alarmType, 0, 0, description, false, false, false,
                false, false, false, false, AlarmStatus.ACTIVE);
    }

    /**
     * Use this constructor to create new Alarm
     *
     * @param alarmType        type of the alarm
     * @param hour             hour of the alarm
     * @param minute           minute of the alarm
     * @param description      description of the alarm
     * @param mondayEnabled    enable flag for monday
     * @param tuesdayEnabled   enable flag for tuesday
     * @param wednesdayEnabled enable flag for wednesday
     * @param thursdayEnabled  enable flag for thursday
     * @param fridayEnabled    enable flag for friday
     * @param saturdayEnabled  enable flag for saturday
     * @param sundayEnabled    enable flag for sunday
     * @param alarmStatus      status of the alarm
     */
    @Ignore
    public Alarm(@NonNull AlarmType alarmType, @NonNull Integer hour,
                 @NonNull Integer minute, @Nullable String description, boolean mondayEnabled,
                 boolean tuesdayEnabled, boolean wednesdayEnabled, boolean thursdayEnabled,
                 boolean fridayEnabled, boolean saturdayEnabled, boolean sundayEnabled,
                 @NonNull AlarmStatus alarmStatus) {
        this(UUID.randomUUID().toString(), alarmType, hour, minute, description, mondayEnabled,
                tuesdayEnabled, wednesdayEnabled, thursdayEnabled, fridayEnabled, saturdayEnabled,
                sundayEnabled, alarmStatus);
    }

    /**
     * @param id               id of the alarm
     * @param alarmType        type of the alarm
     * @param hour             hour of the alarm
     * @param minute           minute of the alarm
     * @param description      description of the alarm
     * @param mondayEnabled    enable flag for monday
     * @param tuesdayEnabled   enable flag for tuesday
     * @param wednesdayEnabled enable flag for wednesday
     * @param thursdayEnabled  enable flag for thursday
     * @param fridayEnabled    enable flag for friday
     * @param saturdayEnabled  enable flag for saturday
     * @param sundayEnabled    enable flag for sunday
     * @param alarmStatus      status of the alarm
     */
    public Alarm(@NonNull String id, @NonNull AlarmType alarmType, @NonNull Integer hour,
                 @NonNull Integer minute, @Nullable String description, boolean mondayEnabled,
                 boolean tuesdayEnabled, boolean wednesdayEnabled, boolean thursdayEnabled,
                 boolean fridayEnabled, boolean saturdayEnabled, boolean sundayEnabled,
                 @NonNull AlarmStatus alarmStatus) {
        mId = id;
        mAlarmType = alarmType;
        mHour = hour;
        mMinute = minute;
        mDescription = description;
        mMondayEnabled = mondayEnabled;
        mTuesdayEnabled = tuesdayEnabled;
        mWednesdayEnabled = wednesdayEnabled;
        mThursdayEnabled = thursdayEnabled;
        mFridayEnabled = fridayEnabled;
        mSaturdayEnabled = saturdayEnabled;
        mSundayEnabled = sundayEnabled;
        mAlarmStatus = alarmStatus;
    }

    @NonNull
    public String getId() {
        return mId;
    }

    @NonNull
    public AlarmType getAlarmType() {
        return mAlarmType;
    }

    @NonNull
    public Integer getHour() {
        return mHour;
    }

    @NonNull
    public Integer getMinute() {
        return mMinute;
    }

    @Nullable
    public String getDescription() {
        return mDescription;
    }

    public boolean isMondayEnabled() {
        return mMondayEnabled;
    }

    public boolean isTuesdayEnabled() {
        return mTuesdayEnabled;
    }

    public boolean isWednesdayEnabled() {
        return mWednesdayEnabled;
    }

    public boolean isThursdayEnabled() {
        return mThursdayEnabled;
    }

    public boolean isFridayEnabled() {
        return mFridayEnabled;
    }

    public boolean isSaturdayEnabled() {
        return mSaturdayEnabled;
    }

    public boolean isSundayEnabled() {
        return mSundayEnabled;
    }

    @NonNull
    public AlarmStatus getAlarmStatus() {
        return mAlarmStatus;
    }

    public void setAlarmStatus(@NonNull AlarmStatus alarmStatus) {
        this.mAlarmStatus = alarmStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Alarm alarm = (Alarm) o;
        return Objects.equal(mId, alarm.mId) &&
                Objects.equal(mAlarmType, alarm.mAlarmType) &&
                Objects.equal(mHour, alarm.mHour) &&
                Objects.equal(mMinute, alarm.mMinute) &&
                Objects.equal(mDescription, alarm.mDescription) &&
                Objects.equal(mMondayEnabled, alarm.mMondayEnabled) &&
                Objects.equal(mTuesdayEnabled, alarm.mTuesdayEnabled) &&
                Objects.equal(mWednesdayEnabled, alarm.mWednesdayEnabled) &&
                Objects.equal(mThursdayEnabled, alarm.mThursdayEnabled) &&
                Objects.equal(mFridayEnabled, alarm.mFridayEnabled) &&
                Objects.equal(mSaturdayEnabled, alarm.mSaturdayEnabled) &&
                Objects.equal(mSundayEnabled, alarm.mSundayEnabled) &&
                Objects.equal(mAlarmStatus, alarm.mAlarmStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(mId, mDescription);
    }


    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
