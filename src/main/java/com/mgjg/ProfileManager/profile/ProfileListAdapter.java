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
package com.mgjg.ProfileManager.profile;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.mgjg.ProfileManager.utils.ListAdapter;

/**
 * Based on the tutorial from anddev.org at http://www.anddev.org/iconified_textlist_-_the_making_of-t97.html
 *
 * @author Mike Partridge/ Jay Goldman
 */
public class ProfileListAdapter extends ListAdapter<Profile>
{

  /**
   * @param context
   */
  public ProfileListAdapter(Context context)
  {
    super(context);
  }

  /*
   * (non-Javadoc)
   *
   * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
   */
  public View getView(int position, View convertView, ViewGroup parent)
  {

    final ProfileView view;

    final Profile profile = getItem(position);

    /*
     * if the View already exists, set its values; otherwise give it the Profile to pull them itself
     */
    if (convertView != null && (convertView instanceof ProfileView))
    {
      view = (ProfileView) convertView;
      view.setFromProfile(profile);
    }
    else
    {
      view = new ProfileView(context, profile);
    }

    return view;
  }

}
