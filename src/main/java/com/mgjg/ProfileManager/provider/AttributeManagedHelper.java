/**
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

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;

import com.mgjg.ProfileManager.attribute.ProfileAttribute;

import java.util.List;

public class AttributeManagedHelper extends ManagedProviderHelper<ProfileAttribute>
{
  private final AttributeHelper helper;

  public AttributeManagedHelper(Activity activity)
  {
    super(activity);
    helper = new AttributeHelper(activity);
  }

  @Override
  public Uri getContentUri(int filter, Object... values)
  {
    return helper.getContentUri(filter, values);
  }

  @Override
  protected List<ProfileAttribute> getEntries(Cursor cc)
  {
    return helper.getEntries(cc);
  }

}
