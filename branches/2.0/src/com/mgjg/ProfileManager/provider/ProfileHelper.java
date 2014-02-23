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

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.mgjg.ProfileManager.profile.Profile;
import com.mgjg.ProfileManager.profile.ProfileListAdapter;
import com.mgjg.ProfileManager.utils.ListAdapter;

/**
 * Abstracts access to profile data in SQLite db
 * 
 * @author Mike Partridge / Jay Goldman
 */
public class ProfileHelper extends ProfileManagerProviderHelper<Profile>
{

  public ProfileHelper(Context context)
  {
    super(context);
    // TODO Auto-generated constructor stub
  }

  public static final String TABLE_PROFILE = "profiles";
  public static final String TABLE_ALIAS_PROFILE = "p";

  public static final String COLUMN_PROFILE_ID = "_id";
  public static final String COLUMN_PROFILE_NAME = "_name";
  public static final String COLUMN_PROFILE_ACTIVE = "_active";
  public static final String COLUMN_PROFILE_MODE = "_override"; // overrides current settings or not
  public static final String COLUMN_PROFILE_TYPE = "_type";

  public static final String PROFILE_DEFAULT_ORDER =
      COLUMN_PROFILE_NAME + " desc, "
          + COLUMN_PROFILE_ACTIVE + ","
          + COLUMN_PROFILE_MODE + ","
          + TABLE_PROFILE + "." + COLUMN_PROFILE_ID;

  // names for Intent values
  public static final String INTENT_PROFILE_ID = "com.mgjg.ProfileManager.profile.id";
  public static final String INTENT_PROFILE_ACTIVE = "com.mgjg.ProfileManager.profile.active";
  public static final String INTENT_PROFILE_NAME = "com.mgjg.ProfileManager.profile.name";

  public static final int FILTER_PROFILE_ID = 1;
  public static final int FILTER_PROFILE_TYPE = 2;
  public static final int FILTER_PROFILE_NAME = 3;
  public static final int FILTER_PROFILE_MODE = 4;

  @Override
  public Uri getContentUri()
  {
    return ProfileProvider.CONTENT_URI;
  }

  @Override
  public Uri getContentUri(int filter, Object... values)
  {
    final Uri uri = getContentUri();
    switch (filter)
    {

    case NO_FILTER:
      return uri;

    case FILTER_PROFILE_ID:
      return Uri.withAppendedPath(uri, String.valueOf(values[0]));

    case FILTER_PROFILE_NAME:
      return Uri.withAppendedPath(Uri.withAppendedPath(uri, "name"), String.valueOf(values[0]));

    case FILTER_PROFILE_TYPE:
      return Uri.withAppendedPath(Uri.withAppendedPath(uri, "type"), String.valueOf(values[0]));

    case FILTER_ALL_ACTIVE:
      return Uri.withAppendedPath(uri, "active");

    default:
      throw new IllegalArgumentException("Unknown filter " + filter);
    }
  }

  @Override
  public final ListAdapter<Profile> createListAdapter(int filter, Object... value)
  {
    return fillListAdapter(new ProfileListAdapter(context), query(filter, value));
  }

  @Override
  public Profile newInstance(Cursor c)
  {
    long id = c.getLong(c.getColumnIndexOrThrow(COLUMN_PROFILE_ID));
    String name = c.getString(c.getColumnIndexOrThrow(COLUMN_PROFILE_NAME));
    boolean active = c.getInt(c.getColumnIndexOrThrow(COLUMN_PROFILE_ACTIVE)) > 0;
    int type = c.getInt(c.getColumnIndexOrThrow(COLUMN_PROFILE_TYPE));
    return new Profile(id, name, type, active);
  }

  public final int deleteProfile(long id)
  {
    new ScheduleHelper(context).deleteProfile(id);
    new AttributeHelper(context).deleteProfile(id);
    return delete(FILTER_PROFILE_ID, id);
  }

}
