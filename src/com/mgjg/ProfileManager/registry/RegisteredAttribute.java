package com.mgjg.ProfileManager.registry;

import static com.mgjg.ProfileManager.provider.AttributeRegistryHelper.COLUMN_REGISTRY_ACTIVE;
import static com.mgjg.ProfileManager.provider.AttributeRegistryHelper.COLUMN_REGISTRY_CLASS;
import static com.mgjg.ProfileManager.provider.AttributeRegistryHelper.COLUMN_REGISTRY_ID;
import static com.mgjg.ProfileManager.provider.AttributeRegistryHelper.COLUMN_REGISTRY_NAME;
import static com.mgjg.ProfileManager.provider.AttributeRegistryHelper.COLUMN_REGISTRY_ORDER;
import static com.mgjg.ProfileManager.provider.AttributeRegistryHelper.COLUMN_REGISTRY_PARAM;
import static com.mgjg.ProfileManager.provider.AttributeRegistryHelper.COLUMN_REGISTRY_TYPE;

import java.lang.reflect.Constructor;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Gravity;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.mgjg.ProfileManager.attribute.ProfileAttribute;
import com.mgjg.ProfileManager.provider.AttributeRegistryProvider;
import com.mgjg.ProfileManager.utils.Listable;
import com.mgjg.ProfileManager.utils.Viewable;

public class RegisteredAttribute implements Viewable<RegisteredAttribute>
{

  private long id;
  private String name;
  private int type;
  private boolean active;
  private final String className;
  private final String params;
  private final int order;

  public RegisteredAttribute(long id, String name, int type, boolean active, String className, String params, int order)
  {
    this.id = id;
    this.name = name;
    this.type = type;
    this.active = active;
    this.className = className;
    this.params = params;
    this.order = order;
  }

  public ProfileAttribute createInstance(Context context)
  {
    try
    {
      Class<?> clz = Class.forName(className);
      Constructor<?> construct = clz.getConstructor(Context.class, String.class);
      return (ProfileAttribute) construct.newInstance(context, params);
    }
    catch (Exception e)
    {
      Log.e("com.mgjg.ProfileManager", "Unable to create profile attribute '" + name + "' using "
          + className + " because " + e.getMessage());
    }
    throw new IllegalArgumentException("Can not create ProfileAttribute '" + name + "' using '" + className + "'");
  }

  @Override
  public ContentValues makeValues()
  {
    ContentValues values = new ContentValues();
    values.put(COLUMN_REGISTRY_ID, id);
    values.put(COLUMN_REGISTRY_NAME, name);
    values.put(COLUMN_REGISTRY_TYPE, type);
    values.put(COLUMN_REGISTRY_ACTIVE, active);
    values.put(COLUMN_REGISTRY_CLASS, className);
    values.put(COLUMN_REGISTRY_PARAM, params);
    values.put(COLUMN_REGISTRY_ORDER, order);
    return values;
  }

  public ContentValues makeUpdateValues(RegisteredAttribute other)
  {
    ContentValues values = new ContentValues();
    if (type == other.type)
    {
      if (!name.equals(other.name))
      {
        values.put(COLUMN_REGISTRY_NAME, other.name);
      }
      if (active != other.active)
      {
        values.put(COLUMN_REGISTRY_ACTIVE, other.active);
      }
      if (!className.equals(other.className))
      {
        values.put(COLUMN_REGISTRY_CLASS, other.className);
      }
      if (!params.equals(other.params))
      {
        values.put(COLUMN_REGISTRY_PARAM, other.params);
      }
      if (order != other.order)
      {
        values.put(COLUMN_REGISTRY_ORDER, other.order);
      }
    }
    return values;
  }

  @Override
  public void setId(long id)
  {
    this.id = id;
  }

  @Override
  public long getId()
  {
    return id;
  }

  @Override
  public boolean isEnabled()
  {
    return active;
  }

  @Override
  public void addView(Context context, TableLayout tableLayout)
  {

    TableRow row = new TableRow(context);
    TextView rowLabel = new TextView(context);
    rowLabel.setPadding(2, 7, 2, 2);
    rowLabel.setText("name" + ":"); // TODO getXXX(context)
    row.addView(rowLabel);

    CheckBox activeCheckBox = new CheckBox(context);
    activeCheckBox.setPadding(2, 2, 2, 2);
    activeCheckBox.setChecked(active);

    TableRow.LayoutParams cbParams = new TableRow.LayoutParams();
    cbParams.span = 1;
    cbParams.gravity = Gravity.CENTER_HORIZONTAL;

    row.addView(activeCheckBox, cbParams);

    TextView rowType = new TextView(context);
    rowType.setPadding(2, 7, 2, 2);
    rowType.setText(name); // TODO getXXX(context)
    row.addView(rowType);

    tableLayout.addView(row);

  }

  @Override
  public boolean copy(RegisteredAttribute attr)
  {
    if (attr.getClass() == this.getClass())
    {
      attr.active = active;
      attr.type = type;
      return true;
    }
    return false;
  }

  @Override
  public int compareTo(Listable another)
  {
    int thisOrder = getListOrder();
    int othOrder = another.getListOrder();
    // we know that thisOrder and othOrder are small integers so can just subtract to fulfill compareTo contract
    return thisOrder - othOrder;
  }

  @Override
  public int getListOrder()
  {
    return this.order;
  }

  public void register(Context context, AttributeRegistry registry)
  {
    ProfileAttribute pa = createInstance(context);
    Log.v("com.mgjg.ProfileManager", "register attribute " + pa.getName(context));
    registry.register(context, pa);
  }

  public long addRegistryEntry(SQLiteDatabase db)
  {
    return AttributeRegistryProvider.addRegistryEntry(db, name, type, className, params, order);
  }

  public boolean sameType(RegisteredAttribute other)
  {
    return type == other.type;
  }
}
