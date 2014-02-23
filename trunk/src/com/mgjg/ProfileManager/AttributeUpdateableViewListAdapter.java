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
package com.mgjg.ProfileManager;

import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.mgjg.ProfileManager.attribute.AttributeUpdatableView;
import com.mgjg.ProfileManager.attribute.ProfileAttribute;
import com.mgjg.ProfileManager.utils.AttributeTableLayout;
import com.mgjg.ProfileManager.utils.ListAdapter;

/**
 * Based on the tutorial from anddev.org at http://www.anddev.org/iconified_textlist_-_the_making_of-t97.html
 * 
 * @author Mike Partridge/ Jay Goldman
 */
public class AttributeUpdateableViewListAdapter extends
    ListAdapter<AttributeTableLayout>
{

  public static final LinearLayout.LayoutParams paramsFillWrap = new LinearLayout.LayoutParams(
      LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

  final List<AttributeTableLayout> layouts;

  /**
   * @param context
   */
  public AttributeUpdateableViewListAdapter(Context context,
      List<AttributeTableLayout> layouts)
  {
    super(context);
    this.layouts = layouts;
    for (AttributeTableLayout atl : layouts)
    {
      addItem(atl);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
   */
  @Override
  public View getView(int position, View convertView, ViewGroup parent)
  {

    AttributeUpdatableView updatableView;

    ProfileAttribute attr = getItem(position).getAttribute();

    /*
     * if the View already exists, set its values; otherwise give it the Attribute to put them itself
     */
    if ((convertView != null) && (convertView instanceof AttributeUpdatableView)
        && ((AttributeUpdatableView) convertView).copyToView(context, attr))
    {
      updatableView = (AttributeUpdatableView) convertView;
      // assume it's already in layouts ...
    }
    else
    {
      Iterator<AttributeTableLayout> i = layouts.iterator();
      while (i.hasNext())
      {
        AttributeTableLayout atl = i.next();
        if (atl.getAttribute().equals(attr))
        {
          return atl.getView();
        }
      }
      updatableView = new AttributeUpdatableView(context, attr, layouts);
      layouts.add(new AttributeTableLayout(attr, updatableView));
    }

    return updatableView;
  }

}
