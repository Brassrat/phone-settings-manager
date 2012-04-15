/**
 * Copyright 2009 Mike Partridge
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
package com.mgjg.ProfileManager.attribute;

import static com.mgjg.ProfileManager.provider.AttributeHelper.FILTER_ATTRIBUTE_PROFILE_ID;
import static com.mgjg.ProfileManager.provider.AttributeHelper.FILTER_ATTRIBUTE_PROFILE_TYPE;
import static com.mgjg.ProfileManager.provider.AttributeHelper.INTENT_ATTRIBUTE_ID;
import static com.mgjg.ProfileManager.provider.AttributeHelper.INTENT_ATTRIBUTE_PROFILE_ID;
import static com.mgjg.ProfileManager.provider.AttributeHelper.INTENT_ATTRIBUTE_PROFILE_NAME;
import static com.mgjg.ProfileManager.provider.AttributeHelper.INTENT_ATTRIBUTE_TYPE;

import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.TextView;

import com.mgjg.ProfileManager.R;
import com.mgjg.ProfileManager.provider.AttributeHelper;
import com.mgjg.ProfileManager.registry.AttributeRegistry;

/**
 * Attribute List
 * 
 * @author Mike Partridge/Jay Goldman
 */
public class AttributeList extends ListActivity
{
  private static final int ACTIVITY_CREATE = 0;
  private static final int ACTIVITY_EDIT = 1;

  private long profileId;
  private String profileName;

  private TextView mListHeader;

  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onCreate(android.os.Bundle)
   */
  @Override
  protected void onCreate(Bundle instanceState)
  {
    super.onCreate(instanceState);

    if (instanceState == null)
    {
      Intent ii = getIntent();
      profileId = ii.getLongExtra(INTENT_ATTRIBUTE_PROFILE_ID, 0);
      profileName = ii.getCharSequenceExtra(INTENT_ATTRIBUTE_PROFILE_NAME).toString();
    }
    else
    {
      profileId = instanceState.getLong(INTENT_ATTRIBUTE_PROFILE_ID);
      profileName = instanceState.getString(INTENT_ATTRIBUTE_PROFILE_NAME);
    }

    if (null == profileName)
    {
      profileName = "NO NAME";
    }

    setContentView(R.layout.attribute_list);

    mListHeader = (TextView) findViewById(R.id.AttributeProfile);
    mListHeader.setText(getText(R.string.AttributeListProfile) + " " + profileName);

    ListView lv = getListView();
    fillData();

    registerForContextMenu(lv);
  }

  /**
   * retrieves attributes from the db and populates the list
   */
  private void fillData()
  {
    AttributeHelper helper = new AttributeHelper(this);
    setListAdapter(helper.createListAdapter(FILTER_ATTRIBUTE_PROFILE_ID, profileId));
  }

  /*
   * (non-Javadoc)
   * 
   * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
   */
  @Override
  protected void onListItemClick(ListView ll, View vv, int position, long id)
  {
    ProfileAttribute attribute = (ProfileAttribute) ll.getItemAtPosition(position);
    Intent ii = new Intent(this, AttributeEdit.class)
        .putExtra(INTENT_ATTRIBUTE_ID, attribute.getId())
        .putExtra(INTENT_ATTRIBUTE_TYPE, attribute.getTypeId())
        .putExtra(INTENT_ATTRIBUTE_PROFILE_ID, profileId)
        .putExtra(INTENT_ATTRIBUTE_PROFILE_NAME, profileName);
    startActivityForResult(ii, ACTIVITY_EDIT);
  }

  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
   */
  @Override
  public void onCreateContextMenu(ContextMenu menu, View v,
      ContextMenuInfo menuInfo)
  {
    super.onCreateContextMenu(menu, v, menuInfo);

    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.attributelist_context, menu);
  }

  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
   */
  @Override
  public boolean onContextItemSelected(MenuItem item)
  {
    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    ProfileAttribute attribute = (ProfileAttribute) getListView().getItemAtPosition(info.position);
    long attributeId = attribute.getId();
    switch (item.getItemId())
    {
    case R.id.editAttribute:
      Intent ii = new Intent(this, AttributeEdit.class)
          .putExtra(INTENT_ATTRIBUTE_ID, attributeId)
          .putExtra(INTENT_ATTRIBUTE_PROFILE_ID, profileId)
          .putExtra(INTENT_ATTRIBUTE_PROFILE_NAME, profileName);
      startActivityForResult(ii, ACTIVITY_EDIT);
      return true;

    case R.id.deleteAttribute:
      new AttributeHelper(this).deleteAttribute(attributeId);

      fillData();
      return true;
    }

    return super.onContextItemSelected(item);
  }

  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
   */
  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    boolean result = super.onCreateOptionsMenu(menu);

    AttributeRegistry.getInstance().onCreateOptionsMenu(this, menu);
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
   */
  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    int resId = item.getItemId();
    if (resId == R.id.done)
    {
      finish();
      return true;
    }

    // int type = AttributeRegistry.getInstance().getTypeNew(resId);
    try
    {
      ProfileAttribute attr = AttributeRegistry.getInstance().getAttribute(resId);
      int type = attr.getTypeId();
      AttributeHelper helper = new AttributeHelper(this);
      List<ProfileAttribute> attributes = helper.getList(FILTER_ATTRIBUTE_PROFILE_TYPE, profileId, type);
      Intent ii = new Intent(this, AttributeEdit.class)
          .putExtra(INTENT_ATTRIBUTE_TYPE, type)
          .putExtra(INTENT_ATTRIBUTE_PROFILE_ID, profileId)
          .putExtra(INTENT_ATTRIBUTE_PROFILE_NAME, profileName);
      int activity;
      if (!attributes.isEmpty())
      {
        ii.putExtra(INTENT_ATTRIBUTE_ID, attributes.get(0).getId());
        activity = ACTIVITY_EDIT;
      }
      else
      {
        activity = ACTIVITY_CREATE;
      }
      startActivityForResult(ii, activity);
      return true;
    }
    catch (Exception e)
    {
      // not Done and not a type, try super
    }
    return super.onOptionsItemSelected(item);
  }

  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onPause()
   */
  @Override
  protected void onPause()
  {
    super.onPause();
  }

  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onResume()
   */
  @Override
  protected void onResume()
  {
    super.onResume();

    fillData();
  }

  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
   */
  @Override
  protected void onSaveInstanceState(Bundle instanceState)
  {
    super.onSaveInstanceState(instanceState);
    instanceState.putLong(INTENT_ATTRIBUTE_PROFILE_ID, profileId);
    instanceState.putString(INTENT_ATTRIBUTE_PROFILE_NAME, profileName);
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

    if (resultCode != RESULT_CANCELED && (requestCode == ACTIVITY_EDIT || requestCode == ACTIVITY_CREATE))
    {
      @SuppressWarnings("unused")
      long profileId = data.getLongExtra(INTENT_ATTRIBUTE_PROFILE_ID, 0);
      // do we have to save anything here?
    }

    fillData();
  }

  @Override
  public void onBackPressed()
  {
    // stop the current 'activity' i.e., exit without saving
    setResult(RESULT_CANCELED);
    super.finish();
  }
}
