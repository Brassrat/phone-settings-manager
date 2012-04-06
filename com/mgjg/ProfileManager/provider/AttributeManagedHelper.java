package com.mgjg.ProfileManager.provider;

import java.util.List;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;

import com.mgjg.ProfileManager.attribute.ProfileAttribute;

public class AttributeManagedHelper extends ManagedProviderHelper<ProfileAttribute>
{
  private final AttributeHelper helper;

  public AttributeManagedHelper(Activity activity)
  {
    super(activity);
    helper = new AttributeHelper(activity);
  }

  @Override
  public Uri getContentUri(int filter, Object... values)
  {
    return helper.getContentUri(filter, values);
  }

  @Override
  protected List<ProfileAttribute> getEntries(Cursor cc)
  {
    return helper.getEntries(cc);
  }

}
