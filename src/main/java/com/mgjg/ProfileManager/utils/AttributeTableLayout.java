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
package com.mgjg.ProfileManager.utils;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;

import com.mgjg.ProfileManager.attribute.ActiveCount;
import com.mgjg.ProfileManager.attribute.AttributeUpdatableView;
import com.mgjg.ProfileManager.attribute.ProfileAttribute;

public class AttributeTableLayout implements Listable, Comparable<Listable>
{

  private final ProfileAttribute attribute;
  private final AttributeUpdatableView view;

  public AttributeTableLayout(ProfileAttribute attribute, AttributeUpdatableView view)
  {
    super();
    this.attribute = attribute;
    this.view = view;
  }

  @Override
  public ContentValues makeValues()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setId(long id)
  {
    attribute.setId(id);
  }

  @Override
  public long getId()
  {
    return attribute.getId();
  }

  @Override
  public boolean isEnabled()
  {
    return attribute.isEnabled();
  }

  public ProfileAttribute getAttribute()
  {
    return attribute;
  }

  public final AttributeUpdatableView getView()
  {
    return view;
  }

  public void setStatusText(Activity aa, ActiveCount activeCount)
  {
    attribute.setStatusText(aa, view, activeCount);
  }

  public void updateView(Context aa)
  {
    attribute.updateView(aa, view);
  }

  public int getListOrder()
  {
    return attribute.getListOrder();
  }

  @Override
  public int compareTo(Listable another)
  {
    int thisOrder = getListOrder();
    int othOrder = another.getListOrder();
    // we know that thisOrder and othOrder are small integers so can just subtract to fulfill compareTo contract
    return thisOrder - othOrder;
  }
}
