package com.mgjg.ProfileManager.attribute.builtin.sound;

import android.content.Context;
import android.media.AudioManager;

import com.mgjg.ProfileManager.R;

public final class InCallVolumeAttribute extends SoundAttribute
{

  InCallVolumeAttribute()
  {
    super(0, 0, 0, false, null);
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
  public int getNameResourceId()
  {
    return R.string.newAttribute_CallVolume;
  }

  @Override
  public int getToastNameResourceId()
  {
    return R.string.toast_CallVolume;
  }

  @Override
  public int getNewResourceId()
  {
    return R.id.newAttribute_CallVolume;
  }

  @Override
  public int getTypeId()
  {
    return TYPE_AUDIO_VOICE_CALL;
  }

  @Override
  public int getAudioStreamId()
  {
    return AudioManager.STREAM_VOICE_CALL;
  }

}
