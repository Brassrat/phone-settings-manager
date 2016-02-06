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

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.mgjg.ProfileManager.R;
import com.mgjg.ProfileManager.attribute.AttributeList;
import com.mgjg.ProfileManager.profile.Profile;
import com.mgjg.ProfileManager.provider.AttributeHelper;
import com.mgjg.ProfileManager.provider.ProfileHelper;
import com.mgjg.ProfileManager.schedule.ScheduleList;

import java.util.List;

import static com.mgjg.ProfileManager.provider.AttributeHelper.INTENT_ATTRIBUTE_PROFILE_ID;
import static com.mgjg.ProfileManager.provider.AttributeHelper.INTENT_ATTRIBUTE_PROFILE_NAME;
import static com.mgjg.ProfileManager.provider.ProfileHelper.INTENT_PROFILE_ACTIVE;
import static com.mgjg.ProfileManager.provider.ProfileHelper.INTENT_PROFILE_ID;
import static com.mgjg.ProfileManager.provider.ScheduleHelper.INTENT_SCHEDULE_PROFILE_ID;
import static com.mgjg.ProfileManager.provider.ScheduleHelper.INTENT_SCHEDULE_PROFILE_NAME;

/**
 * Profile Edit screen
 *
 * @author Mike Partridge/ Jay Goldman
 */
public class ProfileEdit extends Activity
{

  private TextView name;
  // private boolean clearNoName;
  private CheckBox active;

  private Long profileId;
  private Profile profile;

  private boolean mSaved;

  /*
   * (non-Javadoc)
   *
   * @see android.app.Activity#onCreate(android.os.Bundle)
   */
  @Override
  protected void onCreate(Bundle instanceState)
  {
    super.onCreate(instanceState);

    setContentView(R.layout.profile_edit);

    /*
     * check the saved state for profile id, then check the bundle passed through the Intent
     */
    profileId = instanceState != null ? instanceState.getLong(INTENT_PROFILE_ID) : null;
    if (profileId == null)
    {
      profileId = getIntent().getLongExtra(INTENT_PROFILE_ID, 0);
      if (profileId < 1)
      {
        profileId = null;
      }
    }

    name = (TextView) findViewById(R.id.nameLabel);
    // clearNoName = true;
    // name.setOnClickListener(new OnClickListener() {
    //
    // @Override
    // public void onClick(View v)
    // {
    //
    // if (clearNoName && ("NO NAME".equals(name.getText().toString())))
    // {
    // clearNoName = false;
    // name.setText("");
    // }
    //
    // }
    //
    // });

    // name.setOnKeyListener(new OnKeyListener() {
    //
    // @Override
    // public boolean onKey(View v, int keyCode, KeyEvent event)
    // {
    //
    // if (clearNoName && ("NO NAME".equals(name.getText().toString())))
    // {
    // clearNoName = false;
    // name.setText("");
    // }
    // return false;
    //
    // }
    //
    // });
    active = (CheckBox) findViewById(R.id.activeCheckbox);
    populateFields();

    Button editAttributes = (Button) findViewById(R.id.edit_attributes);
    editAttributes.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View view)
      {
        editAttributes();
      }
    });

    Button editSchedules = (Button) findViewById(R.id.edit_schedules);
    editSchedules.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View view)
      {
        editSchedules();
      }
    });

    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    name.setFocusable(true);
    name.setFocusableInTouchMode(true);

    mSaved = false;

    Button done = (Button) findViewById(R.id.doneButton);
    if (null != done)
    {
      done.setOnClickListener(new OnClickListener()
      {

        @Override
        public void onClick(View v)
        {
          done();
        }
      });
    }
  }

  private void done()
  {
    CharSequence nm = name.getText();
    if ((nm.length() <= 0) || "NO NAME".equals(nm))
    {
      CharSequence msg = this.getText(R.string.mustSetProfileNameBeforeSave);
      Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    else
    {
      finish();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    super.onCreateOptionsMenu(menu);
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.profile_menu, menu);
    return true;
  }

  private void editAttributes()
  {
    if (null == profileId)
    {
      // must save before we edit
      saveState(false);
    }
    if (null == profileId)
    {
      CharSequence msg = this.getText(R.string.mustSetProfileNameBeforeEditAttributes);
      Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
      return;
    }
    Intent ai = new Intent(this, AttributeList.class)
        .putExtra(INTENT_ATTRIBUTE_PROFILE_ID, profileId)
        .putExtra(INTENT_ATTRIBUTE_PROFILE_NAME, name.getText());
    startActivity(ai);
  }

  private void editSchedules()
  {
    if (null == profileId)
    {
      // must save before we edit
      saveState(false);
    }
    if (null == profileId)
    {
      CharSequence msg = this.getText(R.string.mustSetProfileNameBeforeEditSchedules);
      Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
      return;
    }
    Intent si = new Intent(this, ScheduleList.class)
        .putExtra(INTENT_SCHEDULE_PROFILE_ID, profileId)
        .putExtra(INTENT_SCHEDULE_PROFILE_NAME, name.getText());
    startActivity(si);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {

    switch (item.getItemId())
    {
//    case R.id.done:
//      finish();
//      return true;

      case R.id.applySettings:
        if (null != profileId)
        {
          AttributeHelper.activate(this, profileId);
        }
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  /*
   * (non-Javadoc)
   *
   * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
   */
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data)
  {
    data.putExtra(INTENT_ATTRIBUTE_PROFILE_ID, profileId);
    super.onActivityResult(requestCode, resultCode, data);
  }

  /**
   * Populate GUI with data from the db if the profile exists, or with defaults if not
   */
  private void populateFields()
  {

    ProfileHelper helper = new ProfileHelper(this);

    /*
     * load data
     */
    if (profileId != null)
    {

      List<Profile> profiles = helper.getList(ProfileHelper.FILTER_PROFILE_ID, profileId);
      if (!profiles.isEmpty())
      {
        profile = profiles.get(0);
      }
    }

    /*
     * new profile - populate defaults
     */
    if (profile == null)
    {
      profileId = null;
      profile = new Profile(0, "NO NAME", 0, true);
      name.setText("");
    }
    else
    {
      name.setText(profile.getName());
    }

    active.setChecked(profile.isActive());
  }

  /*
   * compare the profile (as populated from the db) to the current state of each gui field
   */
  private boolean isModified()
  {
    boolean result = false;

    if (profileId == null ||
        profile == null ||
        (active.isChecked() != profile.isActive()) ||
        (!(name.getText().toString().equals(profile.getName()))))
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
    if (isModified() && !mSaved)
    {
      saveState(true);
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
  }

  @Override
  protected void onRestoreInstanceState(Bundle instanceState)
  {
    populateFields();
    mSaved = false;
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
    if (isModified() && !mSaved)
    {
      saveState(true);
    }

    // store some things for re-display on resume
    if (profileId != null)
    {
      instanceState.putLong(INTENT_PROFILE_ID, profileId);
    }
  }

  /**
   * writes profile to db
   */
  private Long saveState(boolean toastOnNoSave)
  {

    profile.setName(name.getText().toString());
    if ((profile.getName().length() <= 0) || "NO NAME".equals(profile.getName()))
    {
      // no name ... no save
      if (toastOnNoSave)
      {
        CharSequence msg = this.getText(R.string.mustSetProfileNameBeforeSave);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
      }
      return null;
    }
    profile.setActive(active.isChecked());

    ProfileHelper helper = new ProfileHelper(this);

    if (profileId == null)
    {
      Uri newProfile = helper.insert(profile.makeValues());
      profileId = Long.parseLong(newProfile.getPathSegments().get(1));
    }
    else
    {
      helper.update(ProfileHelper.FILTER_PROFILE_ID, profileId, profile.makeValues());
    }

    mSaved = true;

    Toast.makeText(this, R.string.savedProfile, Toast.LENGTH_SHORT).show();
    return profileId;
  }

  /*
   * (non-Javadoc)
   *
   * @see android.app.Activity#finish()
   */
  @Override
  public void finish()
  {

    if (isModified())
    {
      if (!mSaved)
      {
        saveState(true);
      }
    }

    setResult(RESULT_OK,
        new Intent().putExtra(INTENT_PROFILE_ID, profileId)
            .putExtra(INTENT_PROFILE_ACTIVE, active.isChecked()));

    super.finish();
  }

  @Override
  public void onBackPressed()
  {
    setResult(RESULT_CANCELED);
    super.finish();
  }

}
