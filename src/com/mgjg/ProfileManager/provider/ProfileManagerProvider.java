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

import java.text.MessageFormat;
import java.util.Map;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.mgjg.ProfileManager.utils.Listable;
import com.mgjg.ProfileManager.utils.SQLiteDatabaseHelper;

public abstract class ProfileManagerProvider<T extends Listable> extends ContentProvider
{

  public static final int NO_FILTER = 0;
  public static final int FILTER_ALL_ACTIVE = 10;

  private SQLiteDatabaseHelper dbHelper;
  private boolean mayUpgrade = true;

  /*
   * (non-Javadoc)
   * 
   * @see android.content.ContentProvider#onCreate()
   */
  @Override
  public final boolean onCreate()
  {
    dbHelper = new SQLiteDatabaseHelper(getContext());
    return (dbHelper == null) ? false : true;
  }

  protected static Uri createTableUri(String authority, String table)
  {
    return Uri.parse("content://" + authority + "/" + table);
  }

  /**
   * checks that the specified initial values are valid for this content
   * 
   * @param initialValues
   * 
   * @throws IllegalArgumentException
   *           if specified values are not valid
   */
  protected abstract void checkInitialValues(ContentValues initialValues);

  /*
   * (non-Javadoc)
   * 
   * @see android.content.ContentProvider#insert(android.net.Uri, android.content.ContentValues)
   */
  @Override
  public final Uri insert(Uri uri, ContentValues initialValues)
  {
    // Only the base NOFILTER URI is allowed for inserts
    if ((null != uri) && (getUriMatcher().match(uri) != NO_FILTER))
    {
      throw new IllegalArgumentException("Invalid URI " + uri);
    }

    checkInitialValues(initialValues);

    /*
     * do the insert
     */
    SQLiteDatabase db = dbHelper.getWritableDatabase();
    mayUpgrade = false;
    String table = getTable(NO_FILTER);
    long rowId = db.insert(table, null, initialValues);
    if (rowId > 0)
    {
      Uri noteUri = ContentUris.withAppendedId(getContentUri(), rowId);
      getContext().getContentResolver().notifyChange(noteUri, null);
      return noteUri;
    }

    throw new SQLException("Failed to insert row into " + table);
  }

  /*
   * (non-Javadoc)
   * 
   * @see android.content.ContentProvider#delete(android.net.Uri, java.lang.String, java.lang.String[])
   */
  @Override
  public final int delete(Uri uri, String where, String[] whereArgs)
  {
    int matchedCode;
    if ((matchedCode = getUriMatcher().match(uri)) > NO_FILTER)
    {
      StringBuilder whereClause = new StringBuilder();
      String[] uf = getUpdateField(matchedCode);
      for (int ii = 0; ii < uf.length; ++ii)
      {
        if (ii > 0)
        {
          whereClause.append(" AND ");
        }
        whereClause.append("(").append(uf[ii]).append(" = ?)");
      }
      whereArgs = getMatchedValue(uri, matchedCode);
      if (uf.length != whereArgs.length)
      {
        String msg = MessageFormat.format("{2}: Number of update fields ({0}) does not match number of values ({1})",
            new Object[] { uf.length, whereArgs.length, uri });
        throw new IllegalArgumentException(msg);
      }
      int count = dbHelper.getWritableDatabase().delete(getTable(matchedCode), whereClause.toString(), whereArgs);
      mayUpgrade = false;
      getContext().getContentResolver().notifyChange(uri, null);
      return count;
    }
    throw new IllegalArgumentException("Invalid URI " + uri);
  }

  /*
   * (non-Javadoc)
   * 
   * @see android.content.ContentProvider#update(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[])
   */
  @Override
  public final int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
  {
    int matchedCode;
    if ((matchedCode = getUriMatcher().match(uri)) > NO_FILTER)
    {
      StringBuilder whereClause = new StringBuilder();
      String[] uf = getUpdateField(matchedCode);
      for (int ii = 0; ii < uf.length; ++ii)
      {
        if (ii > 0)
        {
          whereClause.append(" AND ");
        }
        whereClause.append("(").append(uf[ii]).append(" = ?)");
      }
      String[] whereArgs = getMatchedValue(uri, matchedCode);
      if (uf.length != whereArgs.length)
      {
        String msg = MessageFormat.format("{2}: Number of update fields ({0}) does not match number of values ({1})",
            new Object[] { uf.length, whereArgs.length, uri });
        throw new IllegalArgumentException(msg);
      }
      int count = dbHelper.getWritableDatabase().update(getTable(matchedCode), values, whereClause.toString(), whereArgs);
      mayUpgrade = false;
      getContext().getContentResolver().notifyChange(uri, null);
      return count;
    }
    throw new IllegalArgumentException("Invalid URI " + uri);
  }

  /*
   * (non-Javadoc)
   * 
   * @see android.content.ContentProvider#query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String)
   */
  @Override
  public final Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
  {
    SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

    int matchedCode;

    if ((matchedCode = getUriMatcher().match(uri)) >= 0)
    {

      // If no sort order is specified use the default
      String orderBy = TextUtils.isEmpty(sortOrder) ? getDefaultSortOrder(matchedCode) : sortOrder;

      // run the query
      qb.setTables(getTable(matchedCode));
      qb.setProjectionMap(getProjectionMap(matchedCode));
      if (matchedCode > NO_FILTER)
      {
        String[] qf = getQueryField(matchedCode);
        String[] mv = getMatchedValue(uri, matchedCode);
        if (qf.length != mv.length)
        {
          String msg = MessageFormat.format("{2}: Number of query fields ({0}) does not match number of values ({1})",
              new Object[] { qf.length, mv.length, uri });
          throw new IllegalArgumentException(msg);
        }

        StringBuilder where = new StringBuilder();
        for (int ii = 0; ii < qf.length; ++ii)
        {
          if (ii > 0)
          {
            where.append(" AND ");
          }
          // TODO - this only works for numeric values ...
          where.append("(").append(qf[ii]).append(" = ").append((null == selection) ? "?" : mv[ii]).append(")");
        }
        String wh = where.toString();
        if (Log.isLoggable("com.mgjg.ProfileManager", Log.DEBUG))
        {
          Log.d("com.mgjg.ProfileManager", wh);
        }
        if ((null != selection))
        {
          qb.appendWhere(wh);
        }
        else
        {
          selection = wh;
          selectionArgs = mv;
        }
      }

      try
      {
        if (mayUpgrade)
        {
          mayUpgrade = false;
          dbHelper.getWritableDatabase();
        }
        Cursor c = qb.query(dbHelper.getReadableDatabase(), projection, selection, selectionArgs, null, null, orderBy); // Tell the cursor what uri to watch, so it knows when its source data changes
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
      }
      catch (RuntimeException e)
      {
        Log.e("com.mgjg.ProfileManager", "query failed " + e.getMessage());
        throw e;
      }
    }

    throw new IllegalArgumentException("Invalid URI " + uri);
  }

  protected abstract Uri getContentUri();

  protected abstract UriMatcher getUriMatcher();

  protected abstract String getType(int matchedCode);

  /*
   * (non-Javadoc)
   * 
   * @see android.content.ContentProvider#getType(android.net.Uri)
   */
  @Override
  public String getType(Uri uri)
  {
    return getType(getUriMatcher().match(uri));
  }

  protected abstract String getTable(int matchedCode);

  protected abstract String[] getUpdateField(int matchedCode);

  protected abstract String[] getQueryField(int matchedCode);

  /**
   * returns value from Uri path segments for specified uri match code, mapped to the appropriate database value
   * 
   * @param matchedCode
   * @param index
   *          which variable segment to return data for, 1-based
   * @return value from Uri path segments for specified uri match code
   */
  protected abstract String[] getMatchedValue(Uri uri, int matchedCode);

  protected abstract String getDefaultSortOrder(int matchedCode);

  protected abstract Map<String, String> getProjectionMap(int matchedCode);

}
