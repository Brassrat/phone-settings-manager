/**
 * Copyright 2009 Daniel Roozen
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

import static com.mgjg.ProfileManager.provider.ScheduleHelper.FILTER_SCHEDULE_ID;

import java.util.List;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.mgjg.ProfileManager.R;
import com.mgjg.ProfileManager.provider.ScheduleHelper;
import com.mgjg.ProfileManager.registry.AttributeRegistry;
import com.mgjg.ProfileManager.schedule.ScheduleEntry;
import com.mgjg.ProfileManager.utils.ToastNotification;
import com.mgjg.ProfileManager.utils.Util;

/**
 * Service that activates all attributes for the profile associated with a schedule entry
 * 
 * @author droozen/jgoldman
 */
public final class ScheduleEntryService extends IntentService
{

  /**
   * A constructor is required, and must call the super IntentService(String) constructor with a name for the worker thread.
   */
  public ScheduleEntryService()
  {
    super("ScheduleActivationService");
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId)
  {
    return super.onStartCommand(intent, flags, startId);
  }

  @Override
  public void onHandleIntent(Intent intent)
  {

    AttributeRegistry.init(this);
    ScheduleHelper.init(this);

    Uri data = intent.getData();

    long scheduleId = (null != data) ? Long.parseLong(data.getPathSegments().get(1)) : 0;

    if (scheduleId > 0)
    {
      String ns = Context.NOTIFICATION_SERVICE;
      NotificationManager notificationManager = (NotificationManager) getSystemService(ns);

      ScheduleHelper provider = new ScheduleHelper(this);
      List<ScheduleEntry> schedules = provider.getList(FILTER_SCHEDULE_ID, scheduleId);

      for (ScheduleEntry schedule : schedules)
      {
        boolean disabled = Util.isBooleanPref(this, R.string.disableProfiles, false);
        if (disabled)
        {
          sendToast(notificationManager, "disabled");
          return;
        }
        CharSequence toast = schedule.activateConditional(this);
        sendToast(notificationManager, toast);
      }
    } // have schedule id
    else
    {
      try
      {
        new ScheduleHelper(this).registerAlarm();
      }
      catch (Exception e)
      {
        // this should never happen - means code is out of sync
        // TODO ...
        Log.v("ProfileManager startup", "unable to retrieve active schedules", e);
      }
    }
  }

  private void sendToast(NotificationManager notificationManager, CharSequence toast)
  {
    Context context = getApplicationContext();
    if ((toast.length() > 0))
    {
      if (Util.isBooleanPref(context, R.string.ShowToasts, true))
      {
        Toast.makeText(context, "Profile Manager: " + toast, Toast.LENGTH_LONG).show();
      }
      if (Util.isBooleanPref(context, R.string.ShowNotifications, true))
      {
        int icon = R.drawable.toast;
        CharSequence tickerText = "Profile Manager";
        long when = System.currentTimeMillis();

        Notification notification = new Notification(icon, tickerText, when);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        CharSequence contentTitle = "Profile Manager";
        CharSequence contentText = toast;
        Intent notificationIntent = new Intent(this, ToastNotification.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        notificationIntent.setAction("com.mgjg.ProfileManager.TOAST");
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(),
            PendingIntent.FLAG_ONE_SHOT + PendingIntent.FLAG_UPDATE_CURRENT);

        notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);

        notificationManager.notify(ToastNotification.TOAST_ID, notification);
      }
    }
  }
}
