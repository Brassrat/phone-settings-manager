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
package com.mgjg.ProfileManager.utils;

import android.content.Context;
import android.database.Cursor;
import android.widget.BaseAdapter;

import com.mgjg.ProfileManager.provider.ProfileManagerProviderHelper;
import com.mgjg.ProfileManager.registry.UnknownAttributeException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Based on the tutorial from anddev.org at http://www.anddev.org/iconified_textlist_-_the_making_of-t97.html
 *
 * @author Mike Partridge
 */
public abstract class ListAdapter<T extends Listable> extends BaseAdapter
{

  protected final Context context;
  private final List<T> mItems = new ArrayList<>();

  /**
   * @param context
   */
  public ListAdapter(Context aContext)
  {
    context = aContext;
  }

  /*
   * (non-Javadoc)
   *
   * @see android.widget.Adapter#getCount()
   */
  @Override
  public final int getCount()
  {
    return mItems.size();
  }

  /*
   * (non-Javadoc)
   *
   * @see android.widget.Adapter#getItem(int)
   */
  @Override
  public final T getItem(int position)
  {
    return mItems.get(position);
  }

  /*
   * (non-Javadoc)
   *
   * @see android.widget.Adapter#getItemId(int)
   */
  @Override
  public final long getItemId(int position)
  {
    return mItems.get(position).getId();
  }

  /**
   * @param s
   */
  public void addItem(T s)
  {
    mItems.add(s);
  }

  public void addItem(ProfileManagerProviderHelper<T> helper, Cursor cc) throws UnknownAttributeException
  {
    mItems.add(helper.newInstance(cc));
  }

  /*
   * (non-Javadoc)
   *
   * @see android.widget.BaseAdapter#isEnabled(int)
   */
  @Override
  public boolean isEnabled(int position)
  {
    try
    {
      return mItems.get(position).isEnabled();
    }
    catch (IndexOutOfBoundsException e)
    {
      return super.isEnabled(position);
    }
  }

  public void sort()
  {
    Collections.sort(mItems);
  }
}
