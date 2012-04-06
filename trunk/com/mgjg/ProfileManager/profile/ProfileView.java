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
package com.mgjg.ProfileManager.profile;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.mgjg.ProfileManager.R;
import com.mgjg.ProfileManager.provider.ScheduleHelper;
import com.mgjg.ProfileManager.schedule.ScheduleEntry;
import com.mgjg.ProfileManager.utils.Util;

/**
 * Defines layout for Profile list items
 * 
 * @author Mike Partridge / Jay Goldman
 */
public final class ProfileView extends LinearLayout
{
  
  // ids for elements of view which are dynamically created
  // since all of the elements are dynamic we can choose any values we want
  private static final int PROFILE_ACTIVE_ID = 1000;
  private static final int PROFILE_NAME_ID = 1001;
  private static final int PROFILE_NEXT_START_TIME_ID = 1002;
  
  /**
   * @param context
   * @param schedule
   */
  public ProfileView(Context context, Profile profile)
  {
    super(context);

    this.setOrientation(VERTICAL);

    // convenience for addView calls later
    LinearLayout.LayoutParams paramsFillWrap = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

    // add Toggle button to show whether profile is active
    CheckBox cb = new CheckBox(context);
    cb.setId(PROFILE_ACTIVE_ID);
    // tb.setTextOn("Enabled");
    // tb.setTextOff("Disabled");
    cb.setEnabled(false);
    cb.setClickable(false);
    cb.setFocusable(false);
    cb.setPadding(0, 0, 10, 0);

    TextView name = new TextView(context);
    name.setId(PROFILE_NAME_ID);

    float zz = name.getTextSize();
    name.setPadding(2, 2, 2, 2);
    // int hh = tb.getHeight();
    name.setTextSize(zz);
    name.setGravity(Gravity.LEFT);
    // name.setLayoutParams(paramsFillWrap);

    TextView blanks = new TextView(context);
    blanks.setPadding(2, 2, 2, 2);
    blanks.setText("   ");
    blanks.setTextSize(zz);


    TextView next = new TextView(context);
    blanks.setPadding(10, 2, 10, 2);
    next.setId(PROFILE_NEXT_START_TIME_ID);
    next.setTextSize(zz);
    next.setGravity(Gravity.RIGHT);
    //next.setMinWidth((int) (zz*10));
    
    TableRow profileRow = new TableRow(context);
    profileRow.addView(cb);
    profileRow.addView(name);
    profileRow.addView(blanks);
    
    TableRow.LayoutParams right = new TableRow.LayoutParams(
        TableRow.LayoutParams.FILL_PARENT,
        TableRow.LayoutParams.WRAP_CONTENT);
    right.gravity = Gravity.RIGHT+Gravity.CENTER_VERTICAL;
    
    profileRow.addView(next, right);

    TableLayout tableLayout = new TableLayout(context);
    tableLayout.setId((int) profile.getId());
    tableLayout.addView(profileRow);
    tableLayout.setColumnStretchable(3, true);
    addView(tableLayout, paramsFillWrap);

    ScheduleHelper.init(context);
    setState(profile);
  }

  /**
   * @param schedule
   */
  public void setFromProfile(Profile profile)
  {
    setState(profile);
  }

  /**
   * @param profile
   */
  private void setState(Profile profile)
  {
    boolean disabled = Util.isBooleanPref(getContext(), R.string.disableProfiles, false);
    CheckBox cb = (CheckBox) this.findViewById(PROFILE_ACTIVE_ID);
    if (null != cb)
    {
      cb.setChecked(!disabled && profile.isActive());
    }
    TextView name = (TextView) this.findViewById(PROFILE_NAME_ID);
    if (null != name)
    {
      name.setText(profile.getName());
      if (disabled)
      {
        name.setTextColor(Color.WHITE);
      }
      else
      {
        name.setTextColor(profile.isActive() ? ScheduleHelper.ACTIVE_COLOR : ScheduleHelper.INACTIVE_COLOR);
      }
    }
    TextView next = (TextView) this.findViewById(PROFILE_NEXT_START_TIME_ID);
    if (null != next)
    {
      ScheduleHelper helper = new ScheduleHelper(getContext());
      List<ScheduleEntry> schedules = helper.getList(ScheduleHelper.FILTER_SCHEDULE_PROFILE_ID, profile.getId());
      String nextText = ScheduleEntry.nextActive(getContext(), schedules);
      next.setText((null != nextText) ? nextText : "");
    }
  }

}
