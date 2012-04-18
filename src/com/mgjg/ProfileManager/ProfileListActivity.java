package com.mgjg.ProfileManager;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;

public abstract class ProfileListActivity extends ListActivity
{

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

  protected void deleteConfirmed(String what, final AdapterContextMenuInfo info)
  {
    AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    alertDialog.setTitle("Delete " + what);
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int which)
      {
        deleteItem(info);
        fillData();
        return;
      }
    });
    alertDialog.show();
  }

  protected abstract boolean itemIsActive(AdapterContextMenuInfo menuInfo);

  protected abstract void deleteItem(AdapterContextMenuInfo info);

  protected abstract void fillData();

}
