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

import com.mgjg.ProfileManager.registry.UnknownAttributeException;

import android.content.Context;
import android.database.Cursor;

public interface ProfileAttributeFactory
{

  ProfileAttribute createInstance(Context aa, int type, long profileId) throws UnknownAttributeException;

  ProfileAttribute createInstance(Context aa, Cursor c) throws UnknownAttributeException;

}