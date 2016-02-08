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
import static com.mgjg.ProfileManager.provider.AttributeHelper.INTENT_ATTRIBUTE_ID;
import static com.mgjg.ProfileManager.provider.AttributeHelper.INTENT_ATTRIBUTE_TYPE;
import static com.mgjg.ProfileManager.provider.ProfileHelper.INTENT_PROFILE_ID;
import static com.mgjg.ProfileManager.provider.ProfileHelper.INTENT_PROFILE_NAME;

/**
 * Attribute List
 *
 * @author Mike Partridge/Jay Goldman
 */
public class AttributeList extends ProfileListActivity
{
  private static final int ACTIVITY_CREATE = 0;
  private static final int ACTIVITY_EDIT = 1;

  /*
   * (non-Javadoc)
   *
   * @see android.app.Activity#onCreate(android.os.Bundle)
   */
  @Override
  protected void onCreate(Bundle instanceState)
  {
    super.onCreate(instanceState);
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
    editAttribute((ProfileAttribute) lv.getItemAtPosition(position));
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

  @Override
  public int optionsMenu()
  {
    return R.menu.attributelist_options;
  }

  @Override
  protected Class<AttributeSelectType> newActivity()
  {
    return AttributeSelectType.class;
  }

  private void editAttribute(ProfileAttribute attribute)
  {
    Intent ii = new Intent(this, AttributeEdit.class)
        .putExtra(INTENT_ATTRIBUTE_ID, attribute.getId())
        .putExtra(INTENT_ATTRIBUTE_TYPE, attribute.getTypeId())
        .putExtra(INTENT_PROFILE_ID, profileId)
        .putExtra(INTENT_PROFILE_NAME, profileName);
    startActivityForResult(ii, ACTIVITY_EDIT);
  }

}
