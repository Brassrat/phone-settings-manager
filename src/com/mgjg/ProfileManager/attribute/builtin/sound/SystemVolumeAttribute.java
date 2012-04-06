package com.mgjg.ProfileManager.attribute.builtin.sound;

import android.content.Context;
import android.media.AudioManager;

import com.mgjg.ProfileManager.R;

public final class SystemVolumeAttribute extends SoundAttribute
{

  SystemVolumeAttribute()
  {
    this(0, 0, 0, false, null);
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
  public int getNameResourceId()
  {
    return R.string.newAttribute_SystemVolume;
  }

  @Override
  public int getToastNameResourceId()
  {
    return R.string.toast_SystemVolume;
  }

  @Override
  public int getNewResourceId()
  {
    return R.id.newAttribute_SystemVolume;
  }

  @Override
  public int getTypeId()
  {
    return TYPE_AUDIO_SYSTEM;
  }

  @Override
  public int getAudioStreamId()
  {
    return AudioManager.STREAM_SYSTEM;
  }

}
