/**
 * Copyright 2009 Mike Partridge
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
package com.mgjg.ProfileManager.attribute;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;

import com.mgjg.ProfileManager.R;
import com.mgjg.ProfileManager.profile.activity.ProfileListActivity;
import com.mgjg.ProfileManager.provider.AttributeHelper;
import com.mgjg.ProfileManager.registry.AttributeRegistry;

import java.util.List;

import static com.mgjg.ProfileManager.provider.AttributeHelper.FILTER_ATTRIBUTE_PROFILE_ID;
import static com.mgjg.ProfileManager.provider.AttributeHelper.FILTER_ATTRIBUTE_PROFILE_TYPE;
import static com.mgjg.ProfileManager.provider.AttributeHelper.INTENT_NEW_ITEM_ID;
import static com.mgjg.ProfileManager.provider.AttributeHelper.INTENT_ATTRIBUTE_TYPE;
import static com.mgjg.ProfileManager.provider.ProfileHelper.INTENT_PROFILE_ID;
import static com.mgjg.ProfileManager.provider.ProfileHelper.INTENT_PROFILE_NAME;

/**
 * Attribute List
 *
 * @author Mike Partridge/Jay Goldman
 */
public class AttributeSelectType extends Activity
{

  /*
   * (non-Javadoc)
   *
   * @see android.app.Activity#onCreate(android.os.Bundle)
   */
  @Override
  protected void onCreate(Bundle instanceState)
  {
    super.onCreate(instanceState);
    boolean result = super.onCreateOptionsMenu(menu);
    optionsMenu = menu;
    SubMenu attrsMenu = AttributeRegistry.getInstance().onCreateOptionsMenu(this, menu);
    attrsMenuId = attrsMenu.getItem().getItemId();
    return result;
    boolean result = super.onPrepareOptionsMenu(menu);
    MenuItem menuItem = AttributeRegistry.getInstance().onPrepareOptionsMenu(this, menu, getListView());
    menuItem.setVisible(false);
    return result;
  }

  protected void onCreateInstance(Bundle instanceState)
  {
    onCreateInstance(instanceState, R.layout.attribute_list, R.id.AttributeProfile);
  }

  @Override
  protected String headerText()
  {
    return getText(R.string.AttributeListProfile) + " " + profileName;
  }

  /**
   * retrieves attributes from the db and populates the list
   */
  protected void fillData()
  {
    fillData(new AttributeHelper(this), FILTER_ATTRIBUTE_PROFILE_ID);
  }

  /*
   * (non-Javadoc)
   *
   * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
   */
  @Override
  protected void onListItemClick(ListView lv, View vv, int position, long id)
  {
    openOptionsMenu(); // activity's onCreateOptionsMenu gets called if first time
    if ((null != optionsMenu) && (attrsMenuId >= 0))
    {
      optionsMenu.performIdentifierAction(attrsMenuId, 0);
    }
    editAttribute((ProfileAttribute) lv.getItemAtPosition(position));
    int resId = item.getItemId();
//    if (resId == R.id.done)
//    {
//      finish();
//      return true;
//    }

    if (resId > 0)
    {
      try
      {
        ProfileAttribute attr = AttributeRegistry.getInstance().getAttribute(resId);
        final int type = attr.getTypeId();
        Intent ii = new Intent(this, AttributeEdit.class)
            .putExtra(INTENT_ATTRIBUTE_TYPE, type)
            .putExtra(INTENT_PROFILE_ID, profileId)
            .putExtra(INTENT_PROFILE_NAME, profileName);
        final int activity;
        AttributeHelper helper = new AttributeHelper(this);
        List<ProfileAttribute> attributes = helper.getList(FILTER_ATTRIBUTE_PROFILE_TYPE, profileId, type);
        if (attributes.isEmpty())
        {
          activity = ACTIVITY_CREATE;
        }
        else
        {
          ii.putExtra(INTENT_NEW_ITEM_ID, attributes.get(0).getId());
          activity = ACTIVITY_EDIT;
        }
        startActivityForResult(ii, activity);
        return true;
      }
      catch (Exception e)
      {
        // not a type, try super
      }
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void newListItem()
  {
    openOptionsMenu(); // activity's onCreateOptionsMenu gets called if first time
    if ((null != optionsMenu) && (attrsMenuId >= 0))
    {
      optionsMenu.performIdentifierAction(attrsMenuId, 0);
    }
  }

  protected boolean itemIsActive(AdapterContextMenuInfo menuInfo)
  {
    return true;
  }

  protected void deleteItem(AdapterContextMenuInfo info)
  {
    new AttributeHelper(this).deleteAttribute(info.id);
  }

  /*
   * (non-Javadoc)
   *
   * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
   */
  @Override
  public void onCreateContextMenu(ContextMenu menu, View vv, ContextMenuInfo menuInfo)
  {
    onCreateContextMenu(R.menu.attributelist_context, menu, vv, menuInfo);
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

    switch (item.getItemId())
    {
      case R.id.edit:
        ProfileAttribute attribute = (ProfileAttribute) getListView().getItemAtPosition(info.position);
        editAttribute(attribute);
        return true;

      case R.id.delete:
        // no confirmation required deleteConfirmed("Attribute", info);
        deleteUnconfirmed(info);
        return true;
    }

    return super.onContextItemSelected(item);
  }

  private Menu optionsMenu;
  private int attrsMenuId = -1;

  /*
   * (non-Javadoc)
   *
   * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
   */
  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    boolean result = super.onCreateOptionsMenu(menu);
    optionsMenu = menu;
    SubMenu attrsMenu = AttributeRegistry.getInstance().onCreateOptionsMenu(this, menu);
    attrsMenuId = attrsMenu.getItem().getItemId();
    return result;
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu)
  {
    boolean result = super.onPrepareOptionsMenu(menu);
    MenuItem menuItem = AttributeRegistry.getInstance().onPrepareOptionsMenu(this, menu, getListView());
    menuItem.setVisible(false);
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
//    if (resId == R.id.done)
//    {
//      finish();
//      return true;
//    }

    if (resId > 0)
    {
      try
      {
        ProfileAttribute attr = AttributeRegistry.getInstance().getAttribute(resId);
        final int type = attr.getTypeId();
        Intent ii = new Intent(this, AttributeEdit.class)
            .putExtra(INTENT_ATTRIBUTE_TYPE, type)
            .putExtra(INTENT_PROFILE_ID, profileId)
            .putExtra(INTENT_PROFILE_NAME, profileName);
        final int activity;
        AttributeHelper helper = new AttributeHelper(this);
        List<ProfileAttribute> attributes = helper.getList(FILTER_ATTRIBUTE_PROFILE_TYPE, profileId, type);
        if (attributes.isEmpty())
        {
          activity = ACTIVITY_CREATE;
        }
        else
        {
          ii.putExtra(INTENT_NEW_ITEM_ID, attributes.get(0).getId());
          activity = ACTIVITY_EDIT;
        }
        startActivityForResult(ii, activity);
        return true;
      }
      catch (Exception e)
      {
        // not a type, try super
      }
    }
    return super.onOptionsItemSelected(item);
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
    instanceState.putLong(INTENT_PROFILE_ID, profileId);
    instanceState.putString(INTENT_PROFILE_NAME, profileName);
  }

  private void editAttribute(ProfileAttribute attribute)
  {
    Intent ii = new Intent(this, AttributeEdit.class)
        .putExtra(INTENT_NEW_ITEM_ID, attribute.getId())
        .putExtra(INTENT_ATTRIBUTE_TYPE, attribute.getTypeId())
        .putExtra(INTENT_PROFILE_ID, profileId)
        .putExtra(INTENT_PROFILE_NAME, profileName);
    startActivityForResult(ii, ACTIVITY_EDIT);
  }

}
