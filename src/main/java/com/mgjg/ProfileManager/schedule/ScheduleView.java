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

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.mgjg.ProfileManager.R;
import com.mgjg.ProfileManager.provider.ScheduleHelper;

/**
 * Defines layout for Schedule list items
 *
 * @author Mike Partridge/Jay Goldman
 */
public final class ScheduleView extends LinearLayout
{

  private static final int DAY_ID = 1000; // actually DAY_ID + day is used
  private static final int ACTIVE_ID = 1010;
  private static final int TIME_ID = 1011;

  /**
   * used by ide tools, should never be used in runtime code
   * @param context
   */
  public ScheduleView (Context context)
  {
    super(context);
    if (!isInEditMode())
    {
      throw new UnsupportedOperationException("Can not construct ScheduleView without ScheudleEntry");
    }
  }

    /**
     * @param context
     * @param schedule
     */
  public ScheduleView(Context context, ScheduleEntry schedule)
  {
    super(context);

    ScheduleHelper.init(context);

    this.setOrientation(VERTICAL);

    // convenience for addView calls later
    LinearLayout.LayoutParams paramsWrapBoth = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

    TextView active = new TextView(context);
    active.setId(ACTIVE_ID);
    active.setPadding(2, 2, 2, 2);
    active.setText(schedule.isActive() ? "ACTIVE" : "INACTIVE");
    active.setTextColor(schedule.isActive() ? Color.GREEN : Color.RED);

    LinearLayout daysLayout = new LinearLayout(context);
    daysLayout.setOrientation(HORIZONTAL);
    daysLayout.setGravity(Gravity.END);

    daysLayout.addView(makeDayView(context, schedule, 0), paramsWrapBoth);
    daysLayout.addView(makeDayView(context, schedule, 1), paramsWrapBoth);
    daysLayout.addView(makeDayView(context, schedule, 2), paramsWrapBoth);
    daysLayout.addView(makeDayView(context, schedule, 3), paramsWrapBoth);
    daysLayout.addView(makeDayView(context, schedule, 4), paramsWrapBoth);
    daysLayout.addView(makeDayView(context, schedule, 5), paramsWrapBoth);
    daysLayout.addView(makeDayView(context, schedule, 6), paramsWrapBoth);

    /*
     * time
     */

    TextView startTimeLabel = new TextView(context);
    startTimeLabel.setPadding(2, 2, 10, 2);
    startTimeLabel.setText(R.string.startTimeLabel);

    TextView startTime = new TextView(context);
    startTime.setId(TIME_ID);
    startTime.setTextSize(18);
    startTime.setPadding(2, 2, 2, 2);
    startTime.setText(formatTime(schedule.getStartHour(), schedule.getStartMinute()));

    TableRow activeDaysRow = new TableRow(context);
    activeDaysRow.addView(active);
    activeDaysRow.addView(daysLayout);

    TableRow timeRow = new TableRow(context);
    timeRow.addView(startTimeLabel);
    timeRow.addView(startTime);

    TableLayout tableLayout = new TableLayout(context);
    tableLayout.setColumnStretchable(1, true);
    tableLayout.addView(activeDaysRow);
    tableLayout.addView(timeRow);
    LinearLayout.LayoutParams paramsFillWrap = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    addView(tableLayout, paramsFillWrap);
  }

  private TextView makeDayView(Context context, ScheduleEntry schedule, int day)
  {
    TextView tv = new TextView(context);
    tv.setPadding(5, 5, 5, 5);
    tv.setText(schedule.getActiveDayName(day));
    tv.setId(DAY_ID + day);
    return tv;
  }

  private String formatTime(int hour, int minute)
  {
    return ScheduleHelper.formatTime(getContext(), hour, minute);
  }

  private void setFromSchedule(int day, CharSequence txt)
  {
    TextView tv = (TextView) this.findViewById(DAY_ID + day);
    if (null != tv)
    {
      tv.setText(txt);
    }
  }

  /**
   * @param schedule
   */
  public void setFromSchedule(ScheduleEntry schedule)
  {
    for (int day = 0; day < 7; ++day)
    {
      setFromSchedule(day, schedule.getActiveDayName(day));
    }

    setStartTime(formatTime(schedule.getStartHour(), schedule.getStartMinute()));
    setActive(schedule.isActive());

  }

  /**
   * @param startTime
   */
  public void setStartTime(String startTime)
  {
    // TextView tv = (TextView) this.findViewWithTag("STARTTIME");
    TextView tv = (TextView) this.findViewById(TIME_ID);
    if (null != tv)
    {
      tv.setText(startTime);
    }

  }

  /**
   * @param active
   */
  public void setActive(boolean active)
  {
    TextView tv = (TextView) this.findViewById(ACTIVE_ID);
    if (null != tv)
    {
      tv.setText(active ? ScheduleHelper.ACTIVE : ScheduleHelper.INACTIVE);
      tv.setTextColor(active ? ScheduleHelper.ACTIVE_COLOR : ScheduleHelper.INACTIVE_COLOR);
    }
  }

}
