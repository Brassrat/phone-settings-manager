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
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.mgjg.ProfileManager.R;
import com.mgjg.ProfileManager.provider.AttributeHelper;
import com.mgjg.ProfileManager.registry.UnknownAttributeException;

import java.util.List;

import static com.mgjg.ProfileManager.provider.AttributeHelper.FILTER_ATTRIBUTE_ID;
import static com.mgjg.ProfileManager.provider.AttributeHelper.FILTER_ATTRIBUTE_PROFILE_TYPE;
import static com.mgjg.ProfileManager.provider.AttributeHelper.INTENT_ATTRIBUTE_ID;
import static com.mgjg.ProfileManager.provider.AttributeHelper.INTENT_ATTRIBUTE_PROFILE_ID;
import static com.mgjg.ProfileManager.provider.AttributeHelper.INTENT_ATTRIBUTE_PROFILE_NAME;
import static com.mgjg.ProfileManager.provider.AttributeHelper.INTENT_ATTRIBUTE_TYPE;

/**
 * Attribute List Edit screen
 *
 * @author Jay Goldman
 */
public class AttributeEdit extends Activity
{

  private static int defaultType;

  public static void setDefaultType(int type)
  {
    defaultType = type;
  }

  private Integer type;
  private Long attributeId;
  private long profileId;
  private String profileName;

  private ProfileAttribute attribute; // help watch for changes

  // GUI related
  private boolean mSaved;
  private boolean canceled;

  /*
   * (non-Javadoc)
   *
   * @see android.app.Activity#onCreate(android.os.Bundle)
   */
  @Override
  protected void onCreate(Bundle instanceState)
  {
    super.onCreate(instanceState);

    try
    {
      setContentView(R.layout.attribute_edit);

      /*
       * check the saved state for attribute id, then check the bundle passed through the Intent
       */
      attributeId = (instanceState != null) ? instanceState.getLong(INTENT_ATTRIBUTE_ID) : 0L;
      if (attributeId < 1)
      {
        attributeId = getIntent().getLongExtra(INTENT_ATTRIBUTE_ID, 0);
        if (attributeId < 1)
        {
          attributeId = null;
        }
        profileId = getIntent().getLongExtra(INTENT_ATTRIBUTE_PROFILE_ID, 0);
        CharSequence profileCS = getIntent().getCharSequenceExtra(INTENT_ATTRIBUTE_PROFILE_NAME);
        profileName = (null == profileCS) ? null : profileCS.toString();
      }
      else if (null != instanceState)
      {
        profileId = instanceState.getLong(INTENT_ATTRIBUTE_PROFILE_ID);
        profileName = instanceState.getString(INTENT_ATTRIBUTE_PROFILE_NAME);
      }

      if (profileId < 1)
      {
        throw new UnsupportedOperationException("Profile id value is required to edit an attribute.");
      }
      if (null == profileName)
      {
        throw new UnsupportedOperationException("Profile name value is required to edit an attribute.");
      }

      initAttributeAndType(instanceState, profileId, attributeId);
      attribute.onCreate(this, profileName);

      Button done = (Button) findViewById(R.id.doneButton);
      if (null != done)
      {
        done.setOnClickListener(new OnClickListener()
        {

          @Override
          public void onClick(View v)
          {
            finish();
          }
        });
      }

      Button cancel = (Button) findViewById(R.id.cancelButton);
      if (null != cancel)
      {
        cancel.setOnClickListener(new OnClickListener()
        {

          @Override
          public void onClick(View v)
          {
            canceled = true;
            finish();
          }
        });
      }

    }
    catch (RuntimeException e)
    {
      Toast.makeText(this, "error: " + e.getMessage(), Toast.LENGTH_LONG).show();
      throw e;
    }
    mSaved = false;
    canceled = false;
  }

  /*
   * compare the attribute (as populated from the db) to the current state of each gui field
   */
  private boolean isModified()
  {
    boolean result = false;

    if (null == attribute)
    {
      return false;
    }
    if (attributeId == null // not saved ever
        || attribute.isModified())
    {
      result = true;
    }

    return result;
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

    /*
     * save only if the gui differs from the db
     */
    if (!mSaved && !canceled && (null != attribute) && isModified())
    {
      saveState();
    }
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

  }

  @Override
  protected void onRestoreInstanceState(Bundle instanceState)
  {
    // have to refetch from db
    initAttributeAndType(null, profileId, attributeId);
    if (null != attribute)
    {
      attribute.populateGui(this);
    }
    mSaved = false;
    canceled = false;
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

    /*
     * save only if the gui differs from the db
     */
    if (!mSaved && !canceled && (null != attribute) && isModified())
    {
      saveState();
    }

    // store some things for re-display on resume
    if (attributeId != null)
    {
      instanceState.putLong(INTENT_ATTRIBUTE_ID, attributeId);
    }

    instanceState.putInt(INTENT_ATTRIBUTE_TYPE, type);
    instanceState.putLong(INTENT_ATTRIBUTE_PROFILE_ID, profileId);
    instanceState.putString(AttributeHelper.INTENT_ATTRIBUTE_PROFILE_NAME, profileName);
  }

  /**
   * writes attribute to db
   */
  private void saveState()
  {

    AttributeHelper helper = new AttributeHelper(this);
    attribute.saveGui();
    ContentValues values = attribute.makeValues();

    if (attributeId == null)
    {
      attributeId = Long.parseLong(helper.insert(values).getPathSegments().get(1));
    }
    else
    {
      helper.update(FILTER_ATTRIBUTE_ID, attributeId, values);
    }

    mSaved = true;

    Toast.makeText(this, R.string.savedAttribute, Toast.LENGTH_SHORT).show();
  }

  /*
   * (non-Javadoc)
   *
   * @see android.app.Activity#finish()
   */
  @Override
  public void finish()
  {

    if ((null != attribute) && !canceled && isModified())
    {
      if (!mSaved)
      {
        saveState();
      }
      AttributeHelper helper = new AttributeHelper(this);
      Uri data = helper.getContentUri(FILTER_ATTRIBUTE_ID, attributeId);
      setResult(RESULT_OK,
          new Intent().putExtra(INTENT_ATTRIBUTE_ID, attributeId)
              .putExtra(INTENT_ATTRIBUTE_PROFILE_ID, profileId)
              .setData(data));
      attribute.finishCreate();
    }

    super.finish();
  }

  @Override
  public void onBackPressed()
  {
    setResult(RESULT_CANCELED);
    super.finish();
  }

  private void initAttributeAndType(Bundle instanceState, long profileId, Long attributeId)
  {
    attribute = null;
    type = null;

    // if have saved attribute id, fetch it from db
    AttributeHelper attributeHelper = new AttributeHelper(this);

    if (null != attributeId)
    {
      List<ProfileAttribute> attributes = attributeHelper.getList(FILTER_ATTRIBUTE_ID, attributeId);
      if (!attributes.isEmpty())
      {
        attribute = attributes.get(0);
        if (profileId != attribute.getProfileId())
        {
          throw new UnsupportedOperationException(
              "Profile id passed to intent does not match id of attribute being edited.");
        }
        // TODO - check that profileName matches profileId
      }
    }

    if (null == attribute)
    {
      type = (instanceState != null) ? instanceState.getInt(INTENT_ATTRIBUTE_TYPE) : null;
      if (null == type)
      {
        type = getIntent().getIntExtra(INTENT_ATTRIBUTE_TYPE, defaultType);
      }
      // have to check if this profile already has an attribute of this type
      List<ProfileAttribute> attributes = attributeHelper.getList(FILTER_ATTRIBUTE_PROFILE_TYPE, profileId, type);
      if (!attributes.isEmpty())
      {
        attribute = attributes.get(0);
        // TODO - check that profileName matches profileId
      }
      else
      {
        try
        {
          attribute = AttributeHelper.getFactory().createInstance(this, type, profileId);
        }
        catch (UnknownAttributeException e)
        {
          throw new UnsupportedOperationException("Type passed to intent is not known", e);
        }
      }
    }
    else
    {
      type = attribute.getTypeId();
    }
  }

}
