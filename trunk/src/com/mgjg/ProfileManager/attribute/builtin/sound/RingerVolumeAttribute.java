/**
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
package com.mgjg.ProfileManager.attribute.builtin.sound;

import static android.media.AudioManager.FLAG_VIBRATE;
import static android.media.AudioManager.RINGER_MODE_NORMAL;
import static android.media.AudioManager.RINGER_MODE_SILENT;
import static android.media.AudioManager.RINGER_MODE_VIBRATE;
import static android.media.AudioManager.VIBRATE_SETTING_OFF;
import static android.media.AudioManager.VIBRATE_SETTING_ON;
import android.content.Context;
import android.media.AudioManager;

public final class RingerVolumeAttribute extends SoundAttribute
{

  RingerVolumeAttribute()
  {
    this(null, "");
  }

  public RingerVolumeAttribute(Context context,String params)
  {
    super(context, params);
  }
  
  private RingerVolumeAttribute(long attributeId, long aProfileId, int aVolume, boolean aVibrate, String settings)
  {
    super(attributeId, aProfileId, aVolume, aVibrate, settings);
  }

  @Override
  public RingerVolumeAttribute createInstance(Context context, long profileId)
  {
    return new RingerVolumeAttribute(0, profileId, getVolumeForStream(context), isVibrateForStream(context), null);
  }

  @Override
  public RingerVolumeAttribute createInstance(long attributeId, long profileId, int volume, boolean vibrate, String settings)
  {
    return new RingerVolumeAttribute(attributeId, profileId, volume, vibrate, settings);
  }

  @Override
  public int getListOrder()
  {
    return ORDER_AUDIO_RING;
  }

  @Override
  public int getSoundAttributeIndex()
  {
    return SOUND_ATTR_RING;
  }

  @Override
  protected void activate(AudioManager audio)
  {
    audio.setStreamVolume(getAudioStreamId(), getNumber(), SET_VOL_FLAGS + (isBoolean() ? FLAG_VIBRATE : 0));
    audio.setVibrateSetting(getVibrateType(), (isBoolean() ? VIBRATE_SETTING_ON : VIBRATE_SETTING_OFF));
    if (getNumber() <= 0)
    {
      audio.setRingerMode(isBoolean() ? RINGER_MODE_VIBRATE : RINGER_MODE_SILENT);
    }
    else
    {
      audio.setRingerMode(RINGER_MODE_NORMAL);
    }
  }

}
