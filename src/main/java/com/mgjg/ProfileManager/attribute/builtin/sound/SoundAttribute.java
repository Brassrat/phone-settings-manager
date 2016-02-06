/**
 * Copyright 2011 Jay Goldman
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package com.mgjg.ProfileManager.attribute.builtin.sound;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
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
import com.mgjg.ProfileManager.attribute.ProfileAttribute;
import com.mgjg.ProfileManager.registry.AttributeRegistry;
import com.mgjg.ProfileManager.registry.RegisteredAttribute;
import com.mgjg.ProfileManager.utils.AttributeTableLayout;
import com.mgjg.ProfileManager.utils.Listable;
import com.mgjg.ProfileManager.utils.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.media.AudioManager.FLAG_PLAY_SOUND;
import static android.media.AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE;
import static android.media.AudioManager.FLAG_SHOW_UI;
import static android.media.AudioManager.FLAG_VIBRATE;
import static android.media.AudioManager.RINGER_MODE_NORMAL;
import static android.media.AudioManager.STREAM_NOTIFICATION;
import static android.media.AudioManager.STREAM_RING;

//import static android.media.AudioManager.VIBRATE_SETTING_OFF;
//import static android.media.AudioManager.VIBRATE_SETTING_ON;
//import static android.media.AudioManager.VIBRATE_TYPE_NOTIFICATION;
//import static android.media.AudioManager.VIBRATE_TYPE_RINGER;

@SuppressLint("DefaultLocale")
public abstract class SoundAttribute extends AttributeBase implements Comparable<Listable>
{

  private static final int ID_SEEK_BAR = 1000;

  private static final int ID_MIN_MAX_TEXT = 1003;

  protected final static int SET_VOL_FLAGS = FLAG_PLAY_SOUND | FLAG_REMOVE_SOUND_AND_VIBRATE | FLAG_SHOW_UI;

  private final static SoundAttribute SYSTEM_VOLUME = new SystemVolumeAttribute();
  private final static SoundAttribute RING_VOLUME = new RingerVolumeAttribute();
  private final static SoundAttribute ALARM_VOLUME = new AlarmVolumeAttribute();
  private final static SoundAttribute MUSIC_VOLUME = new MediaVolumeAttribute();
  private final static SoundAttribute NOTIFICATION_VOLUME = new NotificationVolumeAttribute();
  private final static SoundAttribute VOICE_CALL_VOLUME = new InCallVolumeAttribute();

  private static boolean hasShownVolumeCouplingWarning = false;
  private static Boolean isVolumeCoupled = null;

  public static boolean isVibrateOn(Context context)
  {
    String vibOnString = android.provider.Settings.System.getString(context.getContentResolver(), android.provider.Settings.System.VIBRATE_ON);
    return ("on".equalsIgnoreCase(vibOnString));
  }

  public static void setVibrate(int mode)
  {

    switch (mode)
    {
      case AudioManager.RINGER_MODE_NORMAL:
        // TODO
        break;
      case AudioManager.RINGER_MODE_VIBRATE:
        // TODO
        break;
      case AudioManager.RINGER_MODE_SILENT:
        // TODO
        break;
    }
  }

  public static ProfileAttribute[] init(Context context)
  {

    hasShownVolumeCouplingWarning = Util.isBooleanPref(context, R.string.ShownVolumeCouplingWarning, false);
    AttributeEdit.setDefaultType(SYSTEM_VOLUME.getTypeId());
    return new ProfileAttribute[]{SYSTEM_VOLUME, RING_VOLUME, NOTIFICATION_VOLUME, MUSIC_VOLUME, ALARM_VOLUME, VOICE_CALL_VOLUME};
  }

  // created when attribute is viewed
  private SeekBar volumeBar;
  private int maxVolume;
  private TextView viewVibrateText;
  private CheckBox viewVibrateCheckBox;

  private SeekBar createVolume;
  private CheckBox createVibrate;

  protected SoundAttribute(Context context, String params)
  {
    super();
  }

  protected SoundAttribute(long attributeId, long profileId, int volume, boolean vibrate, String settings)
  {
    super(attributeId, profileId, volume, vibrate, settings);
  }

  protected final int getVolumeForStream(Context context)
  {
    AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    return audio.getStreamVolume(getAudioStreamId());
  }

  protected void setVibrate(Context context)
  {
    // audio.setVibrateSetting(getVibrateType(), (isBoolean() ? VIBRATE_SETTING_ON : VIBRATE_SETTING_OFF));
    // to be overridden by specific streams
  }

  protected boolean isVibrateForStream(Context context)
  {
    return false;
  }

  // protected final void doVibrate(Context context)
  // {
  // Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
  // // Vibrate for 500 milliseconds
  // v.vibrate(500);
  // }

  /**
   * apply settings to phone via AudioManager, default is to set the volume
   *
   * @param audio
   */
  protected void activate(Context context, AudioManager audio)
  {
    audio.setStreamVolume(getAudioStreamId(), getNumber(), SET_VOL_FLAGS);
  }

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

  /**
   * @param volume the volume to set
   * @return
   */
  @Override
  protected final void onNumberChange()
  {
    changeVolumeBar();
  }

  private void changeVolumeBar()
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

  @Override
  public String activate(Context context)
  {
    activate(context, (AudioManager) context.getSystemService(Context.AUDIO_SERVICE));
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

  private String labelText(Context context)
  {
    return getName(context) + ":";
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
    volumeLabel.setText(labelText(context));
    volumeLabel.setMinWidth(labelMinWidth());
    volumeRow.addView(volumeLabel);

    volumeBar = new SeekBar(context);
    volumeBar.setEnabled(false);
    volumeBar.setFocusable(false);
    volumeBar.setFocusableInTouchMode(false);
    volumeBar.setClickable(false);
    volumeBar.setPadding(2, 2, rightPadding(), 2);
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
      vibrateLabel.setGravity(Gravity.END);
      vibrateLabel.setText(R.string.vibrateLabel);

      TableRow.LayoutParams labelParms = new TableRow.LayoutParams();
      labelParms.span = 1;
      labelParms.gravity = Gravity.END;

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
      vibrateRow.addView(vibrateLabel, labelParms);
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

    Util.putBooleanPref(context, R.string.ShownVolumeCouplingWarning, true);

    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    builder.setMessage("By default, ringer and notification volume are linked such that any changes to " +
        "ringer volume affects notification volume, although not the other way around. " +
        "To unlink them, go to Menu > Settings > Sound & Display > Ringer volume, " +
        "and uncheck \"Use incoming call volume for notifications\".");
    builder.show();

    hasShownVolumeCouplingWarning = true;
  }

  @Override
  public void addUpdatableView(final Context context, final TableLayout soundLayout, final List<AttributeTableLayout> layouts)
  {
    TableRow volMinMaxRow = new TableRow(context);
    soundLayout.addView(volMinMaxRow, new ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT));

    TableRow seekBarRow = new TableRow(context);
    soundLayout.addView(seekBarRow);

    // volMinMaxRow.setLayoutParams(new TableRow.LayoutParams(
    // TableRow.LayoutParams.MATCH_PARENT,
    // TableRow.LayoutParams.WRAP_CONTENT));

    TextView volumeLabelView = new TextView(context);
    volumeLabelView.setPadding(2, 4, 2, 3);
    volumeLabelView.setText(labelText(context));
    volumeLabelView.setMinWidth(labelMinWidth());
    // volumeLabelView.setLayoutParams(new TableRow.LayoutParams(
    // TableRow.LayoutParams.MATCH_PARENT,
    // TableRow.LayoutParams.WRAP_CONTENT));

    TableRow.LayoutParams singleColumnParams = new TableRow.LayoutParams(
        TableRow.LayoutParams.MATCH_PARENT,
        TableRow.LayoutParams.WRAP_CONTENT);
    singleColumnParams.span = 1;
    volMinMaxRow.addView(volumeLabelView, singleColumnParams);

    TextView count = new TextView(context);
    count.setId(ID_COUNT_TEXT);
    count.setPadding(2, 4, 2, 3);
    count.setText(R.string.tbd);
    volMinMaxRow.addView(count, singleColumnParams);

    final AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    maxVolume = audio.getStreamMaxVolume(getAudioStreamId());
    TextView minMax = new TextView(context);
    minMax.setId(ID_MIN_MAX_TEXT);
    minMax.setText("MIN/" + maxVolume);
    minMax.setPadding(2, 4, rightPadding(), 3);
    TableRow.LayoutParams minMaxParms = new TableRow.LayoutParams();
    minMaxParms.span = 1;
    minMaxParms.gravity = Gravity.END;
    volMinMaxRow.addView(minMax, minMaxParms);

    SeekBar seekBar = new SeekBar(context);
    seekBar.setId(ID_SEEK_BAR); // BAR_IDS.get(type));
    seekBar.setEnabled(true);
    seekBar.setFocusable(true);
    seekBar.setFocusableInTouchMode(true);
    seekBar.setClickable(true);
    seekBar.setPadding(10, 4, rightPadding(), 3);
    seekBar.setMax(maxVolume);
    seekBar.setProgress(getNumber());

    // int h = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, context.getResources().getDisplayMetrics());
    // int w = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, context.getResources().getDisplayMetrics());
    //
    // TableRow.LayoutParams seekBarParams = new TableRow.LayoutParams(w, h);
    TableRow.LayoutParams seekBarParams = new TableRow.LayoutParams();
    seekBarParams.span = 3;
    seekBarRow.addView(seekBar, seekBarParams);

    seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
    {

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
      vibLayoutParams.gravity = Gravity.END + Gravity.CENTER_VERTICAL;
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
      vibrateCheckBox.setPadding(2, 2, rightPadding(), 2);
      vibrateCheckBox.setGravity(Gravity.END);
      vibrateCheckBox.setChecked(isBoolean());
      vibrateCheckBox.setOnClickListener(new View.OnClickListener()
      {
        @Override
        public void onClick(View v)
        {
          setBoolean(!isBoolean());
          ((CheckBox) v).setChecked(isBoolean());
          setVibrate(context);
        }

      });

      TableRow.LayoutParams vibParms = new TableRow.LayoutParams(
          LayoutParams.WRAP_CONTENT,
          LayoutParams.WRAP_CONTENT);
      vibParms.span = 1;
      vibParms.gravity = Gravity.END;
      vibParms.rightMargin = rightPadding();

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

    SeekBar bar = (SeekBar) layout.findViewById(ID_SEEK_BAR);
    if (null != bar)
    {
      bar.setProgress(volume);
    }

    if (isSupportsBoolean())
    {
      CheckBox vib = (CheckBox) layout.findViewById(ID_CHECKBOX);
      if (null != vib)
      {
        vib.setChecked(isVibrateOn(context));
      }
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
    int ringerMode = audio.getRingerMode();
    // NotificationManager notifService = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    // int notifVibType = audio.getVibrateSetting(VIBRATE_TYPE_NOTIFICATION);
    audio.setRingerMode(RINGER_MODE_NORMAL);

    // choose a value different than the current notification volume
    int tmpRingVol = (ringVol > 2) ? ringVol - 1 : ringMax;
    while (tmpRingVol == notifVol)
    {
      tmpRingVol = (tmpRingVol > 2) ? tmpRingVol - 1 : ringMax;
    }

    audio.setStreamVolume(STREAM_RING, tmpRingVol, 0);

    int ringCheck = audio.getStreamVolume(STREAM_RING);
    int notifCheck = audio.getStreamVolume(STREAM_NOTIFICATION);
    // if they are the same then changing the ring must have changed the notification
    isVolumeCoupled = (notifCheck == ringCheck);

    // put everything back to their previous values
    audio.setRingerMode(ringerMode);
    // audio.setVibrateSetting(VIBRATE_TYPE_RINGER, ringVibType);
    // audio.setVibrateSetting(VIBRATE_TYPE_NOTIFICATION, notifVibType);
    audio.setStreamVolume(STREAM_RING, ringVol, 0);
    audio.setStreamVolume(STREAM_NOTIFICATION, notifVol, 0);

    return isVolumeCoupled;
  }

  protected final static int SOUND_ATTR_SYSTEM = 0;
  protected final static int SOUND_ATTR_RING = 1;
  protected final static int SOUND_ATTR_ALARM = 2;
  protected final static int SOUND_ATTR_NOTIFICATION = 3;
  protected final static int SOUND_ATTR_VOICE_CALL = 4;
  protected final static int SOUND_ATTR_MUSIC = 5;

  protected abstract int getSoundAttributeIndex();

  private static final int title[] = {
      R.string.title_system,
      R.string.title_ringer,
      R.string.title_alarm,
      R.string.title_notif,
      R.string.title_phonecall,
      R.string.title_media};

  public final int getNameResourceId()
  {
    return title[getSoundAttributeIndex()];
  }

  private static final int toast[] = {
      R.string.toast_SystemVolume,
      R.string.toast_RingerVolume,
      R.string.toast_AlarmVolume,
      R.string.toast_NotificationVolume,
      R.string.toast_CallVolume,
      R.string.toast_MediaVolume};

  public final int getToastNameResourceId()
  {
    return toast[getSoundAttributeIndex()];
  }

  protected final static int TYPE_AUDIO_SYSTEM = AttributeRegistry.TYPE_AUDIO + 1;
  protected final static int TYPE_AUDIO_RING = AttributeRegistry.TYPE_AUDIO + 2;
  protected final static int TYPE_AUDIO_ALARM = AttributeRegistry.TYPE_AUDIO + 3;
  protected final static int TYPE_AUDIO_MUSIC = AttributeRegistry.TYPE_AUDIO + 4;
  protected final static int TYPE_AUDIO_NOTIFICATION = AttributeRegistry.TYPE_AUDIO + 5;
  protected final static int TYPE_AUDIO_VOICE_CALL = AttributeRegistry.TYPE_AUDIO + 6;

  private static final int typeId[] = {
      TYPE_AUDIO_SYSTEM,
      TYPE_AUDIO_RING,
      TYPE_AUDIO_ALARM,
      TYPE_AUDIO_NOTIFICATION,
      TYPE_AUDIO_VOICE_CALL,
      TYPE_AUDIO_MUSIC};

  @Override
  public final int getTypeId()
  {
    return typeId[getSoundAttributeIndex()];
  }

  private enum VIBRATE_TYPE
  {
    RINGER(1),
    NOTIFICATION(2),
    ALARM(3);

    private final int value;

    VIBRATE_TYPE(int value)
    {
      this.value = value;
    }
  }

  private static final int streamId[] = {
      AudioManager.STREAM_SYSTEM,
      AudioManager.STREAM_RING,
      AudioManager.STREAM_ALARM,
      AudioManager.STREAM_NOTIFICATION,
      AudioManager.STREAM_VOICE_CALL,
      AudioManager.STREAM_MUSIC};

  public final int getAudioStreamId()
  {
    return streamId[getSoundAttributeIndex()];
  }

  private static final int vibrateType[] = {
      -1,
      VIBRATE_TYPE.RINGER.value,
      VIBRATE_TYPE.ALARM.value,
      VIBRATE_TYPE.NOTIFICATION.value,
      -1,
      -1};

  /**
   * returns AudioManager VIBRATE_TYPE_XXX value appropriate for this Sound Attribute
   *
   * @return
   */
  protected final int getVibrateType()
  {
    return vibrateType[getSoundAttributeIndex()];
  }

  @Override
  public final boolean isSupportsBoolean()
  {
    return (getVibrateType() >= 0);
  }

  protected final static int ORDER_AUDIO_SYSTEM = 1;
  protected final static int ORDER_AUDIO_RING = 2;
  protected final static int ORDER_AUDIO_ALARM = 3;
  protected final static int ORDER_AUDIO_NOTIFICATION = 4;
  protected final static int ORDER_AUDIO_VOICE_CALL = 20;
  protected final static int ORDER_AUDIO_MUSIC = 21;

  @Override
  public int compareTo(Listable another)
  {
    int thisOrder = getListOrder();
    int othOrder = another.getListOrder();
    // we know that thisOrder and othOrder are small integers so can just subtract to fulfill compareTo contract
    return thisOrder - othOrder;
  }

  private final static String soundName[] = {"System", "Ring", "Alarm", "Notification", "Call", "Media"};
  private static final int soundIndexes[] = {SOUND_ATTR_SYSTEM, SOUND_ATTR_RING, SOUND_ATTR_ALARM, SOUND_ATTR_NOTIFICATION, SOUND_ATTR_VOICE_CALL, SOUND_ATTR_MUSIC};
  private final static int soundOrder[] = {ORDER_AUDIO_SYSTEM, ORDER_AUDIO_RING, ORDER_AUDIO_ALARM, ORDER_AUDIO_NOTIFICATION, ORDER_AUDIO_VOICE_CALL, ORDER_AUDIO_MUSIC};
  private final static String soundClass[] = {"System", "Ringer", "Alarm", "Notification", "InCall", "Media"};

  @SuppressLint("DefaultLocale")
  private static String makeRegistryJSON(int typeId, String name, int order)
  {
    Locale locale = Locale.getDefault();
    return String.format(locale, "{ \"id\" : \"%1$d\", \"name\" : \"%2$s\", \"order\" : \"%3$d\"}", typeId, name, order);
  }

  public static List<RegisteredAttribute> addRegistryEntries(SQLiteDatabase db)
  {
    List<RegisteredAttribute> ras = new ArrayList<>();
    for (int xx : soundIndexes)
    {
      String params = makeRegistryJSON(typeId[xx], soundName[xx], soundOrder[xx]);
      ras.add(new RegisteredAttribute(0, soundName[xx], typeId[xx],
          true, "com.mgjg.ProfileManager.attribute.builtin.sound." + soundClass[xx] + "VolumeAttribute", params, soundOrder[xx]));
    }

    return ras;
  }
}
