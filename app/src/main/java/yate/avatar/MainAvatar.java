package yate.avatar;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.auth.AUTH;

import yate.avatar.provider.Avatar;
import yate.avatar.syncadapter.SyncAdapter;
import yate.avatar.provider.AvatarDatabaseHelper;


public class MainAvatar extends FragmentActivity
        implements FragmentNavDrawer.NavigationDrawerCallbacks {


    //Constants
    public static final String TAG = "MainAvatar";
    public static final String AUTHORITY = "com.avatar";
    public static final String ACCOUNT_TYPE = "example.com";
    public static final String ACCOUNT = "dummyaccount";


    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private FragmentNavDrawer mFragmentNavDrawer;

    private CharSequence mTitle;

//    AvatarDatabaseHelper db = new AvatarDatabaseHelper(this);

    Account myAccount;
    AccountManager mAccountManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mFragmentNavDrawer = (FragmentNavDrawer)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        mFragmentNavDrawer.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout)
        );




        mAccountManager = AccountManager.get(this);

        //Temporary for manual sync
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_EXPEDITED, true
        );

        myAccount = new Account(ACCOUNT, ACCOUNT_TYPE);
        myAccount = CreateSyncAccount(this);

        ContentResolver.requestSync(myAccount, Constants.AUTHORITY, settingsBundle);
        SyncAdapter blah = new SyncAdapter(this, true);
        blah.onPerformSync(myAccount, null, AUTHORITY, null, null);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
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
     * Google Maps Methods  wololol
     */

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }


    private void setUpMap() {
        Cursor mCursor = getContentResolver().query(Avatar.PointContent.CONTENT_URI, null, null, null, null);
        if(mCursor.moveToNext()) {
            Log.d(Constants.LOG_ID, TAG + ">" + mCursor.getCount());
            do {
                mMap.addMarker(new MarkerOptions().position(
                        new LatLng(mCursor.getDouble(Constants.LATITUDE_INDEX),
                                mCursor.getDouble(Constants.LONGITUDE_INDEX)))
                        .title(mCursor.getString(Constants.NAME_INDEX)));
            } while (mCursor.moveToNext());
        }
//        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
//        mMap.addMarker(new MarkerOptions().position(new LatLng(40.0369697, -82.8894337)).title("Home"));
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
            case 1:
                break;
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
            case 4:
                mTitle = "Another section";
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu item) {
        if (!mFragmentNavDrawer.isDrawerOpen()) {
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
