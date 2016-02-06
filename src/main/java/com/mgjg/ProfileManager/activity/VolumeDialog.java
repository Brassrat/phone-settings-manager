/**
 * Copyright 2009 Daniel Roozen
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
package com.mgjg.ProfileManager.activity;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.mgjg.ProfileManager.R;

public class VolumeDialog extends Activity
{

  public static final String TYPE = "TYPE";

  @Override
  protected void onCreate(Bundle instanceState)
  {
    super.onCreate(instanceState);

    Bundle extras = getIntent().getExtras();
    final int volumeType = extras != null ? extras.getInt(TYPE) : -1;

    final AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    String title = "Unknown Volume";
    if (volumeType != -1)
    {
      switch (volumeType)
      {
        case AudioManager.STREAM_SYSTEM:
          title = getString(R.string.title_system);
          break;
        case AudioManager.STREAM_RING:
          title = getString(R.string.title_ringer);
          break;
        case AudioManager.STREAM_NOTIFICATION:
          title = getString(R.string.title_notif);
          break;
        case AudioManager.STREAM_MUSIC:
          title = getString(R.string.title_media);
          break;
        case AudioManager.STREAM_VOICE_CALL:
          title = getString(R.string.title_phonecall);
          break;
        case AudioManager.STREAM_ALARM:
          title = getString(R.string.title_alarm);
          break;
      }

      setContentView(R.layout.volume_edit);
      setTitle(title);

      final SeekBar systemSeek = (SeekBar) findViewById(R.id.volume_bar);
      systemSeek.setMax(audio.getStreamMaxVolume(volumeType));
      systemSeek.setProgress(audio.getStreamVolume(volumeType));
      systemSeek.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
      {

        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromTouch)
        {
          // ignore
        }

        public void onStartTrackingTouch(SeekBar seekBar)
        {
          // ignore
        }

        public void onStopTrackingTouch(SeekBar seekBar)
        {
          final int setVolFlags = AudioManager.FLAG_PLAY_SOUND |
              AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE |
              AudioManager.FLAG_SHOW_UI | AudioManager.FLAG_VIBRATE;
          audio.setStreamVolume(volumeType, seekBar.getProgress(), setVolFlags);
        }

      });

      Button ok = (Button) findViewById(R.id.ok_button);
      ok.setOnClickListener(new OnClickListener()
      {

        public void onClick(View v)
        {
          if (volumeType == AudioManager.STREAM_SYSTEM ||
              volumeType == AudioManager.STREAM_RING ||
              volumeType == AudioManager.STREAM_NOTIFICATION)
          {
            audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
          }

          finish();
        }

      });

    }
    else
    {
      finish();
    }
  }

}
