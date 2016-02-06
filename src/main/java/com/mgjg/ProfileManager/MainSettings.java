/**
 * Copyright 2009 Daniel Roozen
 * Copyright 2011 Jay Goldman
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
package com.mgjg.ProfileManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.mgjg.ProfileManager.activity.MuteActivity;
import com.mgjg.ProfileManager.activity.RingmodeToggle;
import com.mgjg.ProfileManager.activity.VibrateSettings;
import com.mgjg.ProfileManager.attribute.ActiveCount;
import com.mgjg.ProfileManager.attribute.AttributeUpdatableView;
import com.mgjg.ProfileManager.attribute.ProfileAttribute;
import com.mgjg.ProfileManager.profile.activity.ProfileList;
import com.mgjg.ProfileManager.provider.ScheduleHelper;
import com.mgjg.ProfileManager.registry.AttributeRegistry;
import com.mgjg.ProfileManager.registry.UnknownAttributeException;
import com.mgjg.ProfileManager.utils.AttributeTableLayout;
import com.mgjg.ProfileManager.utils.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//import android.gesture.Gesture;
//import android.gesture.GestureLibrary;
//import android.gesture.GestureOverlayView;
//import android.gesture.GestureOverlayView.OnGesturePerformedListener;
//import android.gesture.Prediction;

public class MainSettings extends ListActivity
// implements OnGesturePerformedListener
{

  public final static int ACTIVITY_LIST = 0;
  public final static int ACTIVITY_MUTE = 1;
  public final static int ACTIVITY_RINGMODE = 2;

  private List<AttributeTableLayout> layouts;

//  private GestureLibrary mLibrary;
//  mLibrary = GestureLibraries.fromRawResource(this, R.raw.gestures);
//  if (!mLibrary.load()) {
//        finish();
//    }
//
//    GestureOverlayView gestures =    (GestureOverlayView)findViewById(R.id.gestures);
//    gestures.addOnGesturePerformedListener(this);

  public MainSettings()
  {
  }

//  public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture)
//  {
//    ArrayList<Prediction> predictions = mLibrary.recognize(gesture);
//    Log.v("performed","performed");
//    // We want at least one prediction
//    if (predictions.size() > 0) {
//        Prediction prediction = predictions.get(0);
//        // We want at least some confidence in the result
//        if (prediction.score > 1.0) {
//                        if(prediction.name.equalsIgnoreCase("right")){
//                          //do you thing here//
//                      }
//        }
//    }
//  }

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle instanceState)
  {
    super.onCreate(instanceState);
    setContentView(R.layout.main);
    // PackageInfo pInfo;
    // try
    // {
    // pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
    // String version = pInfo.versionName;
    // }
    // catch (NameNotFoundException e)
    // {
    //
    // }

    ScheduleHelper.init(this);

    boolean hasShownStartup = Util.isBooleanPref(this, R.string.ShownStartup, false);
    if (!hasShownStartup)
    {
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setMessage("Please see my FAQ page (Menu > F.A.Q.) for a full " +
          "explanation of how Profile Manager works if you need help or " +
          "have questions. Feel free to contact the developer by email " +
          "if you've found problems, have a feature request, or need help.\n\n" +
          "Thanks for downloading!");
      builder.show();

      Util.putBooleanPref(this, R.string.ShownStartup, true);
    }
    // createView();
  }

  @Override
  public void onStart()
  {
    if (null == layouts)
    {
      createView();
    }
    super.onStart();
  }

  @Override
  public void onPause()
  {
    super.onPause();
  }

  @Override
  public boolean onKeyLongPress(int keyCode, KeyEvent event)
  {
    if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP)
    {
      updateSeekBars(this, layouts);
    }
    return super.onKeyLongPress(keyCode, event);
  }

  @Override
  public boolean onKeyUp(int keyCode, KeyEvent event)
  {
    if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP)
    {
      updateSeekBars(this, layouts);
    }
    return super.onKeyUp(keyCode, event);
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu)
  {
    MenuItem item = menu.findItem(R.id.disable_profiles);
    if (null != item)
    {
      boolean disabled = Util.isBooleanPref(this, R.string.disableProfiles, false);
      CharSequence menuTitle = this.getText(disabled ? R.string.enable : R.string.disable);
      item.setTitle(menuTitle);
      item.setTitleCondensed(menuTitle);
    }
    super.onPrepareOptionsMenu(menu);
    return true;
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    super.onCreateOptionsMenu(menu);
    getMenuInflater().inflate(R.menu.menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {

    switch (item.getItemId())
    {
      case R.id.just_mute:
        Intent mute = new Intent(this, MuteActivity.class);
        startActivityForResult(mute, ACTIVITY_MUTE);
        return true;

      case R.id.create_mute_shortcut:
        Intent shortcut = new Intent(Intent.ACTION_MAIN);
        shortcut.setClassName(this, MuteActivity.class.getName());

        Parcelable iconResource = Intent.ShortcutIconResource.fromContext(this, R.drawable.mute);
        Intent ii = new Intent()
            .putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcut)
            .putExtra(Intent.EXTRA_SHORTCUT_NAME, "Mute/Unmute")
            .putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconResource)
            .setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        sendBroadcast(ii);

        // Inform the user that the shortcut has been created
        Toast.makeText(this, "Shortcut Created", Toast.LENGTH_SHORT).show();
        return true;

      case R.id.vibrate_settings:
        Intent vibrate = new Intent(this, VibrateSettings.class);
        startActivity(vibrate);
        return true;

      case R.id.toggle_ringmode:
        Intent ring = new Intent(this, RingmodeToggle.class);
        startActivityForResult(ring, ACTIVITY_RINGMODE);
        return true;

      case R.id.faq:
        Uri uri = Uri.parse("http://github.com/Brassrat/phone-settings-manager/wiki/FAQ");
        Intent faq = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(faq);
        return true;

      case R.id.edit_profiles:
        Intent edit = new Intent(this, ProfileList.class);
        startActivityForResult(edit, ACTIVITY_LIST);
        return true;

      case R.id.disable_profiles:
        boolean disabled = Util.isBooleanPref(this, R.string.disableProfiles, false);
        Util.putBooleanPref(this, R.string.disableProfiles, !disabled);
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  /*
   * (non-Javadoc)
   *
   * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
   */
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data)
  {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == ACTIVITY_LIST)
    {
      setStatusText(this, layouts);
    }

    updateSeekBars(this, layouts);
  }

  @Override
  protected void onResume()
  {
    super.onResume();
    updateSeekBars(this, layouts);
  }

  private void updateSeekBars(Activity aa, List<AttributeTableLayout> layouts)
  {
    for (AttributeTableLayout atl : layouts)
    {
      atl.updateView(aa);
    }
  }

  private void setStatusText(Activity aa, List<AttributeTableLayout> layouts)
  {

    ActiveCount activeCount = new ActiveCount(aa);

    for (AttributeTableLayout atl : layouts)
    {
      atl.setStatusText(aa, activeCount);
    }

  }

  private void setupAttributeViews(Context context, List<Integer> activeTypes, List<AttributeTableLayout> layouts) throws UnknownAttributeException
  {
    AttributeRegistry registry = AttributeRegistry.getInstance();
    for (Integer type : activeTypes)
    {
      ProfileAttribute pa = registry.getAttribute(type);
      layouts.add(new AttributeTableLayout(pa, new AttributeUpdatableView(context, pa, layouts)));
    }
    // order layouts ...
  }

  public void createView()
  {
    AttributeRegistry.init(this);
    layouts = new ArrayList<>();
    List<Integer> activeTypes = new ArrayList<>();
    // treat all registered attributes as active
    // TODO - change to get active list from registry
    for (Integer type : AttributeRegistry.getInstance().registeredAttributes())
    {
      activeTypes.add(type);
    }
    try
    {
      setupAttributeViews(this, activeTypes, layouts);
    }
    catch (UnknownAttributeException e)
    {
      Log.e(Util.LOG_TAG, "Unknown active type: " + e.getType(), e);
    }
    Collections.sort(layouts);
    setListAdapter(new AttributeUpdateableViewListAdapter(this, layouts));
    setStatusText(this, layouts);
  }
}
