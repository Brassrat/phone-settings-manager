package com.mgjg.ProfileManager.attribute.builtin.sound;

import static android.media.AudioManager.FLAG_VIBRATE;
import static android.media.AudioManager.RINGER_MODE_NORMAL;
import static android.media.AudioManager.RINGER_MODE_SILENT;
import static android.media.AudioManager.RINGER_MODE_VIBRATE;
import static android.media.AudioManager.VIBRATE_SETTING_OFF;
import static android.media.AudioManager.VIBRATE_SETTING_ON;
import android.content.Context;
import android.media.AudioManager;

public final class RingerVolumeAttribute extends SoundAttribute
{

  RingerVolumeAttribute()
  {
    super();
  }

  private RingerVolumeAttribute(long attributeId, long aProfileId, int aVolume, boolean aVibrate, String settings)
  {
    super(attributeId, aProfileId, aVolume, aVibrate, settings);
  }

  @Override
  public RingerVolumeAttribute createInstance(Context context, long profileId)
  {
    return new RingerVolumeAttribute(0, profileId, getVolumeForStream(context), isVibrateForStream(context), null);
  }

  @Override
  public RingerVolumeAttribute createInstance(long attributeId, long profileId, int volume, boolean vibrate, String settings)
  {
    return new RingerVolumeAttribute(attributeId, profileId, volume, vibrate, settings);
  }

  @Override
  public int getListOrder()
  {
    return ORDER_AUDIO_RING;
  }

  @Override
  public int getSoundAttributeIndex()
  {
    return SOUND_ATTR_RING;
  }

  @Override
  protected void activate(AudioManager audio)
  {
    audio.setStreamVolume(getAudioStreamId(), getNumber(), SET_VOL_FLAGS + (isBoolean() ? FLAG_VIBRATE : 0));
    audio.setVibrateSetting(getVibrateType(), (isBoolean() ? VIBRATE_SETTING_ON : VIBRATE_SETTING_OFF));
    if (getNumber() <= 0)
    {
      audio.setRingerMode(isBoolean() ? RINGER_MODE_VIBRATE : RINGER_MODE_SILENT);
    }
    else
    {
      audio.setRingerMode(RINGER_MODE_NORMAL);
    }
  }

}
