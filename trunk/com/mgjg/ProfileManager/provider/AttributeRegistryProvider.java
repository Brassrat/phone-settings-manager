/**
 * Copyright 2012 Jay Goldman
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

import static com.mgjg.ProfileManager.provider.AttributeRegistryHelper.COLUMN_REGISTRY_ACTIVE;
import static com.mgjg.ProfileManager.provider.AttributeRegistryHelper.COLUMN_REGISTRY_CLASS;
import static com.mgjg.ProfileManager.provider.AttributeRegistryHelper.COLUMN_REGISTRY_ID;
import static com.mgjg.ProfileManager.provider.AttributeRegistryHelper.COLUMN_REGISTRY_NAME;
import static com.mgjg.ProfileManager.provider.AttributeRegistryHelper.COLUMN_REGISTRY_PARAM;
import static com.mgjg.ProfileManager.provider.AttributeRegistryHelper.COLUMN_REGISTRY_TYPE;
import static com.mgjg.ProfileManager.provider.AttributeRegistryHelper.FILTER_REGISTRY_ID;
import static com.mgjg.ProfileManager.provider.AttributeRegistryHelper.FILTER_REGISTRY_NAME;
import static com.mgjg.ProfileManager.provider.AttributeRegistryHelper.FILTER_REGISTRY_TYPE;
import static com.mgjg.ProfileManager.provider.AttributeRegistryHelper.REGISTRY_DEFAULT_ORDER;
import static com.mgjg.ProfileManager.provider.AttributeRegistryHelper.TABLE_REGISTRY;

import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.mgjg.ProfileManager.profile.Profile;

/**
 * Abstracts access to profile manager attribute registry data in SQLite db
 * 
 * @author Jay Goldman
 */
public class AttributeRegistryProvider extends ProfileManagerProvider<Profile>
{

  public static final String AUTHORITY = "com.mgjg.ProfileManager.provider.ProfileProvider";

  public static final String TABLE_REGISTRY_CREATE = "create table "
      + TABLE_REGISTRY + " ("
      + COLUMN_REGISTRY_ID + " integer primary key autoincrement, "
      + COLUMN_REGISTRY_NAME + " text not null, " // unique
      + COLUMN_REGISTRY_TYPE + " integer not null default 0, "
      + COLUMN_REGISTRY_ACTIVE + " integer not null default 1, "
      + COLUMN_REGISTRY_CLASS + " text not null, "
      + COLUMN_REGISTRY_PARAM + " text not null"
      + ");";

  // expose a URI for our data
  public static final Uri CONTENT_URI = createTableUri(AUTHORITY, TABLE_REGISTRY);

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
    sUriMatcher.addURI(AUTHORITY, TABLE_REGISTRY, NO_FILTER);
    sUriMatcher.addURI(AUTHORITY, TABLE_REGISTRY + "/#", FILTER_REGISTRY_ID);
    sUriMatcher.addURI(AUTHORITY, TABLE_REGISTRY + "/name/*", FILTER_REGISTRY_NAME);
    sUriMatcher.addURI(AUTHORITY, TABLE_REGISTRY + "/type/#", FILTER_REGISTRY_TYPE);
    sUriMatcher.addURI(AUTHORITY, TABLE_REGISTRY + "/active", FILTER_ALL_ACTIVE);

    sGoalProjectionMap = new HashMap<String, String>();
    sGoalProjectionMap.put(COLUMN_REGISTRY_ID, TABLE_REGISTRY + "." + COLUMN_REGISTRY_ID);
    sGoalProjectionMap.put(COLUMN_REGISTRY_NAME, COLUMN_REGISTRY_NAME);
    sGoalProjectionMap.put(COLUMN_REGISTRY_ACTIVE, TABLE_REGISTRY + "." + COLUMN_REGISTRY_ACTIVE);
    sGoalProjectionMap.put(COLUMN_REGISTRY_TYPE, COLUMN_REGISTRY_TYPE);
  }

  @Override
  protected String getTable(int matchedCode)
  {
    return TABLE_REGISTRY;
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
    case FILTER_REGISTRY_ID:
      return "vnd.android.cursor.item/" + AUTHORITY;
    case FILTER_REGISTRY_NAME:
      return "vnd.android.cursor.item/" + AUTHORITY + ".name";
    case FILTER_REGISTRY_TYPE:
      return "vnd.android.cursor.dir/" + AUTHORITY + ".type";
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
    if ((initialValues == null) || !initialValues.containsKey(COLUMN_REGISTRY_TYPE))
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

    case FILTER_REGISTRY_ID:
      return new String[] { COLUMN_REGISTRY_ID };

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

    case FILTER_REGISTRY_ID:
      return new String[] { uri.getPathSegments().get(1) };

    case FILTER_REGISTRY_NAME:
      return new String[] { uri.getPathSegments().get(2) };

    case FILTER_REGISTRY_TYPE:
      String type = uri.getPathSegments().get(2);
      return new String[] { type };

    case FILTER_ALL_ACTIVE:
      return new String[] { "1" };

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
      return COLUMN_REGISTRY_TYPE;
    }
    return REGISTRY_DEFAULT_ORDER;
  }

  @Override
  protected String[] getQueryField(int matchedCode)
  {
    /*
     * act on supported query URIs
     */
    switch (matchedCode)
    {

    case FILTER_REGISTRY_TYPE:
      return new String[] { COLUMN_REGISTRY_TYPE };

    case FILTER_REGISTRY_ID:
      return new String[] { COLUMN_REGISTRY_ID };

    case FILTER_REGISTRY_NAME:
      return new String[] { COLUMN_REGISTRY_NAME };

    case FILTER_ALL_ACTIVE:
      return new String[] { COLUMN_REGISTRY_ACTIVE };

    default:
      throw new IllegalArgumentException("Unknown Filter " + matchedCode);
    }
  }

  public static void createTable(SQLiteDatabase db) throws SQLException
  {
    db.execSQL(TABLE_REGISTRY_CREATE);
  }

  public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
  {

  }
}
