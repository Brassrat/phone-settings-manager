/**
 * Copyright 2009 Mike Partridge
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
package com.mgjg.ProfileManager.provider;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.mgjg.ProfileManager.profile.Profile;

import java.util.HashMap;
import java.util.Map;

import static com.mgjg.ProfileManager.provider.ProfileHelper.COLUMN_PROFILE_ACTIVE;
import static com.mgjg.ProfileManager.provider.ProfileHelper.COLUMN_PROFILE_ID;
import static com.mgjg.ProfileManager.provider.ProfileHelper.COLUMN_PROFILE_MODE;
import static com.mgjg.ProfileManager.provider.ProfileHelper.COLUMN_PROFILE_NAME;
import static com.mgjg.ProfileManager.provider.ProfileHelper.COLUMN_PROFILE_TYPE;
import static com.mgjg.ProfileManager.provider.ProfileHelper.FILTER_PROFILE_ID;
import static com.mgjg.ProfileManager.provider.ProfileHelper.FILTER_PROFILE_MODE;
import static com.mgjg.ProfileManager.provider.ProfileHelper.FILTER_PROFILE_NAME;
import static com.mgjg.ProfileManager.provider.ProfileHelper.FILTER_PROFILE_TYPE;
import static com.mgjg.ProfileManager.provider.ProfileHelper.PROFILE_DEFAULT_ORDER;
import static com.mgjg.ProfileManager.provider.ProfileHelper.TABLE_PROFILE;

/**
 * Abstracts access to profile data in SQLite db
 *
 * @author Mike Partridge / Jay Goldman
 */
public class ProfileProvider extends ProfileManagerProvider<Profile>
{

  public static final String AUTHORITY = "com.mgjg.ProfileManager.provider.ProfileProvider";

  public static final String TABLE_PROFILE_CREATE = "create table "
      + TABLE_PROFILE + " ("
      + COLUMN_PROFILE_ID + " integer primary key autoincrement, "
      + COLUMN_PROFILE_NAME + " text not null, " // unique
      + COLUMN_PROFILE_ACTIVE + " integer not null default 1, " // disables all schedules
      + COLUMN_PROFILE_MODE + " integer not null default 1, "
      + COLUMN_PROFILE_TYPE + " integer not null default 0"
      + ");";

  // expose a URI for our data
  public static final Uri CONTENT_URI = createTableUri(AUTHORITY, TABLE_PROFILE);

  // Content Provider requisites
  static final UriMatcher sUriMatcher;
  static HashMap<String, String> sGoalProjectionMap;

  /*
   * initialize sUriMatcher and sGoalProjectionMap
   */
  static
  {
    /*
     * defines how to identify what is being requested
     */
    sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    sUriMatcher.addURI(AUTHORITY, TABLE_PROFILE, NO_FILTER);
    sUriMatcher.addURI(AUTHORITY, TABLE_PROFILE + "/#", FILTER_PROFILE_ID);
    sUriMatcher.addURI(AUTHORITY, TABLE_PROFILE + "/name/*", FILTER_PROFILE_NAME);
    sUriMatcher.addURI(AUTHORITY, TABLE_PROFILE + "/type/#", FILTER_PROFILE_TYPE);
    sUriMatcher.addURI(AUTHORITY, TABLE_PROFILE + "/mode/#", FILTER_PROFILE_MODE);
    sUriMatcher.addURI(AUTHORITY, TABLE_PROFILE + "/active", FILTER_ALL_ACTIVE);

    sGoalProjectionMap = new HashMap<>();
    sGoalProjectionMap.put(COLUMN_PROFILE_ID, TABLE_PROFILE + "." + COLUMN_PROFILE_ID);
    sGoalProjectionMap.put(COLUMN_PROFILE_NAME, COLUMN_PROFILE_NAME);
    sGoalProjectionMap.put(COLUMN_PROFILE_ACTIVE, TABLE_PROFILE + "." + COLUMN_PROFILE_ACTIVE);
    sGoalProjectionMap.put(COLUMN_PROFILE_MODE, COLUMN_PROFILE_MODE);
    sGoalProjectionMap.put(COLUMN_PROFILE_TYPE, COLUMN_PROFILE_TYPE);
  }

  @Override
  protected String getTable(int matchedCode)
  {
    return TABLE_PROFILE;
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

  /*
   * (non-Javadoc)
   *
   * @see android.content.ContentProvider#getType(android.net.Uri)
   */
  @Override
  public String getType(int matchedCode)
  {
    switch (matchedCode)
    {
      case FILTER_PROFILE_ID:
        return "vnd.android.cursor.item/" + AUTHORITY;
      case FILTER_PROFILE_NAME:
        return "vnd.android.cursor.item/" + AUTHORITY + ".name";
      case FILTER_PROFILE_TYPE:
        return "vnd.android.cursor.dir/" + AUTHORITY + ".type";
      case FILTER_PROFILE_MODE:
        return "vnd.android.cursor.dir/" + AUTHORITY + ".mode";
      case FILTER_ALL_ACTIVE:
        return "vnd.android.cursor.dir/" + AUTHORITY + ".active";
      default:
        throw new IllegalArgumentException("Unknown Filter code " + matchedCode);
    }
  }

  @Override
  protected Map<String, String> getProjectionMap(int matchedCode)
  {
    return sGoalProjectionMap;
  }

  @Override
  protected void checkInitialValues(ContentValues initialValues)
  {
    /*
     * profile type is required on insert
     */
    if ((initialValues == null) || !initialValues.containsKey(COLUMN_PROFILE_TYPE))
    {
      throw new IllegalArgumentException("Profile Type value is required on insert.");
    }

  }

  @Override
  protected String[] getUpdateField(int matchedCode)
  {
    /*
     * update by ID only
     */
    switch (matchedCode)
    {

      case FILTER_PROFILE_ID:
        return new String[]{COLUMN_PROFILE_ID};

      default:
        // should never get here if app is coded correctly
        throw new IllegalArgumentException("Invalid Filter " + matchedCode);
    }
  }

  @Override
  protected String[] getMatchedValue(Uri uri, int matchedCode)
  {
    switch (matchedCode)
    {

      case FILTER_PROFILE_ID:
        return new String[]{uri.getPathSegments().get(1)};

      case FILTER_PROFILE_NAME:
        return new String[]{uri.getPathSegments().get(2)};

      case FILTER_PROFILE_TYPE:
        String type = uri.getPathSegments().get(2);
        return new String[]{type}; // String.valueOf(SoundAttribute.mapMimeTypeToSoundType(mimeType))

      case FILTER_PROFILE_MODE:
        return new String[]{uri.getPathSegments().get(2)};

      case FILTER_ALL_ACTIVE:
        return new String[]{"1"};

      default:
        throw new IllegalArgumentException("Unknown URI " + uri);
    }
  }

  @Override
  protected String getDefaultSortOrder(int matchedCode)
  {
    switch (matchedCode)
    {
      case FILTER_ALL_ACTIVE:
        return COLUMN_PROFILE_TYPE;
    }
    return PROFILE_DEFAULT_ORDER;
  }

  @Override
  protected String[] getQueryField(int matchedCode)
  {
    /*
     * act on supported query URIs
     */
    switch (matchedCode)
    {

      case FILTER_PROFILE_TYPE:
        return new String[]{COLUMN_PROFILE_TYPE};

      case FILTER_PROFILE_ID:
        return new String[]{COLUMN_PROFILE_ID};

      case FILTER_PROFILE_NAME:
        return new String[]{COLUMN_PROFILE_NAME};

      case FILTER_PROFILE_MODE:
        return new String[]{COLUMN_PROFILE_MODE};

      case FILTER_ALL_ACTIVE:
        return new String[]{COLUMN_PROFILE_ACTIVE};

      default:
        throw new IllegalArgumentException("Unknown Filter " + matchedCode);
    }
  }

  public static void createTable(SQLiteDatabase db) throws SQLException
  {
    db.execSQL(TABLE_PROFILE_CREATE);
  }

  public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
  {

  }
}
