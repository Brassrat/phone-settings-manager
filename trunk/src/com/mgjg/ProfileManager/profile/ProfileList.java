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

import static com.mgjg.ProfileManager.provider.ProfileHelper.COLUMN_PROFILE_ACTIVE;
import static com.mgjg.ProfileManager.provider.ProfileHelper.FILTER_PROFILE_ID;
import static com.mgjg.ProfileManager.provider.ProfileHelper.INTENT_PROFILE_ID;
import static com.mgjg.ProfileManager.provider.ScheduleHelper.FILTER_SCHEDULE_PROFILE_ID;

import java.util.List;

import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.mgjg.ProfileManager.R;
import com.mgjg.ProfileManager.provider.AttributeHelper;
import com.mgjg.ProfileManager.provider.ProfileHelper;
import com.mgjg.ProfileManager.provider.ScheduleHelper;
import com.mgjg.ProfileManager.schedule.ScheduleEntry;

/**
 * Profile List
 * 
 * @author Jay Goldman
 */
public final class ProfileList extends ListActivity
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
    setContentView(R.layout.profile_list);

    fillData();

    registerForContextMenu(getListView());

    Button add = (Button) findViewById(R.id.newProfile);
    add.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view)
      {
        newProfile();
      }
    });
  }

  /**
   * retrieves profiles from the db and populates the list
   */
  private void fillData()
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
    // registerProfile(profileId);
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

  private void newProfile()
  {
    Intent ii = new Intent(this, ProfileEdit.class)
        .putExtra(INTENT_PROFILE_ID, 0);
    startActivityForResult(ii, ACTIVITY_CREATE);
    long profileId = ii.getLongExtra(INTENT_PROFILE_ID, 0);
    registerProfile(profileId);
  }

  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
   */
  @Override
  public void onCreateContextMenu(ContextMenu menu, View vv, ContextMenuInfo menuInfo)
  {
    super.onCreateContextMenu(menu, vv, menuInfo);
    getMenuInflater().inflate(R.menu.profilelist_context, menu);
    MenuItem item = menu.findItem(R.id.toggleProfile);
    if (null != item)
    {
      AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
      ListAdapter la = getListAdapter();
      Profile pp = (Profile) la.getItem(info.position);
      CharSequence menuTitle = this.getText(pp.isEnabled() ? R.string.disable : R.string.enable);
      item.setTitle(menuTitle);
      item.setTitleCondensed(menuTitle);
    }
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
    ScheduleHelper schedHelper = new ScheduleHelper(this);
    List<ScheduleEntry> schedules;
    switch (item.getItemId())
    {
    case R.id.editProfile:
      editProfile(info.id);
      return true;

    case R.id.deleteProfile:
      ProfileHelper helper = new ProfileHelper(this);
      schedules = schedHelper.getList(FILTER_SCHEDULE_PROFILE_ID, info.id);

      for (ScheduleEntry schedule : schedules)
      {
        // set/unset all alarms for this profile
        schedHelper.setAlarm(schedule.getId(), false);
      }

      new AttributeHelper(this).deleteProfile(info.id);

      helper.delete(FILTER_PROFILE_ID, info.id);
      fillData();
      return true;

    case R.id.toggleProfile:
      toggleProfile(info.id);
      fillData();
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
    case R.id.newProfile:
      newProfile();
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

    boolean active = true;

    ProfileHelper helper = new ProfileHelper(this);
    List<Profile> profiles = helper.getList(FILTER_PROFILE_ID, profileId);

    /*
     * retrieve the active state for this profile
     */
    if (!profiles.isEmpty())
    {
      Profile profile = profiles.get(0);
      active = !profile.isActive();

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
    }

  }

  @Override
  public void onBackPressed()
  {
    // stop the current 'activity' i.e., exit without saving
    setResult(RESULT_CANCELED);
    super.finish();
  }

  @Override
  public void finish()
  {
    setResult(RESULT_OK);
    super.finish();
  }
}
