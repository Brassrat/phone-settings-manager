package com.mgjg.ProfileManager.profile.activity;

import com.mgjg.ProfileManager.R;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;

public abstract class ProfileListActivity extends ListActivity
{

  protected static final int ACTIVITY_CREATE = 0;
  protected static final int ACTIVITY_EDIT = 1;

  protected void onCreate(Bundle instanceState)
  {
    super.onCreate(instanceState);

    onCreateInstance(instanceState);

    fillData();

    registerForContextMenu(getListView());

    Button add = (Button) findViewById(R.id.newItemButton);
    if (null != add)
    {
      add.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view)
        {
          newListItem();
        }
      });
    }
    Button done = (Button) findViewById(R.id.doneButton);
    if (null != done)
    {
      done.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view)
        {
          done();
        }
      });
    }
  }

  protected abstract void onCreateInstance(Bundle instanceState);

  protected abstract void newListItem();

  protected void done()
  {
    finish();
  }

  public void onCreateContextMenu(int menuId, ContextMenu menu, View vv, ContextMenuInfo menuInfo)
  {
    super.onCreateContextMenu(menu, vv, menuInfo);
    getMenuInflater().inflate(menuId, menu);
    MenuItem item = menu.findItem(R.id.toggle);
    if (null != item)
    {
      CharSequence menuTitle = this.getText(itemIsActive((AdapterContextMenuInfo) menuInfo) ? R.string.disable : R.string.enable);
      item.setTitle(menuTitle);
      item.setTitleCondensed(menuTitle);
    }
  }

  protected boolean deleteConfirmed(String what, final AdapterContextMenuInfo info)
  {
    AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    alertDialog.setTitle("Delete " + what);
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int which)
      {
        deleteUnconfirmed(info);
      }
    });
    alertDialog.show();
    return true;
  }

  protected boolean deleteUnconfirmed(AdapterContextMenuInfo info)
  {
    deleteItem(info);
    fillData();
    return true;
  }

  protected abstract boolean itemIsActive(AdapterContextMenuInfo menuInfo);

  protected abstract void deleteItem(AdapterContextMenuInfo info);

  protected abstract void fillData();

  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
   */
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent intent)
  {
    super.onActivityResult(requestCode, resultCode, intent);

    // if (resultCode == RESULT_OK &&
    // (requestCode == ACTIVITY_EDIT || requestCode == ACTIVITY_CREATE))
    // {
    //
    // }

    fillData();
  }

  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onPause()
   */
  @Override
  protected void onPause()
  {
    super.onPause();
  }

  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onResume()
   */
  @Override
  protected void onResume()
  {
    super.onResume();
    fillData();
  }

  @Override
  public void onBackPressed()
  {
    setResult(RESULT_OK);
    super.finish();
  }

  @Override
  public void finish()
  {
    setResult(RESULT_OK);
    super.finish();
  }
}
