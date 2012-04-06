package com.mgjg.ProfileManager.attribute.builtin.sound;

import android.content.Context;
import android.media.AudioManager;

import com.mgjg.ProfileManager.R;

public final class AlarmVolumeAttribute extends SoundAttribute
{

  AlarmVolumeAttribute()
  {
    super(0, 0, 0, false, null);
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
  public int getNameResourceId()
  {
    return R.string.newAttribute_AlarmVolume;
  }

  @Override
  public int getToastNameResourceId()
  {
    return R.string.toast_AlarmVolume;
  }

  @Override
  public int getNewResourceId()
  {
    return R.id.newAttribute_AlarmVolume;
  }

  @Override
  public int getTypeId()
  {
    return TYPE_AUDIO_ALARM;
  }

  @Override
  public int getAudioStreamId()
  {
    return AudioManager.STREAM_ALARM;
  }

}
