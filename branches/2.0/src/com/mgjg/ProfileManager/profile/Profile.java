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
package com.mgjg.ProfileManager.profile;

import static com.mgjg.ProfileManager.provider.ProfileHelper.COLUMN_PROFILE_ACTIVE;
import static com.mgjg.ProfileManager.provider.ProfileHelper.COLUMN_PROFILE_NAME;
import static com.mgjg.ProfileManager.provider.ProfileHelper.COLUMN_PROFILE_TYPE;
import android.content.ContentValues;

import com.mgjg.ProfileManager.utils.Listable;

/**
 * 
 * @author Jay Goldman
 * 
 */
public class Profile implements Listable
{

  private long id;
  private String name;
  private final int type;
  private boolean active;

  public Profile(long id, String name, int type, boolean active)
  {
    this.id = id;
    this.name = name;
    this.type = type;
    this.active = active;
  }

  /**
   * @return the id
   */
  @Override
  public long getId()
  {
    return id;
  }

  @Override
  public void setId(long id)
  {
    this.id = id;
  }

  public String getName()
  {
    return name;
  }

  public int getType()
  {
    return type;
  }

  public boolean isActive()
  {
    return active;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public void setActive(boolean active)
  {
    this.active = active;
  }

  /**
   * Required for use in a ListAdapter; indicates that this is selectable and clickable
   * 
   * @return the mEnabled
   */
  @Override
  public boolean isEnabled()
  {
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object o)
  {
    boolean result = false;

    if (o instanceof Profile)
    {
      Profile compare = (Profile) o;
      result = (this.id > 0 && this.id == compare.getId());
    }

    return result;
  }

  @Override
  public int hashCode()
  {
    return (int) id;
  }

  @Override
  public ContentValues makeValues()
  {
    ContentValues values = new ContentValues();

    values.put(COLUMN_PROFILE_ACTIVE, isActive() ? "1" : "0");
    values.put(COLUMN_PROFILE_TYPE, getType());
    values.put(COLUMN_PROFILE_NAME, getName());
    return values;
  }

  @Override
  public int compareTo(Listable another)
  {
    if (another instanceof Profile)
    {
      return name.compareTo(((Profile) another).name);
    }
    int thisOrder = getListOrder();
    int othOrder = another.getListOrder();
    // we know that thisOrder and othOrder are small integers so can just subtract to fulfill compareTo contract
    return thisOrder - othOrder;
  }

  @Override
  public int getListOrder()
  {
    return 0;
  }
}
