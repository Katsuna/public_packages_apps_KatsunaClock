package com.katsuna.clock.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.gson.Gson;

@Entity(tableName = "alarms")
public final class Alarm implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "alarmId")
    private long mAlarmId;

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

    @Nullable
    @ColumnInfo(name = "ringtone")
    private final String mRingtone;

    @ColumnInfo(name = "vibrate")
    private final boolean mVibrate;

    /**
     * Use this constructor to create easily a new active Alarm without day recurrence.
     * All the other alarm values get the default one.
     *
     * @param alarmType   type of the alarm
     * @param description description of the alarm
     */
    @Ignore
    public Alarm(@NonNull AlarmType alarmType, @Nullable String description) {
        this(0, alarmType, 0, 0, description, false, false, false,
                false, false, false, false, AlarmStatus.ACTIVE, null, false);
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
     * @param ringtone         ringtone of the alarm
     * @param vibrate          vibrate flag of the alarm
     */
    @Ignore
    public Alarm(@NonNull AlarmType alarmType, @NonNull Integer hour,
                 @NonNull Integer minute, @Nullable String description, boolean mondayEnabled,
                 boolean tuesdayEnabled, boolean wednesdayEnabled, boolean thursdayEnabled,
                 boolean fridayEnabled, boolean saturdayEnabled, boolean sundayEnabled,
                 @NonNull AlarmStatus alarmStatus, @Nullable String ringtone, boolean vibrate) {
        this(0, alarmType, hour, minute, description, mondayEnabled,
                tuesdayEnabled, wednesdayEnabled, thursdayEnabled, fridayEnabled, saturdayEnabled,
                sundayEnabled, alarmStatus, ringtone, vibrate);
    }

    /**
     * @param alarmId          id of the alarm
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
     * @param ringtone         ringtone of the alarm
     * @param vibrate          vibrate flag of the alarm
     */
    public Alarm(long alarmId, @NonNull AlarmType alarmType, @NonNull Integer hour,
                 @NonNull Integer minute, @Nullable String description, boolean mondayEnabled,
                 boolean tuesdayEnabled, boolean wednesdayEnabled, boolean thursdayEnabled,
                 boolean fridayEnabled, boolean saturdayEnabled, boolean sundayEnabled,
                 @NonNull AlarmStatus alarmStatus, @Nullable String ringtone, boolean vibrate) {
        mAlarmId = alarmId;
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
        mRingtone = ringtone;
        mVibrate = vibrate;
    }

    public long getAlarmId() {
        return mAlarmId;
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

    public boolean isRecurring() {
        return mMondayEnabled || mTuesdayEnabled || mWednesdayEnabled || mThursdayEnabled
                || mFridayEnabled || mSaturdayEnabled || mSundayEnabled;
    }

    @NonNull
    public AlarmStatus getAlarmStatus() {
        return mAlarmStatus;
    }

    @Nullable
    public String getRingtone() {
        return mRingtone;
    }

    public boolean isVibrate() {
        return mVibrate;
    }

    public void setAlarmStatus(@NonNull AlarmStatus alarmStatus) {
        this.mAlarmStatus = alarmStatus;
    }

    public void setAlarmId(long mAlarmId) {
        this.mAlarmId = mAlarmId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Alarm alarm = (Alarm) o;
        return Objects.equal(mAlarmId, alarm.mAlarmId) &&
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
                Objects.equal(mAlarmStatus, alarm.mAlarmStatus) &&
                Objects.equal(mRingtone, alarm.mRingtone) &&
                Objects.equal(mVibrate, alarm.mVibrate);
    }

    @Override
    public int hashCode() {
        return Long.valueOf(mAlarmId).hashCode();
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mAlarmId);
        dest.writeInt(this.mAlarmType.ordinal());
        dest.writeValue(this.mHour);
        dest.writeValue(this.mMinute);
        dest.writeString(this.mDescription);
        dest.writeByte(this.mMondayEnabled ? (byte) 1 : (byte) 0);
        dest.writeByte(this.mTuesdayEnabled ? (byte) 1 : (byte) 0);
        dest.writeByte(this.mWednesdayEnabled ? (byte) 1 : (byte) 0);
        dest.writeByte(this.mThursdayEnabled ? (byte) 1 : (byte) 0);
        dest.writeByte(this.mFridayEnabled ? (byte) 1 : (byte) 0);
        dest.writeByte(this.mSaturdayEnabled ? (byte) 1 : (byte) 0);
        dest.writeByte(this.mSundayEnabled ? (byte) 1 : (byte) 0);
        dest.writeInt(this.mAlarmStatus.ordinal());
        dest.writeString(this.mRingtone);
        dest.writeByte(this.mVibrate ? (byte) 1 : (byte) 0);
    }

    protected Alarm(Parcel in) {
        this.mAlarmId = in.readLong();
        int tmpMAlarmType = in.readInt();
        this.mAlarmType = AlarmType.values()[tmpMAlarmType];
        this.mHour = (Integer) in.readValue(Integer.class.getClassLoader());
        this.mMinute = (Integer) in.readValue(Integer.class.getClassLoader());
        this.mDescription = in.readString();
        this.mMondayEnabled = in.readByte() != 0;
        this.mTuesdayEnabled = in.readByte() != 0;
        this.mWednesdayEnabled = in.readByte() != 0;
        this.mThursdayEnabled = in.readByte() != 0;
        this.mFridayEnabled = in.readByte() != 0;
        this.mSaturdayEnabled = in.readByte() != 0;
        this.mSundayEnabled = in.readByte() != 0;
        int tmpMAlarmStatus = in.readInt();
        this.mAlarmStatus = AlarmStatus.values()[tmpMAlarmStatus];
        this.mRingtone = in.readString();
        this.mVibrate = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Alarm> CREATOR = new Parcelable.Creator<Alarm>() {
        @Override
        public Alarm createFromParcel(Parcel source) {
            return new Alarm(source);
        }

        @Override
        public Alarm[] newArray(int size) {
            return new Alarm[size];
        }
    };
}
