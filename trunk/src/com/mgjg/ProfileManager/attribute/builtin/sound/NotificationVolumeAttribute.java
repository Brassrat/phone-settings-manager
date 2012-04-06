package com.mgjg.ProfileManager.attribute.builtin.sound;

import static android.media.AudioManager.VIBRATE_SETTING_OFF;
import static android.media.AudioManager.VIBRATE_SETTING_ON;
import android.content.Context;
import android.media.AudioManager;

import com.mgjg.ProfileManager.R;

public final class NotificationVolumeAttribute extends SoundAttribute
{
  NotificationVolumeAttribute()
  {
    super(0, 0, 0, false, null);
  }

  private NotificationVolumeAttribute(long attributeId, long aProfileId, int aVolume, boolean aVibrate, String settings)
  {
    super(attributeId, aProfileId, aVolume, aVibrate, settings);
  }

  @Override
  public NotificationVolumeAttribute createInstance(Context context, long profileId)
  {
    return new NotificationVolumeAttribute(0, profileId, getVolumeForStream(context), isVibrateForStream(context), null);
  }

  @Override
  public NotificationVolumeAttribute createInstance(long attributeId, long profileId, int volume, boolean vibrate, String settings)
  {
    return new NotificationVolumeAttribute(attributeId, profileId, volume, vibrate, settings);
  }

  @Override
  public int getNameResourceId()
  {
    return R.string.newAttribute_NotificationVolume;
  }

  @Override
  public int getToastNameResourceId()
  {
    return R.string.toast_NotificationVolume;
  }

  @Override
  public int getNewResourceId()
  {
    return R.id.newAttribute_NotificationVolume;
  }

  @Override
  public int getTypeId()
  {
    return TYPE_AUDIO_NOTIFICATION;
  }

  @Override
  public int getAudioStreamId()
  {
    return AudioManager.STREAM_NOTIFICATION;
  }

  @Override
  protected int getVibrateType()
  {
    return AudioManager.VIBRATE_TYPE_NOTIFICATION;
  }
  
  @Override
  protected void activate(AudioManager audio)
  {
    audio.setStreamVolume(getAudioStreamId(), getNumber(), SET_VOL_FLAGS);
    audio.setVibrateSetting(getVibrateType(), (isBoolean() ? VIBRATE_SETTING_ON : VIBRATE_SETTING_OFF));
    // audio.setRingerMode(RINGER_MODE_NORMAL);
  }

  @Override
  public boolean isSupportsBoolean()
  {
    return true;
  }

}
