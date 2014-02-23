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
package com.mgjg.ProfileManager.attribute.builtin.xmit;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.mgjg.ProfileManager.attribute.JSONBooleanAttribute;
import com.mgjg.ProfileManager.registry.AttributeRegistry;
import com.mgjg.ProfileManager.registry.RegisteredAttribute;
import com.mgjg.ProfileManager.services.UnknownServiceException;

public final class XmitAttribute extends JSONBooleanAttribute
{

  public static final String XMIT_ATTRIBUTE_CLASS = "com.mgjg.ProfileManager.attribute.builtin.xmit.XmitAttribute";

  public XmitAttribute(Context context, String registryDefinition) throws JSONException, UnknownServiceException
  {
    super(context, registryDefinition);
  }

  private static String makeRegistryJSON(String serviceName, int typeId, String name, int order)
  {
    return String.format("{ \"service\" : \"%1$s\", \"id\" : \"%2$d\", \"name\" : \"%3$s\", \"order\" : \"%4$d\"}",
        serviceName, typeId, name, order);
  }

  private static RegisteredAttribute mkRegisteredAttribute(String name, int typex)
  {
    final int typeId = AttributeRegistry.TYPE_XMIT + typex;
    final String params = makeRegistryJSON(name, typeId, name, 10 + typex);
    return new RegisteredAttribute(0, name, typeId, true, XMIT_ATTRIBUTE_CLASS, params, 10 + typex);
  }

  private static final String[] builtins = { "AirPlane", "WiFi", "MobileData" };

  public static List<RegisteredAttribute> addRegistryEntries(SQLiteDatabase db)
  {
    final List<RegisteredAttribute> ras = new ArrayList<RegisteredAttribute>();
    for (int ii = 0; ii < builtins.length; ++ii)
    {
      ras.add(mkRegisteredAttribute(builtins[ii], ii));
    }
    return ras;

  }
}
