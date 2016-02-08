/**
 * Copyright 2009 Mike Partridge
 * Copyright 2011 Jay Goldman
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package com.mgjg.ProfileManager.schedule;

import android.content.ContentValues;
import android.content.Context;

import com.mgjg.ProfileManager.R;
import com.mgjg.ProfileManager.attribute.ProfileAttribute;
import com.mgjg.ProfileManager.provider.AttributeHelper;
import com.mgjg.ProfileManager.provider.ScheduleHelper;
import com.mgjg.ProfileManager.utils.Listable;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static com.mgjg.ProfileManager.provider.AttributeHelper.FILTER_ATTRIBUTE_PROFILE_ACTIVE;
import static com.mgjg.ProfileManager.provider.ScheduleHelper.COLUMN_SCHEDULE_ACTIVE;
import static com.mgjg.ProfileManager.provider.ScheduleHelper.COLUMN_SCHEDULE_DAY0;
import static com.mgjg.ProfileManager.provider.ScheduleHelper.COLUMN_SCHEDULE_DAY1;
import static com.mgjg.ProfileManager.provider.ScheduleHelper.COLUMN_SCHEDULE_DAY2;
import static com.mgjg.ProfileManager.provider.ScheduleHelper.COLUMN_SCHEDULE_DAY3;
import static com.mgjg.ProfileManager.provider.ScheduleHelper.COLUMN_SCHEDULE_DAY4;
import static com.mgjg.ProfileManager.provider.ScheduleHelper.COLUMN_SCHEDULE_DAY5;
import static com.mgjg.ProfileManager.provider.ScheduleHelper.COLUMN_SCHEDULE_DAY6;
import static com.mgjg.ProfileManager.provider.ScheduleHelper.COLUMN_SCHEDULE_PROFILE_ID;
import static com.mgjg.ProfileManager.provider.ScheduleHelper.COLUMN_SCHEDULE_START_HOUR;
import static com.mgjg.ProfileManager.provider.ScheduleHelper.COLUMN_SCHEDULE_START_MINUTE;
import static java.util.Calendar.FRIDAY;
import static java.util.Calendar.MONDAY;
import static java.util.Calendar.SATURDAY;
import static java.util.Calendar.SUNDAY;
import static java.util.Calendar.THURSDAY;
import static java.util.Calendar.TUESDAY;
import static java.util.Calendar.WEDNESDAY;

/**
 * In-memory representation of a schedule
 *
 * @author Mike Partridge/Jay Goldman
 */
public final class ScheduleEntry implements Listable
{

  public static final Integer[] DAYS_OF_WEEK = new Integer[]{SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY};

  private static CharSequence[] DAY_NAMES;

  public synchronized static void init(Context context)
  {
    if (null == DAY_NAMES)
    {
      DAY_NAMES = new CharSequence[]{
          context.getText(R.string.dayOn0),
          context.getText(R.string.dayOn1),
          context.getText(R.string.dayOn2),
          context.getText(R.string.dayOn3),
          context.getText(R.string.dayOn4),
          context.getText(R.string.dayOn5),
          context.getText(R.string.dayOn6)
      };
    }
  }

  /**
   * returns a ScheduleEntry with nothing enabled
   *
   * @return
   */
  public static ScheduleEntry defaultSchedule(long profileId)
  {
    return new ScheduleEntry(0, profileId, false, false, false, false, false, false, false, 0, 0, false);
  }

  /**
   * returns a ScheduleEntry enabled for all days at the specified time (HH:MM)
   *
   * @param timeStart
   * @return
   */
  public static ScheduleEntry defaultSchedule(long profileId, String timeStart)
  {
    return new ScheduleEntry(0, profileId, true, true, true, true, true, true, false,
        Integer.parseInt(timeStart.substring(0, timeStart.indexOf(":"))),
        Integer.parseInt(timeStart.substring(timeStart.indexOf(":") + 1)), true);
  }

  private long id; // can't be final because we need to set it after insert
  private final long profileId;

  // mutable attributes of schedule entry
  private boolean activeDays[];
  private int startHour;
  private int startMinute;
  private boolean active;

  /**
   * @param id
   * @param profileId
   * @param day0
   * @param day1
   * @param day2
   * @param day3
   * @param day4
   * @param day5
   * @param day6
   * @param endTime
   * @param startTime
   */
  public ScheduleEntry(long id, long profileId,
                       boolean day0, boolean day1, boolean day2, boolean day3, boolean day4, boolean day5,
                       boolean day6, int startHour, int startMinute,
                       boolean active)
  {
    this.id = id;
    this.profileId = profileId;
    activeDays = new boolean[]{day0, day1, day2, day3, day4, day5, day6};
    this.startHour = startHour;
    this.startMinute = startMinute;
    this.active = active;
  }

  @Override
  public final long getId()
  {
    return id;
  }

  @Override
  public final void setId(long id)
  {
    this.id = id;
  }

  public long getProfile_id()
  {
    return profileId;
  }

  /**
   * @return the startHour
   */
  public int getStartHour()
  {
    return startHour;
  }

  /**
   * @param startHour
   */
  public void setStartHour(int startHour)
  {
    this.startHour = startHour;
  }

  /**
   * @return the startMinute
   */
  public int getStartMinute()
  {
    return startMinute;
  }

  /**
   * @param startMinut
   */
  public void setStartMinute(int startMinute)
  {
    this.startMinute = startMinute;
  }

  /**
   * @param aActive
   */
  public void setActive(boolean active)
  {
    this.active = active;
  }

  /**
   * @return the mActive
   */
  public boolean isActive()
  {
    return active;
  }

  /**
   * Required for use in a ListAdapter; indicates that this is selectable and clickable
   *
   * @return the mEnabled
   */
  @Override
  public boolean isEnabled()
  {
    return true;
  }

  public boolean isActiveDay(int day)
  {
    return activeDays[day % 7];
  }

  public void setActiveDay(int day, boolean active)
  {
    activeDays[day] = active;
  }

  /**
   * @param day day number 0 - 6
   * @return
   */
  public CharSequence getActiveDayName(int day)
  {
    return activeDays[day] ? DAY_NAMES[day] : "___";
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object o)
  {
    boolean result = false;

    if (o instanceof ScheduleEntry)
    {
      ScheduleEntry s = (ScheduleEntry) o;

      result = (
          isActive() == s.isActive() &&
              activeDays[0] == s.activeDays[0] &&
              activeDays[1] == s.activeDays[1] &&
              activeDays[2] == s.activeDays[2] &&
              activeDays[3] == s.activeDays[3] &&
              activeDays[4] == s.activeDays[4] &&
              activeDays[5] == s.activeDays[5] &&
              activeDays[6] == s.activeDays[6] &&
              getStartHour() == s.getStartHour() &&
              getStartMinute() == s.getStartMinute()
      );
    }
    return result;
  }

  @Override
  public ContentValues makeValues()
  {
    ContentValues values = new ContentValues();

    values.put(COLUMN_SCHEDULE_DAY0, activeDays[0]);
    values.put(COLUMN_SCHEDULE_DAY1, activeDays[1]);
    values.put(COLUMN_SCHEDULE_DAY2, activeDays[2]);
    values.put(COLUMN_SCHEDULE_DAY3, activeDays[3]);
    values.put(COLUMN_SCHEDULE_DAY4, activeDays[4]);
    values.put(COLUMN_SCHEDULE_DAY5, activeDays[5]);
    values.put(COLUMN_SCHEDULE_DAY6, activeDays[6]);
    values.put(COLUMN_SCHEDULE_START_HOUR, getStartHour());
    values.put(COLUMN_SCHEDULE_START_MINUTE, getStartMinute());
    values.put(COLUMN_SCHEDULE_ACTIVE, isActive());
    values.put(COLUMN_SCHEDULE_PROFILE_ID, getProfile_id());
    return values;
  }

  private CharSequence activate(Context context)
  {
    AttributeHelper attrHelper = new AttributeHelper(context);
    List<ProfileAttribute> attrs = attrHelper.getList(FILTER_ATTRIBUTE_PROFILE_ACTIVE, getProfile_id());
    StringBuilder toast = new StringBuilder();
    for (ProfileAttribute attr : attrs)
    {
      attr.activate(context);
      if (toast.length() > 0)
      {
        toast.append(", ");
      }
      toast.append(attr.getToast(context));
    }
    return toast.toString();
  }

  public void activateManual(Context context)
  {
    activate(context);
  }

  private static int getDayIndex(Calendar cal)
  {

    int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

    int dayx = dayOfWeek - 1;
    if (DAYS_OF_WEEK[dayx] == dayOfWeek)
    {
      return dayx;
    }

    for (dayx = 0; dayx < DAYS_OF_WEEK.length; ++dayx)
    {
      if (DAYS_OF_WEEK[dayx] == dayOfWeek)
      {
        break;
      }
    }
    return dayx;
  }

  private boolean isEarlier(int earliestDayx, int earliestHour, int earliestMinute, int dayx)
  {
    if (dayx == earliestDayx)
    {
      final int hr = getStartHour();
      if (hr == earliestHour)
      {
        return (getStartMinute() < earliestMinute);
      }
      return (hr < earliestHour);
    }
    return (dayx < earliestDayx);
  }

  public static String nextActive(Context context, List<ScheduleEntry> schedules)
  {
    String next = null;

    Calendar cal = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
    int dayx = getDayIndex(cal);
    int hourOfDay = cal.get(Calendar.HOUR_OF_DAY);
    int minuteOfHour = cal.get(Calendar.MINUTE);
    final int tod = hourOfDay * 60 + minuteOfHour;

    int earliestDayx = dayx + 7;
    int earliestHour = 25;
    int earliestMinute = 60;

    boolean haveNext = false;
    boolean nextIsToday = false;

    for (ScheduleEntry sched : schedules)
    {
      if (sched.isActive())
      {
        haveNext = true;
        /*
         * if the schedule is setup for today, see if it will occur after now
         */
        if (sched.activeDays[dayx] && ((sched.getStartHour() * 60 + sched.getStartMinute()) > tod))
        {
          // schedule applies later today
          nextIsToday = true;
          if (sched.isEarlier(earliestDayx, earliestHour, earliestMinute, dayx))
          {
            earliestDayx = dayx;
            earliestHour = sched.getStartHour();
            earliestMinute = sched.getStartMinute();
          }
        }

        if (!nextIsToday)
        {
          // if we haven't found one for today
          // look for next day it is active
          for (int xx = dayx + 1; xx <= dayx + 7; ++xx)
          {
            if (sched.activeDays[(xx) % 7])
            {
              if (sched.isEarlier(earliestDayx, earliestHour, earliestMinute, xx))
              {
                earliestDayx = xx;
                earliestHour = sched.getStartHour();
                earliestMinute = sched.getStartMinute();
                break;
              }
            }
          }
        }
      } // end active check
    }
    if (haveNext)
    {
      if (nextIsToday)
      {
        next = ScheduleHelper.formatTime(context, earliestHour, earliestMinute);
      }
      else
      {
        next = DAY_NAMES[earliestDayx % 7] + " " + ScheduleHelper.formatTime(context, earliestHour, earliestMinute);
      }
    }
    return next;
  }

  public static String padZero(int num)
  {
    String str = Integer.toString(num);
    if (num < 10)
    {
      str = "0" + str;
    }
    return str;
  }

  public CharSequence activateConditional(Context context)
  {
    if (isActive())
    {
      Calendar cal = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
      // if the schedule is active for today, apply the settings
      if (activeDays[getDayIndex(cal)])
      {
        return activate(context);
      }
    }
    return "";
  }

  @Override
  public int compareTo(Listable another)
  {
    int thisOrder = getListOrder();
    int othOrder = another.getListOrder();
    // we know that thisOrder and othOrder are small integers so can just subtract to fulfill compareTo contract
    return thisOrder - othOrder;
  }

  @Override
  public int getListOrder()
  {
    return startHour * 60 + startMinute;
  }
}
