package com.mgjg.ProfileManager.services;

import android.content.Context;

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

    throw new UnknownServiceException(serviceName);
  }

}
