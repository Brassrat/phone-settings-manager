/**
 * Copyright 2009 Daniel Roozen
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
package com.mgjg.ProfileManager.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

public class Util
{

  public static final String PREFS_NAME = "ProfileManagerPrefs";
  public static final String LOG_TAG = "com.mgjg.ProfileManager";

  /**
   * Queries system settings for the system clock format
   *
   * @return boolean
   */
  public static boolean is24HourClock(ContentResolver contentResolver)
  {
    boolean clock24hour;

    try
    {
      clock24hour = (Settings.System.getInt(contentResolver, Settings.System.TIME_12_24) == 24);
    }
    catch (SettingNotFoundException e)
    {
      // not set, running in emulator?
      clock24hour = false;
    }

    return clock24hour;
  }

  /**
   * get a Boolean out of SharedPreferences
   *
   * @param context
   * @param resId
   * @param def
   * @return
   */
  public static boolean isBooleanPref(Context context, int resId, boolean def)
  {
    return context.getSharedPreferences(PREFS_NAME,
        Context.MODE_PRIVATE).getBoolean(context.getString(resId), def);
  }

  /**
   * put a Boolean into SharedPreferences
   *
   * @param context
   * @param resId
   * @param value
   */
  public static void putBooleanPref(Context context, int resId, boolean value)
  {
    SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = prefs.edit();
    editor.putBoolean(context.getString(resId), value);
    editor.apply();
  }

  /**
   * get a Boolean out of SharedPreferences
   *
   * @param context
   * @param resId
   * @param def
   * @return
   */
  public static int getIntPref(Context context, int resId, int def)
  {
    return context.getSharedPreferences(PREFS_NAME,
        Context.MODE_PRIVATE).getInt(context.getString(resId), def);
  }

  /**
   * put a Boolean into SharedPreferences
   *
   * @param context
   * @param resId
   * @param value
   */
  public static void putIntPref(Context context, int resId, int value)
  {
    SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = prefs.edit();
    editor.putInt(context.getString(resId), value);
    editor.apply();
  }

}
