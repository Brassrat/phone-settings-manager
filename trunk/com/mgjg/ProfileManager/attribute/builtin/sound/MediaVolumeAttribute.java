package com.mgjg.ProfileManager.attribute.builtin.sound;

import android.content.Context;
import android.media.AudioManager;

import com.mgjg.ProfileManager.R;

public final class MediaVolumeAttribute extends SoundAttribute
{

  MediaVolumeAttribute()
  {
    super(0, 0, 0, false, null);
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
  public int getNameResourceId()
  {
    return R.string.newAttribute_MediaVolume;
  }

  @Override
  public int getToastNameResourceId()
  {
    return R.string.toast_MediaVolume;
  }

  @Override
  public int getNewResourceId()
  {
    return R.id.newAttribute_MediaVolume;
  }

  @Override
  public int getTypeId()
  {
    return TYPE_AUDIO_MUSIC;
  }

  @Override
  public int getAudioStreamId()
  {
    return AudioManager.STREAM_MUSIC;
  }
}
