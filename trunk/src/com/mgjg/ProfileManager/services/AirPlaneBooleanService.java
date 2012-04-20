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
