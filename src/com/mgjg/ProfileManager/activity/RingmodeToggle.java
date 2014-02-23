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

public class RingmodeToggle extends Activity
{

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle instanceState)
  {
    super.onCreate(instanceState);
    setContentView(R.layout.ringmode_toggle);

    final AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    setupButtons(audio);

  }

  private void setupButtons(final AudioManager audio)
  {
    int ringmode = audio.getRingerMode();

    final RadioButton ringVibrate = (RadioButton) findViewById(R.id.ring_vibrate);
    final RadioButton vibrateOnly = (RadioButton) findViewById(R.id.vibrate_only);
    final RadioButton silent = (RadioButton) findViewById(R.id.silent);

    switch (ringmode)
    {
    case AudioManager.RINGER_MODE_SILENT:
      silent.setChecked(true);
      break;
    case AudioManager.RINGER_MODE_VIBRATE:
      vibrateOnly.setChecked(true);
      break;
    case AudioManager.RINGER_MODE_NORMAL:
      ringVibrate.setChecked(true);
      break;
    }

    ringVibrate.setOnCheckedChangeListener(new OnCheckedChangeListener() {

      public void onCheckedChanged(CompoundButton button,
          boolean isChecked)
      {
        if (isChecked)
        {
          audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
          //audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_ON);
        }
      }

    });

    vibrateOnly.setOnCheckedChangeListener(new OnCheckedChangeListener() {

      public void onCheckedChanged(CompoundButton buttonView,
          boolean isChecked)
      {
        if (isChecked)
        {
          audio.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        }
      }

    });

    silent.setOnCheckedChangeListener(new OnCheckedChangeListener() {

      public void onCheckedChanged(CompoundButton buttonView,
          boolean isChecked)
      {
        if (isChecked)
        {
          audio.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        }
      }

    });
  }

  public static void fixRingMode(AudioManager audio)
  {
    int vol = audio.getStreamVolume(AudioManager.STREAM_RING);
    RingmodeToggle.fixRingMode(audio, vol);
  }

  public static void fixRingMode(AudioManager audio, int vol)
  {
    int ringerVibrate = audio.getRingerMode();

    if (vol == 0 && ringerVibrate != AudioManager.RINGER_MODE_VIBRATE)
    {
      audio.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
    }
    else if (vol == 0 && ringerVibrate == AudioManager.RINGER_MODE_NORMAL)
    {
      audio.setRingerMode(AudioManager.RINGER_MODE_SILENT);
    }
    else if (vol > 0)
    {
      audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    super.onCreateOptionsMenu(menu);
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.ringer_menu, menu);
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
      intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "RingMode Toggle");
      Parcelable iconResource = Intent.ShortcutIconResource.fromContext(this, R.drawable.bell);
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
