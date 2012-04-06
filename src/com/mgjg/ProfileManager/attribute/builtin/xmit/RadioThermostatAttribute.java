package com.mgjg.ProfileManager.attribute.builtin.xmit;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.widget.TableLayout;

import com.mgjg.ProfileManager.attribute.AttributeBase;
import com.mgjg.ProfileManager.attribute.ProfileAttribute;
import com.mgjg.ProfileManager.registry.AttributeRegistry;
import com.mgjg.ProfileManager.utils.AttributeTableLayout;

public class RadioThermostatAttribute extends AttributeBase
{

  private RadioThermostatAttribute()
  {
    // TODO Auto-generated constructor stub
  }

  @Override
  public ProfileAttribute createInstance(Context aa, long profileId)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ProfileAttribute createInstance(long attributeId, long profileId, int intValue, boolean booleanValue, String settings)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public long getId()
  {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public boolean isEnabled()
  {
    // TODO Auto-generated method stub
    return true;
  }

  @Override
  public String getName(Context context)
  {
    // TODO Auto-generated method stub
    return "THERMO";
  }

  @Override
  public String getToast(Context context)
  {
    // TODO Auto-generated method stub
    return "()";
  }

  @Override
  public String getNew(Context context)
  {
    // TODO Auto-generated method stub
    return "THERMO";
  }
  
  @Override
  public int getTypeId()
  {
    return AttributeRegistry.TYPE_XMIT + 2;
  }

  @Override
  public boolean isSupportsNumber()
  {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean isSupportsBoolean()
  {
    return false;
  }

  @Override
  public String activate(Context ctxt)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean isModified()
  {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void saveGui()
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void finishCreate()
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void populateGui(Activity aa)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void addView(Context context, TableLayout tableLayout)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void addUpdatableView(Context context, TableLayout layout, List<AttributeTableLayout> layouts)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void updateView(Context context, TableLayout layout)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void removeView()
  {
    // TODO Auto-generated method stub

  }

}
