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

import android.content.Context;

public final class InCallVolumeAttribute extends SoundAttribute
{

  InCallVolumeAttribute()
  {
    this(null, "");
  }

  public InCallVolumeAttribute(Context context, String params)
  {
    super(context, params);
  }

  private InCallVolumeAttribute(long attributeId, long aProfileId, int aVolume, boolean aVibrate, String settings)
  {
    super(attributeId, aProfileId, aVolume, aVibrate, settings);
  }

  @Override
  public InCallVolumeAttribute createInstance(Context context, long profileId)
  {
    return new InCallVolumeAttribute(0, profileId, getVolumeForStream(context), false, null);
  }

  @Override
  public InCallVolumeAttribute createInstance(long attributeId, long profileId, int volume, boolean vibrate, String settings)
  {
    return new InCallVolumeAttribute(attributeId, profileId, volume, vibrate, settings);
  }

  @Override
  public int getListOrder()
  {
    return ORDER_AUDIO_VOICE_CALL;
  }

  @Override
  public int getSoundAttributeIndex()
  {
    return SOUND_ATTR_VOICE_CALL;
  }

}
