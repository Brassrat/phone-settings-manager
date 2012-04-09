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
package com.mgjg.ProfileManager.services;

public class UnknownServiceException extends Exception
{

  private static final long serialVersionUID = 1L;

  private final String name;

  UnknownServiceException(String serviceName)
  {
    super("Service name '" + serviceName + "' is not known"); // TODO need string resource
    this.name = serviceName;
  }

  public String getName()
  {
    return name;
  }

}
