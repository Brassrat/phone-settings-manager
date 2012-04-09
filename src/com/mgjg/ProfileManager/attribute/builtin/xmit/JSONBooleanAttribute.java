package com.mgjg.ProfileManager.attribute.builtin.xmit;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.mgjg.ProfileManager.services.BooleanService;
import com.mgjg.ProfileManager.services.UnknownServiceException;

public class JSONBooleanAttribute extends XmitAttribute
{
  private final BooleanService service;
  private final int typeId;
  private final String name;
  private final int order;

  public JSONBooleanAttribute(Context context, String registryDefinition) throws JSONException, UnknownServiceException
  {
    super(0, 0, 0, false, "");
    JSONObject registryData = new JSONObject(registryDefinition);
    // check that registry definition is valid
    typeId = registryData.getInt("id");
    name = registryData.getString("name");
    order = registryData.getInt("order");

    String serviceName = registryData.getString("service");
    service = com.mgjg.ProfileManager.services.ServiceFactory.findBooleanService(context, serviceName);
  }

  private JSONBooleanAttribute(BooleanService aService, int aTypeId, String aName, int aOrder, long attributeId, long profileId, int intValue, boolean booleanValue, String settings)
  {
    super(attributeId, profileId, intValue, booleanValue, settings);
    service = aService;
    typeId = aTypeId;
    name = aName;
    order = aOrder;
  }

  @Override
  public JSONBooleanAttribute createInstance(long attributeId, long profileId, int intValue, boolean booleanValue, String settings)
  {
    return new JSONBooleanAttribute(service, typeId, name, order, attributeId, profileId, intValue, booleanValue, settings);
  }

  @Override
  protected boolean isMode(Context context)
  {
    return service.isEnabled(context);
  }

  @Override
  protected void setMode(Context context, boolean enabled)
  {
    service.setEnabled(context, enabled);
  }

  @Override
  public String getName(Context context)
  {
    return name;
  }

  @Override
  public int getTypeId()
  {
    return typeId;
  }

  @Override
  public int getListOrder()
  {
    return order;
  }

}
