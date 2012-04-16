package com.mgjg.ProfileManager.attribute.builtin.sound;

import android.content.Context;

public final class SystemVolumeAttribute extends SoundAttribute
{
  
  SystemVolumeAttribute()
  {
    this(null, "");
  }

  public SystemVolumeAttribute(Context context,String params)
  {
    super(context, params);
  }
  
  private SystemVolumeAttribute(long attributeId, long aProfileId, int aVolume, boolean aVibrate, String settings)
  {
    super(attributeId, aProfileId, aVolume, aVibrate, settings);
  }

  @Override
  public SystemVolumeAttribute createInstance(Context context, long profileId)
  {
    return new SystemVolumeAttribute(0, profileId, getVolumeForStream(context), false, null);
  }

  @Override
  public SystemVolumeAttribute createInstance(long attributeId, long profileId, int volume, boolean vibrate, String settings)
  {
    return new SystemVolumeAttribute(attributeId, profileId, volume, vibrate, settings);
  }

  @Override
  public int getListOrder()
  {
    return ORDER_AUDIO_SYSTEM;
  }

  @Override
  public int getSoundAttributeIndex()
  {
    return SOUND_ATTR_SYSTEM;
  }

}
