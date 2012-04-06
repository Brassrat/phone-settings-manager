package com.mgjg.ProfileManager.utils;

import android.R;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;

public class ClearTextX extends Activity
{

  @Override
  protected void onCreate(Bundle instanceState)
  {
    String value = "";// any text you are pre-filling in the EditText

    final EditText et = new EditText(this);
    et.setText(value);
    final Drawable x = getResources().getDrawable(R.drawable.presence_offline);// your x image, this one from standard android images looks pretty good actually
    x.setBounds(0, 0, x.getIntrinsicWidth(), x.getIntrinsicHeight());
    et.setCompoundDrawables(null, null, value.equals("") ? null : x, null);
    et.setOnTouchListener(new OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event)
      {
        if (et.getCompoundDrawables()[2] == null)
        {
          return false;
        }
        if (event.getAction() != MotionEvent.ACTION_UP)
        {
          return false;
        }
        if (event.getX() > et.getWidth() - et.getPaddingRight() - x.getIntrinsicWidth())
        {
          et.setText("");
          et.setCompoundDrawables(null, null, null, null);
        }
        return false;
      }

    });
    et.addTextChangedListener(new TextWatcher() {
      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count)
      {
        et.setCompoundDrawables(null, null, et.getText().toString().equals("") ? null : x, null);
      }

      @Override
      public void afterTextChanged(Editable arg0)
      {
      }

      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after)
      {
      }
    });

  }
}
