package com.mgjg.ProfileManager.attribute.builtin.xmit;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.mgjg.ProfileManager.R;
import com.mgjg.ProfileManager.attribute.JSONBooleanAttribute;
import com.mgjg.ProfileManager.attribute.ProfileAttribute;
import com.mgjg.ProfileManager.registry.AttributeRegistry;
import com.mgjg.ProfileManager.registry.RegisteredAttribute;
import com.mgjg.ProfileManager.services.UnknownServiceException;

public class XmitAttribute extends JSONBooleanAttribute
{

  public static ProfileAttribute[] init(Context context)
  {
    try
    {
      // hard-coded registry entries for now ...
      JSONBooleanAttribute AIRPLANE = new XmitAttribute(context,
          makeRegistryJSON(context, "AirPlane", 0, context.getString(R.string.title_airplane), 10));
      JSONBooleanAttribute WIFI = new XmitAttribute(context,
          makeRegistryJSON(context, "WiFi", 1, context.getString(R.string.title_wifi), 11));
      JSONBooleanAttribute MOBILE_DATA = new XmitAttribute(context,
          makeRegistryJSON(context, "MobileData", 2, context.getString(R.string.title_mobiledata), 12));
      return new ProfileAttribute[] { AIRPLANE, WIFI, MOBILE_DATA };
    }
    catch (JSONException e)
    {
      Log.e("com.mgjg.ProfileManager", "Unable to initialize Xmit Attributes: " + e.getMessage());
      return new ProfileAttribute[0];
    }
    catch (UnknownServiceException e)
    {
      Log.e("com.mgjg.ProfileManager", "Unable to initialize Xmit Attributes: " + e.getMessage());
      return new ProfileAttribute[0];
    }
  }
 
  public XmitAttribute(Context context, String registryDefinition) throws JSONException, UnknownServiceException
  {
    super(context, registryDefinition);
  }

  private static String makeRegistryJSON(Context context, String serviceName, int typeId, String name, int order)
  {
    return String.format("{ \"service\" : \"%1$s\", \"id\" : \"%2$d\", \"name\" : \"%3$s\", \"order\" : \"%4$d\"}",
        serviceName, AttributeRegistry.TYPE_XMIT + typeId, name, order);
  }

  public static List<RegisteredAttribute> addRegistryEntries(SQLiteDatabase db)
  {
    List<RegisteredAttribute> ras = new ArrayList<RegisteredAttribute>();
    String params = makeRegistryJSON(null, "AirPlane", 0, "AirPlane", 10);
    ras.add(new RegisteredAttribute(0, "AirPlane", AttributeRegistry.TYPE_XMIT + 0,
        true, "com.mgjg.ProfileManager.attribute.builtin.xmit.XmitAttribute", params, 10));

    params = makeRegistryJSON(null, "WiFi", 1, "WiFi", 11);
    ras.add(new RegisteredAttribute(0, "WiFi", AttributeRegistry.TYPE_XMIT + 1,
        true, "com.mgjg.ProfileManager.attribute.builtin.xmit.XmitAttribute", params, 11));

    params = makeRegistryJSON(null, "MobileData", 2, "MobileData", 12);
    ras.add(new RegisteredAttribute(0, "MobileData", AttributeRegistry.TYPE_XMIT + 2,
        true, "com.mgjg.ProfileManager.attribute.builtin.xmit.XmitAttribute", params, 12));

    return ras;

  }
}
