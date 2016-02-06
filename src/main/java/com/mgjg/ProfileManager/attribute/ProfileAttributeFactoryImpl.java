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

import android.content.Context;
import android.database.Cursor;

import com.mgjg.ProfileManager.registry.AttributeRegistry;
import com.mgjg.ProfileManager.registry.UnknownAttributeException;

public class ProfileAttributeFactoryImpl implements ProfileAttributeFactory
{

  private static ProfileAttributeFactory factory;

  /**
   * create default factory for profile attributes
   *
   * @param context
   * @return <code>ProfileAttributeFactory</code>
   */
  public synchronized static ProfileAttributeFactory createProfileAttributeFactory(Context context)
  {
    if (null == factory)
    {
      factory = new ProfileAttributeFactoryImpl();
    }
    return factory;
  }

  private ProfileAttributeFactoryImpl()
  {
  }

  @Override
  public ProfileAttribute createInstance(Context context, int type, long profileId) throws UnknownAttributeException
  {
    return AttributeRegistry.getInstance().createInstance(context, type, profileId);
  }

  @Override
  public ProfileAttribute createInstance(Context context, Cursor c) throws UnknownAttributeException
  {
    return AttributeRegistry.getInstance().createInstance(context, c);
  }

}
