package com.mgjg.ProfileManager.provider;

import java.util.List;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;

import com.mgjg.ProfileManager.utils.Listable;

public abstract class ManagedProviderHelper<T extends Listable>
{
  private Activity activity;

  public ManagedProviderHelper(Activity activity)
  {
    this.activity = activity;
  }

  public List<T> getListManaged(int filter, Object... values)
  {
    return getListManaged(getContentUri(filter, values));
  }

  public List<T> getListManaged(Uri uri)
  {
    return getEntries(getCursorManaged(uri));
  }

  /**
   * Retrieve entries from database using a {@code ManagedQuery} based upon a specified Uri.
   * 
   * @param activity
   * @param uri
   * @return
   */
  public final Cursor getCursorManaged(Uri uri)
  {
    return activity.managedQuery(uri, null, null, null, null);
  }

  public Cursor getCursorManaged(int filter, Object... values)
  {
    return getCursorManaged(getContentUri(filter, values));
  }

  protected abstract List<T> getEntries(Cursor cc);
  
  public abstract Uri getContentUri(int filter, Object... values);
}
