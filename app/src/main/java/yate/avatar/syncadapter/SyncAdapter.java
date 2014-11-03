package yate.avatar.syncadapter;

import com.google.gson.Gson;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import yate.avatar.Constants;
import yate.avatar.Point;
import yate.avatar.provider.Avatar;

/**
 * Handle the transfer of data between a server and an
 * app, using the Android sync adapter framework.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String TAG = "SyncAdapter";

    // Global variables
    // Define a variable to contain a content resolver instance
    ContentResolver mContentResolver;
    private AccountManager mAccountManager;

    /**
     * Set up the sync adapter
     */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        Log.d(Constants.LOG_ID, TAG + "> In first Constructor");
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();
        mAccountManager = AccountManager.get(context);
    }

    /**
     * Set up the sync adapter. This form of the
     * constructor maintains compatibility with Android 3.0
     * and later platform versions
     */
    public SyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        Log.d(Constants.LOG_ID, TAG + "> In second Constructor");
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        //Do everything here.
        Log.d(Constants.LOG_ID, TAG + ">In perform sync yo");
        try {
//            String authToken = mAccountManager.blockingGetAuthToken(account, Constants.AUTHTOKEN_TYPE_FULL_ACCESS, true);
//            String userObjectId = mAccountManager.getUserData(account, Constants.USERDATA_USER_OBJ_ID);

            //Get external points
            List<Point> remotePoints = getPoints();

            //Get internal points
            ArrayList<Point> localPoints = new ArrayList<Point>();
            Cursor curPoints = provider.query(Avatar.PointContent.CONTENT_URI, null, null, null, null);
            if (curPoints != null) {
                while (curPoints.moveToNext()) {
                    localPoints.add(Point.fromCursor(curPoints));
                }
                curPoints.close();
            }

            //See what local points are missing on remote
            ArrayList<Point> pointsToRemote = new ArrayList<Point>();
            for (Point p : localPoints) {
                if (!remotePoints.contains(p))
                    pointsToRemote.add(p);
            }

            //See what remote points are missing on local
            ArrayList<Point> pointsToLocal = new ArrayList<Point>();
            for (Point p : remotePoints) {
                if (!localPoints.contains(p)) {
                    pointsToLocal.add(p);
                }
            }

            if (pointsToRemote.size() == 0) {
                Log.d(Constants.LOG_ID, TAG + ">No local changes to update server");
            } else {
                Log.d(Constants.LOG_ID, TAG + ">Updating remote server with local changes");

                //TODO: Add functionality to upload the points to database
            }

            if (pointsToLocal.size() == 0) {
                Log.d(Constants.LOG_ID, TAG + ">No server changes to update local database");
            } else {
                Log.d(Constants.LOG_ID, TAG + ">Updating local database with remote changes");

                int i = 0;
                ContentValues pointsToLocalValues[] = new ContentValues[pointsToLocal.size()];
                for (Point p : pointsToLocal) {
                    Log.d(Constants.LOG_ID, TAG + "> Remote -> Local [" + p.getName() + "]");
                    pointsToLocalValues[i++] = p.getContentValues();
                }
                provider.bulkInsert(Avatar.PointContent.CONTENT_URI, pointsToLocalValues);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Point> getPoints() {
        Log.d(Constants.LOG_ID, TAG + "> Getting points");

        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(Constants.POINT_DATABASE_URL_JSON + "/jsonOutput.php");
        try {
            Log.d(Constants.LOG_ID, TAG + "> something " + httpGet.getURI().toURL().toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            Log.d(Constants.LOG_ID, TAG + ">trying my hardest");
            HttpResponse response = httpClient.execute(httpGet);
            String responseString = EntityUtils.toString(response.getEntity());
            Log.d(Constants.LOG_ID, TAG + "> httpget response " + responseString);

            if (response.getStatusLine().getStatusCode() != HttpURLConnection.HTTP_OK) {
                throw new Exception("error retreving points");
            }

            Points points = new Gson().fromJson(responseString, Points.class);
            Log.d(Constants.LOG_ID, TAG + "> Points length " + points.results.size());
            return points.results;
        } catch (IOException e) {

            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<Point>();
    }

    private class Points implements Serializable {
        List<Point> results;
    }
}
