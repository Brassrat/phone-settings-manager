/**
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
package com.mgjg.ProfileManager.attribute.builtin.sound;

//import static android.media.AudioManager.VIBRATE_SETTING_OFF;
//import static android.media.AudioManager.VIBRATE_SETTING_ON;

import android.content.Context;
import android.media.AudioManager;

public final class NotificationVolumeAttribute extends SoundAttribute
{

  NotificationVolumeAttribute()
  {
    this(null, "");
  }

  public NotificationVolumeAttribute(Context context, String params)
  {
    super(context, params);
  }

  private NotificationVolumeAttribute(long attributeId, long aProfileId, int aVolume, boolean aVibrate, String settings)
  {
    super(attributeId, aProfileId, aVolume, aVibrate, settings);
  }

  @Override
  public NotificationVolumeAttribute createInstance(Context context, long profileId)
  {
    return new NotificationVolumeAttribute(0, profileId, getVolumeForStream(context), isVibrateForStream(context), null);
  }

  @Override
  public NotificationVolumeAttribute createInstance(long attributeId, long profileId, int volume, boolean vibrate, String settings)
  {
    return new NotificationVolumeAttribute(attributeId, profileId, volume, vibrate, settings);
  }

  @Override
  public int getListOrder()
  {
    return ORDER_AUDIO_NOTIFICATION;
  }

  @Override
  public int getSoundAttributeIndex()
  {
    return SOUND_ATTR_NOTIFICATION;
  }

  @Override
  protected void activate(Context context, AudioManager audio)
  {
    super.activate(context, audio);
    //audio.setVibrateSetting(getVibrateType(), (isBoolean() ? VIBRATE_SETTING_ON : VIBRATE_SETTING_OFF));
    // audio.setRingerMode(RINGER_MODE_NORMAL);
    setVibrate(context);
  }

  protected boolean isVibrateForStream(Context context)
  {
//    AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
//    return audio.getVibrateSetting(getAudioStreamId()) != AudioManager.VIBRATE_SETTING_OFF; 
    return SoundAttribute.isVibrateOn(context);
  }

  protected void setVibrate(Context context)
  {
    //audio.setVibrateSetting(getVibrateType(), (isBoolean() ? VIBRATE_SETTING_ON : VIBRATE_SETTING_OFF));
  }
}
