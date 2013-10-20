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

import com.mgjg.ProfileManager.utils.Util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

public class MobileDataBooleanService implements BooleanService
{

  public MobileDataBooleanService(Context context)
  {

  }

  @Override
  public boolean isEnabled(Context context)
  {

    // Object[] objAndMethod = getObjAndMethod(context, "getMobileDataEnabled");
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
    // Log.e(Util.LOG_TAG, "unable to enable/disable mobile data: illegal argument, " + e.getMessage());
    // }
    // catch (IllegalAccessException e)
    // {
    // Log.e(Util.LOG_TAG, "unable to enable/disable mobile data: illegal access" + e.getMessage());
    // }
    // catch (InvocationTargetException e)
    // {
    // Log.e(Util.LOG_TAG, "unable to enable/disable mobile data: invocation, " + e.getMessage());
    // }
    // }

    NetworkInfo info = null;
    final ConnectivityManager conman = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    if (null != conman)
    {
      info = conman.getNetworkInfo(android.net.ConnectivityManager.TYPE_MOBILE);
    }
    return (null != info) ? info.isAvailable() : false;
  }

  private Object[] getObjAndMethod(Context context, String methodName, Class<?>... args)
  {
    Object conService = context.getSystemService(Context.CONNECTIVITY_SERVICE);
    Object conMgr = null;

      @SuppressWarnings("rawtypes")
      Class conManClass = (null != conService) ? conService.getClass() : ConnectivityManager.class;
      try
      {
        @SuppressWarnings("unchecked")
        Method methodToCall = conManClass.getDeclaredMethod(methodName, boolean.class);
        if ((null == methodToCall) && (null != conManClass))
        {
          final Field connectivityServiceField = conManClass.getDeclaredField("mService");
          if (null != connectivityServiceField)
          {
            connectivityServiceField.setAccessible(true);
            conMgr = connectivityServiceField.get(conService);
          }
        }
        if ((null != methodToCall) && (null != conMgr))
        {
          methodToCall.setAccessible(true);
          return new Object[] { conMgr, methodToCall };
        }
      }
      catch (IllegalArgumentException e)
      {
        Log.e(Util.LOG_TAG, "unable to enable/disable mobile data: illegal argument, " + e.getMessage());
      }
      catch (IllegalAccessException e)
      {
        Log.e(Util.LOG_TAG, "unable to enable/disable mobile data: illegal access" + e.getMessage());
      }
      catch (SecurityException e)
      {
        Log.e(Util.LOG_TAG, "unable to enable/disable mobile data: security, " + e.getMessage());
      }
      catch (NoSuchFieldException e)
      {
        Log.e(Util.LOG_TAG, "unable to enable/disable mobile data: no such field, " + e.getMessage());
      }
      catch (NoSuchMethodException e)
      {
        Log.e(Util.LOG_TAG, "unable to enable/disable mobile data: no such method, " + e.getMessage());
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
          Toast.makeText(context, "ill Arg", Toast.LENGTH_LONG).show();
          Log.e(Util.LOG_TAG, "unable to enable/disable mobile data: illegal argument, " + e.getMessage());
        }
        catch (IllegalAccessException e)
        {
          Toast.makeText(context, "ill Acc", Toast.LENGTH_LONG).show();
          Log.e(Util.LOG_TAG, "unable to enable/disable mobile data: illegal access" + e.getMessage());
        }
        catch (InvocationTargetException e)
        {
          Toast.makeText(context, "inv tgt", Toast.LENGTH_LONG).show();
          Log.e(Util.LOG_TAG, "unable to enable/disable mobile data: invocation, " + e.getMessage());
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
