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

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.mgjg.ProfileManager.attribute.AttributeListAdapter;
import com.mgjg.ProfileManager.attribute.ProfileAttribute;
import com.mgjg.ProfileManager.attribute.ProfileAttributeFactory;
import com.mgjg.ProfileManager.registry.UnknownAttributeException;
import com.mgjg.ProfileManager.utils.ListAdapter;

import java.util.List;

import static com.mgjg.ProfileManager.provider.AttributeProvider.CONTENT_URI;

/**
 * Abstracts access to profile data in SQLite db
 *
 * @author Mike Partridge / Jay Goldman
 */
public class AttributeHelper extends ProfileManagerProviderHelper<ProfileAttribute>
{
  public static final String TABLE_ATTRIBUTE = "attributes";
  public static final String TABLE_ALIAS_ATTRIBUTE = "a";

  // values used as names of table columns and names of values passed via Intents
  public static final String COLUMN_ATTRIBUTE_ID = "_id";
  public static final String COLUMN_ATTRIBUTE_PROFILE_ID = "_profile_id";
  public static final String COLUMN_ATTRIBUTE_TYPE = "_type";
  public static final String COLUMN_ATTRIBUTE_INT_VALUE = "_volume";
  public static final String COLUMN_ATTRIBUTE_BOOL_VALUE = "_vibrate";
  public static final String COLUMN_ATTRIBUTE_SETTING = "_setting";

  // names of values passed via Intents
  public static final String INTENT_ATTRIBUTE_ID = "com.mgjg.ProfileManager.attribute.id";
  public static final String INTENT_ATTRIBUTE_TYPE = "com.mgjg.ProfileManager.attribute.type";
  public static final String INTENT_ATTRIBUTE_INT_VALUE = "com.mgjg.ProfileManager.attribute.int_value";
  public static final String INTENT_ATTRIBUTE_BOOL_VALUE = "com.mgjg.ProfileManager.attribute.bool_value";
  public static final String INTENT_ATTRIBUTE_SETTING = "com.mgjg.ProfileManager.attribute.setting";
  public static final String INTENT_ATTRIBUTE_PROFILE_ID = "com.mgjg.ProfileManager.attribute.profile.id";
  public static final String INTENT_ATTRIBUTE_PROFILE_NAME = "com.jgjg.ProfileManager.attribute.profile.name";

  public static final String DEFAULT_ORDER_ATTRIBUTE =
      COLUMN_ATTRIBUTE_PROFILE_ID + " desc, "
          + COLUMN_ATTRIBUTE_TYPE + " desc, "
          + COLUMN_ATTRIBUTE_INT_VALUE + " desc, "
          + COLUMN_ATTRIBUTE_BOOL_VALUE + " desc, "
          + COLUMN_ATTRIBUTE_SETTING + " desc, "
          + TABLE_ATTRIBUTE + "." + COLUMN_ATTRIBUTE_ID;

  public static final int FILTER_ATTRIBUTE_ID = 1;
  public static final int FILTER_ATTRIBUTE_PROFILE_ID = 2;
  public static final int FILTER_ATTRIBUTE_TYPE = 3;
  public static final int FILTER_ATTRIBUTE_PROFILE_TYPE = 4;
  public static final int FILTER_ATTRIBUTE_PROFILE_ACTIVE = 5;

  private static ProfileAttributeFactory factory;

  /**
   * allow for alternative implementation
   *
   * @param factory
   */
  public synchronized static void setProfileAttributeFactory(ProfileAttributeFactory factory)
  {
    AttributeHelper.factory = factory;
  }

  public synchronized static ProfileAttributeFactory getFactory()
  {
    return factory;
  }

  public AttributeHelper(Context context)
  {
    super(context);
  }

  @Override
  public Uri getContentUri()
  {
    return CONTENT_URI;
  }

  @Override
  public Uri getContentUri(int filter, Object... values)
  {
    final Uri uri = getContentUri();
    Uri urip;
    switch (filter)
    {

      case NO_FILTER:
        return uri;

      case FILTER_ATTRIBUTE_ID:
        return Uri.withAppendedPath(uri, String.valueOf(values[0]));

      case FILTER_ATTRIBUTE_PROFILE_ID:
        return Uri.withAppendedPath(Uri.withAppendedPath(uri, "profile"), String.valueOf(values[0]));

      case FILTER_ATTRIBUTE_TYPE:
        return Uri.withAppendedPath(Uri.withAppendedPath(uri, "type"), String.valueOf(values[0]));

      case FILTER_ALL_ACTIVE:
        return Uri.withAppendedPath(uri, "active");

      case FILTER_ATTRIBUTE_PROFILE_TYPE:
        urip = Uri.withAppendedPath(Uri.withAppendedPath(uri, "profile"), String.valueOf(values[0]));
        return Uri.withAppendedPath(Uri.withAppendedPath(urip, "type"), String.valueOf(values[1]));

      case FILTER_ATTRIBUTE_PROFILE_ACTIVE:
        urip = Uri.withAppendedPath(Uri.withAppendedPath(uri, "profile"), String.valueOf(values[0]));
        return Uri.withAppendedPath(urip, "active");

      default:
        throw new IllegalArgumentException("Unknown filter " + filter);
    }
  }

  @Override
  public final ListAdapter<ProfileAttribute> createListAdapter(int filter, Object... values)
  {
    return fillListAdapter(new AttributeListAdapter(context), query(filter, values));
  }

  @Override
  public ProfileAttribute newInstance(Cursor c) throws UnknownAttributeException
  {
    return getFactory().createInstance(context, c);
  }

  public final int deleteAttribute(Long id)
  {
    if (null == id)
    {
      return 0;
    }
    return delete(FILTER_ATTRIBUTE_ID, id);
  }

  public final int deleteProfile(Long id)
  {
    if (null == id)
    {
      return 0;
    }
    return delete(FILTER_ATTRIBUTE_PROFILE_ID, id);
  }

  /**
   * @param aa
   * @param profileId
   */
  public static void activate(Context context, Long profileId)
  {
    if (null != profileId)
    {
      AttributeHelper attrHelper = new AttributeHelper(context);
      List<ProfileAttribute> attrs = attrHelper.getList(FILTER_ATTRIBUTE_PROFILE_ID, profileId);
      for (ProfileAttribute attr : attrs)
      {
        attr.activate(context);
      }
    }
  }
}
