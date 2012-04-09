package com.mgjg.ProfileManager.services;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

public class AirPlaneBooleanService implements BooleanService
{

  public AirPlaneBooleanService(Context context)
  {
  }

  @Override
  public boolean isEnabled(Context context)
  {
    return Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) == 1;
  }

  @Override
  public void setEnabled(Context context, boolean enabled)
  {
    Settings.System.putInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, enabled ? 1 : 0);

    // Post an intent to reload
    Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
    intent.putExtra("state", enabled);
    context.sendBroadcast(intent);

  }

  @Override
  public String getServiceName()
  {
    return "AirPlane";
  }

}
