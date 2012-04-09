package com.mgjg.ProfileManager.attribute.builtin.sound;

import android.content.Context;

public final class MediaVolumeAttribute extends SoundAttribute
{

  MediaVolumeAttribute()
  {
    super();
  }

  private MediaVolumeAttribute(long attributeId, long aProfileId, int aVolume, boolean aVibrate, String settings)
  {
    super(attributeId, aProfileId, aVolume, aVibrate, settings);
  }

  @Override
  public MediaVolumeAttribute createInstance(Context context, long profileId)
  {
    return new MediaVolumeAttribute(0, profileId, getVolumeForStream(context), false, null);
  }

  @Override
  public MediaVolumeAttribute createInstance(long attributeId, long profileId, int volume, boolean vibrate, String settings)
  {
    return new MediaVolumeAttribute(attributeId, profileId, volume, vibrate, settings);
  }

  @Override
  public int getListOrder()
  {
    return ORDER_AUDIO_MUSIC;
  }
  
  
  @Override
  public int getSoundAttributeIndex()
  {
    return SOUND_ATTR_MUSIC;
  }
}
