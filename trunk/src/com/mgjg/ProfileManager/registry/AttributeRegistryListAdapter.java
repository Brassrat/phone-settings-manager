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
package com.mgjg.ProfileManager.registry;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.mgjg.ProfileManager.utils.ListAdapter;

/**
 * Based on the tutorial from anddev.org at http://www.anddev.org/iconified_textlist_-_the_making_of-t97.html
 * 
 * @author Jay Goldman
 */
public class AttributeRegistryListAdapter extends ListAdapter<RegisteredAttribute>
{

  /**
   * @param context
   */
  public AttributeRegistryListAdapter(Context context)
  {
    super(context);
  }

  /*
   * (non-Javadoc)
   * 
   * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
   */
  @SuppressWarnings("unchecked")
  @Override
  public View getView(int position, View convertView, ViewGroup parent)
  {

    AttributeRegistryView<RegisteredAttribute> attributeView;

    RegisteredAttribute attr = getItem(position);

    /*
     * if the View already exists, set its values; otherwise give it the Attribute to pull them itself
     */
    if (convertView != null && (convertView instanceof AttributeRegistryView))
    {
      attributeView = (AttributeRegistryView<RegisteredAttribute>) convertView;
      attributeView = attributeView.copyToView(context, attr);
    }
    else
    {
      attributeView = new AttributeRegistryView<RegisteredAttribute>(context, attr);
    }

    return attributeView;
  }

}
