package com.mgjg.ProfileManager.attribute;

import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.mgjg.ProfileManager.R;
import com.mgjg.ProfileManager.utils.AttributeTableLayout;

public abstract class BooleanAttributeBase extends AttributeBase
{

  private CheckBox viewCheckBox;

  private CheckBox createCheckBox;

  protected BooleanAttributeBase()
  {
    super(0, 0, 0, false, "");
  }

  protected BooleanAttributeBase(long attributeId, long profileId, int intValue, boolean booleanValue, String settings)
  {
    super(attributeId, profileId, intValue, booleanValue, settings);
  }

  @Override
  public final String activate(Context context)
  {
    boolean isEnabled = isMode(context);
    if (isEnabled != isBoolean())
    {
      setMode(context, !isEnabled);
    }
    return getToast(context);
  }

  @Override
  public final String getToast(Context context)
  {
    // toast text should come from either settings or context
    return String.format("%1$s %2$s", getName(context),
        context.getText(isBoolean() ? R.string.enabled : R.string.disabled));
  }

  public final void onBooleanChange()
  {
    changeView();
  }

  protected final void changeView()
  {
    if ((createCheckBox != null) && (createCheckBox.isChecked() != isBoolean()))
    {
      createCheckBox.setChecked(isBoolean());
      createCheckBox.setTextColor(isBoolean() ? Color.GREEN : Color.RED);
    }

    if (null != viewCheckBox)
    {
      viewCheckBox.setChecked(isBoolean());
      viewCheckBox.setTextColor(isBoolean() ? Color.GREEN : Color.RED);
    }
  }

  @Override
  public final boolean isSupportsBoolean()
  {
    return true;
  }

  @Override
  public final boolean isModified()
  {
    if (null != createCheckBox)
    {
      return (createCheckBox.isChecked() != isBoolean());
    }
    return false;
  }

  @Override
  public final void saveGui()
  {
    setBoolean(createCheckBox);
  }

  @Override
  public final void finishCreate()
  {
    createCheckBox = null;
  }

  @Override
  public final void populateGui(Activity aa)
  {

    View bar = aa.findViewById(R.id.volume);
    if (null != bar)
    {
      bar.setVisibility(View.GONE);
    }

    View lbl = aa.findViewById(R.id.vibrateLabel);
    if (null != lbl)
    {
      lbl.setVisibility(View.GONE);
    }

    if (null == createCheckBox)
    {
      createCheckBox = (CheckBox) aa.findViewById(R.id.vibrateCheckbox);
      createCheckBox.setVisibility(isSupportsBoolean() ? View.VISIBLE : View.GONE);
    }
    createCheckBox.setChecked(isBoolean());

  }

  @Override
  public final void addView(Context context, TableLayout tableLayout)
  {
    TextView label = new TextView(context);
    label.setPadding(2, 2, 2, 2);
    label.setText(getName(context) + ":");
    label.setMinWidth(labelMinWidth());
    label.setGravity(Gravity.LEFT + Gravity.CENTER_VERTICAL);

    viewCheckBox = new CheckBox(context);
    viewCheckBox.setPadding(2, 2, 2, 2);
    viewCheckBox.setChecked(isBoolean());
    viewCheckBox.setEnabled(false);
    viewCheckBox.setFocusable(false);
    viewCheckBox.setFocusableInTouchMode(false);
    viewCheckBox.setClickable(false);
    viewCheckBox.setGravity(Gravity.CENTER_HORIZONTAL);

    TableRow row = new TableRow(context);

    TableRow.LayoutParams labelParams = new TableRow.LayoutParams(
        TableRow.LayoutParams.FILL_PARENT,
        TableRow.LayoutParams.WRAP_CONTENT);
    labelParams.span = 1;
    labelParams.gravity = Gravity.LEFT + Gravity.CENTER_VERTICAL;
    row.addView(label, labelParams);

    TableRow.LayoutParams checkParams = new TableRow.LayoutParams(
        TableRow.LayoutParams.FILL_PARENT,
        TableRow.LayoutParams.WRAP_CONTENT);
    checkParams.span = 1;
    checkParams.gravity = Gravity.CENTER_HORIZONTAL;
    checkParams.rightMargin = rightPadding();
    row.addView(viewCheckBox, checkParams);

    tableLayout.addView(row, AttributeView.paramsFillWrap);
  }

  @Override
  public final void addUpdatableView(final Context context, final TableLayout layout, final List<AttributeTableLayout> layouts)
  {
    TableRow labelRow = new TableRow(context);

    layout.addView(labelRow, new ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.FILL_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT));

    TextView label = new TextView(context);
    label.setPadding(2, 4, 2, 3);
    label.setText(getName(context) + ":");
    label.setMinWidth(labelMinWidth());
    label.setGravity(Gravity.LEFT + Gravity.CENTER_VERTICAL);

    TableRow.LayoutParams singleColumnParams = new TableRow.LayoutParams(
        TableRow.LayoutParams.FILL_PARENT,
        TableRow.LayoutParams.WRAP_CONTENT);
    singleColumnParams.span = 1;

    labelRow.addView(label, singleColumnParams);

    TextView count = new TextView(context);
    count.setId(ID_COUNT_TEXT);
    count.setPadding(2, 4, 2, 3);
    count.setGravity(Gravity.LEFT + Gravity.CENTER_VERTICAL);
    count.setText("TBD");
    labelRow.addView(count, singleColumnParams);

    // <TableRow>
    // <TextView android:id="@+id/label"
    // android:layout_width="wrap_content" android:layout_height="wrap_content"
    // android:layout_marginRight="10dip" android:text="@string/vibrateLabel" />
    // <CheckBox android:id="@+id/checkBox"
    // android:layout_width="wrap_content" android:layout_height="wrap_content" />
    // </TableRow>
    CheckBox checkBox = new CheckBox(context);
    checkBox.setId(ID_CHECKBOX);
    checkBox.setPadding(2, 4, rightPadding(), 3);
    checkBox.setGravity(Gravity.RIGHT);
    checkBox.setChecked(isBoolean());
    checkBox.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
        setBoolean(!isBoolean());
        ((CheckBox) v).setChecked(isBoolean());
        setMode(context, isBoolean());
      }

    });

    TableRow.LayoutParams checkBoxParams = new TableRow.LayoutParams(
        TableRow.LayoutParams.FILL_PARENT,
        TableRow.LayoutParams.WRAP_CONTENT);
    checkBoxParams.span = 1;
    checkBoxParams.gravity = Gravity.RIGHT;
    checkBoxParams.rightMargin = rightPadding();

    labelRow.addView(checkBox, checkBoxParams);

    updateView(context, layout);
  }

  protected abstract void setMode(Context context, boolean enabled);

  protected abstract boolean isMode(Context context);

  @Override
  public final void updateView(Context context, TableLayout layout)
  {
    CheckBox checkBox = (CheckBox) layout.findViewById(ID_CHECKBOX);
    if (null != checkBox)
    {
      checkBox.setChecked(isMode(context));
    }
  }

  @Override
  public final void removeView()
  {
    createCheckBox = null;
  }

  public final void registerStateChangedReceiver(Context context, BroadcastReceiver receiver)
  {

    IntentFilter intentFilter = new IntentFilter("android.intent.action.SERVICE_STATE");

    // BroadcastReceiver receiver = new BroadcastReceiver() {
    // @Override
    // public void onReceive(Context context, android.content.Intent intent) {
    // Log.d("AirplaneMode", "Service state changed");
    // }
    // };

    context.registerReceiver(receiver, intentFilter);

  }
}
