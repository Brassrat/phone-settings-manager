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
package com.mgjg.ProfileManager.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author Mike Partridge /Jay Goldman
 * 
 */
public class SQLiteDatabaseHelper extends SQLiteOpenHelper
{

  private static final String DATABASE_NAME = "data";
  private static final int DATABASE_VERSION = 4;

  public SQLiteDatabaseHelper(Context context)
  {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  /*
   * (non-Javadoc)
   * 
   * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite .SQLiteDatabase)
   */
  @Override
  public void onCreate(SQLiteDatabase db)
  {
    com.mgjg.ProfileManager.provider.AttributeRegistryProvider.createTable(db);
    com.mgjg.ProfileManager.provider.ProfileProvider.createTable(db);
    com.mgjg.ProfileManager.provider.ScheduleProvider.createTable(db);
    com.mgjg.ProfileManager.provider.AttributeProvider.createTable(db);
  }

  /*
   * (non-Javadoc)
   * 
   * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite .SQLiteDatabase, int, int)
   */
  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
  {
    Log.w(SQLiteDatabaseHelper.class.toString(),
        "Upgrading database from version " + oldVersion + " to " + newVersion + ".");

    com.mgjg.ProfileManager.provider.AttributeRegistryProvider.onUpgrade(db, oldVersion, newVersion);
    com.mgjg.ProfileManager.provider.ProfileProvider.onUpgrade(db, oldVersion, newVersion);
    com.mgjg.ProfileManager.provider.ScheduleProvider.onUpgrade(db, oldVersion, newVersion);
    com.mgjg.ProfileManager.provider.AttributeProvider.onUpgrade(db, oldVersion, newVersion);
    
    Log.w(SQLiteDatabaseHelper.class.toString(), "Upgrade completed.");
  }

}
