package com.mgjg.ProfileManager.services;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class ServiceFactory
{

  public static BooleanService findBooleanService(Context context, String serviceName) throws UnknownServiceException
  {
    if ("wifi".equalsIgnoreCase(serviceName))
    {
      return new WifiBooleanService(context);
    }

    if ("airplane".equalsIgnoreCase(serviceName))
    {
      return new AirPlaneBooleanService(context);
    }

    if ("mobiledata".equalsIgnoreCase(serviceName))
    {
      return new MobileDataBooleanService(context);
    }

    Log.e("com.mgjg.ProfileManager", "Unknown service " + serviceName);
    Toast.makeText(context, "Unknown service " + serviceName,  Toast.LENGTH_LONG).show();
    throw new UnknownServiceException(serviceName);
  }

}
