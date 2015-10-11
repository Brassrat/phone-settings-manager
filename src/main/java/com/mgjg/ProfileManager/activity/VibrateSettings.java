/**
 * Copyright 2009 Daniel Roozen
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
package com.mgjg.ProfileManager.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.Toast;

import com.mgjg.ProfileManager.R;
import com.mgjg.ProfileManager.attribute.builtin.sound.SoundAttribute;

public class VibrateSettings extends Activity
{

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle instanceState)
  {
    super.onCreate(instanceState);
    setContentView(R.layout.vibrate_settings);

    final AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

    setupButtons(audio);

  }

  private void setupButtons(final AudioManager audio)
  {
    final RadioButton ringerAlways = (RadioButton) findViewById(R.id.ringer_vibrate_when_possible);
    final RadioButton ringerSilent = (RadioButton) findViewById(R.id.ringer_vibrate_when_silent);
    final RadioButton ringerNever = (RadioButton) findViewById(R.id.ringer_vibrate_never);

    switch (audio.getRingerMode())
    {
    case AudioManager.RINGER_MODE_VIBRATE:
      ringerAlways.setChecked(true);
      break;
    case AudioManager.RINGER_MODE_NORMAL:
      ringerSilent.setChecked(true);
      break;
    case AudioManager.RINGER_MODE_SILENT:
      ringerNever.setChecked(true);
      break;
    }

    boolean vibrateNotif = SoundAttribute.isVibrateOn(this);

    final RadioButton notifAlways = (RadioButton) findViewById(R.id.notif_vibrate_when_possible);
    final RadioButton notifSilent = (RadioButton) findViewById(R.id.notif_vibrate_when_silent);
    final RadioButton notifNever = (RadioButton) findViewById(R.id.notif_vibrate_never);

    if (vibrateNotif)
    {
      notifAlways.setChecked(true);
    }
    else
    {
      notifNever.setChecked(true);
      notifSilent.setChecked(true);
    }
    // switch (vibrateNotif)
    // {
    // case AudioManager.RINGER_MODE_VIBRATE:
    // notifAlways.setChecked(true);
    // break;
    // case AudioManager.RINGER_MODE_NORMAL:
    // notifSilent.setChecked(true);
    // break;
    // case AudioManager.RINGER_MODE_SILENT:
    // notifNever.setChecked(true);
    // break;
    // }

    ringerAlways.setOnCheckedChangeListener(new OnCheckedChangeListener() {

      public void onCheckedChanged(CompoundButton button, boolean isChecked)
      {
        if (isChecked)
        {
          // audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_ON);
          audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
          RingmodeToggle.fixRingMode(audio);
        }
      }

    });

    ringerSilent.setOnCheckedChangeListener(new OnCheckedChangeListener() {

      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
      {
        if (isChecked)
        {
          // audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_ONLY_SILENT);
          audio.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
          RingmodeToggle.fixRingMode(audio);
        }
      }

    });

    ringerNever.setOnCheckedChangeListener(new OnCheckedChangeListener() {

      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
      {
        if (isChecked)
        {
          // audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_OFF);
          audio.setRingerMode(AudioManager.RINGER_MODE_SILENT);
          RingmodeToggle.fixRingMode(audio);
        }
      }

    });

    notifAlways.setOnCheckedChangeListener(new OnCheckedChangeListener() {

      public void onCheckedChanged(CompoundButton button, boolean isChecked)
      {
        if (isChecked)
        {
          SoundAttribute.setVibrate(AudioManager.RINGER_MODE_NORMAL);
          // audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION, AudioManager.VIBRATE_SETTING_ON);
        }
      }

    });

    notifSilent.setOnCheckedChangeListener(new OnCheckedChangeListener() {

      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
      {
        if (isChecked)
        {
          SoundAttribute.setVibrate(AudioManager.RINGER_MODE_VIBRATE);
          // audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION, AudioManager.VIBRATE_SETTING_ONLY_SILENT);
        }
      }

    });

    notifNever.setOnCheckedChangeListener(new OnCheckedChangeListener() {

      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
      {
        if (isChecked)
        {
          SoundAttribute.setVibrate(AudioManager.RINGER_MODE_SILENT);
          // audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION, AudioManager.VIBRATE_SETTING_OFF);
        }
      }

    });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    super.onCreateOptionsMenu(menu);
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.shortcut, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    switch (item.getItemId())
    {
    case R.id.create_shortcut:
      Intent shortcutIntent = new Intent(Intent.ACTION_MAIN);
      shortcutIntent.setClassName(this, this.getClass().getName());

      Intent intent = new Intent();
      intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
      intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "Vibration Settings");
      Parcelable iconResource = Intent.ShortcutIconResource.fromContext(this, R.drawable.sound_icon);
      intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconResource);

      intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
      sendBroadcast(intent);

      // Inform the user that the shortcut has been created
      Toast.makeText(this, "Shortcut Created", Toast.LENGTH_SHORT).show();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
}
