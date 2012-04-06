package com.mgjg.ProfileManager.utils;

import android.app.Activity;
import android.os.Bundle;

public class ToastNotification extends Activity
{

  public static final int STARTUP_ID = 1;
  public static final int TOAST_ID = 2;

  @Override
  protected void onCreate(Bundle instanceState)
  {
    super.onCreate(instanceState);
    finish();
  }

  @Override
  public void onBackPressed()
  {
    finish();
  }
}