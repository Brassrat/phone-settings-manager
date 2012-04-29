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
package com.mgjg.ProfileManager.services;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class MobileDataBooleanService implements BooleanService
{

  public MobileDataBooleanService(Context context)
  {

  }

  @Override
  public boolean isEnabled(Context context)
  {

    // Object[] objAndMethod = getObjAndMethod(context, "isMobileDataEnabled");
    // if (null != objAndMethod)
    // {
    // try
    // {
    // Object value = ((Method) objAndMethod[1]).invoke(objAndMethod[0]);
    // if ((null != value) && value instanceof Boolean)
    // {
    // return ((Boolean) value).booleanValue();
    // }
    // }
    // catch (IllegalArgumentException e)
    // {
    // Log.e("com.mgjg.ProfileManager", "unable to enable/disable mobile data: illegal argument, " + e.getMessage());
    // }
    // catch (IllegalAccessException e)
    // {
    // Log.e("com.mgjg.ProfileManager", "unable to enable/disable mobile data: illegal access" + e.getMessage());
    // }
    // catch (InvocationTargetException e)
    // {
    // Log.e("com.mgjg.ProfileManager", "unable to enable/disable mobile data: invocation, " + e.getMessage());
    // }
    // }

    final ConnectivityManager conman = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo info = conman.getNetworkInfo(android.net.ConnectivityManager.TYPE_MOBILE);
    if (null != info)
    {
      return info.isAvailable();
    }
    return false;
  }

  private Object[] getObjAndMethod(Context context, String methodName, Class<?>... args)
  {
    final ConnectivityManager conman = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

    Field iConnectivityManagerField;
    try
    {
      iConnectivityManagerField = conman.getClass().getDeclaredField("mService");

      iConnectivityManagerField.setAccessible(true);
      Object iConnectivityManager = iConnectivityManagerField.get(conman);
      if (null != iConnectivityManager)
      {
        // final Class iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
        final Method mobileDataEnabledMethod = iConnectivityManager.getClass().getDeclaredMethod(methodName, args);
        mobileDataEnabledMethod.setAccessible(true);
        return new Object[] { iConnectivityManager, mobileDataEnabledMethod };
      }
    }
    catch (IllegalArgumentException e)
    {
      Log.e("com.mgjg.ProfileManager", "unable to enable/disable mobile data: illegal argument, " + e.getMessage());
    }
    catch (IllegalAccessException e)
    {
      Log.e("com.mgjg.ProfileManager", "unable to enable/disable mobile data: illegal access" + e.getMessage());
    }
    catch (SecurityException e)
    {
      Log.e("com.mgjg.ProfileManager", "unable to enable/disable mobile data: security, " + e.getMessage());
    }
    catch (NoSuchFieldException e)
    {
      Log.e("com.mgjg.ProfileManager", "unable to enable/disable mobile data: no such field, " + e.getMessage());
    }
    catch (NoSuchMethodException e)
    {
      Log.e("com.mgjg.ProfileManager", "unable to enable/disable mobile data: no such method, " + e.getMessage());
    }
    return null;
  }

  @Override
  public void setEnabled(Context context, boolean enabled)
  {

    if (enabled != isEnabled(context))
    {
      Object[] objAndMethod = getObjAndMethod(context, "setMobileDataEnabled", Boolean.TYPE);
      if (null != objAndMethod)
      {
        try
        {
          ((Method) objAndMethod[1]).invoke(objAndMethod[0], enabled);
        }
        catch (IllegalArgumentException e)
        {
          Log.e("com.mgjg.ProfileManager", "unable to enable/disable mobile data: illegal argument, " + e.getMessage());
        }
        catch (IllegalAccessException e)
        {
          Log.e("com.mgjg.ProfileManager", "unable to enable/disable mobile data: illegal access" + e.getMessage());
        }
        catch (InvocationTargetException e)
        {
          Log.e("com.mgjg.ProfileManager", "unable to enable/disable mobile data: invocation, " + e.getMessage());
        }
      }
    }

  }

  @Override
  public String getServiceName()
  {
    return "MobileData";
  }

}
