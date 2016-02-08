package com.mgjg.ProfileManager.profile.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.TextView;

import com.mgjg.ProfileManager.R;
import com.mgjg.ProfileManager.attribute.AttributeSelectType;
import com.mgjg.ProfileManager.attribute.ProfileAttribute;
import com.mgjg.ProfileManager.provider.ProfileManagerProviderHelper;
import com.mgjg.ProfileManager.utils.Listable;

import static com.mgjg.ProfileManager.provider.ProfileHelper.INTENT_PROFILE_ID;
import static com.mgjg.ProfileManager.provider.ProfileHelper.INTENT_PROFILE_NAME;

public abstract class ProfileListActivity extends ListActivity
{

  protected static final int ACTIVITY_CREATE = 0;
  protected static final int ACTIVITY_EDIT = 1;

  protected long profileId;
  protected String profileName;

  protected void onCreate(Bundle instanceState)
  {
    super.onCreate(instanceState);

    onCreateInstance(instanceState);

    fillData();

    registerForContextMenu(getListView());

    Button done = (Button) findViewById(R.id.done);
    if (null != done)
    {
      done.setOnClickListener(new View.OnClickListener()
      {
        @Override
        public void onClick(View view)
        {
          done();
        }
      });
    }
  }

  protected abstract void onCreateInstance(Bundle instanceState);

  protected void onCreateInstance(Bundle instanceState, int layout, int header)
  {
    if (instanceState == null)
    {
      Intent ii = getIntent();
      profileId = ii.getLongExtra(INTENT_PROFILE_ID, 0);
      profileName = ii.getCharSequenceExtra(INTENT_PROFILE_NAME).toString();
    }
    else
    {
      profileId = instanceState.getLong(INTENT_PROFILE_ID);
      profileName = instanceState.getString(INTENT_PROFILE_NAME);
    }

    if (null == profileName)
    {
      profileName = "NO NAME";
    }

    setContentView(layout);
    if (header > 0)
    {
      TextView mListHeader = (TextView) findViewById(header);
      mListHeader.setText(headerText());
    }
  }

  protected abstract String headerText();

  protected abstract int optionsMenu();

  public boolean onCreateOptionsMenu(Menu menu)
  {
    boolean result = super.onCreateOptionsMenu(menu);
    if (optionsMenu() > 0)
    {
      getMenuInflater().inflate(optionsMenu(), menu);
    }
    return result;
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu)
  {
    return true;
  }

  protected abstract Class<? extends Activity> newActivity();

  protected boolean onOptionsItemSelected(int menuId)
  {
    return false;
  }

  /*
   * (non-Javadoc)
   *
   * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
   */
  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    switch (item.getItemId())
    {
      case R.id.newItemButton:
        newListItem(newActivity());
        break;

      default:
        if (!onOptionsItemSelected(item.getItemId()))
        {
          return super.onOptionsItemSelected(item);
        }
    }
    return true;
  }

  protected <T extends Activity> Intent newListItem(Class<T> newActivity)
  {
    Intent ii = new Intent(this, newActivity)
        .putExtra(INTENT_PROFILE_ID, profileId)
        .putExtra(INTENT_PROFILE_NAME, profileName);
    startActivityForResult(ii, ACTIVITY_CREATE);
    return ii;
  }

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

  protected void done()
  {
    finish();
  }

  /*
* (non-Javadoc)
*
* @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
*/
  @Override
  protected void onSaveInstanceState(Bundle instanceState)
  {
    super.onSaveInstanceState(instanceState);
    instanceState.putLong(INTENT_PROFILE_ID, profileId);
    instanceState.putString(INTENT_PROFILE_NAME, profileName);
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
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener()
    {
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

  protected <T extends Listable> void fillData(ProfileManagerProviderHelper<T> helper, int filterId)
  {
    setListAdapter(helper.createListAdapter(filterId, profileId));
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
