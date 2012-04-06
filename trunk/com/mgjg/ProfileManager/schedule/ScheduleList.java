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
package com.mgjg.ProfileManager.schedule;

import static com.mgjg.ProfileManager.provider.ScheduleHelper.INTENT_SCHEDULE_ID;
import static com.mgjg.ProfileManager.provider.ScheduleHelper.INTENT_SCHEDULE_PROFILE_ID;
import static com.mgjg.ProfileManager.provider.ScheduleHelper.INTENT_SCHEDULE_PROFILE_NAME;
import static com.mgjg.ProfileManager.provider.ScheduleHelper.COLUMN_SCHEDULE_ACTIVE;
import static com.mgjg.ProfileManager.provider.ScheduleHelper.FILTER_SCHEDULE_ID;
import static com.mgjg.ProfileManager.provider.ScheduleHelper.FILTER_SCHEDULE_PROFILE_ID;

import java.util.List;

import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.mgjg.ProfileManager.R;
import com.mgjg.ProfileManager.provider.ScheduleHelper;

/**
 * Schedule List
 * 
 * @author Mike Partridge
 */
public class ScheduleList extends ListActivity
{
  private static final int ACTIVITY_CREATE = 0;
  private static final int ACTIVITY_EDIT = 1;

  private long profileId;
  private String profileName;

  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onCreate(android.os.Bundle)
   */
  @Override
  protected void onCreate(Bundle instanceState)
  {
    super.onCreate(instanceState);

    if (instanceState == null)
    {
      Intent ii = getIntent();
      profileId = ii.getLongExtra(INTENT_SCHEDULE_PROFILE_ID, 0);
      profileName = ii.getCharSequenceExtra(INTENT_SCHEDULE_PROFILE_NAME).toString();
    }
    else
    {
      profileId = instanceState.getLong(INTENT_SCHEDULE_PROFILE_ID);
      profileName = instanceState.getString(INTENT_SCHEDULE_PROFILE_NAME);
    }

    if (null == profileName)
    {
      profileName = "NO NAME";
    }

    setContentView(R.layout.schedule_list);

    TextView header = (TextView) findViewById(R.id.ScheduleForProfile);
    header.setText(getText(R.string.ScheduleListProfile) + " " + profileName);

    fillData();

    registerForContextMenu(getListView());

    Button add = (Button) findViewById(R.id.newSchedule);
    add.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view)
      {
        newSchedule();
      }
    });
  }

  /**
   * retrieves schedules from the db and populates the list
   */
  private void fillData()
  {
    ScheduleHelper helper = new ScheduleHelper(this);
    setListAdapter(helper.createListAdapter(FILTER_SCHEDULE_PROFILE_ID, profileId));
  }

  /*
   * (non-Javadoc)
   * 
   * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
   */
  @Override
  protected void onListItemClick(ListView l, View v, int position, long id)
  {
    editSchedule(id);
  }

  private void editSchedule(long scheduleId)
  {
    Intent ii = new Intent(this, ScheduleEdit.class)
        .putExtra(INTENT_SCHEDULE_ID, scheduleId)
        .putExtra(INTENT_SCHEDULE_PROFILE_ID, profileId)
        .putExtra(INTENT_SCHEDULE_PROFILE_NAME, profileName);
    startActivityForResult(ii, ACTIVITY_EDIT);
  }

  private void newSchedule()
  {
    Intent ii = new Intent(this, ScheduleEdit.class)
        .putExtra(INTENT_SCHEDULE_PROFILE_ID, profileId)
        .putExtra(INTENT_SCHEDULE_PROFILE_NAME, profileName);
    startActivityForResult(ii, ACTIVITY_CREATE);
  }

  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
   */
  @Override
  public void onCreateContextMenu(ContextMenu menu, View v,
      ContextMenuInfo menuInfo)
  {
    super.onCreateContextMenu(menu, v, menuInfo);
    getMenuInflater().inflate(R.menu.schedulelist_context, menu);
  }

  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
   */
  @Override
  public boolean onContextItemSelected(MenuItem item)
  {
    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    ScheduleEntry sched = (ScheduleEntry) getListView().getItemAtPosition(info.position);
    // long scheduleId = sched.getId();
    long scheduleId = info.id;
    if (sched.getId() != scheduleId)
    {
      Log.e("com.mgjg.ProfileManager", "info.id <> sched.id");
    }
    switch (item.getItemId())
    {
    case R.id.editSchedule:
      editSchedule(scheduleId);
      break;

    case R.id.deleteSchedule:
      new ScheduleHelper(this).deleteSchedule(scheduleId);
      fillData();
      break;

    case R.id.toggleSchedule:
      toggleSchedule(scheduleId);
      fillData();
      break;

    case R.id.applySettings:
      new ScheduleHelper(this).setAlarm(scheduleId, true);
      break;

    default:
      return super.onContextItemSelected(item);
    }
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
   */
  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    boolean result = super.onCreateOptionsMenu(menu);
    getMenuInflater().inflate(R.menu.schedulelist_options, menu);
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
   */
  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {

    switch (item.getItemId())
    {
    case R.id.newSchedule:
      newSchedule();
      break;

      // todo add setting all alarms for a profile
//    case R.id.applySettings:
//      new ScheduleHelper(this).setAlarm();
//      break;
      
    default:
      return super.onOptionsItemSelected(item);
    }
    return true;

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
    fillData();
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
    instanceState.putLong(INTENT_SCHEDULE_PROFILE_ID, profileId);
    instanceState.putString(INTENT_SCHEDULE_PROFILE_NAME, profileName);
  }

  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
   */
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent intent)
  {
    super.onActivityResult(requestCode, resultCode, intent);

    if (resultCode == RESULT_OK &&
        (requestCode == ACTIVITY_EDIT || requestCode == ACTIVITY_CREATE))
    {

    }

    fillData();
  }

  private void toggleSchedule(long scheduleId)
  {

    boolean active = true;

    /*
     * retrieve the active state for this schedule
     */
    ScheduleHelper helper = new ScheduleHelper(this);
    List<ScheduleEntry> schedules = helper.getList(FILTER_SCHEDULE_ID, scheduleId);
    if (!schedules.isEmpty())
    {
      ScheduleEntry schedule = schedules.get(0);
      active = schedule.isActive();
    }
    ContentValues values = new ContentValues();

    // flip it
    values.put(COLUMN_SCHEDULE_ACTIVE, active ? "0" : "1");
    helper.update(FILTER_SCHEDULE_ID, scheduleId, values);

    // set/unset the alarm
    helper.setAlarm(scheduleId, !active);
  }

  @Override
  public void onBackPressed()
  {
    setResult(RESULT_OK);
    super.finish();
  }
}
