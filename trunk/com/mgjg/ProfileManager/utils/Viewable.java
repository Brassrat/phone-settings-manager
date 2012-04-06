package com.mgjg.ProfileManager.utils;

import android.content.Context;
import android.widget.TableLayout;

public interface Viewable<T extends Listable> extends Listable
{
  void addView(Context context, TableLayout tableLayout);

  boolean copy(T attr);

}
