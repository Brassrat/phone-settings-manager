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

import com.mgjg.ProfileManager.registry.AttributeRegistryListAdapter;
import com.mgjg.ProfileManager.registry.RegisteredAttribute;
import com.mgjg.ProfileManager.utils.ListAdapter;

/**
 * Abstracts access to attribute registry data in SQLite db
 * 
 * @author Jay Goldman
 */
public class AttributeRegistryHelper extends ProfileManagerProviderHelper<RegisteredAttribute>
{

  public AttributeRegistryHelper(Context context)
  {
    super(context);
  }

  public static final String TABLE_REGISTRY = "registry";
  public static final String TABLE_ALIAS_REGISTRY = "r";

  public static final String COLUMN_REGISTRY_ID = "_id";
  public static final String COLUMN_REGISTRY_NAME = "_name";
  public static final String COLUMN_REGISTRY_TYPE = "_type"; // registered type id
  public static final String COLUMN_REGISTRY_ACTIVE = "_active";
  public static final String COLUMN_REGISTRY_MENU = "_menu";
  public static final String COLUMN_REGISTRY_CLASS = "_class";
  public static final String COLUMN_REGISTRY_PARAM = "_param";
  public static final String COLUMN_REGISTRY_ORDER = "_order";

  public static final String REGISTRY_DEFAULT_ORDER =
      COLUMN_REGISTRY_NAME + " desc, "
          + COLUMN_REGISTRY_TYPE + ","
          + COLUMN_REGISTRY_ACTIVE + ","
          + COLUMN_REGISTRY_CLASS + ","
          + COLUMN_REGISTRY_PARAM + ","
          + COLUMN_REGISTRY_ORDER + ","
          + TABLE_REGISTRY + "." + COLUMN_REGISTRY_ID;

  // names for Intent values
  public static final String INTENT_REGISTRY_ID = "com.mgjg.ProfileManager.registry.id";
  public static final String INTENT_REGISTRY_NAME = "com.mgjg.ProfileManager.registry.name";
  public static final String INTENT_REGISTRY_TYPE = "com.mgjg.ProfileManager.registry.type";
  public static final String INTENT_REGISTRY_ACTIVE = "com.mgjg.ProfileManager.registry.active";

  public static final int FILTER_REGISTRY_ID = 1;
  public static final int FILTER_REGISTRY_NAME = 2;
  public static final int FILTER_REGISTRY_TYPE = 3;

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

    case FILTER_ALL_ACTIVE:
      return Uri.withAppendedPath(uri, "active");

    case FILTER_REGISTRY_ID:
      return Uri.withAppendedPath(uri, String.valueOf(values[0]));

    case FILTER_REGISTRY_NAME:
      return Uri.withAppendedPath(Uri.withAppendedPath(uri, "name"), String.valueOf(values[0]));

    case FILTER_REGISTRY_TYPE:
      return Uri.withAppendedPath(Uri.withAppendedPath(uri, "type"), String.valueOf(values[0]));

    default:
      throw new IllegalArgumentException("Unknown filter " + filter);
    }
  }

  @Override
  public final ListAdapter<RegisteredAttribute> createListAdapter(int filter, Object... value)
  {
    return fillListAdapter(new AttributeRegistryListAdapter(context), query(filter, value));
  }

  @Override
  public RegisteredAttribute newInstance(Cursor c)
  {
    long id = c.getLong(c.getColumnIndexOrThrow(COLUMN_REGISTRY_ID));
    String name = c.getString(c.getColumnIndexOrThrow(COLUMN_REGISTRY_NAME));
    boolean active = c.getInt(c.getColumnIndexOrThrow(COLUMN_REGISTRY_ACTIVE)) > 0;
    int type = c.getInt(c.getColumnIndexOrThrow(COLUMN_REGISTRY_TYPE));
    String menu = c.getString(c.getColumnIndexOrThrow(COLUMN_REGISTRY_MENU));
    String clz = c.getString(c.getColumnIndexOrThrow(COLUMN_REGISTRY_CLASS));
    String param = c.getString(c.getColumnIndexOrThrow(COLUMN_REGISTRY_PARAM));
    int order = c.getInt(c.getColumnIndexOrThrow(COLUMN_REGISTRY_ORDER));
    return new RegisteredAttribute(id, name, type, menu, active, clz, param, order);
  }

  // public final int deleteAttribute(long id)
  // {
  // new ScheduleHelper(context).deleteProfile(id);
  // new AttributeHelper(context).deleteProfile(id);
  // return delete(FILTER_REGISTRY_ID, id);
  // }

}
