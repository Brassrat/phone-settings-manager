/**
 * Copyright 2009 Mike Partridge
 * Copyright 2012 Jay Goldman
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
package com.mgjg.ProfileManager.registry;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TableLayout;

import com.mgjg.ProfileManager.utils.Viewable;

/**
 * Defines layout wrapper for Attribute Registry list items
 *
 * @author Jay Goldman
 */
public final class AttributeRegistryView<T extends Viewable<T>> extends LinearLayout
{

  // convenience for addView calls later
  // LinearLayout.LayoutParams paramsWrapBoth = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
  public static final LinearLayout.LayoutParams paramsFillWrap = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

  private final T attr;

  /**
   * used by ide tools, should never be used in runtime code
   * @param context
   */
  public AttributeRegistryView(Context context)
  {
    super(context);
    attr = null;
    if (!isInEditMode())
    {
      throw new UnsupportedOperationException("Can not construct AttributeRegistryView without ProfileAttribute");
    }
  }

  /**
   * @param context
   * @param aAttr
   */
  public AttributeRegistryView(Context context, T aAttr)
  {
    super(context);
    this.attr = aAttr;

    this.setOrientation(VERTICAL);

    TableLayout tableLayout = new TableLayout(context);
    tableLayout.setColumnStretchable(1, true);

    attr.addView(context, tableLayout);
    addView(tableLayout, paramsFillWrap);
  }

  /**
   * @param aAttr
   */
  public AttributeRegistryView<T> copyToView(Context context, T aAttr)
  {
    // try to copy specified ProfileAttribute to this view's ProfileAttribute;
    // if aAttr is not compatible with attr create a new View
    if (!attr.copy(aAttr))
    {
      return new AttributeRegistryView<>(context, aAttr);
    }
    return this;
  }

}
