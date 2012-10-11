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
package com.mgjg.ProfileManager.provider;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.mgjg.ProfileManager.registry.UnknownAttributeException;
import com.mgjg.ProfileManager.utils.ListAdapter;
import com.mgjg.ProfileManager.utils.Listable;

/**
 * Provides ProfileManager specific operations on the various {@code Listable} objects shown in the GUI that are represented by entries in the database
 * 
 * @param <T>
 */
public abstract class ProfileManagerProviderHelper<T extends Listable>
{

  public static final int NO_FILTER = 0;
  public static final int FILTER_ALL_ACTIVE = 10;

  protected Context context;

  protected static Uri createTableUri(String authority, String table)
  {
    return Uri.parse("content://" + authority + "/" + table);
  }

  protected ProfileManagerProviderHelper(Context context)
  {
    this.context = context;
  }

  public Uri insertEntry(T entry)
  {
    Uri noteUri = context.getContentResolver().insert(getContentUri(), entry.makeValues());
    setId(entry, noteUri);
    return noteUri;
  }

  public final Uri insert(ContentValues initialValues)
  {
    return context.getContentResolver().insert(getContentUri(NO_FILTER, (Object) null), initialValues);
  }

  public final int delete(int filter, long id)
  {
    return context.getContentResolver().delete(getContentUri(filter, id), null, null);
  }

  /**
   * retrieves the record Id from a Uri
   * 
   * @param noteUri
   * @return
   */
  public long getId(Uri noteUri)
  {
    return Long.parseLong(noteUri.getPathSegments().get(1));
  }

  /**
   * update specified entry with id extracted from specified Uri
   * 
   * @param entry
   * @param noteUri
   * @return
   */
  public long setId(T entry, Uri noteUri)
  {
    entry.setId(getId(noteUri));
    return entry.getId();
  }

  protected Cursor query(int filter, Object... values)
  {
    return query(getContentUri(filter, values));
  }

  protected Cursor query(Uri uri)
  {
    return context.getContentResolver().query(uri, null, null, null, null);
  }

  public final int update(int filter, Object filterValue, ContentValues values)
  {
    return context.getContentResolver().update(getContentUri(filter, filterValue), values, null, null);
  }

  public Cursor getCursor(Uri uri)
  {
    return context.getContentResolver().query(uri, null, null, null, null);
  }

  public List<T> getList(int filter, Object... values)
  {
    Cursor cc = null;
    try
    {
      cc = getCursor(getContentUri(filter, values));
      return getEntries(cc);
    }
    finally
    {
      if (null != cc)
      {
        cc.close();
      }
    }
  }

  public abstract ListAdapter<T> createListAdapter(int filter, Object... value);

  /**
   * fill list using query cursor
   * 
   * @param la
   *          {@code ListAdapter}
   * @param c
   *          {@code Cursor}
   * @return the input {@code ListAdapter} to allow chaining of calls
   */
  public ListAdapter<T> fillListAdapter(ListAdapter<T> la, Cursor c)
  {

    if (c.moveToFirst())
    {
      do
      {
        try
        {
          la.addItem(this, c);
        }
        catch (UnknownAttributeException e)
        {
          // skip this
          Log.e("com.mgjg.ProfileManager", e.getMessage());
        }
      } while (c.moveToNext());
    }
    la.sort();
    return la;
  }

  protected List<T> getEntries(Cursor cc)
  {
    List<T> attrs = new ArrayList<T>();

    if ((null != cc) && cc.moveToFirst())
    {
      do
      {
        try
        {
          attrs.add(newInstance(cc));
        }
        catch (UnknownAttributeException e)
        {
          // ignore ???
        }
      } while (cc.moveToNext());
    }
    return attrs;
  }

  public abstract T newInstance(Cursor c) throws UnknownAttributeException;

  public abstract Uri getContentUri();

  public abstract Uri getContentUri(int filter, Object... values);

}
