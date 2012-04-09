package com.mgjg.ProfileManager.attribute.builtin.sound;

import android.content.Context;

public final class AlarmVolumeAttribute extends SoundAttribute
{

  AlarmVolumeAttribute()
  {
    super();
  }

  private AlarmVolumeAttribute(long aAttributeId, long aProfileId, int aVolume, boolean aVibrate, String settings)
  {
    super(aAttributeId, aProfileId, aVolume, aVibrate, settings);
  }

  @Override
  public AlarmVolumeAttribute createInstance(Context context, long profileId)
  {
    return new AlarmVolumeAttribute(0, profileId, getVolumeForStream(context), false, null);
  }

  @Override
  public AlarmVolumeAttribute createInstance(long attributeId, long profileId, int volume, boolean vibrate, String settings)
  {
    return new AlarmVolumeAttribute(attributeId, profileId, volume, vibrate, settings);
  }

  @Override
  public int getListOrder()
  {
    return ORDER_AUDIO_ALARM;
  }
  
  @Override
  public int getSoundAttributeIndex()
  {
    return SOUND_ATTR_ALARM;
  }
  
}
