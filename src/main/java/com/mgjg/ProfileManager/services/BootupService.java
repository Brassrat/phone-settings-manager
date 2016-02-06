/**
 * Copyright 2009 Daniel Roozen
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package com.mgjg.ProfileManager.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.mgjg.ProfileManager.R;
import com.mgjg.ProfileManager.provider.ScheduleHelper;
import com.mgjg.ProfileManager.registry.AttributeRegistry;
import com.mgjg.ProfileManager.utils.ToastNotification;

public class BootupService extends Service
{

  @Override
  public int onStartCommand(Intent intent, int flags, int startId)
  {
    super.onStartCommand(intent, flags, startId);

    AttributeRegistry.init(this);
    ScheduleHelper.init(this);

    int numAlarms;
    try
    {
      numAlarms = new ScheduleHelper(this).registerAlarm();
    }
    catch (Exception e)
    {
      // this should never happen - means code is out of sync
      // TODO ...
      Log.v("ProfileManager startup", "unable to retrieve active schedules", e);
      numAlarms = -1;
    }

    String toast;
    if (numAlarms == 1)
    {
      toast = "Started 1 alarm";
    }
    else if (numAlarms > 1)
    {
      toast = "Started " + numAlarms + " alarms";
    }
    else if (numAlarms < 0)
    {
      toast = "Started, but registration failed";
    }
    else
    {
      toast = "Started, no alarms to set";
    }
    Toast.makeText(this, "Profile Manager" + toast, Toast.LENGTH_LONG).show();
    int icon = R.drawable.toast;
    CharSequence tickerText = "Profile Manager";

    Intent notificationIntent = new Intent(this, ToastNotification.class);
    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    notificationIntent.setAction("com.mgjg.ProfileManager.TOAST");
    PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

    Notification notification = new Notification.Builder(this)
        .setSmallIcon(icon)
        .setTicker(tickerText)
        .setShowWhen(true)
        .setAutoCancel(true)
        .setContentTitle("Profile Manager")
        .setContentText(toast)
        .setContentIntent(contentIntent)
        .build();

    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.notify(ToastNotification.STARTUP_ID, notification);

    stopSelf();
    return START_NOT_STICKY;
  }

  @Override
  public IBinder onBind(Intent intent)
  {
    onStart(intent, 0);
    return null;
  }

  // This is the old onStart method that will be called on the pre-2.0
  // platform. On 2.0 or later we override onStartCommand() so this
  // method will not be called.
  @Override
  public void onStart(Intent intent, int startId)
  {
    onStartCommand(intent, 0, startId);
  }
}
