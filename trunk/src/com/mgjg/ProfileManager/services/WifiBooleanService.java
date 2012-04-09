package com.mgjg.ProfileManager.services;

import android.content.Context;
import android.net.wifi.WifiManager;

public class WifiBooleanService implements BooleanService
{

  public WifiBooleanService(Context context)
  {

  }

  @Override
  public boolean isEnabled(Context context)
  {
    WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    return wifi.isWifiEnabled();
  }

  @Override
  public void setEnabled(Context context, boolean enabled)
  {
    WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    if (enabled != wifi.isWifiEnabled())
    {
      wifi.setWifiEnabled(enabled);
      if (enabled)
      {
        wifi.reassociate();
      }
    }

  }

  @Override
  public String getServiceName()
  {
    return "WiFi";
  }

}
