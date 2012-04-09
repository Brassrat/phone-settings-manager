package com.mgjg.ProfileManager.services;

import android.content.Context;

public interface BooleanService
{

  String getServiceName();
  
  boolean isEnabled(Context context);

  void setEnabled(Context context, boolean enabled);

}
