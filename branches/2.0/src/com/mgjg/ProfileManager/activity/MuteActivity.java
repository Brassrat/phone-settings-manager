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
import android.media.AudioManager;
import android.os.Bundle;

import com.mgjg.ProfileManager.R;
import com.mgjg.ProfileManager.utils.Util;

public class MuteActivity extends Activity
{

  @Override
  protected void onCreate(Bundle instanceState)
  {
    super.onCreate(instanceState);

    AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    boolean muted = Util.isBooleanPref(this, R.string.muted, false);

    final int flagsNoUI = AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE |
        AudioManager.FLAG_VIBRATE;

    if (muted)
    {
      int systemVol = Util.getIntPref(this, R.string.SavedSystemVolume, -1);
      int ringerVol = Util.getIntPref(this, R.string.SavedRingerVolume, -1);
      int notifVol = Util.getIntPref(this, R.string.SavedNotifVolume, -1);
      int alarmVol = Util.getIntPref(this, R.string.SavedAlarmVolume, -1);
      int mediaVol = Util.getIntPref(this, R.string.SavedMediaVolume, -1);

      RingmodeToggle.fixRingMode(audio, ringerVol);
      if (systemVol != -1)
      {
        audio.setStreamVolume(AudioManager.STREAM_SYSTEM, systemVol, flagsNoUI);
      }
      if (ringerVol != -1)
      {
        audio.setStreamVolume(AudioManager.STREAM_RING, ringerVol, flagsNoUI);
      }
      if (notifVol != -1)
      {
        audio.setStreamVolume(AudioManager.STREAM_NOTIFICATION, notifVol, flagsNoUI);
      }
      if (alarmVol != -1)
      {
        audio.setStreamVolume(AudioManager.STREAM_ALARM, alarmVol, flagsNoUI);
      }
      if (mediaVol != -1)
      {
        audio.setStreamVolume(AudioManager.STREAM_MUSIC, mediaVol, flagsNoUI);
      }

      Util.putBooleanPref(this, R.string.muted, false);
      Util.putBooleanPref(this, R.string.disableProfiles, false);
    }
    else
    {
      Util.putIntPref(this, R.string.SavedSystemVolume, audio.getStreamVolume(AudioManager.STREAM_SYSTEM));
      Util.putIntPref(this, R.string.SavedRingerVolume, audio.getStreamVolume(AudioManager.STREAM_RING));
      Util.putIntPref(this, R.string.SavedNotifVolume, audio.getStreamVolume(AudioManager.STREAM_NOTIFICATION));
      Util.putIntPref(this, R.string.SavedAlarmVolume, audio.getStreamVolume(AudioManager.STREAM_ALARM));
      Util.putIntPref(this, R.string.SavedMediaVolume, audio.getStreamVolume(AudioManager.STREAM_MUSIC));

      Util.putBooleanPref(this, R.string.muted, true);
      Util.putBooleanPref(this, R.string.disableProfiles, true);

      RingmodeToggle.fixRingMode(audio, 0);
      audio.setStreamVolume(AudioManager.STREAM_SYSTEM, 0, flagsNoUI);
      audio.setStreamVolume(AudioManager.STREAM_RING, 0, flagsNoUI);
      audio.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 0, flagsNoUI);
      audio.setStreamVolume(AudioManager.STREAM_ALARM, 0, flagsNoUI);
      audio.setStreamVolume(AudioManager.STREAM_MUSIC, 0, flagsNoUI);
    }

    finish();
  }
}
