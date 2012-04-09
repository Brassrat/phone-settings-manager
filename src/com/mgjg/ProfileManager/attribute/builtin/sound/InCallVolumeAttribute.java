package com.mgjg.ProfileManager.attribute.builtin.sound;

import android.content.Context;

public final class InCallVolumeAttribute extends SoundAttribute
{

  InCallVolumeAttribute()
  {
    super();
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
