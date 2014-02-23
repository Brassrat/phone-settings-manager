/**
 * Copyright 2009 Mike Partridge
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
package com.mgjg.ProfileManager.provider;

import static com.mgjg.ProfileManager.provider.ProfileHelper.COLUMN_PROFILE_ACTIVE;
import static com.mgjg.ProfileManager.provider.ProfileHelper.COLUMN_PROFILE_ID;
import static com.mgjg.ProfileManager.provider.ProfileHelper.TABLE_PROFILE;
import static com.mgjg.ProfileManager.provider.ScheduleHelper.COLUMN_SCHEDULE_ACTIVE;
import static com.mgjg.ProfileManager.provider.ScheduleHelper.COLUMN_SCHEDULE_DAY0;
import static com.mgjg.ProfileManager.provider.ScheduleHelper.COLUMN_SCHEDULE_DAY1;
import static com.mgjg.ProfileManager.provider.ScheduleHelper.COLUMN_SCHEDULE_DAY2;
import static com.mgjg.ProfileManager.provider.ScheduleHelper.COLUMN_SCHEDULE_DAY3;
import static com.mgjg.ProfileManager.provider.ScheduleHelper.COLUMN_SCHEDULE_DAY4;
import static com.mgjg.ProfileManager.provider.ScheduleHelper.COLUMN_SCHEDULE_DAY5;
import static com.mgjg.ProfileManager.provider.ScheduleHelper.COLUMN_SCHEDULE_DAY6;
import static com.mgjg.ProfileManager.provider.ScheduleHelper.COLUMN_SCHEDULE_ID;
import static com.mgjg.ProfileManager.provider.ScheduleHelper.COLUMN_SCHEDULE_PROFILE_ID;
import static com.mgjg.ProfileManager.provider.ScheduleHelper.COLUMN_SCHEDULE_START_HOUR;
import static com.mgjg.ProfileManager.provider.ScheduleHelper.COLUMN_SCHEDULE_START_MINUTE;
import static com.mgjg.ProfileManager.provider.ScheduleHelper.FILTER_SCHEDULE_ID;
import static com.mgjg.ProfileManager.provider.ScheduleHelper.FILTER_SCHEDULE_PROFILE_ID;
import static com.mgjg.ProfileManager.provider.ScheduleHelper.FILTER_SCHEDULE_PROFILE_ID_START_TIME;
import static com.mgjg.ProfileManager.provider.ScheduleHelper.TABLE_SCHEDULE;

import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.mgjg.ProfileManager.schedule.ScheduleEntry;

/**
 * Abstracts access to profile data in SQLite db
 * 
 * @author Mike Partridge / Jay Goldman
 */
public class ScheduleProvider extends ProfileManagerProvider<ScheduleEntry>
{

  public static final String AUTHORITY = "com.mgjg.ProfileManager.provider.ScheduleProvider";

  public static final String TABLE_SCHEDULE_CREATE = "create table "
      + TABLE_SCHEDULE + " ("
      + COLUMN_SCHEDULE_ID + " integer primary key autoincrement, "
      + COLUMN_SCHEDULE_START_HOUR + " integer not null default 0, "
      + COLUMN_SCHEDULE_START_MINUTE + " integer not null default 0, "
      + COLUMN_SCHEDULE_ACTIVE + " integer not null default 1, "
      + COLUMN_SCHEDULE_DAY0 + " integer not null default 0, "
      + COLUMN_SCHEDULE_DAY1 + " integer not null default 0, "
      + COLUMN_SCHEDULE_DAY2 + " integer not null default 0, "
      + COLUMN_SCHEDULE_DAY3 + " integer not null default 0, "
      + COLUMN_SCHEDULE_DAY4 + " integer not null default 0, "
      + COLUMN_SCHEDULE_DAY5 + " integer not null default 0, "
      + COLUMN_SCHEDULE_DAY6 + " integer not null default 0, "
      + COLUMN_SCHEDULE_PROFILE_ID + " integer not null " + ");";

  public static final String SCHEDULE_DEFAULT_ORDER =
      COLUMN_SCHEDULE_START_HOUR + ","
          + COLUMN_SCHEDULE_START_MINUTE + ","
          + COLUMN_SCHEDULE_DAY0 + " desc, "
          + COLUMN_SCHEDULE_DAY1 + " desc, "
          + COLUMN_SCHEDULE_DAY2 + " desc, "
          + COLUMN_SCHEDULE_DAY3 + " desc, "
          + COLUMN_SCHEDULE_DAY4 + " desc, "
          + COLUMN_SCHEDULE_DAY5 + " desc, "
          + COLUMN_SCHEDULE_DAY6 + " desc, "
          + TABLE_SCHEDULE + "." + COLUMN_SCHEDULE_ID;

  // expose a URI for our data
  public static final Uri CONTENT_URI = createTableUri(AUTHORITY, TABLE_SCHEDULE);

  // Content Provider requisites
  static final UriMatcher sUriMatcher;
  static final HashMap<String, String> sGoalProjectionMap;

  /*
   * initialize sUriMatcher and sGoalProjectionMap
   */
  static
  {
    /*
     * defines how to identify what is being requested
     */
    sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    sUriMatcher.addURI(AUTHORITY, TABLE_SCHEDULE, NO_FILTER);
    sUriMatcher.addURI(AUTHORITY, TABLE_SCHEDULE + "/#", FILTER_SCHEDULE_ID);
    sUriMatcher.addURI(AUTHORITY, TABLE_SCHEDULE + "/profile/#", FILTER_SCHEDULE_PROFILE_ID);
    sUriMatcher.addURI(AUTHORITY, TABLE_SCHEDULE + "/profile/#/hour/#/minute/#", FILTER_SCHEDULE_PROFILE_ID_START_TIME);
    sUriMatcher.addURI(AUTHORITY, TABLE_SCHEDULE + "/active", FILTER_ALL_ACTIVE);

    /*
     * defines the columns returned for any query
     */
    sGoalProjectionMap = new HashMap<String, String>();
    sGoalProjectionMap.put(COLUMN_SCHEDULE_ID, TABLE_SCHEDULE + "." + COLUMN_SCHEDULE_ID);
    sGoalProjectionMap.put(COLUMN_SCHEDULE_ACTIVE, TABLE_SCHEDULE + "." + COLUMN_SCHEDULE_ACTIVE);
    sGoalProjectionMap.put(COLUMN_SCHEDULE_START_HOUR, COLUMN_SCHEDULE_START_HOUR);
    sGoalProjectionMap.put(COLUMN_SCHEDULE_START_MINUTE, COLUMN_SCHEDULE_START_MINUTE);
    sGoalProjectionMap.put(COLUMN_SCHEDULE_DAY0, COLUMN_SCHEDULE_DAY0);
    sGoalProjectionMap.put(COLUMN_SCHEDULE_DAY1, COLUMN_SCHEDULE_DAY1);
    sGoalProjectionMap.put(COLUMN_SCHEDULE_DAY2, COLUMN_SCHEDULE_DAY2);
    sGoalProjectionMap.put(COLUMN_SCHEDULE_DAY3, COLUMN_SCHEDULE_DAY3);
    sGoalProjectionMap.put(COLUMN_SCHEDULE_DAY4, COLUMN_SCHEDULE_DAY4);
    sGoalProjectionMap.put(COLUMN_SCHEDULE_DAY5, COLUMN_SCHEDULE_DAY5);
    sGoalProjectionMap.put(COLUMN_SCHEDULE_DAY6, COLUMN_SCHEDULE_DAY6);
    sGoalProjectionMap.put(COLUMN_SCHEDULE_PROFILE_ID, TABLE_SCHEDULE + "." + COLUMN_SCHEDULE_PROFILE_ID);
  }

  /*
   * (non-Javadoc)
   * 
   * @see android.content.ContentProvider#getType(android.net.Uri)
   */
  @Override
  protected String getType(int matchedCode)
  {
    switch (matchedCode)
    {
    case FILTER_SCHEDULE_ID:
      return "vnd.android.cursor.item/" + AUTHORITY;
    case FILTER_SCHEDULE_PROFILE_ID:
      return "vnd.android.cursor.dir/" + AUTHORITY + ".profile";
    case FILTER_SCHEDULE_PROFILE_ID_START_TIME:
      return "vnd.android.cursor.dir/" + AUTHORITY + ".profile.startTime";
    case FILTER_ALL_ACTIVE:
      return "vnd.android.cursor.dir/" + AUTHORITY + ".active";
    default:
      throw new IllegalArgumentException("Unknown code: " + matchedCode);
    }
  }

  @Override
  protected String getTable(int matchedCode)
  {
    switch (matchedCode)
    {
    case FILTER_ALL_ACTIVE:
    case FILTER_SCHEDULE_PROFILE_ID_START_TIME:
      return TABLE_SCHEDULE + " LEFT OUTER JOIN " + TABLE_PROFILE +
          " ON (" + TABLE_SCHEDULE + "." + COLUMN_SCHEDULE_PROFILE_ID + " = " + TABLE_PROFILE + "." + COLUMN_PROFILE_ID + ")";
    default:
      return TABLE_SCHEDULE;
    }
  }

  @Override
  protected Uri getContentUri()
  {
    return CONTENT_URI;
  }

  @Override
  protected UriMatcher getUriMatcher()
  {
    return sUriMatcher;
  }

  @Override
  protected Map<String, String> getProjectionMap(int matchedCode)
  {
    return sGoalProjectionMap;
  }

  @Override
  public void checkInitialValues(ContentValues initialValues)
  {
    if ((initialValues == null) || !initialValues.containsKey(COLUMN_SCHEDULE_PROFILE_ID))
    {
      throw new IllegalArgumentException("Schedule's Profile Id is required on insert.");
    }
  }

  @Override
  protected String[] getUpdateField(int matchedCode)
  {
    /*
     * allow update by ID only
     */
    if (matchedCode == FILTER_SCHEDULE_ID)
    {
      return new String[] { COLUMN_SCHEDULE_ID };
    }

    throw new IllegalArgumentException("unknown code:" + matchedCode);
  }

  @Override
  protected String[] getMatchedValue(Uri uri, int matchedCode)
  {
    switch (matchedCode)
    {
    case FILTER_SCHEDULE_ID: // /#
      return new String[] { uri.getPathSegments().get(1) };
    case FILTER_SCHEDULE_PROFILE_ID: // /profile/#
      return new String[] { uri.getPathSegments().get(2) };
    case FILTER_SCHEDULE_PROFILE_ID_START_TIME: // /profile/#/hour/#/minute/#
      return new String[] { uri.getPathSegments().get(2), uri.getPathSegments().get(4), uri.getPathSegments().get(6) };
    case FILTER_ALL_ACTIVE:
      return new String[] { "1", "1" };
    }
    throw new IllegalArgumentException("unknown code:" + matchedCode);
  }

  @Override
  protected String getDefaultSortOrder(int matchedCode)
  {
    switch (matchedCode)
    {
    case FILTER_SCHEDULE_ID:
    case FILTER_SCHEDULE_PROFILE_ID:
    case FILTER_SCHEDULE_PROFILE_ID_START_TIME:
    case FILTER_ALL_ACTIVE:
      return SCHEDULE_DEFAULT_ORDER;
    }
    throw new IllegalArgumentException("unknown code:" + matchedCode);
  }

  @Override
  protected String[] getQueryField(int matchedCode)
  {
    switch (matchedCode)
    {
    case FILTER_SCHEDULE_ID:
      return new String[] { COLUMN_SCHEDULE_ID };

    case FILTER_SCHEDULE_PROFILE_ID:
      return new String[] { COLUMN_SCHEDULE_PROFILE_ID };

    case FILTER_SCHEDULE_PROFILE_ID_START_TIME:
      return new String[] { COLUMN_SCHEDULE_PROFILE_ID, COLUMN_SCHEDULE_START_HOUR, COLUMN_SCHEDULE_START_MINUTE };

    case FILTER_ALL_ACTIVE:
      return new String[] { TABLE_SCHEDULE + "." + COLUMN_SCHEDULE_ACTIVE, TABLE_PROFILE + "." + COLUMN_PROFILE_ACTIVE };

    }
    throw new IllegalArgumentException("unknown code:" + matchedCode);
  }

  public static void createTable(SQLiteDatabase db) throws SQLException
  {
    db.execSQL(TABLE_SCHEDULE_CREATE);
  }

  public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
  {

  }
}
