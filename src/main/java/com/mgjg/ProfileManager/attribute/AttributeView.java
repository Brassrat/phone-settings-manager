/**
 * Copyright 2009 Mike Partridge
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
import android.widget.LinearLayout;
import android.widget.TableLayout;

/**
 * Defines layout wrapper for Attribute list items
 *
 * @author Mike Partridge/ Jay Goldman
 */
public final class AttributeView extends LinearLayout
{

  // convenience for addView calls later
  // LinearLayout.LayoutParams paramsWrapBoth = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
  public static final LinearLayout.LayoutParams paramsFillWrap = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

  private final ProfileAttribute attr;

  /**
   * used by ide tools, should never be used in runtime code
   * @param context
   */
  public AttributeView(Context context)
  {
    super(context);
    attr = null;
    if (!isInEditMode())
    {
      throw new UnsupportedOperationException("Can not construct AttributeView without ProfileAttribute");
    }
  }

  /**
   * @param context
   * @param aAttr
   */
  public AttributeView(Context context, ProfileAttribute aAttr)
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
  public AttributeView copyToView(Context context, ProfileAttribute aAttr)
  {
    // try to copy specified ProfileAttribute to this view's ProfileAttribute;
    // if aAttr is not compatible with attr create a new View
    if (!attr.copy(aAttr))
    {
      return new AttributeView(context, aAttr);
    }
    return this;
  }

}
