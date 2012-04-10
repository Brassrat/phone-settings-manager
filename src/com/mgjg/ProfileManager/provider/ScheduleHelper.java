/**
 * Copyright 2009 Mike Partridge
 * Copyright 2011 Jay Goldman
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed 
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language 
 * governing permissions and limitations under the License. 
 */
package com.mgjg.ProfileManager.provider;

import static com.mgjg.ProfileManager.provider.ScheduleProvider.CONTENT_URI;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.widget.Toast;

import com.mgjg.ProfileManager.R;
import com.mgjg.ProfileManager.receivers.ScheduleReceiver;
import com.mgjg.ProfileManager.schedule.ScheduleEntry;
import com.mgjg.ProfileManager.schedule.ScheduleListAdapter;
import com.mgjg.ProfileManager.utils.ListAdapter;
import com.mgjg.ProfileManager.utils.Util;

/**
 * Abstracts access to profile data in SQLite db
 * 
 * @author Mike Partridge / Jay Goldman
 */
public final class ScheduleHelper extends ProfileManagerProviderHelper<ScheduleEntry>
{

  public static final String TABLE_SCHEDULE = "schedules";
  public static final String TABLE_ALIAS_SCHEDULE = "s";
  
  public static final String COLUMN_SCHEDULE_ID = "_id";
  public static final String COLUMN_SCHEDULE_PROFILE_ID = "_profile_id";
  public static final String COLUMN_SCHEDULE_ACTIVE = "_active";
  public static final String COLUMN_SCHEDULE_START_HOUR = "_start_hour";
  public static final String COLUMN_SCHEDULE_START_MINUTE = "_start_min";

  public static final String COLUMN_SCHEDULE_DAY0 = "_day0";
  public static final String COLUMN_SCHEDULE_DAY1 = "_day1";
  public static final String COLUMN_SCHEDULE_DAY2 = "_day2";
  public static final String COLUMN_SCHEDULE_DAY3 = "_day3";
  public static final String COLUMN_SCHEDULE_DAY4 = "_day4";
  public static final String COLUMN_SCHEDULE_DAY5 = "_day5";
  public static final String COLUMN_SCHEDULE_DAY6 = "_day6";

  // names of values passed via Intents
  public static final String INTENT_SCHEDULE_ID = "com.mgjg.ProfileManager.schedule.id";
  public static final String INTENT_SCHEDULE_ACTIVE = "com.mgjg.ProfileManager.schedule.active";
  public static final String INTENT_SCHEDULE_PROFILE_ID = "com.mgjg.ProfileManager.schedule.profile.id";
  public static final String INTENT_SCHEDULE_PROFILE_NAME = "com.mgjg.ProfileManager.schedule.profile.name";

  public static final String DEFAULT_ORDER_SCHEDULE =
            COLUMN_SCHEDULE_DAY0 + " desc, "
          + COLUMN_SCHEDULE_DAY1 + " desc, "
          + COLUMN_SCHEDULE_DAY2 + " desc, "
          + COLUMN_SCHEDULE_DAY3 + " desc, "
          + COLUMN_SCHEDULE_DAY4 + " desc, "
          + COLUMN_SCHEDULE_DAY5 + " desc, "
          + COLUMN_SCHEDULE_DAY6 + " desc, "
          + COLUMN_SCHEDULE_START_HOUR + ","
          + COLUMN_SCHEDULE_START_MINUTE + ","
          + TABLE_SCHEDULE + "." + COLUMN_SCHEDULE_ID;

  public static int ACTIVE = R.string.active;
  public static int INACTIVE = R.string.inactive;
  
  public static int ACTIVE_COLOR = Color.GREEN;
  public static int INACTIVE_COLOR = Color.RED;

  public static final int FILTER_SCHEDULE_ID = 1;
  public static final int FILTER_SCHEDULE_PROFILE_ID = 2;
  public static final int FILTER_SCHEDULE_PROFILE_ID_START_TIME = 3;

  public static void init(Context context)
  {
    ScheduleEntry.init(context);
    try
    {
      ACTIVE_COLOR = Color.parseColor(context.getString(R.string.active_color));
    }
    catch (Exception e)
    {
      // just ignore bad values
    }
    try
    {
      INACTIVE_COLOR = Color.parseColor(context.getString(R.string.inactive_color));
    }
    catch (Exception e)
    {
      // just ignore bad values
    }
  }

  public ScheduleHelper(Context context)
  {
    super(context);
  }

  @Override
  public Uri getContentUri()
  {
    return CONTENT_URI;
  }

  @Override
  public Uri getContentUri(int filter, Object... values)
  {
    Uri uri = getContentUri();
    switch (filter)
    {
    case NO_FILTER:
      return uri;

    case FILTER_SCHEDULE_ID:
      return Uri.withAppendedPath(uri, String.valueOf(values[0]));

    case FILTER_SCHEDULE_PROFILE_ID:
      return Uri.withAppendedPath(Uri.withAppendedPath(uri, "profile"), String.valueOf(values[0]));

    case FILTER_ALL_ACTIVE:
      return Uri.withAppendedPath(uri, "active");

    case FILTER_SCHEDULE_PROFILE_ID_START_TIME:
      uri = Uri.withAppendedPath(Uri.withAppendedPath(uri, "profile"), String.valueOf(values[0]));
      uri = Uri.withAppendedPath(Uri.withAppendedPath(uri, "hour"), String.valueOf(values[1]));
      uri = Uri.withAppendedPath(Uri.withAppendedPath(uri, "minute"), String.valueOf(values[2]));

    default:
      throw new IllegalArgumentException("Unknown filter " + filter);
    }
  }

  @Override
  public final ListAdapter<ScheduleEntry> createListAdapter(int filter, Object... value)
  {
    init(context);
    return fillListAdapter(new ScheduleListAdapter(context), query(filter, value));
  }

  @Override
  public ScheduleEntry newInstance(Cursor c)
  {
    long id = c.getLong(c.getColumnIndexOrThrow(COLUMN_SCHEDULE_ID));
    boolean day0 = (c.getInt(c.getColumnIndexOrThrow(COLUMN_SCHEDULE_DAY0)) > 0);
    boolean day1 = (c.getInt(c.getColumnIndexOrThrow(COLUMN_SCHEDULE_DAY1)) > 0);
    boolean day2 = (c.getInt(c.getColumnIndexOrThrow(COLUMN_SCHEDULE_DAY2)) > 0);
    boolean day3 = (c.getInt(c.getColumnIndexOrThrow(COLUMN_SCHEDULE_DAY3)) > 0);
    boolean day4 = (c.getInt(c.getColumnIndexOrThrow(COLUMN_SCHEDULE_DAY4)) > 0);
    boolean day5 = (c.getInt(c.getColumnIndexOrThrow(COLUMN_SCHEDULE_DAY5)) > 0);
    boolean day6 = (c.getInt(c.getColumnIndexOrThrow(COLUMN_SCHEDULE_DAY6)) > 0);
    int startHour = c.getInt(c.getColumnIndexOrThrow(COLUMN_SCHEDULE_START_HOUR));
    int startMinute = c.getInt(c.getColumnIndexOrThrow(COLUMN_SCHEDULE_START_MINUTE));
    boolean active = (c.getInt(c.getColumnIndexOrThrow(COLUMN_SCHEDULE_ACTIVE)) > 0);
    long profile_id = c.getLong(c.getColumnIndexOrThrow(COLUMN_SCHEDULE_PROFILE_ID));
    return new ScheduleEntry(id, profile_id, day0, day1, day2, day3, day4, day5, day6, startHour, startMinute, active);
  }

  public final int deleteSchedule(long scheduleId)
  {
    setAlarm(false, false, scheduleId); // first cancel any alarms
    return delete(FILTER_SCHEDULE_ID, scheduleId);
  }

  public final int deleteProfile(Long profileId)
  {
    if (null == profileId)
    {
      return 0;
    }
    List<ScheduleEntry> schedules = getList(FILTER_SCHEDULE_PROFILE_ID, profileId);

    for (ScheduleEntry schedule : schedules)
    {
      // clear all alarms for this profile
      setAlarm(false, false, schedule.getId());
    }
    return schedules.size();
  }

  /**
   * register alarms for all schedules
   * 
   */
  public int registerAlarm()
  {
    int numAlarms = setAlarm(true, true, null);
    if (numAlarms > 0)
    {
      Toast.makeText(context, "Profile Manager registered " + numAlarms +
          ((numAlarms == 1) ? " alarm " : " alarms"), Toast.LENGTH_LONG).show();
    }
    else
    {
      Toast.makeText(context, "Profile Manager registered no alarms", Toast.LENGTH_SHORT).show();
    }
    return numAlarms;
  }

  public int setAlarm(long scheduleId, boolean register)
  {
    return setAlarm(register, false, scheduleId);
  }

  private int setAlarm(boolean register, boolean allowMissedTime, Long scheduleId)
  {
    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    final Calendar cal = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());

    List<ScheduleEntry> schedules;
    if (null == scheduleId)
    {
      schedules = getList(ScheduleProvider.FILTER_ALL_ACTIVE, (Object) null);
    }
    else
    {
      schedules = getList(FILTER_SCHEDULE_ID, scheduleId);
    }
    int numAlarms = 0;
    for (ScheduleEntry schedule : schedules)
    {
      PendingIntent pi = makePendingScheduleIntent(schedule.getId());
      alarmManager.cancel(pi); // first, cancel any existing alarms for this schedule

      if (register)
      {
        cal.set(Calendar.HOUR_OF_DAY, schedule.getStartHour());
        cal.set(Calendar.MINUTE, schedule.getStartMinute());
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 200);

        // repeat the alarm every day; the receiver will check day of week
        // allowed missed time to trigger alarm immediately if caller requests, otherwise, make sure
        // initial trigger time is in the future
        long nextTime = cal.getTimeInMillis();
        if (!allowMissedTime && (nextTime < System.currentTimeMillis()))
        {
          cal.add(Calendar.DATE, 1);
          nextTime = cal.getTimeInMillis();
        }

        // if (null == scheduleId)
        // {
        // Toast.makeText(context, "REGISTER " + schedule.getId() + " at " + schedule.getStartHour() + ":" + schedule.getStartMinute(), Toast.LENGTH_LONG).show();
        // }
        ++numAlarms;
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, nextTime, AlarmManager.INTERVAL_DAY, pi);
      }
    }
    return numAlarms;
  }

  private PendingIntent makePendingScheduleIntent(long scheduleId)
  {
    Intent scheduleIntent = new Intent(context, ScheduleReceiver.class);
    scheduleIntent.setData(getContentUri(FILTER_SCHEDULE_ID, scheduleId));
    return PendingIntent.getBroadcast(context, 0, scheduleIntent, PendingIntent.FLAG_CANCEL_CURRENT);
  }

  public static String formatTime(Context context, int hour, int minute)
  {

    if (Util.is24HourClock(context.getContentResolver()))
    {
      return (hour < 10 ? "0" : "") + hour + ":" +
          (minute < 10 ? "0" : "") + minute;
    }
    else
    {
      final String hourDsc;

      if (hour < 1 || hour > 23)
      {
        hourDsc = "12";
      }
      else if (hour > 12)
      {
        hourDsc = String.valueOf(hour - 12);
      }
      else
      {
        hourDsc = String.valueOf(hour);
      }
      return hourDsc + ":" +
          (minute < 10 ? "0" : "") + minute +
          (hour >= 12 && hour < 24 ? "PM" : "AM");
    }

  }
}
