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

import static com.mgjg.ProfileManager.provider.AttributeHelper.DEFAULT_ORDER_ATTRIBUTE;
import static com.mgjg.ProfileManager.provider.AttributeHelper.FILTER_ATTRIBUTE_ID;
import static com.mgjg.ProfileManager.provider.AttributeHelper.FILTER_ATTRIBUTE_PROFILE_ACTIVE;
import static com.mgjg.ProfileManager.provider.AttributeHelper.FILTER_ATTRIBUTE_PROFILE_ID;
import static com.mgjg.ProfileManager.provider.AttributeHelper.FILTER_ATTRIBUTE_PROFILE_TYPE;
import static com.mgjg.ProfileManager.provider.AttributeHelper.FILTER_ATTRIBUTE_TYPE;
import static com.mgjg.ProfileManager.provider.AttributeHelper.COLUMN_ATTRIBUTE_ID;
import static com.mgjg.ProfileManager.provider.AttributeHelper.COLUMN_ATTRIBUTE_PROFILE_ID;
import static com.mgjg.ProfileManager.provider.AttributeHelper.COLUMN_ATTRIBUTE_SETTING;
import static com.mgjg.ProfileManager.provider.AttributeHelper.COLUMN_ATTRIBUTE_TYPE;
import static com.mgjg.ProfileManager.provider.AttributeHelper.COLUMN_ATTRIBUTE_BOOL_VALUE;
import static com.mgjg.ProfileManager.provider.AttributeHelper.COLUMN_ATTRIBUTE_INT_VALUE;
import static com.mgjg.ProfileManager.provider.AttributeHelper.TABLE_ATTRIBUTE;
import static com.mgjg.ProfileManager.provider.ProfileHelper.COLUMN_PROFILE_ACTIVE;
import static com.mgjg.ProfileManager.provider.ProfileHelper.COLUMN_PROFILE_ID;
import static com.mgjg.ProfileManager.provider.ProfileHelper.TABLE_PROFILE;

import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.mgjg.ProfileManager.attribute.ProfileAttribute;

/**
 * Abstracts access to profile data in SQLite db
 * 
 * @author Mike Partridge / Jay Goldman
 */
public class AttributeProvider extends ProfileManagerProvider<ProfileAttribute>
{
  public static final String AUTHORITY = "com.mgjg.ProfileManager.provider.AttributeProvider";

  public static final String TABLE_ATTRIBUTE_CREATE = "create table "
      + TABLE_ATTRIBUTE + " ("
      + COLUMN_ATTRIBUTE_ID + " integer primary key autoincrement, "
      + COLUMN_ATTRIBUTE_PROFILE_ID + " integer not null, "
      + COLUMN_ATTRIBUTE_TYPE + " integer not null, "
      + COLUMN_ATTRIBUTE_INT_VALUE + " integer not null default 0, "
      + COLUMN_ATTRIBUTE_BOOL_VALUE + " integer not null default 0, "
      + COLUMN_ATTRIBUTE_SETTING + " text not null default '' "
      + ");";

  // expose a URI for our data
  public static final Uri CONTENT_URI = createTableUri(AUTHORITY, TABLE_ATTRIBUTE);

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
    sUriMatcher.addURI(AUTHORITY, TABLE_ATTRIBUTE, NO_FILTER);
    sUriMatcher.addURI(AUTHORITY, TABLE_ATTRIBUTE + "/#", FILTER_ATTRIBUTE_ID);
    sUriMatcher.addURI(AUTHORITY, TABLE_ATTRIBUTE + "/profile/#", FILTER_ATTRIBUTE_PROFILE_ID);
    sUriMatcher.addURI(AUTHORITY, TABLE_ATTRIBUTE + "/type/#", FILTER_ATTRIBUTE_TYPE);
    sUriMatcher.addURI(AUTHORITY, TABLE_ATTRIBUTE + "/active", FILTER_ALL_ACTIVE);
    sUriMatcher.addURI(AUTHORITY, TABLE_ATTRIBUTE + "/profile/#/type/#", FILTER_ATTRIBUTE_PROFILE_TYPE);
    sUriMatcher.addURI(AUTHORITY, TABLE_ATTRIBUTE + "/profile/#/active", FILTER_ATTRIBUTE_PROFILE_ACTIVE);

    sGoalProjectionMap = new HashMap<String, String>();
    sGoalProjectionMap.put(COLUMN_ATTRIBUTE_ID, TABLE_ATTRIBUTE + "." + COLUMN_ATTRIBUTE_ID);
    sGoalProjectionMap.put(COLUMN_ATTRIBUTE_PROFILE_ID, TABLE_ATTRIBUTE + "." + COLUMN_ATTRIBUTE_PROFILE_ID);
    sGoalProjectionMap.put(COLUMN_ATTRIBUTE_TYPE, TABLE_ATTRIBUTE + "." + COLUMN_ATTRIBUTE_TYPE);
    sGoalProjectionMap.put(COLUMN_ATTRIBUTE_INT_VALUE, COLUMN_ATTRIBUTE_INT_VALUE);
    sGoalProjectionMap.put(COLUMN_ATTRIBUTE_BOOL_VALUE, COLUMN_ATTRIBUTE_BOOL_VALUE);
    sGoalProjectionMap.put(COLUMN_ATTRIBUTE_SETTING, COLUMN_ATTRIBUTE_SETTING);
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
    case FILTER_ATTRIBUTE_ID:
      return "vnd.android.cursor.item/" + AUTHORITY;
    case FILTER_ATTRIBUTE_PROFILE_ID:
      return "vnd.android.cursor.dir/" + AUTHORITY + ".profile";
    case FILTER_ATTRIBUTE_TYPE:
      return "vnd.android.cursor.dir/" + AUTHORITY + ".type";
    case FILTER_ALL_ACTIVE:
      return "vnd.android.cursor.dir/" + AUTHORITY + ".active";
    case FILTER_ATTRIBUTE_PROFILE_TYPE:
      return "vnd.android.cursor.dir/" + AUTHORITY + ".profile.type";
    case FILTER_ATTRIBUTE_PROFILE_ACTIVE:
      return "vnd.android.cursor.dir/" + AUTHORITY + ".profile.active";
    default:
      throw new IllegalArgumentException("Unknown Filter code " + matchedCode);
    }
  }

  @Override
  protected String getTable(int matchedCode)
  {
    switch (matchedCode)
    {
    case FILTER_ALL_ACTIVE:
    case FILTER_ATTRIBUTE_PROFILE_ACTIVE:
      return TABLE_ATTRIBUTE + " LEFT OUTER JOIN " + TABLE_PROFILE +
          " ON (" + TABLE_ATTRIBUTE + "." + COLUMN_ATTRIBUTE_PROFILE_ID + " = " + TABLE_PROFILE + "." + COLUMN_PROFILE_ID + ")";
    default:
      return TABLE_ATTRIBUTE;
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
  protected void checkInitialValues(ContentValues initialValues)
  {
    /*
     * must have a profile id
     */
    if ((initialValues == null) || !initialValues.containsKey(COLUMN_ATTRIBUTE_PROFILE_ID))
    {
      throw new IllegalArgumentException("Profile id value is required on insert.");
    }
    /*
     * type is required on insert
     */
    if (!initialValues.containsKey(COLUMN_ATTRIBUTE_TYPE))
    {
      throw new IllegalArgumentException("Type value is required on insert.");
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

    case FILTER_ATTRIBUTE_ID:
      return new String[] { COLUMN_ATTRIBUTE_ID };

    case FILTER_ATTRIBUTE_PROFILE_ID:
      return new String[] { COLUMN_ATTRIBUTE_PROFILE_ID };
      
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

    case FILTER_ATTRIBUTE_ID:
      return new String[] { uri.getPathSegments().get(1) };

    case FILTER_ATTRIBUTE_PROFILE_ID:
      return new String[] { uri.getPathSegments().get(2) };

    case FILTER_ATTRIBUTE_TYPE:
      return new String[] { uri.getPathSegments().get(2) };

    case FILTER_ATTRIBUTE_PROFILE_TYPE:
      return new String[] { uri.getPathSegments().get(2), uri.getPathSegments().get(4) };

    case FILTER_ATTRIBUTE_PROFILE_ACTIVE:
      return new String[] { uri.getPathSegments().get(2), "1" };

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
    case FILTER_ATTRIBUTE_ID:
    case FILTER_ATTRIBUTE_PROFILE_ID:
    case FILTER_ATTRIBUTE_TYPE:
      return COLUMN_ATTRIBUTE_TYPE;

    case FILTER_ALL_ACTIVE:
    case FILTER_ATTRIBUTE_PROFILE_ACTIVE:
      return TABLE_ATTRIBUTE + "." + COLUMN_ATTRIBUTE_TYPE;
    }
    return DEFAULT_ORDER_ATTRIBUTE;
  }

  @Override
  protected String[] getQueryField(int matchedCode)
  {
    switch (matchedCode)
    {

    case FILTER_ATTRIBUTE_ID:
      return new String[] { COLUMN_ATTRIBUTE_ID };

    case FILTER_ATTRIBUTE_PROFILE_ID:
      return new String[] { COLUMN_ATTRIBUTE_PROFILE_ID };

    case FILTER_ATTRIBUTE_TYPE:
      return new String[] { COLUMN_ATTRIBUTE_TYPE };

    case FILTER_ATTRIBUTE_PROFILE_TYPE:
      return new String[] { COLUMN_ATTRIBUTE_PROFILE_ID, COLUMN_ATTRIBUTE_TYPE };

    case FILTER_ATTRIBUTE_PROFILE_ACTIVE:
      return new String[] { COLUMN_ATTRIBUTE_PROFILE_ID, TABLE_PROFILE + "." + COLUMN_PROFILE_ACTIVE };

    case FILTER_ALL_ACTIVE:
      // should be a reference to the associated profile's active field
      return new String[] { TABLE_PROFILE + "." + COLUMN_PROFILE_ACTIVE };

    default:
      // should never get here if app is coded correctly
      throw new IllegalArgumentException("Invalid Filter " + matchedCode);
    }
  }

  public static void createTable(SQLiteDatabase db) throws SQLException
  {
    db.execSQL(TABLE_ATTRIBUTE_CREATE);
  }
  
  public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
  {
    
  }
}
