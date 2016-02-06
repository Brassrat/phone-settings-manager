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
package com.mgjg.ProfileManager.profile.activity;

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

import com.mgjg.ProfileManager.R;
import com.mgjg.ProfileManager.profile.Profile;
import com.mgjg.ProfileManager.provider.AttributeHelper;
import com.mgjg.ProfileManager.provider.ProfileHelper;
import com.mgjg.ProfileManager.provider.ScheduleHelper;
import com.mgjg.ProfileManager.schedule.ScheduleEntry;
import com.mgjg.ProfileManager.utils.Util;

import java.util.List;

import static com.mgjg.ProfileManager.provider.ProfileHelper.COLUMN_PROFILE_ACTIVE;
import static com.mgjg.ProfileManager.provider.ProfileHelper.FILTER_PROFILE_ID;
import static com.mgjg.ProfileManager.provider.ProfileHelper.INTENT_PROFILE_ID;
import static com.mgjg.ProfileManager.provider.ScheduleHelper.FILTER_SCHEDULE_PROFILE_ID;

/**
 * Profile List
 *
 * @author Jay Goldman
 */
public final class ProfileList extends ProfileListActivity
{
  private static final int ACTIVITY_CREATE = 0;
  private static final int ACTIVITY_EDIT = 1;

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
    setContentView(R.layout.profile_list);
  }

  /**
   * retrieves profiles from the db and populates the list
   */
  protected void fillData()
  {
    ProfileHelper helper = new ProfileHelper(this);
    setListAdapter(helper.createListAdapter(ProfileHelper.NO_FILTER, (Object) null));
  }

  /*
   * (non-Javadoc)
   *
   * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
   */
  @Override
  protected void onListItemClick(ListView l, View v, int position, long id)
  {
    editProfile(id);
  }

  private void editProfile(long profileId)
  {
    Intent ii = new Intent(this, ProfileEdit.class)
        .putExtra(INTENT_PROFILE_ID, profileId);
    startActivityForResult(ii, ACTIVITY_EDIT);
  }

  private void registerProfile(long profileId)
  {
    if (profileId > 0)
    {
      ProfileHelper helper = new ProfileHelper(this);
      List<Profile> profiles = helper.getList(FILTER_PROFILE_ID, profileId);

      /*
       * retrieve the active state for this profile
       */
      if (!profiles.isEmpty())
      {
        Profile profile = profiles.get(0);
        boolean active = profile.isActive();
        ScheduleHelper schedHelper = new ScheduleHelper(this);
        List<ScheduleEntry> schedules = schedHelper.getList(FILTER_SCHEDULE_PROFILE_ID, profileId);

        for (ScheduleEntry schedule : schedules)
        {
          // set/unset all alarms for this profile
          schedHelper.setAlarm(schedule.getId(), active);
        }
      }
    }
  }

  protected void newListItem()
  {
    Intent ii = new Intent(this, ProfileEdit.class)
        .putExtra(INTENT_PROFILE_ID, 0L);
    startActivityForResult(ii, ACTIVITY_CREATE);
    long profileId = ii.getLongExtra(INTENT_PROFILE_ID, 0);
    registerProfile(profileId);
  }

  @Override
  protected boolean itemIsActive(AdapterContextMenuInfo menuInfo)
  {
    int position = menuInfo.position;
    Profile pp = (Profile) getListView().getItemAtPosition(position);
    return pp.isActive();
  }

  @Override
  protected void deleteItem(AdapterContextMenuInfo info)
  {
    ProfileHelper helper = new ProfileHelper(this);
    ScheduleHelper schedHelper = new ScheduleHelper(this);
    List<ScheduleEntry> schedules = schedHelper.getList(FILTER_SCHEDULE_PROFILE_ID, info.id);

    for (ScheduleEntry schedule : schedules)
    {
      // set/unset all alarms for this profile
      schedHelper.setAlarm(schedule.getId(), false);
    }

    new AttributeHelper(this).deleteProfile(info.id);

    helper.delete(FILTER_PROFILE_ID, info.id);
  }

  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
   */

  @Override
  public void onCreateContextMenu(ContextMenu menu, View vv, ContextMenuInfo menuInfo)
  {
    onCreateContextMenu(R.menu.profilelist_context, menu, vv, menuInfo);
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
        editProfile(info.id);
        return true;

      case R.id.delete:
        Profile pp = (Profile) getListView().getItemAtPosition(info.position);
        deleteConfirmed("Profile " + pp.getName(), info);
        return true;

      case R.id.toggle:
        toggleProfile(info.id);
        return true;

      case R.id.applySettings:
        AttributeHelper.activate(this, info.id);
        return true;

    }

    return super.onContextItemSelected(item);
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
    getMenuInflater().inflate(R.menu.profilelist_options, menu);
    return result;
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu)
  {
    MenuItem item = menu.findItem(R.id.disable_profiles);
    if (null != item)
    {
      boolean disabled = Util.isBooleanPref(this, R.string.disableProfiles, false);
      CharSequence menuTitle = this.getText(disabled ? R.string.enable : R.string.disable);
      item.setTitle(menuTitle);
      item.setTitleCondensed(menuTitle);
    }
    super.onPrepareOptionsMenu(menu);
    return true;
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
      case R.id.disable_profiles:
        boolean disabled = Util.isBooleanPref(this, R.string.disableProfiles, false);
        Util.putBooleanPref(this, R.string.disableProfiles, !disabled);
        fillData();
        return true;

      case R.id.add_profile:
        newListItem();
        return true;

      case R.id.applySettings:
        new ScheduleHelper(this).registerAlarm();
        return true;
    }

    return super.onOptionsItemSelected(item);
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
  }

  /*
   * (non-Javadoc)
   *
   * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
   */
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data)
  {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == ACTIVITY_EDIT || requestCode == ACTIVITY_CREATE)
    {
      if (null != data)
      {
        long profileId = data.getLongExtra(INTENT_PROFILE_ID, 0);
        registerProfile(profileId);
      }
    }

    fillData();
  }

  private void toggleProfile(long profileId)
  {

    ProfileHelper helper = new ProfileHelper(this);
    List<Profile> profiles = helper.getList(FILTER_PROFILE_ID, profileId);

    /*
     * retrieve the active state for this profile
     */
    if (!profiles.isEmpty())
    {
      Profile profile = profiles.get(0);
      final boolean active = !profile.isActive();

      ContentValues values = new ContentValues();

      // flip it
      values.put(COLUMN_PROFILE_ACTIVE, active ? "1" : "0");
      helper.update(FILTER_PROFILE_ID, profileId, values);

      // see if this profile has any schedules
      ScheduleHelper schedHelper = new ScheduleHelper(this);
      List<ScheduleEntry> schedules = schedHelper.getList(FILTER_SCHEDULE_PROFILE_ID, profileId);

      for (ScheduleEntry schedule : schedules)
      {
        // toggle the alarm
        schedHelper.setAlarm(schedule.getId(), active);
      }
      fillData();
    }

  }

}
