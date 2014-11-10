package yate.avatar.syncadapter;

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
import android.support.v4.app.FragmentActivity;
import android.util.JsonReader;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import yate.avatar.Constants;
import yate.avatar.MainAvatar;
import yate.avatar.PointsMapFragment;
import yate.avatar.contentprovider.Avatar;

/**
 * Handle the transfer of data between a server and an
 * app, using the Android sync adapter framework.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String TAG = "SyncAdapter";

    // Global variables
    // Define a variable to contain a content resolver instance
    private ContentResolver contentResolver;
    private AccountManager accountManager;

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
        contentResolver = context.getContentResolver();
        accountManager = AccountManager.get(context);

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
        contentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        // Do everything here.
        Log.d(Constants.LOG_ID, TAG + ">In perform sync");
        try {

            // Get external points
            Log.d(Constants.LOG_ID, TAG + ">Calling GetPoints");
            List<Point> remotePoints = getPoints();
            Log.d(Constants.LOG_ID, TAG + "> remotePoints length: " + remotePoints.size());

            // Get internal points
            List<Point> localPoints = new ArrayList<Point>();
            Cursor curPoints = contentResolver.query(Avatar.PointContent.CONTENT_URI, null, null, null, null);
            if (curPoints != null) {
                while (curPoints.moveToNext()) {
                    localPoints.add(Point.fromCursor(curPoints));
                }
                curPoints.close();
            } else {
                Log.d(Constants.LOG_ID, TAG + "> curPoints is null");
            }
            Log.d(Constants.LOG_ID, TAG + "> localPoints length: " + localPoints.size());

            // See what local points are missing on remote
            ArrayList<Point> pointsToRemote = new ArrayList<Point>();
            for (Point p : localPoints) {
                if (!remotePoints.contains(p))
                    pointsToRemote.add(p);
                Log.d(Constants.LOG_ID, TAG + ">  " + p.getName());
            }

            // See what remote points are missing on local
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

                // TODO: Add functionality to upload the points to database
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
        List<Point> lPoints;

        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(Constants.POINT_DATABASE_URL_JSON + "/jsonOutput.php");
        try {
            Log.d(Constants.LOG_ID, TAG + "> Connecting to " + httpGet.getURI().toURL().toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            Log.d(Constants.LOG_ID, TAG + "> Trying to get points");
            HttpResponse response = httpClient.execute(httpGet);
            InputStream responseStream = response.getEntity().getContent();

            if (response.getStatusLine().getStatusCode() != HttpURLConnection.HTTP_OK) {
                throw new Exception("error retreving points");
            }

            //TODO: Use GSON library instead of manually converting data.
            lPoints = jsonToList(responseStream);
            return lPoints;
        } catch (IOException e) {

            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<Point>();
    }

    private List<Point> jsonToList(InputStream jsonString) {
        List<Point> lPoints = new ArrayList<Point>();

        JsonReader jReader = new JsonReader(new InputStreamReader(jsonString));

        try {
            jReader.beginArray();
            while (jReader.hasNext()) {
                jReader.beginObject();
                Point pNext = new Point();
                while (jReader.hasNext()) {
                    String key = jReader.nextName();
                    if (key.equalsIgnoreCase(Avatar.PointContent.COL_NAME)) {
                        pNext.setName(jReader.nextString());
                    } else if (key.equalsIgnoreCase(Avatar.PointContent.COL_LAT)) {
                        pNext.setLat(jReader.nextDouble());
                    } else if (key.equalsIgnoreCase(Avatar.PointContent.COL_LONG)) {
                        pNext.setLng(jReader.nextDouble());
                    } else if (key.equalsIgnoreCase(Avatar.PointContent.COL_ALTITUDE)) {
                        pNext.setAlt(jReader.nextDouble());
                    } else if (key.equalsIgnoreCase(Avatar.PointContent.COL_UPLOAD_DATE)) {
                        pNext.setDate(jReader.nextString());
                    } else if (key.equalsIgnoreCase(Avatar.PointContent.COL_LINK)) {
                        pNext.setLink(jReader.nextString());
                    } else {
                        jReader.skipValue();
                    }
                }
                lPoints.add(pNext);
                jReader.endObject();
            }
            jReader.endArray();
            jReader.close();
        } catch (IllegalStateException e) {
            try {
                Log.d(Constants.LOG_ID, TAG + "> ISE CAUGHT: ");
                jReader.skipValue();
            } catch (IOException f) {
                f.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return lPoints;
    }
}
