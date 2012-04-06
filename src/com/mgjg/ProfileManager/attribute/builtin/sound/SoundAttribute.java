/**
 * Copyright 2011 Jay Goldman
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed 
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language 
 * governing permissions and limitations under the License. 
 */
package com.mgjg.ProfileManager.attribute.builtin.sound;

import static android.media.AudioManager.FLAG_PLAY_SOUND;
import static android.media.AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE;
import static android.media.AudioManager.FLAG_SHOW_UI;
import static android.media.AudioManager.FLAG_VIBRATE;
import static android.media.AudioManager.STREAM_NOTIFICATION;
import static android.media.AudioManager.STREAM_RING;
import static android.media.AudioManager.VIBRATE_SETTING_OFF;
import static android.media.AudioManager.VIBRATE_SETTING_ON;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.mgjg.ProfileManager.R;
import com.mgjg.ProfileManager.attribute.AttributeBase;
import com.mgjg.ProfileManager.attribute.AttributeEdit;
import com.mgjg.ProfileManager.attribute.AttributeView;
import com.mgjg.ProfileManager.registry.AttributeRegistry;
import com.mgjg.ProfileManager.utils.AttributeTableLayout;
import com.mgjg.ProfileManager.utils.Util;

public abstract class SoundAttribute extends AttributeBase
{

  private static final int ID_SEEK_BAR = 1000;

  private static final int ID_MIN_MAX_TEXT = 1003;

  protected final static int SET_VOL_FLAGS = FLAG_PLAY_SOUND | FLAG_REMOVE_SOUND_AND_VIBRATE | FLAG_SHOW_UI;

  protected final static int TYPE_AUDIO_SYSTEM = AttributeRegistry.TYPE_AUDIO + 1;
  protected final static int TYPE_AUDIO_RING = AttributeRegistry.TYPE_AUDIO + 2;
  protected final static int TYPE_AUDIO_ALARM = AttributeRegistry.TYPE_AUDIO + 3;
  protected final static int TYPE_AUDIO_MUSIC = AttributeRegistry.TYPE_AUDIO + 4;
  protected final static int TYPE_AUDIO_NOTIFICATION = AttributeRegistry.TYPE_AUDIO + 5;
  protected final static int TYPE_AUDIO_VOICE_CALL = AttributeRegistry.TYPE_AUDIO + 6;

  private final static SoundAttribute SYSTEM_VOLUME = new SystemVolumeAttribute();
  private final static SoundAttribute RING_VOLUME = new RingerVolumeAttribute();
  private final static SoundAttribute ALARM_VOLUME = new AlarmVolumeAttribute();
  private final static SoundAttribute MUSIC_VOLUME = new MediaVolumeAttribute();
  private final static SoundAttribute NOTIFICATION_VOLUME = new NotificationVolumeAttribute();
  private final static SoundAttribute VOICE_CALL_VOLUME = new InCallVolumeAttribute();

  private static boolean hasShownVolumeCouplingWarning = false;
  private static Boolean isVolumeCoupled = null;

  public static AttributeBase[] init(Context context)
  {

    hasShownVolumeCouplingWarning = Util.isBooleanPref(context, R.string.ShownVolumeCouplingWarning, false);
    AttributeEdit.setDefaultType(SYSTEM_VOLUME.getTypeId());
    return new AttributeBase[] { SYSTEM_VOLUME, RING_VOLUME, NOTIFICATION_VOLUME, MUSIC_VOLUME, ALARM_VOLUME, VOICE_CALL_VOLUME };
  }

  // created when attribute is viewed
  private SeekBar volumeBar;
  private int maxVolume;
  private TextView viewVibrateText;
  private CheckBox viewVibrateCheckBox;

  private SeekBar createVolume;
  private CheckBox createVibrate;

  // public SoundAttribute(String params)
  // {
  // // TODO params is JSON string...
  // // TODO for now just TYPE,Volume,Vibrate as integers
  // }

  protected SoundAttribute(long attributeId, long profileId, int volume, boolean vibrate, String settings)
  {
    super(attributeId, profileId, volume, vibrate, settings);
  }

  protected final int getVolumeForStream(Context context)
  {
    AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    return audio.getStreamVolume(getAudioStreamId());
  }

  protected final boolean isVibrateForStream(Context context)
  {
    AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    return audio.getVibrateSetting(getAudioStreamId()) != AudioManager.VIBRATE_SETTING_OFF;
  }

  /**
   * apply settings to phone via AudioManager, default is to set the volume
   * 
   * @param audio
   */
  protected void activate(AudioManager audio)
  {
    audio.setStreamVolume(getAudioStreamId(), getNumber(), SET_VOL_FLAGS);
  }

  public abstract int getNameResourceId();

  public abstract int getToastNameResourceId();

  public abstract int getNewResourceId();

  @Override
  public final boolean isSupportsNumber()
  {
    return true;
  }

  @Override
  public String getToast(Context context)
  {
    if (isBoolean())
    {
      if (getNumber() <= 0)
      {
        return context.getText(getToastNameResourceId()) + ":" + "v";
      }
      else
      {
        return context.getText(getToastNameResourceId()) + ":" + getNumber() + "v";
      }
    }
    return context.getText(getToastNameResourceId()) + ":" + getNumber();
  }

  @Override
  public abstract int getTypeId();

  protected abstract int getAudioStreamId();

  /**
   * @param volume
   *          the volume to set
   * @return
   */
  @Override
  protected final void onNumberChange()
  {
    changeVolumeBar();
  }

  private final void changeVolumeBar()
  {
    if ((null != volumeBar) && (volumeBar.getProgress() != getNumber()))
    {
      volumeBar.setProgress(getNumber());
    }
  }

  @Override
  public final void onBooleanChange()
  {
    if (isSupportsBoolean())
    {
      changeVibrateView();
    }
  }

  protected final void changeVibrateView()
  {
    if ((createVibrate != null) && (createVibrate.isChecked() != isBoolean()))
    {
      createVibrate.setChecked(isBoolean());
      createVibrate.setTextColor(isBoolean() ? Color.GREEN : Color.RED);
    }

    if (null != viewVibrateText)
    {
      String vibText = isBoolean() ? "On" : "Off";
      if (!vibText.equalsIgnoreCase(viewVibrateText.getText().toString()))
      {
        viewVibrateText.setText(vibText);
        viewVibrateText.setTextColor(isBoolean() ? Color.GREEN : Color.RED);
      }
    }

    if (null != viewVibrateCheckBox)
    {
      viewVibrateCheckBox.setChecked(isBoolean());
      viewVibrateCheckBox.setTextColor(isBoolean() ? Color.GREEN : Color.RED);
    }
  }

  /**
   * returns AudioManager VIBRATE_TYPE_XXX value appropriate for this Sound Attribute
   * 
   * @return
   */
  protected int getVibrateType()
  {
    return 0;
  }

  @Override
  public String activate(Context context)
  {
    activate((AudioManager) context.getSystemService(Context.AUDIO_SERVICE));
    return getToast(context);
  }

  /**
   * can have only one attribute for given type (per schedule entry)
   */
  @Override
  public boolean equals(Object o)
  {
    boolean result = false;

    if (o instanceof SoundAttribute)
    {
      SoundAttribute s = (SoundAttribute) o;
      result = this.getTypeId() == s.getTypeId();
    }

    return result;
  }

  @Override
  public int hashCode()
  {
    return getTypeId();
  }

  @Override
  public String getName(Context context)
  {
    return context.getText(getNameResourceId()).toString();
  }

  /**
   * Populate GUI with data from this attribute
   */
  @Override
  public void populateGui(Activity aa)
  {

    /*
     * get handles to the gui
     */
    if (null == createVolume)
    {
      createVolume = (SeekBar) aa.findViewById(R.id.volume);
      AudioManager audio = (AudioManager) aa.getSystemService(Context.AUDIO_SERVICE);
      createVolume.setMax(audio.getStreamMaxVolume(getAudioStreamId()));
    }
    createVolume.setProgress(getNumber());

    if (null == createVibrate)
    {
      TextView vibrateLabel = (TextView) aa.findViewById(R.id.vibrateLabel);
      createVibrate = (CheckBox) aa.findViewById(R.id.vibrateCheckbox);
      vibrateLabel.setVisibility(isSupportsBoolean() ? View.VISIBLE : View.GONE);
      createVibrate.setVisibility(isSupportsBoolean() ? View.VISIBLE : View.GONE);
    }
    createVibrate.setChecked(isBoolean());
  }

  @Override
  public void saveGui()
  {
    if (null != createVolume)
    {
      setNumber(createVolume.getProgress());
    }
    setBoolean(createVibrate);
  }

  @Override
  public boolean isModified()
  {
    if ((null != createVolume) && (null != createVibrate))
    {
      return (createVolume.getProgress() != getNumber() || createVibrate.isChecked() != isBoolean());
    }
    return false;
  }

  @Override
  public void finishCreate()
  {
    createVolume = null;
    createVibrate = null;
  }

  @Override
  public void removeView()
  {
    volumeBar = null;
    viewVibrateText = null;
  }

  @Override
  public void addView(Context context, TableLayout tableLayout)
  {

    AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

    /*
     * volume
     */
    TableRow volumeRow = new TableRow(context);

    TextView volumeLabel = new TextView(context);
    volumeLabel.setPadding(2, 7, 2, 2);
    volumeLabel.setText(getName(context) + ":");
    volumeRow.addView(volumeLabel);

    volumeBar = new SeekBar(context);
    volumeBar.setEnabled(false);
    volumeBar.setFocusable(false);
    volumeBar.setFocusableInTouchMode(false);
    volumeBar.setClickable(false);
    volumeBar.setPadding(2, 2, 7, 2);
    maxVolume = audio.getStreamMaxVolume(getAudioStreamId());
    volumeBar.setMax(maxVolume);
    setNumber(getNumber());

    TableRow.LayoutParams volumeLayoutParams = new TableRow.LayoutParams();
    volumeLayoutParams.span = 2;
    volumeRow.addView(volumeBar, volumeLayoutParams);

    tableLayout.addView(volumeRow);

    /*
     * vibrate
     */

    /*
     * display the vibrate setting only for certain streams
     */
    if (isSupportsBoolean())
    {

      TextView vibrateLabel = new TextView(context);
      vibrateLabel.setPadding(2, 2, 2, 2);
      vibrateLabel.setText(R.string.vibrateLabel);

      viewVibrateCheckBox = new CheckBox(context);
      viewVibrateCheckBox.setPadding(2, 2, 2, 2);
      viewVibrateCheckBox.setChecked(isBoolean());
      viewVibrateCheckBox.setEnabled(false);
      viewVibrateCheckBox.setFocusable(false);
      viewVibrateCheckBox.setFocusableInTouchMode(false);
      viewVibrateCheckBox.setClickable(false);
      TableRow.LayoutParams vibParms = new TableRow.LayoutParams();
      vibParms.span = 1;
      vibParms.gravity = Gravity.CENTER_HORIZONTAL;

      TableRow vibrateRow = new TableRow(context);
      vibrateRow.addView(vibrateLabel);
      vibrateRow.addView(viewVibrateCheckBox, vibParms);
      tableLayout.addView(vibrateRow, AttributeView.paramsFillWrap);
    }
    else
    {
      viewVibrateText = null;
      viewVibrateCheckBox = null;
    }
  }

  public void showVolumeCouplingWarning(Context context)
  {

    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    builder.setMessage("By default, ringer and notification volume are linked such that any changes to " +
        "ringer volume affects notification volume, although not the other way around. " +
        "To unlink them, go to Menu > Settings > Sound & Display > Ringer volume, " +
        "and uncheck \"Use incoming call volume for notifications\".");
    builder.show();

    Util.putBooleanPref(context, R.string.ShownVolumeCouplingWarning, true);
    hasShownVolumeCouplingWarning = true;

  }

  @Override
  public void addUpdatableView(final Context context, final TableLayout soundLayout, final List<AttributeTableLayout> layouts)
  {
    TableRow volMinMaxRow = new TableRow(context);
    soundLayout.addView(volMinMaxRow, new ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.FILL_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT));

    TableRow seekBarRow = new TableRow(context);
    soundLayout.addView(seekBarRow);

    // volMinMaxRow.setLayoutParams(new TableRow.LayoutParams(
    // TableRow.LayoutParams.FILL_PARENT,
    // TableRow.LayoutParams.WRAP_CONTENT));

    TextView volumeLabelView = new TextView(context);
    volumeLabelView.setPadding(2, 4, 2, 3);
    volumeLabelView.setText(getName(context) + ":");
    int minWidth = 8*20;
    volumeLabelView.setMinWidth(minWidth);
    // volumeLabelView.setLayoutParams(new TableRow.LayoutParams(
    // TableRow.LayoutParams.FILL_PARENT,
    // TableRow.LayoutParams.WRAP_CONTENT));

    TableRow.LayoutParams singleColumnParams = new TableRow.LayoutParams(
        TableRow.LayoutParams.FILL_PARENT,
        TableRow.LayoutParams.WRAP_CONTENT);
    singleColumnParams.span = 1;
    volMinMaxRow.addView(volumeLabelView, singleColumnParams);

    TextView count = new TextView(context);
    count.setId(ID_COUNT_TEXT);
    count.setPadding(2, 4, 2, 3);
    count.setText("TBD");
    volMinMaxRow.addView(count, singleColumnParams);
    
    final AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    maxVolume = audio.getStreamMaxVolume(getAudioStreamId());
    TextView minMax = new TextView(context);
    minMax.setId(ID_MIN_MAX_TEXT);
    minMax.setText("MIN/" + maxVolume);
    minMax.setPadding(2, 4, 2, 3);
    TableRow.LayoutParams minMaxParms = new TableRow.LayoutParams();
    minMaxParms.span = 1;
    minMaxParms.gravity = Gravity.RIGHT;
    volMinMaxRow.addView(minMax, minMaxParms);

    SeekBar seekBar = new SeekBar(context);
    seekBar.setId(ID_SEEK_BAR); // BAR_IDS.get(type));
    seekBar.setEnabled(true);
    seekBar.setFocusable(true);
    seekBar.setFocusableInTouchMode(true);
    seekBar.setClickable(true);
    seekBar.setPadding(10, 4, 10, 3);
    seekBar.setMax(maxVolume);
    seekBar.setProgress(getNumber());

    // int h = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, context.getResources().getDisplayMetrics());
    // int w = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, context.getResources().getDisplayMetrics());
    //
    // TableRow.LayoutParams seekBarParams = new TableRow.LayoutParams(w, h);
    TableRow.LayoutParams seekBarParams = new TableRow.LayoutParams();
    seekBarParams.span = 3;
    seekBarRow.addView(seekBar, seekBarParams);

    seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
      {
        TextView minMax = (TextView) soundLayout.findViewById(ID_MIN_MAX_TEXT);
        if (null != minMax)
        {
          CharSequence lbl = seekBar.getProgress() + "/" + maxVolume;
          minMax.setText(lbl);
        }
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar)
      {
        // ignore
      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar)
      {
        setNumber(seekBar.getProgress());

        boolean coupled = isRingerNotifVolumeCoupled(context);
        if (!hasShownVolumeCouplingWarning && coupled)
        {
          showVolumeCouplingWarning(context);
        }

        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audio.setStreamVolume(getAudioStreamId(), getNumber(), SET_VOL_FLAGS + (isBoolean() ? FLAG_VIBRATE : 0));

        updateView(context, soundLayout);
        if (coupled || (getAudioStreamId() == AudioManager.STREAM_SYSTEM))
        {
          updateViews(context, layouts);
        }
      }

    });

    /*
     * vibrate
     */
    /*
     * display the vibrate setting only for certain streams
     */
    if (isSupportsBoolean())
    {

      TableRow vibrateRow = new TableRow(context);

      TextView vibrateLabel = new TextView(context);
      vibrateLabel.setPadding(10, 2, 2, 2);
      vibrateLabel.setText(R.string.vibrateLabel);
      vibrateLabel.setGravity(Gravity.CENTER_VERTICAL);

      TableRow.LayoutParams vibLayoutParams = new TableRow.LayoutParams(
          LayoutParams.WRAP_CONTENT,
          LayoutParams.WRAP_CONTENT);
      vibLayoutParams.span = 2;
      vibLayoutParams.gravity = Gravity.RIGHT;
      vibrateRow.addView(vibrateLabel, vibLayoutParams);

      // <TableRow>
      // <TextView android:id="@+id/vibrateLabel"
      // android:layout_width="wrap_content" android:layout_height="wrap_content"
      // android:layout_marginRight="10dip" android:text="@string/vibrateLabel" />
      // <CheckBox android:id="@+id/vibrateCheckbox"
      // android:layout_width="wrap_content" android:layout_height="wrap_content" />
      // </TableRow>
      CheckBox vibrateCheckBox = new CheckBox(context);
      vibrateCheckBox.setId(ID_CHECKBOX);
      // vibrateCheckBox.setPadding(2, 2, 2, 2);
      vibrateCheckBox.setGravity(Gravity.RIGHT);
      vibrateCheckBox.setChecked(isBoolean());
      vibrateCheckBox.setOnClickListener(new View.OnClickListener()
      {
        @Override
        public void onClick(View v)
        {
          setBoolean(!isBoolean());
          ((CheckBox) v).setChecked(isBoolean());
          final AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
          audio.setVibrateSetting(getVibrateType(), (isBoolean() ? VIBRATE_SETTING_ON : VIBRATE_SETTING_OFF));
        }

      });

      TableRow.LayoutParams vibParms = new TableRow.LayoutParams(
          LayoutParams.WRAP_CONTENT,
          LayoutParams.WRAP_CONTENT);
      vibParms.span = 1;
      vibParms.gravity = Gravity.RIGHT;

      vibrateRow.addView(vibrateCheckBox, vibParms);

      soundLayout.addView(vibrateRow, AttributeView.paramsFillWrap);
    }


  }

  @Override
  public void updateView(Context context, TableLayout layout)
  {
    final AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

    int volume = audio.getStreamVolume(getAudioStreamId());

    TextView minMax = (TextView) layout.findViewById(ID_MIN_MAX_TEXT);
    if (null != minMax)
    {
      CharSequence lbl = volume + "/" + maxVolume;
      minMax.setText(lbl);
    }

    if (isSupportsBoolean())
    {
      CheckBox vib = (CheckBox) layout.findViewById(ID_CHECKBOX);
      if (null != vib)
      {
        vib.setChecked(audio.getVibrateSetting(getAudioStreamId()) != AudioManager.VIBRATE_SETTING_OFF);
      }
    }

    SeekBar bar = (SeekBar) layout.findViewById(ID_SEEK_BAR);
    if (null != bar)
    {
      bar.setProgress(volume);
    }
  }

  /**
   * temporarily change the ringer volume and check if the notif volume changed with it
   * 
   * @return result
   */
  private boolean isRingerNotifVolumeCoupled(Context context)
  {

    if (isVolumeCoupled != null)
    {
      return isVolumeCoupled;
    }

    int stream = getAudioStreamId();
    if ((stream != STREAM_RING) && (stream != STREAM_NOTIFICATION))
    {
      isVolumeCoupled = false;
      return false; // this stream is not coupled to any other stream
    }

    final AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    int ringMax = audio.getStreamMaxVolume(STREAM_RING);

    // get current volumes
    int ringVol = audio.getStreamVolume(STREAM_RING);
    int notifVol = audio.getStreamVolume(STREAM_NOTIFICATION);

    // choose a value different than the current notification volume
    int tmpRingVol = (ringVol > 1) ? ringVol - 1 : ringMax;
    while (tmpRingVol == notifVol)
    {
      tmpRingVol = (tmpRingVol > 1) ? tmpRingVol - 1 : ringMax;
    }

    audio.setStreamVolume(STREAM_RING, tmpRingVol, 0);

    int ringCheck = audio.getStreamVolume(STREAM_RING);
    int notifCheck = audio.getStreamVolume(STREAM_NOTIFICATION);
    // if they are the same then changing the ring must have changed the notification
    boolean isVolumeCoupled = (notifCheck == ringCheck);

    // put everything back to their previous values
    audio.setStreamVolume(STREAM_RING, ringVol, 0);
    audio.setStreamVolume(STREAM_NOTIFICATION, notifVol, 0);

    return isVolumeCoupled;
  }

  @Override
  public String getNew(Context context)
  {
    return context.getText(getNameResourceId()).toString();
  }
}
