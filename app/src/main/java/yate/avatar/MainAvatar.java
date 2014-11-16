package yate.avatar;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.FragmentManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import yate.avatar.contentprovider.Avatar;
import yate.avatar.syncadapter.SyncAdapter;


public class MainAvatar extends ActionBarActivity
        implements FragmentNavDrawer.NavigationDrawerCallbacks {


    //Constants
    public static final String TAG = "MainAvatar";
    public static final String AUTHORITY = "yate.avatar.contentprovider";
    public static final String ACCOUNT_TYPE = "yate.avatar";
    public static final String ACCOUNT = "dummyaccount";


    private FragmentNavDrawer fragmentNavDrawer;

    private CharSequence title;

    private Account myAccount;
    private AccountManager mAccountManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fragmentNavDrawer = (FragmentNavDrawer)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        title = getTitle();

        fragmentNavDrawer.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout)
        );

        mAccountManager = AccountManager.get(this);

        //Temporary for manual sync
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);

        myAccount = new Account(ACCOUNT, ACCOUNT_TYPE);
        myAccount = CreateSyncAccount(this);

        ContentResolver.requestSync(myAccount, Constants.AUTHORITY, settingsBundle);
        getContentResolver().delete(Avatar.PointContent.CONTENT_URI, null, null);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * SyncAdapter methods
     */
    public static Account CreateSyncAccount(Context context) {
        AccountManager accountManager = (AccountManager) context.getSystemService(ACCOUNT_SERVICE);
        for (int i = 0; i < accountManager.getAccounts().length; i++) {
            Log.d(Constants.LOG_ID, TAG + "> " +  i + " " + accountManager.getAccounts()[i].toString());

        }
        Log.d(Constants.LOG_ID, TAG + "> in CreateSyncAccount");
        Account newAccount = new Account(ACCOUNT, ACCOUNT_TYPE);
        Log.d(Constants.LOG_ID, TAG + "> " + newAccount.toString());
        ContentResolver.setSyncAutomatically(newAccount, AUTHORITY, true);
        ContentResolver.setIsSyncable(newAccount, AUTHORITY, 1);

        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
            Log.d(Constants.LOG_ID, TAG + "> Added successfully");
            ContentResolver.setSyncAutomatically(newAccount, AUTHORITY, true);
            ContentResolver.setIsSyncable(newAccount, AUTHORITY, 1);
        } else {
            Log.d(Constants.LOG_ID, TAG + "> Exist or Error");
        }
        return newAccount;
    }

    /**
     * Navigation Drawer Methods
     */

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        FragmentManager fragmentManager = getFragmentManager();
        switch (position) {
            case 0:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new PointsMapFragment())
                        .commit();
                break;
            case 2:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new PointsListFragment())
                        .commit();
                break;
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                title = getString(R.string.title_section1);
                break;
            case 2:
                title = getString(R.string.title_section2);
                break;
            case 3:
                title = getString(R.string.title_section3);
                break;
            case 4:
                title = "Another section";
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu item) {
        if (!fragmentNavDrawer.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.settings_dialog, item);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(item);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
