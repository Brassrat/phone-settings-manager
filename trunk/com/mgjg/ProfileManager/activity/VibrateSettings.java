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
    int vibrateRinger = audio.getVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER);
    int vibrateNotif = audio.getVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION);

    final RadioButton ringerAlways = (RadioButton) findViewById(R.id.ringer_vibrate_when_possible);
    final RadioButton ringerSilent = (RadioButton) findViewById(R.id.ringer_vibrate_when_silent);
    final RadioButton ringerNever = (RadioButton) findViewById(R.id.ringer_vibrate_never);

    final RadioButton notifAlways = (RadioButton) findViewById(R.id.notif_vibrate_when_possible);
    final RadioButton notifSilent = (RadioButton) findViewById(R.id.notif_vibrate_when_silent);
    final RadioButton notifNever = (RadioButton) findViewById(R.id.notif_vibrate_never);

    switch (vibrateRinger)
    {
    case AudioManager.VIBRATE_SETTING_ON:
      ringerAlways.setChecked(true);
      break;
    case AudioManager.VIBRATE_SETTING_ONLY_SILENT:
      ringerSilent.setChecked(true);
      break;
    case AudioManager.VIBRATE_SETTING_OFF:
      ringerNever.setChecked(true);
      break;
    }

    switch (vibrateNotif)
    {
    case AudioManager.VIBRATE_SETTING_ON:
      notifAlways.setChecked(true);
      break;
    case AudioManager.VIBRATE_SETTING_ONLY_SILENT:
      notifSilent.setChecked(true);
      break;
    case AudioManager.VIBRATE_SETTING_OFF:
      notifNever.setChecked(true);
      break;
    }

    ringerAlways.setOnCheckedChangeListener(new OnCheckedChangeListener() {

      public void onCheckedChanged(CompoundButton button, boolean isChecked)
      {
        if (isChecked)
        {
          audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_ON);
          RingmodeToggle.fixRingMode(audio);
        }
      }

    });

    ringerSilent.setOnCheckedChangeListener(new OnCheckedChangeListener() {

      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
      {
        if (isChecked)
        {
          audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_ONLY_SILENT);
          RingmodeToggle.fixRingMode(audio);
        }
      }

    });

    ringerNever.setOnCheckedChangeListener(new OnCheckedChangeListener() {

      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
      {
        if (isChecked)
        {
          audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_OFF);
          RingmodeToggle.fixRingMode(audio);
        }
      }

    });

    notifAlways.setOnCheckedChangeListener(new OnCheckedChangeListener() {

      public void onCheckedChanged(CompoundButton button, boolean isChecked)
      {
        if (isChecked)
        {
          audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION, AudioManager.VIBRATE_SETTING_ON);
        }
      }

    });

    notifSilent.setOnCheckedChangeListener(new OnCheckedChangeListener() {

      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
      {
        if (isChecked)
        {
          audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION, AudioManager.VIBRATE_SETTING_ONLY_SILENT);
        }
      }

    });

    notifNever.setOnCheckedChangeListener(new OnCheckedChangeListener() {

      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
      {
        if (isChecked)
        {
          audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION, AudioManager.VIBRATE_SETTING_OFF);
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
