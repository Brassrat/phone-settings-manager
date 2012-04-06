package com.mgjg.ProfileManager.attribute.builtin.xmit;

import android.content.Context;
import android.net.wifi.WifiManager;

import com.mgjg.ProfileManager.R;
import com.mgjg.ProfileManager.registry.AttributeRegistry;

public class WifiAttribute extends XmitAttribute
{

  public WifiAttribute()
  {
    super(0, 0, 0, false, "");
  }

  private WifiAttribute(long attributeId, long profileId, int intValue, boolean booleanValue, String settings)
  {
    super(attributeId, profileId, intValue, booleanValue, settings);
  }

  @Override
  public WifiAttribute createInstance(long attributeId, long profileId, int intValue, boolean booleanValue, String settings)
  {
    return new WifiAttribute(attributeId, profileId, intValue, booleanValue, settings);
  }

  public String getNew(Context context)
  {
    return context.getString(R.string.newAttribute_WifiMode);
  }

  @Override
  protected boolean isMode(Context context)
  {
    WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    return wifi.isWifiEnabled();
  }

  @Override
  protected void setMode(Context context, boolean enabled)
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
  public String getName(Context context)
  {
    return context.getString(R.string.wifi_title);
  }

  @Override
  public int getTypeId()
  {
    return AttributeRegistry.TYPE_XMIT + 1;
  }

}
