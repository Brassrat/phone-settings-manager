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

public class XmitAttribute extends JSONBooleanAttribute
{
 
  public XmitAttribute(Context context, String registryDefinition) throws JSONException, UnknownServiceException
  {
    super(context, registryDefinition);
  }

  private static String makeRegistryJSON(Context context, String serviceName, int typeId, String name, int order)
  {
    return String.format("{ \"service\" : \"%1$s\", \"id\" : \"%2$d\", \"name\" : \"%3$s\", \"order\" : \"%4$d\"}",
        serviceName, AttributeRegistry.TYPE_XMIT + typeId, name, order);
  }

  public static List<RegisteredAttribute> addRegistryEntries(SQLiteDatabase db)
  {
    List<RegisteredAttribute> ras = new ArrayList<RegisteredAttribute>();
    String params = makeRegistryJSON(null, "AirPlane", 0, "AirPlane", 10);
    ras.add(new RegisteredAttribute(0, "AirPlane", AttributeRegistry.TYPE_XMIT + 0,
        true, "com.mgjg.ProfileManager.attribute.builtin.xmit.XmitAttribute", params, 10));

    params = makeRegistryJSON(null, "WiFi", 1, "WiFi", 11);
    ras.add(new RegisteredAttribute(0, "WiFi", AttributeRegistry.TYPE_XMIT + 1,
        true, "com.mgjg.ProfileManager.attribute.builtin.xmit.XmitAttribute", params, 11));

    params = makeRegistryJSON(null, "MobileData", 2, "MobileData", 12);
    ras.add(new RegisteredAttribute(0, "MobileData", AttributeRegistry.TYPE_XMIT + 2,
        true, "com.mgjg.ProfileManager.attribute.builtin.xmit.XmitAttribute", params, 12));

    return ras;

  }
}
