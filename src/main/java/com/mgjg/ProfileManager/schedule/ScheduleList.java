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
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.TextView;

import com.mgjg.ProfileManager.R;
import com.mgjg.ProfileManager.profile.activity.ProfileListActivity;
import com.mgjg.ProfileManager.provider.ScheduleHelper;

import java.util.List;

import static com.mgjg.ProfileManager.provider.ScheduleHelper.COLUMN_SCHEDULE_ACTIVE;
import static com.mgjg.ProfileManager.provider.ScheduleHelper.FILTER_SCHEDULE_ID;
import static com.mgjg.ProfileManager.provider.ScheduleHelper.FILTER_SCHEDULE_PROFILE_ID;
import static com.mgjg.ProfileManager.provider.ScheduleHelper.INTENT_SCHEDULE_ID;
import static com.mgjg.ProfileManager.provider.ScheduleHelper.INTENT_SCHEDULE_PROFILE_ID;
import static com.mgjg.ProfileManager.provider.ScheduleHelper.INTENT_SCHEDULE_PROFILE_NAME;

/**
 * Schedule List
 *
 * @author Mike Partridge
 */
public final class ScheduleList extends ProfileListActivity
{

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
  }

  protected void onCreateInstance(Bundle instanceState)
  {
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
    header.setText(headerText());
  }

  private String headerText()
  {
    return getText(R.string.ScheduleListProfile) + " " + profileName;
  }

  /**
   * retrieves schedules from the db and populates the list
   */
  @Override
  protected void fillData()
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

  @Override
  protected void newListItem()
  {
    Intent ii = new Intent(this, ScheduleEdit.class)
        .putExtra(INTENT_SCHEDULE_PROFILE_ID, profileId)
        .putExtra(INTENT_SCHEDULE_PROFILE_NAME, profileName);
    startActivityForResult(ii, ACTIVITY_CREATE);
  }

  protected boolean itemIsActive(AdapterContextMenuInfo menuInfo)
  {
    int position = menuInfo.position;
    ScheduleEntry sched = (ScheduleEntry) getListView().getItemAtPosition(position);
    return sched.isActive();
  }

  protected void deleteItem(AdapterContextMenuInfo info)
  {
    new ScheduleHelper(this).deleteSchedule(info.id);
  }

  /*
   * (non-Javadoc)
   *
   * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
   */
  @Override
  public void onCreateContextMenu(ContextMenu menu, View vv, ContextMenuInfo menuInfo)
  {
    onCreateContextMenu(R.menu.schedulelist_context, menu, vv, menuInfo);
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
    switch (item.getItemId())
    {
      case R.id.edit:
        editSchedule(info.id);
        break;

      case R.id.delete:
        deleteConfirmed("Schedule ", info);
        break;

      case R.id.toggle:
        toggleSchedule(info.id);
        break;

      case R.id.applySettings:
        new ScheduleHelper(this).setAlarm(info.id, true);
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
    return false; // do not show, access via button
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
      case R.id.newItemButton:
        newListItem();
        break;

      // todo add setting all alarms for a profile
      // case R.id.applySettings:
      // new ScheduleHelper(this).setAlarm();
      // break;

      default:
        return super.onOptionsItemSelected(item);
    }
    return true;

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

  private void editSchedule(long scheduleId)
  {
    Intent ii = new Intent(this, ScheduleEdit.class)
        .putExtra(INTENT_SCHEDULE_ID, scheduleId)
        .putExtra(INTENT_SCHEDULE_PROFILE_ID, profileId)
        .putExtra(INTENT_SCHEDULE_PROFILE_NAME, profileName);
    startActivityForResult(ii, ACTIVITY_EDIT);
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

    fillData();
  }

}
