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
    private final Integer mType;

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


    /**
     * Use this constructor to create a new active Alarm without day recurrence.
     *
     * @param type             type of the alarm
     * @param description      description of the alarm
     */
    @Ignore
    public Alarm(@NonNull Integer type, @Nullable String description) {
        this(UUID.randomUUID().toString(), type, description, false, false, false, false, false,
                false, false);
    }

    /**
     * Use this constructor to create a new active Alarm.
     *
     * @param type             type of the alarm
     * @param description      description of the alarm
     * @param mondayEnabled    enable flag for monday
     * @param tuesdayEnabled   enable flag for tuesday
     * @param wednesdayEnabled enable flag for wednesday
     * @param thursdayEnabled  enable flag for thursday
     * @param fridayEnabled    enable flag for friday
     * @param saturdayEnabled  enable flag for saturday
     * @param sundayEnabled    enable flag for sunday
     */
    @Ignore
    public Alarm(@NonNull Integer type, @Nullable String description, boolean mondayEnabled,
                 boolean tuesdayEnabled, boolean wednesdayEnabled, boolean thursdayEnabled,
                 boolean fridayEnabled, boolean saturdayEnabled, boolean sundayEnabled) {
        this(UUID.randomUUID().toString(), type, description, mondayEnabled, tuesdayEnabled,
                wednesdayEnabled, thursdayEnabled, fridayEnabled, saturdayEnabled, sundayEnabled);
    }


    /**
     * @param id               id of the alarm
     * @param type             type of the alarm
     * @param description      description of the alarm
     * @param mondayEnabled    enable flag for monday
     * @param tuesdayEnabled   enable flag for tuesday
     * @param wednesdayEnabled enable flag for wednesday
     * @param thursdayEnabled  enable flag for thursday
     * @param fridayEnabled    enable flag for friday
     * @param saturdayEnabled  enable flag for saturday
     * @param sundayEnabled    enable flag for sunday
     */
    public Alarm(@NonNull String id, @NonNull Integer type, @Nullable String description,
                 boolean mondayEnabled, boolean tuesdayEnabled, boolean wednesdayEnabled,
                 boolean thursdayEnabled, boolean fridayEnabled, boolean saturdayEnabled,
                 boolean sundayEnabled) {
        mId = id;
        mType = type;
        mDescription = description;
        mMondayEnabled = mondayEnabled;
        mTuesdayEnabled = tuesdayEnabled;
        mWednesdayEnabled = wednesdayEnabled;
        mThursdayEnabled = thursdayEnabled;
        mFridayEnabled = fridayEnabled;
        mSaturdayEnabled = saturdayEnabled;
        mSundayEnabled = sundayEnabled;
    }

    @NonNull
    public String getId() {
        return mId;
    }

    @NonNull
    public Integer getType() {
        return mType;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Alarm alarm = (Alarm) o;
        return Objects.equal(mId, alarm.mId) &&
                Objects.equal(mType, alarm.mType) &&
                Objects.equal(mDescription, alarm.mDescription) &&
                Objects.equal(mMondayEnabled, alarm.mMondayEnabled) &&
                Objects.equal(mTuesdayEnabled, alarm.mTuesdayEnabled) &&
                Objects.equal(mWednesdayEnabled, alarm.mWednesdayEnabled) &&
                Objects.equal(mThursdayEnabled, alarm.mThursdayEnabled) &&
                Objects.equal(mFridayEnabled, alarm.mFridayEnabled) &&
                Objects.equal(mSaturdayEnabled, alarm.mSaturdayEnabled) &&
                Objects.equal(mSundayEnabled, alarm.mSundayEnabled);
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
