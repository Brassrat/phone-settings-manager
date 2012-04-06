/**
 * Copyright 2011 Jay Goldman
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package com.mgjg.ProfileManager.registry;

import static android.view.Menu.NONE;
import static com.mgjg.ProfileManager.provider.AttributeHelper.COLUMN_ATTRIBUTE_TYPE;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.SubMenu;

import com.mgjg.ProfileManager.R;
import com.mgjg.ProfileManager.attribute.AttributeBase;
import com.mgjg.ProfileManager.attribute.ProfileAttribute;
import com.mgjg.ProfileManager.attribute.ProfileAttributeFactoryImpl;
import com.mgjg.ProfileManager.provider.AttributeHelper;

public class AttributeRegistry
{

  // these need to go into a config file/table
  public final static int TYPE_AUDIO = 1000; /* reserved 1000 - 1009 */
  public final static int TYPE_XMIT = 1020; /* reserved 1020 - 1029 */

  private final Map<Integer, ProfileAttribute> RegisteredAttributesByType = new HashMap<Integer, ProfileAttribute>();
  private final Map<String, ProfileAttribute> RegisteredAttributesByName = new HashMap<String, ProfileAttribute>();
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
      initialized = true;
      AttributeHelper.setProfileAttributeFactory(ProfileAttributeFactoryImpl.createProfileAttributeFactory(context));
      // todo read registry data from table, for now just initialize the know builtin attributes
      for (String clz : new String[] {
          "com.mgjg.ProfileManager.attribute.builtin.sound.SoundAttribute",
          "com.mgjg.ProfileManager.attribute.builtin.xmit.AirPlaneAttribute" })
      {
        register(context, clz);
      }

    }
  }

  static void register(Context context, String clz)
  {
    try
    {
      @SuppressWarnings("unchecked")
      Class<AttributeBase> cl = (Class<AttributeBase>) Class.forName(clz);
      Method method = cl.getMethod("init", Context.class);
      AttributeBase[] attrs = (AttributeBase[]) method.invoke(null, context);
      register(context, attrs);
    }
    catch (Throwable t)
    {
      Log.e("com.mgjg", "Specified class is not valid " + clz, t);
    }
  }

  static void register(Context context, AttributeBase[] attrs)
  {
    for (AttributeBase attr : attrs)
    {
      registry.register(context, attr);
    }
  }

  synchronized void register(Context context, ProfileAttribute attr)
  {
    RegisteredAttributesByType.put(attr.getTypeId(), attr);
    RegisteredAttributesByName.put(attr.getName(context), attr);
  }

  public synchronized Set<Integer> registeredAttributes()
  {
    return new HashSet<Integer>(RegisteredAttributesByType.keySet());
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

  public void onCreateOptionsMenu(Activity activity, Menu menu)
  {
    // add selectable attributes to menu
    // MenuInflater inflater = activity.getMenuInflater();
    // inflater.inflate(R.menu.attributelist_options, menu);
    SubMenu attrMenu = menu.addSubMenu(activity.getString(R.string.AddAttribute));
    for (Map.Entry<String, ProfileAttribute> mape : RegisteredAttributesByName.entrySet())
    {
      ProfileAttribute attr = mape.getValue();
      attrMenu.add(NONE, attr.getTypeId(), NONE, attr.getNew(activity));
    }
    
    // add default menu entries...
    menu.add(NONE, R.id.done, NONE, activity.getString(R.string.done));
  }
}
