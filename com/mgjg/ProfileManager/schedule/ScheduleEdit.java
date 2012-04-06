/**
 * Copyright 2009 Mike Partridge/ Jay Goldman
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
package com.mgjg.ProfileManager.schedule;

import static com.mgjg.ProfileManager.provider.ScheduleHelper.FILTER_SCHEDULE_ID;
import static com.mgjg.ProfileManager.provider.ScheduleHelper.INTENT_SCHEDULE_ID;
import static com.mgjg.ProfileManager.provider.ScheduleHelper.INTENT_SCHEDULE_PROFILE_ID;
import static com.mgjg.ProfileManager.provider.ScheduleHelper.INTENT_SCHEDULE_PROFILE_NAME;

import java.text.MessageFormat;
import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.mgjg.ProfileManager.R;
import com.mgjg.ProfileManager.provider.ScheduleHelper;
import com.mgjg.ProfileManager.utils.Util;

/**
 * Schedule Edit screen
 * 
 * @author Mike Partridge
 */
public class ScheduleEdit extends Activity
{

  private static final int[] DAY_IDS = new int[] {
      R.id.day0toggle,
      R.id.day1toggle,
      R.id.day2toggle,
      R.id.day3toggle,
      R.id.day4toggle,
      R.id.day5toggle,
      R.id.day6toggle,
  };
  private Long scheduleId;
  private Long profileId = 0L;
  private String profileName;

  private ScheduleEntry schedule; // help watch for changes

  private boolean saved;
  private boolean canceled;

  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onCreate(android.os.Bundle)
   */
  @Override
  protected void onCreate(Bundle instanceState)
  {
    super.onCreate(instanceState);

    setContentView(R.layout.schedule_edit);

    init(instanceState);

    /*
     * get handles to the gui, setup some basics
     */

    TextView typeLabel = (TextView) findViewById(R.id.ScheduleForProfile);
    String lbl = MessageFormat.format(getLabelFmt(this), "Schedule", profileName);
    typeLabel.setText(lbl);

    TimePicker startTime = (TimePicker) findViewById(R.id.startTime);

    boolean mClock24hour = Util.is24HourClock(this.getContentResolver());
    startTime.setIs24HourView(mClock24hour);

    Button done = (Button) findViewById(R.id.doneButton);
    if (null != done)
    {
      done.setOnClickListener(new OnClickListener() {

        @Override
        public void onClick(View v)
        {
          finish();
        }
      });
    }

    Button cancel = (Button) findViewById(R.id.cancelButton);
    if (null != cancel)
    {
      cancel.setOnClickListener(new OnClickListener() {

        @Override
        public void onClick(View v)
        {
          canceled = true;
          finish();
        }
      });
    }

    populateFields();

  }

  private static String labelFmt;

  private final synchronized String getLabelFmt(Activity aa)
  {
    if (null == labelFmt)
    {
      labelFmt = aa.getText(R.string.attr_for_profile).toString();
    }
    return labelFmt;
  }

  /**
   * Populate GUI with data from the db if the schedule exists, or with defaults if not
   */
  private void populateFields()
  {

    if (scheduleId != null)
    {
      ScheduleHelper helper = new ScheduleHelper(this);
      List<ScheduleEntry> schedules = helper.getList(FILTER_SCHEDULE_ID, scheduleId);
      if (!schedules.isEmpty())
      {
        schedule = schedules.get(0);
        profileId = schedule.getProfile_id();
      }
      else
      {
        // no such schedule entry, i guess we will make one, better than dying with an NPE :-)
        schedule = new ScheduleEntry(scheduleId, profileId, false, true, true, true, true, true, false, 8, 0, true);
      }
    }
    else
    {
      /*
       * new schedule - populate defaults
       */
      schedule = new ScheduleEntry(0, profileId, false, true, true, true, true, true, false, 8, 0, true);
    }

    for (int day = 0; day < 7; ++day)
    {
      ToggleButton tb = (ToggleButton) findViewById(DAY_IDS[day]);
      tb.setChecked(schedule.isActiveDay(day));
    }

    TimePicker startTime = (TimePicker) findViewById(R.id.startTime);
    startTime.setCurrentHour(schedule.getStartHour());
    startTime.setCurrentMinute(schedule.getStartMinute());

    CheckBox active = (CheckBox) findViewById(R.id.activeCheckbox);
    active.setChecked(schedule.isActive());
  }

  /*
   * compare the mSchedule (as populated from the db) to the current state of each gui field
   */
  private boolean isModified()
  {
    boolean result = false;

    if (null == schedule)
    {
      return false; // shouldn't happen ... but prevent NPEs
    }
    if (null == scheduleId)
    {
      return true;
    }
    for (int day = 0; day < 7; ++day)
    {
      ToggleButton tb = (ToggleButton) findViewById(DAY_IDS[day]);
      if ((null != tb) && tb.isChecked() != schedule.isActiveDay(day))
      {
        return true;
      }
    }

    CheckBox active = (CheckBox) findViewById(R.id.activeCheckbox);
    if ((null != active) && (active.isChecked() != schedule.isActive()))
    {
      return true;
    }

    TimePicker startTime = (TimePicker) findViewById(R.id.startTime);
    if ((null != startTime) && (startTime.getCurrentHour() != schedule.getStartHour() ||
        startTime.getCurrentMinute() != schedule.getStartMinute()))
    {
      result = true;
    }

    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onPause()
   */
  @Override
  protected void onPause()
  {
    super.onPause();

    /*
     * save only if the gui differs from the db
     */
    if (!canceled && (null != schedule) && isModified() && !saved)
    {
      saveSchedule();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onResume()
   */
  @Override
  protected void onResume()
  {
    super.onResume();
    // canceled = false;
    //
    // TimePicker startTime = (TimePicker) findViewById(R.id.startTime);
    // CheckBox active = (CheckBox) findViewById(R.id.activeCheckbox);
    // schedule = populateFields(startTime, active);
    // profileId = schedule.getProfile_id();
    // saved = false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
   */
  @Override
  protected void onSaveInstanceState(Bundle instanceState)
  {
    super.onSaveInstanceState(instanceState);

    /*
     * save only if the gui differs from the db
     */
    if ((null != schedule) && isModified() && !saved)
    {
      saveSchedule();
    }

    // store some things for re-display on resume
    if (scheduleId != null)
    {
      instanceState.putLong(INTENT_SCHEDULE_ID, scheduleId);
    }

    instanceState.putLong(INTENT_SCHEDULE_PROFILE_ID, profileId);
    instanceState.putCharSequence(INTENT_SCHEDULE_PROFILE_NAME, profileName);
  }

  @Override
  protected void onRestoreInstanceState(Bundle instanceState)
  {
    init(instanceState);
    populateFields();
  }

  /**
   * check the saved state for schedule id, then check the bundle passed through the Intent
   */
  private void init(Bundle instanceState)
  {
    /*
     * check the saved state for schedule id, then check the bundle passed through the Intent
     */
    scheduleId = (instanceState != null) ? instanceState.getLong(INTENT_SCHEDULE_ID) : null;
    CharSequence profileCS;
    if (scheduleId == null)
    {
      Intent ii = getIntent();
      if (null != ii)
      {
        scheduleId = ii.getLongExtra(INTENT_SCHEDULE_ID, 0);
        if ((null != scheduleId) && (scheduleId < 1))
        {
          scheduleId = null;
        }
        profileId = ii.getLongExtra(INTENT_SCHEDULE_PROFILE_ID, 0);
        profileCS = ii.getCharSequenceExtra(INTENT_SCHEDULE_PROFILE_NAME);
      }
      else
      {
        profileId = 0L;  // this is not good
        profileCS = "UNKNOWN";
      }
    }
    else
    {
      profileId = instanceState.getLong(INTENT_SCHEDULE_PROFILE_ID);
      profileCS = instanceState.getCharSequence(INTENT_SCHEDULE_PROFILE_NAME);
    }
    profileName = (null == profileCS) ? null : profileCS.toString();

    canceled = false;
    saved = false;
  }

  private void saveSchedule()
  {
    for (int day = 0; day < 7; ++day)
    {
      ToggleButton dayx = (ToggleButton) findViewById(DAY_IDS[day]);
      if (null != dayx)
      {
        schedule.setActiveDay(day, (null != dayx) ? dayx.isChecked() : false);
      }
    }
    TimePicker startTime = (TimePicker) findViewById(R.id.startTime);
    if (null != startTime)
    {
      schedule.setStartHour(startTime.getCurrentHour());
      schedule.setStartMinute(startTime.getCurrentMinute());
    }

    CheckBox active = (CheckBox) findViewById(R.id.activeCheckbox);
    if (null != active)
    {
      schedule.setActive(active.isChecked());
    }

    ContentValues values = schedule.makeValues();

    ScheduleHelper helper = new ScheduleHelper(this);
    if (scheduleId == null)
    {
      scheduleId = Long.parseLong(helper.insert(values).getPathSegments().get(1));
    }
    else
    {
      helper.update(FILTER_SCHEDULE_ID, scheduleId, values);
    }
    helper.setAlarm(scheduleId, schedule.isActive());

    saved = true;

    Toast.makeText(this, R.string.savedSchedule, Toast.LENGTH_SHORT).show();
  }

  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#finish()
   */
  @Override
  public void finish()
  {

    if (!canceled && (null != schedule) && isModified() && !saved)
    {
      saveSchedule();
    }
    setResult(!canceled ? RESULT_OK : RESULT_CANCELED);
    super.finish();
  }

  @Override
  public void onBackPressed()
  {
    setResult(!canceled ? RESULT_OK : RESULT_CANCELED);
    super.finish();
  }
}
