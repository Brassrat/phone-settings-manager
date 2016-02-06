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

import android.content.Context;
import android.widget.TableLayout;

import com.mgjg.ProfileManager.utils.AttributeTableLayout;

import java.util.List;

/**
 * Defines layout wrapper for Attribute list items
 *
 * @author Mike Partridge/ Jay Goldman
 */
public final class AttributeUpdatableView extends TableLayout
{

  private final ProfileAttribute attr;

  /**
   * used by ide tools, should never be used in runtime code
   * @param context
   */
  public AttributeUpdatableView(Context context)
  {
    super(context);
    attr = null;
    if (!isInEditMode())
    {
      throw new UnsupportedOperationException("Can not construct AttributeUpdatableView without ProfileAttribute");
    }
  }

  /**
   * @param context
   * @param aAttr
   * @param layouts
   */
  public AttributeUpdatableView(Context context, ProfileAttribute aAttr, List<AttributeTableLayout> layouts)
  {
    super(context);
    attr = aAttr;

    setOrientation(VERTICAL);
    setColumnStretchable(1, true);
    attr.addUpdatableView(context, this, layouts);
    attr.updateView(context, this);
  }

  public boolean copyToView(Context context, ProfileAttribute aAttr)
  {
    if (attr == aAttr)
    {
      attr.updateView(context, this);
      return true;
    }
    else if (attr.equals(aAttr))
    {
      attr.updateView(context, this);
      return true;
    }
    return false;
  }

  @Override
  public boolean equals(Object o)
  {
    return ((o instanceof AttributeUpdatableView) && ((AttributeUpdatableView) o).attr.equals(attr));
  }

  @Override
  public int hashCode()
  {
    return attr.hashCode();
  }
}
