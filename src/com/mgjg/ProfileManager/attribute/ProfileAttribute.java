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
package com.mgjg.ProfileManager.attribute;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.widget.TableLayout;

import com.mgjg.ProfileManager.registry.UnknownAttributeException;
import com.mgjg.ProfileManager.utils.AttributeTableLayout;
import com.mgjg.ProfileManager.utils.Listable;
import com.mgjg.ProfileManager.utils.Viewable;

/**
 * the in-memory representation of an attribute. Attributes are system values (such as ring volume) that can be managed by the profile manager. Some attributes can be associated with values that a user can manually change - where possible the
 * attribute will be notified of this change.
 * 
 * @author Jay Goldman
 * 
 */
public interface ProfileAttribute extends Viewable<ProfileAttribute>, Comparable<Listable>
{

  /**
   * create profile attribute instance for specified profile using current system state and/or defaults
   */
  ProfileAttribute createInstance(Context aa, long profileId);

  /**
   * create profile attribute from database entry
   * 
   * @param aa
   * @param c
   * @return
   * @throws UnknownAttributeException
   */
  ProfileAttribute createInstance(Context aa, Cursor c) throws UnknownAttributeException;

  /**
   * create a profile attribute from specified values.
   * 
   * @param attributeId
   *          Database Id of attribute record in db (or 0 for template)
   * @param profileIdDatabase
   *          Id of profile record which owns this attribute in db (or 0 for template)
   * @param intValue
   *          integer value state for this attribute, such as volume value
   * @param booleanValue
   *          boolean value state for this attribute, such as vibration enabled
   * @param settings
   *          TODO JSON description of state of attribute
   * @return
   */
  ProfileAttribute createInstance(long attributeId, long profileId, int intValue, boolean booleanValue, String settings);

  long getProfileId();

  String getName(Context context);

  String getToast(Context context);

  int getTypeId(); // registered type id
  
  /**
   * value used to sort attributes in display of current values
   * TODO - get from registry
   */
  int getListOrder(); // order for to show in list
  
  /**
   * returns settings for this attribute as a string
   * 
   * @return
   */
  String getSettings();

  /**
   * sets state of this attribute using a previously stored string
   */
  void setSettings(String aSettings);

  boolean isSupportsNumber();

  int getNumber();

  boolean isSupportsBoolean();

  boolean isBoolean();

  // API for static description of attribute
  /**
   * This attribute can override a user-settable value
   * 
   * @return
   */
  boolean isOverrider();

  /**
   * This attribute can be enabled
   * 
   * @return
   */
  boolean isActivatable();

  /**
   * activates this attribute
   */
  String activate(Context ctxt);

  /**
   * This attribute can be disabled
   * 
   * @return
   */
  boolean isDeactivatable();

  /**
   * Disable this attribute
   */
  void deactivate();

  // API for dynamic behavior
  /**
   * invoked when there is a system event that might indicate that the system value associated with this attribute has been changed (by other than the ProfileManager itself).
   */
  void onChange();

  /**
   * indicates if attribute's associated system value was manually changed since the last time this profile was activated.
   */
  void isChanged();

  /**
   * indicates if current state of GUI fields matches values in attribute
   * 
   * @return <code>true</code> if GUI fields differ from attribute values
   */
  boolean isModified();

  /**
   * called when starting Activity which creates or edits an attribute
   * 
   * @param aa
   * @param profileName
   */
  void onCreate(Activity aa, String profileName);

  /**
   * save current state of create/edit view as values in attribute
   */
  void saveGui();

  void finishCreate();

  // /**
  // * create attribute object from database
  // *
  // * @param aa
  // * @param profileId
  // * @param attributeId
  // * @return ProfileAttribute initialized from database data if available
  // */
  // ProfileAttribute findOrCreate(Context context, long profileId, Long attributeId);

  void populateGui(Activity aa);

  /**
   * create view objects for this attribute and add them to the <code>TableLayout</code>
   * 
   * @param context
   * @param tableLayout
   */
  @Override
  void addView(Context context, TableLayout tableLayout);

  void addUpdatableView(final Context context, TableLayout layout, List<AttributeTableLayout> layouts);

  void updateView(Context context, TableLayout layout);

  /**
   * remove view objects
   */
  void removeView();

  /**
   * copy values from specified attribute to this one, updating view objects
   * 
   * @param attr
   */
  @Override
  boolean copy(ProfileAttribute attr);

  void setStatusText(Activity aa, TableLayout value, ActiveCount activeCount);

}
