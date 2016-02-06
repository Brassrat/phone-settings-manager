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
package com.mgjg.ProfileManager.attribute;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.util.SparseIntArray;

import com.mgjg.ProfileManager.R;
import com.mgjg.ProfileManager.provider.AttributeHelper;
import com.mgjg.ProfileManager.provider.AttributeProvider;
import com.mgjg.ProfileManager.registry.UnknownAttributeException;

import java.util.HashMap;
import java.util.Map;

public class ActiveCount
{
  final SparseIntArray activeCount = new SparseIntArray();
  final CharSequence singular;
  final CharSequence plural;
  final CharSequence none;

  public ActiveCount(Activity aa)
  {

    singular = aa.getText(R.string.activeAttributeCount);
    plural = aa.getText(R.string.activeAttributeCountPlural);
    none = aa.getText(R.string.activeAttributeCountNone);

    /*
     * get all active attributes, count them by type,
     */
    AttributeHelper helper = new AttributeHelper(aa);
    Uri attributesUri = helper.getContentUri(AttributeProvider.FILTER_ALL_ACTIVE, (Object) null);
    Cursor cc = helper.getCursor(attributesUri);

    if (null != cc)
    {
      try
      {
        if (cc.moveToFirst())
        {

          do
          {
            ProfileAttribute attr;
            try
            {
              attr = helper.newInstance(cc);

              Integer type = attr.getTypeId();
              activeCount.put(type, activeCount.get(type, 0)+1);
            }
            catch (UnknownAttributeException e)
            {
              // ignore
            }
          }
          while (cc.moveToNext());

        }
      }
      finally
      {
        cc.close();
      }
    }
  }

  public String getCountText(Integer type)
  {
    final String result;

    if (activeCount.get(type, 0) > 0)
    {
      result = String.valueOf(activeCount.get(type)) + " " + ((activeCount.get(type) == 1) ? singular : plural);
    }
    else
    {
      result = String.valueOf(none);
    }

    return result;
  }

}