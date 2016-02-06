/**
 * Copyright 2011 Jay Goldman
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package com.mgjg.ProfileManager.registry;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.ListView;

import com.mgjg.ProfileManager.R;
import com.mgjg.ProfileManager.attribute.ProfileAttribute;
import com.mgjg.ProfileManager.attribute.ProfileAttributeFactoryImpl;
import com.mgjg.ProfileManager.provider.AttributeHelper;
import com.mgjg.ProfileManager.provider.AttributeRegistryHelper;
import com.mgjg.ProfileManager.utils.Util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static android.view.Menu.NONE;
import static com.mgjg.ProfileManager.provider.AttributeHelper.COLUMN_ATTRIBUTE_TYPE;
import static com.mgjg.ProfileManager.provider.ProfileManagerProvider.FILTER_ALL_ACTIVE;

public class AttributeRegistry
{

  // these need to go into a config file/table
  public final static int TYPE_AUDIO = 1000; /* reserved 1000 - 1009 */
  public final static int TYPE_XMIT = 1020; /* reserved 1020 - 1029 */

  private final Map<Integer, ProfileAttribute> RegisteredAttributesByType = new HashMap<>();
  private final Map<String, ProfileAttribute> RegisteredAttributesByName = new HashMap<>();
  // private final Map<Integer, Integer> RegisteredAttributesByNewId = new HashMap<Integer, Integer>();

  private final static AttributeRegistry registry = new AttributeRegistry();
  private static boolean initialized;

  private AttributeRegistry()
  {
    // nothing to do, someone has to call init with a context
  }

  public static AttributeRegistry getInstance()
  {
    return registry;
  }

  public static synchronized void init(Context context)
  {
    if (!initialized)
    {
      AttributeHelper.setProfileAttributeFactory(ProfileAttributeFactoryImpl.createProfileAttributeFactory(context));
      AttributeRegistryHelper helper = new AttributeRegistryHelper(context);
      List<RegisteredAttribute> aa = helper.getList(FILTER_ALL_ACTIVE);
      for (RegisteredAttribute ra : aa)
      {
        Log.v(Util.LOG_TAG, "Processing registered attribute " + ra.getId());
        ra.register(context, registry);
      }
      initialized = true;
    }
  }

  synchronized void register(Context context, ProfileAttribute attr)
  {
    RegisteredAttributesByType.put(attr.getTypeId(), attr);
    RegisteredAttributesByName.put(attr.getName(context), attr);
  }

  public synchronized Set<Integer> registeredAttributes()
  {
    return new HashSet<>(RegisteredAttributesByType.keySet());
  }

  public synchronized int getType(String attributeName) throws UnknownAttributeException
  {
    if (RegisteredAttributesByName.containsKey(attributeName))
    {
      return RegisteredAttributesByName.get(attributeName).getTypeId();
    }
    throw new UnknownAttributeException(attributeName);
  }

  public synchronized boolean isType(int type)
  {
    return RegisteredAttributesByType.containsKey(type);
  }

  public synchronized ProfileAttribute getAttribute(int type) throws UnknownAttributeException
  {
    if (isType(type))
    {
      return RegisteredAttributesByType.get(type);
    }
    throw new UnknownAttributeException(type);
  }

  public ProfileAttribute createInstance(Context context, int type, long profileId) throws UnknownAttributeException
  {
    return getAttribute(type).createInstance(context, profileId);
  }

  public ProfileAttribute createInstance(Context context, Cursor c) throws UnknownAttributeException
  {
    int type = c.getInt(c.getColumnIndexOrThrow(COLUMN_ATTRIBUTE_TYPE));
    return getAttribute(type).createInstance(context, c);
  }

  public SubMenu onCreateOptionsMenu(Activity activity, Menu menu)
  {
    // add sub-menu for selectable attributes
    SubMenu attrsMenu = menu.addSubMenu(activity.getString(R.string.AddAttribute));
    // add default menu entries...
    // menu.add(NONE, R.id.done, NONE, activity.getString(R.string.done));
    return attrsMenu;
  }

  /**
   * add sub-menu of
   *
   * @param activity
   * @param menu
   * @param lv
   * @return
   */
  public MenuItem onPrepareOptionsMenu(Activity activity, Menu menu, ListView lv)
  {
    // add selectable attributes to menu
    MenuItem menuItem = menu.getItem(0);
    SubMenu attrMenu = menuItem.getSubMenu();
    attrMenu.clear();
    for (ProfileAttribute attr : RegisteredAttributesByName.values())
    {
      if (!isInList(lv, attr.getTypeId()))
      {
        attrMenu.add(NONE, attr.getTypeId(), attr.getListOrder(), attr.getName(activity));
      }
    }
    return menuItem;
  }

  private boolean isInList(ListView lv, int typeId)
  {
    int xx = lv.getChildCount();
    for (int pp = 0; pp < xx; ++pp)
    {
      if (typeId == ((ProfileAttribute) lv.getItemAtPosition(pp)).getTypeId())
      {
        return true;
      }
    }
    return false;
  }
}
