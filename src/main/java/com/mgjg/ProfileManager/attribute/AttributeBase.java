/**
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
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TextView;

import com.mgjg.ProfileManager.R;
import com.mgjg.ProfileManager.registry.UnknownAttributeException;
import com.mgjg.ProfileManager.utils.AttributeTableLayout;
import com.mgjg.ProfileManager.utils.Listable;

import java.text.MessageFormat;
import java.util.List;

import static com.mgjg.ProfileManager.provider.AttributeHelper.COLUMN_ATTRIBUTE_BOOL_VALUE;
import static com.mgjg.ProfileManager.provider.AttributeHelper.COLUMN_ATTRIBUTE_ID;
import static com.mgjg.ProfileManager.provider.AttributeHelper.COLUMN_ATTRIBUTE_INT_VALUE;
import static com.mgjg.ProfileManager.provider.AttributeHelper.COLUMN_ATTRIBUTE_PROFILE_ID;
import static com.mgjg.ProfileManager.provider.AttributeHelper.COLUMN_ATTRIBUTE_SETTING;
import static com.mgjg.ProfileManager.provider.AttributeHelper.COLUMN_ATTRIBUTE_TYPE;

public abstract class AttributeBase implements ProfileAttribute
{
  protected static final int ID_CHECKBOX = 10101;
  protected static final int ID_COUNT_TEXT = 10102;

  private static String labelFmt;

  private final long profileId;

  // can be set, but should only be set after insert
  private long id;

  // set-able values
  private String settings;
  private boolean booleanValue;
  private int intValue;

  protected AttributeBase()
  {
    profileId = 0;
    id = 0;
  }

  protected AttributeBase(long attributeId, long profileId, int intValue, boolean booleanValue, String settings)
  {
    this.profileId = profileId;
    this.id = attributeId;
    setNumberValue(intValue);
    setBooleanValue(booleanValue);
    this.settings = settings;
  }

  public ProfileAttribute createInstance(Context aa, long profileId)
  {
    return createInstance(0L, profileId, 0, false, null);
  }

  public final ProfileAttribute createInstance(Context aa, Cursor c) throws UnknownAttributeException
  {
    long id = c.getLong(c.getColumnIndexOrThrow(COLUMN_ATTRIBUTE_ID));
    long profileId = c.getLong(c.getColumnIndexOrThrow(COLUMN_ATTRIBUTE_PROFILE_ID));
    int intValue = c.getInt(c.getColumnIndexOrThrow(COLUMN_ATTRIBUTE_INT_VALUE));
    boolean boolValue = (c.getInt(c.getColumnIndexOrThrow(COLUMN_ATTRIBUTE_BOOL_VALUE)) > 0);
    String setting = c.getString(c.getColumnIndexOrThrow(COLUMN_ATTRIBUTE_SETTING));
    return createInstance(id, profileId, intValue, boolValue, setting);
  }

  public final String getSettings()
  {
    return settings;
  }

  public final void setSettings(String settings)
  {
    this.settings = settings;
  }

  public final long getProfileId()
  {
    return profileId;
  }

  @Override
  public long getId()
  {
    return id;
  }

  public final void setId(long id)
  {
    this.id = id;
  }

  @Override
  public boolean isSupportsNumber()
  {
    return false;
  }

  @Override
  public final int getNumber()
  {
    return isSupportsNumber() ? intValue : 0;
  }

  protected final void setNumber(int numberValue)
  {
    setNumberValue(numberValue);
  }

  protected final void setNumber(SeekBar bar)
  {
    if (null != bar)
    {
      setNumberValue(bar.getProgress());
    }
  }

  protected void onNumberChange()
  {
    // do nothing
  }

  private void setNumberValue(int intValue)
  {
    this.intValue = isSupportsNumber() ? intValue : 0;
    onNumberChange();
  }

  @Override
  public boolean isSupportsBoolean()
  {
    return false;
  }

  @Override
  public final boolean isBoolean()
  {
    return isSupportsBoolean() && booleanValue;
  }

  protected final void setBoolean(boolean booleanValue)
  {
    setBooleanValue(booleanValue);
  }

  protected final void setBoolean(CheckBox checkBox)
  {
    if (null != checkBox)
    {
      setBooleanValue(checkBox.isChecked());
    }
  }

  protected void onBooleanChange()
  {
    // do nothing
  }

  protected final void setBooleanValue(boolean booleanValue)
  {
    this.booleanValue = isSupportsBoolean() && booleanValue;
    onBooleanChange();
  }

  @Override
  public final ContentValues makeValues()
  {
    ContentValues values = new ContentValues();
    values.put(COLUMN_ATTRIBUTE_PROFILE_ID, getProfileId());
    values.put(COLUMN_ATTRIBUTE_TYPE, getTypeId());
    values.put(COLUMN_ATTRIBUTE_INT_VALUE, getNumber());
    values.put(COLUMN_ATTRIBUTE_BOOL_VALUE, isBoolean());
    if (null != getSettings())
    {
      values.put(COLUMN_ATTRIBUTE_SETTING, getSettings());
    }
    return values;
  }

  /**
   * Required for use in a ListAdapter; indicates that this is selectable and clickable
   *
   * @return the mEnabled
   */
  @Override
  public boolean isEnabled()
  {
    return true;
  }

  @Override
  public boolean isOverrider()
  {
    return true;
  }

  @Override
  public boolean isActivatable()
  {
    return true;
  }

  @Override
  public String activate(Context ctxt)
  {
    return null;
  }

  @Override
  public boolean isDeactivatable()
  {
    return false;
  }

  @Override
  public void deactivate()
  {

  }

  @Override
  public void onChange()
  {
    // not implemented
    // want to hook profile manager to system events
  }

  @Override
  public void isChanged()
  {
    // not implemented
  }

  private synchronized String getLabelFmt(Activity aa)
  {
    if (null == labelFmt)
    {
      labelFmt = aa.getText(R.string.attr_for_profile).toString();
    }
    return labelFmt;
  }

  @Override
  public final void onCreate(Activity aa, String profileName)
  {
    TextView typeLabel = (TextView) aa.findViewById(R.id.AttributeForProfile);
    String name = getName(aa);
    String lbl = MessageFormat.format(getLabelFmt(aa), name, profileName);
    typeLabel.setText(lbl);

    TextView attrLabel = (TextView) aa.findViewById(R.id.AttributeName);
    attrLabel.setPadding(2, 7, 2, 2);
    attrLabel.setText(labelText(name));

    populateGui(aa);
  }

  private String labelText(String name)
  {
    return name + ":";
  }

  protected void updateViews(Context context, List<AttributeTableLayout> layouts)
  {
    for (AttributeTableLayout atl : layouts)
    {
      atl.updateView(context);
    }
  }

  @Override
  public boolean copy(ProfileAttribute attr)
  {
    if (attr.getClass() == this.getClass())
    {
      setNumber(attr.getNumber());
      setBoolean(attr.isBoolean());
      setSettings(attr.getSettings());
      return true;
    }
    return false;
  }

  @Override
  public void setStatusText(Activity aa, TableLayout value, ActiveCount activeCount)
  {
    TextView text = (TextView) value.findViewById(ID_COUNT_TEXT);
    if (null != text)
    {
      text.setText(activeCount.getCountText(getTypeId()));
    }
  }

  public int labelMinWidth()
  {
    // need to figure this out from screen... hard-code for now
    return 8 * 24;
  }

  public int rightPadding()
  {
    return 20; // padding to right of active widget to make right-hand edge accessible
  }

  @Override
  public int compareTo(Listable another)
  {
    int thisOrder = getListOrder();
    int othOrder = another.getListOrder();
    // we know that thisOrder and othOrder are small integers so can just subtract to fulfill compareTo contract
    return thisOrder - othOrder;
  }

}
