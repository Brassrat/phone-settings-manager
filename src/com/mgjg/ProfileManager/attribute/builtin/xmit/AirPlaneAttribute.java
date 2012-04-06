package com.mgjg.ProfileManager.attribute.builtin.xmit;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import com.mgjg.ProfileManager.R;
import com.mgjg.ProfileManager.registry.AttributeRegistry;

public class AirPlaneAttribute extends XmitAttribute
{

  public AirPlaneAttribute()
  {
    super(0, 0, 0, false, "");
  }

  private AirPlaneAttribute(long attributeId, long profileId, int intValue, boolean booleanValue, String settings)
  {
    super(attributeId, profileId, intValue, booleanValue, settings);
  }

  @Override
  public AirPlaneAttribute createInstance(long attributeId, long profileId, int intValue, boolean booleanValue, String settings)
  {
    return new AirPlaneAttribute(attributeId, profileId, intValue, booleanValue, settings);
  }

  public String getNew(Context context)
  {
    return context.getString(R.string.newAttribute_AirPlaneMode);
  }

  @Override
  protected boolean isMode(Context context)
  {
    return Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) == 1;
  }

  @Override
  protected void setMode(Context context, boolean enabled)
  {
    Settings.System.putInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, enabled ? 1 : 0);

    // Post an intent to reload
    Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
    intent.putExtra("state", enabled);
    context.sendBroadcast(intent);
  }

  @Override
  public String getName(Context context)
  {
    return context.getString(R.string.airplane_title);
  }

  @Override
  public int getTypeId()
  {
    return AttributeRegistry.TYPE_XMIT + 0;
  }

}
